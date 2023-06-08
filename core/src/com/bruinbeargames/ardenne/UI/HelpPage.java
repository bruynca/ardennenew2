package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.AI.AIMain;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;

public class HelpPage {
    I18NBundle i18NBundle;
    public static HelpPage instance;
    Phase phase;
    boolean isOther = false;
    String strOther;
    public HelpPage(){
        instance  =this;
        i18NBundle = GameMenuLoader.instance.localization;
    }


    public void nextPhase() {
        int iPhase = NextPhase.instance.getPhase();
        EventPopUp.instance.hideImmediate();
        if (iPhase == 0){
            int b=0;
        }
        /**
         *  special processing if no cards do not show the help for Allied Card
         */
        if (iPhase == 1){
            if (CardHandler.instance.getAlliedNumberCards(NextPhase.instance.getTurn()) == 0){
                return;
            }
        }
        phase = Phase.values()[iPhase];
        if (phase.isHelp()) {
            if (GamePreferences.getPhaseInfo(phase.toString())) {
                return;  // if its there we dont want to do a thing
            }
            if (AIMain.instance.isHandleAlliedPhase(phase.ordinal())){
                return;
            }
            if (AIMain.instance.isHandleAxisPhase(phase.ordinal())){
                return;
            }
            String str = i18NBundle.format(phase.toString());
            EventPopUp.instance.showHelp(str);
        }
        isOther = false;

    }
    public void showOther(String strOther){
        this.strOther = strOther;
        isOther = true;
        if (!GamePreferences.getPhaseInfo(strOther)){
            String str = i18NBundle.format(strOther);
            EventPopUp.instance.showHelp(str);
        }
    }

    public void dontShowAgain() {
        if (isOther){
            GamePreferences.setOther(strOther);
        }else {
            GamePreferences.setPhaseInfo(phase);
        }
        isOther = false;
    }
}
