package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.Hex.HexUnits;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Comparator;

public class AIOrders {
    ArrayList<Unit> arrUnit = new ArrayList<>(); // unit
    ArrayList<Hex> arrHexMoveTo = new ArrayList<>(); //move to
    ArrayList<Hex> arrHexShootAt = new ArrayList<>(); //Shoot at
    ArrayList<Hex> arrUnitPosition = new ArrayList<>(); // position before move
    ArrayList<String> arrScoreMessage = new ArrayList<>();
    /**
     *  if MOA hex same as move to then no MOA
     *   if diff then need to attack MOA before moving to move to
     */
    ArrayList<Hex> arrHexMobileAssault = new ArrayList<>();
    int scoreBombard =0;
    int scoreMain = 0;
    ArrayList<Unit> arrUnitsTemp = new ArrayList<>();
    Type type;

    /**
     * Create an AIOrder
     * @param type type to create
     * @param arrHexIn Hex's unit can move to or bombard depending on type
     * @param arrUnitsIn Units 1:1 with hexes with move to or shoot at
     * @param arrunitPosition units starting position NOT USED
     */
    public AIOrders(Type type,ArrayList<Hex> arrHexIn, ArrayList<Unit> arrUnitsIn,ArrayList<Hex> arrunitPosition){
        this.type = type;
        if (type == Type.Bombard) {
            arrUnit.addAll(arrUnitsIn);
            arrHexShootAt.addAll(arrHexIn);
        }else if (type == Type.MoveTo) {
            arrUnit.addAll(arrUnitsIn);
            arrHexMoveTo.addAll(arrHexIn);
            arrHexMobileAssault.addAll(arrHexIn);

        }else if (type == Type.SupplyBlocks) {
            arrHexMoveTo.addAll(arrHexIn);
        }
        arrUnitPosition.addAll(arrUnitPosition);

    }

    public AIOrders() {

    }
    public AIOrders(Type type,Unit unit, Hex hexDestination, Hex hexPosition){
        this.type = type;
        if (type == Type.Bombard) {
            arrUnit.add(unit);
            arrHexShootAt.add(hexDestination);
        }else if (type == Type.MoveTo) {
            arrUnit.add(unit);
            arrHexMoveTo.add(hexDestination);
            arrHexMobileAssault.add(hexPosition);
        }
        arrUnitPosition.addAll(arrUnitPosition);
    }

    /**
     * Remove AIOrders that move to same hex
     * @param arrIn Input
     * @param arrAllowDuplicates those hexes we wont check
     * @return AIOrders with no duplicates
     */
    public static ArrayList<AIOrders> removeDupeMoveToHexes(ArrayList<AIOrders> arrIn, ArrayList<Hex> arrAllowDuplicates) {
        ArrayList<AIOrders> arrReturn = new ArrayList<AIOrders>();
        for (AIOrders aiO:arrIn){
            ArrayList<Hex> arrHexWork = new ArrayList<>();
            boolean isGood = true;
            for (Hex hex:aiO.arrHexMoveTo){
                if (arrHexWork.contains(hex) && !arrAllowDuplicates.contains(hex)){
                    isGood = false;
                    break;
                }else{
                    arrHexWork.add(hex);
                }
            }
            if (isGood){
                arrReturn.add(aiO);
            }
        }
        return arrReturn;
    }

    /**
     *  same as abbobe but allow n dupes
     * @param arrIn
     * @param arrAllowDuplicates
     * @param allow
     * @return
     */
    public static ArrayList<AIOrders> removeDupeMoveToHexesAllow(ArrayList<AIOrders> arrIn, ArrayList<Hex> arrAllowDuplicates, int allow) {
        ArrayList<AIOrders> arrReturn = new ArrayList<AIOrders>();
        for (AIOrders aiO:arrIn){
            ArrayList<Hex> arrHexWork = new ArrayList<>();
            boolean isGood = true;
            int dupes=0;
            for (Hex hex:aiO.arrHexMoveTo){
                if (arrHexWork.contains(hex) && !arrAllowDuplicates.contains(hex)){
                    dupes++;
                    if (dupes > allow) {
                        isGood = false;
                        break;
                    }
                }else{
                    arrHexWork.add(hex);
                }
            }
            if (isGood){
                arrReturn.add(aiO);
            }
        }
        return arrReturn;
    }

