package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameLogic.Airplane;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.CardsforGame;
import com.bruinbeargames.ardenne.GameLogic.LehrHalts;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerHalts;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerLoses;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.WinCardChooseAI;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AICardHandler implements Observer {
    static public AICardHandler instance;
    boolean isAlliesAI = true;
    ArrayList<CardsforGame> arrCards = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsForThisTurn = new ArrayList<>();
    boolean isAllies;

    int[][] hexBridge = {{29,9},{31,13},{26,8}};
    ArrayList<Bridge> arrBlowBridges = new ArrayList<>();
    public AICardHandler(){
        instance = this;
        if (GameSetup.instance.isAlliedVersusAI()){
            isAlliesAI = false;
        }
        for (int[] hexI:hexBridge ){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            Bridge bridge = Bridge.findBridge(hex);
            arrBlowBridges.add(bridge);
        }
    }

    /**
     *  using game parms setup cards for the AI
     *  at this time random
     */
    /* todo change to reflect history

     */
    public void initialize() {
        ArrayList<CardsforGame> arrWork = new ArrayList<>();
        arrWork = CardHandler.instance.getPlayingDeck(isAlliesAI);
        int countCards;
        if (isAlliesAI){
            countCards = CardHandler.instance.getCountAllied();
        }else{
            countCards = CardHandler.instance.getCountGerman();
        }
        arrCards = getRandom(countCards,arrWork);
        Gdx.app.log("AICardHandler", "AI Cards Chosen ="+arrCards);

        CardHandler.instance.setAlliedChosen(arrCards);
    }

    /**
     *  select card by random
     * @param countCards
     * @param arrCards
     * @return
     */
    private ArrayList<CardsforGame> getRandom(int countCards, ArrayList<CardsforGame> arrCards) {
        ArrayList<CardsforGame> arrWork = new ArrayList<>();
        while(arrWork.size() < countCards){
            int len = arrCards.size();
            int die = (int)(Math.random()*len);
            arrWork.add(arrCards.get(die));
            Gdx.app.log("AICardHandler", "added Card="+arrCards.get(die).getDescription());
            arrCards.remove(die);
        }
        return arrWork;
    }

    /**
     *  play the cards  one at a time  by displaying them in WinCardsChoose
     */
    public void doAllied() {
        isAllies = true;
        arrCardsForThisTurn = CardHandler.instance.getCardsForThisTurn(NextPhase.instance.getTurn());
        if (arrCardsForThisTurn.size() ==0 ){
            NextPhase.instance.nextPhase();
            return;
        }
        playCardsOneAtaTime(isAlliesAI);
        return;
    }

    public void playCardsOneAtaTime(boolean isAllies) {
        Gdx.app.log("AICardHandler", "PlayCardsOneataTime");

        if (arrCardsForThisTurn.size() == 0){
            Gdx.app.log("AICardHandler", "NoMoreCards");

            NextPhase.instance.nextPhase();
            return;
        }
        CardsforGame card = arrCardsForThisTurn.get(0);
        arrCardsForThisTurn.remove(0);
        CardHandler.instance.removeCard(card);
 //       playCard(card);
        ArrayList<CardsforGame> arrNew = new ArrayList();
        arrNew.add(card);
        WinCardChooseAI winCardChooseAI = new WinCardChooseAI(isAllies, arrNew, card);
    }
    public void playCard(CardsforGame card){
        switch (card.getDescription()) {
            case "blownbridge":
                /**
                 * blow up all bridges as fast as we can
                 */
                blowBridge();
                break;
            case "2ndpanzerhalts":
                CardHandler.instance.removeCard(card);
                SecondPanzerHalts.instance.addObserver(this);
                SecondPanzerHalts.instance.halt();
                break;
            case "fritz1":
                CardHandler.instance.removeCard(card);

                LehrHalts.instance.addObserver(this);

                LehrHalts.instance.halt();
                break;
            case "2ndpanzerloses2units":
                CardHandler.instance.removeCard(card);
                SecondPanzerLoses.instance.addObserver(this);
                SecondPanzerLoses.instance.removeUnits();
                // let it break removeunits will move on
                break;
            case "prayforweather":
                CardHandler.instance.removeCard(card);
                Airplane.instance.add(5);
                playCardsOneAtaTime(isAllies);
                break;

            default:
                int j = 8/0;
        }
        if(isAllies){
            CardHandler.instance.setAlliedCardPlayed(card);
        }else{
            CardHandler.instance.setGermanCardPlayed(card);
        }
 //       playCardsOneAtaTime(isAllies);

    }


    /**
     *  bridges are blown in the priority
     */
    private void blowBridge() {
        Gdx.app.log("AICardHandler", "Blowing Bridge");
        if (arrBlowBridges.size() == 0){
            int b=0;
        }
        arrBlowBridges.get(0).blowUp();
        arrBlowBridges.remove(0);
        playCardsOneAtaTime(isAllies);
    }

    @Override
    public void update(Observable od, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes kick off processing for that type
         *  else do nothing
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.CardPlayed){
            LehrHalts.instance.deleteObserver(this);
            SecondPanzerLoses.instance.deleteObserver(this);
            SecondPanzerHalts.instance.deleteObserver(this);
            playCardsOneAtaTime(isAllies);
        }
    }
}

