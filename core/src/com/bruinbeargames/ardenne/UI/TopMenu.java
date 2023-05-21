package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;


public class TopMenu {

    private Button reinforcementButton;
    private Button quitbutton;
    private Button soundButton;
    private Button effectsButton;
    private Button manualButton;
    private Button menuButton;
    private Button objectivesButton;
    private Button saveButton;
    private Button keyboardButton;
    private Button crtButton;
    private Button btButton;
    private Button cardButton;
    private Button tecButton;
    private final TextTooltip.TextTooltipStyle tooltipStyle;
    private Group group;
    public static TopMenu instance;
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion bReinforce =  textureAtlas.findRegion("rienforcementbuttonpressed");
    static TextureRegion bReinforceP =  textureAtlas.findRegion("rienforcementbutton");
    static TextureRegion bCRT = textureAtlas.findRegion("crttop");
    static TextureRegion bCRTP = textureAtlas.findRegion("crttoppushed");
    static TextureRegion bBt = textureAtlas.findRegion("bt");
    static TextureRegion bBtP = textureAtlas.findRegion("btpushed");
    static TextureRegion bCard = textureAtlas.findRegion("cardbutton");
    static TextureRegion bCardP = textureAtlas.findRegion("cardbuttonp");
    static TextureRegion bTec = textureAtlas.findRegion("tectop");
    static TextureRegion bTecP = textureAtlas.findRegion("tectoppushed");
    Stage stage;


    public TopMenu(){

        stage = ardenne.instance.guiStage;
        instance = this;

        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"),2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        int x =10;
        initializeMenuButton();
        initializeManualButton();
        x +=manualButton.getWidth()+5;
        initializeSoundButton(x);
        x +=soundButton.getWidth()+5;
        initializeReinForcementButton(x);
        x +=reinforcementButton.getWidth()+5;
        initializeCrtButton(x);
        x +=crtButton.getWidth()+5;
        initializeBtButton(x);
        x +=btButton.getWidth()+5;
 //       initializeBtButton();
 //       x +=btButton.getWidth()+5;


        //       initializeEffectsButton(x);
//        x +=effectsButton.getWidth()+5;
        initializeObjectivesButton(x);
        x +=objectivesButton.getWidth()+5;
        initializeCardButton(x);
        x +=cardButton.getWidth()+5;
        initializeKeyboardButton(x);
        x += keyboardButton.getWidth()+5;
        initializeTECButton(x);
        x += tecButton.getWidth()+5;
        initializeSaveGame(x);
        x +=saveButton.getWidth()+5;
        initializeQuitButton(x);


        group = new Group();

        group.addActor(manualButton);
        group.addActor(soundButton);
        group.addActor(reinforcementButton);
        group.addActor(objectivesButton);
        group.addActor(crtButton);
        group.addActor(btButton);
        group.addActor(cardButton);
 //       group.addActor(effectsButton);
        group.addActor(keyboardButton);
        group.addActor(tecButton);
        group.addActor(quitbutton);
        group.addActor(saveButton);
        group.addAction(Actions.fadeOut(0.01f));
        group.setVisible(false);

        stage.addActor(menuButton);
        stage.addActor(group);
    }


