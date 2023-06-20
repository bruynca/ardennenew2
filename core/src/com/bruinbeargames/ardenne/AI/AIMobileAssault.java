package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.MobileAssualt;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitHexes;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;

public class AIMobileAssault {
    private static ArrayList<AIMobileAssault> arrMobileAssults = new ArrayList<>();
    private static ArrayList<Unit> arrUnits = new ArrayList<>();
    /**
     *  contains Unit and all Hex that it can go to after MOA
     */
    private static ArrayList<UnitHexes> arrUnitHexes = new ArrayList<>();
    final static int  oddGreaterThan = 3 ;
    Unit unit;
    final int greaterThanAttackFactor = 3;
    Attack attackDisplay;
    private boolean isOK = false;
    ArrayList<Hex> arrHex = new ArrayList<>();
    ArrayList<ArrayList<Hex>> arrArrAfterMOA = new ArrayList<>();

    /**
     *  get moves for units
     *   for each MOA having odds greater or equal to 3 get the hexes it can go after
     *   make sure hexes that it can go after are not in original move group
     *   if none found OK will be false;
     * @param unit
     * @param  hexStart if reinforcement
     */
    private AIMobileAssault(Unit unit, Hex hexStart){
        this.unit = unit;
        if (unit.isArtillery) {
            isOK = false;
        }else {
            if ((unit.getHexOccupy().isAxisZOC() && unit.isAllies) || (unit.getHexOccupy().isAlliedZOC() && unit.isAxis)) {
                isOK = false;
            } else {
                UnitMove unitMove = null;
                if (hexStart == null) {
                    unitMove = new UnitMove(unit, unit.getCurrentMovement(), true, true, 0);
                }else{
                    unitMove = new UnitMove(unit, unit.getCurrentMovement(), true, true,hexStart, 0);
                }
                MobileAssualt.instance.doMobileInitialAssualtSetUp(unitMove.getMovePossible(),unit,true);
                ArrayList<Hex> arrMove = unitMove.getMovePossible();
                for (Hex hex : arrMove) {
                    if ((hex.checkAlliesInHex() && unit.isAxis) || (hex.checkAxisInHex() && unit.isAllies)) {
                        if (unit.getCurrenAttackFactor() > 3) {
                            attackDisplay = new Attack(hex,unit.isAllies,true,true,unit);
                            attackDisplay.addAttacker(unit, true);
                            if (Character.getNumericValue(attackDisplay.getAttackOdd().charAt(0)) >= greaterThanAttackFactor){
                                arrHex.add(hex);
                                /**
                                 *  find best from hex
                                 */
                                float moveCost = 0;
                                Hex hexCalc = hex; // sannity check
                                for (Hex hexSurround:hex.getSurround()){
                                    if (hexSurround.getCalcMoveCost(0) > moveCost){
                                        moveCost = hexSurround.getCalcMoveCost(0);
                                        hexCalc = hexSurround;
                                    }
                                }
                                int newMove = 0;
                                if (hex != hexCalc){
                                    newMove = MobileAssualt.instance.getMoveCostAfterMOA(unit,hexCalc,hex);
                                }
                               UnitMove unitMoveKeepGoing = new UnitMove(unit,newMove,true,true,hex,0);
                                ArrayList<Hex> arrWork = unitMoveKeepGoing.getMovePossible();
                                arrWork.removeAll(arrMove);
                                if (arrWork.size() > 0) {
                                    arrArrAfterMOA.add(arrWork);
                                }
                            }
                        }
                    }
                }
                if (arrArrAfterMOA.size() > 0) {
                    isOK = true;
                }
            }

        }
    }

    /**
     *  get the array of mobile assualts
     *  this is called after createfor units has been called
     * @return
     */
    public static ArrayList<AIMobileAssault> getAssualt() {
        ArrayList<AIMobileAssault> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrMobileAssults);
        return arrReturn ;
    }

    /**
     *  get all hexes for unit after initial MOA
     * @param unit
     * @return
     */
    public static ArrayList<Hex> getHexAfter(Unit unit) {
        for (UnitHexes uhs:arrUnitHexes){
            if (uhs.unit == unit ){
               return uhs.arrHexes;
            }
        }
        return null;
    }

    public static Hex getHexWhereMOAStarted(Unit unit, Hex hexCheck) {
        ArrayList<Hex> arrHex = getHexAfter(unit);
        if (arrHex == null){
            return null;
        }
        if (!arrHex.contains(hexCheck)){
            return null;
        }
        /**
         *  get MOA for unit
         *  get Array which contains hex
         *  return where MOA happens ;
         */
        for (AIMobileAssault aim:arrMobileAssults){
            if (unit == aim.unit){
                int ix=0;
                for (ArrayList arr: aim.arrArrAfterMOA){
                    if (arr.contains(hexCheck)){
                        return aim.arrHex.get(ix);
                    }
                }
            }
        }
        return null;
    }

    public boolean isOK() {
        return isOK;
    }

    /**
     *  create the array of mobile assalts
     * @param arrUnitsIn
     */
    static void createArrays(ArrayList<Unit> arrUnitsIn, ArrayList<Hex> arrPosition){
        arrMobileAssults.clear();
        ArrayList<Hex> arrHexReachAfterMOA = new ArrayList<>();
        int ix=0;
        for (Unit unit:arrUnitsIn){
            if (!unit.isArtillery && unit.getCurrenAttackFactor() > 5){
                Hex hexPosition= null;
                if (arrPosition != null){
                    hexPosition = arrPosition.get(ix);
                }
                AIMobileAssault aiMobileAssault = new AIMobileAssault(unit, hexPosition);
                if (aiMobileAssault.isOK) {
                    arrMobileAssults.add(aiMobileAssault);
                    arrUnits.add(unit);
                    for (ArrayList<Hex> arrHex : aiMobileAssault.arrArrAfterMOA) {
                        arrHexReachAfterMOA.addAll(arrHex);
                    }
                    UnitHexes unitHexs = new UnitHexes(unit, arrHexReachAfterMOA);
                    arrUnitHexes.add(unitHexs);
                }
            }
            ix++;
        }
        MobileAssualt.instance.endMOA(); // turn off MOA display
    }


}

