package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.WinReinforcements;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import static com.bruinbeargames.ardenne.AI.AIOrders.combine;


public class AIReinforcementScenarioOther implements Observer {
    static public AIReinforcementScenarioOther instance;
    ArrayList<Unit> arrReinforcementsThisTurn = new ArrayList<>();
    ArrayList<Hex> arrEnemySurround = new ArrayList<>();
    int ixCurrentReinArea = 0;
    /**
     * set uo for each reinforcement area
     */
    ArrayList<Hex> arrReinforceAreas = new ArrayList<>();
    ArrayList<AIOrders>[] arrOrdersReinArea;
    ArrayList<ArrayList<Hex>[]>[] arrHexMoveReinArea;
    ArrayList<Unit>[] arrUnitReinArea;
    ArrayList<Unit> arrArtillery = new ArrayList<>();
    AIProcess aiProcess;
    Hex hexBastogneReinforceEntry = Hex.hexTable[0][19];
    Hex hexMartelangeReinforceEntry = Hex.hexTable[9][24];
    Hex hexEttlebruckReinforceEntry = Hex.hexTable[28][24];
    /**
     * because we have broken this up into 3 areas
     * Arrcovered will be set by each so that we do not have too many units
     */
    ArrayList<Hex> arrCovered = new ArrayList<>();


    public AIReinforcementScenarioOther(){
        instance = this;
        arrReinforceAreas.add(hexBastogneReinforceEntry);
        arrReinforceAreas.add(hexMartelangeReinforceEntry);
        arrReinforceAreas.add(hexEttlebruckReinforceEntry);
    }

    /**
     * For each Reinforcement point develop a package to be handed to the OrderProducer
     *
     * */
    public void doReinforcementAllies() {
        Gdx.app.log("AIReinforcementsOther", "doReinforcementalliea");

        /**
         *  initialize the aiScore used
         */
        arrCovered.clear();
        Hex.initTempAI();

        arrArtillery.clear();
        /**
         *  count reinforcement areas
         */
        int cntReinforceAreas = 0;
        for (Hex hex : arrReinforceAreas) {
            ArrayList<Unit> arrUnit = new ArrayList<>();
            arrUnit.addAll(Reinforcement.instance.getReinforcementsAvailable(hex, true));
            if (arrUnit.size() > 0){
                cntReinforceAreas++;
            }
        }


        /**
         *  initialize the units for each area
         *  if none availabel then there will be zero
         */
        arrUnitReinArea = new ArrayList[cntReinforceAreas];
        arrOrdersReinArea = new ArrayList[cntReinforceAreas];
        int ix = 0;
        for (Hex hex : arrReinforceAreas) {
            ArrayList<Unit> arrUnit = new ArrayList<>();
            arrUnit.addAll(Reinforcement.instance.getReinforcementsAvailable(hex, true));
            if (arrUnit.size() > 0) {
                arrUnitReinArea[ix] = arrUnit;
                ix++;
            }
        }
        ixCurrentReinArea = -1;
        doNextReinArea();
        return;
    }

