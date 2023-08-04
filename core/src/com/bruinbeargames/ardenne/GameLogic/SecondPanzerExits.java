package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Hex.Hex;
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
    public SecondPanzerExits(){
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

}