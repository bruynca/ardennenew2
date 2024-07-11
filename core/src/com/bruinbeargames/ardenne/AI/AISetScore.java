package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

/**
 *  Set the 2 ai scores
 *   do an anlysis - to bdone
 *   set the AISCore to screen the initial iteraions placements
 *   set the AIScoreFaker to score the iterations
 */
public class AISetScore {
    public static AISetScore instance;
    int[][] bestHexDefenseTurn1 ={{29,9,4},{31,13,4},{33,17,4},{35,20,4},{28,23,4}, // first row of bridges
            {25,8,3},{24,15,3},{29,22,3}, //second row of bridges
            {21,6,3},{18,9,3},{14,10,3},{12,11,3}, // top road
            {28,14,3},{20,14,3},{19,20,3},{16,23,3},{9,23,3}, // bottom road
            {8,11,5},{8,12,5},{19,14,5},
            {26,8,2},{27,8,2},{25,14,2},{28,13,2},{31,21,2},{11,22,2}};
    int[][] bastogneDefenseReinforcement ={{8,11,6},{8,12,6},{9,10,5},{9,11,5},{9,12,5}, // first row of bridges
            {8,13,5},{10,11,3},{12,11,3},{8,10,5},
            {7,10,3},{7,11,3},{7,13,3},//second row of bridges
            {14,10,4},{11,12,4},{9,14,4},{6,18,3},{11,7,2}};
    int[][] supplyBreakup={{31,13,3},{28,11,3},{27,8,3},{25,8,3},{33,17,3}};
    Hex hexBastogne1 = Hex.hexTable[8][11];
    Hex hexBastogne2 = Hex.hexTable[8][12];
    Hex hexMartelange = Hex.hexTable[9][23];
    Hex hexWiltz = Hex.hexTable[19][14];
    Hex hexEttlebruck = Hex.hexTable[28][23];
    Hex hexBastogneReinforceEntry = Hex.hexTable[0][19];
    Hex hexMartelangeReinforceEntry = Hex.hexTable[9][24];
    Hex hexEttlebruckReinforceEntry = Hex.hexTable[28][24];
    ArrayList<Unit> arrUnits = new ArrayList<>();
    ArrayList<Hex>[] arrMoves = null;
    public ArrayList<Unit> arrUnitsToBloack;
    public Strategy strategy;

