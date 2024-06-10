package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.ArrayList;

public class AIMover {
    static public AIMover instance;
    private I18NBundle i18NBundle;
    ArrayList<AIOrders> arrToBeScored = new ArrayList<>();

    int[][] bestHexDefenseTurn1 ={{29,9,4},{31,13,4},{33,17,4},{35,20,4},{28,23,4}, // first row of bridges
            {25,8,3},{24,15,3},{29,22,3}, //second row of bridges
            {21,6,3},{18,9,3},{14,10,3},{12,11,3}, // top road
            {28,14,3},{20,14,3},{19,20,3},{16,23,3},{9,23,3}, // bottom road
            {8,11,5},{8,12,5},{19,14,5},
            {26,8,2},{27,8,2},{25,14,2},{28,13,2},{31,21,2},{11,22,2}};

    boolean isAllies;
    private VisWindow visWindow;

    AIMover() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
    }
    public void moveAnalysis(boolean isAllies) {
        this.isAllies = isAllies;
        EventAI.instance.show(i18NBundle.format("aimove"));
        creatAIWindow();  // for debugging

 /*       if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            if (NextPhase.instance.getTurn() < 3){ // first reinforcements  
                AIScenario1.instance.doInitialMoveAlliesTurn1to3();
            }else{
                AIScenario1Turn3to6.instance.doAlliesMove();

            }

        }
        if ((isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.SecondPanzer) ||
                isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Lehr) {
            if (NextPhase.instance.getTurn() < 3){ // first reinforcements
                AIScenario1.instance.doInitialMoveAlliesTurn1to3();
            }else{
                AIScenarioOther.instance.doAlliesMove();
            }

        }*/

    }

    private void creatAIWindow() {
        Gdx.app.log("AIMOVe", "Create AI Window");

        visWindow = new VisWindow("AI View");
        VisTextButton visTextButton = new VisTextButton("Run");
        visWindow.add(visTextButton);
        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                visWindow.remove();
                AINew.instance.doAlliesMove();
            }

        });
        ardenne.instance.guiStage.addActor(visWindow);
    }


    /**
     *  analysis done
     *   set AIOrder in execute
     *   execute will be kicked off by enter
     * @param aiOrders
     */
    public void execute(AIOrders aiOrders) {
        /**
         *  remove overstacking units
         */
        AIOrders.removeOverStackUnits(aiOrders);
        /**
         *  remove any aiorders that are not going anywhere
         */
        ArrayList<Unit> arrREmove  = new ArrayList<>();
        int ix =0 ;
        for (Unit unit : aiOrders.arrUnit) {
            if (unit.getHexOccupy() == aiOrders.arrHexMoveTo.get(ix)) {
                arrREmove.add(unit);
            }
            ix++;
        }
        aiOrders.remove(arrREmove);
        if (aiOrders.arrHexMoveTo.size() == 0){
            EventAI.instance.hide();
            NextPhase.instance.nextPhase();
      }else {
            AIExecute.instance.moveAndMOA(aiOrders);
        }
    }
}
