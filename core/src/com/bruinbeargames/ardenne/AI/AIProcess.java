package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

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
    private VisWindow visWindow;

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
        int iterates = 1;
        arrAIOrders.clear();
        for (ArrayList<Hex> arr:arrArrayOfHexArray){
            iterates *= arr.size();
            if (arr.contains(AISetScore.instance.hexWiltz)){
                int b=0;
            }
        }
        Gdx.app.log("AIProcess", "Iteratiosn Before ="+iterates);

        if (iterates < 0 || iterates > 1000000) {
            AIUtil.reduceToMillion(arrArrayOfHexArray);
        }
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
        if (NextPhase.instance.getTurn() > 3 && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
            arrDupes.clear();
            arrDupes.addAll(AISetScore.instance.hexBastogne1.getSurround());
            arrDupes.addAll(AISetScore.instance.hexBastogne2.getSurround());
            arrDupes.addAll(AISetScore.instance.hexWiltz.getSurround());
            arrDupes.add(AISetScore.instance.hexBastogne1);
            arrDupes.add(AISetScore.instance.hexBastogne2);
            arrDupes.add(AISetScore.instance.hexWiltz);
            AIUtil.RemoveDuplicateHex(arrDupes);
        }
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
        int maxNumber = 180000;
        if (arrAIOrders.size() > maxNumber){
            Collections.sort(arrAIOrders, new AIOrders.SortbyScoreDescending());
            int end = arrAIOrders.size()-1;
            int start =maxNumber;
            List<AIOrders> arrWork = arrAIOrders.subList(0,maxNumber);
            arrSmall = new ArrayList<AIOrders>(arrWork);
        }else{
            arrSmall.addAll(arrAIOrders);
        }
 //       leaveInMajorCity(arrSmall);
        doHandOff(arrSmall);
    }



    private void doHandOff(ArrayList<AIOrders> arrOrders){
//        EventAI.instance.setIterations(arrOrders.size());
        AIScorer.Type type = AIScorer.Type.NewProcess;
        AIFaker.instance.startScoringOrders(arrOrders, type, true);
    }

    /**
     *  if units are in major city make sure at least 1 left there
     * @param arrSmall
     *
     */
    public void leaveInMajorCity(ArrayList<AIOrders> arrSmall) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        ArrayList<Hex> arrRemove = new ArrayList<>();
        for (AIOrders aiO:arrSmall){
            arrRemove.clear();
            for (Hex hex:aiO.arrHexMoveTo){
                if (Hex.arrMajorCities.contains(hex)){
                    arrRemove.add(hex);
                }
            }
            if (arrRemove.size() > 0) {
                AIUtil.RemoveDuplicateHex(arrRemove); // just delete 1
                for (Hex hex : arrRemove) {
                    int ix = aiO.arrHexMoveTo.indexOf(hex);
                    aiO.arrUnit.remove(ix);
                    aiO.arrHexMoveTo.remove(ix);
                }
            }
        }
    }

    public boolean isFailed(){
        return isFailed;
    }

    public ArrayList<AIOrders> getAIOrders() {
        return arrAIOrders;
    }
    private void creatAIWindow(final ArrayList<AIOrders> arrSmall) {
        Gdx.app.log("AIProcess", "Create AI Window");

        visWindow = new VisWindow("AI View");
        VisTextButton visTextButton = new VisTextButton("Run");
        visWindow.add(visTextButton);
        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                visWindow.remove();
                doHandOff(arrSmall);
            }

        });
        ardenne.instance.guiStage.addActor(visWindow);
    }
}
