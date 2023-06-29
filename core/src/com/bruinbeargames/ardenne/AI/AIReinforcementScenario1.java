package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitHex;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

public class AIReinforcementScenario1 implements Observer {
    static public AIReinforcementScenario1 instance;
    private I18NBundle i18NBundle;
    AIOrders aiBastogne; // orders developed for
    AIOrders aiMartelang;
    AIOrders aiEttleBruck;
    int reinBastogne = 0;
    int reinMartenlang = 0;
    int reinEttlebruk = 0;

    ArrayList<AIOrders> arrAiOrdersBastogne = new ArrayList<>();
    ArrayList<AIOrders> arrAiOrdersMarrtenLange = new ArrayList<>();
    ArrayList<AIOrders> arrAiOrdersEttleBruck= new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    ArrayList<Unit> arrReinforcementsThisTurn = new ArrayList<>();
    ArrayList<AIOrders> arrSupply = new ArrayList<>();
    boolean isAllies;
    AIScorer.Type type;
    Hex hexBastogneReinforceEntry = Hex.hexTable[0][19];
    Hex hexMartelangeReinforceEntry = Hex.hexTable[9][24];
    Hex hexEttlebruckReinforceEntry = Hex.hexTable[28][24];
    static public Hex bastogne1 = Hex.hexTable[8][11];
    static public Hex bastogne2 = Hex.hexTable[8][12];
    static public Hex hexMartelange = Hex.hexTable[9][23];
    static public Hex hexWiltz = Hex.hexTable[19][14];
    int[][] bastogneRing = {{7, 10}, {7, 11}, {7, 12}, {8, 10}, {9, 10}, {9, 11}, {9, 12}, {8, 13}};
    int[][] bastogneOuterDefense = {{9, 9}, {12, 11}, {11, 12}, {9, 14}
            , {8, 14}};
    int[][] entryBastongeRange = {{2, 18}, {6, 18}, {4, 14}, {8, 13}, {6, 15}};

    int[][] ettlebruckTargets = {{19,14}, {18, 9}, {19,20}, {31, 21}};
    int[][] artilleryPlace = {{7,14},{4,14},{5,14},{6,18}};

    public static ArrayList<Hex> arrBastogneRing = new ArrayList<>();
    public static ArrayList<Hex> arrBastogneOuterDefense = new ArrayList<>();
    public static ArrayList<Hex> arrBastogne = new ArrayList<>();
    public ArrayList<Hex> arrEttlebruckTargets = new ArrayList<>();
    ArrayList<Hex> arrEntryBastogne = new ArrayList<>();
    ArrayList<Hex> arrReinDestBastogneLimit = new ArrayList<>();
    ArrayList<Hex> arrBastogneReinforcementDestinationArtillery = new ArrayList<>();
    AIPath aiPath;
    ArrayList<Hex>[] arrMoves;
    AIFaker aiFaker;
    ArrayList<HexInt> arrStackCount = new ArrayList<>();
    BastogneWiltzDefenseStatus bastogneWiltzDefenseStatus;
    ArrayList<Hex> arrEnemySurround = new ArrayList<>();
    ArrayList<Unit> arrArtillery;


    AIReinforcementScenario1() {
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
        for (int[] in : bastogneRing) {
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrBastogneRing.add(hex);
        }
        for (int[] in : bastogneOuterDefense) {
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrBastogneOuterDefense.add(hex);
        }
        for (int[] in : ettlebruckTargets) {
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrEttlebruckTargets.add(hex);
        }
        for (int[] in : entryBastongeRange) {
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrEntryBastogne.add(hex);
        }
        arrBastogne.add(bastogne1);
        arrBastogne.add(bastogne2);
        arrReinDestBastogneLimit.addAll(arrBastogneRing);
        arrReinDestBastogneLimit.addAll(arrBastogneOuterDefense);
        arrReinDestBastogneLimit.addAll(arrBastogne);
        arrBastogneReinforcementDestinationArtillery.addAll(arrReinDestBastogneLimit);
        for (int[] in : artilleryPlace) {
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrBastogneReinforcementDestinationArtillery.add(hex);
        }


    }

    /**
     * Do all 3 REinforcement points
     * EttleBruck will then be looked at
     *
     * @param isAllies
     * @param arrSupplyIn
     */