    public static AIOrders combine(AIOrders aiO1, AIOrders aiO2, boolean isTotal) {
        AIOrders aiOrders =  new AIOrders();
        aiOrders.arrUnit.addAll(aiO1.arrUnit);
        aiOrders.arrUnit.addAll(aiO2.arrUnit);
        aiOrders.arrHexMoveTo.addAll(aiO1.arrHexMoveTo);
        aiOrders.arrHexMoveTo.addAll(aiO2.arrHexMoveTo);
        aiOrders.arrHexMobileAssault.addAll(aiO1.arrHexMobileAssault);
        aiOrders.arrHexMobileAssault.addAll(aiO2.arrHexMobileAssault);
        aiOrders.arrHexShootAt.addAll(aiO1.arrHexShootAt);
        aiOrders.arrHexShootAt.addAll(aiO2.arrHexShootAt);
        aiOrders.arrUnitPosition.addAll(aiO1.arrUnitPosition);
        aiOrders.arrUnitPosition.addAll(aiO2.arrUnitPosition);
        aiOrders.arrScoreMessage.addAll(aiO1.arrScoreMessage);
        aiOrders.arrScoreMessage.addAll(aiO2.arrScoreMessage);
        if (isTotal){
            aiOrders.scoreBombard = aiO1.scoreBombard + aiO2.scoreBombard;
            aiOrders.scoreMain = aiO1.scoreMain + aiO2.scoreMain;
        }else{
            aiOrders.scoreMain = 0;
            aiOrders.scoreBombard = 0;
        }
        aiOrders.scoreBombard = 0;
        return aiOrders;
    }
    public static boolean checkStack(AIOrders aiO, Hex hex, ArrayList<Hex> arrAllowDuplicates){
        if (!arrAllowDuplicates.contains(hex)){
            return false;
        }
        int cntStack = 0;
        int ix=0;
        for (Unit unit:aiO.arrUnit){
            if (hex == aiO.arrHexMoveTo.get(ix)){
                cntStack += unit.getCurrentStep();
            }
        }
        if (cntStack > Hex.stackMax){
            return false;
        }
        return true;
    }

    /**
     * remove iterations that are the same i.e.  attackx and attack y same as
     * attack y and attack x
     * @param arrAIStart
     * @return
     */
    public static ArrayList<AIOrders> removeSameAttackHexes(ArrayList<AIOrders> arrAIStart) {
        return null;
    }

