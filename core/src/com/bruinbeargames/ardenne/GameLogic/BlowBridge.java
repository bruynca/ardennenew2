package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.BridgeExplosion;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class BlowBridge implements Observer {
    static public BlowBridge instance;
    private I18NBundle i18NBundle;
    ArrayList<Bridge> arrBridgeCanBeBlown = new ArrayList<>();
    ArrayList<Image> arrBlowBridgeImage = new ArrayList<>();
    ArrayList<Bridge> arrBridgeGone = new ArrayList<>();
    ArrayList<Image>  arrBridgeGoneImage = new ArrayList<>();
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion dynamite = textureAtlas.findRegion("blowbridge");
    static TextureRegion bridgegone = textureAtlas.findRegion("bridgeblown");
    CardsforGame cardsforGame;
    Image imageBridgeGone;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;



    public BlowBridge() {
        instance = this;
    }

    /**
     * display all bridges eligible
     *
     * @param cardsforGame
     */
    public void display(CardsforGame cardsforGame) {
        Gdx.app.log("BlowBridge", "createCombatImage");
        this.cardsforGame = cardsforGame;
        i18NBundle = GameMenuLoader.instance.localization;

        arrBridgeCanBeBlown = new ArrayList<>();
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);

        /** get eligible bridges
         *
         */
        for (Bridge bridge : Bridge.arrBridges) {
            if (!bridge.getBlown())
                if (!arrBridgeCanBeBlown.contains(bridge)) {
                    if (bridge.getHex1().isAxisEntered() && bridge.getHex2().isAxisEntered()) {
                        // do nothing  cant blow this bridge
                    } else {
                        arrBridgeCanBeBlown.add(bridge);
                    }
                }
        }
        /**
         * create displays
         *
         */
        for (final Bridge bridge : arrBridgeCanBeBlown) {
            Image image = new Image(dynamite);
            Vector2 v1 = bridge.getHex1().getCounterPosition();
            Vector2 v2 = bridge.getHex2().getCounterPosition();
            Vector2 v3 = new Vector2(((v1.x + v2.x) / 2)-17, ((v1.y + v2.y) / 2)+18);
            image.setPosition(v3.x, v3.y);
            arrBlowBridgeImage.add(image);
            ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    bridgeClicked(bridge);
                }
            };
            image.addListener(clickListener);
            String strTip = i18NBundle.format("dynamite");

            hitOK = new TextTooltip(
                    strTip,
                    tooltipStyle);
            image.addListener(hitOK);

            ardenne.instance.mapStage.addActor(image);
            ardenne.instance.addObserver(this);
        }

    }

    private void bridgeClicked(Bridge bridge) {
        for (Image image : arrBlowBridgeImage) {
            image.remove();
        }
        arrBlowBridgeImage.clear();
        bridge.blowUp();
        CardHandler.instance.removeCard(cardsforGame);
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               //                              cntProcess++;
                               //                              final int copy = cntProcess;
                               cleanup();
                           }
                       }
                , 1f                    //    (delay)
        );

    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        Hex hex = oB.hex;
        /**
         *  Hex touched
         */
        if (oB.type != ObserverPackage.Type.TouchUp) {
            return;
        }
        for (Bridge bridge : arrBridgeCanBeBlown) { // dont process clicks here
            if (bridge.getHex2() == hex || bridge.getHex1() == hex) {
                return;
            }
        }
        /**
         * clicked outside of bridges to blow  getout
         */
        cleanup();
    }


    private void cleanup() {
        ardenne.instance.deleteObserver(this);
        for (Image image : arrBlowBridgeImage) {
            image.remove();
        }
        if (cardsforGame.isAllies) {
            CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
        } else {
            CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
        }
    }
    public ArrayList<Bridge> getBlown(){
        return arrBridgeGone;
    }
    public ArrayList<Image> getBlownImage(){
        return arrBridgeGoneImage;
    }

    public void removeBlown(Bridge bridge) {
        int ix = arrBridgeGone.indexOf(bridge);
        arrBridgeGone.remove(bridge);
        arrBridgeGoneImage.get(ix).remove();
        arrBridgeGoneImage.remove(ix);

    }
    public void displayBlownBridge(Bridge bridge){

        imageBridgeGone = new Image(bridgegone);
        arrBridgeGoneImage.add(imageBridgeGone);
        arrBridgeGone.add(bridge);
        Vector2 v1 = bridge.getHex1().getCounterPosition();
        Vector2 v2 = bridge.getHex2().getCounterPosition();
        Vector2 v3 = new Vector2(((v1.x + v2.x) / 2)-17, ((v1.y + v2.y) / 2)+18);
        imageBridgeGone.setPosition(v3.x, v3.y);
        ardenne.instance.hexStage.addActor(imageBridgeGone);
        CenterScreen.instance.start(bridge.getHex1());
    }

    public void explosionFinshed() {
        ardenne.instance.isBridgeExplosion = false;

    }
    /**
     *  bridges are blown with animation after the german turn
     *  all blown bridges are checked to see if they match this turn
     *  We then blow them up 1 at a time
     */
    ArrayList<Bridge> arrBridgesToBlow = new ArrayList<>();

    /**
     *  check Bridges to blow
     * @return if none and calling rtn must go to next phase
     */
    public boolean  checkBridgeBlow() {
        arrBridgesToBlow.clear();
        ArrayList<Bridge> arrBlown = getBlown();
        if (arrBlown.size() == 0){
            Gdx.app.log("BlowBrdige", "CheckBrideBlow None");

            //           NextPhase.instance.nextPhase();
            return true;
        }
        for (Bridge bridge:arrBlown){
            if (bridge.getTurnBlown() == 1 && NextPhase.instance.getTurn() == 1){ // only blow bridges turn 1
                arrBridgesToBlow.add(bridge);
            }
        }
        if (arrBridgesToBlow.size() == 0){
 //           NextPhase.instance.nextPhase();
            Gdx.app.log("BlowBrdige", "CheckBrideBlow None");
            return true;
        }

        explodeBridge();
        return false;
    }
    public  void explodeBridge() {
        if (arrBridgesToBlow.size() == 0){
            Gdx.app.log("BlowBrdige", "CheckBrideBlow None");
            NextPhase.instance.nextPhase();
            return;
        }
        Bridge bridge = arrBridgesToBlow.get(0);
        arrBridgesToBlow.remove(bridge);
        int ix = arrBridgeGone.indexOf(bridge);
        arrBridgeGoneImage.get(ix).remove();
        ardenne.instance.hexStage.addActor(arrBridgeGoneImage.get(ix));
        BridgeExplosion.instance.setPosition(bridge.getHex1(), bridge.getHex2());
        BridgeExplosion.instance.start(0.12f);
        return;
    }
    public void moveToHexStage(){
        for (Image image:arrBridgeGoneImage){
            image.remove();
            ardenne.instance.hexStage.addActor(image);
        }
    }

    /**
     *  remove blownbridges allies have retaken  so Germans can not blowthem
     *
     */
    public void resetEligible() {
        ArrayList<Bridge> arrRemove = new ArrayList<>();
        ArrayList<Image> arrRemoveImage = new ArrayList<>();
        for (Bridge bridge:arrBridgeGone){
            if (bridge.getHex1().checkAxisInHex() || bridge.getHex2().checkAxisInHex()){
                // ok 1 side
            }else{
                arrRemove.add(bridge);
                int ix = arrBridgeGone.indexOf(bridge);
                arrRemoveImage.add(arrBridgeGoneImage.get(ix));
            }
        }
        arrBridgeGone.removeAll(arrRemove);
        arrBridgeGoneImage.removeAll(arrRemoveImage);
    }
}
