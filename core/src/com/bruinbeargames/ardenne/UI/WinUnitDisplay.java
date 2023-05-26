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
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.WinModal;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class WinUnitDisplay {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    Window window;
    Stage stage;
    Label label;
    Table table;
    ArrayList<Counter> arrCounterSave = new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    ArrayList<ArrayList> arrUnitsByTurn = new ArrayList<>();
    ArrayList<Integer> arrTurns = new ArrayList<>();
    float winWidth = 500; // 900 original
    float winHeight = 200; // 650 original
    final float counterSize =70f;
    private I18NBundle i18NBundle;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;
    Hex hex;
    int maxRows = 0;
    int maxCounters = 0;
    WinModal winModal;
    public WinUnitDisplay(){
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
 //       String title = i18NBundle.format(Reinforcement.instance.getName(hex));
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

        window.setModal(false);
        window.setTransform(true);
        int heightWindow = (Counter.sizeOnMap + 100);
        if (winWidth < 120){
            winWidth = 120;
        }
        window.setSize(winWidth,heightWindow);
        window.setPosition(100,100);
        WinModal.instance.set();
        createdisplayUnits();

        showWindow();
    }
    private void createdisplayUnits() {
        arrUnits.clear();
//        arrUnits.addAll(Unit.getOnBoardAllied());
        arrUnits.addAll(Unit.getAllied());
        int cnt = 0;
        Integer in=0;
        ArrayList<Unit> arrWork = new ArrayList<>();
        arrUnitsByTurn.add(arrWork);
        arrTurns.add(1);
        for (Unit unit:arrUnits){
            if (cnt < 10){
                arrWork.add(unit);
                cnt++;
            }else{
                arrWork.add(unit);
                arrWork = new ArrayList<>();
                arrUnitsByTurn.add(arrWork);
                arrTurns.add(1);

                cnt=0;
            }
        }
        /**
         *  Calculate min size
         */
        maxRows = arrTurns.size();
        for (ArrayList arr:arrUnitsByTurn){
            if (arr.size() > maxCounters){
                maxCounters = arr.size();
            }
        }
        /**
         *  sort the tabel
         */
    }

    private void showWindow(){
        Vector2 v2 = GamePreferences.getWindowLocation("reinforcedisplay");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        if (maxCounters > 1) {
            winWidth = maxCounters * (Counter.sizeOnMap + 5) + 100;
        }else {
            winWidth =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
        }
        winHeight = (maxRows * (counterSize + 50) + 100);
        window.setSize(winWidth,winHeight);
        Label.LabelStyle labelStyle = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        int i=0;
        int turnsMax = GameSetup.instance.getScenario().getLength();
        for (Integer in:arrTurns){
            if (in <= turnsMax) {
                String str = i18NBundle.format("turn") + " " + in.toString();
                label = new Label(str, labelStyle);
                window.add(label).colspan(maxRows);
                window.row();
                Table table = loadTable(arrUnitsByTurn.get(i));
                window.add(table).padBottom(15);
                window.row();
                i++;
            }
        }

        ardenne.instance.guiStage.addActor(window);


    }
    private Table loadTable(ArrayList<Unit> arrUnits) {
        Table table = new Table();
        final int size =100;
        int i = 0;
        for (final Unit unit : arrUnits) {
            final Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
            arrCounterSave.add(counter);
            counter.stack.setTransform(true);
            float ratio =(float) size/Counter.size;
            counter.stack.setScale(ratio);
            counter.getCounterStack().adjustFont(.8f);
            counter.getCounterStack().getStack().setSize(counterSize, counterSize);
            table.add(counter.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap);
        }
        return table;

    }
    public  void end(){
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        GamePreferences.setWindowLocation("reinforcedisplay", lastX, lastY);
        window.remove();
        WinModal.instance.release();
    }
    public boolean isVisble() {
        if (window.getParent() != null){
            return true;
        }
        return false;
    }

}
