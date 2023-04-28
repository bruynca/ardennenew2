package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;

import java.util.ArrayList;

public class AIReinforcement {
    /**
     *  AI For Reinforcement looking for high scoere
     */
    static public AIReinforcement instance;
    private I18NBundle i18NBundle;
    ArrayList<AIOrders> arrToBeScored = new ArrayList<>();
    ArrayList<AIOrders> arrSupply = new ArrayList<>();
    boolean isAllies;

    AIReinforcement() {
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
    }

    public void reinforceAnalysis(boolean isAllies) {
        arrSupply.clear();
        arrToBeScored.clear();
        this.isAllies = isAllies;
        EventAI.instance.show(i18NBundle.format("aireinforce"));
        /**
         *  supply done before
         */
        arrSupply.addAll(AISupply.instance.getProcessedRoadBlocks());
        /**
         *  kick off appropriate scenario
         */

        if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            /**
             *  if no reinforcements go to next phase
             */
            if (!AIReinforcementScenario1.instance.doReinforcementAllies(isAllies,arrSupply)){
                NextPhase.instance.nextPhase();
            }
        }


    }

 }
