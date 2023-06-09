package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class LehrHalts extends Observable {
    public static LehrHalts instance;
    ArrayList<Unit> arrLehr = new ArrayList<>();
    ArrayList<Integer> arrLehrMovementSave = new ArrayList<>();
    boolean isLehrHalted = false;
    public LehrHalts(){
        instance = this;
    }
    public void halt(){
        isLehrHalted = true;
        for (Unit unit:Unit.getOnBoardAxis()){
            if (unit.designation.contains("Pz Lehr")){
                arrLehr.add(unit);
                arrLehrMovementSave.add(unit.getCurrentMovement());
                unit.setCurrentMovement(1);
                unit.getMapCounter().getCounterStack().setPoints();
            }
        }
        setChanged();
        notifyObservers(new ObserverPackage(ObserverPackage.Type.CardPlayed, null,0,0));
        return;

    }
    public void restore(){
        int ix=0;
        for (Unit unit:arrLehr){
            if (!unit.isEliminated()) {
                unit.setCurrentMovement(arrLehrMovementSave.get(ix));
                unit.getMapCounter().getCounterStack().setPoints();
            }
            ix++;
        }
    }
    public boolean isLehrHalted(){
        return isLehrHalted;
    }

}
