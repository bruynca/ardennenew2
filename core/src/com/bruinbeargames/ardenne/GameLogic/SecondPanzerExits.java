package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class SecondPanzerExits {
    public static SecondPanzerExits instance;
    int[] numOfUnitsToExit = {0,0,0,2,4,5,6};
    ArrayList<Unit> arrUnits = new ArrayList<>();
    Hex hexExit1 = Hex.hexTable[0][8];
    ArrayList<Unit> unitExit1 = new ArrayList<>();
    Hex hexExit2 = Hex.hexTable[0][19];
    ArrayList<Unit> unitExit2 = new ArrayList<>();
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion tExitBoard =  textureAtlas.findRegion("exitboard");
    Stack exit1Stack;
    Stack exit2Stack;
    Label exit1Label;
    Label exit2Label;
    public SecondPanzerExits(){
        instance = this;
        for (Unit unit:Unit.getAxis()){
            if (unit.designation.contains("2nd")){
                arrUnits.add(unit);
            }
        }

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
        if (hexExit2ndPanzer == hexExit1){
            unitExit1.add(unit);
        }else{
            unitExit2.add(unit);
        }

    }
}
