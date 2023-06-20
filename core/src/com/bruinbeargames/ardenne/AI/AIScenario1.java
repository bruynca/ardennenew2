package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;

public class AIScenario1 {
    public static AIScenario1 instance;
    int[][] routeClerVaux ={{29,9},{27,8},{25,8},{21,6},{18,9},{14,10},{12,11}};
    int[][] routeWiltz ={{24,15},{19,14},{18,16},{11,12},{9,14}};
    int [][] routeEttlebruck = {{33,17},{35,20},{28,23},{19,20},{19,24},
            {11,22},{9,23},{6,18}};
    ArrayList<Hex> arrClervaux = new ArrayList<Hex>();
    ArrayList<Hex> arrWiltz = new ArrayList<Hex>();
    ArrayList<Hex> arrEttlebruck = new ArrayList<Hex>();
    ArrayList<Hex> arrClervauxReset = new ArrayList<Hex>();
    ArrayList<Hex> arrWiltzReset = new ArrayList<Hex>();
    ArrayList<Hex> arrEttlebruckReset = new ArrayList<Hex>();
    ArrayList<Hex> arrAllRoutes = new ArrayList<>();
    ArrayList<Unit> arrUnitsScenario = new ArrayList<>();

    /**
     *  to determine if Germans are past defense
     */
    int yClervauxMin =3;
    int yClervauxMax =11;
    int yWiltzMin = 12;
    int yWiltzMax = 18;
    int yEttleBruckMin = 19;
    int yEttleBruckMax = 24;

    ArrayList<Unit> arrNotPenetration = new ArrayList<>();
    boolean isAllies;



    AIScenario1(){
        instance = this;
        AIApproach aiApproach = new AIApproach();
        for (int[] in:routeClerVaux){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrClervaux.add(hex);
            arrAllRoutes.add(hex);
        }
        for (int[] in:routeWiltz){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrWiltz.add(hex);
            arrAllRoutes.add(hex);
        }
        for (int[] in:routeEttlebruck){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrEttlebruck.add(hex);
            arrAllRoutes.add(hex);
        }
   }

    public ArrayList<Hex> getArrAllRoutes() {
        ArrayList<Hex> arrWork = new ArrayList<>();
                arrWork.addAll(arrAllRoutes);
        return arrWork;
    }

    public ArrayList<Hex> getArrClervaux() {
        return arrClervaux;
    }

    public ArrayList<Hex> getArrEttlebruck() {
        return arrEttlebruck;
    }

    public ArrayList<Hex> getArrWiltz() {
        return arrWiltz;
    }

    /**
     *  get hexes that are not overrun by germans
     * @return 3 arrays representing Clervaux, ettlebruck and wiltz
     */

    public ArrayList<Hex>[] getActivePathReset() {
        ArrayList<Hex>[] arrReturn = new ArrayList[3];
        arrReturn[0] = new ArrayList<>();
        for (Hex hex:arrClervauxReset){
                arrReturn[0].add(hex);

        }
        arrReturn[1] = new ArrayList<>();
        for (Hex hex:arrEttlebruckReset){
                arrReturn[1].add(hex);

        }
        arrReturn[2] = new ArrayList<>();
        for (Hex hex:arrWiltzReset){
                arrReturn[2].add(hex);
       }
        return arrReturn;
    }

    public ArrayList<Hex> getActiveArrEttlebruck() {
        return arrEttlebruck;
    }

    public ArrayList<Hex> getActiveArrWiltz() {
        return arrWiltz;
    }

    /**
     *  analyse all the hexes and just include relevant
     * @return
     */
    public ArrayList<Hex> getAlliedLimit() {
        ArrayList<Hex> arrReturn =  new ArrayList<>();
        return arrReturn;
    }

