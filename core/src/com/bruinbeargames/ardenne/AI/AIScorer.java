package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHandler;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitHex;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;

public class AIScorer {
    static public AIScorer instance;
    static public Hex Bastogne1 = Hex.hexTable[8][11];
    static public Hex Bastogne2 = Hex.hexTable[8][12];
    static public Hex Wiltz = Hex.hexTable[19][14];
    static public Hex MartleBurg = Hex.hexTable[9][23];
    static public Hex EttleBruck = Hex.hexTable[28][23];
    static public Hex HaimVille = Hex.hexTable[18][9];
    static public Hex Essendorf = Hex.hexTable[19][20];
    static public Hex Diekirch = Hex.hexTable[31][21];

    static int cntBastogne1 = 1000;
    static int cntBastogne2 = 1000;
    static int cntBastogneRing = 300;
    static int cntWiltz = 1000;
    static int cntMartleburg = 300;
    static int cntEttlebruk = 400;
    static int cntHainville = 300;
    static int cntEssendorf = 250;
    static int cntDiekirch = 350;
    int totalXGermans = 0;
    ArrayList<Unit> arrEnemy = new ArrayList<>();
    ArrayList<Hex> arrTarget = new ArrayList<>();
    boolean isBastogneFree;
    boolean isWiltzFree;
    AIScorer() {
        instance = this;
    }

    public int getScore(Type type, ArrayList<Unit> arrGermans, AIOrders aiO, int thread) {
        int score =0;
        switch (type){
            case GermanPenetration:
                score = getGermanPenetration(arrGermans,aiO, thread);
                break;
            case NonPenetrate:
                score = getNonPenetrateScore(aiO, thread);
            case GermanRegular:
                score = getGermanPenetration(arrGermans,aiO,thread);
                score += sumGermanInSupply(thread);
                for (Hex hex: aiO.arrHexMobileAssault){
                    if (hex != null){
                        int ix = aiO.arrHexMobileAssault.indexOf(hex);
                        if (hex != aiO.arrHexMoveTo.get(ix) ){
                            score -= 1000;
                        }
                    }
                }
                break;
            case ReinAndMoveOther:
                score = find2nDLehrThatCanExit(arrGermans,aiO,thread);
                for (Hex hex:aiO.arrHexMoveTo){
                        score += hex.aiScoreGen;
                }
                break;
            case ReinMartelange:
            case ReinBastogneAttack:
                score = getGermanPenetration(arrGermans,aiO,thread);
                score /=4; // divide by 4
                score += getAttackBastogne(aiO,thread);
                break;
            case ReinEttlebruck:
                score += getScoreEttleBruck(aiO, thread);
                break;
            case AttackWiltz:
            case AttackBastogne:
                score += getAttackBastogne(aiO,thread);
                break;
            case NewProcess:
                score +=newProcess(aiO,arrGermans,thread);
 //               score += accumulateGerman(aiO,arrGermans,thread);
                break;
            default:
                score = getGermanPenetration(arrGermans,aiO,thread);
                score /=4; // divide by 4
                score += getAttackBastogne(aiO,thread);
                break;

        }

        return score;
    }

