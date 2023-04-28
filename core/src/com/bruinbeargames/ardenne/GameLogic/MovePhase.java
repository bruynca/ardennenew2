package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.River;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class MovePhase {
    static public MovePhase instance;
    private I18NBundle i18NBundle;

    boolean isAllies;
    boolean isAI;
    public MovePhase(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

    }


    /**
     * Set all on boards unit counters to receive move requests
     *
     * @param isAllies
     * @param isAI
     */
    public void doMovePhase(boolean isAllies, boolean isAI) {
       this.isAI = isAI;
       this.isAllies = isAllies;
        Gdx.app.log("Move", "doMovePhase allies=" + isAllies);
        this.isAllies = isAllies;
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
        if (arrUnitToMoveWork.size() > 0){
            TurnCounter.instance.updateText(i18NBundle.get("gmovephase"));
        }
        for (Unit unit : arrUnitToMoveWork) {
            if (unit.getMovedLast() < turn) { // on load only units that have not shade
                unit.resetCurrentMove();
                unit.getMapCounter().getCounterStack().removeShade();
                if (!isAI) {
                    ClickAction clickAction = new ClickAction(unit, ClickAction.TypeAction.Move);
                }
            } else {
                unit.getMapCounter().getCounterStack().shade();
            }
        }
    }
    public boolean  anyMovesLeft(){
        if (ClickAction.getClickActionsLeft() > 0){
            return true;
        }
        return false;
    }

    public void moveReturnFromClick(){
        /**
         * check if anymoves left
         */
        if (Move.instance.anyMovesLeft(isAI))
        {
            return;
        }
    }
    public enum AfterMove {
        ToClick, ToAI, ToRetreat, ToAdvance, ToReturn, ToAfterCombat, ToMOA, ToMOARetreat, ToAlliedAI, None;

    }




    static public boolean isMOAEncountered = false;
    public static float cost(Unit unit, Hex startHex, Hex endHex, boolean isMobileAssault, boolean checkTerrain, boolean checkAdjacent, boolean isFakeAi) {
        /**
         *  start with a cost of 1 for clear
         *  anything else we will add to this cost unless hex is prohibited
         *  IT IS ASSUMED THAT Mobile assault has been checked for validity before
         *  invoking this rtn
         */
        isMOAEncountered = false;
        float cost = 0; // assume clear

        if (!startHex.getSurround().contains(endHex)) { // should not happen
            return 999;
        }

//        if (!HexCanNotCross.checkCanCross(startHex, endHex)) {
//            return 999;
//        }

        /**
         *  check if target hex has a unit
         *  or in case of fakeAI check if hex has been set
         *
         */
        boolean isEnemyInHex = false;
        if (endHex.getUnitsInHex().size() > 0) {
            if ((unit.isAllies && endHex.checkAxisInHex() || (unit.isAxis && endHex.checkAlliesInHex()))) {
                if (isMobileAssault) {
                    isEnemyInHex = true;
                } else {
                    return 888;
                }
            }
        }
        if (endHex == Hex.hexTable[31][13]){
            int bb =0;
        }

/*        if (checkAdjacent) { // for supply
            for (Hex hex : endHex.getSurround()) {
                if (unit.isAllies && hex.checkAxisInHex() || unit.isAxis && hex.checkAlliesInHex()) {
                    return 777;
                }
            }
        }*/

        if ((unit.isAllies && endHex.getAxisZoc(0)) || unit.isAxis && endHex.getAlliedZoc(0) ) {
            cost += 2;
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
            cost += .5F;
        }else if (endHex.isCity() || endHex.isTown() || endHex.isForest()){
            // do nothing keep looking atcost
        }else if (unit.isMechanized){
            cost +=2; // for clear
        }else{
            cost +=1; // for clear
        }
        if (isRoad || isPath){
            int steps =endHex.getStacksIn();
            if (steps > 4){
                cost +=3;
            }else if (steps > 2){
                cost +=2;
            }else if (steps >0 ){
                cost += 1;
            }
        }
        if (startHex.isStreamBank() && endHex.isStreamBank() && checkTerrain) {
            if (Bridge.isBridge(startHex, endHex)){
                // nothing
            }else if (River.instance.isStreamBetween(startHex, endHex)) {
                if (unit.isMechanized){
                    cost +=8;
                }else{
                    cost +=3;
                }
            }

        }
        if (endHex.isForest() && !(isPath || isRoad) && checkTerrain){
            if (unit.isMechanized){
                cost +=3;
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
            cost += 2; //
            isMOAEncountered = true;
        }
        return cost;

    }

}
