package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
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
    AISetScore(){
        instance = this;
    }
    public void scoreMove(){
        Hex.initAI();
        Hex.initAIFaker();
        if (NextPhase.instance.getTurn() < 4){
            doInitialTurns();
            return;
        }else{
            if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){

            }
        }

    }

    /**
     *  used for scenario 1 to detemine attack against bastogne or wilts
     * @param arrUnits
     * @param arrWork
     */
    public  void scoreMove(ArrayList<Unit> arrUnits, ArrayList<Hex>[] arrWork){
        this.arrUnits = arrUnits;
        arrMoves = arrWork;
        scoreMove();
    }
    public void scoreReinforcement(Hex hex) {
        Hex.initAI();
        Hex.initAIFaker();
        if (hexBastogneReinforceEntry == hex){
            setupBastogne();
            Hex.addNewAIScoreSurroundGerman();
            loadAIScoreFakerBastogne();
        }else{
            if (hexEttlebruckReinforceEntry == hex){
                setUpEttleBruck();
            }else{
                setUpMatrelange();
            }
        }
    }

    private void doInitialTurns() {
        loadAIScore(bestHexDefenseTurn1);
        loadAIScoreFakerInitial();
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
        setScore(hexBastogne1,6,Direction.All);
        setScore(hexBastogne2,6,Direction.All);
        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            setScore(hexWiltz,6,Direction.Right);
        }else{
            setScore(hexWiltz,4,Direction.Right);
        }
        setScore(hexMartelange,5,Direction.Right);
        setScore(hexEttlebruck,4,Direction.Left);
    }

    private  void loadAIScoreFakerBastogne(){
        /**
         * bastogne
         */
        setScore(hexBastogne1,8,Direction.All);
        setScore(hexBastogne2,8,Direction.All);
        /**
         *  double for ring around bastogne
         */
        for (int[] hexI:bastogneDefenseReinforcement){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAiScoreFaker(hex.getAiScoreFaker()*2); // multiply by 2
        }


    }
    /**
     *  set the aiscoreFaker starting at hex
     *  each branch makes score seem smaller
     * @param hexIn
     * @param i -score to start out
     */
    public static void setScore(Hex hexIn,int score, Direction direct) {
        int start = score;
        hexIn.setAiScoreFaker(start);
        start--;
        Hex[][] arrArr =  hexIn.getSurround(start);
        ArrayList<Hex> arrWork = hexIn.getSurround();
        for (Hex[] arr:arrArr){
            for (Hex hex:arr){
                if (hex.isRoad() || hex.isPath()){
                    if (direct == Direction.All) {
                        hex.setAiScoreFaker(start);
                    }else if (direct == Direction.Right && hexIn.xTable <= hex.xTable){
                        hex.setAiScoreFaker(start);
                    }else if (direct == Direction.Left && hexIn.xTable >= hex.xTable ){
                        hex.setAiScoreFaker(start);
                    }
                }
            }
            start--;
        }

    }
    public static void setScoreAI(Hex hexIn,int score, Direction direct) {
        int start = score;
        hexIn.setAiScoreFaker(start);
        start--;
        Hex[][] arrArr =  hexIn.getSurround(start);
        ArrayList<Hex> arrWork = hexIn.getSurround();
        for (Hex[] arr:arrArr){
            for (Hex hex:arr){
                if (hex.isRoad() || hex.isPath()){
                    if (direct == Direction.All) {
                        hex.setAI(start);
                    }else if (direct == Direction.Right && hexIn.xTable <= hex.xTable){
                        hex.setAI(start);
                    }else if (direct == Direction.Left && hexIn.xTable >= hex.xTable ){
                        hex.setAI(start);
                    }
                }
            }
            start--;
        }

    }



    public enum Direction {All, Left, Right, };
    private void setUpMatrelange() {
    }

    private void setUpEttleBruck() {

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
        setScoreAI(Hex.hexBastogne2, 13, Direction.All);
        /**
         *  set up score for around entry
         *
         */
        ArrayList<Hex> arrWork = hexBastogneReinforceEntry.getSurroundMapArr(hexBastogneReinforceEntry,3);
        for (Hex hex: arrWork){
            hex.setAI(2);
        }
    }

}
