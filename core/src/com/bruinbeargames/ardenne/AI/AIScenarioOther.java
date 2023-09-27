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
import java.util.Observer;

public class AIScenarioOther implements Observer {
    static public AIScenarioOther instance;
    ArrayList<Hex>[] arrMoves;
    ArrayList<AIOrders>[] arrOrders;
    AIScorer.Type type;
    AIProcess aiProcess;
    ArrayList<AIOrders>[] arrOrdersPortion;
    ArrayList<Unit>[] arrUnitPortion;
    int ixNextMove;
    ArrayList<Hex> arrCovered = new ArrayList<>();



    AIScenarioOther(){
        instance = this;
    }

    public void doAlliesMove() {
        Gdx.app.log("AIScenarioOther", "doAlliesMove");
        ArrayList<Unit> arrUnits = new ArrayList<>();
        ArrayList<Unit> arrArtillery = new ArrayList<>();
        arrCovered.clear();
        /**
         * dont do artillery
         */
        for (Unit unit : Unit.getOnBoardAllied()) {
            if (unit.getMovedLast() < NextPhase.instance.getTurn()) {
                if (unit.isArtillery) {
                    arrArtillery.add(unit);
                } else {
                    arrUnits.add(unit);
                }
            }
        }
        int portion = 4;
        /**
         *  get number of portions dividing by portions
         */
        int numPortions = (arrUnits.size() / portion);
        if (arrUnits.size() % portion > 0){
            numPortions++;
        }
        arrUnitPortion = new ArrayList[numPortions];
        arrOrdersPortion = new ArrayList[numPortions];
        /**
         *  spread out units to portions
         */
        for (int i = 0; i < numPortions; i++) {
            int toMOve = portion;
            if (arrUnits.size() <= portion) {
                toMOve = arrUnits.size();
            }
            arrUnitPortion[i] = new ArrayList<>();
            for (int j = 0; j < portion; j++) {
                if (j < arrUnits.size()) {
                    arrUnitPortion[i].add(arrUnits.get(j));
                }
            }
            arrUnits.removeAll(arrUnitPortion[i]);
        }
        ixNextMove = -1;
        /**
         *  initialize the aiScore used
         *  also check for Germa occupied
         */

        doNextMove();
    }
    private void doNextMove(){
        Gdx.app.log("AIScenarioOther", "doNextReinArea ix"+ixNextMove);
        if (ixNextMove + 1 < arrUnitPortion.length ) {
            Gdx.app.log("AIScenariother", "doNextReinArea size =" + arrUnitPortion[ixNextMove + 1].size());
        }
        ixNextMove++;
        if (ixNextMove >= arrUnitPortion.length){
            doHandOffToMover();
            return;
        }
        arrMoves = AIUtil.getUnitsMaxMove(arrUnitPortion[ixNextMove],0, false);
        for (ArrayList<Hex> arr:arrMoves){
            arr.removeAll(arrCovered);
        }
        ArrayList<ArrayList<Hex>> arrArr = new ArrayList<>();
        for (ArrayList<Hex> arr:arrMoves){
            arrArr.add(arr);
        }
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment
        AIFaker.instance.addObserver(this);
        Hex.initTempAI();
        Hex.addAIScoreSurroundGerman();
        Hex.addAISecondPanzerLehrOccupied();

        aiProcess = new AIProcess(arrUnitPortion[ixNextMove],arrArr,arrDupes,2);
        if (aiProcess.isFailed()){
            doNextMove();
            return;
        }
        return;
    }

    /**
     *  all done
     *
     */
    private void doHandOffToMover() {
        AIOrders aiStart = new AIOrders();
        for (ArrayList<AIOrders> arr:arrOrdersPortion){
            if (arr != null){
                AIOrders aiNew = AIOrders.combine(aiStart,arr.get(0),true);
                aiStart = aiNew;
            }
        }
        Gdx.app.log("AIScenarioOther", "do final processing # units="+aiStart.arrUnit.size());

        if (aiStart.arrUnit.size() == 0) {
            NextPhase.instance.nextPhase();
            return;
        }
        Gdx.app.log("AIcenarioOther", "execute");

        execute(aiStart);
    }
    private  void execute(AIOrders aiOrdersIn){
        Gdx.app.log("AIScenarioOther", "execute");
        AIMover.instance.execute(aiOrdersIn);
    }



    @Override
    public void update(java.util.Observable observable, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakerDone) {
            Gdx.app.log("AIScenariOthers", "Received Faker");
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
            arrOrdersPortion[ixNextMove] = new ArrayList<>();
            arrOrdersPortion[ixNextMove].addAll(arrTop);
            doNextMove();
        }

    }
}
