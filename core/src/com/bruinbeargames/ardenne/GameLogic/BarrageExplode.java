package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.BottomMenu;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.Explosions;
import com.bruinbeargames.ardenne.UI.FlyingShell;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Random;

/**
 *  This class will run through all the barrages set up  and do the fireworks
 */
public class BarrageExplode {
    public static BarrageExplode instance;
    //   public Phase[] Phases = null;
    boolean isAlliesStart = false;
    public Phase[] Phases = null;
    int cntProcess = 0;
    Button buttonDone;
    float duration;
    private I18NBundle i18NBundle;

    private ArrayList<CombatResults> axisCombatResults;
    private ArrayList<CombatResults> alliedCombatResults;
    private boolean alliedAttacker;
    /**
     * check against table finding first greatest artillery points
     */
    public static  int[] barrageTable = {1,3,5,8,12,17,23};
    public static int[] chanceHit={1,2,3,4,5,8,10,11};
    public static  String[] barrageTableSTR = {" 1 ","2 - 3","4 - 5","6 - 8","9 - 12","13 - 17","18 - 23","24+"};
    public static String[][] barrageResult = {
            {""  ,""  ,""  ,""  ,""  ,""  ,""  ,"DG"},  // fake for die of 1
            {""  ,""  ,""  ,""  ,""  ,""  ,""  ,"DG"},
            {""  ,""  ,""  ,""  ,""  ,""  ,"DG","DG"},
            {""  ,""  ,""  ,""  ,""  ,""  ,"DG","DG"},
            {""  ,""  ,""  ,""  ,""  ,"DG","DG","DG"},
            {""  ,""  ,""  ,""  ,""  ,"DG","DG","DG"},
            {""  ,""  ,  "",""  ,""  ,"DG","DG","DG"},
            {""  ,""  ,""  ,""  ,"DG","DG","DG","1" },
            {""  ,""  ,""  ,"DG","DG","DG","DG","1" },
            {""  ,""  ,"DG","DG","DG","DG","1" ,"1" },
            {""  ,"DG","DG","DG","DG","1" ,"1" ,"2" },
            {"DG","DG","DG","1" ,"1" ,"2" ,"2" ,"2" }};



            Barrage.TargetShooterSave targetShooterSave;


    public ArrayList<Barrage.TargetShooterSave> targetShooterArrayList = new ArrayList<>();
    public static float getPercentHits(int bombardPoints){
        int i=0;
        if (bombardPoints == 0){
            return 0;
        }
        for (i= 0;i <barrageTable.length;++i){
            if (bombardPoints < barrageTable[i]){
                break;
            }
        }
        if (i > barrageTable.length){
            return 1f;
        }else{
            return (float)chanceHit[i-1]/11;
        }
    }
    public static String[] getTable(int barragePoints){
        String[] strRet =  new String[12];
        int i=0;
        for (i=0; i<barrageTable.length;i++){
            if (barragePoints <=  barrageTable[i] ){
                break;
            }
        }
        if (i ==barrageTable.length){
 //           i--;
        }
        for (int j=0; j< 12; j++){
            strRet[j] = barrageResult[j][i];
        }
        return strRet;
    }
    public BarrageExplode() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

        Gdx.app.log("BarrageExplode", "Constructor");
        BottomMenu.instance.setEnablePhaseChange(false);


        axisCombatResults = new ArrayList<>();
        alliedCombatResults = new ArrayList<>();
        Phases = Phase.values();
        /**
         *  cleanupDisplay just in case user has forced nextphase
         */
        Barrage.instance.clearCrossHairs();
        /**
         *  remove listners from prev
         */
        for (Barrage.TargetShooterSave targetShooterSave:Barrage.instance.targetShooterSaveArrayListAxis){
            targetShooterSave.removeListner();
        }
        for (Barrage.TargetShooterSave targetShooterSave:Barrage.instance.targetShooterSaveArrayListAllies){
            targetShooterSave.removeListner();
        }

