package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AIMover {
    static public AIMover instance;
    private I18NBundle i18NBundle;
    ArrayList<AIOrders> arrToBeScored = new ArrayList<>();
    boolean isAllies;
    AIMover() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
    }
    public void moveAnalysis(boolean isAllies) {
        this.isAllies = isAllies;
        EventAI.instance.show(i18NBundle.format("aimove"));
        /**
         *  kick off appropriate scenario
         */

        if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            if (NextPhase.instance.getTurn() < 3){ // first reinforcements  
                AIScenario1.instance.doInitialMoveAlliesTurn1to3();
            }else{
                AIScenario1Turn3to6.instance.doAlliesMove();

            }

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
        AIExecute.instance.moveAndMOA(aiOrders);
    }
}
