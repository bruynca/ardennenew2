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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.AI.AIReinforcementScenario1;
import com.bruinbeargames.ardenne.AI.AIScenario1;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSelection;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class WinAIDisplay {
    Window window;
    Stage stage;
    TextTooltip.TextTooltipStyle tooltipStyle;

    int cntCountersToProcess =0;
    I18NBundle i18NBundle;
    TextButton.TextButtonStyle tx = GameSelection.instance.textButtonStyle;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");

    HiliteHex hiliteHex;
    ArrayList<TextButton> arrTexButtons = new ArrayList<>();
    ArrayList<Label> arrLabel = new ArrayList<>();
    static Label.LabelStyle labelStyleName
            = new Label.LabelStyle(FontFactory.instance.yellowFont, Color.YELLOW);


    private EventListener hitOK;
    static public WinAIDisplay instance;
    public WinAIDisplay(){
        instance = this;
        stage= ardenne.instance.guiStage;
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = "AI Window";
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setScale(1.5f);
        lab.setAlignment(Align.center);
        Image image = new Image(close);
        image.setScale(1.5f);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.setVisible(false);
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                clearLabels();
                int lastX = (int) window.getX();
                int lastY = (int) window.getY();
                GamePreferences.setWindowLocation("WinAI", lastX, lastY);
            }
        });
        window.getTitleTable().add(image);
        window.setModal(false);
        window.setTransform(true);

        int widthWindow = 300;
        if (cntCountersToProcess > 1) {
            widthWindow = cntCountersToProcess * (Counter.sizeOnMap + 5) + 100;
        }else {
            widthWindow =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
        }
        int heightWindow = (Counter.sizeOnMap + 100);
        window.setSize(widthWindow,heightWindow);
        window.setPosition(100,100);
//        showWindow();
    }

    private void showWindow() {
        Vector2 v2 = GamePreferences.getWindowLocation("WinAI");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        Table table = getButtons();
        window.add(table);
        window.pack();
 //       window.remove();
        setLabels();
        stage.addActor(window);
    }

    private Table getButtons() {
        Table table = new Table();
        TextButton tb = new TextButton("Clervaux",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                hiliteHex = new HiliteHex(AIScenario1.instance.getArrClervaux(), HiliteHex.TypeHilite.AI,null);
                displayArrayLabels(AIScenario1.instance.getArrClervaux());

            }

        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);

        tb = new TextButton("Wiltz",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                hiliteHex = new HiliteHex(AIScenario1.instance.getArrWiltz(), HiliteHex.TypeHilite.AI,null);
                displayArrayLabels(AIScenario1.instance.getArrWiltz());
            }
        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);

        tb = new TextButton("EttleBruck",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                hiliteHex = new HiliteHex(AIScenario1.instance.getArrEttlebruck(), HiliteHex.TypeHilite.AI,null);
                displayArrayLabels(AIScenario1.instance.getArrEttlebruck());

            }
        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);

        table.row();

        tb = new TextButton("Special Hexs",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                clearLabels();
                hiliteHex = new HiliteHex(arrSpecial,  HiliteHex.TypeHilite.AI,null);

            }
        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);
        table.row();
        tb = new TextButton("AIOrders Hexs",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                clearLabels();
                hiliteHex = new HiliteHex(arrHexAI,  HiliteHex.TypeHilite.AI,null);

            }
        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);

        tb = new TextButton("Bastogne",tx);
        tb.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiliteHex != null){
                    hiliteHex.remove();
                }
                clearLabels();
                hiliteHex = new HiliteHex(AIReinforcementScenario1.instance.arrBastogne,   HiliteHex.TypeHilite.AI,null);

            }
        });
        arrTexButtons.add(tb);
        table.add(tb).width(100);
        table.pack();

        return table;

    }
    ArrayList<Hex> arrSpecial = new ArrayList<>();
    ArrayList<Hex> arrHexAI = new ArrayList<>();
    public void addSpecial(ArrayList<Hex> arrIn){
        arrSpecial.clear();
        arrSpecial.addAll(arrIn);
    }
    public void addHexAi(ArrayList<Hex> arrIn){
        arrHexAI.clear();
        arrHexAI.addAll(arrIn);
    }

    private void displayArrayLabels(ArrayList<Hex> arrHex) {
        clearLabels();
        int i=0;
        for (Hex hex:arrHex){
            Label label = new Label(Integer.toString(i), labelStyleName);
            label.setFontScale(1.5f);
            label.setColor(Color.YELLOW);
            Vector2 vector2 = hex.GetDisplayCoord();
            label.setPosition(vector2.x + 30, vector2.y + 50);
            ardenne.instance.mapStage.addActor(label);
            arrLabel.add(label);
            i++;
        }
    }

    private void clearLabels() {
        for (Label lb:arrLabel){
            lb.remove();
        }
        arrLabel.clear();
    }


    private void setLabels() {
        Table table = new Table();
    }

    public void show() {
        window.setVisible(true);
    }
}
