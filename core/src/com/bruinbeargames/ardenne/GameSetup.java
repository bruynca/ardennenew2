package com.bruinbeargames.ardenne;

import com.bruinbeargames.ardenne.GameLogic.CardHandler;

public class GameSetup {

    private boolean hotSeatGame;


    private boolean germanVersusAI;
    private boolean alliedVersusAI;
    private boolean easyGerman;
    private boolean easyAmerican;

    private boolean balanced;

    private Scenario scenario = Scenario.Intro;
   static public GameSetup instance;
   public GameSetup(){
        instance = this;
    }
    public boolean isHotSeatGame() {
        return hotSeatGame;
    }

    public void setHotSeatGame(boolean hotSeatGame) {
        this.hotSeatGame = hotSeatGame;
    }

    public boolean isGermanVersusAI() {
        return germanVersusAI;
    }

    public void setGermanVersusAI(boolean germanVersusAI) {

        this.germanVersusAI = germanVersusAI;
    }


    public void setAlliedVersusAI(boolean isIn) {
        this.alliedVersusAI = isIn;
    }

    public boolean isEasyGerman() {
        return easyGerman;
    }

    public void setEasyGerman(boolean easyGerman) {
        this.easyGerman = easyGerman;
        CardHandler.instance.setCountAllied(2);
        CardHandler.instance.setCountGerman(4);
    }
    public void setScenario(Scenario scenario)
    {
    	this.scenario = scenario;
    }
    public Scenario getScenario()
    {
        return scenario;
    }
    public boolean isEasyAmerican() {
        return easyAmerican;
    }

    public void setEasyAmerican(boolean easyAmerican) {
        this.easyAmerican = easyAmerican;
        CardHandler.instance.setCountAllied(4);
        CardHandler.instance.setCountGerman(2);

    }

    public boolean isBalanced() {
        return balanced;
    }

    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
        CardHandler.instance.setCountAllied(3);
        CardHandler.instance.setCountGerman(3);
    }

    public boolean isAlliedVersusAI() {
        return alliedVersusAI;
    }




    public enum Scenario{Intro(6),SecondPanzer(7),Lehr(8),CounterAttack(10);

            private int length;
            Scenario(int length){
                this.length = length;
            }
            public int getLength(){
                return length;
            }
        }




}
