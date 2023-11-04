package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

public class BombardDisplay {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion tTown =  textureAtlas.findRegion("town");
    TextureRegion tTrees =  textureAtlas.findRegion("trees");
    TextureRegion tBackGerman =  textureAtlas.findRegion("barragexis");
    TextureRegion tBackAllied =  textureAtlas.findRegion("barrageallies");
    TextureRegion tVillage =  textureAtlas.findRegion("village");
    TextureRegion tbinoculars =  textureAtlas.findRegion("binoculars");
    private Image background;
    private Image trees;
    private Image village;
    private Image line;
    private Image town;
    private Image binoculars;
    private Group group;
    private Group hexGroup;
    private Group artGroup;
    private int artilleryYPosition = 0;
    private Stage stage;
    private TextTooltip.TextTooltipStyle tooltipStyle;
    private I18NBundle i18NBundle;
    private Label barrageFactors;


    static public BombardDisplay instance;

    public BombardDisplay(){
        instance = this;
        stage = ardenne.instance.guiStage;
        group = new Group();
        artGroup = new Group();
        hexGroup = new Group();

        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);

        i18NBundle = GameMenuLoader.instance.localization;

        initializeBackgroundImage();
        initializeVillage();
        initializeBinocular();
        initializeLineImage();
        initializeTownImage();
        initializeTrees();
        initializeBarrageFactors();

        group.setVisible(false);
        stage.addActor(group);

    }


    public void initialize(Hex hexTarget, boolean allies){
        group.setVisible(true);
        if (allies){
            background.setDrawable(new TextureRegionDrawable(tBackAllied));
        }else {
            background.setDrawable(new TextureRegionDrawable(tBackGerman));
        }
        background.setHeight(180);
        background.setWidth(230);
        background.setPosition((Gdx.graphics.getWidth() - background.getWidth() - 10), (Gdx.graphics.getHeight() - background.getHeight() - 10 ));

        showTerrainIcons(hexTarget, allies);

        group.addActor(hexGroup);

    }

    public void show(boolean display){
        group.setVisible(display);
    }

    public void updateBarrage(int barrage) {

        artGroup.clear();
        artGroup.remove();

        int yNextLine = 0;
        int xSpace = 0;
        barrageFactors.setText((int)barrage + "");


        group.addActor(artGroup);
        WinBombard.instance.show(barrage);

    }

    public Vector2 getPosition()
    {
        Vector2 v2 = new Vector2(background.getX(),background.getY());
        return v2;
    }

    public Vector2 getSize()
    {
        Vector2 v2 = new Vector2(background.getWidth(),background.getHeight());
        return v2;
    }

    public void remove(){
        artGroup.remove();
        hexGroup.remove();
        group.setVisible(false);
    }

    public void displayCombat(){
        group.setVisible(true);
    }

    private void showTerrainIcons(Hex hexTarget, boolean allies){

        // Clear up first
        village.remove();
        trees.remove();
        town.remove();
        line.remove();
        binoculars.remove();

        artilleryYPosition = 0;
        int xPosition = 20;
        boolean isObserver =  checkAdjacentHex(hexTarget,allies);

        if(hexTarget.isTown()){
            village.setPosition(background.getX() + xPosition , background.getY() + background.getHeight() - 125);
            xPosition += 55;
            hexGroup.addActor(village);
        }
        if(hexTarget.isCity()){
            town.setPosition(background.getX() + xPosition , background.getY() + background.getHeight() - 125);
            xPosition += 55;
            hexGroup.addActor(town);
        }
        if(hexTarget.isForest()){
            trees.setPosition(background.getX() + xPosition , background.getY() + background.getHeight() - 125);
            xPosition += 55;
            hexGroup.addActor(trees);
        }
        if (isObserver){
            binoculars.setPosition(background.getX() + xPosition , background.getY() + background.getHeight() - 125);
            xPosition += 55;
            hexGroup.addActor(binoculars);

        }


        if (xPosition > 30){
            artilleryYPosition = 50;
            hexGroup.addActor(line);
        }
    }

    public boolean checkAdjacentHex(Hex hexTarget, boolean allies) {
        for (Hex hex:hexTarget.getSurround()){
            if (allies && hex.isAlliedOccupied[0]){
                return true;
            }
            if (!allies && hex.isAxisOccupied[0]){
                return true;
            }
        }
        return false;
    }

    private void initializeBackgroundImage(){

        background = new Image(new TextureRegion(UILoader.instance.combatDisplay.asset.get("russianbarragedisplay")));
        background.setHeight(180);
        background.setWidth(230);
        background.setPosition((Gdx.graphics.getWidth() - background.getWidth() - 10), (Gdx.graphics.getHeight() - background.getHeight() - 10 ));

        group.addActor(background);
    }
    private void initializeBinocular(){

        binoculars = new Image(tbinoculars);
        binoculars.setHeight(24);
        binoculars.setWidth(51);
        binoculars.setPosition(background.getX() + 30 , background.getY() + background.getHeight() - 125);
        binoculars.addListener(new TextTooltip(
                i18NBundle.get("binshift"),
                tooltipStyle));
    }
    private void initializeVillage(){

        village = new Image(tVillage);
        village.setHeight(24);
        village.setWidth(51);
        village.setPosition(background.getX() + 30 , background.getY() + background.getHeight() - 125);
        village.addListener(new TextTooltip(
                i18NBundle.get("artshift"),
                tooltipStyle));
    }

    private void initializeTrees(){

        trees = new Image(tTrees);
        trees.setHeight(24);
        trees.setWidth(51);
        trees.setPosition((Gdx.graphics.getWidth() / 2),
                (Gdx.graphics.getHeight() / 2));
        trees.addListener(new TextTooltip(
                i18NBundle.get("artshift"),
                tooltipStyle));

    }

    private void initializeLineImage(){

        line = new Image(new TextureRegion(UILoader.instance.combatDisplay.asset.get("line")));
        line.setHeight(2);
        line.setWidth(180);
        line.setPosition(background.getX() + 20 , background.getY() + background.getHeight() - 130);

    }

    private void initializeTownImage(){

        town = new Image(new TextureRegion(UILoader.instance.combatDisplay.asset.get("town")));
        town.setHeight(24);
        town.setWidth(51);
        town.setPosition((Gdx.graphics.getWidth() / 2),
                (Gdx.graphics.getHeight() / 2));
        town.addListener(new TextTooltip(
                i18NBundle.get("artshift"),
                tooltipStyle));

    }
    private void initializeBarrageFactors() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        barrageFactors = new Label("",style);
        barrageFactors.setFontScale(1.1F);
        barrageFactors.setAlignment(Align.center);
        barrageFactors.setSize(30, 20);
        barrageFactors.setPosition(background.getX() + 112 , background.getY() + background.getHeight() - 54);// 42
        barrageFactors.setVisible(true);

        group.addActor(barrageFactors);
    }


    /**
     * @param targetShooterSave
     */
    public void explode(Barrage.TargetShooterSave targetShooterSave) {
        /**
         *  This is after BarrageExplode has calculated stats
         *  all data should be in targetShooterSave
         */
    }
    public void create(Barrage.TargetShooterSave targetShooterSave){

    }
}

