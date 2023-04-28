package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.BarrageExplode;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import static com.badlogic.gdx.graphics.Color.WHITE;

public class WinBombard {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    TextureRegion backHilite = textureAtlas.findRegion("crtback");
    TextureRegion info = textureAtlas.findRegion("info");


    TextTooltip.TextTooltipStyle tooltipStyle;
    Window window;
    Stage stage;
    I18NBundle i18NBundle;
    private EventListener hitOK;
    static public WinBombard instance;
    public WinBombard(){
        instance = this;
    }
    public void show(int barragePoints){
        if (window != null){
            window.remove();
            window = null;
        }
        stage= ardenne.instance.guiStage;
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);

        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("bombardwindow");
        window = new Window(title, windowStyle);
//        Label lab = window.getTitleLabel();
//       lab.setAlignment(Align.center);
//       Image image = new Image(info);
//        image.addListener(new ClickListener() {
//            @Override/
//            public void clicked(InputEvent event, float x, float y) {
//                end();
//            }
//        });
//        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("barrageoverview"),
                tooltipStyle);
        window.addListener(hitOK);
        int height = 0;
        Label.LabelStyle labelStyleg =new Label.LabelStyle(FontFactory.instance.largeFontWhite, Color.LIGHT_GRAY);

        Label.LabelStyle labelStyle =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        Label.LabelStyle labelStyle2 =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        TextureRegionDrawable hilite = new TextureRegionDrawable(backHilite);
        Label.LabelStyle labelStyleHi =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        Label.LabelStyle labelStyle2Hi =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        labelStyle2Hi.background = hilite;
        labelStyleHi.background = hilite;
        /**
         *  draw heading
         */

        Label label = new Label("RESULT",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        window.add(label).align(Align.left);
        label = new Label("Dice Total",labelStyleg);
        label.setAlignment(Align.right);
        window.add(label).align(Align.right).expandX();
        window.row();
        label = new Label("",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        window.add(label).align(Align.left);
        label = new Label("",labelStyleg);
        label.setAlignment(Align.right);
        height += label.getHeight()*12;

        window.add(label).align(Align.right).expandX();
        window.row();
        String strTab[] = BarrageExplode.instance.getTable(barragePoints);

        for (int i=1; i< 12; i++){
            String str;
            if (strTab[i].length() == 0){
                str="Ne";
            }else{
                str = "Su";
                if (strTab[i].contentEquals("1")){
                    str = "Su -1";
                }else{
                    if (strTab[i].contentEquals("2")){
                    str = "Su -2";
                    }
                }
            }
            label = new Label(str, labelStyle);
            label.setAlignment(Align.center);
            //          label.setFontScale(.5F);
            window.add(label).align(Align.center);
            /**
             * dice
             */
            height += label.getHeight();
            String dice = Integer.toString(i+1);
            label = new Label(dice,labelStyle2);
            label.setAlignment(Align.center);
            window.add(label).align(Align.center).expandX();
            window.row();
        }
        height += window.getTitleLabel().getHeight() + 100;

        window.setModal(false);
        window.setTransform(true);
        window.setHeight(height);
        window.setWidth(210);
        window.setPosition((Gdx.graphics.getWidth() - (window.getWidth() +160)), (Gdx.graphics.getHeight() - window.getHeight() - 400 ));
        Vector2 v2 = GamePreferences.getWindowLocation("bombard");
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
            GamePreferences.setWindowLocation("bombard", lastX, lastY);
            window.remove();
        }
    }

}
