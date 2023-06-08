package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.HelpPage;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static com.badlogic.gdx.Gdx.app;

public class AdvanceAfterCombat implements Observer {
    static public AdvanceAfterCombat instance;
    public AdvanceAfterCombat(){
        instance = this;
    }
    Attack attack;
    int numHexAdvance;
    boolean isSeen = false;
    ArrayList<Hex> arrHexOK = new ArrayList<>();
    ArrayList<Hex> arrHexAdvance = new ArrayList<>();
    ArrayList<Unit> arrUnitsToAdvance =  new ArrayList<>();
    ArrayList<Unit> arrSanity = new ArrayList<>();

    /**
     * got here because hex was vacated
     * @param attack
     */
    public void doAdvance(Attack attack) {
        app.log("AdvanceAfterCombat", "doAdvance=" + attack.hexTarget);
        if (!isSeen){
            isSeen = true;
  //          HelpPage.instance.showOther("advanceafterhelp");
        }
        this.attack = attack;
        numHexAdvance = attack.defenderRetreats;
        arrHexOK.clear();
        ArrayList<Hex> arrHexCheck = new ArrayList<>();
        for (Unit unit:attack.arrAttackers){
            if (!unit.isEliminated()) {
                ClickAction clickAction = new ClickAction(unit, ClickAction.TypeAction.Advance);
                arrHexOK.add(unit.getHexOccupy());
                arrSanity.add(unit);
                if (!arrHexCheck.contains(unit.getHexOccupy())){
                    arrHexCheck.add(unit.getHexOccupy());
                }
            }

        }
        /**
         * shade units that did not attack
         */
        for (Hex hex:arrHexCheck){
            for (Unit unit:hex.getUnitsInHex()){
                if (!attack.arrAttackers.contains(unit)){
                    unit.getMapCounter().getCounterStack().shade();
                }
            }
        }

        arrHexOK.add(attack.hexTarget);
        ardenne.instance.addObserver(this);
        arrHexAdvance = calculateAdvancePossible(attack.hexTarget);
        arrUnitsToAdvance.clear();
        arrUnitsToAdvance.addAll(attack.arrAttackers);
        HelpPage.instance.showOther("advance");
        if (arrSanity.size() == 0){
            end();
        }
    }
    public void end(){
        app.log("AdvanceAfterCombat", "end");

        ClickAction.cancelAll();
        ardenne.instance.deleteObserver(this);
        Combat.instance.doCombatPhase();
    }

    /**
     *  is this the end of all possible advances for this combat
     */
    public void checkEnd(Unit unit){
        app.log("advanceAfterCombat", "checkend unit="+unit);

        arrUnitsToAdvance.remove(unit);
        if (arrUnitsToAdvance.size() == 0 || arrSanity.size() == 0){
            end();
        }
    }

    /**
     * max advance is 2 hexes for this game Get possible hexes
     * @param unit
     */
    public ArrayList<Hex> getPossible(Unit unit){
        app.log("advanceAfterCombat", "getPossible unit="+unit);

        ArrayList<Hex> arrReturn = new ArrayList<>();
        for (Hex hex:arrHexAdvance){
            if (hex.canOccupy(unit)){
                arrReturn.add(hex);
            }
        }
        return arrReturn;
    }
    public void addToOK(ArrayList<Hex> arrHex){
        arrHexOK.addAll(arrHex);
    }


    /**
     * is it still possible to advance because of stacking
     */
    public void checkStillPossible(){
        app.log("advanceAfterCombat", "checkstillPossible ");


        for (Unit unit:arrUnitsToAdvance){
            ArrayList<Hex> arrHex = getPossible(unit);
            if (arrHex.size() > 0){
                return; // still possible
            }
        }
        //not possible
        end();
    }

    /**
     * Calculate possible advances
     *  stacking is not checked here
     * @param hexTarget
     * @return possible hexes.
     */
    private ArrayList<Hex> calculateAdvancePossible(Hex hexTarget) {
        app.log("advanceAfterCombat", "caclulateAdvancePossible");

        ArrayList<Hex> arrReturn = new ArrayList<>();
        arrReturn.add(hexTarget);
        ArrayList arrHexAttackers = new ArrayList();
        for (Unit unit:attack.arrAttackers){
            if (!arrHexAttackers.contains(unit.getHexOccupy())){
                arrHexAttackers.add(unit.getHexOccupy());
            }
        }

        if (numHexAdvance > 1 ){
            for (Hex hex:hexTarget.getSurround()){
                if (hex.getUnitsInHex().size() == 0){
                    arrReturn.add(hex);
                }else{
                    if ((hex.checkAlliesInHex() && !attack.isAllies) ||(hex.checkAxisInHex() && attack.isAllies)){
                        // do nothing
                    }else{
                        if (arrHexAttackers.contains(hex)){
                            // do nothing
                        }else{
                            arrReturn.add(hex); // need to check stack
                        }
                    }
                }
            }
        }
        return arrReturn;
    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        Hex hex = oB.hex;
        /**
         *  Hex touched
         */
        if (oB.type != ObserverPackage.Type.TouchUp) {
            return;
        }
        if (EventPopUp.instance.isShowing()){
            return;
        }
        app.log("AdvanceAfterCombat","observer update hex="+hex);

        if (arrHexOK.contains(hex)) {
            return;
        }else{
            end();
        }
    }


}
