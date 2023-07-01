package com.bruinbeargames.ardenne.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.AdvanceAfterCombat;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameLogic.MobileAssualt;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventConfirm;
import com.bruinbeargames.ardenne.UI.EventPopUp;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.input;

public class ClickAction implements Observer {
    Unit unit;
    public TypeAction typeAction;
    static ArrayList<ClickAction> arrClickAction = new ArrayList<>();
    static Unit unitInProcess;
    static Hex hexProcess;
    static boolean isLocked = false;
    private HiliteHex hiliteHex;
    private I18NBundle i18NBundle;


    public ClickAction(Unit unit, TypeAction type) {

        this.unit = unit;
        typeAction = type;
        unit.getMapCounter().addClickAction(this);
        arrClickAction.add(this);
        unitInProcess = null; // when new clickactions added
    }

    /**
     *  USE FO AI
     * @param unit
     * @param type
     * @param b
     */
    public ClickAction(Unit unit, TypeAction type, boolean b) {
        this.unit = unit;
        typeAction = type;
//        unit.getMapCounter().addClickAction(this);
        arrClickAction.add(this);
        unitInProcess = null; // when new clickactions added

    }
    public Unit getUnit(){
        return unit;
    }

    public static void clearClickListeners(){
        arrClickAction.clear();
    }


    public static void unLock() {
        isLocked = false;
        unitInProcess = null;
    }


