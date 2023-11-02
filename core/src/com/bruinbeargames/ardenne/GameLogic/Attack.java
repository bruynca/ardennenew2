package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.AI.AIAdvance;
import com.bruinbeargames.ardenne.AI.AIReinforcementScenario1;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHelper;
import com.bruinbeargames.ardenne.Hex.River;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.CombatDisplay;
import com.bruinbeargames.ardenne.UI.CombatDisplayResults;
import com.bruinbeargames.ardenne.UI.DiceEffect;
import com.bruinbeargames.ardenne.UI.WinCRT;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Attack extends Observable implements Observer  {
    ArrayList<Unit> arrAttackers = new ArrayList<>();
    ArrayList<Unit> arrDefenders = new ArrayList<>();
    Hex hexTarget;
    boolean isAllies;
    boolean isMobileAssualt;
    boolean isAI;
    String odds;
    AttackOdds attackOdds;
    float attackStrength;
    float defenseStrength;
    int attackerLoses;
    int attackRetreats;
    int defenderLoses;
    int defenderRetreats;
    Losses defenderLosses;
    Losses attackerLosses;
    boolean isDefendHexVacant = false;
    DefenderRetreat defenderRetreat;
    static public Attack instance;
    boolean isVillage;
    boolean isTown;
    boolean isBridge;
    boolean isTrees;
    boolean isRiver;
    boolean isMechAttack;
    String dieResult;



    public boolean isVillage() {
        return isVillage;
    }

    public boolean isTown() {
        return isTown;
    }

    public boolean isTrees() {
        return isTrees;
    }

    public boolean isRiver() {
        return isRiver;
    }

    public boolean isMechAttack() {
        return isMechAttack;
    }

    public boolean isBridge() {
        return isBridge;
    }

    public String getAttackOdd(){
        return attackOdds.oddactualString;
    }
    public float getActualOdds(){
        return attackOdds.oddsCheck;
    }

    public Attack(Hex hex, boolean isAllies, boolean isMobileAssualt, boolean isAI, Unit unitMobileAssualt) {
        if (!isAI) {
            Gdx.app.log("Attack", "Constructor Hex=" + hex);
        }
        hexTarget = hex;
        instance = this;
        this.isAllies = isAllies;
        this.isAI = isAI;
        this.isMobileAssualt = isMobileAssualt;
        for (Unit unit : hexTarget.getUnitsInHex()) {
            if ((isAllies && unit.isAxis) || (!isAllies && unit.isAllies)) {
                arrDefenders.add(unit);
                if (unit.isArtillery) {
                    if (unit.isMobileArtillery){
                        defenseStrength += 2;
                    }else {
                        defenseStrength += 1;
                    }
                } else {
 //                   if (unit.getInSupplyThisTurn()) {
                        defenseStrength += unit.getCurrentDefenseFactor();
 //                   } else {
 //                       defenseStrength += unit.getCurrentDefenseFactor() / 2;
 //                   }
                }
            }
        }
        attackOdds = new AttackOdds(this);
        odds = attackOdds.oddactualString;
        if (isMobileAssualt) {
            addAttacker(unitMobileAssualt, isAI);
        } else {
            if (!isAI) {
                CombatDisplay.instance.updateCombat(this, odds);
            }
        }
    }

    public String[] getAttackResults() {
        return AttackOdds.result;
    }

    public int[][] getDice() {
        int[][] tabReturn = new int[AttackOdds.combatResultTableAttacker.length][2];
        for (int i = 0; i < tabReturn.length; i++) {
            tabReturn[i][0] = AttackOdds.combatResultTableAttacker[i][AttackOdds.ixTableView][0];
            tabReturn[i][1] = AttackOdds.combatResultTableAttacker[i][AttackOdds.ixTableView][1];
        }
        return tabReturn;
    }

    public void addAttacker(Unit unit, boolean isAI) {
        if (!isAI) {
            Gdx.app.log("Attack", "add attacker =" + unit);
        }
        if (arrAttackers.contains(unit)) {
            return;
        }
        arrAttackers.add(unit);
        updateDisplayFlags();
        attackStrength = calcAttackStrength();
        attackOdds.update();
        odds = attackOdds.oddactualString;
        if (!isAI) {
            CombatDisplay.instance.updateCombat(this, attackOdds.oddUnConverted);
        }
    }

    private void updateDisplayFlags() {
        isBridge = false;
        isMechAttack = false;
        isRiver = false;
        isTown = false;
        isTown = false;
        isVillage = false;

    }

    private int calcAttackStrength() {
        if (!isAI) {
            Gdx.app.log("Attack", "calacAttackStregtn");
        }

        int retAttack = 0;
        for (Unit unit : arrAttackers) {
            Hex hex;
            if (isMobileAssualt) {
                hex = MobileAssualt.instance.getAttackFromHex(getHexTarget());
                if (hex == null){ // stop abend
                    return 1;
                }
            } else {
                hex = unit.getHexOccupy();
            }
            float cntAttackStrength = unit.getCurrenAttackFactor();

  //          if (unit.inSupply()) {
 //           } else {
 //               cntAttackStrength /= 2;
 //           }
            if (Bridge.isBridge(hex, getHexTarget())) {
                cntAttackStrength /= 2;
                isBridge = true;
            } else {
                if (hex != null) { // hack for Reinforcements
                    if (River.instance.isStreamBetween(hex, getHexTarget())) {
                        if (unit.isMechanized) {
                            cntAttackStrength /= 4;
                            isRiver = true;

                        } else {
                            cntAttackStrength /= 2;
                            isRiver = true;
                        }
                    }
                }
            }
            retAttack += cntAttackStrength + .5f;
        }
        return retAttack;
    }

    public void removeAttacker(Unit unit) {
        Gdx.app.log("Attack", "remove attacker =" + unit);
        if (arrAttackers.contains(unit)) {
            arrAttackers.remove(unit);
        }
        updateDisplayFlags();
        attackStrength = calcAttackStrength();
        attackOdds.update();
        odds = attackOdds.oddactualString;
        CombatDisplay.instance.updateCombat(this, odds);
    }

    public Hex getHexTarget() {
        return hexTarget;
    }

    public boolean isAllies() {
        return isAllies;
    }

    public float getAttackStrength() {
        return attackStrength;
    }
    public ArrayList<Unit> getAttackers(){
        return arrAttackers;
    }
     public float getDefenseStrength() {
        return defenseStrength;
    }


    public void cancel() {
        CombatDisplay.instance.end();
    }

    ArrayList<Unit> arrUnitsToRetreat = new ArrayList<>();

    public void dieRoll() {
        Gdx.app.log("Attack", "dieRoll");
        for (Unit unit : arrAttackers) {
            if (!isMobileAssualt) {
                unit.setCanAttackThisTurnOff();
            }
            unit.setHasAttackedThisTurn();
        }
        for (Unit unit : arrDefenders) {
            unit.setHasbeenAttackedThisTurn();
        }
        if (NextPhase.instance.getPhase() == Phase.GERMAN_COMBAT.ordinal() ||
                NextPhase.instance.getPhase() == Phase.ALLIED_COMBAT.ordinal()) {
            hexTarget.setHasBeenAttackedThisTurn(true);
        }

        CombatResults.init();
        attackOdds.update();
  //      CombatDisplay.instance.end();
 //       int calc = 0;
 //       for (int i=0; i< 10000; i++){
 //           calc += getDieRoll();
 //       }
        int die1 = getDieRoll();
        int die2 = getDieRoll();
 //       die1 =6;
 //       die2 = 4;
        Gdx.app.log("Attack", "die=" + die1 + " " + die2);

        dieResult = attackOdds.getResult(die1, die2);
        DiceEffect.instance.addObserver(this);
        DiceEffect.instance.rollBlueDice(die2);
        DiceEffect.instance.rollRedDice(die1);

    }
    public void afterDieRoll(){
        Gdx.app.log("Attack", "dieResult=" + dieResult);
         //     dieResult ="D2r2";
        WinCRT.instance.show(this, dieResult);

        for (int i = 0; i < dieResult.length(); i++) {
            switch (dieResult.charAt(i)) {
                case 'A':
                    attackerLoses = Character.getNumericValue(dieResult.charAt(i + 1));
                    break;
                case 'D':
                    defenderLoses = Character.getNumericValue(dieResult.charAt(i + 1));
                    break;
                case 'r':
                    defenderRetreats = Character.getNumericValue(dieResult.charAt(i + 1));
                    break;
            }
        }
        Gdx.app.log("Attack", "attacker Loses    =" + attackerLoses);
        Gdx.app.log("Attack", "defender Loses    =" + defenderLoses);
        Gdx.app.log("Attack", "defender retreats =" + defenderRetreats);

        defenderLosses = new Losses(arrDefenders, defenderLoses);
        attackerLosses = new Losses(arrAttackers, attackerLoses);
        /**
         *  defender retreats
         */
        if (defenderLosses.areAllEliminated) {
            isDefendHexVacant = true;
        }
        arrUnitsToRetreat.clear();
        if (!defenderLosses.areAllEliminated && defenderRetreats > 0) {
            defenderRetreat = new DefenderRetreat(this);
            if (defenderRetreat.losses > 0) {
                defenderLosses = new Losses(arrDefenders, defenderRetreat.losses);
            }
            /**
             * save all units to retreat and fire the first on not eliminated
             */
            arrUnitsToRetreat.addAll(arrDefenders);

            for (Unit unit : arrDefenders) {
                if (!unit.isEliminated()) {
                    CombatResults cr = CombatResults.find(unit);
                    cr.setHexesRetreated(defenderRetreat.arrRetreatPath.size());
                    instance = this;
                    Move.instance.actualMove(unit, defenderRetreat.arrRetreatPath, Move.AfterMove.Retreats, isAI);
                    return; //
                }
            }
            isDefendHexVacant = true;
        }
        afterRetreat();

    }

    /**
     *  after first retreat this does all others
     * @param unitDone
     */
    public void doNextRetreat(Unit unitDone){
        Gdx.app.log("Attack", "doNextRetreat unitDone="+unitDone);

        arrUnitsToRetreat.remove(unitDone);
        ArrayList<Unit> arrRemove = new ArrayList<>();

        for (Unit unit : arrUnitsToRetreat) {
            if (!unit.isEliminated()) {
  //              arrUnitsToRetreat.add(unit); // why ??
                Gdx.app.log("Attack", "doNextRetreat UnitNot Elim");
                CombatResults cr = CombatResults.find(unit);
                cr.setHexesRetreated(defenderRetreat.arrRetreatPath.size());
                Move.instance.actualMove(unit, defenderRetreat.arrRetreatPath, Move.AfterMove.Retreats, isAI);
                return; //
            }else {
                arrRemove.add(unit);
            }
        }
        arrUnitsToRetreat.removeAll(arrRemove);
        if (arrUnitsToRetreat.size() == 0) {
            isDefendHexVacant = true;
            afterRetreat();
        }
    }


    public void afterRetreat(){
        Gdx.app.log("Attack", "AfterRetreat");

        /**
         *  update combat display with advanve or continue results
         */

        if (isDefendHexVacant && attackerLosses != null && !attackerLosses.areAllEliminated){
            if (isMobileAssualt) {
                for (Unit unit : arrAttackers) {
                    if (!unit.isEliminated() && unit.getCurrentMovement() > 0){
                        CombatResults cb = new CombatResults(unit);
                        cb.setCanContinueMovement(true);
                    }
                }
            }else{
                for (Unit unit : arrAttackers) {
                    if (!unit.isEliminated()){
                        CombatResults cb = new CombatResults(unit);
                        cb.setCanAdvance(true);
                    }
                }
            }
        }

        /**
         * display the result of combat
         * We have turned the display with Americans first so need to change flags
         */
        if (isAllies()) {
            CombatDisplayResults.instance.updateCombatResultsDefender(CombatResults.arrCombatAxis, false, this);
            CombatDisplayResults.instance.updateCombatResultsAttacker(CombatResults.arrCombatAllied, false, this);
        }else{
            CombatDisplayResults.instance.updateCombatResultsDefender(CombatResults.arrCombatAllied, true, this);
            CombatDisplayResults.instance.updateCombatResultsAttacker(CombatResults.arrCombatAxis, true, this);
        }
        CombatDisplayResults.instance.updateResults(dieResult);
        return;

    }

    /**
     *
     * @param object
     * @return true if advance after
     */
    public void afterDisplay(Object object){
        Gdx.app.log("Attack", "AfterDisplay invoked by:"+object.toString());

        /**
         *  defender doesnt retreat and mobile assault stop
         */
        if (!isDefendHexVacant && isMobileAssualt) {
            for (Unit unit : arrAttackers) {
                if (!unit.isEliminated()) {
                    unit.getMapCounter().removeClickAction();
                    unit.getMapCounter().getCounterStack().shade();
                }
            }
            return;
        }
        if (attackerLosses.getAreAlliminated()){
            if (isMobileAssualt){
                MobileAssualt.instance.endMOA();
                return;
            }
            Combat.instance.cleanup(true);
            Combat.instance.doCombatPhase();
        }
        if (isDefendHexVacant){
            if (isMobileAssualt){
                MobileAssualt.instance.continueMove(this);
                return;
            }else{
                if (isAI) {
                    Gdx.app.log("Attack", "Check AIAdvance");

                    if (Hex.arrMajorCities.contains(hexTarget)){
                        AIAdvance aiAdvance = new AIAdvance(this);
                    }
                    setChanged();
                    notifyObservers(new ObserverPackage(ObserverPackage.Type.Advance, null, 0, 0));
                    return;
                }
                AdvanceAfterCombat.instance.doAdvance(this);
                return;
            }
        }
        if (!isAI) {
            Combat.instance.cleanup(true);
            Combat.instance.doCombatPhase();
        }else{
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.AfterAttackDisplay,null,0,0));
        }
        return;

    }
    public int getDieRoll()
    {
        Random diceRoller = new Random();
        int diceResult = diceRoller.nextInt(6) + 1;
//        int die = diceRoller.nextInt(6) + 1;
        int die = (int)(Math.random()*6) + 1;
        return die;

    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  Hex touched
         */
        if (oB.type == ObserverPackage.Type.DiceRollFinished) {
            afterDieRoll();
            DiceEffect.instance.deleteObserver(this);
            return;
        }

    }
 }

