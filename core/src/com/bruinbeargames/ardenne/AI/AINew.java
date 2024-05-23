package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

public class AINew implements Observer {
    static public AINew instance;
    AIProcess aiProcess;
    AINew(){
        instance = this;
    }

    public void doAlliesMove() {
        /**
         * do the AIAlled Move
         * Assume all aiscores have been set
         * Get all allied units except for Artilley
         */
        ArrayList<Unit> arrArtillery = new ArrayList<>();
        ArrayList<Unit> arrUnits = new ArrayList<>();
        for (Unit unit : Unit.getOnBoardAllied()) {
            if (unit.getMovedLast() < NextPhase.instance.getTurn()) {
                if (unit.isArtillery) {
                    arrArtillery.add(unit);
                } else {
                    arrUnits.add(unit);
                }
            }
        }
        /**
         *  get all moves possible
         */
        ArrayList<Hex>[] arrMoves = AIUtil.getUnitsMaxMove(arrUnits,0, false);
        /**
         *  remove any that have no AI score
         *
         *  this is done
         */
        int ix=0;
        ArrayList<ArrayList<Hex>> arrArr = new ArrayList<>();
        for (ArrayList<Hex> arr:arrMoves){
            ArrayList<Hex> arrDelete = new ArrayList<Hex>();
            for (Hex hex:arr){
                if (hex.getAiScore() == 0){
                    if (!arrDelete.contains(hex)){
                        arrDelete.add(hex);
                    }
                }
            }
            arr.removeAll(arrDelete);
            /**
             * if no place to move leave it where it is
             */
            if (arr.size() == 0){
                Gdx.app.log("AINew", "unit has no move-"+arrUnits.get(ix));
                arr.add(arrUnits.get(ix).getHexOccupy());
            }
            arrArr.add(arr);
            ix++;
        }
        int b =0;
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment

        aiProcess = new AIProcess(arrUnits,arrArr,arrDupes,99);
        if (aiProcess.isFailed()){
            EventAI.instance.hide();
            NextPhase.instance.nextPhase();
            return;
        }else{
            AIFaker.instance.addObserver(this);
        }
        return;


    }
    private void doHandOffToMover(ArrayList<AIOrders> arrTop) {
        AIOrders aiStart = arrTop.get(0);
        Gdx.app.log("AINew", "do final processing # units="+aiStart.arrUnit.size());

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
    public void update(Observable observable, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakerDone) {
            Gdx.app.log("AINew", "Received Faker");
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
            doHandOffToMover(arrTop);
        }


    }
}
