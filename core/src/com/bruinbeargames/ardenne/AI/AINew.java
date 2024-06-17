package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.WinReinforcements;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

public class AINew implements Observer {
    static public AINew instance;
    AIProcess aiProcess;
    private VisWindow visWindow;

    AINew(){
        instance = this;
    }
    ArrayList<Unit>[] arrUnitArea;
    ArrayList<AIOrders>[] arrOrdersArea;
    ArrayList<Unit> arrArtillery = new ArrayList<>();

    int ixCurrentArea =0;
    ArrayList<Unit> arrUnit = new ArrayList<>();
    ArrayList<Hex>[] arrMoves;


    public void doAlliesMove() {
        /**
         * do the AIAlled Move
         * Assume all aiscores have been set
         * Get all allied units except for Artilley if past game turn 3
         */
        Gdx.app.log("AINew", "doAlliesMove");

        ArrayList<Unit> arrArtillery = new ArrayList<>();
        ArrayList<Unit> arrUnit = new ArrayList<>();
        for (Unit unit : Unit.getOnBoardAllied()) {
            if (unit.getMovedLast() < NextPhase.instance.getTurn()) {
                if (unit.isArtillery && NextPhase.instance.getTurn() > 3) {
                    arrArtillery.add(unit);
                } else {
                    arrUnit.add(unit);
                }
            }
        }
        /**
         *  set up moves for AI Seup only
         *  Real moves don in doNext
         */
        arrMoves = AIUtil.getUnitsMaxMove(arrUnit,0, false);
        if (arrUnit.size() == 0) {
            Gdx.app.log("AINew", "No Units  go to next phase");

            EventAI.instance.hide();
            NextPhase.instance.nextPhase();
            return;
        }
        /**
         *  initialize the areas
         */
        int cntAreas = arrUnit.size() / 3;
        arrUnitArea = new ArrayList[cntAreas];
        arrOrdersArea = new ArrayList[cntAreas];
        for (int i = 0; i < cntAreas; i++) {
            arrUnitArea[i] = new ArrayList<Unit>();
            arrOrdersArea[i] = new ArrayList<AIOrders>();
        }
        /**
         *  spread units to the area
         */
        int ix = 0;
        for (Unit unit : arrUnit) {
            arrUnitArea[ix].add(unit);
            ix++;
            if (ix == cntAreas) {
                ix = 0;
            }
        }
        ixCurrentArea = -1;
        doNextArea();
        return;
    }

    private void doNextArea() {
        Gdx.app.log("AINew", "doNextArea ix" + ixCurrentArea);
        ixCurrentArea++;
        if (ixCurrentArea == 0) {
            setupAiScoreandFaker();
        }else {
            doNextActual();
        }

        return;

    }

    private void doNextActual() {
        Gdx.app.log("AINew", "doNextActual" + ixCurrentArea);
        if (ixCurrentArea >= arrUnitArea.length) {
            doFinalAllAreasProcessign();
            return;
        }
        ArrayList<Unit>  arrWorkingOn = new ArrayList<>();
        arrWorkingOn.addAll(arrUnitArea[ixCurrentArea]);
        /**
         * seperate artillery
         */
        ArrayList<Unit> arrRemove  =  new ArrayList<>();
        arrArtillery.clear();
        for (Unit unit:arrWorkingOn){
            if (unit.isArtillery){
                arrArtillery.add(unit);
                arrRemove.add(unit);
            }
        }
        Gdx.app.log("AINew", "After Artllery size ="+arrRemove.size());
        arrWorkingOn.removeAll(arrRemove);
        if (arrWorkingOn.size() == 0){
            doNextArea();
            return;
        }


        /**
         * Create the moves
         */
        ArrayList<ArrayList<Hex>> arrWork = new ArrayList<>();
        ArrayList<Unit> arrCantMove = new ArrayList<>();
        int entryCost=0;
        /**
         *  create moves only for hexes that have a count
         */
        for (Unit unit : arrWorkingOn) {
            ArrayList<Hex> arrNoAI = new ArrayList<>();
            ArrayList<Hex> arrHexMove = AIReinforcementScenario1.instance.findHexesOnReinforcement(unit,entryCost);
            entryCost++;
            AIUtil.RemoveDuplicateHex(arrHexMove);
//            arrHexMove.removeAll(arrCovered); use in reinforcemets ????
            for (Hex hex:arrHexMove){
                if (hex.getAiScore() == 0){
                    arrNoAI.add(hex);
                }
            }
            arrHexMove.removeAll(arrNoAI);
            if (arrHexMove.size() > 0) {
                arrWork.add(arrHexMove);
            } else {
                arrCantMove.add(unit);
            }
        }
        Gdx.app.log("AINewr", "After cant move size ="+arrWorkingOn.size());

        arrWorkingOn.removeAll(arrCantMove);
        if (arrWork.size() == 0){
            doNextArea();
            return;
        }
        int ix=0;
        for (ArrayList<Hex> arr:arrWork){
            ArrayList<Hex> arrDelete = new ArrayList<Hex>();
            for (Hex hex:arr){
                if (hex.getAiScore() == 0){
                    if (!arrDelete.contains(hex)){
                        arrDelete.add(hex);
                    }
                }
            }
            arr.removeAll(arrDelete);
            /**
             * if no place to move leave it where it is
             */
            if (arr.size() == 0){
                Gdx.app.log("AINew", "unit has no move-"+arrWorkingOn.get(ix));
                arr.add(arrWorkingOn.get(ix).getHexOccupy());
            }
            ix++;
        }
        ArrayList<Hex> arrDupes = new ArrayList<>(); // no dupes at moment
        aiProcess = new AIProcess(arrWorkingOn,arrWork,arrDupes,99);
        if (aiProcess.isFailed()){
            doNextArea();
            return;
        }else{
            AIFaker.instance.addObserver(this);
        }


    }

    private void doFinalAllAreasProcessign() {
        AIOrders aiStart = new AIOrders();
        for (ArrayList<AIOrders> arr:arrOrdersArea){
            if (arr != null){
                AIOrders aiNew = AIOrders.combine(aiStart,arr.get(0),true);
                aiStart = aiNew;
            }
        }
        Gdx.app.log("AIReinforcementScenarioOther", "do final processing # units="+aiStart.arrUnit.size());

        if (arrArtillery.size() > 0){
            doArtillery(aiStart);
        }
        Gdx.app.log("AIReinforcementScenarioOther", "execute");
        execute(aiStart);

    }

    private void doArtillery(AIOrders aiStart) {
    }

    private void setupAiScoreandFaker() {
        AISetScore.instance.scoreMove(arrUnit,arrMoves);
        //       doNextRinActual();
        creatAIWindow();
    }
    private void creatAIWindow() {
        Gdx.app.log("AINewr", "Create AI Window");

        visWindow = new VisWindow("AI View");
        VisTextButton visTextButton = new VisTextButton("Run");
        visWindow.add(visTextButton);
        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                visWindow.remove();
                doNextActual();
            }

        });
        ardenne.instance.guiStage.addActor(visWindow);
    }



   private  void execute(AIOrders aiOrdersIn){
        Gdx.app.log("AIScenarioOther", "execute");
        AIMover.instance.execute(aiOrdersIn);
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
            ArrayList<AIOrders> arrTop = AIOrders.gettopNumber(arrWork, 1);
            arrOrdersArea[ixCurrentArea] = new ArrayList<>();
            arrOrdersArea[ixCurrentArea].addAll(arrTop);

            doNextArea();
        }

    }
}
