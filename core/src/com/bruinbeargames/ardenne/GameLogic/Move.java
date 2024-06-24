package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.AI.AIUtil;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.River;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SaveGame;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.Unit.*;
import com.bruinbeargames.ardenne.WinModal;

import java.util.ArrayList;
import java.util.Observable;

public class Move extends Observable {
    static public Move instance;
    private I18NBundle i18NBundle;

    boolean isAllies;
    boolean isAI;
    ArrayList<Unit> arrUnitsInMoa = new ArrayList<>();
    public ArrayList<Hex> arrEntryPoints = new ArrayList<>();
    public Move(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
        for (Unit unit:Unit.getAllAllied()){
            Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
            if (unit.entryNum > 1){
                arrEntryPoints.add(hex);
            }

        }
        AIUtil.RemoveDuplicateHex(arrEntryPoints);

    }

    /**
     * Start of the move
     * issue any warnings or instructions
     *
     * @param isAllies
     */
    public void intializeMove(boolean isAllies, boolean isAI, boolean isExplotation) {
        this.isAllies = isAllies;
        this.isAI = isAI;

        doMovePhase(isAllies, isAI, isExplotation);
    }
    /**
     * Set all on boards unit counters to receive move requests
     *
     * @param isAllies
     * @param isAI
     */
    public void doMovePhase(boolean isAllies, boolean isAI, boolean isExplotation) {
        Gdx.app.log("Move", "doMovePhase allies=" + isAllies);
        this.isAllies = isAllies;
        arrUnitsInMoa.clear();
        ArrayList<Unit> arrUnitToMoveWork;
        if (isAllies) {
            arrUnitToMoveWork = Unit.getOnBoardAllied();
        } else {
            arrUnitToMoveWork = Unit.getOnBoardAxis();
        }
        /**
         * Check Turn last shade
         */
        int turn = NextPhase.instance.getTurn();
        int phase =NextPhase.instance.getPhase();
  /*      if (phase == Phase.GERMAN_EXPLOTATION.ordinal() || phase == Phase.ALLIED_EXPLOTATION.ordinal()){
            isExplotation = true;
        }*/
        if (!isAllies) {
            if (isExplotation) {
                TurnCounter.instance.updateText(i18NBundle.get("gexploit"));
            } else {
                TurnCounter.instance.updateText(i18NBundle.get("gmovephase"));

            }
        }else {
            if (isExplotation) {
                TurnCounter.instance.updateText(i18NBundle.get("aexploit"));
            } else {
                TurnCounter.instance.updateText(i18NBundle.get("amovephase"));

            }
        }
        ArrayList<Unit> arrUnitToDisplay = new ArrayList<>()  ;
        for (Unit unit : arrUnitToMoveWork) {
            if (unit.getID() == 35){
                int b=0;
            }

            if (unit.getMovedLast() < turn) { // on load only units that have not shade
               if (((isExplotation && unit.getCurrentMovement() >= 8 && !unit.hasAttackedThisTurn && !unit.hasBeenAttackedThisTurn)) || !isExplotation) {
                   unit.getMapCounter().getCounterStack().removeShade();
                   if (!isAI) {
                       arrUnitToDisplay.add(unit) ;
                   }
               }else{
                   unit.getMapCounter().getCounterStack().shade();
               }
            } else {
                unit.getMapCounter().getCounterStack().shade();
            }
        }
        if (arrUnitToDisplay.size() > 0) {
            WinModal.instance.set();
            scheduleMoveHilite(arrUnitToDisplay);
        }else{
            anyMovesLeft(isAI);
        }

    }
    private void scheduleMoveHilite(final ArrayList<Unit> arrUnitToMove) {
        final Unit unit = arrUnitToMove.get(0);
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {

                               SoundsLoader.instance.playLimber();
                               unit.getMapCounter().getCounterStack().removeShade();
                               unit.getHexOccupy().moveUnitToFront(unit);
                               Counter.rePlace(unit.getHexOccupy());
                               ClickAction clickAction = new ClickAction(unit, ClickAction.TypeAction.Move);
                               arrUnitToMove.remove(unit);
                               if (arrUnitToMove.size() == 0 ){
                                   WinModal.instance.release();
                                   anyMovesLeft(isAI);
                                   return;
                               }else{
                                   scheduleMoveHilite(arrUnitToMove);
                               }
                           }
                       }
                , .08F        //    (delay)
        );


    }
    public boolean  anyMovesLeft(boolean isAI) {
        if (ClickAction.getClickActionsLeft() > 0) {
            return true;
        } else {
            if (!isAI) {
                String str = i18NBundle.get("nomoremove");
                EventPopUp.instance.show(str);
                return false;
            }
        }
        return false;
    }

    public ArrayList<Unit> getArrUnitsInMoa(){
        ArrayList<Unit> arrUnitsReturn = new ArrayList<>();
        arrUnitsReturn.addAll(arrUnitsInMoa);
        return arrUnitsReturn;
    }

    ArrayList<Hex> arrMove;
    public void moveUnitFromClick(Unit unit, Hex hex, boolean isAI) {
        if (unit.getID() == 69){
            int b=0;
        }
        UnitMove unitMove = new UnitMove(unit, unit.getCurrentMovement(),true,true,0);
        arrMove=  unitMove.getLeastPath(hex, true, null);
 //       SoundsLoader.instance.playTrucksSound();
        actualMove(unit,arrMove,AfterMove.ToClick, isAI);
    }
    public void moveUnitAfterAdvance(Unit unit,Hex hex) {
        UnitMove unitMove = new UnitMove(unit, 10,true,true,0);
        arrMove=  unitMove.getLeastPath(hex, true, null);
        //       SoundsLoader.instance.playTrucksSound();
        actualMove(unit,arrMove,AfterMove.ToAdvance, isAI);
    }
    public void actualMove(Unit unit, ArrayList<Hex> arrMove, AfterMove afterMove, final boolean isAI) {
        /**
         *  calling rtn should have set off anything
         */
        Gdx.app.log("Move", "actualMove Unit="+unit);
        if (unit.isMechanized) {
            SoundsLoader.instance.playMovementSound();
        }else{
            SoundsLoader.instance.playMarch();
        }

        float delay = .42f;
        WinModal.instance.set(); // freeze counters
        if (unit.getMapCounter() != null) {
            unit.getMapCounter().getCounterStack().setPoints();
        }
      //  unit.getMapCounter().getCounterStack().setSupplyGas();
        float steps = delay / arrMove.size();
        float timer = steps;

        int i = 0;
        Hex hexEnd = null;
        if (GameSetup.instance.getScenario().ordinal() <= GameSetup.Scenario.Lehr.ordinal())
        {
            if (SecondPanzerExits.instance.isInSecond(unit)){
                Hex hexWork = arrMove.get(arrMove.size()-1);
                if (SecondPanzerExits.instance.isInExit(hexWork)){
                    hexEnd = hexWork                   ;
                }
            }
            if (LehrExits.instance.isInLehr(unit)){
                Hex hexWork = arrMove.get(arrMove.size()-1);
                if (LehrExits.instance.isInExit(hexWork)){
                    hexEnd = hexWork                   ;
                }
            }

        }

        for (Hex hex : arrMove) {
            final Hex hexTime = hex;
            final Hex hexPrevious;
            if (i == 0) {
                hexPrevious = unit.getHexOccupy();
                // set first hex
            } else {
                hexPrevious = arrMove.get(i - 1);
            }
 //           Gdx.app.log("Move", "actualMove before run hex="+hexPrevious+" i="+i);

            final Unit unitMove = unit;
           Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
//                   Gdx.app.log("Move", "actualMove leaving hex="+hexPrevious);

                    if (unitMove.getHexOccupy().leaveHex(unitMove)) {
                        Counter.rePlace(hexPrevious);
                        hexTime.enterHex(unitMove);
                        Counter.rePlace(hexTime);
                    }else{
                        ErrorGame errorGame = new ErrorGame("Move Error Leaving Hex", this);
                    }
                }

            }, timer);

            timer += steps+.08f;
            i++;
        }
        timer += .1f;
        final Unit unitDone  = unit;
        final AfterMove after = afterMove;
        final Hex finalHexEnd = hexEnd;
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                              afterMoveDisplay(after, unitDone, isAI, finalHexEnd);
                           }


                       }, timer  //delay * 2f
        );
    }
    public void  afterMoveDisplay(AfterMove af, Unit unit, boolean isAI, Hex hexEnd){
        WinModal.instance.release();
        SoundsLoader.instance.stopSounds();
        if (af == AfterMove.ToClick){
            unit.setMovedThisTurn(NextPhase.instance.getTurn());
            unit.getMapCounter().getCounterStack().shade();
            unit.setCurrentMovement((int) (unit.getHexOccupy().getCalcMoveCost(0)));
            unit.getMapCounter().getCounterStack().setPoints();
       //     unit.getMapCounter().getCounterStack().setSupplyGas();
            int i = unit.getCurrentMovement();
            if (!isAI) {
                moveReturnFromClick(true,hexEnd,unit);
            }else{
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.MoveFinished,null,0,0));
            }
            return;
        }

        if (af == AfterMove.ToAdvance){
            unit.setMovedThisTurn(NextPhase.instance.getTurn());
            if (unit.getMapCounter() != null) {
                unit.getMapCounter().getCounterStack().shade();
            }
            moveReturnFromClick(false, hexEnd, unit);
            return;
        }
        if (af == AfterMove.ToMOA){
            unit.setMovedThisTurn(NextPhase.instance.getTurn());
            MobileAssualt.instance.attackMOA(unit);
            return;
        }
        if (af == AfterMove.ToMOAKeepMove){
            unit.setMovedThisTurn(NextPhase.instance.getTurn());
            MobileAssualt.instance.keepMove(unit);
            return;
        }
        if (af == AfterMove.Retreats){
            Attack.instance.doNextRetreat(unit);
            return;
        }
        if (af == AfterMove.ToReinforcement){
            unit.setMovedThisTurn(NextPhase.instance.getTurn());
            unit.getMapCounter().getCounterStack().shade();
            unit.setCurrentMovement((int) (unit.getHexOccupy().getCalcMoveCost(0)));
            unit.getMapCounter().getCounterStack().setPoints();
            Reinforcement.instance.getScreen().afterMove(unit);
            return;
        }


    }

    public void moveReturnFromClick(boolean isSaveMove, Hex hexExitPanzer, Unit unit){
        if (isSaveMove) {
            SaveGame.SaveLastPhase(" Last Turn", 2);
        }

        MobileAssualt.instance.endMOA();
        /**
         *
         * check if anymoves left
         */
        if (hexExitPanzer != null){
            if (SecondPanzerExits.instance.isInSecond(unit)) {
                SecondPanzerExits.instance.exitUnit(hexExitPanzer, unit);
            }else{
                LehrExits.instance.exitUnit(hexExitPanzer,unit);
            }
        }
        ClickAction.unLock();
        if (Move.instance.anyMovesLeft(isAI))
        {
            return;
        }

    }

    public void endMove(boolean isAllies, boolean isAI) {
        ArrayList<Unit> arrUnitToMoveWork;
        if (isAllies) {
            arrUnitToMoveWork = Unit.getOnBoardAllied();
        } else {
            arrUnitToMoveWork = Unit.getOnBoardAxis();
        }
        for (Unit unit:arrUnitToMoveWork){
            unit.getMapCounter().getCounterStack().removeHilite();
            unit.getMapCounter().getCounterStack().removeShade();
            unit.getMapCounter().removeClickAction();
        }

    }

    public enum AfterMove {
        ToClick, ToAI, ToRetreat, ToAdvance, ToReturn, ToAfterCombat, ToMOA, ToMOAKeepMove, ToAlliedAI, None, Retreats,ToReinforcement;

    }




    static public boolean isMOAEncountered = false;
    static public boolean isRiverCrossed = false;

    public static float cost(Unit unit, Hex startHex, Hex endHex, boolean isMobileAssault, boolean checkTerrain, int thread) {
        /**
         *  start with a cost of 1 for clear
         *  anything else we will add to this cost unless hex is prohibited
         *  IT IS ASSUMED THAT Mobile assault has been checked for validity before
         *  invoking this rtn
         */
        if (startHex.xTable ==20 && startHex.yTable == 14 && endHex.xTable == 20 && endHex.yTable == 15){
            int bt=0;
        }
        isMOAEncountered = false;
        isRiverCrossed = false;
//        if (endHex.xTable == 16 && endHex.yTable == 10) {
//              int bg = 0;
//         }
        float cost = 0; // assume clear

//        if (!startHex.getSurround().contains(endHex)) { // should not happen
//            return 999;
//        }

//        if (!HexCanNotCross.checkCanCross(startHex, endHex)) {
//            return 999;
//        }

        /**
         *  check if target hex has a unit
         *  or in case of fakeAI check if hex has been set
         *
         */
        boolean isEnemyInHex = false;
        if ((unit.isAllies && endHex.isAxisOccupied[thread] || (unit.isAxis && endHex.isAlliedOccupied[thread]))) {
            if (isMobileAssault) {
                    isEnemyInHex = true;
            } else {
                return 888;
            }
        }

/*        if (checkAdjacent) { // for supply
            for (Hex hex : endHex.getSurround()) {
                if (unit.isAllies && hex.checkAxisInHex() || unit.isAxis && hex.checkAlliesInHex()) {
                    return 777;
                }
            }
        }*/
        if (unit == null){
            int bg =0;
        }
        if ((unit.isAllies && endHex.isAxisZOC[thread]) || unit.isAxis && endHex.isAlliedZOC[thread] ) {
            if (unit.isTransport) {
                if ((endHex.getAxisZoc(thread) && endHex.isAlliedOccupied[thread]) || endHex.getAlliedZoc(thread) && endHex.isAxisOccupied[thread]){
                    // OK
                }else{
                    cost=999;
                }
            }else {
                cost += 2;
            }
        }
        if (endHex.isJunction() && CardHandler.instance.isJunctionSet()){
            cost +=4;
        }
        boolean isRoad =false;
        boolean isPath = false;
        if (Hex.isRoadConnection(startHex,endHex)){
            isRoad = true;
            cost += .5F;
        }else if (startHex.isPath() && endHex.isPath() && Hex.isPathConnection(startHex,endHex)){
            isPath = true;
            if (unit.isTransport){
                cost +=1;
            }else {
                cost += .5F;
            }
        }else if (endHex.isCity() || endHex.isTown() || endHex.isForest()){
            // do nothing keep looking atcost
        }else if (unit.isMechanized){
                cost +=2; // for clear
            }else{
                cost +=1; // for clear
            }
        int steps =endHex.getStacksIn();

        if (isPath ){
            if (steps > 4){
                cost +=3;
            }else if (steps > 2){
                cost +=2;
            }else if (steps >1 ){
                cost += .5f;
            }
        }
        /**
         *  make it easier on Road
         */
        if (isRoad){
            if (steps > 4){
                cost +=3;
            }else if (steps > 2){
                cost +=1;
            }else if (steps >0 ){
                cost += 0;
            }
        }
        if (startHex.isStreamBank() && endHex.isStreamBank() && checkTerrain) {
            if (Bridge.isBridge(startHex, endHex)){
              // nothing
            }else if (River.instance.isStreamBetween(startHex, endHex)) {
                isRiverCrossed = true;
                if (unit.isMechanized){
                    if (unit.isTransport) {
                        cost += 14;
                    }else {

                        cost += 12;
                    }
                }else{
                    cost +=3;
                }
            }

        }
        if (endHex.isForest() && !(isPath || isRoad) && checkTerrain){
            if (unit.isMechanized){
                if (unit.isTransport){
                    cost += 6;
                }else {
                    cost += 4;
                }
            }else{
                cost +=2;
            }
        }
        if (!endHex.isForest() && endHex.isTown() && checkTerrain && !(isPath || isRoad)){
            if (unit.isMechanized){
                cost +=2;
            }else{
                cost +=1;
            }
        }
        if (endHex.isCity() && checkTerrain && !(isPath || isRoad)){
            if (unit.isMechanized){
                cost +=2;
            }else{
                cost +=1;
            }
        }

        if (isEnemyInHex) {
            if (unit.isTransport){
                cost =999;
            }else {
                cost += 2.5f; //
                isMOAEncountered = true;

            }
        }
 //       if (endHex.xTable == 24 && endHex.yTable == 8){
 //           int bg =0;
 //       }

        return cost;

    }

}
