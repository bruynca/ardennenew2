package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.CombatDisplay;
import com.bruinbeargames.ardenne.UI.HelpPage;
import com.bruinbeargames.ardenne.UI.WinStack;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Handle bot all Mobile assualts and
 * Individual assualts
 */
public class MobileAssualt implements Observer {
    static public MobileAssualt instance;
    boolean isSeen = false;
    ArrayList<Hex> arrHexMobile = new ArrayList<>();
    ArrayList<Stack> arrStack = new ArrayList<>();
    ArrayList<Unit> arrUnitsToMove = new ArrayList<>();
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");

    TextureRegion combat =textureAtlas.findRegion("mobileassualt");
//    FileHandle fichier = Gdx.files.external("moa.png");
    Unit unitMOA;
    ArrayList<Unit> arrUnits = new ArrayList<>();
    boolean isAI;
    boolean isAllies;
    boolean isStack;
    Attack attack;
    Attack attackDisplay;
    Hex hexAttackFrom;
    Hex hexAttack;
    WinStack winStack;

    public MobileAssualt(){
        instance = this;
//        fichier.read();
    }
    public void doMobileInitialAssualtSetUp(ArrayList<Hex> arrHex, Unit unit, boolean isAI){
        Gdx.app.log("MobileAssualt","DoMobileAssualtSetup");

        /**
         * check MobileAssualt can not start in ZOC
         */
        if ((unit.isAxis && unit.getHexOccupy().getAlliedZoc(0))|| unit.isAllies && unit.getHexOccupy().getAxisZoc(0)){
            return;
        }
        arrHexMovePath = new ArrayList<>();
        this.unitMOA = unit;
        this.isAI = isAI;
        isAllies = unit.isAllies;
        for (Hex hex:arrHex){
            if (getAttackFromHex(hex) != null){
                arrHexMobile.add(hex);
            }
        }
  //      arrHexMobile.addAll(arrHex);

        for (Hex hex:arrHexMobile){
            if (!isAI) {
                createCombatImage(hex, false);
            }
        }
    }
    public void createCombatImage(final Hex hex, final boolean isListen){
        Gdx.app.log("Mobile Assualt", "createMobileImage");
        ardenne.instance.addObserver(this);
        Image image = new Image(combat);
        final Stack stack = new Stack();
        arrStack.add(stack);
        stack.add(image);
//        stack.setSize(163, 187);
        Vector2 pos = hex.getCounterPosition();
        stack.setPosition(pos.x - 30, pos.y - 10); //18
        final Hex hexMobileAssualt = hex;
        final Unit unitMobileAssualt = unitMOA;
        isStack = false;
        if (!isListen) {
            stack.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor stack) {
                    attackDisplay = new Attack(hexMobileAssualt,isAllies,true,isAI,unitMobileAssualt);
                    attackDisplay.addAttacker(unitMobileAssualt, false);
                }
                public void exit(InputEvent event, float x, float y, int pointer, Actor stack) {
                    if (!isStack) {
                        if (attackDisplay != null) {
                            attackDisplay.cancel();
                            attackDisplay = null;
                        }
                    }
                }
            });
        }
