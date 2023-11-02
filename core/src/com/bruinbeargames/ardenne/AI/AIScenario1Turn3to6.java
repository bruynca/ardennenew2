package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 *  For Scenario1 after turn 3
 *  Do analysis check Bastogne first, then wiltz to see which we can attack
 *  if Turn is not 3 or 4  check supply as it will affect German Units for game turn 6
 */
public class AIScenario1Turn3to6 implements Observer {
    static public AIScenario1Turn3to6 instance;
    ArrayList<Unit>[] arrUnitKampgruppe;
    /**
     *  limit hexes to search so iterations dont go through the roof
     */
    ArrayList<Hex> arrHexLimit = new ArrayList<>();
    ArrayList<Hex> arrEnemySurround;
    ArrayList<Hex>[] arrMoves;
    ArrayList<AIOrders>[] arrOrders;
    int ixWorkingOn = 0;
    int cntGroups = 0;
    AIScorer.Type type;

    AIReinforcementScenario1.BastogneWiltzDefenseStatus bastogneWiltzDefenseStatus;
    AIScenario1Turn3to6(){
        instance = this;
    }

    /**
     * Get all the units
     * Get all Artillery that can move
     * Get Strategy
     * Divide units up into groups(4 max) that we can iterate through
     * Do the iterations
     * at the end d the movement
     */
    public void doAlliesMove() {
        /**
         *  get all units tht have not moved
         */
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
        arrEnemySurround = new ArrayList<>();
        for (Unit unit:Unit.getOnBoardAxis()){
            arrEnemySurround.addAll(unit.getHexOccupy().getSurround());
        }


        /**
         *  divide number of units into groups of 4
         *   resulting in 1 to n groups
         */
        // y = x/3
        int div = arrUnits.size()/3 ;
        cntGroups = div; // divide into groups of 3
        if ((cntGroups *  3) !=  arrUnits.size()){
            cntGroups++;
        }
        arrUnitKampgruppe = new ArrayList[cntGroups];
        arrOrders = new ArrayList[cntGroups];
        for (int i=0; i<cntGroups;i++){;
            arrUnitKampgruppe[i] = new ArrayList<>();
        }
        bastogneWiltzDefenseStatus =  new AIReinforcementScenario1.BastogneWiltzDefenseStatus(arrUnits,arrMoves);

        int ix =0;
        while (arrUnits.size() > 0) {
            arrUnitKampgruppe[ix].add(arrUnits.get(0));
            arrUnits.remove(0);
            ix++;
            if (ix == cntGroups) {
                ix = 0;
            }
        }
        arrHexLimit = limitHex(bastogneWiltzDefenseStatus);
        ixWorkingOn = 0;
        if (arrUnitKampgruppe.length > 0){
            doGroup(ixWorkingOn);
        }else{
            doFinal();
        }
    }

    /**
     *  Do Next group of units
     */
    public void doNextProcess() {
        Gdx.app.log("AIScenario1Turn3to6", "doDone for"+ixWorkingOn);

        ixWorkingOn++;
        if (ixWorkingOn < cntGroups){
            doGroup(ixWorkingOn);
        }else{
            doFinal();
        }
    }

