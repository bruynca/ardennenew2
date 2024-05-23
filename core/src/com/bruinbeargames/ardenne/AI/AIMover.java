package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.WinModal;

import java.util.ArrayList;

public class AIMover {
    static public AIMover instance;
    private I18NBundle i18NBundle;
    ArrayList<AIOrders> arrToBeScored = new ArrayList<>();
    int[][] bestHexDefenseTurn1 ={{29,9,4},{31,13,4},{33,17,4},{35,20,4},{28,23,4}, // first row of bridges
            {25,8,3},{24,15,3},{29,22,3}, //second row of bridges
            {21,6,3},{18,9,3},{14,10,3},{12,11,3}, // top road
            {28,14,3},{20,14,3},{19,20,3},{16,23,3},{9,23,3}, // bottom road
            {8,11,5},{8,12,5},{19,14,5}};

    boolean isAllies;
    AIMover() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
    }
    public void moveAnalysis(boolean isAllies) {
        this.isAllies = isAllies;
        EventAI.instance.show(i18NBundle.format("aimove"));
        Hex.initAI();
        if (NextPhase.instance.getTurn() == 1){

            loadAIScore(bestHexDefenseTurn1);
            AINew.instance.doAlliesMove();
        }
        /**
         *  kick off appropriate scenario
         */
        int i=0;

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

    private void loadAIScore(int[][] bestHexDefenseTurn) {
        for (int[] hexI:bestHexDefenseTurn){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAI(hexI[2]);
        }

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
