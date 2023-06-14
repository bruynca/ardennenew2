package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class WinSupply {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    TextureRegion supplyIcon =  textureAtlas.findRegion("supply");

    static public WinSupply instance;
    TextTooltip.TextTooltipStyle tooltipStyle;
    Window window;
    Stage stage;
    int cntCountersToProcess =0;
    I18NBundle i18NBundle;
    ArrayList<Counter> counterArrayList = new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    Attack attack;
    Hex hex;
    Unit unitTransportWorkOn;
    Counter counterWorkedOn;
    private int numSupplyTruck = 0;
    private EventListener hitOK;
    Image imageSupply;
    boolean isHoufflaize = false;
    public WinSupply(ArrayList<Unit> arrSupply){
        arrUnits.clear();
        arrUnits.addAll(arrSupply);
        instance = this;
        stage= ardenne.instance.guiStage;
        this.hex = hex;
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("supplywindow");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setScale(1.5f);
        lab.setAlignment(Align.center);
        Image image = new Image(close);
        image.setScale(1.5f);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                end();
            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("supplyclose"),
                tooltipStyle);
        image.addListener(hitOK);

        window.setModal(false);
        window.setTransform(true);

        cntCountersToProcess = arrUnits.size();
        int widthWindow;
        if (cntCountersToProcess > 1) {
            widthWindow = cntCountersToProcess * (Counter.sizeOnMap + 5) + 100;
        }else {
            widthWindow =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
        }
        int heightWindow = (Counter.sizeOnMap + 100);
        window.setSize(widthWindow,heightWindow);
        window.setPosition(100,100);
        if (Supply.instance.getHoouflaize()){
            createHouffalizeSupplyImage();
        }
        showWindow();
    }
    private void showWindow() {
        Vector2 v2 = GamePreferences.getWindowLocation("winsupply");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        window.remove();
        setCounters();

        stage.addActor(window);
    }
    /**
     * create new UI counters with listners for our window
     * These counters are differant than the map counters and are only used for this UI screen
     */
    private void  setCounters(){
        final int size =100;
        boolean isFirst = true;
        for (Unit unit: arrUnits)
        {
            final Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
            counter.stack.setTransform(true);
            float ratio =(float) size/Counter.size;
            counter.stack.setScale(ratio);
            counter.getCounterStack().adjustFont(.8f);

            counter.stack.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override public void clicked (InputEvent event, float x, float y)
                {
                    if (counter.getCounterStack().isHilited()) {
                        counter.getCounterStack().removeHilite();
                        Supply.instance.cancelTransport(unitTransportWorkOn);
                        numSupplyTruck--;
                        if (numSupplyTruck < 0) {
                            numSupplyTruck = 0;
                        }
                        unitTransportWorkOn = null;
                    }
                    else
                    {
                        counter.getCounterStack().hilite();
                        counterWorkedOn = counter;
                        numSupplyTruck++;
                        if (numSupplyTruck == 2 && !isHoufflaize){
                            imageSupply.remove();
                        }
                        Supply.instance.createHexChoice(counter.getUnit(),0,false);
                    }
                }
            });
            //              counter.stack.setSize( Counter.sizeOnMap,Counter.sizeOnMap);
            window.add(counter.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap).padBottom(8);
            counterArrayList.add(counter);
        }
        //        reCalculate();
    }
    public void hideCurrent(){
        counterWorkedOn.getCounterStack().getStack().setVisible(false);
    }
    public void reShow(Unit unit){
        counterWorkedOn.getCounterStack().getStack().setVisible(true);
        counterWorkedOn.getCounterStack().removeHilite();
    }
    public void cancelHilite(){
        for (Counter counter:counterArrayList){
            counter.getCounterStack().removeHilite();
        }
    }



    public void end(){
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        GamePreferences.setWindowLocation("winsupply", lastX, lastY);
        window.remove();
        Unit.initTouchable(false);
        Supply.instance.removeHoufflaize();
        imageSupply.remove();
        NextPhase.instance.nextPhase();
    }
    public int getNumSupplyTruck(){
        return numSupplyTruck;
    }
    private void createHouffalizeSupplyImage(){
        imageSupply = new Image(supplyIcon);
        imageSupply.setScale(1.0F);
        blink(imageSupply);
        Vector2 v2 = Hex.hexTable[12][3].getCounterPosition(); // houffalize

        imageSupply.setPosition(v2.x + 10,v2.y +10-7);
        imageSupply.setSize(120f,120f);
        ardenne.instance.mapStage.addActor(imageSupply);

    }

    private void blink(Image image) {
        image.addAction(Actions.forever(Actions.sequence(
                Actions.alpha(0),
                Actions.fadeIn(0.5f),
                Actions.delay(0.5f),
                Actions.fadeOut(0.5f)
        )));

    }

    public void setHouffalizePossible() {
        imageSupply.clearActions();
        imageSupply.remove();
        ardenne.instance.mapStage.addActor(imageSupply);
        imageSupply.addAction(Actions.fadeIn(.01f));
    }

    public void blinkHouffalizeOn() {
        blink(imageSupply);
    }
    public void setHoufflaizeSet(){
        setHouffalizePossible();
        Hex.hexTable[12][3].cycleUnits();
        Supply.instance.addHoufflaize();
        isHoufflaize = true;
    }
}