    /**
     *  return hexes that are still available i.e. not behind enemy lines .
     *  change to only get firt 3 for route
     * @return
     */
    public ArrayList<Hex> getRoutesAheadOfGermanRoutes() {

        ArrayList<Hex> arrReturn = new ArrayList<>();
        resetRoutesAheadOfGermans();

        int i=0;
        i=0;
        for (Hex hex:arrClervauxReset){
            arrReturn.add(hex);
            i++;
            if (i==2){
                break;
            }
        }
        for (Hex hex:arrWiltzReset){
            arrReturn.add(hex);
            i++;
            if (i==2){
                break;
            }
        }
        for (Hex hex:arrEttlebruckReset){
            arrReturn.add(hex);
            i++;
            if (i==2){
                break;
            }
        }
        return arrReturn;
    }

    /**
     *   add hexes that are ahead of germans to reset routes;
     */
    private void resetRoutesAheadOfGermans() {
        arrClervauxReset.clear();
        arrWiltzReset.clear();
        arrEttlebruckReset.clear();
        int limitxClervaux = 40;
        int limitxEttlebruck = 40;
        int limitxWiltz = 40;
        /**
         *  calculate x where germans are at  for each route
         */
        for (Unit unit: Unit.getOnBoardAxis()) {
            Hex hexCheck = unit.getHexOccupy();
            if (hexCheck.yTable >= yClervauxMin && hexCheck.yTable <= yClervauxMax){
                if (hexCheck.xTable < limitxClervaux){
                    limitxClervaux = hexCheck.xTable;
                }
            }
            if (hexCheck.yTable >= yWiltzMin && hexCheck.yTable <= yWiltzMax){
                if (hexCheck.xTable < limitxWiltz){
                    limitxWiltz = hexCheck.xTable;
                }
            }
            if (hexCheck.yTable >= yEttleBruckMin && hexCheck.yTable <= yEttleBruckMax){
                if (hexCheck.xTable < limitxEttlebruck){
                    limitxEttlebruck = hexCheck.xTable;
                }
            }

        }
        for (Hex hex:arrClervaux){
            if (hex.xTable < limitxClervaux){
                arrClervauxReset.add(hex);
            }
        }

        for (Hex hex:arrWiltz){
            if (hex.xTable < limitxWiltz){
                arrWiltzReset.add(hex);
            }
        }

        for (Hex hex:arrEttlebruck){
            if (hex.xTable < limitxEttlebruck){
                arrEttlebruckReset.add(hex);
            }
        }
    }

