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
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import static com.badlogic.gdx.graphics.Color.WHITE;

public class WinDisplayCRT {
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


    public WinDisplayCRT(){
        initializeWindow();
        initializeLabels();
        showOdds();
        displayWindow();
    }

    private void displayWindow() {
        window.setModal(false);
        window.setTransform(true);
        height = 500;
        width = 1600;
        window.setHeight(height);
        window.setWidth(width);
 //       window.pack();
        Vector2 v2 = GamePreferences.getWindowLocation("CRTDisplay");
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
        for (String strOdds: AttackOdds.result){
            Table table = getOddsTable(ix);
            window.add(table).expandX();
            width += widthShim;
            ix++;
        }
    }

    private Table getOddsTable(int ix) {
        Table table = new Table();
 //       table.debug();
        Label label = new Label(AttackOdds.oddsString[ix],labelStyle2Hi);
        table.add(label).colspan(3);
        table.row();
        int ixOdds = 0;
        label = new Label("RESULT ",labelStyleg);

        label.setAlignment(Align.left);
        //          label.setFontScale(.5F);
        table.add(label).align(Align.left);
        label = new Label("Dice",labelStyleg);
        label.setAlignment(Align.right);
        table.add(label).align(Align.right).expandX();
        for (String strOdds: AttackOdds.result){
            table.row();
            label = new Label(strOdds,labelStyle);
            label.setAlignment(Align.left);
            label.setFontScale(.8f);
            table.add(label).align(Align.left);
            int[][] diceTable = AttackOdds.getDiceRow(ix);
            String dice = Integer.toString(diceTable[ixOdds][0])+".."+Integer.toString(diceTable[ixOdds][1]);
            if (diceTable[ixOdds][0] == 0 || diceTable[ixOdds][1] == 0){
                dice = "";
            }
            label = new Label(dice,labelStyle2);
            label.setAlignment(Align.right);
            table.add(label).align(Align.right).expandX();
 //           label.setFontScale(.8f);

            ixOdds++;
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
            GamePreferences.setWindowLocation("CRTDisplay", lastX, lastY);

        }
    }
}
