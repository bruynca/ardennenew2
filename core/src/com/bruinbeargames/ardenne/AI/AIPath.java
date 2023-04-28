package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitHex;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 *  find the easiest path to a destination
 *   determine which enemy to be eliminated to get there
 *   used for ai mobile assualts and explotation movement
 *   AIORDERS is used as per
 *
 *     The arrhex  To will be at hex 0,0 to show that the corresponding unit can be
 *     eliminated to get a breakthrou to the target hexes
 *     The best solutions are where there is ony 1 hex and therefore only 1 unit
 *     that needs to be eliminated
 */
public class AIPath extends Observable implements Observer  {
    static Hex hexNone = Hex.hexTable[0][0];
    ArrayList<Hex> arrTargets = new ArrayList<>();
    ArrayList<UnitHex> arrUnits = new ArrayList<UnitHex>();
    boolean isAllies;
    int thread = 1;;
    ArrayList<AIOrders> arrPathOrders;

    /**
     *  find blocking units to the objective
     * @param arrHexIn  Objective
     * @param isAllies
     * @param arrUnitsIn Units and Hexes that will try to reach objective
     * @param thread thread to use   overriden by 1
     */
    AIPath(ArrayList<Hex> arrHexIn, boolean isAllies, ArrayList<UnitHex> arrUnitsIn, int thread) {
        arrTargets.addAll(arrHexIn);
        arrUnits.addAll(arrUnitsIn);
        this.isAllies = isAllies;
        ArrayList<UnitHex> arrCanMakeIt = new ArrayList<>();
        Hex.fakeClearMoveFields(false, true, thread);
        ArrayList<Hex> arrAllMoves = new ArrayList<>();
        /**
         *  get units that can make objectives
         */
        for (UnitHex uh : arrUnitsIn) {
            UnitMove unitMove = new UnitMove(uh.unit, uh.unit.getCurrentMovement(), true, true, uh.hex, 1);
            arrAllMoves.addAll(unitMove.getMovePossible(thread));
            ArrayList<Hex> arrTemp = new ArrayList<>();
            for (Hex hex : arrTargets) {
                if (unitMove.getMovePossible(1).contains(hex)) {
                    arrCanMakeIt.add(uh);
                    break;
                }
            }
        }
        /**
         *  get an area between unit/hexes and objectives  with a buffer
         *   to cut down the number of enemy to check
         */
        AIUtil.RemoveDuplicateHex(arrAllMoves);
        ArrayList<Unit> arrDefenders = new ArrayList<>();
        if (isAllies) {
            for (Unit unit : Unit.getOnBoardAxis()) {
                if (arrAllMoves.contains(unit.getHexOccupy())) {
                    arrDefenders.add(unit);
                }
            }
        } else {
            for (Unit unit : Unit.getOnBoardAllied()) {
                if (arrAllMoves.contains(unit.getHexOccupy())) {
                    arrDefenders.add(unit);
                }
            }
        }
        /**
         *  create array for Iterations
         */
        ArrayList<Hex>[] arrArr = new ArrayList[arrDefenders.size()];
        int ix = 0;
        for (Unit unit : arrDefenders) {
            ArrayList<Hex> arrWork = new ArrayList<>();
            arrWork.add(hexNone);
            arrWork.add(unit.getHexOccupy());
            arrArr[ix] = arrWork;
            ix++;
        }
        arrPathOrders = AIUtil.GetIterations(arrDefenders, arrArr);
        /**
         *  score
         */
        AIFaker.instance.addObserver(this);
        AIFaker.instance.setUnitHex(arrCanMakeIt);
        AIScorer.instance.setTarget(arrTargets);
        AIFaker.instance.startScoringOrders(arrPathOrders, AIScorer.Type.AIPath, isAllies);
   /*     Hex.fakeClearMoveFields(false, true, thread);
        for (AIOrders aiO : arrOrders) {
            AIFaker.setFakeAxisOccupied(aiO, true, thread); // on thread fake
            AIFaker.setFakeZoc(aiO, thread); // on thread fake
            int score = 0;
                score=0;
            for (UnitHex uh : arrCanMakeIt) {
                UnitMove unitMove = new UnitMove(uh.unit, uh.unit.getCurrentMovement(), true, true, uh.hex, 1);
                for (Hex hex : arrTargets) {
                    if (unitMove.getMovePossible(1).contains(hex)) {
                        if (!aiO.arrUnitsTemp.contains(uh.unit)){
                            aiO.arrUnitsTemp.add(uh.unit);
                        }
                        for (Hex hex2: aiO.arrHexMoveTo){
                            if (hex2 != hexNone) {
                                score++;
                            }
                        }
                    }
                }
            }
            aiO.setScoreMain(score);
            // do scoring
            AIFaker.setFakeAxisOccupied(aiO, false, thread); // on thread fake
            AIFaker.resetZOC(aiO, thread);
        } */
    }

    @Override
    public void update(Observable o, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakeDone) {
            AIFaker.instance.deleteObserver(this);
            Collections.sort(arrPathOrders, new AIOrders.SortbyScoreDescending());
            setChanged();
//            notifyObservers(new ObserverPackage(ObserverPackage.Type.AIPathDone, null, 0, 0));
            int bk=0;
        }


    }
}