        /**
         * check if anything to do
         */
        if (Barrage.instance.targetShooterSaveArrayListAllies.size() == 0 &&
                Barrage.instance.targetShooterSaveArrayListAxis.size() == 0) {
            Gdx.app.log("BarrageExplode", "Nothing To Do");
            EventPopUp.instance.show(i18NBundle.get("nobarrage"));
            BottomMenu.instance.setEnablePhaseChange(true);
//            NextPhase.instance.nextPhase();
            return;
        }
        TurnCounter.instance.updateText(i18NBundle.get("barrageexplode"));


        // lets find aggressor and put in correct order for processin
        int phase = NextPhase.instance.getPhase();
        if (Phases[phase] == Phase.GERMAN_BARRAGE_RESOLVE){
            targetShooterArrayList.addAll(Barrage.instance.targetShooterSaveArrayListAxis);
            targetShooterArrayList.addAll(Barrage.instance.targetShooterSaveArrayListAllies);
            alliedAttacker = false;
        }
        else{
            targetShooterArrayList.addAll(Barrage.instance.targetShooterSaveArrayListAllies);
            targetShooterArrayList.addAll(Barrage.instance.targetShooterSaveArrayListAxis);
            alliedAttacker = true;
        }
        cntProcess = 0;
        processNext();
        //createButtonToStart();
    }

    public static void End() {
        Barrage.clearResults();
    }


    /**
     *  Do logic for a barrage
     */
    public void processNext() {
        Gdx.app.log("BarrageExplode", "processNext");

        if (targetShooterArrayList.size() == 0) {

//            BarrageDisplayResults.instance.updateRussianCombatResults(alliedCombatResults);
//            BarrageDisplayResults.instance.updateGermanCombatResults(axisCombatResults);
            BottomMenu.instance.setEnablePhaseChange(true);
            return;
        }

        targetShooterSave = targetShooterArrayList.get(0);
        targetShooterArrayList.remove(0);
        targetShooterSave.removeStack(); // invisible
        int adjustBarrage = getAdjustment(targetShooterSave);
        int die1 = getDieRoll();
        int die2 = getDieRoll();
        int totDie =die1 + die2;
        totDie--; // for zero
 //       totDie = 11;
        Gdx.app.log("BarrageExplode", "die Total="+totDie);


        /**
         *  find table to use
         */
        int table = getTable(targetShooterSave,adjustBarrage);
        String resultTable = barrageResult[totDie][table];
 //       resultTable="DG1";
        if (resultTable.length() == 0){
            resultTable="NE";
        }
        targetShooterSave.updateResult(resultTable);
        boolean isHit=false;
        /**
         *  deplete ammo
         */
        for (Unit unit:targetShooterSave.arrShooters){
            int ammo =  unit.getArtAmmo();
            ammo--;
            unit.setArtAmmo(ammo);
            unit.resetArtillery();
        }
        isHit = true;

 //        Unit unit = getRandomUnitInHex(targetShooterSave.hexTarget);
//        CenterScreen.getInstance().start(targetShooterSave.hexTarget.getHexCounterPosition().x, targetShooterSave.hexTarget.getHexCounterPosition().y, StateEngine.instance);
         if (isHit) {
//             if (targetShooterSave.cntAir == 0) {
                 Gdx.app.log("BarrageExplode", "Hit  Hex=" + targetShooterSave.hexTarget);
                 FlyingShell.instance.start(targetShooterSave);
 //            }else{
 //                doBombardExplode(targetShooterSave);
 //            }
        }
//        BombardDisplay.instance.explode(targetShooterSave);
        /**
         *  TO BE REPLACED with button for next or a timer
         */

    }
    public void endShellFly(Barrage.TargetShooterSave targetShooterSave){
        doBombardExplode(targetShooterSave);
    }

    private int getTable(Barrage.TargetShooterSave targetShooterSave, int adjustBarrage) {
        int totAttack = 0;
        for (Unit unit:targetShooterSave.arrShooters){
            totAttack += unit.getCurrenAttackFactor();
        }
        totAttack += targetShooterSave.cntAir * 5;
        int table =0;
        int i=0;
        for (i=0; i <  barrageTable.length; i++){
            if (totAttack < barrageTable[i])
            {
                table = i;
                break;
            }
        }
        if (i == barrageTable.length){
            table = i;
        }
        table += adjustBarrage;
        if (table <0){
            table = 0;
        }
        if (table >= barrageTable.length ){
            table = barrageTable.length-1;
        }
        return table;
    }

    private int getAdjustment(Barrage.TargetShooterSave targetShooterSave) {
 /**       boolean isAllies = false;
        if (targetShooterSave.isAllies){
            isAllies = true;
        }
        int adjust = -1; // assume no spotter
        ArrayList<Hex> arrSurround = targetShooterSave.hexTarget.getSurround();
        /**
         * check for spotter
         */ /**
        for (Hex hex:arrSurround){
            if (hex.checkAxisInHex() && !isAllies){
                adjust =0;
            }
            if (hex.checkAlliesInHex() && isAllies){
                adjust =0;
            }
        } **/
        int adjust =0;
        if (targetShooterSave.hexTarget.isForest() || targetShooterSave.hexTarget.isCity() ||
            targetShooterSave.hexTarget.isCity()){
            adjust--;
        }
        int steps =0;
        for (Unit unit:targetShooterSave.hexTarget.getUnitsInHex()){
            steps += unit.getCurrentStep();
        }
        if (steps >= 3){
            adjust++;
        }
        return adjust;
    }

    /**
     *  logic to determine if unit is hit and what happens to it
     * @param targetShooterSave
     */
    public void doBombardExplode(Barrage.TargetShooterSave targetShooterSave){

/*
        if (isHit){
            Gdx.app.log("BarrageExplode","afterDisplay hit ");
            if (unit.canStepLoss()){
                unit.reduceStep();
                if (unit.nation.equals(Unit.NationEnum.German) || unit.nation.equals(Unit.NationEnum.Romanian )){
                    CombatResults combatResult = new CombatResults();
                    combatResult.setStepLosses(true);
                    combatResult.setUnitName(unit.designation);
                    axisCombatResults.add(combatResult);
                }else{
                    CombatResults combatResult = new CombatResults();
                    combatResult.setStepLosses(true);
                    combatResult.setUnitName(unit.designation);
                    sovietCombatResults.add(combatResult);
                }
                targetShooterSave.isStepLoss = true;
            }else{
                unit.eliminate();
                if (unit.nation.equals(Unit.NationEnum.German) || unit.nation.equals(Unit.NationEnum.Romanian )){
                    CombatResults combatResult = new CombatResults();
                    combatResult.setDestroyed(true);
                    combatResult.setUnitName(unit.designation);
                    axisCombatResults.add(combatResult);
                }else{
                    CombatResults combatResult = new CombatResults();
                    combatResult.setDestroyed(true);
                    combatResult.setUnitName(unit.designation);
                    sovietCombatResults.add(combatResult);
                }
                targetShooterSave.isEliminate = true;
            }

        }
        else{
            targetShooterSave.isNoEffect = true;
        }*/
        if (targetShooterSave.getaircnt() > 0 && targetShooterSave.isAI){
            Airplane.instance.remove(targetShooterSave.getaircnt());
        }
        Explosions.instance.setPosition(targetShooterSave);
        duration = Explosions.instance.start(0.09f);
        Gdx.app.log("BarrageExplode","explode duration= "+duration);
        /**
         * Timer is 1 second
         */
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               //                              cntProcess++;
                               //                              final int copy = cntProcess;
                               processNext();
                           }
                       }
                , duration        			//    (delay)
        );

    }

    private Unit getRandomUnitInHex(Hex hex) {
        Gdx.app.log("BarrageExplode","getRandomUnits Hex"+hex);

        int numUnits = hex.getUnitsInHex().size();
        int cntUnit = 0;
        if (numUnits == 1) {
            cntUnit = 0;
        }else{
            Random diceRoller = new Random();
            cntUnit = diceRoller.nextInt(numUnits);
        }
        return hex.getUnitsInHex().get(cntUnit);
    }
    public int getDieRoll()
    {
        Random diceRoller = new Random();
        int die = diceRoller.nextInt(6) + 1;
        return die;

    }

}
