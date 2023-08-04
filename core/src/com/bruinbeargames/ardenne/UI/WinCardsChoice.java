package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.CardsforGame;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class WinCardsChoice {
    /**
     *  Window to display cards to select for game
     */
    Window window;
    Stage stage;
    Label label;
    Table table;
    float topShim = 100f;
    float midshim = 48f;
    float winsScale = .80f;
    float winWidth = 1000;
    float winHeight = 600;
    private I18NBundle i18NBundle;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    static TextureAtlas textureAtlas2 = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion shaded =  textureAtlas2.findRegion("moved");

    boolean isPreviousClose = false;
    boolean isAllies = false;
    int choices = 0;
    ArrayList<CardsforGame> arrCardToChoose = new ArrayList();
    ArrayList<Stack> arrStackToChoose  = new ArrayList();
    ArrayList<Integer> arrCardsAllowed = new ArrayList<>();
    CardsforGame[] cardsChosen;
    Stack[] stackChosen;
    Texture empty = SplashScreen.instance.cardManager.get("cards/empty.png", Texture.class);
    Texture hilite = SplashScreen.instance.cardManager.get("cards/hilite.png", Texture.class);
    float perCard;
    static float scaleCardImage = .4f;
    float imgCardHeight =0;
    float imgCardWidth =0;
    TextureRegion ok =  textureAtlas.findRegion("ok");


    public WinCardsChoice(boolean isAllies){
        Gdx.app.log("WinCardsChoice", "Constructor allied="+isAllies);

        i18NBundle = GameMenuLoader.instance.localization;
        this.isAllies = isAllies;
        initializeWindow();
        setCloseWindowImage();
        initObjectsandSetWindowSize();
        initChoices();

        window.setWidth(winWidth);
        window.setHeight(winHeight);
        window.setModal(true);
        window.setTransform(true);
 //       window.setScale(winsScale);  done in width and height
        centerScreen();
        ardenne.instance.guiStage.addActor(window);
        EventPopUp.instance.show(i18NBundle.format("cardselection"));


    }



    private void initializeWindow() {
        /**
         *  initialize window
         */
        Gdx.app.log("WinCardsChoice", "InitializeWindow");

        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("cardstart");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);

    }
    private void setCloseWindowImage() {
        Gdx.app.log("WinCardsChoice", "setCloseWindowImage");

        /**
         *  set close for window and logic
         */
        Image image = new Image(ok);
        image.setScale(2.0f);

        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeWindowLogic();

            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("endCardSelection"),
                tooltipStyle);
        image.addListener(hitOK);

    }

    private void closeWindowLogic() {
        Gdx.app.log("WinCardsChoice", "closeWindowLogic");

        /**
         *  checl all cards there
         */
        boolean isAllCardsChosen = true;
        for (CardsforGame cardsforGame:cardsChosen){
            if (cardsforGame == null){
                isAllCardsChosen = false;
            }
        }
        if (!isAllCardsChosen && !isPreviousClose){
            isPreviousClose = true;
            EventPopUp.instance.show(i18NBundle.format("previousclose"));
            return;
        }else{
            ArrayList<CardsforGame> arrWork = new ArrayList<>();
            for (CardsforGame cardsforGame:cardsChosen) {
                if (cardsforGame != null) {
                    arrWork.add(cardsforGame);
                }
            }
             if (isAllies){
               CardHandler.instance.setAlliedChosen(arrWork);
            }else{
                CardHandler.instance.setGermanChosen(arrWork);
            }
            window.remove();
            if (isAllies) {
                TurnCounter.instance.updateText(i18NBundle.format("selectgerman"));
                 WinCardsChoice winCardsChoice = new WinCardsChoice(false);
            }else{
                NextPhase.instance.nextPhase();
            }

        }
    }
    private void initObjectsandSetWindowSize() {
        Gdx.app.log("WinCardsChoice", "initObjectsandSetWindowSize");

        arrCardToChoose.addAll(CardHandler.instance.getAvailableCards(isAllies));

        /**
         * all the cards have to fit on the screen at present time we have 5
         * cards.
         * limit will be set by screen width
         */

        float screenWidth = Gdx.graphics.getWidth();
        screenWidth -= 20; // for borders.
        winWidth = screenWidth * winsScale;
        int xOffsett = 4;

//        perCard = screenWidth/ arrCardToChoose.size();
        perCard = screenWidth/ 5;
        perCard -= 10; // border
        for (CardsforGame card: arrCardToChoose){
            arrCardsAllowed.add(card.getCardsAllowed());
            Stack stack = createCardStack(card);
            arrStackToChoose.add(stack);
           setListnersAvailable(stack, card);

        }

        winHeight = (((imgCardHeight *scaleCardImage + 20)*2) * winsScale) + topShim+ midshim;
        float y=((winHeight - 25) - imgCardHeight *scaleCardImage); // below title bar

        for (Stack stack:arrStackToChoose){
            window.addActor(stack);
            stack.setPosition(xOffsett,y);
            xOffsett += imgCardWidth *scaleCardImage+ 10;
        }
  //      window.add(table);
    }

    private Stack createCardStack(CardsforGame card) {
        Image image = new Image(card.getDrawable());
        imgCardWidth = image.getWidth();
        imgCardHeight = image.getHeight();

        Stack stack = new Stack();
        stack.setScale(winsScale);
        stack.add(image);
        stack.setWidth(imgCardWidth);
        stack.setHeight(imgCardHeight);
        stack.setTransform(true);
        scaleCardImage = (perCard/ imgCardWidth) * winsScale;
        stack.setScale(scaleCardImage);
        return stack;
    }


    private void setListnersAvailable(final Stack stack, CardsforGame card) {
        /**
         *  create the listners for card to be chosen
         */
        final CardsforGame cardInput = card;
        /**
         * create tooltip
         */
        String key =card.getDecriptionKey();
//        Gdx.app.log("WinCardsChoice", "initcards str="+key);

        String strTip = i18NBundle.format(key);
//        Gdx.app.log("WinCardsChoice", "initcards str="+strTip);
        hitOK = new TextTooltip(
                strTip,
                tooltipStyle);
       stack.addListener(hitOK);
        /**
         * create touchups
         */
        stack.addListener(new ClickListener() {

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //              Gdx.app.log("Counter ", "enter unit="+unit);
   //             stack.setScale(1.0F);
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //              Gdx.app.log("Counter", "exit unit="+unit);
   //             stack.setScale(1.0f);
            }
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //              Gdx.app.log("Counter","TouchDown unit="+unit);
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //             Gdx.app.log("Counter","TouchUp");
                if (event.getButton( ) == Input.Buttons.LEFT)
                {
                    cardChosen(cardInput);
                }
            }

        });
    }

    /**
     * A Card Available has been chosen
     * @param cardInput
     */
    private void cardChosen(CardsforGame cardInput) {
        int ix = arrCardToChoose.indexOf(cardInput); // find card pressed
        Stack stackChosen = arrStackToChoose.get(ix);

        /**
         *  check that  that card only has 1 image else it has a darken on top.
         */
        if (stackChosen.getChildren().size > 1){
            EventPopUp.instance.show(i18NBundle.format("cardblocked"));
            return;
        }
        /**
         *  check there is room available to chhose card
         */
        int slotFound = 99;

        for (int i=0; i<cardsChosen.length; i++){;
            if (cardsChosen[i] == null){
                slotFound = i;
                break;
            }
        }
        if (slotFound != 99){
            moveCardtoChosen(cardInput, slotFound);
            return;
        }else{
            EventPopUp.instance.show(i18NBundle.format("nocardroom"));
            return;
        }
    }

    private void moveCardtoChosen(CardsforGame cardInput, int slotFound) {
        /**
         * move cards from available to chosen
         */

        /**
         *  shade out cards
         */
        int ix = arrCardToChoose.indexOf(cardInput);
        int num = arrCardsAllowed.get(ix);
        num--;
        arrCardsAllowed.set(ix,num);
        if (num == 0){
            Stack stack = arrStackToChoose.get(ix);
            Image shade = new Image(shaded);
            stack.add(shade);
        }
        addCardToChosenDisplay(cardInput, slotFound);
        return;

    }

    private void addCardToChosenDisplay(CardsforGame cardInput, int slotFound) {
        /**
         * display card in chosen area
         */
        Stack stack = createCardStack(cardInput);
        cardsChosen[slotFound] = cardInput;
        float x =stackChosen[slotFound].getX();
        float y =stackChosen[slotFound].getY();
        float scaleX = stackChosen[slotFound].getScaleX();
        float scaleY = stackChosen[slotFound].getScaleY();
        stackChosen[slotFound].remove();
        stackChosen[slotFound] = stack;
        stackChosen[slotFound].setPosition(x,y);
        stackChosen[slotFound].setScale(scaleX,scaleY);
        window.addActor(stackChosen[slotFound]);
        setListnersChoice(stackChosen[slotFound], cardInput);

    }
    private void setListnersChoice(Stack stack, CardsforGame card) {
        /**
         *  create the listners for card to be chosen
         */
        final CardsforGame cardInput = card;
        /**
         * create tooltip
         */

        String strTip = i18NBundle.format("unchoosecard");
//        Gdx.app.log("WinCardsChoice", "initcards str="+strTip);

        hitOK = new TextTooltip(
                strTip,
                tooltipStyle);
        /**
         * create touchups
         */
        stack.addListener(new ClickListener() {

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //              Gdx.app.log("Counter ", "enter unit="+unit);
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //              Gdx.app.log("Counter", "exit unit="+unit);
            }
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //              Gdx.app.log("Counter","TouchDown unit="+unit);
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //             Gdx.app.log("Counter","TouchUp");
                if (event.getButton( ) == Input.Buttons.LEFT)
                {
                    cardUnChosen(cardInput);
                }
            }

        });

    }

    private void cardUnChosen(CardsforGame cardInput) {
        /**
         *  remove card from chosen
         */
      int slotFound = 0;
        for (int i=0; i<cardsChosen.length; i++){;
            if (cardsChosen[i] == cardInput){
                slotFound = i;
                break;
            }
        }
        float x =stackChosen[slotFound].getX();
        float y =stackChosen[slotFound].getY();
        cardsChosen[slotFound] = null;
        stackChosen[slotFound].remove();
        /**
         *  replace with blank
         */
        Stack stack = createUnchosenStack();
        stackChosen[slotFound] = stack;
        stackChosen[slotFound].setPosition(x,y);
        window.addActor(stackChosen[slotFound]);
        /**
         *  update the count on available
         */
        int ix = arrCardToChoose.indexOf(cardInput);
        int cnt  = arrCardsAllowed.get(ix);
        cnt++;
        arrCardsAllowed.set(ix,cnt);
        if (cnt == 1) {
            int shade = arrStackToChoose.get(ix).getChildren().size;
            arrStackToChoose.get(ix).getChildren().removeIndex(shade - 1);
        }// last child is shade
    }

    private Stack createUnchosenStack() {
        Image image = new Image(empty);
        imgCardWidth = image.getWidth();
        float imgCardHeight = image.getHeight();
        Stack stack = new Stack();
        stack.add(image);
        stack.setWidth(imgCardWidth);
        stack.setHeight(imgCardHeight);
        stack.setScale(scaleCardImage * winsScale);
        stack.setTransform(true);
        return stack;

    }

    private void initChoices() {
        if (isAllies) {
            choices = CardHandler.instance.getCountAllied();
        }else{
            choices = CardHandler.instance.getCountGerman();
        }
        stackChosen = new Stack[choices];
        cardsChosen = new CardsforGame[choices];

        for (int i=0; i<choices; i++){;
            Stack stack = createUnchosenStack();
            stackChosen[i] = stack;
        }
        /**
         *  calculate offsett
         */
        int gaps = choices + 1;
        float leftAfterImages = winWidth - (imgCardWidth*scaleCardImage*choices);
        float xOffsett =leftAfterImages/gaps;
        float xinit = xOffsett;
        float y=10; //
        float cardLength = imgCardWidth*scaleCardImage;

        for (Stack stack:stackChosen){
            window.addActor(stack);
            stack.setPosition(xOffsett,y);
            xOffsett += cardLength+xinit;
        }

    }


    private void centerScreen() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float x = (screenWidth - winWidth) /2;
        float y = (screenHeight - winHeight)/2;
        window.setPosition(x,y);

    }

    public static float getScaleCard() {
        return scaleCardImage;
    }





}
