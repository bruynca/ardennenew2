package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;

import java.io.File;
import java.util.Observable;

public class GameMenu extends Observable {

    private Image backGroundImage;
    private TextButton newGameButton;
    private TextButton loadGameButton;
    private TextButton loadSavedGameButton;
    private TextButton instructionsButton;
    private TextButton exitButton;
    private TextButton creditsButton;
    private TextButton mainMenuButton;
    private TextButton optionsButton;
    //private Slider soundSlider;
    private TextButton gameOptionsButton;
    private TextButton backButton;
    private ScrollPane scrollPane;
    private Label creditsText;
    private CheckBox fullScreen;
    private CheckBox windowed;

    private CheckBox resetHelp;
    private SelectBox windowsSizeSelectBox;
    private Label windowTextLabel;
    private ButtonGroup buttonGroup;
    private boolean exiting;
    private Stage stage;
    private float screenWidth;
    private float screenHeight;
    private LoadGamePopUp loadGamePopUp;
    private I18NBundle i18NBundle;
    private TextButton.TextButtonStyle textButtonStyle;
    private CheckBox.CheckBoxStyle checkBoxStyle;
    private GameSelection gameSelection;
    private final int backGroundHeight = 500;
    public static GameMenu instance;

    public GameMenu(Stage stage , Sound sound, TextureAtlas atlas, float screenHeight, float screenWidth){
        this.stage = stage;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        instance = this;

        i18NBundle= GameMenuLoader.instance.localization;
        Fonts.loadFont();
        loadGamePopUp = new LoadGamePopUp(stage);

        initializeStyles(atlas);
        initializeImageBackGround();
        initializeExitButton();
        initializeInstructionsButton();
        initializeNewGameButton(screenHeight,  sound );
        initializeLoadSavedGameButton();
        initializeLoadGameButton(sound);
        initializeScrollPane(atlas);
        initializeCreditsButton();
        initializeMainMenuButton();
        initializeBackButton();
        initializeOptionsButton();
        initializeGameOptionsButton();
        initializeCreditsTextLabel();
        initializeWindowTextLabel();
        initializeFullScreenCheckBox();
        initializeResetHelpCheckBox();
        initializeWindowedCheckBox();
        // TODO: Needs more work
        //initializeWindowsSizeSelectBox();
        initializeButtonGroup();

    }

