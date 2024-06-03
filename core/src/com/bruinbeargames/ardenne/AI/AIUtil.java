package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexCount;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AIUtil {
    static public ArrayList<Hex> arrSafe = new ArrayList<Hex>();
    static public ArrayList<Hex> arrEnemy = new ArrayList<Hex>();



    /**
     *  Get safe Area that can not be attacked
     * @param
     * @param
     * @param
     * @return
     */

    public static void RemoveDuplicateHex(ArrayList<Hex> arrHexIn)
    {
        Set<Hex> aSet =  new HashSet<Hex>();
        aSet.addAll(arrHexIn);
        arrHexIn.clear();
        arrHexIn.addAll(aSet);
        return;
    }
    /**
     * Sort by x and y
     * @param arrHex
     * @return
     */
    static public ArrayList<Hex> SortArray(ArrayList<Hex> arrHex)
    {
        ArrayList<Hex> arrReturn = new ArrayList();
        for (Hex hex:arrHex)
        {
            if (arrReturn.size() == 0)
            {
                arrReturn.add(hex);
            }
            else
            {
                boolean isAdded = false;
                for (int i= 0; i <arrReturn.size(); i++)
                {
                    Hex hex2 = arrReturn.get(i);
                    if (hex.xTable > hex2.xTable)
                    {
                        arrReturn.add(i, hex);
                        isAdded =true;
                        break;
                    }
                    else if (hex.xTable == hex2.xTable)
                    {
                        if (hex.yTable > hex2.yTable)
                        {
                            arrReturn.add(i, hex);
                            isAdded =true;
                            break;
                        }
                    }
                }
                if (!isAdded)
                {
                    arrReturn.add(hex);
                }
            }
        }
        return arrReturn;
    }


    /**
     *  reduce the area by 1 layer
     * @param arrHexIn
     * @return reduced
     */
    public static ArrayList<Hex> RemoveBarb(ArrayList<Hex> arrHexIn)
    {
        ArrayList<Hex> arrReturn = new ArrayList();
        arrReturn.addAll(arrHexIn);
        int xMax =0, yMax = 0, xMin = 99, yMin=99;
        for (Hex hex:arrHexIn)
        {
            if (hex.xTable > xMax)
            {
                xMax = hex.xTable;
            }
            if (hex.xTable < xMin)
            {
                xMin = hex.xTable;
            }
            if (hex.yTable > yMax)
            {
                yMax = hex.yTable;
            }
            if (hex.yTable < yMin)
            {
                yMin = hex.yTable;
            }

        }
        /**
         * shave top if not at max
         */
        if (Hex.xEnd - 1 > xMax  )
        {
            for (Hex hex:arrHexIn)
            {
                if (hex.xTable == xMax)
                {
                    arrReturn.remove(hex);
                }
            }

        }
        /**
         * shave Bottom if not at max
         */
        if (xMin > 0  )
        {
            for (Hex hex:arrHexIn)
            {
                if (hex.xTable == xMin)
                {
                    arrReturn.remove(hex);
                }
            }

        }
        /**
         * shave right if not at max
         */
        if (Hex.yEnd - 1 > yMax  )
        {
            for (Hex hex:arrHexIn)
            {
                if (hex.yTable == yMax)
                {
                    arrReturn.remove(hex);
                }
            }
        }
        /**
         * shave Left if not at min
         */
        if (yMin > 0  )
        {
            for (Hex hex:arrHexIn)
            {
                if (hex.yTable == yMin)
                {
                    arrReturn.remove(hex);
                }
            }
        }

        return arrReturn;
    }

    /**
     *  Get a square from max points
     * @param arrHex
     * @return
     */
    static public ArrayList<Hex> GetSquareFromPoints(ArrayList<Hex> arrHex)
    {
        int xMin = 99 ;
        int xMax = 0;
        int yMin = 99;
        int yMax = 0;
        for (Hex hex:arrHex)
        {
            if (hex.xTable > xMax)
            {
                xMax = hex.xTable;
            }
            if (hex.xTable < xMin)
            {
                xMin = hex.xTable;
            }
            if (hex.yTable > yMax)
            {
                yMax = hex.yTable;
            }
            if (hex.yTable < yMin)
            {
                yMin = hex.yTable;
            }
        }
        ArrayList<Hex> arrReturn = new ArrayList();
        for (int x =xMin ;x <= xMax; x++)
        {
            for (int y =yMin ;y <= yMax; y++)
            {
                arrReturn.add(Hex.hexTable[x][y]);
            }
        }
        return arrReturn;
    }

    /**
     *  find any hexes that are not included in  the input
     *   but are surrounded by all hexes in the input
     * @param arrHexInput Array to examine
     * @param diffCount - 6 hexes minus this value
     * @return
     */
    public static ArrayList<Hex> findHoles(ArrayList<Hex> arrHexInput, int diffCount) {
        ArrayList<Hex> arrNotInclude = new ArrayList<>();
        ArrayList<Hex> arrReturn = new ArrayList<>();
        /**
         *  get all surrounding not in input
         */
        for (Hex hex:arrHexInput){
            ArrayList<Hex> arrSurround = hex.getSurround();
            for (Hex hex1:arrSurround){
                if (!arrHexInput.contains(hex1)){
                    arrNotInclude.add(hex1);
                }
            }
        }
        AIUtil.RemoveDuplicateHex(arrNotInclude);
        for (Hex hex:arrNotInclude){
            ArrayList<Hex> arrSurround = hex.getSurround();
            int count = arrSurround.size();
            arrSurround.retainAll(arrHexInput);
            count -= diffCount;
            if (count <= arrSurround.size()){
                arrReturn.add(hex);
            }
        }
        AIUtil.RemoveDuplicateHex(arrReturn);

        return arrReturn;
    }

    public static ArrayList<Hex>[] getArtilleryMaxMove(ArrayList<Unit> arrUnitsin, int thread) {
        ArrayList<Hex>[] arrReturn = new ArrayList[arrUnitsin.size()];
        int ix=0;
        for (Unit unit:arrUnitsin){
            int move = unit.getCurrentMoveNoBarrage();
            UnitMove unitMove = new UnitMove(unit,move,true,true, thread);
            ArrayList<Hex> arrHexMove = new ArrayList<>();
            arrHexMove.addAll(unitMove.getMovePossible());
            arrReturn[ix]= arrHexMove;
            ix++;
        }
        return arrReturn;
    }
    public static ArrayList<Hex>[] getUnitsMaxMove(ArrayList<Unit> arrUnitsin, int thread, boolean isMOA) {
        ArrayList<Hex>[] arrReturn = new ArrayList[arrUnitsin.size()];
        int ix=0;
        for (Unit unit:arrUnitsin){
            int move = unit.getCurrentMovement();
            UnitMove unitMove = new UnitMove(unit,move,isMOA,true, thread);
            ArrayList<Hex> arrHexMove = new ArrayList<>();
            arrHexMove.addAll(unitMove.getMovePossible());
            arrReturn[ix]= arrHexMove;
            ix++;
        }
        return arrReturn;
    }

    /**
     * Create Iterations for possible moves
     * @param arrUnits units that will be moving
     * @param thread thread to use
     * @param isArtilleryOnly  is this artillery
     * @param arrLimitIterate limit of search
     * @param arrMOA
     * @return an array of AIorders with combinations
     *    This rtn will use the current hex that unit is in for position
     */
    public static ArrayList<AIOrders> GetIterations(ArrayList<Unit> arrUnits, int thread, boolean isArtilleryOnly, ArrayList<Hex> arrLimitIterate, ArrayList<AIMobileAssault> arrMOA,AIOrders aiOrdersInclude) {

        ArrayList<AIOrders> arrReturn =  new ArrayList<>();
        ArrayList<Hex>[] arrHexResult;
        if (isArtilleryOnly){
            arrHexResult = getArtilleryMaxMove(arrUnits,thread);
        }else{
            arrHexResult = getUnitsMaxMove(arrUnits,thread, true);
        }

        /**
         *  include  Mobile assualt if available
         */
        boolean isMOA = false;
        if (arrMOA != null && arrMOA.size() > 0){
            int ix=0;
            for (Unit unit:arrUnits){
                ArrayList<Hex> arrHexAfterMOA =  AIMobileAssault.getHexAfter(unit);
                if (arrHexAfterMOA != null) {
                    arrHexAfterMOA.retainAll(arrLimitIterate);
                    if (arrHexAfterMOA != null) {
                        arrHexResult[ix].addAll(arrHexAfterMOA);
                    }
                }
                ix++;
            }
            isMOA = true;

        }
        /**
         *  add in AIOrder don in any preceeding iterations
         */
        if (aiOrdersInclude != null){
            ArrayList<Hex>[] arrWork = new ArrayList[arrHexResult.length+aiOrdersInclude.arrHexMoveTo.size()];
            int ix=0;
            for (ArrayList<Hex> arr:arrHexResult){
                    arrWork[ix] = new ArrayList();
                    arrWork[ix].addAll(arr);
                    ix++;
            }
            for (Hex hex:aiOrdersInclude.arrHexMoveTo){
                ArrayList<Hex> arrHex = new ArrayList<>();
                arrHex.add(hex);
                arrWork[ix] = arrHex;
                ix++;
            }
            arrUnits.addAll(aiOrdersInclude.arrUnit);
            arrHexResult = arrWork;

        }

        /**
         *  retain only what we need but make sure at least one is kept
         *  but check for AIORDERS Joined
         */
        int ix = 0;
        for (ArrayList<Hex> arr:arrHexResult){
            if (arr.size() > 1) {
                arr.retainAll(arrLimitIterate);
            }
            if (arr.size() ==0){
                arr.add(arrUnits.get(ix).getHexOccupy());
            }
            ix++;
        }
        ArrayList<Hex> arrPosition = new ArrayList<>();
        for (Unit unit:arrUnits){
            arrPosition.add(unit.getHexOccupy());
        }
        AIIterator aiIterator = new AIIterator(arrHexResult,arrUnits,arrPosition, AIOrders.Type.MoveTo);
        AIOrders aiOrders = aiIterator.Iteration();
        if (isMOA) {
            checkMobile(aiOrders);
        }
        while (aiOrders != null){
            arrReturn.add(aiOrders);
            aiOrders = aiIterator.doNext();
            if (isMOA && aiOrders != null) {
                checkMobile(aiOrders);
            }
        }
        if (Gdx.app.getType() != Application.ApplicationType.Desktop){
            reduceIterations(arrReturn);
        }

        return arrReturn;
      }

    /**
     *  reduce the amount o iterations beacuse of android has weaker cpu
     * @param arrReturn
     */
    public static void reduceIterations(ArrayList<AIOrders> arrReturn) {
        Gdx.app.log("AIUtil", "Reducing Iterations start="+arrReturn.size());

        if (arrReturn.size() < 100) {
            return;
        }

        float keep = 250/arrReturn.size();
        keep *= 100; //create a percentage
        double percentageToRemove = 100 - keep; // 30% in this example

        // Calculate the number of elements to remove
        int elementsToRemove = (int) (percentageToRemove / 100 * arrReturn.size());
        Random random = null;
        // Use a random number generator to select elements to remove
        for (int i = 0; i < elementsToRemove; i++) {
            Random diceRoller = new Random();
            int indexToRemove = diceRoller.nextInt(arrReturn.size());
            arrReturn.remove(indexToRemove);
            if (arrReturn.size() < 10){
                break;
            }
        }
        Gdx.app.log("AIUtil", "Reducing Iterations end="+arrReturn.size());


    }

    public static ArrayList<AIOrders> GetIterations(ArrayList<Unit> arrUnits, ArrayList<Hex>[] arrArrs) {
        Gdx.app.log("AIUtil", "GetIterations units="+arrUnits.size());
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            int cntIters = 1;
            for (ArrayList<Hex> arr : arrArrs) {
                Gdx.app.log("AIUtil", "arr size==" + arr.size());

                cntIters *= arr.size();
            }
            Gdx.app.log("AIUtil", "Possible Iters ==" + cntIters);

            while (cntIters > 4000) {
                cntIters = 1;
                for (ArrayList<Hex> arr : arrArrs) {
                    if (arr.size() > 4) {
                        arr.remove(arr.size() - 1);
                    }
                    cntIters *= arr.size();
                }
            }
            Gdx.app.log("AIUtil", "Result Iters ==" + cntIters);

        }


        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        ArrayList<Hex> arrPosition = new ArrayList<>();
        for (Unit unit:arrUnits){
            arrPosition.add(unit.getHexOccupy());
        }
        AIIterator aiIterator = new AIIterator(arrArrs,arrUnits,arrPosition, AIOrders.Type.MoveTo);
        AIOrders aiOrders = aiIterator.Iteration();
        while (aiOrders != null){
            arrReturn.add(aiOrders);
            aiOrders = aiIterator.doNext();
        }
        if (Gdx.app.getType() != Application.ApplicationType.Desktop){
            reduceIterations(arrReturn);
        }

        return arrReturn;
   }


    private static void checkMobile(AIOrders aiOrders) {
        int ix =0;
        for (Unit unit:aiOrders.arrUnit){
            ix = aiOrders.arrUnit.indexOf(unit);
            Hex hexCheck = aiOrders.arrHexMoveTo.get(ix);
            Hex hexMOA= AIMobileAssault.getHexWhereMOAStarted(unit,hexCheck);
            if (hexMOA != null){
                aiOrders.addMOA(unit, hexCheck,hexMOA);
            }
        }
    }


    /**
     * determine which units can not make any of hexesin array
     * @param arrUnits
     * @param arrHexes
     * @return
     */
    public static ArrayList<Unit> cantReach(ArrayList<Unit> arrUnits, ArrayList<Hex> arrHexes) {
        ArrayList<Unit> arrReturn = new ArrayList<>();
        for (Unit unit:arrUnits){
            int move = unit.getCurrentMovement();
            UnitMove unitMove = new UnitMove(unit,move,true,true, 0);
            ArrayList<Hex> arrHexMove = new ArrayList<>();
            arrHexMove.addAll(unitMove.getMovePossible());
            arrHexMove.retainAll(arrHexes);
            if (arrHexMove.size() == 0){
                arrReturn.add(unit);
            }
        }
        return arrReturn;
    }

    public static ArrayList<Hex> getSurroundUnits(boolean isAllies) {
        ArrayList<Hex> arrReturn = new ArrayList<>();

        if (isAllies){
            for (Unit unit:Unit.getOnBoardAllied()) {
                arrReturn.addAll(unit.getHexOccupy().getSurround());
            }
        }else{
            for (Unit unit:Unit.getOnBoardAxis()) {
                arrReturn.addAll(unit.getHexOccupy().getSurround());
            }

        }
        RemoveDuplicateHex(arrReturn);
        return arrReturn;
    }

    /**
     *  find the closest hex  in the array
     * @param hexes
     * @param hexEnd
     * @return
     */
    public static Hex findClosestHex(ArrayList<Hex> hexes, Hex hexEnd) {
        Hex hexReturn = null;
        for (Hex hex:hexEnd.getSurround()){
            if (hexes.contains(hex)){
                return hex;
            }
        }
        /**
         *  didnt find it the easy way
         */
        int cntDiff= 99;
        int calcDiff =0;
        for (Hex hex:hexes){
            calcDiff = Math.abs(hex.xTable - hexEnd.xTable);
            calcDiff +=  Math.abs(hex.yTable - hexEnd.yTable);
            if (calcDiff <= cntDiff){
                cntDiff = calcDiff;
                hexReturn = hex;
            }
        }
        return hexReturn;
    }

    /**
     * Check that AIOrder does not cause overstacking but not overall stacking
     *
     * @param arrHexStackCnt
     * @param arrOrders      the aiorders to check
     * @return
     */
    public static ArrayList<AIOrders> checkStaking(ArrayList<HexInt> arrHexStackCnt, ArrayList<AIOrders> arrOrders) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrOrders);
        ArrayList<AIOrders> arrRemove = new ArrayList<>();
        for (AIOrders aiO:arrOrders){
            int ix=0;
            ArrayList<HexInt> arrTemp = new ArrayList<>();
            arrTemp.addAll(arrHexStackCnt);
            for (Hex hex:aiO.arrHexMoveTo){
                boolean isHit=false;
                for (HexInt hi:arrTemp) {
                    if (hi.hex == hex) {
                        isHit = true;
                        if (hi.count + aiO.arrUnit.get(ix).getCurrentStep() > Hex.stackMax){
                            if (!arrRemove.contains(aiO)) {
                                arrRemove.add(aiO);
                            }
                        }else{
                            hi.count += aiO.arrUnit.get(ix).getCurrentStep();
                        }
                    }
                }
                if (!isHit){
                    HexInt hexInt = new HexInt(hex,aiO.arrUnit.get(ix).getCurrentStep());
                    arrTemp.add(hexInt);
                }
            }
            ix++;
        }

        arrReturn.removeAll(arrRemove);
        return arrReturn;
    }

    public static ArrayList<HexInt> setStackCount(boolean isAllies, ArrayList<AIOrders> arrOrders, AIOrders aiBastogne) {
        ArrayList<Unit> arrUnitsNotOnOrders = new ArrayList<>();
        if (isAllies) {
            arrUnitsNotOnOrders.addAll(Unit.getOnBoardAllied());
        }else{
            arrUnitsNotOnOrders.addAll(Unit.getOnBoardAllied());
        }
        arrUnitsNotOnOrders.removeAll(arrOrders.get(0).arrUnit);

        ArrayList<HexInt> arrHexStackCnt = new ArrayList<>();
        /**
         *  set up table with stack cnt for units not on orders
         */
        for (Unit unit:arrUnitsNotOnOrders){
            Hex hex = unit.getHexOccupy();
            HexInt hexInt = null;
            for (HexInt hi:arrHexStackCnt) {
                if (hi.hex == hex) {
                    hexInt = hi;
                }
            }
            if (hexInt == null) {
                hexInt = new HexInt(hex, unit.getCurrentStep());
                arrHexStackCnt.add(hexInt);
            }else{
                hexInt.count += unit.getCurrentStep();
            }
        }
        /**
         *  add Orders from Bastogne
         */
        int ix =0;
        for (Hex hex:aiBastogne.arrHexMoveTo){
            HexInt hexInt = null;
            for (HexInt hi:arrHexStackCnt) {
                if (hi.hex == hex) {
                    hexInt = hi;
                }
            }
            if (hexInt == null) {
                hexInt = new HexInt(hex, aiBastogne.arrUnit.get(ix).getCurrentStep());
                arrHexStackCnt.add(hexInt);
            }else{
                hexInt.count += aiBastogne.arrUnit.get(ix).getCurrentStep();
            }
            ix++;
        }
        return arrHexStackCnt;
    }

    /**
     * keep only top
     *
     * @param arrAirAllocate
     * @param keep
     * @return
     */
    public static ArrayList<HexInt> keepTop(ArrayList<HexInt> arrAirAllocate, int keep) {
        ArrayList<HexInt> arrNew = new ArrayList<>();
        for (int i=0; i< keep;i++ ){
            arrNew.add(arrAirAllocate.get(i));
        }
        return arrNew;
    }
    static public ArrayList<HexInt> getAIHexStats(int thread) {
        UnitMove.setAIStat(true);
        HexCount.init();
        if (thread == 0) {

        } else {
            Hex.fakeClearMoveFields(false, true, thread);
        }
        ArrayList<Hex>[] arrArr = createGermanMoves(Unit.getOnBoardAxis(), thread);
        UnitMove.setAIStat(false);
        ArrayList<HexInt> arrReturn = countHexes(arrArr);
        return arrReturn;
    }
    static public ArrayList<HexInt> countHexes(ArrayList<Hex>[] arrArr){
        for (ArrayList<Hex> arr:arrArr){
            for (Hex hex:arr){
                HexCount hexCount = new HexCount(hex);
            }
        }
        ArrayList<Hex> arrHex = HexCount.getArrHex();
        ArrayList<Integer> arrInt = HexCount.getArrInt();
        ArrayList<HexInt> arrWork = new ArrayList<>();
        for (int i=0; i< arrHex.size();i++){;
            HexInt hi = new HexInt(arrHex.get(i), arrInt.get(i));
            arrWork.add(hi);
        }
        Collections.sort(arrWork, new AIScenarioOther.SortDescending());
        return arrWork;
    }
    static public  ArrayList<HexInt> countHexes(ArrayList<ArrayList<Hex>> arrIn){
        ArrayList<Hex>[] arrWork = new ArrayList[arrIn.size()];
        int ix=0;
        for (ArrayList<Hex> arr:arrIn){
            arrWork[ix] = arr;
            ix++;
        }
        return countHexes(arrWork);
    }
    private static ArrayList<Hex>[] createGermanMoves(ArrayList<Unit> arrGermans, int thread) {
        ArrayList<Hex>[] arrArrHex = new ArrayList[arrGermans.size()];
        int i=0;
        for (Unit unit:arrGermans){

            //          Gdx.app.log("AIFaker", "creatGermanMove="+unit);
            UnitMove unitMove = new UnitMove(unit,unit.getCurrentMovement(),true,true,thread);
            arrArrHex[i] = unitMove.getMovePossible(thread);
 /*           if (arrArrHex[i].contains(Wiltz) || arrArrHex[i].contains(Hex.hexTable[8][11])) {
                WinAIDisplay.instance.addSpecial(arrArrHex[i]);
                int bk=0;
            }*/

            i++;
        }
        return arrArrHex;
    }



}
/**
 *  Temporary Class to take threat envelopes and apply analysis to them
 * @author Casey
 *
 */
class AIThreatAnalysis
{
    static public ArrayList<AIThreatAnalysis> arrAnalysis = new ArrayList<AIThreatAnalysis>();
    Hex hex;
    float score;
    public AIThreatAnalysis(Hex hexIn)
    {
        hex =hexIn;
    }
    private void  UpdateScore(float cnt)
    {
        score += cnt;
    }
    static public AIThreatAnalysis Find(Hex hex)
    {
        for (AIThreatAnalysis aiThreatAnalysis:arrAnalysis)
        {
            if (aiThreatAnalysis.hex == hex)
            {
                return aiThreatAnalysis;
            }
        }
        return null;
    }
    static public ArrayList<Hex> GetAllHex()
    {
        ArrayList<Hex>arrReturn = new ArrayList<Hex>();
        for (AIThreatAnalysis aiThreatAnalysis:arrAnalysis)
        {
            arrReturn.add(aiThreatAnalysis.hex);
        }
        return arrReturn;

    }

    /**
     * Get usage of hex by Germans
     * if thread is zero then use setup zoc and occupied
     * if thread any other then clear it
     *
     *  This should be used before going into allied AI
     * @param thread  if 0
     * @return ArrayList of HexCount
     *
     *
     */


}

