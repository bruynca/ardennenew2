package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.EventAI;

import java.util.ArrayList;

public class AICombat {
    static public AICombat instance;
    private I18NBundle i18NBundle;
    ArrayList<AIOrdersCombat> arrToBeScored = new ArrayList<>();

    boolean isAllies;
    AICombat(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
    }
    public void doCombatAnalysis(boolean isAllies){
        this.isAllies = isAllies;
        EventAI.instance.show(i18NBundle.format("aicombt"));
        /**
         *  kick off appropriate scenario
         */

        if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
    //        if (NextPhase.instance.getTurn() < 3){ // first reinforcements
                AICombatScenario1.instance.doInitialCombatTurn1to3(isAllies);
    //        }
        }else{
            AICombatScenario1.instance.doInitialCombatTurn1to3(isAllies);
        }
    }

    public void doCombatExecute(){

        if (arrToBeScored.size() > 0) {
            Gdx.app.log("AICombat", "docombat execute siz >0");

            AIExecute.instance.Combat(arrToBeScored);
        }else{
            EventAI.instance.hide();
//            WinModal.instance.release();
            NextPhase.instance.nextPhase();
            return;
        }
    }

    /**
     * Finished analysis
     * use as input the arrIn
     * @param arrIN
     **/
    public void setArrToBeScored(ArrayList<AIOrdersCombat> arrIN) {
        Gdx.app.log("AIMover", "setArrToBeScored");
        arrToBeScored.clear();
        arrToBeScored.addAll(arrIN);
        /**
         *  input has been sorted and is viable meaning highest
         */
       AIExecute.instance.Combat(arrToBeScored);
    }

    public ArrayList<Hex> getCombatHex() {
        ArrayList<Hex> arrReturn  = new ArrayList<>();
        for (int i=0;i < arrToBeScored.size(); i++){
            arrReturn.add(arrToBeScored.get(i).hexAttackedByUnits.getHex());
        }
        return arrReturn;

    }
}
