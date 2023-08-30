package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.VictoryPopup;
import com.bruinbeargames.ardenne.UI.WinExitDisplay;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.WinModal;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class SecondPanzerExits {
    public static SecondPanzerExits instance;
    public int[] numOfUnitsToExit = {0,0,0,2,4,5,7,7};
    ArrayList<Unit> arrUnits = new ArrayList<>();
    public Hex hexExit1 = Hex.hexTable[0][8];
    public ArrayList<Unit> unitExit1 = new ArrayList<>();
    public Hex hexExit2 = Hex.hexTable[0][19];
    public ArrayList<Unit> unitExit2 = new ArrayList<>();
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion tExitBoard =  textureAtlas.findRegion("exitboard");
    static TextureRegion tExitBoardShade =  textureAtlas.findRegion("exitboarddarken");
    private I18NBundle i18NBundle;

    public SecondPanzerExits(){
        instance = this;
        for (Unit unit:Unit.getAxis()){
            if (unit.designation.contains("2nd")){
                arrUnits.add(unit);
            }
        }
        i18NBundle = GameMenuLoader.instance.localization;
        arrIcons.clear();


    }
    public boolean isInSecond(Unit unit){
        if (arrUnits.contains(unit)){
            return true;
        }
        return false;
    }

    public boolean isInExit(Hex hex) {
         if (hex == hexExit1 || hex == hexExit2){
             return true;
         }
         return false;
    }
    public boolean isInExit(ArrayList<Hex> arrHexes){
        if(arrHexes.contains(hexExit1) || arrHexes.contains(hexExit2)){
            return true;
        }
        return false;
    }

    /**
     * exit the the unit
     *
     * @param unit
     * @param hex
     */
    public void exit(Unit unit, Hex hex) {
    }

    public void exitUnit(Hex hexExit2ndPanzer, Unit unit) {
        unit.eliminate();
        addIcon(hexExit2ndPanzer);
        if (hexExit2ndPanzer == hexExit1){
            unitExit1.add(unit);
        }else{
            unitExit2.add(unit);
        }
        WinExitDisplay winExitDisplay = new WinExitDisplay();

    }   public void exitUnitLoad(Hex hexExit2ndPanzer, Unit unit) {
        addIcon(hexExit2ndPanzer);
        if (hexExit2ndPanzer == hexExit1){
            unitExit1.add(unit);
        }else{
            unitExit2.add(unit);
        }

    }

    public ArrayList<Unit> getExitted()
    {
        ArrayList<Unit> arrUnits = new ArrayList<>();
        arrUnits.addAll(unitExit1);
        arrUnits.addAll(unitExit2);
        return arrUnits;
    }

    public boolean checkExits() {
       int turn = NextPhase.instance.getTurn();
        turn--; //look in table;
        if (numOfUnitsToExit[turn] > getExitted().size()){
            return true;
        }
        return false;
    }
    public void addIcon(Hex hex){
        Icon icFound = null;
        for (Icon ic:arrIcons){
            if (ic.hex == hex){
                icFound = ic;
                break;
            }
        }
        if (icFound == null){
            Icon icon = new Icon(hex);
            arrIcons.add(icon);
        }else{
            icFound.addCount();
        }

    }
    static public ArrayList<Icon> arrIcons = new ArrayList<Icon>();

    public void shade(Hex hex) {
        Icon ic = findIcon(hex);
        ic.shade();
    }

    public void removeShade(Hex hex) {
        Icon ic = findIcon(hex);
        ic.unShade();

    }
    private Icon findIcon(Hex hex){
        for (Icon ic:arrIcons){
            if (ic.hex == hex){
                return ic;
            }
        }
        return null;
    }

    public void supply(Hex hex) {
        Icon ic = findIcon(hex);
        ic.unShade();
        ic.setSupplied();
        /**
         *  check victory
         */

    }
    public boolean checkVictory(){
        boolean isVictory = true;
        for (Icon icon:arrIcons){
            if (!icon.inSupply){
                isVictory = false;
            }
        }
        return isVictory;
    }

    public class Icon{
        Hex hex;
        int count;
        boolean inSupply = false;
        Image image;
 //       Label label;
 //       Stack stack;
        Icon(Hex hex){
            this.hex = hex;
            count = 1;
            addImage(tExitBoard);
        }
        public void shade(){
            image.clearActions();
            image.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(0),
                    Actions.fadeIn(0.25f),
                    Actions.delay(0.25f),
                    Actions.fadeOut(0.25f)
            )));

        }
        public void unShade(){
            image.clearActions();
            image.addAction(Actions.fadeIn(.25f));
        }

        /**
         *  replace ICON with shade
         * @param hex
         * @param texIN
         */
        Icon(Hex hex, TextureRegion texIN){
            this.hex = hex;
            count = 1;
            addImage(texIN);
        }

        private void addImage(TextureRegion texIN) {
            image = new Image(texIN);
            image.setScale(.9f);
            Vector2 v2 = hex.getCounterPosition();
            float x = 0;
            float y=0;
            x = v2.x-80;
            y=v2.y+10;
            if (hex == hexExit1){
                y +=120;
            }else{
                y +=5;
            }
            image.setPosition(x,y);
            ardenne.instance.mapStage.addActor(image);

        }

        void addCount(){
            count++;
 //           label.setText(Integer.toString(count));
        }

        public void setSupplied() {
            inSupply = true;
        }
    }
}