class DefenderRetreat {
    /**
     *  need to check stacking
     */
    int losses;
    ArrayList<Hex> arrRetreatPath = new ArrayList<>();
    DefenderRetreat(Attack attack){
        Gdx.app.log("DefenderRetreat","Constructor");

        /**
         * get an array of 3 hexes without checkin terrain
         */
        if (attack.arrDefenders.size() < 1){
            ErrorGame errorGame = new ErrorGame("No Defenders to retreat",this);
            return;
        }
        Unit unitRetreatBase = attack.arrDefenders.get(0);
        UnitMove unitMove = new UnitMove(unitRetreatBase,attack.defenderRetreats,false,false,0);
        ArrayList<Hex> arrUnitCanGo = unitMove.getMovePossible();
        ArrayList<Hex> arrHexPossible = HexHelper.getSurroundinghexes(attack.hexTarget,attack.defenderRetreats);
        arrHexPossible.retainAll(arrUnitCanGo); // intersection
        ArrayList<RetreathPath> arrRetreats = createRetreatPaths(attack.defenderRetreats,arrHexPossible);
        scoreRetreatPath(arrRetreats, attack);
        ArrayList<RetreathPath> arrSorted = sortRetreat(arrRetreats);
        if (arrSorted.size()  == 0) { // no retreats possible
            losses +=attack.defenderRetreats;
        }else {
            /**
             *  take first from sorted
             */
            losses += attack.defenderRetreats - arrSorted.get(0).arrHexPath.size();
            if (arrSorted.get(0).points > 100) {
                losses++;
            }
            for (int i = arrSorted.get(0).arrHexPath.size() - 1; i >= 0; i--) {
                arrRetreatPath.add(arrSorted.get(0).arrHexPath.get(i));
            }
        }
        Gdx.app.log("DefenderRetreat","Losses="+losses);

    }
    public int getLosses(){
        return losses;

    }

