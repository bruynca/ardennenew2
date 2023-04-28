package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.WinCardChoose;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CardHandler implements Observer {
    static public CardHandler instance;
    private I18NBundle i18NBundle;
    boolean isAllies;
    boolean isAI;
    ArrayList<CardsforGame> arrCardsAllied = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsGerman = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsAlliedChosen = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsGermanChosen = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsAlliedPlayed = new ArrayList<>();
    ArrayList<CardsforGame> arrCardsGermanPlayed = new ArrayList<>();
    String[] descriptionGerman = {"hooufgas","moreammo","szorney1","fixbridge","szorney2"};
    int[]cardGermanNumber = {1,1,1,1,1};
    int[] weightGerman = {1,1,1,1,1}; // not used at moment
    int [] startGerman = {1,1,1,2,1};
    boolean[] isConfirmGerman = {true,true,true,false,true} ;


    String[] descriptionAllied = {"2ndpanzerhalts","2ndpanzerloses2units","blownbridge","fritz1","prayforweather"};
    int[]cardAlliedNumber = {1,1,3,1,1};
    int[] weightAllied = {1,1,1,1,1};
    int [] startAllied = {3,4,1,3,3};
    boolean[] isConfirmAllied = {true,true,false,true,true} ;
    private int countGerman;
    private int countAllied;
    private boolean isJunctionSet = false;
    WinCardChoose winCardChoose;


    public CardHandler(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;
        /**
         *  load German Cards
         */
        for (int i=0; i< descriptionGerman.length; i++){;
            Image image = new Image(SplashScreen.instance.cardManager.get("cards/"+descriptionGerman[i]+".jpg", Texture.class));
            Drawable drawable = image.getDrawable();
            CardsforGame card = new CardsforGame(cardGermanNumber[i],weightGerman[i],false,descriptionGerman[i], drawable,startGerman[i]);
            arrCardsGerman.add(card);
            card.setConfirm(isConfirmGerman[i]);
        }
        /**
         *  load Allied Cards
         */
        for (int i=0; i< descriptionAllied.length; i++){;
            Image image = new Image(SplashScreen.instance.cardManager.get("cards/"+descriptionAllied[i]+".jpg", Texture.class));
            Drawable drawable = image.getDrawable();

            CardsforGame card = new CardsforGame(cardAlliedNumber[i],weightAllied[i],true,descriptionAllied[i], drawable, startAllied[i]);
            arrCardsAllied.add(card);
            card.setConfirm(isConfirmAllied[i]);

        }

    }

    public void setCountAllied(int countAllied) {
        this.countAllied = countAllied;
    }
    public int getCountAllied(){
        return countAllied;
    }
    public void setCountGerman(int countGerman){
        this.countGerman = countGerman;
    }

    public int getCountGerman() {
        return countGerman;
    }

    public ArrayList<CardsforGame> getAvailableCards(boolean isAllies) {
        if (isAllies){
            return arrCardsAllied;
        }else{
            return arrCardsGerman;
        }
    }
    public void setGermanChosen(ArrayList<CardsforGame> arrIn){
        arrCardsGermanChosen.clear();
        arrCardsGermanChosen.addAll(arrIn);
    }
    public void setAlliedChosen(ArrayList<CardsforGame> arrIn){
        arrCardsAlliedChosen.clear();
        arrCardsAlliedChosen.addAll(arrIn);
    }
    public void setGermanPlayed(ArrayList<CardsforGame> arrIn){
        arrCardsGermanPlayed.clear();
        arrCardsGermanPlayed.addAll(arrIn);
    }
    public void setAlliedPlayed(ArrayList<CardsforGame> arrIn){
        arrCardsAlliedPlayed.clear();
        arrCardsAlliedPlayed.addAll(arrIn);
    }
    public void setAlliedCardPlayed(CardsforGame card){

        arrCardsAlliedPlayed.add(card);
    }
    public void setGermanCardPlayed(CardsforGame card){
        arrCardsGermanPlayed.add(card);
    }

    public ArrayList<CardsforGame> getArrCardsChosenAllied() {
        return arrCardsAlliedChosen;
    }
    public ArrayList<CardsforGame> getArrCardsChosenGerman() {
        return arrCardsGermanChosen;
    }

    public CardsforGame getByKey(String desc) {
        for (CardsforGame cardsforGame:arrCardsAllied){
            if (desc.compareTo(cardsforGame.description) == 0){
                return cardsforGame;
            }
        }
        for (CardsforGame cardsforGame:arrCardsGerman){
            if (desc.compareTo(cardsforGame.description) == 0){
                return cardsforGame;
            }
        }
        return null;
    }

    public void alliedCardPhase(int turn) {
        Gdx.app.log("CardHandler", "AlliedCardPhase turn="+turn);
        if (EventPopUp.instance.isShowing()){
            EventPopUp.instance.addObserver(this);
            return;
        }

        /**
         * do Allied Card phases
         */
        ArrayList<CardsforGame> arrValidCards = new ArrayList<>();
        arrValidCards = getCardsForThisTurn(turn);
        if (arrValidCards.size() > 0) {
            winCardChoose = new WinCardChoose(true, arrValidCards);
        }else{
            NextPhase.instance.nextPhase();
        }
    }

    /**
     *  added for AI to get cards
     * @param turn
     * @return
     */
    public ArrayList<CardsforGame> getCardsForThisTurn(int turn){
        ArrayList<CardsforGame> arrValidCards = new ArrayList<>();
        if (arrCardsAlliedChosen.size() > 0){
           for (CardsforGame cardsforGame:arrCardsAlliedChosen){
                if (cardsforGame.turnStart <= turn)
                {
                    arrValidCards.add(cardsforGame);
                }
            }
        }
        return arrValidCards;
    }

    public void germanCardPhase(int turn) {
        Gdx.app.log("CardHandler", "GermanCardPhase turn="+turn);
        if (EventPopUp.instance.isShowing()){
            EventPopUp.instance.addObserver(this);
            return;
        }

        /**
         *  turn off sign posts  only on for 1 turn
         */

        if (isJunctionSet){
            setJunctionSet(false);
            SignPost.instance.remove(turn);
        }
        /**
         * do GermanCard phases
         */
        if (arrCardsGermanChosen.size() > 0){
            ArrayList<CardsforGame> arrValidCards = new ArrayList<>();
            for (CardsforGame cardsforGame:arrCardsGermanChosen){
                if (cardsforGame.turnStart <= turn)
                {
                    arrValidCards.add(cardsforGame);
                }
            }
            checkValidCardsLogic(arrValidCards);

            if (arrValidCards.size() > 0) {
                WinCardChoose winCardChoose = new WinCardChoose(false, arrValidCards);
                return;
            }else{
                if (BlowBridge.instance.checkBridgeBlow()) {
                    NextPhase.instance.nextPhase();
                }
            }
        }else{
            if (BlowBridge.instance.checkBridgeBlow()) {
                NextPhase.instance.nextPhase();
            }
        }
        if (winCardChoose != null) {
            winCardChoose.closeWindowLogic(); // just in case
        }

    }



    /**
     *  check that card can be played
     *  we have checked turn start already
     * @param arrValidCards - cards removed
     */
    private void checkValidCardsLogic(ArrayList<CardsforGame> arrValidCards) {
        ArrayList<CardsforGame> arrRemove = new ArrayList<>();
        for (CardsforGame cardsforGame:arrValidCards){

            if (cardsforGame.description.contains("szorney1")){
                boolean isInclude = false;
                for (Bridge bridge:Bridge.arrBridges){
                    if (bridge.getBlown()){
                        if (bridge.getTurnBlown() == NextPhase.instance.getTurn()){
                            isInclude = true;
                            break;
                        }

                    }
                }
                if (!isInclude){
                    arrRemove.add(cardsforGame);
                }
            }
            if (cardsforGame.description.contains("fixbridge")){
                boolean isInclude = false;
                for (Bridge bridge:Bridge.arrBridges){
                    if (bridge.getBlown()){
                        if ((bridge.getTurnBlown()+1) == NextPhase.instance.getTurn()){
                            isInclude = true;
                            break;
                        }

                    }
                }
                if (!isInclude){
                    arrRemove.add(cardsforGame);
                }
            }
            if (cardsforGame.description.contains("hooufgas")){
                boolean isInclude = false;
                Hex hexHouf = Hex.hexTable[12][3];
                if (!hexHouf.isAxisEntered()){
                    arrRemove.add(cardsforGame);
                }
            }

        }
        arrValidCards.removeAll(arrRemove);
    }

    public void removeCard(CardsforGame cardsforGame) {
        if (cardsforGame.isAllies){
            arrCardsAlliedChosen.remove(cardsforGame);
        }else{
            arrCardsGermanChosen.remove(cardsforGame);
        }
    }


    public ArrayList<CardsforGame> getArrCardsPlayedAllied() {
        ArrayList<CardsforGame> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrCardsAlliedPlayed);
        return arrReturn;
    }
    public ArrayList<CardsforGame> getArrCardsPlayedGerman() {
        ArrayList<CardsforGame> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrCardsGermanPlayed);
        return arrReturn;
    }

    /**
     *  load all effects of played cards.
     *  Do not do bridges
     */
    //    "hooufgas","moreammo","szorney1","fixbridge","szorney2"};
    public void loadGame() {
        for (CardsforGame cardsforGame:arrCardsGerman){
            switch (cardsforGame.description) {


                case "moreammo":
                    cardsforGame.setMoreAmmo();
                    break;
                case "hooufgas": // handled in logic for load
 //                   HooufGas.instance.setHooufGas();
                    break;
                case "szorney2": // handled in logic for turn
                    if (cardsforGame.getTurnPlayed() == NextPhase.instance.getTurn()) {
                        cardsforGame.addSignPosts(NextPhase.instance.getTurn());
                    }
                    break;
                case "2ndpanzerhalts":
                    if (cardsforGame.getTurnPlayed() == NextPhase.instance.getTurn() &&
                         NextPhase.instance.getPhase() < Phase.GERMAN_POST_MOVEMENT.ordinal()) {
                         SecondPanzerHalts.instance.halt();
                    }
                    break;
                case "fritz1":
                    if (cardsforGame.getTurnPlayed() == NextPhase.instance.getTurn()&&
                        NextPhase.instance.getPhase() < Phase.GERMAN_POST_MOVEMENT.ordinal()) {
                        LehrHalts.instance.halt();
                    }
                default:
                    break;

            }
        }
    }

    public boolean isJunctionSet() {
        return  isJunctionSet;
    }
    public void setJunctionSet(boolean in){
        isJunctionSet = in;
    }
    public void cleanLastTurn(int turn) {
        /**
         * no cleanup for now
         */
        NextPhase.instance.nextPhase();

    }

    public ArrayList<CardsforGame> getPlayingDeck(boolean isAlliesAI) {

        ArrayList<CardsforGame> arrReturn = new ArrayList();
        if (isAlliesAI){
            for (CardsforGame card:arrCardsAllied){
                for (int i=0; i< card.getNumber();i++ ){;
                    arrReturn.add(card);
                }
            }

        }else{
            for (CardsforGame card:arrCardsGerman){
                for (int i=0; i< card.getNumber();i++ ){;
                    arrReturn.add(card);
                }
            }
        }
        return arrReturn;
    }

    @Override
    public void update(Observable o, Object arg) {
        ObserverPackage oB = (ObserverPackage) arg;
        if (oB.type == ObserverPackage.Type.EVENTPOPUPHIDE){
            EventPopUp.instance.deleteObserver(this);
            if (NextPhase.instance.getPhase() == Phase.ALLIED_CARD.ordinal())
            {
                alliedCardPhase(NextPhase.instance.getTurn());
            }else if (NextPhase.instance.getPhase() == Phase.GERMAN_CARD.ordinal()){
                germanCardPhase(NextPhase.instance.getTurn());
            }
        }
    }
}


