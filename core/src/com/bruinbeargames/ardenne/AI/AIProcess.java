package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     *  Create AIOrders Array for passed paramaters.
     *   it is assumed that the invoking object will add and observer to AIFaker
     *  if AIOrders can not be genarted then will set isFailed to true and Exit
     *  1. Validate that the passed array of hexes have a hex. Use the occupying hex for the unit. If it
     *      is  aReinforcement then fail.
     *  2.  Get Iterations of AIOrders
     *  3.  Remove AIOrders that have duplicate hex destinations.
     *  4.  Set Type for AIFaker
     *  5.  Release to AIFaker
     *
     * @param arrUnitsIn Units input
     * @param arrArrayOfHexArray Arraylist of ArrayList of Hexs that the Units can move to  1:1 with the units
     * @param arrDupes Array of hexes that can have duplicates on the AIOrder
     * @param aiTocheck the minimum aiScoreGen to keep hex in solution less than 99 will be old
     *                  process  99 is new
     */
    AIProcess(ArrayList<Unit> arrUnitsIn, ArrayList<ArrayList<Hex>> arrArrayOfHexArray,ArrayList<Hex> arrDupes, int aiTocheck){
        Gdx.app.log("AIProcess", "Constructor #Units="+arrUnitsIn.size()
                    +"  hex arrays="+arrArrayOfHexArray.size());
        /**
         * reduce amount of hexes to check
         * we can adjust this later in case we get too many by changing the aitoCheck
         * This is driven by AIScoreTemp - set by INVOKING
         * also it will eliminate any hexes already occupied
         *
         * This is no longer used
         * see next routine
         */
  /*      if (aiTocheck < 99) {
            reduceHexsToCheck(arrArrayOfHexArray, aiTocheck);

            /**
             *  in case there are no hexes to check move in occupying hex
             */
    /*        for (int ix = 0; ix < arrArrayOfHexArray.size(); ix++) {
                ;
                if (arrArrayOfHexArray.get(ix).size() == 0) {
                    arrArrayOfHexArray.get(ix).add(arrUnitsIn.get(ix).getHexOccupy());
                    if (arrUnitsIn.get(ix).getHexOccupy() == null) {
                        isFailed = true;
                        return;
                    }
                }
            }
        }*/
        /**
         *  reduce to a million
         *
         */
        int iterates = 1;
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            iterates *= arr.size();
        }
        Gdx.app.log("AIProcess", "Iteratiosn Before ="+iterates);


        AIUtil.reduceToMillion(arrArrayOfHexArray);
        iterates = 1;
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            iterates *= arr.size();
        }
        Gdx.app.log("AIProcess", "Iteratiosn After ="+iterates);


        /**
         *  Check for cases where we have no movement possible then
         *  remove it from the units
         *  and create an array for the iterations
         */
        ArrayList<Unit> arrRemoveUnits = new ArrayList<>();
        ArrayList<ArrayList<Hex>> arrRemove =  new ArrayList<>();
        int ix=0;
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            if (arr.size() == 0 ) {
                arrRemove.add(arr);
                arrRemoveUnits.add(arrUnitsIn.get(ix));
            }
            ix++;
        }
        Gdx.app.log("AIProcess", "Remove ="+arrRemove.size());
        arrArrayOfHexArray.removeAll(arrRemove);
        arrUnitsIn.removeAll(arrRemoveUnits);
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
            Gdx.app.log("AIProcess", "NO Units to move");
            isFailed = true;
            return;
        }
        /**
         *  update the AIORders with preliminary AIScore
         */
        AIOrders.updateAIScore(arrAIOrders);
        /**
         *  add in check for over a specific amount of aiorders
         *  we can reduce
         */
        ArrayList<AIOrders> arrSmall = new ArrayList<>();
        int maxNumber = 5000;
        if (arrAIOrders.size() > maxNumber){
            Collections.sort(arrAIOrders, new AIOrders.SortbyScoreDescending());
            int end = arrAIOrders.size()-1;
            int start =maxNumber;
            List<AIOrders> arrWork = arrAIOrders.subList(0,maxNumber);
            arrSmall = new ArrayList<AIOrders>(arrWork);
        }
        AIScorer.Type type = AIScorer.Type.NewProcess;
        AIFaker.instance.startScoringOrders(arrSmall, type, true);
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
