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
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class WinStackCombat implements Observer {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion ok =  textureAtlas.findRegion("ok");

    TextTooltip.TextTooltipStyle tooltipStyle;
    Window window;
    Stage stage;
    int cntCountersToProcess =0;
    I18NBundle i18NBundle;
    ArrayList<Counter> counterArrayList = new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    Attack attack;
    Hex hex;
    private boolean isActive = true;
    private EventListener hitOK;


    public WinStackCombat(ArrayList<Unit> arrUnitsIn,  Hex hex) {
        Gdx.app.log("WinStackCombat", "Observer");

        stage= ardenne.instance.guiStage;
        this.hex = hex;
        arrUnits.addAll(arrUnitsIn);
        i18NBundle = GameMenuLoader.instance.localization;
        ardenne.instance.addObserver(this);
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("moastackwindow");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setScale(1.5f);
        lab.setAlignment(Align.center);
        Image image = new Image(ok);
        image.setScale(1.5f);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                end();
            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("stackmoa"),
                tooltipStyle);
        image.addListener(hitOK);

        window.setModal(false);
        window.setTransform(true);

        cntCountersToProcess = arrUnits.size();
        if (cntCountersToProcess > 12){
            cntCountersToProcess =12;
        }
        int widthWindow;
        if (cntCountersToProcess > 1) {
            widthWindow = cntCountersToProcess * (Counter.sizeOnMap + 5) + 100;
        }else {
            widthWindow =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
        }
        int rows = 1;
        if (arrUnitsIn.size() > 12 ){
            rows =2;
        }
        int heightWindow = (Counter.sizeOnMap * rows + 100);
        window.setSize(widthWindow,heightWindow);
        window.setPosition(100,100);
        showWindow();

    }
    private void showWindow() {
        Vector2 v2 = GamePreferences.getWindowLocation("StackOfUnits");
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
        int cnt = 0;
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
                    ClickAction click = counter.getUnit().getMapCounter().getClickAction();
                    if (click != null){
                        click.click();
                    }
                    if (counter.getCounterStack().isHilited()) {
                        counter.getCounterStack().removeHilite();
                    }
                    else
                    {
                        counter.getCounterStack().hilite();
                    }
 //                   reCalculate();
                }
            });
            //              counter.stack.setSize( Counter.sizeOnMap,Counter.sizeOnMap);
            window.add(counter.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap).padBottom(8);
            counterArrayList.add(counter);
            cnt ++;
            if (cnt == 12){
                window.row();
            }
        }
        //        reCalculate();
    }



    private void end(){
        Gdx.app.log("WinStackCombat", "end()");
        isActive =false;
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        GamePreferences.setWindowLocation("StackOfUnits", lastX, lastY);
        window.remove();
        ArrayList<Unit> arrWork = new ArrayList<>();
        for (Counter counter:counterArrayList){
            if (counter.getCounterStack().isHilited()){
                arrWork.add(counter.getUnit());
            }
        }
        ardenne.instance.deleteObserver(this);
        if (arrWork.size() == 0){
            cancel();
        }else {
            Combat.instance.doDieRoll();
        }
    }
    public void cancel() {
        Gdx.app.log("WinStackCombat", "cancel");
        isActive = false;
        window.remove();
        ardenne.instance.deleteObserver(this);
        Combat.instance.cleanup(true);
        Combat.instance.doCombatPhase();
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void update(Observable o, Object arg) {
        Gdx.app.log("WinStackCombat", "update");

        ObserverPackage op;
        op = (ObserverPackage) arg;
        /**
         *  check if user has clicked outside the window
         *  which is  a cancel
         */
        float  winStartx = window.getX();
        float  winEndx = window.getX()+window.getWidth();
        float  winStarty = window.getY();
        float  winEndy = window.getY()+window.getHeight();
        int reverse = Gdx.graphics.getHeight() - op.y;
        if (op.x < winStartx || op.x > winEndx || reverse < winStarty || reverse > winEndy) {
            cancel();
        }
    }


}
