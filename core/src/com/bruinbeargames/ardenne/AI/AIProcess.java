package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * AIProcess will do common AI routines for Reinforcements and moves
 * for scenario 2 and 3
 *
 * Assume the Hex AI scoreGen has been set
 * Simplicity
 */
public class AIProcess{
    /**
     *  Do common processing for AIMve
     * @param arrUnitsIn
     * @param arrArrayOfHexArray
     */
    public boolean isFailed = false;
    AIProcess(ArrayList<Unit> arrUnitsIn, ArrayList<ArrayList<Hex>> arrArrayOfHexArray){
        Gdx.app.log("AIProcess", "Constructor #Units="+arrUnitsIn.size()
                    +"  hex arrays="+arrArrayOfHexArray.size());
        /**
         * reduce amount of hexes to check
         * we can adjust this later in case we get too many by changing the aitoCheck
         */
        reduceHexsToCheck(arrArrayOfHexArray,1);
        /**
         *  Check for cases where we have no movement possible then
         *  remove it from the units
         *  and create an array for the iterations
         */
        ArrayList<Unit> arrValidUnits = new ArrayList<>();
        ArrayList<ArrayList<Hex>> arrRemove =  new ArrayList<>();
        int ix=0;
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            if (arr.size() == 0 ){
                arrRemove.add(arr);
            }else{
                arrValidUnits.add(arrUnitsIn.get(ix));
            }
            ix++;
        }
        Gdx.app.log("AIProcess", "Remove ="+arrRemove.size();
        arrArrayOfHexArray.removeAll(arrRemove);
        if (arrArrayOfHexArray.size() == 0){
            Gdx.app.log("AIProcess", "Failed");
            isFailed = true;
            return;
        }
        ArrayList<Hex> arrNewHexMove[] = new ArrayList[arrArrayOfHexArray.size()];
        for (int i=0; i < arrArrayOfHexArray.size();i++ ){
            arrNewHexMove[i] = arrArrayOfHexArray.get(i);
        }
        /**
         * get iteration
         */
        Gdx.app.log("AIProcess", "Staring Iterations");
        ArrayList<AIOrders> arrStart = AIUtil.GetIterations(arrUnitsIn, arrNewHexMove);
        Gdx.app.log("AIProcess", "Iterations count ="+arrStart.size());



    }

    private void reduceHexsToCheck(ArrayList<ArrayList<Hex>> arrArrayOfHexArray, int aiToCheck) {
        /**
         *  reduce size of the hexes to search
         *  by just looking at hexes that have ai greater than 0
         *  It is assumed that invoking rtn will have set the aiScoreGen
         */
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            ArrayList<Hex> arrRemove = new ArrayList<>();
            AIUtil.RemoveDuplicateHex(arr);
            for (Hex hex:arr){
                if (hex.aiScoreGen < 1){
                    arrRemove.add(hex);
                }
            }
            arr.removeAll(arrRemove);
        }
    }
}