    public boolean doReinforcementAllies(boolean isAllies, ArrayList<AIOrders> arrSupplyIn) {
        Gdx.app.log("AIReinforcementsScenario1", "Start");

        this.isAllies = isAllies;
        arrSupply.clear();
        arrSupply.addAll(arrSupplyIn);
        arrStackCount.clear();
        /**
         *  get reinforcements this turn
         *  will check against when getting reinforcements for specific areas
         */
        arrReinforcementsThisTurn = Reinforcement.instance.getReinforcementsAvailable(NextPhase.instance.getTurn());
        for (Unit unit:Unit.getOnBoardAxis()){
            arrEnemySurround.addAll(unit.getHexOccupy().getSurround());
        }
        AIUtil.RemoveDuplicateHex(arrEnemySurround);
        reinBastogne = 0;
        reinMartenlang = 0;
        reinEttlebruk = 0;
        if (NextPhase.instance.getTurn() > 3){
            aiMartelang = new AIOrders();
            doEttleBruck();
            return true;
        }
        doBastogne();
        return true;
    }
    public void doBastogne(){
        Gdx.app.log("AIReinforcementsScenario1", "doBastogne");
        aiBastogne = new AIOrders(); // create an empty in case

        /**
         *  so thet we dont have too may iterations
         *  filter out only what we need for bastogne
         */
        arrReinDestBastogneLimit.addAll(arrEnemySurround);
        arrReinDestBastogneLimit.add(hexWiltz);
        arrReinDestBastogneLimit.addAll(hexWiltz.getSurround());
        AIUtil.RemoveDuplicateHex(arrReinDestBastogneLimit);
        Gdx.app.log("AIReinforcementsScenario1", "destinations ="+ arrReinDestBastogneLimit.size());
        arrUnits.clear();
        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hexBastogneReinforceEntry));