//		stack.addAction(Actions.sequence(Actions.fadeOut(0.05f), Actions.fadeIn(0.25f)));
        ardenne.instance.mapStage.addActor(stack);
    }
    @Override
    public void update(Observable observable, Object o) {
        Gdx.app.log("Mobile Assualt", "update on hex hit");

        ObserverPackage oB = (ObserverPackage) o;
        Hex hex =  oB.hex;
        /**
         *  Hex touched
         */
        if (oB.type == ObserverPackage.Type.TouchUpShift || oB.type == ObserverPackage.Type.TouchUp ||oB.type == ObserverPackage.Type.TouchUpMiddle) {
            if (arrHexMobile.contains(hex)) {
                attackDisplay.cancel();
                attackDisplay = null;
                hit(hex, oB, false);
            }else{
                endMOA();
            }
        }
    }
    public void updateFromStack(ArrayList<Unit> arrUnitsIn){
        arrUnits.clear();
        arrUnits.addAll(arrUnitsIn);
        attack = new Attack(hexAttack,isAllies,true,isAI, arrUnits.get(0));
        for (Unit unit:arrUnits){
            attack.addAttacker(unit, false);
        }
        endMOA();
        moveUnit(arrUnits.get(0),hexAttack);
    }

    /**
     * TouchDown on hex that has a mobile assualt
     * @param hex
     * @param oB
     */
    public void hit(Hex hex, ObserverPackage oB,boolean isAI){

        Gdx.app.log("MobileAssualt","hit");
        if (!isSeen){
            isSeen = true;
            HelpPage.instance.showOther("mobileassualthelp");
        }
        hexAttack =hex;
        arrUnits.clear();
        if (isAI ||oB.type == ObserverPackage.Type.TouchUp){
            arrUnits.addAll(getMOACanInStack(hex,unitMOA.getHexOccupy().getUnitsInHex()));
            if (arrUnits.size() == 1){
                attack = new Attack(hexAttack,isAllies,true,isAI, arrUnits.get(0));
                endMOA();
                moveUnit(arrUnits.get(0),hex);
            }else {
                winStack = new WinStack(arrUnits, hexAttack);
                endMOA();
                createCombatImage(hexAttack, false);
                isStack = true;
            }
        }else{
            ErrorGame errorGame = new ErrorGame("Mobile assualt Observer fault",this);
        }

        /**
             * move first Unit  other will be done later after move
             */
    }

    /**
     *  Get all the units that can MOA this hex in a stack
     * @return
     */
    public ArrayList<Unit> getMOACanInStack(Hex hex, ArrayList<Unit> arrUnitsMaybe){
        ArrayList<Unit> arrReturn = new ArrayList<>();
        for (Unit unit:arrUnitsMaybe) {
            if (!unit.isArtillery) {
                UnitMove unitMove = new UnitMove(unit, unit.getCurrentMovement(), true, true,0);
                if (unitMove.getMovePossible().contains(hex)) {
                    arrReturn.add(unit);
                }
            }
        }
        return arrReturn;
    }

    /**
     * Move the  unit to adjacent hex
     * @param unit
     * @param hex
     * @return
     */
    UnitMove unitMove;
    ArrayList<Hex> arrHexMovePath;
    public Hex getAttackFromHex(Hex hex){
        unitMove = new UnitMove(unitMOA, unitMOA.getCurrentMovement(), true, true,0);
        ArrayList<Hex> arrSurr= hex.getSurround();
        ArrayList<Hex> arrCheck = new ArrayList();
        /**
         *  sort possible in move left order
         */
        for (Hex hex1:arrSurr){
            if (unitMove.getMovePossible().contains(hex1)){
                int i=0;
                for (Hex hex2:arrCheck){
                    if (hex1.getCalcMoveCost(0) > hex2.getCalcMoveCost(0)){
                        break;
                    }
                    i++;
               }
                arrCheck.add(i, hex1);
            }
        }
        unitMOA.getHexOccupy().setCalcMoveCost(unitMOA.getCurrentMovement(),0); //
        /**
         *  use first move found if not crossiong river
         */
        for (Hex hex1:arrCheck){
            float cost = Move.cost(unitMOA,hex1,hex,true,true,0);
            if (Move.isRiverCrossed && unitMOA.isMechanized) {
                // dont allow
            }else{
                if (hex1.getCalcMoveCost(0) - cost >= 0){
                    hexAttackFrom = hex1;
                    return hex1;
                }
            }
        }
//        ErrorGame errorGame = new ErrorGame("Mobile Assualt no attack from hex found", this);
       return null;
    }
    private void moveUnit(Unit unit, Hex hex){
        unitMove = new UnitMove(unit, unit.getCurrentMovement(), true, true,0);
        arrHexMovePath = unitMove.getLeastPath(hexAttackFrom, true, null);
        arrHexMovePath.add(hex);
        movePathset(unit, false);
    }
    private void movePathset(Unit unit, boolean isStack){
        Gdx.app.log("MobileAssualt","Move|Pathset  unit="+unit);
        if (isStack){

        }else {
            int lastHex = arrHexMovePath.size() - 1;
            Hex hexTarget = arrHexMovePath.get(lastHex);
            arrHexMovePath.remove(lastHex);
        }
        int newMove = getMoveCostAfterMOA(unit,hexAttackFrom,hexAttack);
        unit.setCurrentMovement(newMove);
        Gdx.app.log("MobileAssualt","Move|Pathset  unit="+unit+" new="+newMove);

        Move.instance.actualMove(unit, arrHexMovePath, Move.AfterMove.ToMOA, isAI);
    }
    public int getMoveCostAfterMOA(Unit unit, Hex hexAttackFrom, Hex hexAttackTo){
        int newMove = (int) (hexAttackFrom.getCalcMoveCost(0) + .5f);
        float calc=Move.cost(unit,hexAttackFrom, hexAttackTo,true,true, 0);
        newMove -= (int) calc;
        return newMove;
    }

    /**
     *  After each MOA in stack  has been moved redo this
     *  if none left do the attack
     * @param unitMOA
     */
    public void attackMOA(Unit unitMOA){
        arrUnits.remove(unitMOA);
        unitMOA.getHexOccupy().moveUnitToFront(unitMOA);
        Counter.rePlace(unitMOA.getHexOccupy());
        if (arrUnits.isEmpty()) {
            attack.dieRoll();
        }else{
            movePathset(arrUnits.get(0), true);
        }
    }


    public void endMOA(){
        Gdx.app.log("MobileAssualt","endMOA");

        for (Stack stack:arrStack){
            stack.remove();
            stack = null;
        }
        arrStack.clear();
        arrHexMobile.clear();
        isStack = false;
        if (attack != null){
            attack.cancel();
        }
  //      winStack.cancel();
        CombatDisplay.instance.end();
        ardenne.instance.deleteObserver(this);
    }

    /**
     * do the initial move
     * @param attack
     */
    public void continueMove(Attack attack) {
        Gdx.app.log("MobileAssualt","ContinueMove");
        arrUnitsToMove.clear();
        arrUnitsToMove.addAll(attack.arrAttackers);
        Move.instance.arrUnitsInMoa.addAll(attack.arrAttackers);
        for (Unit unit:attack.arrAttackers){
            if (!unit.isEliminated()) {
                ArrayList<Hex> arrHexWork = new ArrayList<>();
                arrHexWork.add(unit.getHexOccupy());
                arrHexWork.add(attack.hexTarget);
                Move.instance.actualMove(unit, arrHexWork, Move.AfterMove.ToMOAKeepMove, isAI);
                break; // just do 1
            }
        }
    }

    /**
     * all moves fater that
     * @param unitiN
     */
    public void keepMove(Unit unitiN) {
        arrUnitsToMove.remove(unitiN);
        for (Unit unit:arrUnitsToMove){
            if (!unit.isEliminated()) {
                ArrayList<Hex> arrHexWork = new ArrayList<>();
                arrHexWork.add(unit.getHexOccupy());
                arrHexWork.add(attack.hexTarget);
                Move.instance.actualMove(unit, arrHexWork, Move.AfterMove.ToMOAKeepMove, isAI);
                break; // just do 1
            }
        }
    }
}
