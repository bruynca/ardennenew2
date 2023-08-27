package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.AccessInternet;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.SoundEffects;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.ardenne;

public class VictoryPopup {

    private Image backGroundImage;

    private Label victoryTitle;
    private Label victorText;
    private Image victoryFlag;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion flag =  textureAtlas.findRegion("usflag");

    private Image victoryUS = new Image(flag);

    private boolean isDisplayed = false;
    private Stage stage;
    private Group group;
    private I18NBundle i18NBundle;

    static public VictoryPopup instance;


    public VictoryPopup() {

        this.stage = ardenne.instance.guiStage;
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

        initialize();
    }

    public void initialize() {
        this.group = new Group();

        initializeImageBackGround();
        initializeLabels();
        addVictoryFlag("german");

        group.setVisible(false);
        stage.addActor(group);
    }

    private void initializeImageBackGround() {
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"),5, 5, 5, 5);
        backGroundImage = new Image();
        backGroundImage.setDrawable(new NinePatchDrawable(np));
        backGroundImage.setVisible(true);
        backGroundImage.setHeight(200);
        backGroundImage.setWidth(600);
        backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - ((backGroundImage.getWidth() / 2)),
                (Gdx.graphics.getHeight() / 2) - ((backGroundImage.getHeight() / 2)));
        group.addActor(backGroundImage);
    }

    private void initializeLabels()
    {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        victoryTitle = new Label("", style);
        victoryTitle.setHeight(30);
        victoryTitle.setWidth(260);
        victoryTitle.setVisible(true);
        victoryTitle.setPosition(backGroundImage.getX() + 70, backGroundImage.getY() + 50);
        victoryTitle.setAlignment(Align.center);
        group.addActor(victoryTitle);

        victorText = new Label("", style);
        victorText.setHeight(30);
        victorText.setWidth(260);
        victorText.setVisible(true);
        victorText.setPosition(backGroundImage.getX() + 70, backGroundImage.getY() + 50);
        victorText.setAlignment(Align.center);
        group.addActor(victorText);
    }

    private void addVictoryFlag(String country){
        if (country.contains("usa")){
            victoryFlag = victoryUS;
        }else {
            victoryFlag = new Image(GameMenuLoader.instance.victory.asset.get(country));
        }
        victoryFlag.setPosition(backGroundImage.getX() + backGroundImage.getWidth()/2 - victoryFlag.getWidth()/2, backGroundImage.getY() + backGroundImage.getHeight() - 30);
        group.addActor(victoryFlag);
    }





    public void updateText(String text, String country) {
        victoryFlag.remove();
        addVictoryFlag(country);
        victorText.setText(text);
        victorText.pack();
        GlyphLayout layout = victorText.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - ((backGroundImage.getWidth() / 2)),
                (Gdx.graphics.getHeight() / 2) - ((backGroundImage.getHeight() / 2)));
        victorText.setPosition(backGroundImage.getX() + backGroundImage.getWidth()/2 - width/2, backGroundImage.getY() + (backGroundImage.getHeight() / 2) - (height / 2));
        show();
        TurnCounter.instance.updateText(i18NBundle.get("gameend"));
    }

    public void hide() {
        group.setVisible(false);
        isDisplayed = false;
    }

    public void show()
    {
        group.setVisible(true);
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public String determineVictor() {
        SoundsLoader.instance.playTada();
        if (GameSetup.instance.getScenario() == GameSetup.Scenario.Intro){
            return checkIntro();
        }else  if (GameSetup.instance.getScenario() == GameSetup.Scenario.Lehr) {
            return checkLehr();
        }if (GameSetup.instance.getScenario() == GameSetup.Scenario.SecondPanzer) {
            return checkSecondPanzer();
        }else{
            checkCounterAttack();
            return"";
        }
    }

    private void checkCounterAttack() {
    }

    private String checkSecondPanzer() {
        /**
         * if end of game check supply
         */
        int turn = NextPhase.instance.getTurn();
        if (turn == GameSetup.instance.getScenario().getLength()){
            return ""; // to be coded
        }
        saveWinner("allied");
        updateText(i18NBundle.get("2ndlosernoexit"),"usa");
        return "allied";
    }

    private String checkLehr() {
        /**
         * if end of game check supply
         */
        int turn = NextPhase.instance.getTurn();
        if (turn == GameSetup.instance.getScenario().getLength()){
            return ""; // to be coded
        }
        saveWinner("allied");
        updateText(i18NBundle.get("lehrlosernoexit"),"usa");
        return "allied";

    }

    private String  checkIntro() {
        if (Hex.hexTable[19][14].isAxisEntered() &&
            Hex.hexTable[8][11].isAxisEntered() &&
            Hex.hexTable[8][12].isAxisEntered()){
            updateText(i18NBundle.get("germanscene1victor"),"german");
            saveWinner("German");
            return "German";
        }else{
            saveWinner("allied");
            updateText(i18NBundle.get("americanscene1victor"),"usa");
            return "allied";
        }

    }
    private void saveWinner(String strVic){
        AccessInternet.updateGame(GameSetup.instance.getScenario().getLength(), strVic);
        return;

    }
}