    /**
     *   Skim of the top AIOrders on based on percentage wanted
     * @param arrSortedIn
     * @param percent
     * @return
     */
    public static ArrayList<AIOrders> gettopPercent(ArrayList<AIOrders> arrSortedIn, float percent) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        if (arrSortedIn.size() == 0){
            return arrReturn;
        }
        if (arrSortedIn.size() == 1){
            arrReturn.add(arrSortedIn.get(0));
            return arrReturn;
        }
        int cntToGet = (int) (arrSortedIn.size() * percent);
        if (cntToGet < 1){
            cntToGet = 1;
        }
        int i =0;
        for (AIOrders aiO:arrSortedIn){
            arrReturn.add(aiO);
            i++;
            if (i > cntToGet){
                break;
            }
        }
        return arrReturn;

    }

    /**
     * Get the top number of aiorders
     * @param arrSortedIn
     * @param cntIn
     * @return
     */
    public static ArrayList<AIOrders> gettopNumber(ArrayList<AIOrders> arrSortedIn, int cntIn) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        if (arrSortedIn.size() == 0){
            return arrReturn;
        }
        if (arrSortedIn.size() == 1){
            arrReturn.add(arrSortedIn.get(0));
            return arrReturn;
        }
        int cntToGet = cntIn;
        if (cntToGet < 1){
            cntToGet = 1;
        }
        int i =0;
        for (AIOrders aiO:arrSortedIn){
            arrReturn.add(aiO);
            i++;
            if (i == cntToGet){
                break;
            }
        }
        return arrReturn;

    }

    public static void display(ArrayList<AIOrders> arrIn) {
        Gdx.app.log("AIOrders", "Display Start");
        for (AIOrders ao:arrIn){
            Gdx.app.log("AIOrders", "Score          ="+ao.scoreMain);
            Gdx.app.log("AIOrders", "Display Units  ="+ao.arrUnit);
            Gdx.app.log("AIOrders", "Display Move to="+ao.arrHexMoveTo);


        }
        Gdx.app.log("AIOrders", "Display End");

    }

    /**
     * Merge 2 AIORDERS
     * @param arrAI1
     * @param arrAI2 merge into this one
     */
    public static void mergeInto(ArrayList<AIOrders> arrAI1, ArrayList<AIOrders> arrAI2) {

        ArrayList<AIOrders> arrMerged = new ArrayList<>();
        for (AIOrders aiO1:arrAI1){
            for (AIOrders aiO2:arrAI2){
                arrMerged.add(combine(aiO1,aiO2,true));
            }
        }
        arrAI2.clear();
        arrAI2.addAll(arrMerged);
    }

    /**
     *  remove any orders that will cause overstacking
     *  check units on order as well units not in order bu on the hex
     * @param arrCheck the arraylist of orders to check
     * @return Array of orders that are safe as per stacking.
     */
    public static ArrayList<AIOrders> removeOverstack(ArrayList<AIOrders> arrCheck) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        for (AIOrders aiO:arrCheck){
            HexUnits.init();
            int ix =0;
            boolean isOK = true;
            for (Hex hex: aiO.arrHexMoveTo){
                HexUnits.add(hex,aiO.arrUnit.get(ix));
                ix++;
                for (Unit unit:hex.getUnitsInHex()){
                    if (!aiO.arrUnit.contains(unit)){
                        HexUnits.add(hex,unit);
                    }
                }
                for (HexUnits hU:HexUnits.arrHexUnits){
                    if (hU.getArrUnits().size() > 2)
                    {
                        int b=0;
                    }
                    int stack = 0;
                    for (Unit unit:hU.getArrUnits()){
                        stack +=unit.getCurrentStep();
                    }
                    if (stack > Hex.stackMax){
                        isOK = false;
                        break;
                    }
                }
                if (!isOK){
                    break;
                }

            }
            if (isOK){
                arrReturn.add(aiO);
            }
        }
        return arrReturn;
    }
    public static void removeOverStackUnits(AIOrders aiOrders){
        int ix =0;
        boolean isOK = true;
        ArrayList<Unit> arrRemove = new ArrayList<>();
        for (Hex hex: aiOrders.arrHexMoveTo){
            HexUnits.add(hex,aiOrders.arrUnit.get(ix));
            for (Unit unit:hex.getUnitsInHex()){
                if (!aiOrders.arrUnit.contains(unit)){
                    HexUnits.add(hex,unit);
                }
            }
            for (HexUnits hU:HexUnits.arrHexUnits){
                if (hU.getArrUnits().size() > 2)
                {
                    int b=0; // debugging
                }
                int stack = 0;
                for (Unit unit:hU.getArrUnits()){
                    stack +=unit.getCurrentStep();
                }
                if (stack > Hex.stackMax){
                    arrRemove.add(aiOrders.arrUnit.get(ix));
                    break;
                }
            }
            ix++;
        }
        aiOrders.remove(arrRemove);
        return;
    }



    /**
     *  remove orders that place units ontop of enemy
     * @param arrIN
     * @param isAllies
     * @return
     */
    public static ArrayList<AIOrders> removeEnemyPlace(ArrayList<AIOrders> arrIN, boolean isAllies) {
            ArrayList<AIOrders> arrReturn = new ArrayList<>();
            ArrayList<Hex> arrHexEnemy = new ArrayList<>();
            if (isAllies){
                for (Unit unit:Unit.getOnBoardAxis()){
                    arrHexEnemy.add(unit.getHexOccupy());
                }
            }
            for (AIOrders aio:arrIN){
                boolean isGood = true;
                for (Hex hex:aio.arrHexMoveTo){
                    if (arrHexEnemy.contains(hex)){
                        isGood = false;
                        break;
                    }
                }
                if (isGood){
                    arrReturn.add(aio);
                }
            }
            return arrReturn;
    }

    public void setScoreBombard(int score){
        scoreBombard = score;
    }

    public int getScoreBombard() {
        return scoreBombard;
    }

    public void setScoreMain(int scoreMain) {
        this.scoreMain = scoreMain;
    }

    public int getScoreMain() {
        return scoreMain;
    }

    public ArrayList<Unit> getArrUnit() {
        return arrUnit;
    }

    public ArrayList<Hex> getArrHexMoveTo() {
        return arrHexMoveTo;
    }
    public ArrayList<Hex> getArrHexShootAt() {
        return arrHexShootAt;
    }

    public void createTargetShooterSave(int countAirPoints) {
        HexUnits.init();
        int ix=0;
        for (ix=0; ix< arrHexShootAt.size(); ix++){
             Hex hex = arrHexShootAt.get(ix);
             HexUnits.add(hex,arrUnit.get(ix));
        }
        for (HexUnits hu:HexUnits.arrHexUnits) {
            Hex hex = hu.getHex();
            Barrage.instance.createTargetShooterSave(hex,hu.getArrUnits(), 0, true);
        }
        if (countAirPoints > 0) {
            ArrayList<Unit> arrAir = new ArrayList<>();
            arrAir.add(Unit.unitAirplane);
            ArrayList<HexInt> arrAircraftAttackHex = AIBarrageHandler.instance.getBestAirBombard(countAirPoints);
            for (HexInt hi:arrAircraftAttackHex) {
                Hex hex = hi.hex;
                Barrage.instance.createTargetShooterSave(hex,arrAir, hi.count, true);
            }

        }


    }

    /**
     *  combine move parts of passed AIOrder into the AIOrder
     * @param aiOrdersCombine
     */
    public void combineMove(AIOrders aiOrdersCombine) {
        /**
         *  sanity clause
         */
        for (Unit unit:aiOrdersCombine.arrUnit){
            if (arrUnit.contains(unit)){
                Gdx.app.log("AIOrder", "ERROR ERROR Combining AiOrder with same unit:"+unit);
                float a = 3/0;
            }
            arrUnit.add(unit);
            int ix = aiOrdersCombine.arrUnit.indexOf(unit);
            arrHexMoveTo.add(aiOrdersCombine.arrHexMoveTo.get(ix));
            if (aiOrdersCombine.arrHexMobileAssault.size() > ix) {
                arrHexMobileAssault.add(aiOrdersCombine.arrHexMobileAssault.get(ix));
            }else{
                arrHexMobileAssault.add(aiOrdersCombine.arrHexMoveTo.get(ix));
            }
        }
    }

    public void addToScoreMain(int score) {
        scoreMain += score;
    }

    public void updateScoreMessage(ArrayList<String> arrString) {
        arrScoreMessage.addAll(arrString);
    }

    public void display() {
        Gdx.app.log("AIOrders", "Score          ="+scoreMain);
        Gdx.app.log("AIOrders", "Display Units  ="+arrUnit);
        Gdx.app.log("AIOrders", "Display Move to="+arrHexMoveTo);

    }

    /**
     *  Replace the Move To Hex with where MOA happens and replace it with MOA
     *   move MOA into MOA
     * @param hexCheck
     * @param hexMOA
     */
    public void addMOA(Unit unitIn, Hex hexCheck, Hex hexMOA) {
        int ix=0;
        for (Unit unit:arrUnit){
            if (unitIn == unit){
                // not a match;
                if (hexCheck == arrHexMoveTo.get(ix)){
                    arrHexMoveTo.set(ix,hexMOA);
                    arrHexMobileAssault.set(ix,hexCheck);
                }
                return;
            }
            ix++;
        }
    }

    public void sortMOAToTop() {
    }

    public void clearMOA() {
        for (int i=0; i< arrHexMobileAssault.size(); i++){;
            arrHexMobileAssault.set(i,null);
        }
    }

    /**
     *  remove units  from orders
     * @param arrREmove
     */
    public void remove(ArrayList<Unit> arrREmove) {
        for (Unit unit : arrREmove) {
        int ix = arrUnit.indexOf(unit);
          arrUnit.remove(ix);
          arrHexMoveTo.remove(ix);
          arrHexMobileAssault.remove(ix);
       }
    }

    public enum Type{Bombard,MoveTo,SupplyBlocks}
    static class SortbyScoreAscending implements Comparator<AIOrders>{
        public int compare(AIOrders a, AIOrders b){

            if (a == null && b== null){
                return 0;
            }
            if (a == null){
                return 1;
            }
            if (b == null){
                return -1;
            }
            if (b.scoreMain == a.scoreMain){
                return 0;
            }
            if (b.scoreMain > a.scoreMain){
                return -1;
            }
            if (b.scoreMain < a.scoreMain){
                return 1;
            }

            return   0;
        }
    }
    static class SortbyScoreDescending implements Comparator<AIOrders>{
        public int compare(AIOrders a, AIOrders b){
            if (a == null && b== null){
                return 0;
            }
            if (a == null){
                return -1;
            }
            if (b == null){
                return 1;
            }
            if (b.scoreMain == a.scoreMain){
                return 0;
            }
            if (b.scoreMain > a.scoreMain){
                return 1;
            }
            if (b.scoreMain < a.scoreMain){
                return -1;
            }

            return   0;
        }
    }
}
