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
import com.bruinbeargames.ardenne.GameLogic.BlowBridge;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.CardsforGame;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class WinCardChoose implements Observer {
    /**
     * choose a card to play this turn
     */
    Window window;
    Stage stage;
    Label label;
    Table table;
    float topShim = 30f;
    float winsScale = .9f;
    float winWidth = 1000;
    float winHeight = 600;
    private I18NBundle i18NBundle;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    boolean isAllies = false;
    boolean isAI = false;
    ArrayList<CardsforGame> arrCardToChoose = new ArrayList();
    ArrayList<Stack> arrStackToChoose  = new ArrayList();
    ArrayList<Integer> arrCardsAllowed = new ArrayList<>();
    Texture hilite = SplashScreen.instance.cardManager.get("cards/hilite.png", Texture.class);
    Image imgHilite = new Image(hilite);
    float perCard;
    float scaleCardImage = .4f;
    float imgCardHeight =0;
    float imgCardWidth =0;
    Stack stackHilite = null;
    CardsforGame cardChosen = null;

    /**
     *
     * @param isAllies
     * @param arrValidCards for this turn
     */
    public WinCardChoose(boolean isAllies, ArrayList<CardsforGame> arrValidCards){
        Gdx.app.log("WinCardsChoose", "Constructor allied="+isAllies);

        arrCardToChoose.addAll(arrValidCards);
        i18NBundle = GameMenuLoader.instance.localization;
        this.isAllies = isAllies;
        initializeWindow();
        setCloseWindowImage();
        initObjectsandSetWindowSize();

        window.setWidth(winWidth);
        window.setHeight(winHeight);
        window.setModal(false);
        window.setTransform(true);
        //       window.setScale(winsScale);  done in width and height
        centerScreen();
        ardenne.instance.guiStage.addActor(window);
        BottomMenu.instance.setEnablePhaseChange(false);

        if (isAllies){
            TurnCounter.instance.updateText(i18NBundle.format("playamerican"));
       }else{
            TurnCounter.instance.updateText(i18NBundle.format("playgerman"));
        }
        if (EventPopUp.instance.isShowing()){
            EventPopUp.instance.toFront();
        }
 //       EventPopUp.instance.show(i18NBundle.format("cardchoose"));



    }

    private void initializeWindow() {
        /**
         *  initialize window
         */
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("cardplay");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);

    }
    private void setCloseWindowImage() {
        /**
         *  set close for window and logic
         */
        Image image = new Image(close);
        image.setScale(1.5F);

        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeWindowLogic();
                if (isAllies){
                    NextPhase.instance.nextPhase();
  //                  CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
                }else{
                    if (BlowBridge.instance.checkBridgeBlow()) {
                        NextPhase.instance.nextPhase();
                    }
                }

            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("endCardPlay"),
                tooltipStyle);
        image.addListener(hitOK);

    }

    public void closeWindowLogic() {

        if (window != null){window.remove();}
        BottomMenu.instance.setEnablePhaseChange(true);

        return;

    }
    private void initObjectsandSetWindowSize() {
        /**
         *
         */
        scaleCardImage = WinCardsChoice.getScaleCard();
        /**
         * create stacks
         */
        for (CardsforGame card:arrCardToChoose){
            Stack stack = createCardStack(card);
            arrStackToChoose.add(stack);
            setListnersAvailable(stack, card);
        }
        float xOffsett = 10;
        float xShim =10;
        float y = topShim;
        winWidth = (imgCardWidth*scaleCardImage+xOffsett)*arrCardToChoose.size() +xOffsett;
        winHeight = (((imgCardHeight *scaleCardImage + 20)+ 30))  + topShim;
        /**
         * put stacks on window
         */
        for (Stack stack:arrStackToChoose){
            window.addActor(stack);
            stack.setPosition(xOffsett,y);
            xOffsett += imgCardWidth *scaleCardImage+ xShim;
        }

    }

    private Stack createCardStack(CardsforGame card) {
        Image image = new Image(card.getDrawable());
        imgCardWidth = image.getWidth();
        imgCardHeight = image.getHeight();
        Stack stack = new Stack();
        stack.add(image);
        stack.setWidth(imgCardWidth);
        stack.setHeight(imgCardHeight);
        stack.setTransform(true);
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
 //               stack.setScale(1.2f);
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //              Gdx.app.log("Counter", "exit unit="+unit);
  //              stack.setScale(1.0f);
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

    private void cardChosen(CardsforGame cardInput) {
        cardChosen = cardInput;
        if (cardInput.isConfirm()) {
            hilite(cardInput);
            EventConfirm.instance.addObserver(this);
            EventConfirm.instance.show(i18NBundle.format("confirmCard"));
        }else{
            closeWindowLogic();
            if(isAllies){
                CardHandler.instance.setAlliedCardPlayed(cardChosen);
            }else{
                CardHandler.instance.setGermanCardPlayed(cardChosen);
            }
            cardChosen.doEffect();
        }
    }

    /**
     *  filite card chosen
     * @param cardInput
     */
    private void hilite(CardsforGame cardInput) {
        int ix = arrCardToChoose.indexOf(cardInput);
        stackHilite = arrStackToChoose.get(ix);
        stackHilite.addActor(imgHilite);
    }

    private void centerScreen() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float x = (screenWidth - winWidth) /2;
        float y = (screenHeight - winHeight)/2;
        window.setPosition(x,y);

    }


    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes kick off processing for that type
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.ConfirmYes){
            EventConfirm.instance.deleteObserver(this);
            closeWindowLogic();
            if(isAllies){
                CardHandler.instance.setAlliedCardPlayed(cardChosen);
            }else{
                CardHandler.instance.setGermanCardPlayed(cardChosen);
            }
            cardChosen.doEffect();
        }else if (((ObserverPackage) o).type == ObserverPackage.Type.ConfirmNo){
            stackHilite.removeActor(imgHilite);
            EventConfirm.instance.deleteObserver(this);
//            closeWindowLogic();
//            if(isAllies){
//                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
//            }else{
//                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());

//            }

        }


    }
}