    private void initializeMenuButton() {

        final Button.ButtonStyle menuButtonStyle = new Button.ButtonStyle();

        menuButtonStyle.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("menuoff")));
        menuButtonStyle.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("menuoff")));
        menuButtonStyle.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("menuon")));

        menuButton = new Button(menuButtonStyle);
        menuButton.setHeight(70);
        menuButton.setWidth(70 );
        menuButton.setVisible(true);

        menuButton.setPosition(10 , Gdx.graphics.getHeight() - menuButton.getHeight() - 5);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (!menuButton.isChecked()) {
                        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                    } else {
                        group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
                    }
                    menuButton.setVisible(true);
                }
            }
        });
    }

    private void initializeManualButton() {

        Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("manualoff")));
        style.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("manualoff")));
        style.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("manualon")));

        manualButton = new Button(style);
        manualButton.setHeight(70);
        manualButton.setWidth(70);
        manualButton.setVisible(true);

        manualButton.setPosition(10 , menuButton.getY() - manualButton.getHeight() - 5);

        manualButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {


                // Mac

                Gdx.net.openURI("https://steamcdn-a.akamaihd.net/steam/apps/1294000/manuals/Krim_Manual.pdf?t=1593984784");

                // Windows
                /**
                 try {
                 File file = new File(GamePreferences.getManualFileLocation());
                 Desktop.getDesktop().open(file);
                 } catch (IOException io) {
                 // no application registered for PDFs
                 }*/
            }

        });

        manualButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("manualtooltip"),
                tooltipStyle));
    }

    private void initializeSoundButton(int x) {

        final Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("musicoff")));
        style.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("musicoff")));
        style.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("musicon")));

        soundButton = new Button(style);
        soundButton.setHeight(70  );
        soundButton.setWidth(70  );
        soundButton.setChecked(false);
        soundButton.setVisible(true);

        soundButton.setPosition(10 + x, menuButton.getY() - manualButton.getHeight() - 5);

        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (soundButton.isChecked()) {
                        SoundSlider.instance.show(true);
                    } else {
                        SoundSlider.instance.show(false);
                    }
                }
            }
        });

        soundButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("soundtooltip"),
                tooltipStyle));
    }

    private void initializeReinForcementButton(int x) {

        Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(bReinforceP);
        style.down = new TextureRegionDrawable(bReinforceP);
        style.checked = new TextureRegionDrawable(bReinforce);

        reinforcementButton = new Button(style);
        reinforcementButton.setHeight(70  );
        reinforcementButton.setWidth(70  );
        reinforcementButton.setChecked(false);
        reinforcementButton.setVisible(true);

        reinforcementButton.setPosition(10 + x, menuButton.getY() - soundButton.getHeight() - 5);

        reinforcementButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (Reinforcement.instance.getReinforcementsAvailable().size() > 0) {
                        EventPopUp.instance.show(GameMenuLoader.instance.localization.get("help4"));
                        CenterScreen.instance.start(Hex.hexTable[9][24]);
                        Reinforcement.instance.showDisplay();
                    }else{
                        EventPopUp.instance.show(GameMenuLoader.instance.localization.get("help5"));
                    }
                    reinforcementButton.setChecked(false);
                    group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                    menuButton.setChecked(false);
                }
            }
        });

        reinforcementButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("reinforcetooltip"),
                tooltipStyle));
    }


    private void initializeCrtButton(int x) {

        Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(bCRT);
        style.down = new TextureRegionDrawable(bCRTP);
        style.checked = new TextureRegionDrawable(bCRT);

        crtButton = new Button(style);
        crtButton.setHeight(70  );
        crtButton.setWidth(70  );
        crtButton.setChecked(false);
        crtButton.setVisible(true);

        crtButton.setPosition(10 + x, menuButton.getY() - soundButton.getHeight() - 5);

        crtButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    WinDisplayCRT winDisplayCRT = new WinDisplayCRT();
                    crtButton.setChecked(false);
                    group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                    menuButton.setChecked(false);
                }
            }
        });

        crtButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("windisplaycrt"),
                tooltipStyle));
    }

    private void initializeBtButton(int x) {

        Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(bBt);
        style.down = new TextureRegionDrawable(bBtP);
        style.checked = new TextureRegionDrawable(bBtP);

        btButton = new Button(style);
        btButton.setHeight(70  );
        btButton.setWidth(70  );
        btButton.setChecked(false);
        btButton.setVisible(true);

        btButton.setPosition(10 + x, menuButton.getY() - soundButton.getHeight() - 5);

        btButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    WinDisplayBombard winDisplayBombard = new WinDisplayBombard();
                    btButton.setChecked(false);
                    group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                    menuButton.setChecked(false);
                }
            }
        });

        btButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("windisplaybt"),
                tooltipStyle));
    }

    private void initializeObjectivesButton(int x) {

        Button.ButtonStyle mapButtonStyle = new Button.ButtonStyle();

        mapButtonStyle.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonobjectiveoff")));
        mapButtonStyle.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonobjectiveoff")));
        mapButtonStyle.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonobjectiveon")));

        objectivesButton = new Button(mapButtonStyle);
        objectivesButton.setHeight(70);
        objectivesButton.setWidth(70);
        objectivesButton.setVisible(true);

        objectivesButton.setPosition(10  + x, menuButton.getY() - reinforcementButton.getHeight() - 5);

        objectivesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (!objectivesButton.isChecked()) {
                        //stateEngine.setShowOBjectives(false);
                    } else {
                        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
                            EventPopUp.instance.show(GameMenuLoader.instance.localization.get("scenario1objectives"));
                        }else{
                            EventPopUp.instance.show(GameMenuLoader.instance.localization.get("scenario1objectives"));
                        }
                        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                        menuButton.setChecked(false);
                        objectivesButton.setChecked(false);
                    }
                }
            }
        });

        objectivesButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("objectivestooltip"),
                tooltipStyle));
    }

    private void initializeCardButton(int x) {

        Button.ButtonStyle mapButtonStyle = new Button.ButtonStyle();
        mapButtonStyle.up = new TextureRegionDrawable(bCard);
        mapButtonStyle.down = new TextureRegionDrawable(bCardP);
        mapButtonStyle.checked = new TextureRegionDrawable(bCardP);


        cardButton = new Button(mapButtonStyle);
        cardButton.setHeight(70);
        cardButton.setWidth(70);
        cardButton.setVisible(true);

        cardButton.setPosition(10  + x, menuButton.getY() - reinforcementButton.getHeight() - 5);

        cardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (!cardButton.isChecked()) {
                        //stateEngine.setShowOBjectives(false);
                    } else {
                        WinCardInHandDisplay winCardInHandDisplay = new WinCardInHandDisplay();
                        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                        menuButton.setChecked(false);
                        cardButton.setChecked(false);
                    }
                }
            }
        });

        cardButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("objectivestooltip"),
                tooltipStyle));
    }
    private void initializeTECButton(int x) {

        Button.ButtonStyle mapButtonStyle = new Button.ButtonStyle();
        mapButtonStyle.up = new TextureRegionDrawable(bTec);
        mapButtonStyle.down = new TextureRegionDrawable(bTecP);
        mapButtonStyle.checked = new TextureRegionDrawable(bTecP);


        tecButton = new Button(mapButtonStyle);
        tecButton.setHeight(70);
        tecButton.setWidth(70);
        tecButton.setVisible(true);

        tecButton.setPosition(10  + x, menuButton.getY() - reinforcementButton.getHeight() - 5);

        tecButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (!tecButton.isChecked()) {
                        //stateEngine.setShowOBjectives(false);
                    } else {
                        WinTEC winTEC = new WinTEC();
                        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                    }
                }
            }
        });

        tecButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("objectivestooltip"),
                tooltipStyle));
    }

    private void initializeEffectsButton(int x) {

        Button.ButtonStyle style = new Button.ButtonStyle();

        style.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("effectsoff")));
        style.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("effectsoff")));
        style.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("effectson")));

        effectsButton = new Button(style);
        effectsButton.setHeight(70);
        effectsButton.setWidth(70);
        effectsButton.setChecked(false);
        effectsButton.setVisible(true);

        effectsButton.setPosition(10  + x, menuButton.getY() - manualButton.getHeight() - 5);

        effectsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (effectsButton.isChecked()) {
  //                      stateEngine.getHudUI().getIngameEffectsPopup().show(false);
                    } else {
  //                      stateEngine.getHudUI().getIngameEffectsPopup().show(true);
                    }
                }
            }
        });

        effectsButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("effectstooltip"),
                tooltipStyle));
    }


    private void initializeQuitButton(int x) {
        TextButton.TextButtonStyle quitstyle = new TextButton.TextButtonStyle();

        quitstyle.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonexitoff")));
        quitstyle.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonexiton")));
        quitstyle.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonexiton")));
        quitstyle.font = Fonts.getFont24();

        quitbutton = new TextButton("", quitstyle);
        quitbutton.setHeight(70);
        quitbutton.setWidth(70);
        quitbutton.setVisible(true);
        quitbutton.setPosition(10  +  x,
                menuButton.getY() - quitbutton.getHeight() - 5);

        quitbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    ExitGameConfirmationPopUp exitGameConfirmationPopUp = new ExitGameConfirmationPopUp();
                    exitGameConfirmationPopUp.show(GameMenuLoader.instance.localization.get("exitgame"));
                }
            }
        });

        quitbutton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("quittooltip"),
                tooltipStyle));
    }

    /**
     * removed
     * @param x
     */
    private void initializeSaveGame(int x) {

        TextButton.TextButtonStyle savestyle = new TextButton.TextButtonStyle();

        savestyle.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonsaveoff")));
        savestyle.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonsaveon")));
        savestyle.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("buttonsaveon")));
        savestyle.font = Fonts.getFont24();

        saveButton = new Button(savestyle);
        saveButton.setHeight(70);
        saveButton.setWidth(70);
        saveButton.setVisible(true);
        saveButton.setPosition(10 + x,
                menuButton.getY() - saveButton.getHeight() - 5);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                WinSaveGame winSaveGame = new WinSaveGame();
    //            SaveGame saveGame = new SaveGame("XX");
    //            saveButton.setChecked(false);
                group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                menuButton.setChecked(false);
            }
        });

        saveButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("savetooltip"),
                tooltipStyle));
   }
   public void hideSave(){

        saveButton.setVisible(false);
   }
   public void showSave(){
        saveButton.setVisible(true);
    }


    private void initializeKeyboardButton(int x) {

        TextButton.TextButtonStyle savestyle = new TextButton.TextButtonStyle();

        savestyle.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("keyboardoff")));
        savestyle.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("keyboardon")));
        savestyle.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.buttons.button.get("keyboardon")));
        savestyle.font = Fonts.getFont24();

        keyboardButton = new Button(savestyle);
        keyboardButton.setHeight(70);
        keyboardButton.setWidth(70);
        keyboardButton.setVisible(true);
        keyboardButton.setPosition(10 + x,
                menuButton.getY() - keyboardButton.getHeight() - 5);

        keyboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EventPopUp.instance.show(GameMenuLoader.instance.localization.get("help3"));
                keyboardButton.setChecked(false);
                group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
                menuButton.setChecked(false);
            }
        });

        keyboardButton.addListener(new TextTooltip(
                GameMenuLoader.instance.localization.get("keyboardtooltip"),
                tooltipStyle));
    }

}

