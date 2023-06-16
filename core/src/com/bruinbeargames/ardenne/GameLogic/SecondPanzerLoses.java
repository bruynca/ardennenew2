package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SecondPanzerLoses extends Observable implements Observer {
    ArrayList<Unit> arr2NDPanzer = new ArrayList<>();
    final int toRemove = 3;
    static public SecondPanzerLoses instance;
    private I18NBundle i18NBundle;
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion tExitBoard =  textureAtlas.findRegion("exitboard");
    Unit unitWork;

    public SecondPanzerLoses(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

    }
    public void removeUnits(){
        /**
         *  sort 2nd pzer units by most western
         */
        for (Unit unit:Unit.getOnBoardAxis()){
            if (unit.designation.contains("2nd Pz")){
                int ix=0;
                for (ix=0;ix<arr2NDPanzer.size();ix++){
                    if (unit.getHexOccupy().xTable < arr2NDPanzer.get(ix).getHexOccupy().xTable){
                        break;
                    }
                }
                arr2NDPanzer.add(ix,unit);
            }
        }
        ArrayList<Unit> arrRemoveFromBoard = new ArrayList<>();
        int ix=0;
        for (Unit unit:arr2NDPanzer){
            if (ix < toRemove){
                arrRemoveFromBoard.add(unit);
                ExitWest.instance.add2ndPanzer(unit);
                ix++;
            }
        }
        arr2NDPanzer.retainAll(arrRemoveFromBoard);
        removeUnit();
        return;

    }

    private void removeUnit() {
        if (arr2NDPanzer.size() == 0){
            if (GameSetup.instance.isGermanVersusAI()){
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.CardPlayed, null,0,0));
                return;
            }else {
                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
                return;
            }
        }
        Unit unit = arr2NDPanzer.get(0);
        ExitWest.instance.add2ndPanzer(unit);
        arr2NDPanzer.remove(unit);
        int xCentre = unit.getHexOccupy().xTable;
        int yCentre = unit.getHexOccupy().yTable;
        Hex hexScroll = Hex.hexTable[xCentre][yCentre];
        unitWork = unit;
        CenterScreen.instance.addObserver(this);
        CenterScreen.instance.start(hexScroll);
        // wait for centerscreen to end
        return;
    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes kick off processing for that type
         *  else do nothing
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.ScreenCentered){
            Vector2 v2 = unitWork.getHexOccupy().getCounterPosition();
            CenterScreen.instance.deleteObserver(this);
            unitWork.fadeOut();
            final Image image = new Image(tExitBoard);
            image.setPosition(v2.x+10,v2.y+10);
            image.addAction(Actions.fadeIn((.2f)));
            image.addAction(Actions.fadeOut(.8f));
            ardenne.instance.mapStage.addActor(image);
            Timer.schedule(new Timer.Task(){
                               @Override
                               public void run() {
                                   image.remove();
                                   unitWork.removeFromBoard();
                                   removeUnit();
                               }
                           }
                    , 1.5f        			//    (delay)
            );
        }
    }
}
