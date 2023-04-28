package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class ExitWest {
    static public ExitWest instance;
    ArrayList<Unit> arr2ndPanzerExits  = new ArrayList();
    ArrayList<Unit> arrLehrExits = new ArrayList();

    public ExitWest(){
        instance = this;
    }
    public void add2ndPanzer(Unit unit){
        arr2ndPanzerExits.add(unit);
    }
    public void addLehr(Unit unit){
        arrLehrExits.add(unit);
    }
    public ArrayList<Unit> getExit2ndPanzer(){
        ArrayList<Unit> arrReturn = new ArrayList<>();
        arrReturn.addAll(arr2ndPanzerExits);
        return arrReturn;
    }
    public ArrayList<Unit> getExitLehr(){
        ArrayList<Unit> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrLehrExits);
        return arrReturn;
    }

}
