package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class SecondPanzerHalts {
    public static SecondPanzerHalts instance;
    ArrayList<Unit> arr2NDPanzer = new ArrayList<>();
    ArrayList<Integer> arr2NDPanzerMovementSave = new ArrayList<>();
    boolean is2NDPanzerHalted = false;
    public SecondPanzerHalts(){
        instance = this;
    }
    public void halt(){
        is2NDPanzerHalted = true;
        for (Unit unit:Unit.getOnBoardAxis()){
            if (unit.designation.contains("2nd Pz")){
                arr2NDPanzer.add(unit);
                arr2NDPanzerMovementSave.add(unit.getCurrentMovement());
                unit.setCurrentMovement(1);
                unit.getMapCounter().getCounterStack().setPoints();
            }
        }
    }
    public void restore(){
        int ix=0;
        for (Unit unit:arr2NDPanzer){
            if (!unit.isEliminated()) {
                unit.setCurrentMovement(arr2NDPanzerMovementSave.get(ix));
                unit.getMapCounter().getCounterStack().setPoints();
            }
            ix++;
        }
    }
    public boolean is2NDPanzerHalted(){
        return is2NDPanzerHalted;
    }


}
