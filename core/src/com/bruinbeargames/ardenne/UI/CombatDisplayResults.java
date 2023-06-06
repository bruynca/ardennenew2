package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.CombatResults;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;

public class CombatDisplayResults extends Observable {

    static public CombatDisplayResults instance;
    private Image background;
    private Group group;
    private Label axisResults;
private Label alliedResults;
    private Label results;

    private boolean visible;
    private Label title;
    private I18NBundle i18NBundle;
    Stage stage;
    Label.LabelStyle labelStyleDisplay;
    public CombatDisplayResults(){
        labelStyleDisplay  =new Label.LabelStyle(FontFactory.instance.largeFontWhite, Color.YELLOW);

        this.instance = this;
        stage = ardenne.instance.guiStage;
        group = new Group();
        group.setVisible(false);
        i18NBundle = GameMenuLoader.instance.localization;

        initializeBackgroundImage();
        initializeDefenderResultsLabel();
        initializeAttackerResultsLabel();
        initializeResultsLabel();
        initializeTitleLabel();

        stage.addActor(group);
    }
    public void clearText(){
        alliedResults.setText("");
        axisResults.setText("");
        results.setText("");
    }
    public void updateResults(String strOdds){
        Gdx.app.log("CombatDisplayResults", "update Results"+strOdds);

        StringBuffer strResult = new StringBuffer();
        int attackerLoses;
        int defenderLoses;
        int defenderRetreats;
        if (strOdds.contentEquals("NR")){
            strResult.append(i18NBundle.get("noresult"));
        }else {
            for (int i = 0; i < strOdds.length(); i++) {
                switch (strOdds.charAt(i)) {
                    case 'A':
                        attackerLoses = Character.getNumericValue(strOdds.charAt(i + 1));
                        strResult.append(i18NBundle.format("attackerlose", Integer.toString(attackerLoses)));
                        strResult.append("  ");
                        break;
                    case 'D':
                        defenderLoses = Character.getNumericValue(strOdds.charAt(i + 1));
                        strResult.append(i18NBundle.format("defenderlose", Integer.toString(defenderLoses)));
                        strResult.append("  ");

                        break;
                    case 'r':
                        defenderRetreats = Character.getNumericValue(strOdds.charAt(i + 1));
                        strResult.append(i18NBundle.format("defenderretreat", Integer.toString(defenderRetreats)));
                        strResult.append("  ");
                        break;
                }
            }
        }
        results.setText(strResult);
        results.pack();
        GlyphLayout layout = results.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        results.setSize(width, height);
        float x = ((background.getWidth() - width)/2) +background.getX();
        results.setPosition(x , background.getY() + background.getHeight() - (height + 120));
        if (!group.isVisible()) {
            visible = true;
            group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.CombatDisplayResults,null,0,0));
       }

    }
    public void updateCombatResultsDefender(ArrayList<CombatResults> combatResults, boolean isAttackerAllies, Attack attack){
        Gdx.app.log("CombatDisplayResults", "updatCombatResultsDefender");

        this.attack = attack;
        isDefenseDisplayed = true;
        String defender = "";
        if (combatResults.size() > 0) {
            for (int i = 0; i < combatResults.size(); i++) {
                CombatResults combatResult = combatResults.get(i);
                String str = combatResult.getUnit().designation + " " +combatResult.getUnit().subDesignation;
                if (combatResult.isDestroyed()) {
                    defender += i18NBundle.format("destroyed", str) + "\n";
                    //defender += combatResult.getUnitName() + " was destroyed" + "\n";
                }
                if (combatResult.isStepLosses()) {
                    defender += i18NBundle.format("loststep", str) + "\n";
                    //defender += combatResult.getUnitName() + " lost step" + "\n";
                }

            }
            if (combatResults.get(0).getHexesRetreated() > 0) {
                defender += i18NBundle.format("defenderretreated", combatResults.get(0).getHexesRetreated()) + "\n";
                //defender += "Defending forces retreated " + combatResults.get(0).getHexesRetreated() + " hexes";
            }
        }else{
            defender += i18NBundle.format("noresult");
        }
        if (!isAttackerAllies){
            alliedResults.setText(defender);
            setAlliedLabel();
        }else {
            axisResults.setText(defender);
            setAxisLabel();
        }

    }
    Attack attack;
    public Attack getAttack(){
        return attack;
    }
    public void updateCombatResultsAttacker(ArrayList<CombatResults> combatResults, boolean isAttackerAllies, Attack attack){
        Gdx.app.log("CombatDisplayResults", "updatCombatResultsAttacker");

        this.attack = attack;
        isAttackDisplayed = true;
        String attacker = "";
        boolean isjustOneAdvance = false;
        boolean isjustMover = false;
        if (combatResults.size() > 0) {

            for (int i = 0; i < combatResults.size(); i++) {
                CombatResults combatResult = combatResults.get(i);
                String str = combatResult.getUnit().designation + " " +combatResult.getUnit().subDesignation;
                if (combatResult.isDestroyed()) {
                    attacker += i18NBundle.format("destroyed", str  )+ "\n";
                    //attacker += combatResult.getUnitName() + " was destroyed" + "\n";
                }
                if (combatResult.isStepLosses()) {
                    attacker += i18NBundle.format("loststep", str )+ "\n";
                    //attacker += combatResult.getUnitName() + " lost step" + "\n";
                }
                if (combatResult.isCanContinueMovement()) {
                    if (!isjustMover) {
                        attacker += i18NBundle.get("cancontinuemoving");
                        isjustMover = true;
                    }
                }
                if (combatResult.isCanAdvance()) {
                    if (!isjustOneAdvance) {
                        attacker += i18NBundle.get("canadvance");
                        isjustOneAdvance = true;
                    }
                    //attacker += "Attacking Units can Advance";
                }
                if (combatResults.get(0).getHexesRetreated() > 0) {
                    attacker += i18NBundle.format("attackerretreated", combatResults.get(0).getHexesRetreated()) + "\n";
                    //attacker += "Defending forces retreated " + combatResults.get(0).getHexesRetreated() + " hexes";
                }

            }
        }else{
            attacker += i18NBundle.format("noresult");
        }
        if (!isAttackerAllies){
            axisResults.setText(attacker);
            setAxisLabel();
            return;
        }else {
            alliedResults.setText(attacker);
            setAlliedLabel();
        }

    }
    boolean isAttackDisplayed = false;
    boolean isDefenseDisplayed = false;
    private void setAxisLabel(){
        axisResults.pack();
        GlyphLayout layout = axisResults.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        axisResults.setSize(width, height);
        axisResults.setPosition(background.getX() + 5 , background.getY() + background.getHeight() - (height + 160));
        if (!group.isVisible()) {
            visible = true;
            group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.CombatDisplayResults,null,0,0));
        }

    }
    private void setAlliedLabel(){
        alliedResults.pack();
        GlyphLayout layout = alliedResults.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        alliedResults.setSize(width, height);
        alliedResults.setPosition(background.getX() + background.getWidth()/2 + 5 , background.getY() + background.getHeight() - (height + 160));
        if (!group.isVisible()) {
            visible = true;
            group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.CombatDisplayResults,null,0,0));
        }

    }


    public void hide(){
        WinCRT.instance.end();
        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                visible = false;
            }
        });
        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false),run));
    }

    public boolean isDisplayed(){
        return visible;
    }


    private void   initializeBackgroundImage(){

        background = new Image(new TextureRegion(UILoader.instance.combatDisplay.asset.get("combatresultsdisplayrussian")));
        background.setHeight(304);
        background.setWidth(650);
        background.setPosition((Gdx.graphics.getWidth()/2 - background.getWidth()/2), (Gdx.graphics.getHeight()/2 - background.getHeight()/2));

        background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (isAttackDisplayed && isDefenseDisplayed){
                        CombatDisplay.instance.end();
                        attack.afterDisplay(this);
                    }
                    hide();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                MouseImage.instance.setIgnore(true);
                MouseImage.instance.setMouseHand();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                MouseImage.instance.setIgnore(false);
                MouseImage.instance.mouseImageReset();
            }
        });

        group.addActor(background);
    }

    private void initializeAttackerResultsLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        axisResults = new Label("",style);
        axisResults.setSize(30, 20);
        axisResults.setPosition(background.getX() + 5 , background.getY() + background.getHeight() - 127);
        axisResults.setVisible(true);

        axisResults.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    hide();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                MouseImage.instance.setIgnore(true);
                MouseImage.instance.setMouseHand();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                MouseImage.instance.setIgnore(false);
                MouseImage.instance.mouseImageReset();
            }
        });


        group.addActor(axisResults);
    }
    private void initializeResultsLabel() {
        results = new Label("",labelStyleDisplay);
        results.setSize(30, 20);
        results.setPosition(background.getX() + background.getWidth()/2 + 5 , background.getY() + background.getHeight() - 127);
        results.setVisible(true);

        results.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                MouseImage.instance.setIgnore(true);
                MouseImage.instance.setMouseHand();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                MouseImage.instance.setIgnore(false);
                MouseImage.instance.mouseImageReset();
            }
        });
        group.addActor(results);
    }


        private void initializeDefenderResultsLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        alliedResults = new Label("",style);
        alliedResults.setSize(30, 20);
        alliedResults.setPosition(background.getX() + background.getWidth()/2 + 5 , background.getY() + background.getHeight() - 127);
        alliedResults.setVisible(true);

        alliedResults.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    hide();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                MouseImage.instance.setIgnore(true);
                MouseImage.instance.setMouseHand();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                MouseImage.instance.setIgnore(false);
                MouseImage.instance.mouseImageReset();
            }
        });

        group.addActor(alliedResults);
    }
    private void initializeTitleLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        title = new Label(GameMenuLoader.instance.localization.get("combatresults"),style);
        title.pack();
        GlyphLayout layout = title.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        title.setSize(30, 20);
        title.setPosition(background.getX() + background.getWidth()/2 - width/2 , background.getY() + background.getHeight() - 90);
        title.setVisible(true);

        title.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    hide();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                MouseImage.instance.setIgnore(true);
                MouseImage.instance.setMouseHand();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                MouseImage.instance.setIgnore(false);
                MouseImage.instance.mouseImageReset();
            }
        });

        group.addActor(title);
    }
}