    private void doDoneForGroup(int ixWorkingOn) {
        Gdx.app.log("AIScenario1Turn3to6", "doDone for"+ixWorkingOn);
        int i = 0;
        Collections.sort(arrOrders[ixWorkingOn], new AIOrders.SortbyScoreDescending());
        ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrOrders[ixWorkingOn], .1f);
        arrOrders[ixWorkingOn].clear();
        arrOrders[ixWorkingOn].addAll(arrTop);
        doNextProcess();
    }

    /**
     *  final combine all the groups  and optimize for combat
     */
    private void doFinal() {
        /**
         * combine aiorders into the top one
         */
        Gdx.app.log("AIScenario1Turn3to6", "doFinal");
        AIOrders aiFinal;
        if (arrOrders.length == 0){
            EventAI.instance.hide();
            NextPhase.instance.nextPhase();
            return;
        }
        if (arrOrders.length == 1){
            aiFinal = arrOrders[0].get(0);
        }else{
            aiFinal = AIOrders.combine(arrOrders[0].get(0), arrOrders[1].get(0), true);
            if (arrOrders.length > 2 && arrOrders[2].size() > 0) {
                AIOrders temp = AIOrders.combine(aiFinal, arrOrders[2].get(0), true);
                aiFinal = temp;
                if (arrOrders.length > 3 && arrOrders[3].size() > 0) {
                    AIOrders temp2 = AIOrders.combine(aiFinal, arrOrders[3].get(0), true);
                    aiFinal = temp2;
                }
            }
        }
        /**
         * Hand over to mover
         * for now use top of last orders.
         */
        AIOrders aiUse = aiFinal;
        /**
         *  let see how scorere handles this
         */

        ArrayList<Unit> arrUnits = null;
        if (arrUnits == null){
            arrUnits = new ArrayList<>();
            arrUnits.addAll((Unit.getOnBoardAxis()));
        }
        Hex.fakeClearMoveFields(false,true,2);
        AIFaker.setFakeOccupied(aiUse, true, 2);
        AIFaker.setFakeZoc(aiUse, 2);
        int score = AIScorer.instance.getScore(type, arrUnits, aiUse, 2);
        int ix=0;
        ArrayList<Unit> arrREmove  = new ArrayList<>();
        int oneInEttBruck = 0;
        for (Unit unit:aiUse.arrUnit){
            if (unit.getHexOccupy() == AIReinforcementScenario1.hexWiltz){
                arrREmove.add(unit);
            }
            if (unit.getHexOccupy() == Hex.hexEttlebruck && oneInEttBruck == 0){
                arrREmove.add(unit);
                oneInEttBruck++;
            }
            ix++;
        }
        aiUse.remove(arrREmove);

        AIMover.instance.execute(aiUse);
    }

    /**
     *  do the Group for iterations
     * @param ixWorkingOn
     */
    private void doGroup(int ixWorkingOn) {
        Gdx.app.log("AISceanrio1Turn3to6", "Do Group working on="+ixWorkingOn);

        ArrayList<Unit> arrUnitsWork = arrUnitKampgruppe[ixWorkingOn];
        if (arrUnitsWork.contains(Unit.getUnitByID(69))){
            int b=0;
        }
        arrOrders[ixWorkingOn] = new ArrayList<>();
        ArrayList<AIOrders> arrAIorders = arrOrders[ixWorkingOn];
        Gdx.app.log("AISceanrio1Turn3to6", "Units cnt="+arrUnitsWork.size());
        ArrayList<Hex>[] arrHexMove = new ArrayList[arrUnitKampgruppe[ixWorkingOn].size()];
        arrHexMove = AIUtil.getUnitsMaxMove(arrUnitsWork,0, false);
        /**
         *  make sure no units move out of wiltz
         */
        /**
         *  reduce movement to hexes to reduce iterations
         */
        int ix =0;
        for (ArrayList<Hex> arr:arrHexMove){
            arr.retainAll(arrHexLimit);
            if (arr.size() == 0){
                arr.add(arrUnitsWork.get(ix).getHexOccupy()); // leave in hex
            }
            ix++;
        }
        /**
         *  get the aiOrders
         */
        ArrayList<AIOrders> arrStart = AIUtil.GetIterations(arrUnitsWork, arrHexMove);
        Gdx.app.log("AISceanrio1Turn3to6", "Aiorders cnt="+arrStart.size());
        /**
         *  set allow duplicate based on strategy
         */
        Gdx.app.log("AISceanrio1Turn3to6", "Strategy="+bastogneWiltzDefenseStatus.strategy);

        ArrayList<Hex> arrAllowDuplicates = new ArrayList<>();
        switch (bastogneWiltzDefenseStatus.strategy) {
            case BastogneAttack:
            case BastognePart:
                type = AIScorer.Type.AttackBastogne;
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogne);
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogneRing);
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogneOuterDefense);
                break;
            case WiltzAttack:
            case WiltzFree:
                type = AIScorer.Type.AttackWiltz;
                arrAllowDuplicates.add(AIReinforcementScenario1.hexWiltz);
                arrAllowDuplicates.addAll(AIReinforcementScenario1.hexWiltz.getSurround());
                break;
            default:
                type = AIScorer.Type.GermanPenetration;
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogne);
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogneRing);
                arrAllowDuplicates.addAll(AIReinforcementScenario1.arrBastogneOuterDefense);
                break;


        }
        Gdx.app.log("AISceanrio1Turn3to6", "dupes allowed="+arrAllowDuplicates);
        ArrayList<AIOrders> arrNodupes = AIOrders.removeDupeMoveToHexes(arrStart,arrAllowDuplicates);
        arrAIorders = AIOrders.removeOverstack(arrNodupes);
        if (arrAIorders.size() == 0){
            arrAIorders.addAll(arrNodupes);
        }
        arrOrders[ixWorkingOn] = arrAIorders;
        Gdx.app.log("AISceanrio1Turn3to6", "Aiorders count after dupe check="+arrAIorders.size());
        for (AIOrders aiO:arrAIorders){
            aiO.clearMOA();
        }
        AIFaker.instance.addObserver(this);
        AIFaker.instance.startScoringOrders(arrAIorders, type, true);
    }

    /**
     *  based on the strartegy get the limit on hexes
     * @param bastogneWiltzDefenseStatus
     * @return
     */
    private ArrayList<Hex> limitHex(AIReinforcementScenario1.BastogneWiltzDefenseStatus bastogneWiltzDefenseStatus) {
        ArrayList<Hex> arrReturn  = new ArrayList<>();
        arrReturn.addAll(Supply.instance.getGermanBottlenecks());
        arrReturn.addAll(Hex.getAllTownsCities());
        arrReturn.addAll(arrEnemySurround);
        switch (bastogneWiltzDefenseStatus.strategy){
            case BastogneFree:
                arrReturn.addAll(AIReinforcementScenario1.arrBastogne);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneRing);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneOuterDefense);
                break;
            case WiltzFree:
                arrReturn.add(AIReinforcementScenario1.hexWiltz);
                arrReturn.addAll(AIReinforcementScenario1.hexWiltz.getSurround());
                break;
            case BastogneAttack:
            case BastognePart:
                arrReturn.addAll(AIReinforcementScenario1.arrBastogne);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneRing);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneOuterDefense);
                break;
            case WiltzAttack:
                arrReturn.add(AIReinforcementScenario1.hexWiltz);
                arrReturn.addAll(AIReinforcementScenario1.hexWiltz.getSurround());
                break;

            default:
                arrReturn.addAll(AIReinforcementScenario1.arrBastogne);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneRing);
                arrReturn.addAll(AIReinforcementScenario1.arrBastogneOuterDefense);
                arrReturn.add(AIReinforcementScenario1.hexWiltz);
                arrReturn.addAll(AIReinforcementScenario1.hexWiltz.getSurround());
                break;
        }
        AIUtil.RemoveDuplicateHex(arrReturn);

        return arrReturn;

    }

    @Override
    public void update(Observable o, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakeScenario1Done) {
            AIFaker.instance.deleteObserver(this);
            doDoneForGroup(ixWorkingOn);
        }


    }
}
