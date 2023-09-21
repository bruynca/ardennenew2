package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;


public class CardsforGame {
    int number;
    int weight;
    boolean isAllies;
    String description;
    Drawable drawable;
    int turnStart;
    int turnPlayed;
    int turnEnd;



    boolean isConfirm = false;

    CardsforGame(int number, int weight, boolean isAllies, String description, Drawable drawable, int turnStart) {
        this.number = number;
        this.weight = weight;
        this.isAllies = isAllies;
        this.drawable = drawable;
        this.description = description;
        this.turnStart = turnStart;
    }

    public void setTurnPlayed(int turn) {
        turnPlayed = turn;
    }

    public int getTurnPlayed() {
        return turnPlayed;
    }

    public int getTurnEnd() {
        return turnPlayed;
    }

    public void setTurnEnd(int turnEnd) {
        turnEnd = turnEnd;
    }


    public Drawable getDrawable() {
        return drawable;
    }

    public String getDecriptionKey() {
        return description;
    }

    public Integer getCardsAllowed() {
        return number;
    }
    public int getNumber(){
        return number;
    }

    public boolean getAllied() {
        return isAllies;
    }

    public int getTurnStart() {
        return turnStart;
    }

    public void setConfirm(boolean b) {
        isConfirm = b;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    /**
     * do the effect of the card
     */
    public void doEffect() {
        boolean isAllies = false;
        setTurnPlayed(NextPhase.instance.getTurn());
        switch (description) {
            case "moreammo":
                isAllies = false;
                MoreGermanAmmo.instance.setON();
                setMoreAmmo();
                CardHandler.instance.removeCard(this);
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
                break;
            case "blownbridge":
                isAllies = true;
                BlowBridge.instance.display(this);
                break;
            case "szorney1":
                isAllies = false;
                FixBridge.instance.display(this);
                break;
            case "fixbridge":
                isAllies = false;
                FixBridge.instance.display(this);
                break;
            case "szorney2":
                isAllies = false;
                addSignPosts(NextPhase.instance.getTurn());
                CardHandler.instance.removeCard(this);
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
                break;
            case "2ndpanzerhalts":
                SecondPanzerHalts.instance.halt();
                CardHandler.instance.removeCard(this);
                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
                break;
            case "fritz1":
                LehrHalts.instance.halt();
                CardHandler.instance.removeCard(this);
                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
                break;
            case "hooufgas":
                HooufGas.instance.setHooufGas();
                CardHandler.instance.removeCard(this);
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
                break;
            case "2ndpanzerloses2units":
                CardHandler.instance.removeCard(this);
                SecondPanzerLoses.instance.removeUnits();
                // let it break removeunits will move on
                break;
            case "prayforweather":
                Airplane.instance.add(10);
                CardHandler.instance.removeCard(this);
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
                break;

            default:

        }
        // check cards available

    }

    /**
     *
     */
    void addSignPosts(int turn) {
        CardHandler.instance.setJunctionSet(true);
        SignPost.instance.display(turn);
    }


    void setMoreAmmo() {
        for (Unit unit : Unit.getOnBoardAxis()) {
            unit.setArtAmmo(2);
        }
        MoreGermanAmmo.instance.setON();
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
