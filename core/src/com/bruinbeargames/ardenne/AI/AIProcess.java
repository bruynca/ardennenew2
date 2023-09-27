package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

/**
 * AIProcess will do common AI routines for Reinforcements and moves
 * for scenario 2 and 3
 *
 * Assume the Hex AI scoreGen has been set
 *
 * Simplicity
 */
public class AIProcess{
    /**
     *  Do common processing for AIMve
     * @param arrUnitsIn      Units
     * @param arrArrayOfHexArray Moves for Units
     * @param arrDupes Hexes that allow duplication for arttacking purposes
     */
    private boolean isFailed = false;
    ArrayList<AIOrders> arrAIOrders = new ArrayList<>();
    AIProcess(ArrayList<Unit> arrUnitsIn, ArrayList<ArrayList<Hex>> arrArrayOfHexArray,ArrayList<Hex> arrDupes, int aiTocheck){
        Gdx.app.log("AIProcess", "Constructor #Units="+arrUnitsIn.size()
                    +"  hex arrays="+arrArrayOfHexArray.size());
        /**
         * reduce amount of hexes to check
         * we can adjust this later in case we get too many by changing the aitoCheck
         * This is driven by AIScoreTemp - set by INVOKING
         * also it will eliminate any hexes already occupied
         */
        reduceHexsToCheck(arrArrayOfHexArray,aiTocheck);
        /**
         *  in case there are no hexes to check move in occupying hex
         */
        for (int ix=0; ix< arrArrayOfHexArray.size();ix++){;
            if (arrArrayOfHexArray.get(ix).size() == 0 ){
                arrArrayOfHexArray.get(ix).add(arrUnitsIn.get(ix).getHexOccupy());
                if (arrUnitsIn.get(ix).getHexOccupy() == null){
                    isFailed = true;
                    return;
                }
            }
        }
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
        Gdx.app.log("AIProcess", "Remove ="+arrRemove.size());
        arrArrayOfHexArray.removeAll(arrRemove);
        if (arrArrayOfHexArray.size() == 0){
            Gdx.app.log("AIProcess", "Failed");
            isFailed = true;
            return;
        }
        /**
         *  change Moves to differant format for Iterator
         */
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
        AIUtil.RemoveDuplicateHex(arrDupes); // cleanup just in case
        /**
         *  remove duplicates
         */
        if (arrStart.size() > 1) {
            if (AIOrders.removeDupeMoveToHexes(arrStart, arrDupes).size() > 0) {
                arrAIOrders.addAll(AIOrders.removeDupeMoveToHexes(arrStart, arrDupes));
            }else{
                arrAIOrders.addAll(arrStart);
            }
        }else{
            arrAIOrders.addAll(arrStart);
        }
        Gdx.app.log("AIProcess", "After Dupe Removal count ="+arrAIOrders.size());

        if (arrAIOrders.size() == 0){
            Gdx.app.log("AIProcess", "Failed");
            isFailed = true;
            return;
        }

        AIScorer.Type type = AIScorer.Type.ReinOther;
        AIFaker.instance.startScoringOrders(arrAIOrders, type, true);
    }

    private void reduceHexsToCheck(ArrayList<ArrayList<Hex>> arrArrayOfHexArray, int aiToCheck) {
        /**
         *  reduce size of the hexes to search
         *  by just looking at hexes that have ai greater than 0
         *  It is assumed that invoking rtn will have set the aiScoreGen
         */
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            AIUtil.RemoveDuplicateHex(arr);
            ArrayList<Hex> arrRemove = new ArrayList<>();
            for (Hex hex:arr){
                if (hex.aiScoreGen < aiToCheck || hex.isAlliedOccupied()){
                    arrRemove.add(hex);
                }
            }
            arr.removeAll(arrRemove);
        }
    }
    public boolean isFailed(){
        return isFailed;
    }

    public ArrayList<AIOrders> getAIOrders() {
        return arrAIOrders;
    }
}
