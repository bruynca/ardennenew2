package com.bruinbeargames.ardenne;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.UI.BottomMenu;
import com.bruinbeargames.ardenne.UI.TopMenu;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.WinCardsChoice;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;


public class Game {
    NextPhase nextPhase;
    FontFactory fontFactory;
    private I18NBundle i18NBundle;


    static public Game instance;
    Game(String str) {
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
        fontFactory = new FontFactory();
        ardenne.instance.hexStage.clear();
        ardenne.instance.guiStage.clear();
        ardenne.instance.mapStage.clear();

        //       Counter.clearStack();
        Hex.initHex();
        Hex.LoadBridges();
        Unit.resetID();
        Unit.loadAllUnits();
        WinDebug winDebug = new WinDebug();
        nextPhase = new NextPhase();
        System.gc();
        WinModal winModal = new WinModal();
        if (str.length() == 0) {
            ArrayList<Unit> arrAllies =Unit.getSetupUnits(true);
            Unit.resetID();
            Unit.loadUnits(arrAllies);
            ArrayList<Unit> arrAxis =Unit.getSetupUnits(false);
            Unit.loadUnits(arrAxis);
//            nextPhase.nextPhase();
            TopMenu topMenu = new TopMenu();
            /**
             *
             */
            if (GameSetup.instance.isHotSeatGame()){
                TurnCounter.instance.updateText(i18NBundle.format("selectamerican"));
                WinCardsChoice winCardsChoice = new WinCardsChoice(true);
            }else if (GameSetup.instance.isGermanVersusAI()){
                NextPhase.instance.getAiMain().chooseCards();
                TurnCounter.instance.updateText(i18NBundle.format("selectgerman"));
                WinCardsChoice winCardsChoice = new WinCardsChoice(false);
            }else{
                TurnCounter.instance.updateText(i18NBundle.format("selectamerican"));
                WinCardsChoice winCardsChoice = new WinCardsChoice(false);
            }
        }else{
            LoadGame loadGame = new LoadGame(str, false);
            BottomMenu.instance.showBottomMenu();
            TopMenu topMenu = new TopMenu();
        }
    }

    public Scenario getScenario() {
        return null;
    }


    public enum Scenario {Short}
}