    /**
     *  Score the the aiorders by how close they are to start of path
     *      *   they are to start of the routes
     *      *   Score the aiorders for how close they are
     *
     * @param arrAIIn
     * @return
     */
    public ArrayList<AIOrders> getForwardBlockHexesOnly(ArrayList<AIOrders> arrAIIn) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        for (AIOrders aiO:arrAIIn){
            aiO.setScoreMain(0);
            int scorer =0;
            ArrayList<String> arrScore = new ArrayList<>();
            for (Hex hex: aiO.arrHexMoveTo){
 //               if (hex == Hex.hexTable[24][15]){
 //                   int b=0;
 //               }
                int ix = arrClervauxReset.indexOf(hex);
                if (ix > -1){
                   scorer -= (arrClervauxReset.size() - ix) * 5;
                   arrScore.add("FB ClervauxReset="+(arrClervauxReset.size() - ix) * 5);

                }
                ix = arrWiltzReset.indexOf(hex);
                if (ix > -1){
                    scorer -= (arrWiltzReset.size() - ix) * 5;
                    arrScore.add("FB Wiltz="+(arrClervauxReset.size() - ix) * 5);

                }
                ix = arrEttlebruckReset.indexOf(hex);
                if (ix > -1){
                    scorer -= (arrEttlebruckReset.size() - ix) * 5;
                    arrScore.add("FB Ettlebruck= -"+(arrClervauxReset.size() - ix) * 5);

                }
            }
            aiO.setScoreMain(scorer);
            aiO.updateScoreMessage(arrScore);
            int i=0;
            for (i=0; i<arrReturn.size(); i++){
                if (aiO.scoreMain < arrReturn.get(i).scoreMain){
                    break;
                }
            }
            arrReturn.add(i,aiO);
        }
        return arrReturn;
    }




    /**
     *  return all aiorders from input that do not cover all paths;
     *  these will be removed
     * @param arrNodupes
     * @return
     */
    public ArrayList<AIOrders> findNotAllPathsBlocked(ArrayList<AIOrders> arrNodupes) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        for (AIOrders ai: arrNodupes) {
            int cntClervaux =0;
            int cntWiltz =0;
            int cntEttlebruk =0;
            boolean isClervaux = false;
            boolean isWiltz = false;
            boolean isEttlebruck = false;
            for (Hex hex:ai.arrHexMoveTo){
                if (arrClervaux.contains(hex)){
                    isClervaux = true;
                    cntClervaux++;
                }
                if (arrWiltz.contains(hex)){
                    isWiltz = true;
                    cntWiltz++;

                }
                if (arrEttlebruck.contains(hex)){
                    cntEttlebruk++;
                    isEttlebruck = true;
                }
            }
            if (isClervaux && isEttlebruck &&isWiltz){
                if (ai.arrUnit.size() > 3 && cntClervaux < 1){
                    arrReturn.add(ai);
                }
            }else{
                arrReturn.add(ai);
            }
        }
        return arrReturn;
    }
    /**
     *  do move for scene1 allied
     * @return true if execute of move handled here
     */
    public void doInitialMoveAlliesTurn1to3() {
        Gdx.app.log("AIScenario1", "doInitialMoveAlliesTurn1to3");
        isAllies = true;

        /**
         * 1. get all iterations for units on hexes that are still available
         * 2. analyse to get least german breakthru
         */
        arrUnitsScenario.clear();
        ArrayList<UnitMove> arrUnitMove = new ArrayList<>();
        ArrayList<UnitMove> arrUnitMoveWithMOA = new ArrayList<>();
        ArrayList<UnitMove> arrUnitMoveNoGermans = new ArrayList<>();
        ArrayList<Unit> arrUnitsCantMakeIt = new ArrayList<>();
        arrNotPenetration.clear();

        /**
         *  get all units for allied
         *  and the unitmoves
         *  and unitMovesMOA
         *  and unitMovesNoGermans on map3 = {AIOrders@2325}
         */
        arrUnitsScenario.addAll(Unit.getOnBoardAllied());
        Hex.initThreadHex(3); // set no one on board
        Gdx.app.log("AIMover", "Allied Units =" + arrUnitsScenario);
 /*       for (Unit unit:arrUnits){
            UnitMove unitMove = new UnitMove(unit,unit.getCurrentMovement(),false, true,0);
            arrUnitMove.add(unitMove);
            unitMove = new UnitMove(unit,unit.getCurrentMovement(),false, true,3);
            arrUnitMoveNoGermans.add(unitMove);
            if (!unit.isArtillery){
                unitMove = new UnitMove(unit,unit.getCurrentMovement(),true, true,0);
                arrUnitMove.add(unitMove);
           }
        }*/

        /**
         *  get all the hex for the defense paths BASED ON GERMAN ADVANCES
         *  dont add any behind german lines
         */
        ArrayList<Hex> arrHexLimit = AIScenario1.instance.getRoutesAheadOfGermanRoutes();
        /**
         *  add any units behind german lines so they are included
         */
        for (Hex hex:arrAllRoutes) {
            if (hex.isAlliedOccupied()) {
                arrHexLimit.add(hex);
            }
        }
        /**
         *  remove any german units from hexes to check
         */
        for (Unit unit:Unit.getOnBoardAxis()){
            arrHexLimit.remove(unit.getHexOccupy());
        }
        if (arrHexLimit.size() == 0) {
            doNonPenetrations(null);
            return;
        }
        /**
         *  any units that can not make put in no iterations
         */
        arrUnitsCantMakeIt = AIUtil.cantReach(arrUnitsScenario,arrHexLimit);
        ArrayList<Unit> arrUnitsToCheck = new ArrayList<>();
        arrUnitsToCheck.addAll(arrUnitsScenario);
        arrUnitsToCheck.removeAll(arrUnitsCantMakeIt);
        /**
         *  get the iteration for the units to the defense paths
         */
        ArrayList<AIOrders> arrAIStart = AIUtil.GetIterations(arrUnitsToCheck,0,false,arrHexLimit, null,null);
        Gdx.app.log("AIMover", "Iterations at start =" + arrAIStart.size());
        if (arrAIStart.size() == 0) {
            doNonPenetrations(null);
            return;
        }
        /**
         *  remove any iterations that are going to same hex(duplicates)
         */
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        ArrayList<AIOrders> arrNodupes = AIOrders.removeDupeMoveToHexes(arrAIStart, arrAllowDuplicates);
        Gdx.app.log("AIMover", "Iterations remove dupes =" + arrNodupes.size());
        if (arrNodupes.size() == 0) {
            doNonPenetrations(null);
            return;
        }
        /**
         *  if we have the units  remove any that do not at least block
         *  1 of the 3 paths
         */
        if (arrUnitsToCheck.size() >= 3){
            ArrayList<AIOrders> arrRemove =  AIScenario1.instance.findNotAllPathsBlocked(arrNodupes);
            arrNodupes.removeAll(arrRemove);
        }
        /**
         *  only use the most forward of the block hexes
         */
        ArrayList<AIOrders> arrForward =  AIScenario1.instance.getForwardBlockHexesOnly(arrNodupes);
        Gdx.app.log("AIMover", "Iterations forward only =" + arrForward.size());
        if (arrForward.size() == 0) {
             doNonPenetrations(null);
             return;
        }
        /**
         *  start looping through iterations
         *  This will start 10 threads that will query FakeDone to end the process
         *  pass control back to AIMover 
         */
        Gdx.app.log("AIScenario1", "doMoveScene1AlliesTurn1to3 arrForward");
 //       AIOrders.display(arrForward);
        setArrToBeScored(arrForward, AIScorer.Type.GermanPenetration);
        return;
    }

    /**
     *   All units that do not fit into path algorithm are
     *    optimized here
     *    These units could not make behind German Lines
     */
    private void doNonPenetrations(AIOrders aiOrders) {
        Gdx.app.log("AIScenario1", "doNonPenetration display order coming in");
        if (aiOrders != null) {
            aiOrders.display();
        }

        Hex.initThreadHex(3); // set no one on board
        /**
         *  check and take out if units that have orders
         */
        if (aiOrders == null){
            aiOrders = new AIOrders();
        }
        arrUnitsScenario.removeAll(aiOrders.arrUnit);
        /**
         *  create an AIORDerS for artillery
         */
        ArrayList<Unit> arrArtillery = new ArrayList<>();
        for (Unit unit: arrUnitsScenario){
            if (unit.isArtillery){
                arrArtillery.add(unit);
            }
        }
        arrUnitsScenario.removeAll(arrArtillery);
        AIOrders aiOrdersArtillery = null;
        if (arrArtillery.size() > 0){
            aiOrdersArtillery = AILimber.instance.getBestBombard(arrArtillery);
            aiOrders.combineMove(aiOrdersArtillery);
        }

        /**
         *  if no units left go to execute
         */
        if (arrUnitsScenario.size() == 0) {
            ArrayList<AIOrders> arrWork = new ArrayList<>();
            arrWork.add(aiOrders);
            endTurn1to3(arrWork);
            return;
        }
        ArrayList<Hex> arrHexToCheck = new ArrayList<>();
        /**
         *  add all routes
         *
         */
        arrHexToCheck.addAll(arrAllRoutes);
 //       arrHexToCheck.addAll(AIUtil.getSurroundUnits(false));
        arrHexToCheck.addAll(Supply.instance.getGermanBottlenecks());
        AIUtil.RemoveDuplicateHex(arrHexToCheck);
        /**
         *  remove any german units from hexes to check
         *
         */
        /**
         *  if we have too many units do not add the surround Germans
         */
        if (arrUnitsScenario.size() < 4) {
            for (Unit unit : Unit.getOnBoardAxis()) {
                arrHexToCheck.addAll(unit.getHexOccupy().getSurround());
            }
        }
        /**
         *  if we have too many units save only towns an bridges
         */
        if (arrUnitsScenario.size() > 3) {
            ArrayList<Hex> arrRemove = new ArrayList<>();
            for (Hex hex:arrHexToCheck){
                if (Bridge.hasBridge(hex) || hex.isTown() || hex.isCity()) {
                    /// do nothing
                }else{
                    arrRemove.add(hex);
                }
            }
            arrHexToCheck.removeAll(arrRemove);
        }
        AIUtil.RemoveDuplicateHex(arrHexToCheck);
        for (Unit unit:Unit.getOnBoardAxis()){
            arrHexToCheck.remove(unit.getHexOccupy());
        }
        /**
         *  check if units can Mobile asault
         */
        AIMobileAssault.createArrays(arrUnitsScenario, null);

        ArrayList<AIOrders> arrAINonPenetrate = AIUtil.GetIterations(arrUnitsScenario,0,false,arrHexToCheck,AIMobileAssault.getAssualt(),aiOrders);
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        ArrayList<AIOrders> arrNodupes = AIOrders.removeDupeMoveToHexes(arrAINonPenetrate, arrAllowDuplicates);
        ArrayList<AIOrders> arrNoEnemy = AIOrders.removeEnemyPlace(arrNodupes, isAllies);

        setArrToBeScored(arrNoEnemy, AIScorer.Type.GermanRegular);

    }

    /**
     *  end of the road do execute the move
     * @param arrIn
     */
    void endTurn1to3(ArrayList<AIOrders> arrIn) {
        Gdx.app.log("AIScenario1", "endTure1to3");

        /**
         *  sort the orders ascending
         */
        ArrayList<AIOrders> arrSortedAscending = new ArrayList<>();
        int i=0;
        for (AIOrders aiO: arrIn){
            for (i=0; i< arrSortedAscending.size(); i++){;
                if (aiO.scoreMain < arrSortedAscending.get(i).scoreMain){
                    break;
                }
            }
            arrSortedAscending.add(i,aiO);
        }
        arrSortedAscending.get(0).display();
        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrSortedAscending, .3f);
        AISupply.instance.doSupplyAnalysis(arrTop, true);
        return;
    }
    public void handOffToMover(ArrayList<AIOrders> arrOrdersIn){
        /**
         *  *  remove any iterations that are going to same hex(duplicates)
         */
        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        ArrayList<AIOrders> arrNodupes = AIOrders.removeDupeMoveToHexes(arrOrdersIn, arrAllowDuplicates);
        /**
         *  force units in wiltz to stay
         */
        int ix=0;
        ArrayList<Unit> arrREmove  = new ArrayList<>();
        for (Unit unit:arrOrdersIn.get(0).arrUnit){
            if (unit.getHexOccupy() == AIReinforcementScenario1.hexWiltz){
                arrOrdersIn.get(0).arrHexMoveTo.set(ix,AIReinforcementScenario1.hexWiltz);
                arrOrdersIn.get(0).arrHexMobileAssault.set(ix, AIReinforcementScenario1.hexWiltz);
            }
            ix++;
        }

        if (arrNodupes.size() == 0) {
            AIMover.instance.execute(arrOrdersIn.get(0));
        }else{
            AIMover.instance.execute(arrNodupes.get(0));
        }
    }

    /**
     *  Iterations done on the Penetration
     *  do final analysis passed from aimover
     *  if orders in is zero then there are no penetration orders
     */

    public void endPenetrationAnalysis(ArrayList<AIOrders> arrIn) {
        Gdx.app.log("AIScenario1", "endPenetrationAnalysis");

        Gdx.app.log("AIMover", "Before Top Ten");
 //       AIOrders.display(arrIn);
        /**
         *  sort the orders ascending
         */
        ArrayList<AIOrders> arrSortedAscending = new ArrayList<>();
        int i=0;
        for (AIOrders aiO: arrIn){
            for (i=0; i< arrSortedAscending.size(); i++){;
                if (aiO.scoreMain < arrSortedAscending.get(i).scoreMain){
                    break;
                }
            }
            arrSortedAscending.add(i,aiO);
        }

        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrSortedAscending, .3f);