    AISetScore(){
        instance = this;
    }
    public void scoreMove(){
        Gdx.app.log("AISetScore", "scoreMove");

        Hex.initAI();
        Hex.initAIFaker();
        /**
         *  supply point
         */
        loadSupplyBottlenecks(2);
        if (NextPhase.instance.getTurn() < 4){
            Gdx.app.log("AISetScore", "doInitialTurns");

            doInitialTurns();
            return;
        }else{
            if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
                AIReinforcementScenario1.BastogneWiltzDefenseStatus bastogneStatus =
                        new AIReinforcementScenario1.BastogneWiltzDefenseStatus(arrUnits,arrMoves);
                Gdx.app.log("AISetScore", "Strategy="+bastogneStatus.strategy);

                if (bastogneStatus.strategy == AIReinforcementScenario1.StrategyBastogne.WiltzAttack ||
                    bastogneStatus.strategy == AIReinforcementScenario1.StrategyBastogne.WiltzFree){
                    scoreWiltzScene1();
                    return;
                }else{
                    scoreBastogneScene1();
                }
            }else{
                scoreAfterTurn3();
            }
        }

    }



    private void loadSupplyBottlenecks(int score) {
        for (int[] hexI:supplyBreakup){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAI(score);
            hex.setAiScoreFaker(score);
        }

    }


    /**
     *  used for scenario 1 to detemine attack against bastogne or wilts
     * @param arrUnits
     * @param arrWork
     */
    public  void scoreMove(ArrayList<Unit> arrUnits, ArrayList<Hex>[] arrWork){

        this.arrUnits.clear();
        this.arrUnits.addAll(arrUnits);
        this.arrMoves = arrWork;
        scoreMove();
    }
    public void scoreReinforcement(Hex hex) {
        Hex.initAI();
        Hex.initAIFaker();
        setupBastogne();
        loadAIScoreFakerBastogne();
        setUpMatrelange();
        loadAIScoreFakerEttlebruck();
        setUpEttleBruck();
        loadAIScoreFakerMartelange();
    }

    private void doInitialTurns() {
        loadAIScore(bestHexDefenseTurn1);
        loadAIScoreFakerInitial();
        for (ArrayList<Hex> arr:arrMoves){
            ArrayList<HexInt> arrSorted = AIUtil.countandSortCloseToAscending(hexBastogne1,arr);
            if (arrSorted.size() > 2) {
                arrSorted.get(0).hex.setAI(2);
                arrSorted.get(1).hex.setAI(1);
            }
        }
    }
    public void loadAIScore(int[][] bestHexs) {
        for (int[] hexI:bestHexs){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAI(hexI[2]);
        }

    }
    private  void loadAIScoreFakerInitial(){
        /**
         * bastogne
         */
        setScoreRoadPathFaker(hexBastogne1,6,Direction.All);
        setScoreRoadPathFaker(hexBastogne2,6,Direction.All);
        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            setScoreRoadPathFaker(hexWiltz,6,Direction.Right);
        }else{
            setScoreRoadPathFaker(hexWiltz,4,Direction.Right);
        }
        setScoreRoadPathFaker(hexMartelange,5,Direction.Right);
        setScoreRoadPathFaker(hexEttlebruck,4,Direction.Left);
    }

    private  void loadAIScoreFakerBastogne(){
        /**
         * bastogne
         */
        setScoreRoadPathFaker(hexBastogne1,8,Direction.All);
        setScoreRoadPathFaker(hexBastogne2,8,Direction.All);
        /**
         *  double for ring around bastogne
         */
        for (int[] hexI:bastogneDefenseReinforcement){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAiScoreFaker(hex.getAiScoreFaker()*2); // multiply by 2
        }

    }
    private  void loadAIScoreFakerEttlebruck(){
        /**
         * bastogne
         */
        setScoreRoadPathFaker(hexEttlebruckReinforceEntry,8,Direction.All);
        setScoreRoadPathFaker(hexEttlebruck,8,Direction.All);
        hexEttlebruckReinforceEntry.setAI(1);
        /**
         *  double for ring around bastogne
         */

    }
    private  void loadAIScoreFakerMartelange(){
        /**
         * bastogne
         */
        setScoreRoadPathFaker(hexMartelangeReinforceEntry,8,Direction.All);
        setScoreRoadPathFaker(hexMartelangeReinforceEntry,8,Direction.All);
        /**
         *  double for ring around bastogne
         */

    }

    private void scoreWiltzScene1() {
        setScoreOnRoadPathAI(hexWiltz,200,Direction.All);
        for (Hex hex:hexWiltz.getSurround()){
                hex.setAI(100);
        }
        /**
         */
        for (ArrayList<Hex> arr:arrMoves){
            ArrayList<HexInt> arrSorted = AIUtil.countandSortCloseToAscending(hexWiltz,arr);
            if (arrSorted.size() > 2) {
                arrSorted.get(0).hex.setAI(2);
                arrSorted.get(1).hex.setAI(1);
            }
        }

        setScoreRoadPathFaker(hexWiltz,5,Direction.All);

        hexWiltz.setAiScoreFaker(200);
        for (Hex hex:hexWiltz.getSurround()){
            if (hex.isAlliedZOC()){
                hex.setAiScoreFaker(109);
            }
        }



    }
    private void scoreBastogneScene1() {
        setScoreOnRoadPathAI(hexBastogne1, 5, Direction.All);
        hexBastogne1.setAI(200);
        hexBastogne2.setAI(200);

        for (Hex hex : hexBastogne1.getSurround()) {
            hex.setAI(100);
        }
    //    Hex[][] test = hexBastogne1.getSurround(4);
        /**
         *  get sorted distance map from Bastogne
         *  get 2 hexes if available and mark them with aiscore
         *  so that we will get in solution
         */
        for (ArrayList<Hex> arr:arrMoves){
            ArrayList<HexInt> arrSorted = AIUtil.countandSortCloseToAscending(hexBastogne1,arr);
            if (arrSorted.size() > 2) {
                arrSorted.get(0).hex.setAI(2);
                arrSorted.get(1).hex.setAI(1);
            }
        }

        /**
         *
         */
        setScoreRoadPathFaker(hexBastogne1, 6, Direction.All);
        hexBastogne1.setAiScoreFaker(200);
        hexBastogne2.setAiScoreFaker(200);
        for (Hex hex : hexBastogne1.getSurround()) {
            //           if (hex.isAlliedZOC()){
            hex.setAiScoreFaker(100);
            //           }
        }
        for (Hex hex : hexBastogne2.getSurround()) {
            //           if (hex.isAlliedZOC()){
            hex.setAiScoreFaker(12);
            //           }
        }
    }



    /**
     *  set the aiscoreFaker starting at hex
     *  each branch makes score seem smaller
     * @param hexIn
     * @param score -score to start out
     */
    public static void setScoreRoadPathFaker(Hex hexIn, int score, Direction direct) {
        int start = score;
        hexIn.setAiScoreFaker(start);
        start--;
        Hex[][] arrArr =  hexIn.getSurround(start);
        ArrayList<Hex> arrWork = hexIn.getSurround();
        for (Hex[] arr:arrArr){
            for (Hex hex:arr) {
                if (hex != null) {
                    if (hex.isRoad() || hex.isPath()) {
                        if (direct == Direction.All) {
                            hex.setAiScoreFaker(start);
                        } else if (direct == Direction.Right && hexIn.xTable <= hex.xTable) {
                            hex.setAiScoreFaker(start);
                        } else if (direct == Direction.Left && hexIn.xTable >= hex.xTable) {
                            hex.setAiScoreFaker(start);
                        }
                    }
                }
            }
            start--;
        }

    }
    public static ArrayList<Hex> setScoreOnRoadPathAI(Hex hexIn, int score, Direction direct) {
        int start = score;
        ArrayList<Hex> arrScored = new ArrayList<>();
        hexIn.setAiScoreFaker(start);
        start--;
        Hex[][] arrArr =  hexIn.getSurround(start);
        ArrayList<Hex> arrWork = hexIn.getSurround();
        for (Hex[] arr:arrArr){
            for (Hex hex:arr) {
                if (hex != null) {
                    if (hex.isRoad() || hex.isPath()) {
                        if (direct == Direction.All) {
                            hex.setAI(start);
                            arrWork.add(hex);
                        } else if (direct == Direction.Right && hexIn.xTable <= hex.xTable) {
                            arrWork.add(hex);
                            hex.setAI(start);
                        } else if (direct == Direction.Left && hexIn.xTable >= hex.xTable) {
                            arrWork.add(hex);
                            hex.setAI(start);
                        }
                    }
                }
            }
            start--;
        }
        return arrScored;
    }



    public enum Direction {All, Left, Right, };
    private void setUpMatrelange() {
        for (Hex hex:hexMartelangeReinforceEntry.getSurround()){
            hex.setAI(4);
        }
        setScoreOnRoadPathAI(hexMartelangeReinforceEntry, 10, Direction.All);

    }

    private void setUpEttleBruck() {
        for (Hex hex:hexEttlebruckReinforceEntry.getSurround()){
            hex.setAI(4);
        }
        setScoreOnRoadPathAI(hexEttlebruckReinforceEntry, 10, Direction.All);


    }

    private void setupBastogne() {
        loadAIScore(AIMover.instance.bestHexDefenseTurn1);
        /**
         *  double for ring around Bastogne
         */
        for (int[] hexI:bastogneDefenseReinforcement){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAI(hexI[2]*2); // multiply by 2
        }
        /**
         *  aff score to the roads
         */
        setScoreOnRoadPathAI(Hex.hexBastogne2, 13, Direction.All);
        /**
         *  set up score for around entry
         *
         */
        ArrayList<Hex> arrWork = hexBastogneReinforceEntry.getSurroundMapArr(hexBastogneReinforceEntry,3);
        for (Hex hex: arrWork){
            hex.setAI(2);
        }
    }

    /**
     *  handle the score set for non scenario a after turn 3
     *
     */
    private void scoreAfterTurn3() {
        /**
         *  aiScore
         *  score bastogne and surrounding big
         *  score ettlebruck and martelang roads  and any enemy close to them
         *  score units we are trying to destroy 2nd Panzer and Lehr
         *  score Supply if none left for second panzer
         *
         *  aiFakerScore
         *   score units trying to destroy including block units
         *   score supplu ??
         *
         */
        Gdx.app.log("AISetScore", "scoreafterturn3");

        int haveExitted = 0;
        int needToExit = 0;
        int totalUnits = 0;
        strategy = Strategy.Attack;
        /**
         *  do the counts for secon panzer and Lehr
         */
        arrUnitsToBloack =  new ArrayList<>();
        for (Unit unit: Unit.getOnBoardAxis()){
                if (SecondPanzerExits.instance.isInSecond(unit)){
                    if (SecondPanzerExits.instance.unitExit1.contains(unit) ||
                        SecondPanzerExits.instance.unitExit2.contains(unit)){
                        haveExitted++;
                    }else{
                        needToExit++;
                        arrUnitsToBloack.add(unit);
                    }
                    totalUnits++;
                }
                if (GameSetup.instance.getScenario().ordinal() > GameSetup.Scenario.SecondPanzer.ordinal()) {
                    if (LehrExits.instance.isInLehr(unit)) {
                        if (LehrExits.instance.unitExit1.contains(unit) ||
                                LehrExits.instance.unitExit2.contains(unit)) {
                            haveExitted++;
                        } else {
                            needToExit++;
                            arrUnitsToBloack.add(unit);
                        }
                        totalUnits++;
                    }
                }
        }
        /** come up with startegy
         *
         */
        if (needToExit > haveExitted){
            Gdx.app.log("AISetScore", "strategy block");

            strategy =Strategy.Block;
        }else{
            strategy = Strategy.Supply;
            loadSupplyBottlenecks(8);
            Gdx.app.log("AISetScore", "strategy Supply");
        }
        /**
         *  for reinforcement points  set up free road network  by surroundin the enemy
         */
        setUpRoadNetworkAttack(hexEttlebruck,5,5);
        setUpRoadNetworkAttack(hexMartelange,5,5);
        /**
         *  center around Bastogne  all units have a hit there
         */
        for (ArrayList<Hex> arr:arrMoves){
            ArrayList<HexInt> arrSorted = AIUtil.countandSortCloseToAscending(hexBastogne1,arr);
            if (arrSorted.size() > 2) {
                arrSorted.get(0).hex.setAI(2);
                arrSorted.get(1).hex.setAI(1);
            }
        }
        hexBastogne1.setAI(100);
        hexBastogne2.setAI(100);

        for (Hex hex : hexBastogne1.getSurround()) {
            hex.setAI(50);
        }
        /**
         *  set up ai for hexes around targets
         */
        for (Unit unit:arrUnitsToBloack){
            for (Hex hex:unit.getHexOccupy().getSurround()){
                hex.setAI(4);
                hex.setAiScoreFaker(4);
            }

        }




    }

    /**
     *   Set AISCore andAIFaker for surroundin hexes on a road network occupied by enemy units
     * @param hexTarget
     * @param length  of road network from target
     * @param startScore start score at hex
     */

    private void setUpRoadNetworkAttack(Hex hexTarget, int length, int startScore) {
        Hex[][] arrArr =  hexTarget.getSurround(length);
        for (Hex[] arr:arrArr){
            int score = startScore;
            for (Hex hex:arr){
                if ((hex.isRoad() || hex.isPath()) && hex.isAxisOccupied()){
                    for (Hex hexSur:hex.getSurround()) {
                        hexSur.setAI(score);
                        hexSur.setAiScoreFaker(score);
                    }
                }
                score--;
            }
        }
    }

    public enum Strategy {
        Block, Supply, Attack
    }
}
