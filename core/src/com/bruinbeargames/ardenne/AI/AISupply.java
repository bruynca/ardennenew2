package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *  Overview
 *   AiSupply is used by 2 Mover and Reinforcements
 *    Reinforcements use the Hard Coded combinations of the  Block Points.
 *    to reduce time only single and double permutations are used.  The Permutations
 *    are scored (getscore) and the list is available as an array of AIOrders to the reinforcements.
 */
public class AISupply implements Observer {
    public static AISupply instance;
    private I18NBundle i18NBundle;

    /**
     * an Array which conatins orders with 1, 2 or 3 hexs to be blocked to generate score
     */
    ArrayList<AIOrders> arrRoadBlocks = new ArrayList<>();
    ArrayList<AIOrders> arrOrders = new ArrayList<>();
    ArrayList<AIOrders> arrRoadBlocksProcessed = new ArrayList<>();
    /**
     *  the score on present situation on the map
     */
    int scoreRegular = 0;
    ArrayList<Unit> arrGermanUnitsOnBoard = new ArrayList<>();
    boolean isScenario1 =false;
    boolean isScenario2 = false;
    boolean isScenario3 =false;
    boolean isScenario4 = false;

    int[][] aroundBastogne ={{8,10},{8,9},{8,11},{8,12},{8,13},{7,10},{7,11},{7,12},{9,10},{9,11}
                            ,{9,12}};
    int[][] aroundWiltz ={{19,13},{19,14},{19,15},{20,14},{20,15},{18,14},{18,15}};

    ArrayList<Hex> arrAroundBastogne = new ArrayList<>();
    ArrayList<Hex> arrAroundWiltz = new ArrayList<>();
    boolean isMove = false;


    AISupply() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
        arrRoadBlocks.clear();

        /**
         *  generate the aiOrders array for all the combinations
         *  of 1, 2 and 3 hex blocked to inhibit German Supply.
         */

        /**
         *  iterators doesnt work with 1
         */
        for (Hex hex : Supply.instance.getGermanBottlenecks()) {
            ArrayList<Hex> arrWork = new ArrayList<>();
            arrWork.add(hex);
            AIOrders aiOrders = new AIOrders(AIOrders.Type.SupplyBlocks, arrWork, null, null);
            arrRoadBlocks.add(aiOrders);
        }
        /**
         *  dont remove dupes
         *  not that many of them
         */
        ArrayList<Hex>[] arrGenerateIterator = new ArrayList[2];
        arrGenerateIterator[0] = new ArrayList<>();
        arrGenerateIterator[0].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[1] = new ArrayList<>();
        arrGenerateIterator[1].addAll(Supply.instance.getGermanBottlenecks());
        arrRoadBlocks.addAll(generateRoadBlocks(arrGenerateIterator));
/*
        arrGenerateIterator = new ArrayList[3];
        arrGenerateIterator[0] = new ArrayList<>();
        arrGenerateIterator[0].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[1] = new ArrayList<>();
        arrGenerateIterator[1].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[2] = new ArrayList<>();
        arrGenerateIterator[2].addAll(Supply.instance.getGermanBottlenecks());
        arrRoadBlocks.addAll(generateRoadBlocks(arrGenerateIterator));
*/
 /*       arrGenerateIterator = new ArrayList[4];
        arrGenerateIterator[0] = new ArrayList<>();
        arrGenerateIterator[0].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[1] = new ArrayList<>();
        arrGenerateIterator[1].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[2] = new ArrayList<>();
        arrGenerateIterator[2].addAll(Supply.instance.getGermanBottlenecks());
        arrGenerateIterator[3] = new ArrayList<>();
        arrGenerateIterator[3].addAll(Supply.instance.getGermanBottlenecks());
        arrRoadBlocks.addAll(generateRoadBlocks(arrGenerateIterator)); */

        /**
         * setup areas around wiltz and bastogne
         */
        for (int[] in:aroundBastogne){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrAroundBastogne.add(hex);
            arrAroundBastogne.addAll(hex.getSurround());
        }
        AIUtil.RemoveDuplicateHex(arrAroundBastogne);