    private void scoreRetreatPath(ArrayList<RetreathPath> arrRetreats, Attack attack) {
        /**
         * update for distance to Supply
         */
        for (RetreathPath rep : arrRetreats) {
            float dist = 9999;
            if (attack.arrDefenders.get(0).isAllies) {
                for (Hex hex : Supply.instance.getAlliedSupply()) {
                    float check = HexHelper.findRange(hex, rep.arrHexPath.get(0));
                    if (check < dist) {
                        dist = check;
                    }
                }
            } else {
                for (Hex hex : Supply.instance.getGermanSupply()) {
                    float check = HexHelper.findRange(hex, rep.arrHexPath.get(0));
                    if (check < dist) {
                        dist = check;
                    }
                }

            }
            rep.updatePoints((int) dist);
        }
        /**
         * update for going through zoc
         */
        for (RetreathPath rep : arrRetreats) {
            for (Hex hex : rep.arrHexPath) {
                if (attack.arrDefenders.get(0).isAllies) {
                    if (hex.getAxisZoc(0)) {
                        rep.points += 100;
                    }
                } else {
                    if (hex.getAlliedZoc(0)) {
                        rep.points += 100;
                    }
                }
            }
        }
    }

    private ArrayList<RetreathPath> createRetreatPaths(int defenderRetreats, ArrayList<Hex> arrHexPossible) {
        ArrayList<RetreathPath> arrReturn =  new ArrayList<>();
        int num = defenderRetreats;
        /**
         *  get all hexes that are number away
         */
        ArrayList<Hex> arrDest = new ArrayList<>();

        for (Hex hex:arrHexPossible)
        {
            if (hex.getRange() == num){
                arrDest.add(hex);
            }
        }
        num--;
        /**
         *  max is retreat 2
         */
        for (Hex hex:arrDest){
            if (num > 0){
                for (Hex hex2:hex.getSurround()){
                    if (arrHexPossible.contains(hex2) && hex2.getRange() == num){
                        RetreathPath  retreathPath= new RetreathPath(hex);
                        retreathPath.addHex(hex2);
                        arrReturn.add(retreathPath);
                    }
                }
            }else{
                RetreathPath  retreathPath= new RetreathPath(hex);
                arrReturn.add(retreathPath);
            }

        }
        return arrReturn;
    }

    private ArrayList<RetreathPath> sortRetreat(ArrayList<RetreathPath> arrRetreats) {
        ArrayList<RetreathPath> arrReturn = new ArrayList();
        int i;
        int points = 99999;
        for (RetreathPath rp:arrRetreats) {
            for (i = 0; i < arrReturn.size(); i++) {
                if (rp.points < arrReturn.get(i).points){
                    break;
                }
            }
            arrReturn.add(i,rp);
        }
        return arrReturn;
    }

}

/**
 *  first hex in array is farthest away
 */
class RetreathPath{
    ArrayList<Hex> arrHexPath = new ArrayList<>();
    int points;
    RetreathPath(Hex hex){
        arrHexPath.add(hex);
    }
    public void addHex(Hex hex){
        arrHexPath.add(hex);
    }
    public void updatePoints(int pointsIn){
        points = pointsIn;
    }
}