    public void click() {
        app.log("ClickAction","Click unit="+unit+unit.ID);
        if (isLocked){
            return;
        }
        if (unitInProcess != null){
            if (unitInProcess.getMapCounter() != null){
                if (unitInProcess.getMapCounter().clickAction != null){
                    unitInProcess.getMapCounter().clickAction.cancel();
                    unitInProcess = null;
                        return;
                }else{
                    unitInProcess = null;
                    return; // click action still there
                }
            }else{
                int brk = 0;
            }

        }else{
            int brk =0;
        }
 //       if (hexProcess != null && hexProcess == unit.getHexOccupy()){
//            app.log("ClickAction", "Hex Used");
//            hexProcess = null;
//            return;
//        }
        unitInProcess = unit;

        switch(typeAction){

            case Move:
                app.log("ClickAction", "move clicked on unit" + unit);
                /**
                 * check if  any Moa are still active
                 *  if yes and this is another click
                 *  then end them
                 */
                moveSetup(unit);
                break;
            case Limber:
                app.log("ClickAction", "Limber clicked on unit" + unit);
 //               unit.getMapCounter().counterStack.hilite();
                if (unit.isLimbered()){
                    unit.setArtilleryUnLimbered();
                }else{
                    unit.setArtilleryLimbered();
                }
                unit.getMapCounter().getCounterStack().setPoints();
                Counter.rePlace(unit.getHexOccupy());
                unitInProcess = null;
                break;
            case CombatClick:
                Gdx.app.log("ClickAction", "Combat clicked on unit" + unit);
                unitInProcess = null; //Combat does not extend beyond click
                if (unit.getMapCounter().getCounterStack().isHilited()){
                    unit.getMapCounter().getCounterStack().removeHilite();
                    Combat.instance.removeUnit(unit);
                    return;
                }
                /**
                 *  Check for all units in stack
                 */
                if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) || input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
                    for (Unit unitCheck:unit.getHexOccupy().getUnitsInHex()){
                        if (unitCheck.canAttackThisTurn){
                            unitCheck.getMapCounter().getCounterStack().hilite();
                            Combat.instance.addUnit(unitCheck);
                        }
                    }
                    return;
                }else{
                    unit.getMapCounter().getCounterStack().hilite();
                    Combat.instance.addUnit(unit);
                }
              break;
            case Advance:
                app.log("ClickAction", "Advance clicked on unit" + unit);
                /**
                 */
                unit.getMapCounter().counterStack.hilite();
                ArrayList<Hex> arrHexWork = AdvanceAfterCombat.instance.getPossible(unit);
                if (arrHexWork.size()> 0) {
                    hiliteHex = new HiliteHex(AdvanceAfterCombat.instance.getPossible(unit), HiliteHex.TypeHilite.Advance, this);
                    AdvanceAfterCombat.instance.addToOK(arrHexWork);
                }else{
                    AdvanceAfterCombat.instance.checkStillPossible();
                }
                break;
            case Supply:
                Gdx.app.log("ClickAction", "Supply clicked on unit" + unit);
                Supply.instance.cancelTransport(unit);
            case SelectDelete:
                Gdx.app.log("ClickAction", "Select clicked on unit" + unit);
//                SecondPanzerLoses.instance.select(unit);
            default:
                break;
        }
    }
    public void cancel(){
        unit.getMapCounter().counterStack.removeHilite();
        unitInProcess = null;
        switch(typeAction){
            case Move:
                if (hiliteHex != null) {
                    hiliteHex.remove();
                }
                break;
            case Advance:
                if (hiliteHex != null) {
                    hiliteHex.remove();
                }
                break;

            default:
                break;

        }
    }
    static public void cancelAll(){
        Gdx.app.log("ClickAction", "cancelAll");

        for (ClickAction clickAction:arrClickAction){
            if (!clickAction.unit.isEliminated()) {
                if (clickAction.hiliteHex != null) {
                    clickAction.hiliteHex.remove();
                }
                if (clickAction.unit.getMapCounter() != null) {
                    clickAction.unit.getMapCounter().removeClickAction();
                }

            }
       }

        unitInProcess = null;

    }
    private Unit unitConfirm;
    private Hex  hexConfirm;
    public void process(Hex hex, boolean isAI) {
        switch(typeAction){
            case Move:
                app.log("ClickAction", "process Move " + unit+" toHex="+hex);
                /**
                 *  Give warning for overstacking
                 */
                int stackPossible = hex.getStacksIn();
                stackPossible +=unit.getCurrentStep();
                if (stackPossible >Hex.stackMax && !isAI){
                    hexConfirm = hex;
                    unitConfirm = unit;
                    i18NBundle = GameMenuLoader.instance.localization;
                    EventPopUp.instance.show(i18NBundle.format("overstacking2"));
  //                  EventConfirm.instance.addObserver(this);
  //                  EventConfirm.instance.show(i18NBundle.format("overstacking"));
                }else{
                    moveUnit(unit, hex, isAI);
                }
                break;
            case Advance:
                app.log("ClickAction", "process Advance=" + unit+" toHex="+hex);
                unit.getMapCounter().getCounterStack().removeHilite();
                unit.getMapCounter().removeClickAction();
                hiliteHex.remove();
                Move.instance.moveUnitAfterAdvance(unit, hex);
                hexProcess = hex;
                AdvanceAfterCombat.instance.checkEnd(unit);
                break;
            default:
                break;
        }

    }
    private void moveUnit(Unit unit, Hex hex, boolean isAI){
        isLocked = true;
        unit.getMapCounter().getCounterStack().removeHilite();
        unit.getMapCounter().removeClickAction();
        hiliteHex.remove();
        Move.instance.moveUnitFromClick(unit, hex, isAI);
        hexProcess = hex;

    }
    public void moveSetup(Unit unit){
        unit.getMapCounter().counterStack.hilite();

        if (!Move.instance.getArrUnitsInMoa().contains(unit)){
            for (Unit unitMOA:Move.instance.getArrUnitsInMoa()){
                app.log("ClickAction", "unitMOA=" + unitMOA);
                if (unitMOA.getMapCounter() != null) {
                    unitMOA.getMapCounter().counterStack.shade();
                    unitMOA.getMapCounter().removeClickAction();
                }else{
                    app.log("ClickAction", "unitMOA Null=" + unitMOA);

                }
            }
        }
        UnitMove unitMove;
        if (!unit.isArtillery){
            unitMove = new UnitMove(unit,unit.getCurrentMovement(),true,true,0);
        }else{
            unitMove = new UnitMove(unit,unit.getCurrentMovement(),false,true,0);

        }
        ArrayList<Hex> arrHexMove = new ArrayList<>();
        arrHexMove.addAll(unitMove.getMovePossible());
        ArrayList<Hex> arrHexMobileAssualt = new ArrayList<>();
        for (Hex hex:unitMove.getMovePossible()){
            if ((hex.checkAlliesInHex() && unit.isAxis) || (hex.checkAxisInHex() && unit.isAllies)){
                if (unit.getCurrenAttackFactor() > 0) {
                    arrHexMobileAssualt.add(hex);
                    arrHexMove.remove(hex);
                }
            }
        }
        if (arrHexMobileAssualt.size() > 0){
            MobileAssualt.instance.doMobileInitialAssualtSetUp(arrHexMobileAssualt,unit, false);
        }
        arrHexMove.remove(unit.getHexOccupy());
        if (!unit.isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            arrHexMove.remove(Hex.hexTable[0][19]);
            arrHexMove.remove(Hex.hexTable[9][24]);
            arrHexMove.remove(Hex.hexTable[27][24]);
        }
        hiliteHex = new HiliteHex(arrHexMove, HiliteHex.TypeHilite.Move, this);  }
    public static int getClickActionsLeft(){
        return arrClickAction.size();
    }
    public static ArrayList<ClickAction> getClickActions(){
        return arrClickAction;
    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes kick off processing for that type
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.ConfirmYes){
            EventConfirm.instance.deleteObserver(this);
            moveUnit(unitConfirm,hexConfirm, false);
            return;

        }else{
            EventConfirm.instance.deleteObserver(this);
            return;
        }
    }

    public enum TypeAction {Move, Limber, CombatClick, Supply, Advance,SelectDelete};

}
