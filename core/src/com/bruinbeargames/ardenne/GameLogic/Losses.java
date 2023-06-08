package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class Losses{
    boolean areAllEliminated = false;
    public Losses(ArrayList<Unit> arrUnits, int toLose) {
        if (toLose == 0) {
            return;
        }
        if (arrUnits.size() == 0) {
            areAllEliminated = true;
            return;
        }
        ArrayList<Unit> arrSorted = new ArrayList<>(); // sort highest first
        int ix =0;
        int test =0;
        for (Unit unit:arrUnits) {
            for (int i = 0; i < arrSorted.size(); i++) {
                if (unit.getCurrentStep() > arrUnits.get(i).getCurrentStep()) {
                    ix = i;
                    break;
                }
            }
            arrSorted.add(ix, unit);
        }
        for (Unit unit:arrSorted) {
            Gdx.app.log("Losses", "sorted unit=" + unit);
        }

        /**
         *  lose all to lose max is 4
         */
        ix = 0;
        while (toLose >0) {
            Unit unitSuffer = arrSorted.get(ix);
            if (unitSuffer.canStepLoss()) {
                unitSuffer.reduceStep();
                CombatResults cr = CombatResults.find(unitSuffer);
                cr.setStepLosses(true);
            }else {
                unitSuffer.eliminate();
                arrSorted.remove(unitSuffer);
                CombatResults cr = CombatResults.find(unitSuffer);
                cr.setDestroyed(true);
            }
            toLose--;
            ix++;
            if (arrSorted.isEmpty()) {
                areAllEliminated = true;
                toLose=0;
                break;
            }
            if (ix >= arrSorted.size()) {
                ix =0;
            }
        }
    }
    public boolean getAreAlliminated(){
        return areAllEliminated;
    }
}
