package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameLogic.BarrageExplode;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Map;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class FlyingShell implements Observer {
    public static FlyingShell instance;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion txtShell =  textureAtlas.findRegion("shell");
    TextureRegion txtAirplane =  textureAtlas.findRegion("airplane2");
    int framesToShow = 8; // 1/2 second
    float timeDiff = .05F; // for 20 frames a second  ;
    int shellCordinates[][][];
    int workFrame = 0;
    Image[] imgShell;
    float deltaTimeStart;
    float delta;
    Barrage.TargetShooterSave ts;


    public FlyingShell () {
        instance = this;
    }

    /**
     *  start flying shooter save shells for this bombardment
     * @param targetShooterSave
     */
    public void start(Barrage.TargetShooterSave targetShooterSave) {
        ts = targetShooterSave;
        Hex hexTo = targetShooterSave.hexTarget;
        ArrayList<Hex> arrShooters = new ArrayList<>();
        if (ts.getaircnt() == 0) {
            for (Unit unit : targetShooterSave.getArrShooters()) {
                if (!arrShooters.contains(unit.getHexOccupy())) {
                    arrShooters.add(unit.getHexOccupy());
                }
            }
            SoundsLoader.instance.playMortar();
        }else{
            int startMid = hexTo.xTable;
            int cntAir = ts.getaircnt();
            if (cntAir > 10){
                cntAir = 10;  // sanity check
            }
            int cntMath = cntAir;
            int mid = startMid - cntAir/2;
            if (mid + cntMath > Hex.xEnd){
                mid -= (Hex.xEnd - cntMath)+1;
            }
            if (mid < 0){
                mid = 0;
            }
            int cnt = 0;
            while (cnt < cntAir){
                Hex hex = Hex.hexTable[mid][0];
                arrShooters.add(hex);
                mid++;
                cnt++;
            }
            SoundsLoader.instance.playStuka();

        }

        shellCordinates = new int[arrShooters.size()][framesToShow +1][2];
        imgShell = new Image[arrShooters.size()];

        /**
         *  populate coordinates
         */
        for (int i=0; i< arrShooters.size(); i++){
            Hex hexFrom = arrShooters.get(i);
            int xDist = (int) (hexTo.GetDisplayCoord().x - hexFrom.GetDisplayCoord().x);
            int yDist = (int) (hexTo.GetDisplayCoord().y - hexFrom.GetDisplayCoord().y);
            int xDelta = xDist/framesToShow;
            int yDelta = yDist/framesToShow;
            /**
             *  set start
             */
            /**
             *  set rest
             */
            int x = (int) hexFrom.GetDisplayCoord().x;
            x += 60;
            int y = (int) hexFrom.GetDisplayCoord().y;
            y+= 75;
            shellCordinates[i][0][0] = x;
            shellCordinates[i][0][1] = y;

            for (int j=1;j<=framesToShow;j++ ){
                x += xDelta;
                y += yDelta;
                shellCordinates[i][j][0] = x;
                shellCordinates[i][j][1] = y;

            }
            if (ts.getaircnt() == 0) {
                imgShell[i] = new Image(txtShell);
            }else{
                imgShell[i] = new Image(txtAirplane);

            }
        }
        if (!Map.onScreen(targetShooterSave.hexTarget)){
            CenterScreen.instance.start(targetShooterSave.hexTarget);
            CenterScreen.instance.addObserver(this);
        }else {
            letHerFly();
        }


    }

    private void letHerFly() {
        workFrame = 1;
        deltaTimeStart = Gdx.graphics.getDeltaTime();
        ardenne.instance.isUpdateShell = true;
        delta = 0;
        int ix=0;
        for (Image image:imgShell){
            image.setScale(.5f);
            ardenne.instance.mapStage.addActor(imgShell[ix]);
            image.setPosition(shellCordinates[ix][0][0],shellCordinates[ix][0][1]);
            ix++;
        }
    }

    public void update() {
  //      Gdx.app.log("FlyingShell", "deltaTime="+Gdx.graphics.getDeltaTime());
  /*      boolean isDebug = true;
        if (isDebug){
            ardenne.instance.isUpdateShell = false;
            return;

        } */
        delta += Gdx.graphics.getDeltaTime();
        if (delta > timeDiff){
            delta = 0;
            deltaTimeStart = delta;
            int ix =0;
            for (Image image:imgShell){
                image.setPosition(shellCordinates[ix][workFrame][0],shellCordinates[ix][workFrame][1]);
                ix++;
            }
            workFrame++;
            if (workFrame > framesToShow){
                ardenne.instance.isUpdateShell = false;
                for (Image image:imgShell){
                    image.remove();
                }
                SoundsLoader.instance.stopSounds();
                BarrageExplode.instance.endShellFly(ts);
            }


        }


    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes kick off processing for that type
         *  else do nothing
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.ScreenCentered) {
            CenterScreen.instance.deleteObserver(this);
            letHerFly();
          }

    }
}
