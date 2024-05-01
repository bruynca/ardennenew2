package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexCount;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.WinReinforcements;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Do the AI Allied Movement
     * 1. Initialize the aiScores
     * 2. Get all onboard allied units  except ar oget all
     * 3. Divide number of units into portions. prortion set to 4
     *    currently. Used to avoid out of memory and long waits.
     * 4. Set portion index to -1  and DoNextMove
     */
    public void doAlliesMove() {
        Gdx.app.log("AIScenarioOther", "doAlliesMove");
        ArrayList<HexInt> arrWork = AIUtil.getAIHexStats(2);
        ArrayList<Unit> arrUnits = new ArrayList<>();
        ArrayList<Unit> arrArtillery = new ArrayList<>();
        arrCovered.clear();
        Hex.initTempAI();
        Hex.addAIScoreSurroundGerman();
        Hex.addAISecondPanzerLehrOccupied();

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

    /**
     * 1. add 1 to index of portion we are working on.  if greater than
     *    portions available then do the handoff to mover.
     * 2. Get all the moves for each unit.
     * 3. Remove hexes that have been covered in previous portions
     * 4. Invoke AIProcess if it failed do the next if it
     *      add this as an observer to AIFaker.
     */
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

        aiProcess = new AIProcess(arrUnitPortion[ixNextMove],arrArr,arrDupes,2);
        if (aiProcess.isFailed()){
            doNextMove();
            return;
        }else{
            AIFaker.instance.addObserver(this);
        }
        return;
    }

    /**
     *  all done
     *  1. Combine portions if they are not null into 1 AIOrder
     *  2. Execute the mover with the AIOrder
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

    static class SortDescending implements Comparator<HexInt> {

        @Override
        public int compare(HexInt hexInt, HexInt t1) {
            if (hexInt == null && t1 == null){
                return 0;
            }
            if (hexInt == null){
                return -1;
            }
            if (t1 == null){
                return 1;
            }
            if (t1.count == hexInt.count){
                return 0;
            }
            if (t1.count > hexInt.count){
                return 1;
            }
            if (t1.count < hexInt.count){
                return -1;
            }
            return 0;
        }
    }

}