//        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hexMartelangeReinforceEntry));
//        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hexEttlebruckReinforceEntry));
        Gdx.app.log("AIReinforcementsScenario1", "Bastogne units ="+arrUnits.size());
        // only artillery in bastogne reinforcements
        arrArtillery = new ArrayList<>();
        for (Unit unit:arrUnits){
            if (unit.isArtillery){
                arrArtillery.add(unit);
            }

        }
       arrUnits.removeAll(arrArtillery);

        /**
 *     generate the moves
 */
       ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();

        int i = 0;
        /**
         *  get all possible moves for Reinforcements
         */
        Gdx.app.log("AIReinforcementsScenario1", "Bastogne Getting Moves");

        ArrayList<Unit> arrCantMove = new ArrayList<>();
        for (Unit unit : arrUnits) {
            ArrayList<Hex> arrHexMove = findHexesOnReinforcement(unit,reinBastogne);
            reinBastogne++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
            arrHexMove.retainAll(Hex.arrAIHex);
            arrHexMove.retainAll(arrReinDestBastogneLimit);
            if (arrHexMove.size() > 0) {
              arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }
       }
        arrUnits.removeAll(arrCantMove);
       arrMoves = new ArrayList[arrWork.size()];
        i=0;
        for (ArrayList<Hex> arr:arrWork){
            arrMoves[i] = arr;
            i++;
        }
       if (arrUnits.size() == 0) { // cant get reinforcements  m
            Gdx.app.log("AIReinforcementsScenario1", "No Reinforcements");
            doMartelange();  // go to next
            return;
        }

        bastogneWiltzDefenseStatus = new BastogneWiltzDefenseStatus(arrUnits,arrMoves);
        Gdx.app.log("AIReinforcementsScenario1", "bastognewiltz strategy="+bastogneWiltzDefenseStatus.strategy);

        /**
         *  get AIOrders
         *  and remove duplicates
         *
         */

        ArrayList<AIOrders> arrStart = AIUtil.GetIterations(arrUnits, arrMoves);
        Gdx.app.log("AIReinforcementsScenario1", "Iterations count ="+arrStart.size());
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        if (bastogneWiltzDefenseStatus.strategy == StrategyBastogne.BastogneAttack){
            type = AIScorer.Type.ReinBastogneAttack;
        }else{
            type= AIScorer.Type.ReinBastogneOcupy;
        }
        arrAllowDuplicates.addAll(arrBastogneRing);
        arrAllowDuplicates.addAll(hexBastogneReinforceEntry.getSurround());
        AIUtil.RemoveDuplicateHex(arrAllowDuplicates);

        AIOrders aiEmpty  = new AIOrders();
        ArrayList<AIOrders> arrAIWork = AIOrders.removeDupeMoveToHexes(arrStart,arrAllowDuplicates);

        ArrayList<HexInt> arrHexStackCnt =  AIUtil.setStackCount(isAllies,arrAIWork, aiEmpty);
        arrAiOrdersBastogne = AIUtil.checkStaking(arrHexStackCnt, arrAIWork);

        for (AIOrders aiO:arrAiOrdersBastogne){
            aiO.clearMOA();
        }
        Gdx.app.log("AIReinforcementsScenario1", "after Dupe Check count ="+arrAiOrdersBastogne.size());
        /**
         *  AIFaker  wiil send message back when done
         */
        AIFaker.instance.addObserver(this);
        AIFaker.instance.startScoringOrders(arrAiOrdersBastogne, type, true);
        return;
    }
    public void doMartelange(){
        Gdx.app.log("AIReinforcementsScenario1", "doMartelange");
        aiMartelang = new AIOrders(); // create empty just in case
        arrUnits.clear();
        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hexMartelangeReinforceEntry));
        Gdx.app.log("AIReinforcementsScenario1", "Martelange units ="+arrUnits.size());
        /**
         *     generate the moves
         */
        /**
         *  get all possible moves for Reinforcements
         */
        Gdx.app.log("AIReinforcementsScenario1", "Martelange Getting Moves");
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();

        ArrayList<Unit> arrCantMove = new ArrayList<>();
        for (Unit unit : arrUnits) {
            ArrayList<Hex> arrHexMove = findHexesOnReinforcement(unit, reinMartenlang);
            reinMartenlang++;
            AIUtil.RemoveDuplicateHex(arrHexMove);

            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }

        }
        arrUnits.removeAll(arrCantMove); // will check after artillery
        arrMoves = new ArrayList[arrWork.size()];
        int i=0;
        for (ArrayList<Hex> arr:arrWork){
            arrMoves[i] = arr;
            i++;
        }

        if (arrUnits.size() == 0){
            aiMartelang = aiBastogne;
            doEttleBruck();
            return;
        }

 //       bastogneWiltzDefenseStatus = new BastogneWiltzDefenseStatus(arrUnits,arrMoves);
        Gdx.app.log("AIReinforcementsScenario1", "bastognewiltz strategy="+bastogneWiltzDefenseStatus.strategy);

        /**
         *  get AIOrders
         *  and remove duplicates
         *
         */
        ArrayList<AIOrders> arrStart = AIUtil.GetIterations(arrUnits, arrMoves);

        Gdx.app.log("AIReinforcementsScenario1", "Iterations count ="+arrStart.size());
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();

        type= AIScorer.Type.ReinMartelange;
        ArrayList<AIOrders> arrNoDupes = new ArrayList<>();
        arrAiOrdersMarrtenLange.clear();
        AIOrders aiArtllery = doArtillery(arrArtillery,aiBastogne);
        if (aiArtllery == null){
            aiArtllery = new AIOrders(); // initialize
        }