//        ArrayList<AIOrders> attTopDefense = AIScenario1.instance.bestDefense(arrTopTen);
        Gdx.app.log("AIMover", "Top Ten");
        AIOrders.display(arrIn);
        ArrayList<AIOrders> arrFinal = getTopInPaths(arrTop);
        AIOrders aiPenetrationOrders = arrFinal.get(0); // get top for now
        Gdx.app.log("AIMover", "Penetration to follow");
        aiPenetrationOrders.display();

        doNonPenetrations(aiPenetrationOrders);

 /*       if (arrNotPenetration.size() > 0){
            AIOrders aiOrdersNotIterate = AIScenario1.instance.getNotPenetrationOrders();
            aiPenetrationOrders.combineMove(aiOrdersNotIterate);
        }
        AIMover.instance.execute(aiPenetrationOrders); */

        return;
    }

        /**
         * adjust the order so that we add points for being top
         * and then sort result
         * @param arrIn
         * @return
         */
    private ArrayList<AIOrders> getTopInPaths(ArrayList<AIOrders> arrIn) {

        ArrayList<Hex>[] arrPaths = getActivePathReset();
        ArrayList<AIOrders> arrReturn = new ArrayList<AIOrders>();
        ArrayList<AIOrders> arrTemp = new ArrayList<>();
        for (AIOrders aiO:arrIn){
            for (Hex hex:aiO.arrHexMoveTo){
                for (ArrayList<Hex> arr:arrPaths){
                    if (arr.indexOf(hex) == 0){
                        aiO.scoreMain *=.7f;  // decrease
                    }
                }
            }
        }
        int i=0;
        for (AIOrders aiO:arrIn){
            for (i=0; i< arrReturn.size(); i++){;
                if (aiO.scoreMain < arrReturn.get(i).scoreMain){
                    break;
                }
            }
            arrReturn.add(i,aiO);
        }
        Gdx.app.log("AIMover", "After GetTopInPath");
        AIOrders.display(arrReturn);

        return arrReturn;

    }

    /**
     *  Handle processing after scoring
     * @param type
     * @param arrScored
     */
    public void doNext(AIScorer.Type type, ArrayList<AIOrders> arrScored) {
        if (NextPhase.instance.getTurn() < 3){ // first reinforcements
            if (type == AIScorer.Type.GermanPenetration) {
                endPenetrationAnalysis(arrScored);
            }else if (type == AIScorer.Type.GermanRegular){
                AIScenario1.instance.endTurn1to3(arrScored);
            }
        }
    }

    /**
     * Start the scoring process
     * use as input the arrIn
     * @param arrIN
     * @param type
     */
    public void setArrToBeScored(ArrayList<AIOrders> arrIN, AIScorer.Type type) {
        Gdx.app.log("AIScenar1", "setArrToBeScored");
        ArrayList<AIOrders>  arrToBeScored = new ArrayList<>();
        arrToBeScored.addAll(arrIN);
        ArrayList<Unit> arrUnitFake = new ArrayList<>();
        arrUnitFake.addAll((Unit.getOnBoardAxis()));
        AIFaker.instance.setUnits(arrUnitFake);
        AIFaker.instance.startScoringOrders(arrToBeScored, type, isAllies);
    }
}
