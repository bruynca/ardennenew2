package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import static com.badlogic.gdx.graphics.Color.WHITE;

public class WinCRT {
TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
TextureRegion close =  textureAtlas.findRegion("close");
    TextureRegion backHilite = textureAtlas.findRegion("crtback");

    TextTooltip.TextTooltipStyle tooltipStyle;
    Window window;
    Stage stage;
    int cntCountersToProcess =0;
    I18NBundle i18NBundle;
    Attack attack;
    private EventListener hitOK;
    static public WinCRT instance;
    public WinCRT(){
        instance= this;
    }
    public void show(Attack attack, String dieResult){
        if (window != null){
            window.remove();
            window = null;
        }
        this.attack = attack;
        stage= ardenne.instance.guiStage;
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);

        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("crtwindow");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);
        Image image = new Image(close);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                end();
            }
        });
//        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("crttool"),
                tooltipStyle);
        window.addListener(hitOK);
        int height = 0;
        Label.LabelStyle labelStyleg =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.LIGHT_GRAY);

        Label.LabelStyle labelStyle =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        Label.LabelStyle labelStyle2 =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        TextureRegionDrawable hilite = new TextureRegionDrawable(backHilite);
        Label.LabelStyle labelStyleHi =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        Label.LabelStyle labelStyle2Hi =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        labelStyle2Hi.background = hilite;
        labelStyleHi.background = hilite;
        String[] strTab = attack.getAttackResults();
        /**
         *  draw heading
         */

        Label label = new Label("RESULT",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        window.add(label).align(Align.left);
        label = new Label("Dice ",labelStyleg);
        label.setAlignment(Align.right);
        window.add(label).align(Align.right).expandX();
        window.row();
        label = new Label("",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        window.add(label).align(Align.left);
        label = new Label("",labelStyleg);
        label.setAlignment(Align.right);
        window.add(label).align(Align.right).expandX();
        window.row();

        int[][] diceTable = attack.getDice();
        for (int i=0; i< strTab.length; i++){
            if (strTab[i].contentEquals(dieResult)){
                label = new Label(strTab[i],labelStyleHi);
            }else {
                label = new Label(strTab[i], labelStyle);
            }
            label.setAlignment(Align.left);
  //          label.setFontScale(.5F);
            window.add(label).align(Align.left);
            height += label.getHeight();
            String dice = Integer.toString(diceTable[i][0])+".."+Integer.toString(diceTable[i][1]);
            if (diceTable[i][0] == 0 || diceTable[i][1] == 0){
                dice = "";
            }
            if (strTab[i].contentEquals(dieResult)){
                label = new Label(dice,labelStyle2Hi);
            }else {
                label = new Label(dice,labelStyle2);
            }
            label.setAlignment(Align.right);
            window.add(label).align(Align.right).expandX();
            window.row();
        }
        height += window.getTitleLabel().getHeight() + 100;

        window.setModal(false);
        window.setTransform(true);
        window.setHeight(height);
        window.setWidth(200);
        window.setPosition((Gdx.graphics.getWidth() - (window.getWidth() +160)), (Gdx.graphics.getHeight() - window.getHeight() - 400 ));
        Vector2 v2 = GamePreferences.getWindowLocation("CRT");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition((Gdx.graphics.getWidth() - (window.getWidth() +120)), (Gdx.graphics.getHeight() - window.getHeight() - 400 ));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        stage.addActor(window);

    }
    public void end(){
        if (window != null) {
            int lastX = (int) window.getX();
            int lastY = (int) window.getY();
            GamePreferences.setWindowLocation("CRT", lastX, lastY);
            window.remove();
        }
    }

}