/**
 *  add artillery
 */
        AIOrders aiBastogneandArtillery = null;
        aiBastogneandArtillery =AIOrders.combine(aiBastogne,aiArtllery,false);

        /**
         *  set isTotal to false as same scorer is used
         */
        arrNoDupes = AIOrders.removeDupeMoveToHexes(arrStart,arrAllowDuplicates);
        /**
         *  do in tandem
         */
        ArrayList<HexInt> arrHexStackCnt =  AIUtil.setStackCount(isAllies,arrNoDupes, aiBastogne);

        ArrayList<AIOrders> arrNoStackViolations= AIUtil.checkStaking(arrHexStackCnt, arrNoDupes);
        for (AIOrders aio:arrNoStackViolations){
            arrAiOrdersMarrtenLange.add(AIOrders.combine(aiBastogneandArtillery,aio,false));
        }

        Gdx.app.log("AIReinforcementsScenario1", "after Dupe Check count ="+arrAiOrdersBastogne.size());
        /**
         *  AIFaker  wiil send message back when done
         */
        AIFaker.instance.addObserver(this);
        AIFaker.instance.startScoringOrders(arrAiOrdersMarrtenLange, type, true);
        return;

    }

    private AIOrders doArtillery(ArrayList<Unit> arrArtillery, AIOrders aiBastogne) {
        /**
         *     generate the moves
         */
        AIOrders aiArtillery = new AIOrders();
        ArrayList<Unit> arrCantMove = new ArrayList<>();
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();

        int i = 0;
        /**
         *  get all possible moves for Reinforcements
         */
        Gdx.app.log("AIReinforcementsScenario1", "doArtillery");

        for (Unit unit : arrArtillery) {
            unit.setArtilleryLimbered();
            ArrayList<Hex> arrHexMove = findHexesOnReinforcement(unit,reinBastogne);
            reinBastogne++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
//            arrHexMove.retainAll(Hex.arrAIHex);
//            arrHexMove.retainAll(arrBastogneReinforcementDestination);
            if (arrHexMove.size() > 6) { // blocked reinforcement
                arrHexMove.removeAll(arrEnemySurround);
            }
            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }
        }
        arrArtillery.removeAll(arrCantMove);
        arrMoves = new ArrayList[arrWork.size()];
        i=0;
        for (ArrayList<Hex> arr:arrWork){
            arrMoves[i] = arr;
            i++;
        }
        if (arrArtillery.size() == 0){
            return aiArtillery;
        }
        ArrayList<AIOrders> arrAIBasicMoveTo = new ArrayList<>();
        arrAIBasicMoveTo.addAll(AIUtil.GetIterations(arrArtillery,arrMoves));
        /**
         * for each iteration get bombard iteration
         */
        ArrayList<AIOrders> arrWorkAI = new ArrayList<>();
        ArrayList<Hex> arrNoCombatHex = new ArrayList<>();
        ArrayList<AIOrders> arrAIBasicBombard = new ArrayList<>();
        for (AIOrders aiO:arrAIBasicMoveTo){
            /**
             *  from getBestBombard in AILimber
             */
            for (int ix=0;ix< aiO.arrUnit.size();ix++){;
                ArrayList<Hex> arrHex = AIBarrageHandler.instance.getArrayOfHexesCanBombard(aiO.arrUnit.get(ix),aiO.arrHexMoveTo.get(ix));
                int score=0;
                if (arrHex != null){
                    for (Hex hex : arrHex) {
                        score += hex.getAttackPointsInHex();
                    }
                }
                aiO.setScoreBombard(score);
            }
        }
        /**
         * Sort into highest score order
         */

        /**
         * score based on defensive terrain
         */
        for (AIOrders ai: arrAIBasicMoveTo){
            int score = ai.getScoreBombard();
            if (score > 4) {
                for (Hex hex : ai.arrHexMoveTo) {
                    if (hex.isTown()) {
                        score += 5;
                    }
                    if (hex.isForest()) {
                        score += 5;
                    }
                    if (hex.isCity()) {
                        score += 8;
                    }
                }

                ai.setScoreBombard(score);
            }
        }
        i=0;
        for (AIOrders ai:arrAIBasicMoveTo) {
            for (i = 0; i < arrAIBasicBombard.size(); i++) {
                if (ai.scoreBombard > arrAIBasicMoveTo.get(i).scoreBombard){
                    break;
                }
            }
            arrAIBasicBombard.add(i,ai);
        }
        ArrayList<HexInt> arrHexStackCnt =  AIUtil.setStackCount(isAllies,arrAIBasicBombard, aiBastogne);

        ArrayList<AIOrders> arrNoStackViolations= AIUtil.checkStaking(arrHexStackCnt, arrAIBasicBombard);

        return arrNoStackViolations.get(0);
    }



    public void doEttleBruck(){
        Gdx.app.log("AIReinforcementsScenario1", "doEttleBruck");
        aiEttleBruck = new AIOrders();

        /**
         *  so thet we dont have too may iterations
         *  filter out only what we need for bastogne
         */
        arrReinDestBastogneLimit.clear(); // reuse limit
        for (Hex hex:arrEttlebruckTargets){
            arrReinDestBastogneLimit.addAll(hex.getSurround());
        }
        arrReinDestBastogneLimit.addAll(arrEttlebruckTargets);
        arrReinDestBastogneLimit.addAll(Supply.instance.getGermanBottlenecks());
        arrReinDestBastogneLimit.addAll(hexWiltz.getSurround());
        AIUtil.RemoveDuplicateHex(arrReinDestBastogneLimit);
        Gdx.app.log("AIReinforcementsScenario1", "ettlebruck destinations ="+ arrReinDestBastogneLimit.size());
        arrUnits.clear();
        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hexEttlebruckReinforceEntry));
        arrUnits.retainAll(arrReinforcementsThisTurn); // dont want all

        Gdx.app.log("AIReinforcementsScenario1", "EttleBruck units ="+arrUnits.size());

        /**
         *     generate the moves
         */
        int i = 0;
        /**
         *  get all possible moves for Reinforcements
         */
        Gdx.app.log("AIReinforcementsScenario1", "Bastogne Getting Moves");
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();

        ArrayList<Unit> arrCantMove = new ArrayList<>();
        for (Unit unit : arrUnits) {
            ArrayList<Hex> arrHexMove = findHexesOnReinforcement(unit,reinEttlebruk);
            reinEttlebruk++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
            //arrHexMove.retainAll(Hex.arrAIHex);
            arrHexMove.retainAll(arrReinDestBastogneLimit);
            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }

        }
        arrUnits.removeAll(arrCantMove);
        arrMoves = new ArrayList[arrWork.size()];
        i=0;
        for (ArrayList<Hex> arr:arrWork){
            arrMoves[i] = arr;
            i++;
        }
        if (arrUnits.size() == 0) { // cant get reinforcements  m
            Gdx.app.log("AIReinforcementsScenario1", "No Reinforcements");
            aiEttleBruck = aiMartelang;
            execute();
            return;
        }

        /**
         *  get AIOrders
         *  and remove duplicates
         *
         */

        ArrayList<AIOrders> arrStart = AIUtil.GetIterations(arrUnits, arrMoves);
        Gdx.app.log("AIReinforcementsScenario1", "Iterations count ="+arrStart.size());
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        arrAllowDuplicates.addAll(arrBastogneRing);

        type = AIScorer.Type.ReinEttlebruck;
        arrAiOrdersEttleBruck.clear();
        ArrayList<AIOrders> arrNoDupes= AIOrders.removeDupeMoveToHexes(arrStart,arrAllowDuplicates);
        /**
         *  add up score
         */
        for (AIOrders aio:arrNoDupes){
            arrAiOrdersEttleBruck.add(AIOrders.combine(aiMartelang,aio,true));
        }
        Gdx.app.log("AIReinforcementsScenario1", "after Dupe Check count ="+arrAiOrdersBastogne.size());
        /**
         *  AIFaker  wiil send message back when done
         */
        AIFaker.instance.addObserver(this);
        AIFaker.instance.startScoringOrders(arrAiOrdersEttleBruck, type, true);
        return;
    }



    /**
     * copied from WinReinforcement
     *
     * @param unit
     * @return array of hexes that the unit can be placed on
     * <p>
     * add stacking restriction if entry place blocked
     */
    public ArrayList<Hex> findHexesOnReinforcement(Unit unit, int entryCost) {
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
        if (hex.canOccupy(unit)) {
            UnitMove unitMove = new UnitMove(unit, unit.getCurrentMovement() -(1 +entryCost), false, true, hex, 0);
            arrHexReturn.addAll(unitMove.getMovePossible());
            arrHexReturn.add(hex);
            return arrHexReturn;
        }
        /**
         *  entry place blocked
         */
        ArrayList<Hex> arrSurr = hex.getSurround();
        for (Hex hex2 : arrSurr) {
            if (hex2.canOccupy(unit)) {
 //               if (stackFake(unit, hex2)) {
                    arrHexReturn.add(hex2);
 //                   break;    // dont take up stacking for all
                }
 //           }
        }
        return arrHexReturn;
    }

    /**
     * check if we can stack here, checking the reinforcements
     *
     * @param unit
     * @param hex
     * @return
     */
    private boolean stackFake(Unit unit, Hex hex) {
        HexInt hexInt = null;
        for (HexInt hi : arrStackCount) {
            if (hi.hex == hex) {
                hexInt = hi;
                break;
            }
        }
        int cntStack = 0;
        if (hexInt != null) {
            cntStack += hexInt.count;
        } else {
            hexInt = new HexInt(hex, 0);
            arrStackCount.add(hexInt);
        }

        if ((unit.getCurrentStep() + cntStack) <= hex.stackMax) {
            hexInt.count += unit.getCurrentStep();
            return true;
        }

        return false;
    }


    /**
     * PROCESSING AFTER    SCORING FOR bASTOGNE
     */
    public void bastogneDone() {
        Gdx.app.log("AIReinforcementScenario1", "bastogneDone");
        int i = 0;
        Collections.sort(arrAiOrdersBastogne, new AIOrders.SortbyScoreDescending());

        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrAiOrdersBastogne, .1f);