        for (int[] in:aroundWiltz){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrAroundWiltz.add(hex);
            arrAroundWiltz.addAll(hex.getSurround());
        }
        AIUtil.RemoveDuplicateHex(arrAroundWiltz);


    }

    /**
     * Using the Iterator function generate combinations
     *
     * @param arrGenerateIterator
     * @return
     */
    private ArrayList<AIOrders> generateRoadBlocks(ArrayList<Hex>[] arrGenerateIterator) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        AIIterator aiIterator = new AIIterator(arrGenerateIterator, null, null, AIOrders.Type.SupplyBlocks);
        AIOrders aiOrders = aiIterator.Iteration();
        while (aiOrders != null) {
            aiOrders = aiIterator.doNext();
            if (aiOrders != null) {
                arrReturn.add(aiOrders);
                aiOrders = aiIterator.doNext();
            }
        }
        return arrReturn;

    }

    /**
     *  using the faker go through all combinations to check for Reinforcements
     *  arrRoadBlocks have been set
     */
    public void doSupplyAnalysis() {
        doSupplyAnalysis(arrRoadBlocks,false);
    }

    /**
     *  Do the analysis of Supply
     *  Move will call directly with best scores ascending
     *  Reinforcements  will use the roadblocks set up
     * @param arrOrdersIn
     */
    public void doSupplyAnalysis(ArrayList<AIOrders> arrOrdersIn, boolean isMove) {
        this.isMove = isMove;
        arrOrders.clear();
        arrOrders.addAll(arrOrdersIn);
        // do first
        /**
         *  set scenario specific rules
         */
        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
            isScenario1 = true;
        }

        arrGermanUnitsOnBoard.clear();
        for (Unit unit:Unit.getOnBoardAxis()) {
            if (isScenario1) {
                if (arrAroundBastogne.contains(unit.getHexOccupy()) ||
                    arrAroundWiltz.contains(unit.getHexOccupy())) {
                    arrGermanUnitsOnBoard.add(unit);
                }

            }
        }
        // then get score
        scoreRegular = scoreSupply(0);
        /**
         *  zero scores
         */
        if (!isMove) {
            for (AIOrders aiO : arrOrders) {
                aiO.setScoreMain(0);
            }
        }
        EventAI.instance.show(i18NBundle.format("aisupply"));
        AIFaker.instance.addObserver(this);
        AIFaker.instance.startScoringOrders(arrOrders, AIScorer.Type.Supply,true);
    }

    /**
     *  score the units in supply
     *  the higher the score the worse for Allied AI
     *
     * @param thread to use
     * @return
     */
    private int scoreSupply(int thread) {
        int score =0;
        /**
         *  get all hexes in supply
         */
        ArrayList<Unit> arrNotInsupply = new ArrayList<>() ;

        Unit unitTransport = Unit.getTransports(false).get(0);
        int moveLength = Supply.instance.toUnit + Supply.instance.initialRange;
 //       int moveLength = Supply.instance.initialRange;
        ArrayList<Hex> arrInSupply = new ArrayList<>();
        for (Hex hex:Supply.instance.getGermanSupply()) {
            UnitMove unitMove = new UnitMove(unitTransport, moveLength, false, true, hex, thread);
            arrInSupply.addAll(unitMove.getMovePossible(thread));
        }
        AIUtil.RemoveDuplicateHex(arrInSupply);
        for (Unit unit:arrGermanUnitsOnBoard){
            if (!arrInSupply.contains(unit.getHexOccupy())){
                arrNotInsupply.add(unit);
            }
        }

        /**
         *  score them
         */
        for (Unit unit:arrNotInsupply){
              score += unit.getAtStartAttackFactor();
        }
        if (thread == 0){
 //           HiliteHex hiliteHex = new HiliteHex(arrInSupply, HiliteHex.TypeHilite.AI,null);
        }


        return score;
    }

    /**
     * return the score based on block of hexs with units
     * Hexes have been blocked in the thread by faker
     * calculate
     *
     * @param thread thread for AI
     * @return
     */
    public int getScore(int thread) {
        int score = 0;
        int scoreSupply =0;
        scoreSupply = scoreSupply(thread);
        score = scoreSupply - scoreRegular;
        if (score > 0){
            score =0;
        }
        if (isScenario1 && NextPhase.instance.getTurn() < 3){
            score *=10;
        }
        return score;
    }

    /**
     *  Sort descending
     *  and sort into
     */
    private void processAfterScoring() {
        arrRoadBlocksProcessed.clear();
        int i=0;
        for (AIOrders aiO: arrOrders){
            for (i=0; i< arrRoadBlocksProcessed.size(); i++){;
                if (aiO.scoreMain > arrRoadBlocksProcessed.get(i).scoreMain){
                    break;
                }
            }
            arrRoadBlocksProcessed.add(i,aiO);
        }

    }
    public ArrayList<AIOrders> getProcessedRoadBlocks(){
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrRoadBlocksProcessed);
        return arrReturn;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.SupplyDone) {
            AIFaker.instance.deleteObserver(this);
            processAfterScoring();
            if (NextPhase.instance.getPhase() == Phase.ALLIED_REINFORCEMENT.ordinal()) {
                AIReinforcement.instance.reinforceAnalysis(true);
                return;
            } else {
                if (NextPhase.instance.getPhase() == Phase.ALLIED_MOVEMENT.ordinal()) {
                    AIScenario1.instance.handOffToMover(arrRoadBlocksProcessed);
                }
            }


        }
    }
}
