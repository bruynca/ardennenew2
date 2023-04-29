package com.bruinbeargames.ardenne;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.UI.EventConfirm;
import com.bruinbeargames.ardenne.UI.EventOK;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.MouseImage;

import java.io.File;

public class GameSelection {


    private Label game1Label;
    private Label game2Label;
    private Label scenariosLabel;
    private Label scenarioDetailsLabel;
    private CheckBox humanVAmericanAICheckBox;
    private CheckBox humanVGermanAICheckBox;
    private CheckBox emailGermanCheckBox;
    private CheckBox emailAmericanCheckBox;
    private CheckBox hotseatCheckBox;
    private CheckBox easyGerman;
    private CheckBox balanced;
    private CheckBox easyAmerican;
    private CheckBox sovietMandatoryAttacks;
    private Image backgroundImage;
    private Group group;
    private I18NBundle i18NBundle;
    private TextButton mainMenuButton;
    private TextButton playButton;
    public TextButton.TextButtonStyle textButtonStyle;
    private CheckBox.CheckBoxStyle checkBoxStyle;
    private ButtonGroup buttonGroupAI;
    private ButtonGroup buttonGroupSetUp;
//    private Setup setup;
    private ScrollPane scenariosScrollPane;
    private final TextTooltip.TextTooltipStyle tooltipStyle;
    static public GameSelection instance;


    public GameSelection(Stage stage, final Sound sound) {
        instance = this;
        EventOK eventOK = new EventOK();
        EventPopUp eventPopUp = new EventPopUp(ardenne.instance.guiStage);
        EventConfirm eventConfirm = new EventConfirm();
        group = new Group();
        group.setVisible(false);
        i18NBundle = GameMenuLoader.instance.localization;

        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);

        MouseImage mouseImage = new MouseImage();
        CardHandler cardHandler = new CardHandler();
        GameSetup gameSetup = new GameSetup();


        initializeStyles();
        initializeImageBackGround();
        initializeScenariosLabel();
        initializeScenariosScrollPane();
        initializeScenarioDetailsLabel();
        initializegame1Label();
        //initializehumanVAmericanAICheckBox();
        initializeMainMenuButton();
        initializePlayButton(sound);
        initializeHotseatCheckBox();
        initializeHumanVGermanAICheckBox();
        initializeEasyGermanCheckBox();
        initializeBalancedCheckBox();
        initializeEasyAmericanCheckBox();
        initializeButtonGroups();