//        ArrayList<AIOrders> attTopDefense = AIScenario1.instance.bestDefense(arrTopTen);
        Gdx.app.log("AIReinforecementScenario1", "Top Ten");
//        AIOrders.display(arrIn);
        aiBastogne = arrTop.get(0); // get top for now
        doMartelange();
//        AIExecute.instance.Reinforcement(aiBastogne);
        Gdx.app.log("AIReinforcementScenario1", "doEttleBruck");
        return;
    }

    /**
     * PROCESSING AFTER    SCORING FOR bASTOGNE
     */
    public void martelangeDone() {
        Gdx.app.log("AIReinforcementScenario1", "martelangeDone");
        int i = 0;
        Collections.sort(arrAiOrdersMarrtenLange, new AIOrders.SortbyScoreDescending());

        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrAiOrdersMarrtenLange, .1f);
//        ArrayList<AIOrders> attTopDefense = AIScenario1.instance.bestDefense(arrTopTen);
        Gdx.app.log("AIReinforecementScenario1", "Top Ten");
//        AIOrders.display(arrIn);
        aiMartelang = arrTop.get(0); // get top for now
        doEttleBruck();
//        AIExecute.instance.Reinforcement(aiMartelang);
        Gdx.app.log("AIReinforcementScenario1", "doEttleBruck");
        return;
    }
    public void ettlebruckDone() {
        Gdx.app.log("AIReinforcementScenario1", "ettlebruckDone");
        int i = 0;
        Collections.sort(arrAiOrdersEttleBruck, new AIOrders.SortbyScoreDescending());

        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrAiOrdersEttleBruck, .1f);
//        ArrayList<AIOrders> attTopDefense = AIScenario1.instance.bestDefense(arrTopTen);
        Gdx.app.log("AIReinforecementScenario1", "Top Ten");
//        AIOrders.display(arrIn);
        aiEttleBruck = arrTop.get(0); // get top for now
//        doEttleBruck();
        execute();
        return;
    }
    public void execute(){
        AIExecute.instance.Reinforcement(aiEttleBruck);
        Gdx.app.log("AIReinforcementScenario1", "doEttleBruck");
    }

    /**
     * input from Update
     */
    public void doNext() {
        switch (type) {
            case ReinBastogneAttack:
            case ReinBastogneOcupy:
                AIReinforcementScenario1.instance.bastogneDone();
                break;
            case ReinMartelange:
                AIReinforcementScenario1.instance.martelangeDone();
                break;
            case ReinEttlebruck:
                AIReinforcementScenario1.instance.ettlebruckDone();
                break;

        }
    }


    public void setArrToBeScored(ArrayList<AIOrders> arrIn, AIScorer.Type type) {
        Gdx.app.log("AIReinforcements", "setArrToBeScored");
        ArrayList<AIOrders> arrToBeScored = new ArrayList<>();
        arrToBeScored.clear();
        arrToBeScored.addAll(arrIn);
        ArrayList<Unit> arrUnitFake = new ArrayList<>();
        arrUnitFake.addAll((Unit.getOnBoardAxis()));
        AIFaker.instance.setUnits(arrUnitFake);
        AIFaker.instance.startScoringOrders(arrToBeScored, type, isAllies);
    }


    /**
     * determine if we can create an MOA to clear out path to Bastogne
     *
     * @return
     */
    private boolean sendMOA() {
        /**
         *  is there an AI Order with only 1 unit to destroy
         *   The AIOrders has a zero hex where german unit is eliminated and
         *   the target can be reached by others
         */
        Hex hexToMOA = null;
        ArrayList<UnitHex> arrSolution = new ArrayList<>();
        for (AIOrders ai : aiPath.arrPathOrders) {
            int cntZeroHex = 0;
            int ix = 0;
            for (Hex hex : ai.arrHexMoveTo) {
                if (hex == Hex.hexTable[0][0]) {
                    cntZeroHex++;
                    if (cntZeroHex == 1) {
                        hexToMOA = hex;
                    }
                }
            }
            if (cntZeroHex == 1) {

            } else {
                hexToMOA = null;
            }
        }
        if (hexToMOA == null) {
            return false;
        }
        int defenseFactor = hexToMOA.getDefensePointsInHex();
        /**
         *  need at least a 3 to 1 to make this work
         */
        defenseFactor *= 3;
        /**
         *  get all units that can get there
         */
 //       AIMobileAssault.createArrays(arrUnits, arrStart);
        for (AIMobileAssault aIM : AIMobileAssault.getAssualt()) {
            if (aIM.isOK()) {

            }
        }
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.BastogneReinScored) {
            AIFaker.instance.deleteObserver(this);
            doNext();
        }

    }

    public static class BastogneWiltzDefenseStatus {
        boolean isBastogneAllOccupied;
        boolean isBastognePartOccupied;
        boolean isWiltzOccupied;

        ArrayList<Unit> arrCanReachWiltz = new ArrayList<>();
        boolean canReachBastogne1;
        boolean canReachBastogne2;
        int numBastogneRingOccupied = 0;
        int numRingWiltzOccupied = 0;
        int numBastogneDefenseOccupied = 0;
        int numAttackBastogne = 0;
        int numAttackWiltz = 0;

        public StrategyBastogne strategy;

        BastogneWiltzDefenseStatus(ArrayList<Unit> arrUnits, ArrayList<Hex>[] arrMoves) {
            if (bastogne1.isAxisOccupied[0] && bastogne2.isAxisOccupied[0]) {
                isBastogneAllOccupied = true;
            } else if (bastogne1.isAxisOccupied[0] || bastogne2.isAxisOccupied[0]) {
                isBastognePartOccupied = true;
            }
            for (Hex hex : arrBastogneRing) {
                if (hex.isAxisOccupied()) {
                    numBastogneRingOccupied++;
                }
            }
            for (Hex hex : arrBastogneOuterDefense) {
                if (hex.isAxisOccupied()) {
                    numBastogneDefenseOccupied++;
                }
            }
            if (hexWiltz.isAxisOccupied()) {
                isWiltzOccupied = true;
            }
            for (Hex hex : hexWiltz.getSurround()) {
                if (hex.isAxisOccupied()) {
                    numRingWiltzOccupied++;
                }
            }
            int ix = 0;
            for (ArrayList arr : arrMoves) {
                if (arr.contains(hexWiltz)) {
                    Unit unit = arrUnits.get(ix);
                    arrCanReachWiltz.add(unit);
                }
                if (arr.contains(bastogne1)) {
                    canReachBastogne1 = true;
                }
                if (arr.contains(bastogne2)) {
                    canReachBastogne2 = true;
                }
                for (Hex hex:hexWiltz.getSurround()){
                    if (arr.contains(hex)){
                        numAttackWiltz++;
                        break;
                    }
                }
                for (Hex hex:arrBastogneRing){
                    if (arr.contains(hex)){
                        numAttackBastogne++;
                        break;
                    }
                }

                ix++;
            }
            /**
             * if can reach bastogne
             */
            if (canReachBastogne1 && canReachBastogne2) {
                if (numBastogneRingOccupied == 0 && numBastogneDefenseOccupied == 0) {
                    strategy = StrategyBastogne.BastogneFree;
                    return;
                }
            }
            if (arrCanReachWiltz.size() > 0 && !isWiltzOccupied && !Hex.hexWiltz.isAlliedOccupied()){
                strategy = StrategyBastogne.WiltzFree;
                return;
            }
            if (numAttackWiltz > numAttackBastogne && !Hex.hexWiltz.isAlliedOccupied()){
                strategy = StrategyBastogne.WiltzAttack;
                return;
            }
            strategy = StrategyBastogne.BastogneAttack;
            return;
        }
    }

    public enum StrategyBastogne {
        BastogneFree, BastognePart, BastogneAttack, WiltzFree,WiltzAttack
    }
}






