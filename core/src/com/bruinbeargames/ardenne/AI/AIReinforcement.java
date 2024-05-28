package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

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
         *  kick off appropriate scenario
         */

/*        if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
            /**
             *  if no reinforcements go to next phase
             */
            /**
             *  supply done before
             */
  /*          arrSupply.addAll(AISupply.instance.getProcessedRoadBlocks());
            if (!AIReinforcementScenario1.instance.doReinforcementAllies(isAllies,arrSupply)){
                NextPhase.instance.nextPhase();
            }
        }else{
            if (Reinforcement.instance.getThisTurnCount() > 0){
                 AIReinforcementScenarioOther.instance.doReinforcementAllies();
            }else{
                 NextPhase.instance.nextPhase();
             }
       }*/
        AIReinforcementScenarioOther.instance.doReinforcementAllies();


    }

 }
