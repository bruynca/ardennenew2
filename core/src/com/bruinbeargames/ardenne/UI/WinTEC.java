package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.WinModal;
import com.bruinbeargames.ardenne.ardenne;

public class WinTEC {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");

    TextureRegion river =  textureAtlas.findRegion("tecriver");
    TextureRegion winback =  textureAtlas.findRegion("tecmain");
    TextureRegion close =  textureAtlas.findRegion("close");

    Image iRiver = new Image(river);


    TextureRegion bridge =  textureAtlas.findRegion("tecbridge");
    Image iBridge = new Image(bridge);

    TextureRegion primeroad =  textureAtlas.findRegion("tecprimeroad");
    Image iProad = new Image(primeroad);
    TextureRegion secroad =  textureAtlas.findRegion("tesecroad");
    Image iSroad = new Image(secroad);
    TextureRegion town =  textureAtlas.findRegion("tectown");
    Image iTown = new Image(town);
    TextureRegion village =  textureAtlas.findRegion("tecvillage");
    Image iVillage = new Image(village);
    TextureRegion forest =  textureAtlas.findRegion("tecforest");
    Image iForest = new Image(forest);
    TextureRegion clear =  textureAtlas.findRegion("tecclear");
    Image iClear = new Image(clear);
    float winWidth = 500; // 900 original
    float winHeight = 200; // 650 original
    final float counterSize =70f;
    private I18NBundle i18NBundle;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;
    Hex hex;
    int maxRows = 0;
    int maxCounters = 0;
    Window window;
    WinTEC(){
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        /**
         * window format
         */
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("wintec");
        window = new Window("Units", windowStyle);
        /**
         * close button
         */
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);
        Image image = new Image(close);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                end();
            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("closereindisplay"),
                tooltipStyle);
        image.addListener(hitOK);

        window.setModal(true);
 //       window.setBackground(new TextureRegionDrawable(new TextureRegion((winback))));
        Image imgBack = new Image(winback);
        window.addActor(imgBack);
        imgBack.setPosition(0,0);
        window.setTransform(true);
        int heightWindow = (Counter.sizeOnMap + 100);
        if (winWidth < 120){
            winWidth = 120;
        }
        window.setSize(winWidth,heightWindow);
        window.setPosition(10,10);
        WinModal.instance.set();
        showWindow();
    }
    private void showWindow(){
        Vector2 v2 = GamePreferences.getWindowLocation("wintec");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
  //          window.setPosition(v2.x, v2.y);

        }
        winWidth = 1061;
        winHeight = 865;
        window.setSize(winWidth,winHeight);
 //       loadTable(window);
        ardenne.instance.guiStage.addActor(window);


    }
    void loadTable(Window window){
        float x  = 10;
        float y= window.getHeight()  - 60;
        Label.LabelStyle labelStyle = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        Label label = new Label("Type",labelStyle);
        label.setFontScale(1.2f);
        window.addActor(label);
        label.setPosition(x,y);
       /*table.add(label);
        label = new Label("Description",labelStyle);
        table.add(label);
        label = new Label("Movement\n Mech",labelStyle);
        table.add(label);
        label = new Label("Movement\n Other",labelStyle);
        table.add(label);
        label = new Label("Combat",labelStyle);
        table.add(label);
        table.row();
        iClear.setScale(.5F);
        table.add(iClear);
        label = new Label("Clear",labelStyle);
        table.add(label);
        label = new Label("2",labelStyle);
        table.add(label);
        label = new Label("1",labelStyle);
        table.add(label);
        label = new Label("Other Terrain",labelStyle);
        table.add(label);
        return table; */
    }

    public  void end(){
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        GamePreferences.setWindowLocation("wintec", lastX, lastY);
        window.remove();
        WinModal.instance.release();
    }


}
