package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.DiceEffect;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 *  fix bridge  will use bridge in BlowBridge
 */
public class FixBridge implements Observer {
    static public FixBridge instance;
    private I18NBundle i18NBundle;
    ArrayList<Bridge> arrBridgeGone = new ArrayList<>();
    ArrayList<Image> arrBridgeGoneImage = new ArrayList<>();
    ArrayList<Label> arrLabel = new ArrayList<>();
    static Label.LabelStyle labelStyleName
            = new Label.LabelStyle(FontFactory.instance.jumboFont, Color.YELLOW);

    //    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
//    static TextureRegion bridgegone = textureAtlas.findRegion("bridgeblown");
    CardsforGame cardsforGame;
    boolean isFixBridge = false;
//    Image imageBridgeGone;

    public FixBridge() {
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

    }

    /**
     * display all bridges eligible
     *
     * @param cardsforGame if null then it a die roll access
     */
    public void display(CardsforGame cardsforGame) {
        Gdx.app.log("FixBridge", "display");
        isFixBridge = false;
        this.cardsforGame = cardsforGame;
        /** get eligible bridges if none
         *  getout
         *
         */
        if (cardsforGame == null){
            BlowBridge.instance.resetEligible();

            if (BlowBridge.instance.getBlown().size() == 0){
                NextPhase.instance.nextPhase();
                return;
            }

            arrLabel.clear();
            EventPopUp.instance.show(i18NBundle.get("bridgerolls"));
            TurnCounter.instance.updateText(i18NBundle.get("fixbridegphase"));
        }

        /**
         * add listners to images form blownbridge
         *  the CardHandler should only allow this to be played
         *  Move form hexstage to guistage for choices
         *
         */
        int ix = 0;
        for (final Bridge bridge : BlowBridge.instance.getBlown()) {
            Image image = BlowBridge.instance.getBlownImage().get(ix);
            image.remove(); // remove from hexStage
            ardenne.instance.mapStage.addActor(image);
            ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    bridgeClicked(bridge);
                }
            };
            image.addListener(clickListener);
            image.toFront();
            if (cardsforGame == null) {
                String dieNeeded = Integer.toString(bridge.getDieNeedAbove());
                Label label = new Label(dieNeeded, labelStyleName);
                Vector2 v2 = new Vector2(image.getX(), image.getY());
                label.setPosition(v2.x+45,v2.y+15);
                ardenne.instance.mapStage.addActor(label);
                arrLabel.add(label);
                label.setFontScale(1.8F);
                label.setTouchable(Touchable.disabled);
            }

            ix++;
        }
        if (cardsforGame != null){
            if (cardsforGame.description.contains("szorney1")) {
                EventPopUp.instance.show(i18NBundle.get("fixbridge2"));
            } else {
                EventPopUp.instance.show(i18NBundle.get("fixbridge3"));
            }
        }else{
            isFixBridge = true;
            EventPopUp.instance.show(i18NBundle.get("fixbridge4"));
        }
    }

    private void bridgeClicked(Bridge bridge) {
        if (cardsforGame != null) {
            bridge.fix();
            BlowBridge.instance.removeBlown(bridge);
            if (cardsforGame != null) {
                CardHandler.instance.removeCard(cardsforGame);
            }

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
        }else{
           initDieRoll(bridge);
        }

    }
    int totDie;
    Bridge bridgeDieRoll;
    private void initDieRoll(Bridge bridge) {
        bridgeDieRoll = bridge;
        int die1 = getDieRoll();
        int die2 = getDieRoll();
        //       die1 =5;
        //       die2 = 6;
        Gdx.app.log("Attack", "die=" + die1 + " " + die2);
        totDie = die1+die2;
 //       totDie = 12;
        DiceEffect.instance.rollBlueDice(die2);
        DiceEffect.instance.rollRedDice(die1);
        DiceEffect.instance.addObserver(this);
    }

    public int getDieRoll()
    {
        Random diceRoller = new Random();
        int diceResult = diceRoller.nextInt(6) + 1;
//        int die = diceRoller.nextInt(6) + 1;
        int die = (int)(Math.random()*6) + 1;
        return die;

    }


    private void cleanup() {
        ardenne.instance.deleteObserver(this);
        DiceEffect.instance.deleteObserver(this);
        EventPopUp.instance.addObserver(this);

        for (Label label:arrLabel){
            label.remove();
        }
        arrLabel.clear();
        if (cardsforGame != null) {
            if (cardsforGame.isAllies) {
                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
            } else {
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
            }
        }else{
                BlowBridge.instance.moveToHexStage();

                NextPhase.instance.nextPhase();
            // see update for eventpopup
        }
    }
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  Hex touched
         */
        if (oB.type == ObserverPackage.Type.DiceRollFinished) {

            if (bridgeDieRoll.getDieNeedAbove() < totDie){
                bridgeDieRoll.fix();
                BlowBridge.instance.removeBlown(bridgeDieRoll);
                EventPopUp.instance.show(i18NBundle.get("bridgefixed"));
            }else{
                bridgeDieRoll.roleAttempt();
                int need = bridgeDieRoll.getDieNeedAbove();
                EventPopUp.instance.show(i18NBundle.format("bridgenotfixed",need));
            }
            int ix=0;
            for (Bridge bridgeDisable : BlowBridge.instance.getBlown()) {
                Image image = BlowBridge.instance.getBlownImage().get(ix);
                image.clearListeners();
                ix++;
            }


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
            return;
        }
        /**
         *  popup is hiding screen go to next phase after user has clicked on it
         *
         */
        if (oB.type == ObserverPackage.Type.EVENTPOPUPHIDE){
            EventPopUp.instance.deleteObserver(this);
            if (isFixBridge){
                isFixBridge = false;
            }else {
  //              NextPhase.instance.nextPhase();
            }
        }
        /**
         *  Hex touched
         */
        if (oB.type != ObserverPackage.Type.TouchUp) {
            return;
        }
        Hex hex = oB.hex;
        /**
         *  if click on correct hex let listner do work.
         */
        for (Bridge bridge : BlowBridge.instance.getBlown()) { // dont process clicks here
            if (bridge.getHex2() == hex || bridge.getHex1() == hex) {
                return;
            }
        }
        /**
         *  do cleanup
         */
        cleanup();


    }
}