    private void doNextReinArea() {
        Gdx.app.log("AIReinforcementsOther", "doNextReinArea ix"+ixCurrentReinArea);
        if (ixCurrentReinArea + 1 < arrUnitReinArea.length ) {
            Gdx.app.log("AIReinforcementsOther", "doNextReinArea size =" + arrUnitReinArea[ixCurrentReinArea + 1].size());
        }
        /**
         *  initialize the aiScore used
         *  also check for Germa occupied
         */
        Hex.initTempAI();
        Hex.addAIScoreSurroundGerman();
        Hex.addAISecondPanzerLehrOccupied();

        /**
         *  Do the next area based on ixCurrentInArea
         *  if none go to next
         *  else if none left go to do final
         */
        ixCurrentReinArea++;
        if (ixCurrentReinArea >= arrUnitReinArea.length) {
            doFinalAllAreasProcessign();
            return;
        }
        if (arrUnitReinArea[ixCurrentReinArea].size() == 0){
            doNextReinArea();
            return;
        }
        ArrayList<Unit>  arrWorkingOn = new ArrayList<>();
        arrWorkingOn.addAll(arrUnitReinArea[ixCurrentReinArea]);
        /**
         * seperate artillery
         */
        ArrayList<Unit> arrRemove  =  new ArrayList<>();
        for (Unit unit:arrWorkingOn){
            if (unit.isArtillery){
                arrArtillery.add(unit);
                arrRemove.add(unit);
            }
        }
        Gdx.app.log("AIReinforcementsOther", "After Artllery size ="+arrRemove.size());
        arrWorkingOn.removeAll(arrRemove);
        if (arrWorkingOn.size() == 0){
            doNextReinArea();
            return;
        }
        /**
         *  this is our chance to change the aiscoreGen  in Hex
         *  it has been initialized
         *  for now we are not doing amything to keep it simple
         */


        /**
         * Create the moves
         */
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();
        ArrayList<Unit> arrCantMove = new ArrayList<>();
        int entryCost=0;
        for (Unit unit : arrWorkingOn) {
            ArrayList<Hex> arrHexMove = AIReinforcementScenario1.instance.findHexesOnReinforcement(unit,entryCost);
            entryCost++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
            arrHexMove.removeAll(arrCovered);
            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }
        }
        Gdx.app.log("AIReinforcementsOther", "After cant move size ="+arrWorkingOn.size());

        arrWorkingOn.removeAll(arrCantMove);
        if (arrWork.size() == 0){
            doNextReinArea();
            return;
        }
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment
        aiProcess = new AIProcess(arrWorkingOn,arrWork,arrDupes,1);
        if (aiProcess.isFailed()){
            doNextReinArea();
            return;
        }else{
            AIFaker.instance.addObserver(this);
        }
    }

    private void doFinalAllAreasProcessign() {
        AIOrders aiStart = new AIOrders();
        for (ArrayList<AIOrders> arr:arrOrdersReinArea){
            if (arr != null){
                AIOrders aiNew = AIOrders.combine(aiStart,arr.get(0),true);
                aiStart = aiNew;
            }
        }
        Gdx.app.log("AIReinforcementScenarioOther", "do final processing # units="+aiStart.arrUnit.size());

        if (aiStart.arrUnit.size() == 0) {
            WinReinforcements winReinforcements = Reinforcement.instance.getScreen();
            winReinforcements.end();
            return;
        }
        if (arrArtillery.size() > 0){
            doArtillery(aiStart);
        }
        Gdx.app.log("AIReinforcementScenarioOther", "execute");

        execute(aiStart);

    }

    private void doArtillery(AIOrders aiStart) {
        /**
         *  add artillery closes to bastogne
         */
        Hex hex = AIUtil.findClosestHex(aiStart.arrHexMoveTo,Hex.hexBastogne2);
        if (aiStart.arrHexMoveTo.contains(Hex.hexBastogne1)){
            hex = Hex.hexBastogne1;
        }
        if (aiStart.arrHexMoveTo.contains(Hex.hexBastogne2)){
            hex = Hex.hexBastogne2;
        }
        for (Unit unit:arrArtillery){
            aiStart.arrUnit.add(unit);
            aiStart.arrHexMoveTo.add(hex);
        }
    }

    public void execute(AIOrders aiOrdersIn){
        Gdx.app.log("AIReinforcementScenarioOther", "execute");
        AIExecute.instance.Reinforcement(aiOrdersIn);
    }




    @Override
    public void update(Observable observable, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakerDone) {
            Gdx.app.log("AIReinforcementsOther", "Received Faker");
            AIFaker.instance.deleteObserver(this);
            /**
             *  orders returned
             *  sort descending
             *  get top 10%
             *  move into storage are for this reinforcement area
             */
            ArrayList<AIOrders> arrWork = new ArrayList<>();
            arrWork.addAll(aiProcess.getAIOrders());
            Collections.sort(arrWork, new AIOrders.SortbyScoreDescending());
            ArrayList<AIOrders> arrTop = AIOrders.gettopNumber(arrWork, 1);
            for (Hex hex:arrTop.get(0).arrHexMoveTo){
                arrCovered.add(hex);
            }
            arrOrdersReinArea[ixCurrentReinArea] = new ArrayList<>();
            arrOrdersReinArea[ixCurrentReinArea].addAll(arrTop);

            doNextReinArea();
        }

    }
}
