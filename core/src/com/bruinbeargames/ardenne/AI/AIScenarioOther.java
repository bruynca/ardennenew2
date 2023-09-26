package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observer;

import sun.jvm.hotspot.utilities.Observable;

public class AIScenarioOther implements Observer {
    static public AIScenarioOther instance;
    ArrayList<Hex>[] arrMoves;
    ArrayList<AIOrders>[] arrOrders;
    AIScorer.Type type;
    AIProcess aiProcess;

    AIScenarioOther(){
        instance = this;
    }

    public void doAlliesMove() {
        Gdx.app.log("AIScenarioOther", "doAlliesMove");
        ArrayList<Unit> arrUnits = new ArrayList<>();
        ArrayList<Unit> arrArtillery = new ArrayList<>();
        for (Unit unit:Unit.getOnBoardAllied()){
            if (unit.getMovedLast() < NextPhase.instance.getTurn()){
                if (unit.isArtillery && unit.isLimbered()){
                    arrArtillery.add(unit);
                }else {
                    arrUnits.add(unit);
                }
            }
        }
        arrMoves = AIUtil.getUnitsMaxMove(arrUnits,0, false);
        ArrayList<ArrayList<Hex>> arrArr = new ArrayList<>();
        for (ArrayList<Hex> arr:arrMoves){
            arrArr.add(arr);
        }
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment
        AIFaker.instance.addObserver(this);
        Hex.initTempAI();
        Hex.addAIScoreSurroundGerman();
        Hex.addAISecondPanzerLehrOccupied();

        aiProcess = new AIProcess(arrUnits,arrArr,arrDupes);
        if (aiProcess.isFailed()){
            NextPhase.instance.nextPhase();
            return;
        }
    }

    /**
     *  all done
     * @param arrTop should only be 1
     *
     */
    private void doHandOffToMover(ArrayList<AIOrders> arrTop) {
        if (arrTop.get(0).arrUnit.size() == 0){
            NextPhase.instance.nextPhase();
            return;
        }
        AIMover.instance.execute(arrTop.get(0));
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
            doHandOffToMover(arrTop);
        }

    }
}