        stage.addActor(group);
    }

    public void show(boolean display) {
        group.setVisible(display);
    }

    public boolean isVisible() {
        return group.isVisible();
    }

    private void initializeStyles() {

        textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("unselectedbutton"))),
                new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton")));

        checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkboxselected")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkbox")));
        checkBoxStyle.font = Fonts.getFont24();
    }

    private void initializeImageBackGround() {

        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 5, 5, 5, 5);
        backgroundImage = new Image();
        backgroundImage.setDrawable(new NinePatchDrawable(np));
        backgroundImage.setHeight(GameMenu.instance.getBackGroundHeight() / (1));
        backgroundImage.setWidth(800 / (1));
        backgroundImage.setVisible(false);
        backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (400 / 1),
                (Gdx.graphics.getHeight() / 2) - (250 / 1));


        group.addActor(backgroundImage);
    }

    private void initializeScenariosLabel() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        scenariosLabel = new Label(i18NBundle.get("scenarios"), style);
        scenariosLabel.setSize(281 / 1, 44 / 1);
        scenariosLabel.setPosition(backgroundImage.getX()  + 20, backgroundImage.getY() + backgroundImage.getHeight() - (50 / 1));
        scenariosLabel.setVisible(true);

        group.addActor(scenariosLabel);
    }

    private void initializeScenarioDetailsLabel() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        scenarioDetailsLabel = new Label(i18NBundle.get("scenariodetail"), style);
        scenarioDetailsLabel.setSize(281 / 1, 44 / 1);
        scenarioDetailsLabel.setPosition(scenariosScrollPane.getX() + scenariosScrollPane.getWidth() +10, backgroundImage.getY() + backgroundImage.getHeight() - (40 / 1));
        scenarioDetailsLabel.setVisible(true);

        //group.addActor(scenarioDetailsLabel);
    }

    private void initializegame1Label() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        game1Label = new Label(i18NBundle.get("scenario1"), style);
        game1Label.setSize(281 / 1, 44 / 1);
        game1Label.setPosition(scenariosScrollPane.getX() + scenariosScrollPane.getWidth() +10, backgroundImage.getY() + backgroundImage.getHeight() - (150 / 1));
        game1Label.setVisible(true);

        group.addActor(game1Label);
    }

    private void initializeMainMenuButton() {

        mainMenuButton = new TextButton(i18NBundle.get("mainmenu"), textButtonStyle);
        mainMenuButton.setSize(281 / 1, 44 / 1);
        mainMenuButton.setPosition(backgroundImage.getX() + ((backgroundImage.getWidth() / 2)), backgroundImage.getY() + (10 / 1));
        mainMenuButton.setVisible(true);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    backgroundImage.setHeight(426 / (1));
                    backgroundImage.setWidth(474 / (1));
                    backgroundImage.setVisible(true);
                    backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (239 / 1),
                            (Gdx.graphics.getHeight() / 2) - (208 / 1));
                    mainMenuButton.setPosition(backgroundImage.getX() + ((backgroundImage.getWidth() / 2)), backgroundImage.getY() + backgroundImage.getHeight() - ((410) / 1));

                }
            }
        });

        //group.addActor(mainMenuButton);
    }

    private void initializePlayButton(final Sound sound) {

        playButton = new TextButton(i18NBundle.get("play"), textButtonStyle);
        playButton.setSize(281 / 1, 44 / 1);
        playButton.setVisible(true);
        playButton.setPosition(backgroundImage.getX() + backgroundImage.getWidth() - (playButton.getWidth() + 20), backgroundImage.getY() + (10 / 1));
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    SplashScreen.instance.end();
                    Game game = new Game("");
//                    sound.stop();
//                    game.showGameScreen(false);
                }
            }
        });


        group.addActor(playButton);
    }

    private void initializeEasyGermanCheckBox() {

        easyGerman = new CheckBox("  " + i18NBundle.get("easygerman"), checkBoxStyle);
        easyGerman.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 + (20 / 1), scenariosScrollPane.getY() - 80);
        easyGerman.setVisible(true);
        easyGerman.setChecked(true);

        easyGerman.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (easyGerman.isChecked()) {
                        GameSetup.instance.setEasyGerman(true);
                    } else {
                        GameSetup.instance.setEasyGerman(false);
                    }
                }
            }
        });

        group.addActor(easyGerman);

    }


    private void initializeBalancedCheckBox() {

        balanced = new CheckBox("  " + i18NBundle.get("balanced"), checkBoxStyle);
        balanced.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 + (20 / 1), scenariosScrollPane.getY() - 130);
        balanced.setVisible(true);
        balanced.setChecked(false);
        GameSetup.instance.setEasyGerman(false);

        balanced.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (balanced.isChecked()) {
                        GameSetup.instance.setBalanced(true);
                    } else {
                        GameSetup.instance.setBalanced(false);
                    }
                }
            }
        });

        group.addActor(balanced);

    }

    private void initializeHotseatCheckBox() {

        hotseatCheckBox = new CheckBox("  " + i18NBundle.get("twoplayer"), checkBoxStyle);
        hotseatCheckBox.setPosition(backgroundImage.getX() + (20 / 1), scenariosScrollPane.getY() - 130);

        hotseatCheckBox.setVisible(true);
        hotseatCheckBox.setChecked(false);

        hotseatCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (hotseatCheckBox.isChecked()) {
                        GameSetup.instance.setHotSeatGame(true);
                        GameSetup.instance.setGermanVersusAI(false);
                        GameSetup.instance.setAlliedVersusAI(false);
                        MouseImage.instance.setMouseHotseat();

                    } else {
                        MouseImage.instance.mouseImageReset();
                        GameSetup.instance.setHotSeatGame(false);
                    }
                }
            }
        });


        group.addActor(hotseatCheckBox);

    }

    private void initializeHumanVGermanAICheckBox() {

 
        humanVGermanAICheckBox = new CheckBox("  " + i18NBundle.format("playas", i18NBundle.get("german")), checkBoxStyle);
        humanVGermanAICheckBox.setPosition(backgroundImage.getX() + (20 / 1), scenariosScrollPane.getY() - 80);

        humanVGermanAICheckBox.setVisible(true);
        humanVGermanAICheckBox.setChecked(false);

        humanVGermanAICheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (humanVGermanAICheckBox.isChecked()) {
                        GameSetup.instance.setGermanVersusAI(true);
                        GameSetup.instance.setHotSeatGame(false);
                        GameSetup.instance.setAlliedVersusAI(false);
                        MouseImage.instance.setMouseGerman();

                    } else {
                        MouseImage.instance.mouseImageReset();
                        GameSetup.instance.setGermanVersusAI(false);

                    }
                }
            }
        });


        group.addActor(humanVGermanAICheckBox);

    }

     private void initializehumanVAmericanAICheckBox() {

        humanVAmericanAICheckBox = new CheckBox("  " + i18NBundle.format("playas", i18NBundle.get("american")), checkBoxStyle);
        humanVAmericanAICheckBox.setPosition(backgroundImage.getX() + (20 / 1), scenariosScrollPane.getY() - 180);

        humanVAmericanAICheckBox.setVisible(false);
        humanVAmericanAICheckBox.setChecked(false);

        humanVAmericanAICheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (humanVAmericanAICheckBox.isChecked()) {
                        GameSetup.instance.setAlliedVersusAI(true);
                        GameSetup.instance.setGermanVersusAI(false);
                        GameSetup.instance.setHotSeatGame(false);
                        MouseImage.instance.setMouseAmerican();


                    } else {
                        MouseImage.instance.mouseImageReset();
                        GameSetup.instance.setAlliedVersusAI(false);
                    }
                }
            }
        });


        group.addActor(humanVAmericanAICheckBox);

    }

    private void initializeEmailGermanCheckBox() {

        humanVAmericanAICheckBox = new CheckBox("  " + i18NBundle.format("playas", i18NBundle.get("american")), checkBoxStyle);
        humanVAmericanAICheckBox.setPosition(backgroundImage.getX() + (20 / 1), scenariosScrollPane.getY() - 180);

        humanVAmericanAICheckBox.setVisible(true);
        humanVAmericanAICheckBox.setChecked(false);

        humanVAmericanAICheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (humanVAmericanAICheckBox.isChecked()) {
                        GameSetup.instance.setAlliedVersusAI(true);
                        GameSetup.instance.setGermanVersusAI(false);
                        GameSetup.instance.setHotSeatGame(false);
                        MouseImage.instance.setMouseAmerican();


                    } else {
                        MouseImage.instance.mouseImageReset();
                        GameSetup.instance.setAlliedVersusAI(false);
                    }
                }
            }
        });


        group.addActor(humanVAmericanAICheckBox);

    }
    private void initializeEasyAmericanCheckBox() {


        easyAmerican = new CheckBox("  " + i18NBundle.get("easyamerican"), checkBoxStyle);
        easyAmerican.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 + (20 / 1), scenariosScrollPane.getY() - 180);

        easyAmerican.setVisible(true);
        easyAmerican.setChecked(false);


        easyAmerican.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (easyAmerican.isChecked()) {
                        GameSetup.instance.setEasyAmerican(true);
                    } else {
                        GameSetup.instance.setEasyAmerican(false);
                    }
                }
            }
        });

  //      easyAmerican.addListener(new TextTooltip(
  //              i18NBundle.get("easyamericantooltip"),
  ///              tooltipStyle));

       group.addActor(easyAmerican);

    }



    private void initializeScenariosScrollPane() {

//        stateEngine.setScenarioName("Full Campaign ");

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Fonts.getFont24();
        listStyle.fontColorSelected.set(Color.WHITE);
        listStyle.fontColorUnselected.set(Color.GRAY);
        listStyle.selection = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("unselectedbutton")));
        listStyle.down = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton")));
        listStyle.selection.setTopHeight(5);
        listStyle.selection.setBottomHeight(5);
        listStyle.selection.setLeftWidth(5);
        listStyle.selection.setRightWidth(5);
        listStyle.selection.setMinWidth(400);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 5, 5, 5, 5);
        listStyle.background = new NinePatchDrawable(np);
        List<String> list = new List<>(listStyle);
        File folder = GamePreferences.getSaveGamesLocation().file();
        int filecount = 0;
        String[] strings = new String[4];
        strings[0] = "Introduction";
        strings[1] = "2nd Panzer Goes West";
        strings[2] = "Bastogne Breakout";
        strings[3] = "CounterAttack";

        list.setItems(strings);

        scenariosScrollPane = new ScrollPane(list);
        scenariosScrollPane.setForceScroll(true, false);
        scenariosScrollPane.setWidth(300);
        scenariosScrollPane.setHeight(150);
        //scrollPane.setBounds(0, 0, 281/1, backGroundImage.getHeight() - 20);
        scenariosScrollPane.setSmoothScrolling(false);
        scenariosScrollPane.setPosition(backgroundImage.getX() + 20 , backgroundImage.getY() + backgroundImage.getHeight() - ((200) / 1));
        scenariosScrollPane.setVisible(true);

        scenariosScrollPane.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    List<String> list = (List<String>) scenariosScrollPane.getActor();
                    String choice = list.getSelected();
                    if (choice.equals("Introduction")){
                        game1Label.setText(i18NBundle.get("scenario1"));
                        GameSetup.instance.setScenario(GameSetup.Scenario.Intro);
                    }else if (choice.equals("2nd Panzer Goes West")){
                        EventOK.instance.show(i18NBundle.get("notavailable"));
                        list.setSelected("Introduction");
                        return;
                       // game1Label.setText(i18NBundle.get("scenario2"));
                       // GameSetup.instance.setScenario(GameSetup.Scenario.SecondPanzer);
                    }else if (choice.equals("Bastogne Breakout")){
                        EventOK.instance.show(i18NBundle.get("notavailable"));
                        list.setSelected("Introduction");
                        return;
//                        game1Label.setText(i18NBundle.get("scenario3"));
//                        GameSetup.instance.setScenario(GameSetup.Scenario.Lehr);
                    }else{
                        EventOK.instance.show(i18NBundle.get("notavailable"));
                        list.setSelected("Introduction");
                        return;

//                        game1Label.setText(i18NBundle.get("scenario4"));
//                        GameSetup.instance.setScenario(GameSetup.Scenario.CounterAttack);
                    }
                }
            }
        });

        group.addActor(scenariosScrollPane);
    }

    private void initializeButtonGroups() {
        buttonGroupAI = new ButtonGroup();
        buttonGroupAI.add(hotseatCheckBox);
        buttonGroupAI.add(humanVGermanAICheckBox);
 //       buttonGroupAI.add(humanVAmericanAICheckBox);
        buttonGroupAI.setMaxCheckCount(1);
        humanVGermanAICheckBox.setChecked(true);
        GameSetup.instance.setGermanVersusAI(true);

        buttonGroupSetUp = new ButtonGroup();
        buttonGroupSetUp.add(easyGerman);
        buttonGroupSetUp.add(balanced);
        buttonGroupSetUp.add(easyAmerican);
        buttonGroupSetUp.setMaxCheckCount(1);
        easyGerman.setChecked(true);
        GameSetup.instance.setEasyGerman(true);

    }
}