    public void updateText(){
        newGameButton.getLabel().setText(i18NBundle.get("newgame"));
        instructionsButton.getLabel().setText(i18NBundle.get("gamemanual"));
        exitButton.getLabel().setText(i18NBundle.get("exittodesktop"));
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            //fullScreen.remove();
            //initializeFullScreenCheckBox();
        }

    }

    private void showCredits() {
        newGameButton.setVisible(false);
        creditsButton.setVisible(false);
        exitButton.setVisible(false);
        instructionsButton.setVisible(false);
        mainMenuButton.setVisible(true);
        creditsText.setVisible(true);
        optionsButton.setVisible(false);
        backButton.setVisible(false);
        gameOptionsButton.setVisible(false);
        loadSavedGameButton.setVisible(false);
        loadGameButton.setVisible(false);

    }

    private void showLoadGame(){
        newGameButton.setVisible(false);
        creditsButton.setVisible(false);
        exitButton.setVisible(false);
        instructionsButton.setVisible(false);
        mainMenuButton.setVisible(true);
        creditsText.setVisible(false);
        optionsButton.setVisible(false);
        backButton.setVisible(false);
        gameOptionsButton.setVisible(false);
        loadSavedGameButton.setVisible(false);
        loadGameButton.setVisible(true);
    }

    private void showOptions(){
        resetHelp.setVisible(true);
        windowed.setVisible(true);
        fullScreen.setVisible(true);
        windowTextLabel.setVisible(true);
        //windowsSizeSelectBox.setVisible(true);
        optionsButton.setVisible(false);
        gameOptionsButton.setVisible(false);
        newGameButton.setVisible(false);
        creditsButton.setVisible(false);
        exitButton.setVisible(false);
        instructionsButton.setVisible(false);
        mainMenuButton.setVisible(true);
        backButton.setVisible(false);
        loadSavedGameButton.setVisible(false);
        loadGameButton.setVisible(false);
    }

    private void showMainMenu(){
        newGameButton.setVisible(true);
        creditsButton.setVisible(true);
        exitButton.setVisible(true);
        instructionsButton.setVisible(true);
        loadSavedGameButton.setVisible(true);
        loadGameButton.setVisible(false);
        mainMenuButton.setVisible(false);
        creditsText.setVisible(false);
        optionsButton.setVisible(false);
        gameOptionsButton.setVisible(true);
        windowTextLabel.setVisible(false);
        resetHelp.setVisible(false);
        windowed.setVisible(false);
        fullScreen.setVisible(false);
        backButton.setVisible(false);
        scrollPane.setVisible(false);

        setChanged();
        notifyObservers(EventMessages.SHOW_LANGUAGE_OPTIONS);
    }

    public void hide(){
        newGameButton.setVisible(false);
        creditsButton.setVisible(false);
        exitButton.setVisible(false);
        instructionsButton.setVisible(false);
        mainMenuButton.setVisible(true);
        creditsText.setVisible(false);
        optionsButton.setVisible(false);
        windowTextLabel.setVisible(false);
        windowed.setVisible(false);
        resetHelp.setVisible(false);
        fullScreen.setVisible(false);
        backButton.setVisible(false);
        gameOptionsButton.setVisible(false);
        loadSavedGameButton.setVisible(false);
        loadGameButton.setVisible(false);
        setChanged();
        notifyObservers(EventMessages.HIDE_LANGUAGE_OPTIONS);
    }

    private void initializeStyles(TextureAtlas atlas){
/*        Skin skin =  VisUI.getSkin();
        VisUI.dispose(true);
        BitmapFont font = Fonts.getFont24();
        skin.add("chines24",Fonts.getFont24(),BitmapFont.class);
        VisUI.load(skin); */
//        Dialogs.showOKDialog(ardenne.instance.guiStage,"Hello","Hello");


        textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(atlas.findRegion("unselectedbutton"))),
                new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton")));
        if(!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            textButtonStyle.font.getData().scale(1);
        }

        checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkboxselected")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkbox")));
        checkBoxStyle.font = Fonts.getFont24();


    }

    private void initializeImageBackGround() {

        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"),5, 5, 5, 5);
        backGroundImage = new Image();
        backGroundImage.setDrawable(new NinePatchDrawable(np));
        backGroundImage.setHeight(486 / (1));
        backGroundImage.setWidth(474 / (1));
        backGroundImage.setVisible(true);
        backGroundImage.setPosition((screenWidth / 2) - (239 / 1),
                (screenHeight / 2) - (243 / 1));


        stage.addActor(backGroundImage);
    }

    private void initializeNewGameButton( float screenHeight,  final Sound sound){

        gameSelection = new GameSelection(stage,  sound);

        newGameButton = new TextButton(i18NBundle.get("newgame"),textButtonStyle);
        newGameButton.setSize(281/1, 44/1);
        newGameButton.setPosition(0,screenHeight);
        newGameButton.setPosition(backGroundImage.getX() + (((backGroundImage.getWidth()/2) - (newGameButton.getWidth()/2)) ), backGroundImage.getY() + backGroundImage.getHeight() - ((100) / 1));

        newGameButton.setVisible(true);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    backGroundImage.setHeight(backGroundHeight / (1));
                    backGroundImage.setWidth(800 / (1));
                    backGroundImage.setVisible(true);
                    backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (400 / 1),
                            (Gdx.graphics.getHeight() / 2) - (250 / 1));
                    hide();
                    mainMenuButton.setPosition(backGroundImage.getX() + 20, backGroundImage.getY() +  (10 / 1));

                    gameSelection.show(true);
                }
            }
        });

        stage.addActor(newGameButton);

    }

    private void initializeLoadSavedGameButton(){

        loadSavedGameButton = new TextButton(i18NBundle.get("loadsavedgame"),textButtonStyle);
        loadSavedGameButton.setSize(281/1, 44/1);
        loadSavedGameButton.setPosition(0,screenHeight);
        loadSavedGameButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (loadSavedGameButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((160) / 1));

        loadSavedGameButton.setVisible(true);

        loadSavedGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
                    mainMenuButton.setVisible(false);
                    scrollPane.setVisible(true);
                    stage.setScrollFocus(scrollPane);
                    stage.setKeyboardFocus(scrollPane);
                    showLoadGame();
                }
            }
        });

        stage.addActor(loadSavedGameButton);

    }

    private void initializeLoadGameButton( final Sound sound){

        loadGameButton = new TextButton(i18NBundle.get("loadgame"),textButtonStyle);
        loadGameButton.setSize(281/1, 44/1);
        loadGameButton.setPosition(0,screenHeight);
        loadGameButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (exitButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((460) / 1)); // 360 prev
        loadGameButton.setVisible(false);

        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (strGame.length() > 0) {
                        SplashScreen.instance.end();
                        Game game = new Game(strGame);
                    }
                }
            }
        });

        stage.addActor(loadGameButton);

    }
    static String strGame;
    private void initializeScrollPane(TextureAtlas atlas) {
        strGame ="";
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Fonts.getFont24();
        listStyle.fontColorSelected.set(Color.WHITE);
        listStyle.fontColorUnselected.set(Color.GRAY);
        listStyle.selection = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("unselectedbutton")));
        listStyle.down = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton")));
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
        String[] strings;
        if (folder.listFiles() != null) {
            for (File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {

                } else {
                    filecount++;

                }
            }
            strings = new String[filecount];
            int i = 0;
            for (File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {

                } else {
                    strings[i++] = fileEntry.getName().substring(0,fileEntry.getName().length()-4);

                }
            }
        }else{
            strings = new String[filecount];
        }


        list.setItems(strings);
        if (strings.length > 0) {
            list.setSelectedIndex(0);
//            stateEngine.setSavedGame("../savedgames/" + list.getSelected()+".xml");
            loadSavedGameButton.setDisabled(false);
        }else{
            loadSavedGameButton.setDisabled(true);
        }
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        //scrollPaneStyle.hScroll = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton")));
        //scrollPaneStyle.hScrollKnob = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton")));
        //scrollPaneStyle.vScroll = new TextureRegionDrawable(new TextureRegion(atlas.findRegion("selectedbutton")));
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("line"), 2, 2, 2, 2);
        scrollPaneStyle.vScrollKnob = new NinePatchDrawable(np);
        scrollPane = new ScrollPane(list, scrollPaneStyle);
        scrollPane.setScrollPercentY(100);
        scrollPane.setWidth(400);
        scrollPane.setHeight(backGroundImage.getHeight() - 150);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(backGroundImage.getX() + (((backGroundImage.getWidth() / 2) - (scrollPane.getWidth() / 2))),
                backGroundImage.getY() + 125);
        scrollPane.setVisible(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    List<String> list = (List<String>) scrollPane.getActor();
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    strGame =GamePreferences.getSaveGamesLocation() + "/" + list.getSelected() + ".xml";

                }
            }
        });
        /**
         *  added from WinterThunder  dont know whci one forces scroll bars to show up
         */
        scrollPane.setSmoothScrolling(true);
        scrollPane.setTransform(true);
        scrollPane.setForceScroll(false, true);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        if (list.getItems().size > 0) {
            String firstGame = list.getItems().get(0);
            if (firstGame != null) {
                strGame = GamePreferences.getSaveGamesLocation() + "/" + firstGame + ".xml";
            }
        }


        stage.addActor(scrollPane);
    }

    private void initializeInstructionsButton(){

        instructionsButton = new TextButton(i18NBundle.get("gamemanual"),textButtonStyle);
        instructionsButton.setSize(281, 44);
        instructionsButton.setPosition(backGroundImage.getX() + (((backGroundImage.getWidth()/2) - (instructionsButton.getWidth()/2))), backGroundImage.getY() + backGroundImage.getHeight() - ((340) ));
        instructionsButton.setVisible(true);

        instructionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                   String url="https://bruinbeargames.com/ardenne/pdf/bastognebreakoutmanual.pdf";
                   Gdx.net.openURI(url);
                }
            }
        });

        stage.addActor(instructionsButton);
    }

    private void initializeCreditsButton(){

        creditsButton = new TextButton(i18NBundle.get("credits"),textButtonStyle);
        creditsButton.setSize(281/1, 44/1);
        creditsButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (creditsButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((280) / 1));
        creditsButton.setVisible(true);

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    showCredits();
                }
            }
        });

        stage.addActor(creditsButton);
    }

    private void initializeOptionsButton(){

        optionsButton = new TextButton(i18NBundle.get("options"),textButtonStyle);
        optionsButton.setSize(281/1, 44/1);
        optionsButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (optionsButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((220) / 1));
        optionsButton.setVisible(false);

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    //gameStartChoicesPopUp.show(true);
                }
            }
        });

        stage.addActor(optionsButton);
    }

    private void initializeGameOptionsButton(){

        gameOptionsButton = new TextButton(i18NBundle.get("gameoptions"),textButtonStyle);
        gameOptionsButton.setSize(281/1, 44/1);
        gameOptionsButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (gameOptionsButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((220) / 1));
        gameOptionsButton.setVisible(true);

        gameOptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    showOptions();
                }
            }
        });

        stage.addActor(gameOptionsButton);
    }

    private void initializeExitButton(){

        exitButton = new TextButton(i18NBundle.get("exittodesktop"),textButtonStyle);
        exitButton.setSize(281/1, 44/1);
        exitButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (exitButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((400) / 1));
        exitButton.setVisible(true);

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    exiting = true;
                    Gdx.app.exit();
                }
            }
        });


        stage.addActor(exitButton);
    }

    private void initializeMainMenuButton(){

        mainMenuButton = new TextButton(i18NBundle.get("mainmenu"),textButtonStyle);
        mainMenuButton.setSize(281/1, 44/1);
        mainMenuButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (exitButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((410) / 1));
        mainMenuButton.setVisible(false);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                    backGroundImage.setHeight(486 / (1));
                    backGroundImage.setWidth(474 / (1));
                    backGroundImage.setVisible(true);
                    backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (239 / 1),
                            (Gdx.graphics.getHeight() / 2) - (243 / 1));
                    mainMenuButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (exitButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((410) / 1));
                    if (gameSelection.isVisible()){
                        gameSelection.show(false);
                    }
                    showMainMenu();
                }
            }
        });

        stage.addActor(mainMenuButton);
    }

    /**
     private void initializePlayButton( final Krim game, final Sound sound){

     gameSelection = new GameSelection(stateEngine, game, sound);

     playButton = new TextButton(i18NBundle.get("play"),textButtonStyle);
     playButton.setSize(281/1, 44/1);
     //playButton.setPosition(backGroundImage.getX() + backGroundImage.getWidth() - playButton.getWidth() - (20/ 1), backGroundImage.getY() + backGroundImage.getHeight() - ((280) / 1));
     playButton.setVisible(false);

     playButton.addListener(new ClickListener() {
    @Override
    public void clicked(InputEvent event, float x, float y) {
    if (!event.getType().equals("touchUp")) {
    hide();
    gameSelection.show(true);
    }
    }
    });


     stage.addActor(playButton);
     }*/

    private void initializeBackButton(){

        backButton = new TextButton(i18NBundle.get("backbutton"),textButtonStyle);
        backButton.setSize(281/1, 44/1);
        //playButton.setPosition(backGroundImage.getX() + backGroundImage.getWidth() - playButton.getWidth() - (20/ 1), backGroundImage.getY() + backGroundImage.getHeight() - ((280) / 1));
        backButton.setVisible(false);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
                    backGroundImage.setHeight(600 / (1));
                    backGroundImage.setWidth(1000 / (1));
                    backGroundImage.setVisible(true);
                    backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (500 / 1),
                            (Gdx.graphics.getHeight() / 2) - (300 / 1));
                    //scenarioDetails.setPosition(backGroundImage.getX() + backGroundImage.getWidth() - playButton.getWidth() - (20/ 1), backGroundImage.getY() +  (20 / 1));
                    //scenarioDetails.setVisible(true);
                    mainMenuButton.setPosition(backGroundImage.getX() + (20/ 1), backGroundImage.getY() +  (20 / 1));
                    //scenarioDetails.setVisible(false);
                    //playButton.setVisible(false);
                    gameOptionsButton.setVisible(false);
                    //scenarios.show();
                }
            }
        });


        stage.addActor(backButton);
    }


    private void initializeCreditsTextLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        creditsText = new Label(i18NBundle.get("creditstext"),style);
        creditsText.setSize(281/1, 44/1);
        creditsText.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (creditsButton.getWidth()/2)), backGroundImage.getY() + backGroundImage.getHeight() - ((200) / 1));
        creditsText.setVisible(false);

        stage.addActor(creditsText);
    }

    private void initializeWindowTextLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        windowTextLabel = new Label(i18NBundle.get("windowtext"),style);
        windowTextLabel.setSize(281/1, 44/1);
        windowTextLabel.setPosition(backGroundImage.getX() + (50 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((150) / 1));
        windowTextLabel.setVisible(false);

        stage.addActor(windowTextLabel);
    }

    private void initializeFullScreenCheckBox(){

        fullScreen = new CheckBox("  " + i18NBundle.get("fullscreen"),checkBoxStyle);
        fullScreen.setPosition(backGroundImage.getX() + (50 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((100) / 1));

        fullScreen.setVisible(false);
        fullScreen.setChecked(false);

        fullScreen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (fullScreen.isChecked()) {
                        GamePreferences.saveFullScreen(true);
                    }else{
                        GamePreferences.saveFullScreen(false);
                    }
                }
            }
        });


        stage.addActor(fullScreen);

    }

    private void initializeWindowedCheckBox(){

        windowed = new CheckBox("  " + i18NBundle.get("windowed"),checkBoxStyle);
        windowed.setPosition(backGroundImage.getX() + (300 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((100) / 1));

        windowed.setVisible(false);
        windowed.setChecked(false);

        windowed.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    if (windowed.isChecked()) {
                        GamePreferences.saveWindowSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        GamePreferences.saveFullScreen(false);
                    }
                }
            }
        });

        stage.addActor(windowed);
    }
    private void initializeResetHelpCheckBox(){

        resetHelp = new CheckBox("  " + i18NBundle.get("resethelp"),checkBoxStyle);
        resetHelp.setPosition(backGroundImage.getX() + (50 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((200) / 1));

        resetHelp.setVisible(false);
        resetHelp.setChecked(false);

        resetHelp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    if (resetHelp.isChecked()) {
                        GamePreferences.resetPhaseInfo();
                    }
                }
            }
        });

        stage.addActor(resetHelp);
    }

    private void initializeWindowsSizeSelectBox(){
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
        listStyle.background = new NinePatchDrawable(np);;
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle(Fonts.getFont24(),
                Color.WHITE,new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton"))), scrollPaneStyle, listStyle);

        windowsSizeSelectBox = new SelectBox(selectBoxStyle);
        windowsSizeSelectBox.setItems("1920 * 1080", "3840 * 1080");
        windowsSizeSelectBox.setSelected("1920 * 1080");
        windowsSizeSelectBox.setSize(200, 30);

        windowsSizeSelectBox.setPosition(backGroundImage.getX() + (50 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((250) / 1));
        windowsSizeSelectBox.setVisible(false);

        stage.addActor(windowsSizeSelectBox);

    }

    private void initializeButtonGroup(){
        buttonGroup = new ButtonGroup();
        buttonGroup.add(fullScreen);
        buttonGroup.add(windowed);
        buttonGroup.setMaxCheckCount(1);
        if (GamePreferences.isFullScreen()){
            fullScreen.setChecked(true);
        }else{
            windowed.setChecked(true);
        }

    }
    public int getBackGroundHeight(){
        return backGroundHeight;
    }


}
