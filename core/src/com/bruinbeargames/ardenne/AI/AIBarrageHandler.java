package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.Airplane;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameLogic.BarrageExplode;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHelper;
import com.bruinbeargames.ardenne.Hex.HexInt;
import com.bruinbeargames.ardenne.Hex.HexUnits;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.EventOK;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AIBarrageHandler implements Observer {
    static public AIBarrageHandler instance;
    ArrayList<Unit> arrShooters = new ArrayList<>();
    ArrayList<Hex> arrTargets = new ArrayList<>();
    ArrayList<AIOrders> arrAIOrders = new ArrayList();
    I18NBundle i18NBundle;

    boolean useAircraft = true;

    AIBarrageHandler() {
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
    }

    public void doAllied(boolean isOffensive) {
        /**
         *  determine Aircraft on last
         */
/*        if (GameSetup.instance.getScenario().getLength()  ==
                NextPhase.instance.getTurn()){
            useAircraft = true;
        }else{
            useAircraft = false;
        }*/
        if (isOffensive) {
            EventAI.instance.hide();
        }
        useAircraft = false;
        /**
         *  set up crosshairs and arrays
         */
        arrShooters.clear();
        arrTargets.clear();
        Barrage.instance.intialize(true, false);
        Unit.initUnShade(); // take off shade
        /**
         *
         */
        arrShooters.addAll(Barrage.instance.getArrArtilleryShooters());
        if (arrShooters.size() == 0) {
            Barrage.instance.clearCrossHairs();
            EventOK.instance.addObserver(this);
            if (isOffensive) {
                EventOK.instance.show(i18NBundle.format("aioffbarrage"));
            } else {
                EventOK.instance.show(i18NBundle.format("aidefbarrage"));
            }
            return;
        }
        /**
         * sort targets by most sp in hex
         */
        ArrayList<Hex> arrPosition = new ArrayList<>();
        for (Unit unit : arrShooters) {
            arrPosition.add(unit.getHexOccupy());
        }
        ArrayList<Hex> arrCombatHex = AICombat.instance.getCombatHex();
        int cnrAir = Airplane.instance.getCount();
        arrAIOrders = getMostDamageChances(arrShooters, arrPosition, arrCombatHex);
        /**
         * lets take top one for now
         */
        Barrage.instance.clearCrossHairs();
        int cntAir = Airplane.instance.getCount();

        if (arrAIOrders != null && arrAIOrders.size() > 0) {
            AIOrders aiPick = arrAIOrders.get(0);
            aiPick.createTargetShooterSave(cntAir);
        } else if (cntAir > 0) {
            AIOrders aiStub = new AIOrders();
            aiStub.createTargetShooterSave(cntAir);
        }
        if (EventPopUp.instance.isShowing()) {
            EventPopUp.instance.hide();
        }
        EventOK.instance.addObserver(this);
        if (isOffensive) {
            EventOK.instance.show(i18NBundle.format("aioffbarrage"));
        } else {
            EventOK.instance.show(i18NBundle.format("aidefbarrage"));

        }
    }

    /**
     * Iterate through all combinations  sorted by hishest score
     *
     * @param arrShooters
     * @param arrShootersHex
     * @param arrHexCombat
     * @return
     */
    public ArrayList<AIOrders> getMostDamageChances(ArrayList<Unit> arrShooters, ArrayList<Hex> arrShootersHex, ArrayList<Hex> arrHexCombat) {
        ArrayList<AIOrders> arrReturn = new ArrayList<>();
        ArrayList<Hex> arrHexPositionUnit = new ArrayList<>();
        arrHexPositionUnit.addAll(arrShootersHex);
        /**
         * set up for Iterator
         * with each unit having an array of hexes it can fire into
         */
        ArrayList<ArrayList<Hex>> arrArrs = new ArrayList<>();
        ArrayList<Unit> arrUnitsWithBlanks = new ArrayList<>();
        int ix = 0;
        for (Unit unit : arrShooters) {
            ArrayList<Hex> arrCanBombard = getArrayOfHexesCanBombard(unit, unit.getHexOccupy());
            if (arrCanBombard.size() == 0) {  // if cant bombard anyone take it out
                arrUnitsWithBlanks.add(unit);
            } else {
                arrArrs.add(arrCanBombard);
            }
            ix++;
        }
        /**
         *  create hex arrays with valid
         */
        ix = 0;
        ArrayList<Hex>[] arrHexArray = new ArrayList[arrArrs.size()];
        for (ArrayList<Hex> arr : arrArrs) {
            arrHexArray[ix] = arr;
            ix++;
        }
        /**
         *  remove units that cant hit anyone
         */
        ArrayList<Unit> arrUnitWork = new ArrayList<>();
        arrUnitWork.addAll(arrShooters);
        for (Unit unit : arrUnitsWithBlanks) {
            ix = arrUnitWork.indexOf(unit);
            arrHexPositionUnit.remove(ix);
            arrUnitWork.remove(ix);
        }
        if (arrUnitWork.size() == 0) {
            return null;
        }

        /**
         *  create the Iterator
         */
        AIIterator aiIterator = new AIIterator(arrHexArray, arrUnitWork, arrHexPositionUnit, AIOrders.Type.Bombard);
        AIOrders aiOrders = aiIterator.Iteration();
        /**
         *  check if we have null at start
         */
        if (aiOrders == null) {
            return arrReturn;
        }
        /**
         *  do the loop for iterator
         */
        while (aiOrders != null) {
            int score = getBombardScore(aiOrders, arrHexCombat);
            if (score > 0) {
                int bk = 0;
            }
            aiOrders.setScoreBombard(score);
            /**
             * insert into return table based on score
             */
            for (ix = 0; ix < arrReturn.size(); ix++) {
                if (score > arrReturn.get(ix).getScoreBombard()) {
                    break;
                }
            }
            arrReturn.add(ix, aiOrders);
            aiOrders = aiIterator.doNext();
        }
        /**
         *  add move to hex
         */
        for (AIOrders ai : arrReturn) {
            ai.arrHexMoveTo = arrShootersHex;
        }
        return arrReturn;
    }


    /**
     * get hexes the unit can bombard from a hex position
     *
     * @param unit
     * @param hexIn
     * @return ArrayofHex
     */
    public ArrayList<Hex> getArrayOfHexesCanBombard(Unit unit, Hex hexIn) {
        if (!unit.isArtillery) {
            return null;
        }
        int range = unit.getRange();
        ArrayList<Hex> arrHexRange = new ArrayList<>();
        for (Hex hex : HexHelper.getSurroundinghexes(hexIn, range)) {
            if ((unit.isAllies && hex.isAxisOccupied()) || unit.isAxis && hex.isAlliedOccupied()) {
                arrHexRange.add(hex);
            }
        }
        if (arrHexRange.size() == 0) {  // if cant bombard anyone take it out
            return null;
        } else {
            return (arrHexRange);
        }
    }

    /**
     * Score the aiorders for bombard  multiply units in hex strength time attacking strength
     *
     * @param aiOrders
     * @return
     */
    final float factorCombatAdjust = 2;

    private int getBombardScore(AIOrders aiOrders, ArrayList<Hex> arrHexAttackCombat) {
        HexUnits.init();
        int score = 0;
        for (int i = 0; i < aiOrders.getArrUnit().size(); i++) {
            HexUnits.add(aiOrders.getArrHexShootAt().get(i), aiOrders.getArrUnit().get(i));
        }
        for (HexUnits heUn : HexUnits.arrHexUnits) {
            int bombard = 0;
            for (Unit unit : heUn.getArrUnits()) {
                bombard += unit.getCurrenAttackFactor();
            }
            /**
             * adjust Bombard for percentages of hits at that number
             */
            float adjust = BarrageExplode.getPercentHits(bombard);
            int points = heUn.getHex().getAttackPointsInHex();

            score += points * adjust;
            /**
             *  multiply for attacking factor
             */
            if (arrHexAttackCombat.contains(heUn.getHex())) {
                score *= factorCombatAdjust;
            }

        }
        return score;

    }

    public void doAlliedOffense() {
        doAllied(true);
        return;
        /*
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.log("NextPhase", "Timer" );
                NextPhase.instance.nextPhase();
            }

        }, .065F); */
    }

    /**
     * Get best bombing for AI
     * scenario 1 at the moment max we want 4 targets
     *
     * @return
     */
    public ArrayList<HexInt> getBestAirBombard(int countAirPoints) {
        int cntUsed = countAirPoints;
        int maxSpread = 4;
        ArrayList<HexInt> arrAirAllocate = new ArrayList<>();
        ArrayList<Hex> arrGermanHex = new ArrayList<>();
        /**
         * get axis hexes
         */
        for (Unit unit : Unit.getOnBoardAxis()) {
            if (!arrGermanHex.contains(unit.getHexOccupy())) {
                if (!unit.isDisorganized()) {
                    arrGermanHex.add(unit.getHexOccupy());
                }
            }
        }
        AIUtil.RemoveDuplicateHex(arrGermanHex);
        /**
         *  go trough from top to bottom adding to bomb targets
         */
        if (arrGermanHex.contains(AIReinforcementScenario1.bastogne1)) {
            arrAirAllocate.add(new HexInt(AIReinforcementScenario1.bastogne1, 0));
        }
        if (arrGermanHex.contains(AIReinforcementScenario1.bastogne2)) {
            arrAirAllocate.add(new HexInt(AIReinforcementScenario1.bastogne2, 0));
        }
        if (arrGermanHex.contains(AIReinforcementScenario1.hexWiltz)) {
            arrAirAllocate.add(new HexInt(AIReinforcementScenario1.hexWiltz, 0));
        }
        /**
         *  create an array sorted with highest atta points
         */
        ArrayList<Hex> arrHexAttackPointsRaw = new ArrayList<>();
        int ix =0;
        for (Hex hex:arrGermanHex){
            for (ix=0; ix<arrHexAttackPointsRaw.size();ix++){;
                if (arrHexAttackPointsRaw.get(ix).getAttackPointIn() <  hex.getAttackPointIn()){
                    break;
                }
            }
            arrHexAttackPointsRaw.add(ix,hex);
        }
        for (Hex hex : arrHexAttackPointsRaw) {
             arrAirAllocate.add(new HexInt(hex,0));
        }
        /**
         *  rationalize table so top gets the aircraft count
         */
        int keep =0;
        if (maxSpread > cntUsed){
            keep = cntUsed;
        }else{
            keep =maxSpread;
        }
        ArrayList<HexInt> arrNew = AIUtil.keepTop(arrAirAllocate,keep);
        while (cntUsed > 0){
            for (HexInt hi:arrNew){
                if (cntUsed >0){
                    hi.count++;
                    cntUsed--;
                }
            }
        }

        return arrNew;
    }

    private ArrayList<HexInt> spreadAir(int countAirPoints, ArrayList<HexInt> arrAirAllocate) {
        int cnt = countAirPoints;
        HexInt.rationlize(arrAirAllocate);
        while (cnt > 0) {
            for (HexInt hI : arrAirAllocate) {
                if (cnt > 0) {
                    hI.count++;
                    cnt--;
                }
            }
        }
        return arrAirAllocate;
    }

    public void update(Observable observable, Object arg) {
        ObserverPackage oB = (ObserverPackage) arg;

        /**
         *  if confimed kick off next phase
         */
        if (((ObserverPackage) arg).type == ObserverPackage.Type.OK) {
            EventOK.instance.deleteObserver(this);
            NextPhase.instance.nextPhase();
        }


    }

}

