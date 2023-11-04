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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.AttackOdds;
import com.bruinbeargames.ardenne.GameLogic.BarrageExplode;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import static com.badlogic.gdx.graphics.Color.WHITE;
import static com.bruinbeargames.ardenne.GameLogic.BarrageExplode.barrageTableSTR;

public class WinDisplayBombard {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    TextureRegion backHilite = textureAtlas.findRegion("crtback");
    TextTooltip.TextTooltipStyle tooltipStyle;
    Stage stage;
    Window window;
    int cntCountersToProcess =0;
    I18NBundle i18NBundle;
    private EventListener hitOK;
    static public WinDisplayCRT instance;
    int height = 800;
    int width;
    int widthShim  = 160;
    Label.LabelStyle labelStyleg;//=new Label.LabelStyle(FontFactory.instance.largeFontWhite, Color.LIGHT_GRAY);
    Label.LabelStyle labelStyle;// =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
    Label.LabelStyle labelStyle2;// =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
    TextureRegionDrawable hilite = new TextureRegionDrawable(backHilite);
    Label.LabelStyle labelStyleHi;// =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
    Label.LabelStyle labelStyle2Hi;// =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);

    Label labelResults;
    Label labelDice;


    public WinDisplayBombard(){
        initializeWindow();
        initializeLabels();
        showOdds();
        displayWindow();
    }

    private void displayWindow() {
        window.setModal(false);
        window.setTransform(true);
        height = 500;
        width = 1300;
        window.setHeight(height);
        window.setWidth(width);
        //       window.pack();
        Vector2 v2 = GamePreferences.getWindowLocation("BombardDisplay");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition((Gdx.graphics.getWidth() - (window.getWidth() +120)), (Gdx.graphics.getHeight() - window.getHeight() - 400 ));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        if (window.getWidth() > Gdx.graphics.getWidth()){
            window.setTransform(true);
            float scale = Gdx.graphics.getWidth() /  (window.getWidth()+ 40);
            window.setScale(scale);
        }

        ardenne.instance.guiStage.addActor(window);

    }

    private void initializeLabels() {
        labelStyleg =new Label.LabelStyle(FontFactory.instance.largeFontWhite, Color.LIGHT_GRAY);
        labelStyle =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        labelStyle2 =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        labelStyleHi =new Label.LabelStyle(FontFactory.instance.largeFontWhite,Color.YELLOW);
        labelStyle2Hi =new Label.LabelStyle(Fonts.getFont24(),Color.WHITE);
        labelStyle2Hi.background = hilite;
        labelStyleHi.background = hilite;
        labelResults = new Label("RESULT",labelStyleg);
        labelDice = new Label("Dice",labelStyleg);

    }

    private void showOdds() {
        int ix=0;
        for (String strOdds: barrageTableSTR){
            Table table = getOddsTable(ix);
            window.add(table).expandX();
            width += widthShim;
            ix++;
        }
    }

    private Table getOddsTable(int ix) {
        Table table = new Table();
        //       table.debug();
        Label label = new Label(barrageTableSTR[ix],labelStyle2Hi);
        table.add(label).colspan(2);
        table.row();
        label = new Label("RESULT ",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        table.add(label).align(Align.left);
        label = new Label("Dice",labelStyleg);
        label.setAlignment(Align.right);
        table.add(label).align(Align.right).expandX();
        int ix2 =1; // watch out for dummy row
        String[] strArray;
        if (ix < 7) {
            strArray = BarrageExplode.getTable(BarrageExplode.barrageTable[ix]);
        }else{
            strArray = BarrageExplode.getTable(24);
        }
        for (String strOdds: strArray){
            table.row();
            String str;
            if (strOdds.length() == 0){
                str="Ne";
            }else{
                str = "Su";
                if (strOdds.contentEquals("1")){
                    str = "Su -1";
                }else{
                    if (strOdds.contentEquals("2")){
                        str = "Su -2";
                    }
                }
            }
            label = new Label(str,labelStyle);
            label.setAlignment(Align.left);
            label.setFontScale(.8f);
            table.add(label).align(Align.left);
            int[][] diceTable = AttackOdds.getDiceRow(ix);
            String dice = Integer.toString(ix2+1);
            label = new Label(dice,labelStyle2);
            label.setAlignment(Align.right);
            table.add(label).align(Align.right).expandX();
            //           label.setFontScale(.8f);

            ix2++;
        }
        return table;
    }

    private void initializeWindow() {
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
                i18NBundle.format("crtclose"),
                tooltipStyle);
        image.addListener(hitOK);
    }

    private void end() {
        window.setVisible(false);
        window.remove();
        if (window != null) {
            int lastX = (int) window.getX();
            int lastY = (int) window.getY();
            GamePreferences.setWindowLocation("BombardDisplay", lastX, lastY);

        }
    }

}
