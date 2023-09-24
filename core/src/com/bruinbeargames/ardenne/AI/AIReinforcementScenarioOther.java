package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;


public class AIReinforcementScenarioOther implements Observer {
    static public AIReinforcementScenarioOther instance;
    ArrayList<Unit> arrReinforcementsThisTurn = new ArrayList<>();
    ArrayList<Hex> arrEnemySurround = new ArrayList<>();
    int ixCurrentReinArea = 0;
    /**
     * set uo for each reinforcement area
     */
    ArrayList<Hex> arrReinforceAreas = new ArrayList<>();
    ArrayList<AIOrders>[] arrOrdersReinArea;
    ArrayList<ArrayList<Hex>[]>[] arrHexMoveReinArea;
    ArrayList<Unit>[] arrUnitReinArea;
    ArrayList<Unit> arrArtillery = new ArrayList<>();
    AIProcess aiProcess;
    Hex hexBastogneReinforceEntry = Hex.hexTable[0][19];
    Hex hexMartelangeReinforceEntry = Hex.hexTable[9][24];
    Hex hexEttlebruckReinforceEntry = Hex.hexTable[28][24];


    public AIReinforcementScenarioOther(){
        instance = this;
        arrReinforceAreas.add(hexBastogneReinforceEntry);
        arrReinforceAreas.add(hexMartelangeReinforceEntry);
        arrReinforceAreas.add(hexEttlebruckReinforceEntry);
    }

    /**
     * For each Reinforcement point develop a package to be handed to the OrderProducer
     *
     * */
    public void doReinforcementAllies() {
        Gdx.app.log("AIReinforcementsOther", "doReinforcementalliea");

        /**
         *  initialize the aiScore used
         */
        Hex.initTempAI();

        arrArtillery.clear();
        /**
         *  initialize the units for each area
         *  if none availabel then there will be zero
         */
        arrUnitReinArea = new ArrayList[3];
        arrOrdersReinArea = new ArrayList[3];
        int ix = 0;
        for (Hex hex : arrReinforceAreas) {
            ArrayList<Unit> arrUnit = new ArrayList<>();
            arrUnit.addAll(Reinforcement.instance.getReinforcementsAvailable(hex, true));
            arrUnitReinArea[ix] = arrUnit;
            ix++;
        }
        ixCurrentReinArea = -1;
        doNextReinArea();
        return;
    }

    private void doNextReinArea() {
        Gdx.app.log("AIReinforcementsOther", "doNextReinArea ix"+ixCurrentReinArea);
        Gdx.app.log("AIReinforcementsOther", "doNextReinArea size ="+arrUnitReinArea[ixCurrentReinArea+1].size());
        /**
         *  initialize the aiScore used
         *  also check for Germa occupied
         */
        Hex.initTempAI();
        Hex.addAIScoreSurroundGerman();

        /**
         *  Do the next area based on ixCurrentInArea
         *  if none go to next
         *  else if none left go to do final
         */
        ixCurrentReinArea++;
        if (ixCurrentReinArea > arrUnitReinArea.length) {
            doFinalAllAreasProcessign();
            return;
        }
        if (arrUnitReinArea[ixCurrentReinArea].size() == 0){
            doNextReinArea();
            return;
        }
        ArrayList<Unit>  arrWorkingOn = new ArrayList<>();
        arrWorkingOn.addAll(arrUnitReinArea[ixCurrentReinArea]);
        /**
         * seperate artillery
         */
        ArrayList<Unit> arrRemove  =  new ArrayList<>();
        for (Unit unit:arrWorkingOn){
            if (unit.isArtillery){
                arrArtillery.add(unit);
                arrRemove.add(unit);
            }
        }
        Gdx.app.log("AIReinforcementsOther", "After Artllery size ="+arrRemove.size());
        arrWorkingOn.removeAll(arrRemove);
        if (arrWorkingOn.size() == 0){
            doNextReinArea();
            return;
        }
        /**
         *  this is our chance to change the aiscoreGen  in Hex
         *  it has been initialized
         *  for now we are not doing amything to keep it simple
         */


        /**
         * Create the moves
         */
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();
        ArrayList<Unit> arrCantMove = new ArrayList<>();
        int entryCost=0;
        for (Unit unit : arrWorkingOn) {
            ArrayList<Hex> arrHexMove = AIReinforcementScenario1.instance.findHexesOnReinforcement(unit,entryCost);
            entryCost++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }
        }
        Gdx.app.log("AIReinforcementsOther", "After cant move size ="+arrWorkingOn.size());

        arrWorkingOn.removeAll(arrCantMove);
        if (arrWork.size() == 0){
            doNextReinArea();
            return;
        }
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment
        AIFaker.instance.addObserver(this);
        aiProcess = new AIProcess(arrWorkingOn,arrWork,arrDupes);
        if (aiProcess.isFailed()){
            doNextReinArea();
            return;
        }
    }

    private void doFinalAllAreasProcessign() {
    }



    @Override
    public void update(Observable observable, Object arg) {
        if (((ObserverPackage) arg).type == ObserverPackage.Type.FakerDone) {
            Gdx.app.log("AIReinforcementsOther", "Received Faker");
            AIFaker.instance.deleteObserver(this);
            /**
             *  orders returned
             *  sort descending
             *  get top 10%
             *  move into storage are for this reinforcement area
             */
            ArrayList<AIOrders> arrWork = new ArrayList<>();
            arrWork.addAll(aiProcess.getAIOrders());
            Collections.sort(arrWork, new AIOrders.SortbyScoreDescending());
            ArrayList<AIOrders> arrTop = AIOrders.gettopPercent(arrWork, .1f);
            arrOrdersReinArea[ixCurrentReinArea] = new ArrayList<>();
            arrOrdersReinArea[ixCurrentReinArea].addAll(arrTop);

            doNextReinArea();
        }

    }
}