    private int newProcess(AIOrders aiO, ArrayList<Unit> arrGermans, int thread) {
        if (NextPhase.instance.getTurn() < 4 && NextPhase.instance.getPhase() != Phase.ALLIED_REINFORCEMENT.ordinal()){
            return accumulateGerman(aiO,arrGermans,thread);
        }
        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
            int score =0;
            for (Hex hex:aiO.arrHexMoveTo){
                if(hex ==  Hex.hexTable[8][11]){
                    int b=0;
                }
                if(hex ==  Hex.hexTable[8][12]){
                    int b=0;
                }
                score +=hex.getAiScoreFaker();
            }
            return score;
        }
        return accumulateGerman(aiO,arrGermans,thread);

    }

    /**
     * part of rewrite for AI
     *
     * @param aiO        AIORders
     * @param arrGermans
     * @param thread
     * @return
     */
    private int accumulateGerman(AIOrders aiO, ArrayList<Unit> arrGermans, int thread) {
        int score = 0;
        ArrayList<Hex>[] arrHexGermanPaths = createGermanMoves(arrGermans,thread); // on thread
        for (ArrayList<Hex> arrWork:arrHexGermanPaths){
            for (Hex hex:arrWork){
                score -=hex.getAiScoreFaker();
            }
        }
        return score;
    }

    private int find2nDLehrThatCanExit(ArrayList<Unit> arrGermans, AIOrders aiO, int thread) {
        final int scoreForExit = -700;
        int score =0;
        ArrayList<Hex>[] arrArrGermans = createGermanMoves(arrGermans, thread);
        for (ArrayList<Hex> arr:arrArrGermans){
            if (arr.contains(SecondPanzerExits.hexExit2) ||
                    arr.contains(SecondPanzerExits.hexExit1)){
                score += scoreForExit;
            }
        }
        return score;
    }


    private int getNonPenetrateScore(AIOrders aiO, int thread) {
        int score =0;
        for (Hex hex: aiO.arrHexMoveTo){
            int ixHex = AIScenario1.instance.arrNonPenetration.indexOf(hex);
            if (ixHex > -1){
                int ixUnit =  aiO.arrHexMoveTo.indexOf(hex);
                int cntCity = AIScenario1.instance.nonPenetrationScore[ixHex];
                int defense = aiO.arrUnit.get(ixUnit).getCurrentDefenseFactor();
                score +=(cntCity * defense);
            }else{
                score +=1;
            }
        }
        return score;
    }

    private int getScoreEttleBruck(AIOrders aiO, int thread) {
        int score = 0;
        score = aiO.getScoreMain();
        if (aiO.arrHexMoveTo.contains(Wiltz)){
            score +=cntWiltz;
        }
        if (aiO.arrHexMoveTo.contains(Diekirch)){
            score +=cntDiekirch;
        }
        if (aiO.arrHexMoveTo.contains(HaimVille)){
            score +=cntHainville;
        }
        if (aiO.arrHexMoveTo.contains(Essendorf)){
            score +=cntEssendorf;
        }
        ArrayList<Hex> arrSurround = new ArrayList<>();
        arrSurround.addAll(Wiltz.getSurround());
        arrSurround.retainAll(aiO.arrHexMoveTo);
        if (arrSurround.size()> 0){
            score +=cntWiltz/3;
        }
        arrSurround.clear();
        arrSurround.addAll(EttleBruck.getSurround());
        arrSurround.retainAll(aiO.arrHexMoveTo);
        if (arrSurround.size()> 0){
            score +=cntEttlebruk/3;
        }
        arrSurround.clear();
        arrSurround.addAll(Diekirch.getSurround());
        arrSurround.retainAll(aiO.arrHexMoveTo);
        if (arrSurround.size()> 0){
            score +=cntDiekirch/3;
        }
        arrSurround.clear();
        arrSurround.addAll(HaimVille.getSurround());
        arrSurround.retainAll(aiO.arrHexMoveTo);
        if (arrSurround.size()> 0){
            score +=cntHainville/3;
        }
        arrSurround.clear();
        arrSurround.addAll(Essendorf.getSurround());
        arrSurround.retainAll(aiO.arrHexMoveTo);
        if (arrSurround.size()> 0){
            score +=cntEssendorf/3;
        }


        /**
         *  just in case we are actually able to get in
         */
        for (Hex hex:aiO.arrHexMoveTo){
            score += scoreEttleBruckCircle(aiO);
        }
        /**
         *  set flag
         */
        boolean isAllies;
        if (aiO.getArrUnit().get(0).isAllies){
            isAllies = true;
        }else{
            isAllies = false;
        }
        ArrayList<AttackScorer> arrScorer = new ArrayList<>();
        /**
         *  get hexes tha can be attacked
         */
        ArrayList<Hex> arrHexesToAttack = Combat.getAttackedHexesForAI(isAllies,aiO);
        if (arrHexesToAttack.size() == 0){
            return score;
        }
        /**
         *
         *  just look for highest attack for each hex at the moment
         *  todo find the highest combo
         *   Some not all units can be used for all attacks on the move
         *
         */
        float scoreAttack = 0;
        boolean isEttleBruck = true;
        for (Hex hex:arrHexesToAttack){

            AttackScorer attackScorer = scoreAttack(aiO,hex,isAllies,isEttleBruck);
            arrScorer.add(attackScorer);
            if (scoreAttack < attackScorer.attackScore){
                scoreAttack = attackScorer.attackScore;
            }
            scoreAttack = attackScorer.attackScore;
        }
        score += scoreAttack;
        return score;

    }

    private int scoreEttleBruckCircle(AIOrders aiO) {
        int score = 0;
        for (Hex hex: aiO.arrHexMoveTo){
            if (AIReinforcementScenario1.instance.arrEttlebruckTargets.contains(hex)){
                score +=10;
            }
        }
        return score;
    }


    /**
     *  score attacking on close to bastogne
     *  refernce this with AICombat calcs to make sure they work out
     * @param aiO
     * @param thread
     * @return
     */
    private int getAttackBastogne(AIOrders aiO, int thread) {
        int score = 0;
        score = aiO.getScoreMain();
        score += scoreCloseToBastogne(aiO);

        /**
         *  just in case we are actually able to get in
         */
        for (Hex hex:aiO.arrHexMoveTo){
            score += scoreBastogneCircle(aiO, hex);
        }
        /**
         *  set flag
         */
        boolean isAllies;
        if (aiO.getArrUnit().get(0).isAllies){
            isAllies = true;
        }else{
            isAllies = false;
        }
        ArrayList<AttackScorer> arrScorer = new ArrayList<>();
        /**
         *  get hexes tha can be attacked
         */
        ArrayList<Hex> arrHexesToAttack = Combat.getAttackedHexesForAI(isAllies,aiO);
        if (arrHexesToAttack.size() == 0){
            return score;
        }
        /**
         *
         *  just look for highest attack for each hex at the moment
         *  todo find the highest combo
         *   Some not all units can be used for all attacks on the move
         *
         */
        float scoreAttack = 0;
        for (Hex hex:arrHexesToAttack){
            AttackScorer attackScorer = scoreAttack(aiO,hex,isAllies, false);
            arrScorer.add(attackScorer);
            scoreAttack = 0;
            if (scoreAttack < attackScorer.attackScore){
                scoreAttack = attackScorer.attackScore;
            }
        }
        score += scoreAttack;
        return score;

    }

    private int scoreCloseToBastogne(AIOrders aiO) {
        int score = 0;
        for (Hex hex: aiO.arrHexMoveTo) {
            float len = HexHandler.shortestLine(AIReinforcementScenario1.instance.bastogne1, hex);
            score += 7 - len;
        }
        return score;
    }
    private int scoreCloseToWiltz(AIOrders aiO) {
        int score = 0;
        for (Hex hex: aiO.arrHexMoveTo) {
            float len = HexHandler.shortestLine(AIReinforcementScenario1.hexWiltz, hex);
            score += 7 - len;
        }
        return score;
    }
    private int getAttackWiltz(AIOrders aiO, int thread) {
        int score = 0;
        score = aiO.getScoreMain();
        score += scoreCloseToWiltz(aiO);

        /**
         *  just in case we are actually able to get in
         */
        ArrayList<Hex> arrWilt = AIReinforcementScenario1.hexWiltz.getSurround();
        for (Hex hex:aiO.arrHexMoveTo){
            if (arrWilt.contains(hex)) {
                score += 5;
            }
       }

        return score;

    }
    /**
     *  find all units that can attack a hex and create an attackscorer
     *  adjust for closenesst to bastogne
     * @param aiO
     * @param hex
     * @param isAllies
     * @return
     */
    private AttackScorer scoreAttack(AIOrders aiO,Hex hex, boolean isAllies, boolean isEttlebruck) {
        ArrayList<Unit> arrUnitsOnAttack = new ArrayList<>();
        ArrayList<Hex> arrSurround = hex.getSurround();
        for (int ix=0; ix < aiO.getArrUnit().size(); ix++){
            if (arrSurround.contains( aiO.getArrHexMoveTo().get(ix))){
                arrUnitsOnAttack.add(aiO.getArrUnit().get(ix));
            }
        }
        Attack attack = new Attack(hex, isAllies, false, true, null);
        for (Unit unit :arrUnitsOnAttack) {
            attack.addAttacker(unit, true);
        }
        float scoreTemp = attack.getActualOdds();

        attack.cancel();
        if (isEttlebruck){

        }else {
            scoreTemp *= adjustForCloseToBastogne(hex);
        }
        AttackScorer attackScorer = new AttackScorer(hex,arrUnitsOnAttack,scoreTemp);
        return attackScorer;
    }

    /**
     *
     * @param hex
     * @return
     */
    private float adjustForCloseToBastogne(Hex hex) {
        if (AIReinforcementScenario1.instance.arrBastogne.contains(hex)){
            return 5;
        }
        if (AIReinforcementScenario1.instance.arrBastogneRing.contains(hex)){
            return 3;
        }
        if (AIReinforcementScenario1.instance.arrEntryBastogne.contains(hex)){
            return 1;
        }
        float len = HexHandler.shortestLine(AIReinforcementScenario1.instance.bastogne1,hex);
        return 1/len;

    }

    public void setTarget(ArrayList<Hex> arrIn) {
        arrTarget.clear();
        arrTarget.addAll(arrIn);
    }

    class AttackScorer{
        Hex hex;
        ArrayList<Unit> arrUnits = new ArrayList<>();
        float attackScore;
        boolean isEttleBruck = false;
        AttackScorer(Hex hex,ArrayList<Unit> arrUnitsIn, float score){
            this.hex = hex;
            arrUnits.addAll(arrUnitsIn);
            attackScore = score;

        }
    }



    /**
     *
     * @param aiO
     * @param thread
     * @return
     */
    private int getGermanPenetrationBastogne(AIOrders aiO, int thread) {
        int score = 0;
        ArrayList<Hex>[] arrHexGermanPaths = createGermanMoves(arrEnemy,thread); // on thread
        ArrayList<String> arrString = new ArrayList<>();

        /**
         *  count hexes
         */
//        AIUtil.RemoveDuplicateHex(arrHex);
//        score += arrHex.size();
        /**
         *  score cities
         */
        for (ArrayList<Hex> arrHex:arrHexGermanPaths) {
            for (Hex hex : arrHex) {
                score += scoreBastogneCircle(aiO, hex);
            }
        }
        return score;

    }

    /**
     *
     * @param aiO
     * @param hex
     * @return
     */
    private int scoreBastogneCircle(AIOrders aiO, Hex hex) {
        int score = 0;
        ArrayList<String> arrString = new ArrayList();
        if (hex == Bastogne1) {
            score += cntBastogne1;
            arrString.add("Bastogne1");
        }
        if (hex == Bastogne2) {
            score += cntBastogne2;
            arrString.add("Bastogne2");

        }
        if (AIReinforcementScenario1.instance.arrBastogneRing.contains(hex)){
            score += cntBastogneRing;
            arrString.add("BastogneRing");

        }
        return score;
    }


    /**
     * Score German Penetration
     * @param arrGermans germans unit
     * @param aiO the AIorder
     * @param thread thread to use
     * @return
     */
    private int getGermanPenetration(ArrayList<Unit> arrGermans, AIOrders aiO, int thread){

        int score = 0;
        ArrayList<Hex>[] arrHexGermanPaths = createGermanMoves(arrGermans,thread); // on thread
        score +=sumGermanFarthestX(arrHexGermanPaths);

        /**
          *  count hexes
          */
//        AIUtil.RemoveDuplicateHex(arrHex);
//        score += arrHex.size();
        /**
         *  score cities
         */
        if (NextPhase.instance.getTurn() > 2) {
            for (ArrayList<Hex> arrHex : arrHexGermanPaths) {
                for (Hex hex : arrHex) {
                    score += scoreAllCities(aiO, hex);
                }
            }
        }
       score += getNonPenetrateScore(aiO,thread);
        return score;
    }
    private int scoreAllCities(AIOrders aiO, Hex hex){
        int score = 0;
        ArrayList<String> arrString = new ArrayList();
        if (hex == Bastogne1) {
            score += cntBastogne1;
            arrString.add("Bastogne1");
        }
        if (hex == Bastogne2) {
            score += cntBastogne2;
            arrString.add("Bastogne2");

        }
        if (hex == Wiltz) {
            score += cntWiltz;
            arrString.add("Wiltz");

        }
        if (hex == MartleBurg) {
            score += cntMartleburg;
            arrString.add("Martleburg");

        }
        if (hex == EttleBruck) {
            score += cntEttlebruk;
            arrString.add("EttleBruck");

        }
        aiO.updateScoreMessage(arrString);
        return score;
    }



    /**
     *  find farthest hex and add  to score after subtracting it from 40  which is end of the board
     * @param arrHexGermanPaths
     * @return
     */
    private int sumGermanFarthestX(ArrayList<Hex>[] arrHexGermanPaths) {
        int sumX = 0;
        for (ArrayList<Hex> arr:arrHexGermanPaths){
            int scoreSave = 0;
            for (Hex hex:arr){
                int tempScore = 40 - hex.xTable;
                if (tempScore > scoreSave){
                    scoreSave = tempScore;
                }
            }
            sumX += scoreSave    ;
        }
        return sumX       ;
    }

    /**
     *  get a count of hexes that can be supplied us same score type as sumGerManFarthest
     * @return
     */
    private int sumGermanInSupply(int thread){
        int sumX = 0;
        int[]sumForY = new int[Hex.yEnd];
        Unit unitTransport = Unit.getTransports(false).get(0);
        ArrayList<Hex> arrHexInSupply = Supply.instance.createHexChoice(unitTransport, thread,true);
        for (Hex hex:arrHexInSupply){
                int tempScore = 40 - hex.xTable;
                if (tempScore > sumForY[hex.yTable]){
                    sumForY[hex.yTable] = tempScore;
                }
        }
        for (int in:sumForY){
            sumX += in;
        }

        return sumX;
    }

    public void initialize(Type type) {
        if (type == Type.GermanPenetration){
            for (Unit unit :Unit.getOnBoardAxis()){
                totalXGermans += unit.getHexOccupy().xTable;
            }
        }
        arrEnemy.addAll(Unit.getOnBoardAxis());
        if (type == null){
            int b=0;
        }
        switch (type){
            case ReinAndMoveOther:
            case GermanPenetration:
            case GermanRegular:
                // do nothing
                break;
            case ReinBastogneAttack:
            case ReinMartelange:
                ArrayList<Unit> arrUnitRemove = getCantMakeTargetsBastogne();
                arrEnemy.removeAll(arrUnitRemove);
                setStrategyReinforcements();
                break;
        }


    }

    private void setStrategyReinforcements() {
        if (AIReinforcementScenario1.bastogne1.isAxisOccupied() &&
            AIReinforcementScenario1.bastogne2.isAxisOccupied()){
            isBastogneFree = false;
        }else{
            isBastogneFree = true;
        }
        if (AIReinforcementScenario1.hexWiltz.isAxisOccupied()){
            isWiltzFree = false;
        }else{
            isWiltzFree = true;
        }

    }

    /**
     *  get axis units that cant make it To Bastogne
     * @return
     */
    private ArrayList<Unit> getCantMakeTargetsBastogne() {
        ArrayList<Hex> arrHits = new ArrayList<>();
        ArrayList<Unit> arrGermans = Unit.getOnBoardAxis();
        arrHits.addAll(AIReinforcementScenario1.instance.arrBastogneOuterDefense);
        arrHits.addAll(AIReinforcementScenario1.instance.arrBastogneRing);
        arrHits.addAll(AIReinforcementScenario1.instance.arrBastogne);
        AIUtil.RemoveDuplicateHex(arrHits);
        ArrayList<Unit> arrLimitUnits = new ArrayList<>();
        ArrayList<Hex>[] arrHexGermanPaths = AIScorer.instance.createGermanMoves(arrGermans,0); // on thread
        int i=0;
        for (Unit unit:arrEnemy){
            boolean isUnitOk = false;
            for (Hex hex:arrHits){
                if (arrHexGermanPaths[i].contains(hex)){
                    isUnitOk = true;
                    break;
                }
            }
            if (!isUnitOk){
                arrLimitUnits.add(unit);
            }
        }
        return arrLimitUnits;

    }

    ArrayList<Hex>[] createGermanMoves(ArrayList<Unit> arrGermans, int thread) {
        ArrayList<Hex>[] arrArrHex = new ArrayList[arrGermans.size()];
        int i=0;
        for (Unit unit:arrGermans){

            //          Gdx.app.log("AIFaker", "creatGermanMove="+unit);
            if (unit.getCurrentMovement() > 7) {
                UnitMove unitMove = new UnitMove(unit, unit.getCurrentMovement(), true, true, thread);
                arrArrHex[i] = unitMove.getMovePossible(thread);
 /*           if (arrArrHex[i].contains(Wiltz) || arrArrHex[i].contains(Hex.hexTable[8][11])) {
                WinAIDisplay.instance.addSpecial(arrArrHex[i]);
                int bk=0;
            }*/

            }else{
                arrArrHex[i] = new ArrayList<Hex>();
            }
            i++;

        }
        return arrArrHex;
    }

    /**
     *  get score for AIPath
     *     targets must be set before getting here
     *     will accumulate score
     * @param type
     * @param thread
     * @param arrUnits
     * @param aiO
     * @return
     */
    public int getScore(Type type, int thread, ArrayList<UnitHex> arrUnits, AIOrders aiO) {
        int score =0;
        for (UnitHex uh : arrUnits) {
            UnitMove unitMove = new UnitMove(uh.unit, uh.unit.getCurrentMovement(), true, true, uh.hex, 1);
            for (Hex hex : arrTarget) {
                if (unitMove.getMovePossible(1).contains(hex)) {
                    /**
                     * add to number of units that can make it
                     */
                    if (!aiO.arrUnitsTemp.contains(uh.unit)){
                        aiO.arrUnitsTemp.add(uh.unit);
                    }
                    /**
                     * count number of enemy units to be eliminated
                     */
                    int cntElim=0;
                    for (Hex hex2: aiO.arrHexMoveTo){
                        if (hex2 == Hex.hexTable[0][0]) {
                            cntElim++;
                        }
                    }
                    /**
                     *  as example
                     *   if one unit eliminated releases 6 units better than
                     *   2 units eliminating 8 units.
                     */
                    if (cntElim > 0) {
                        score = aiO.arrUnitsTemp.size() / cntElim;
                    }else{
                        score = aiO.arrUnitsTemp.size();
                    }
                }
            }
        }


        return score;
    }



    public enum Type {GermanPenetration, NonPenetrate, GermanRegular,ReinBastogneAttack,ReinBastogneOcupy,GermanMoveScenario1,
        ReinEttlebruck, ReinMartelange, AIPath,Supply, AttackBastogne, AttackWiltz, ReinAndMoveOther,
        NewProcess;
    }


}
