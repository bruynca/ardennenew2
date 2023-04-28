package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.CombatResults;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class BarrageDisplayResults {

    static public BarrageDisplayResults instance;
    private Image background;
    private Group group;
    private Label attackerResults;
    private Label defenderResults;
    private Label title;
    private I18NBundle i18NBundle;
    Stage stage;


    public BarrageDisplayResults(){

        this.instance = this;
        stage = ardenne.instance.guiStage;
         group = new Group();
        group.setVisible(false);
        i18NBundle = GameMenuLoader.instance.localization;

        initializeBackgroundImage();
        initializeDefenderResultsLabel();
        initializeAttackerResultsLabel();
        initializeTitleLabel();

        stage.addActor(group);
    }

    public void updateGermanCombatResults(ArrayList<CombatResults> combatResults){


        String defender = "";
        if (combatResults.size() < 1){
            defender += i18NBundle.get("nolosses");
        }else {
            for (int i = 0; i < combatResults.size(); i++) {
                CombatResults combatResult = combatResults.get(i);
                String str = combatResult.getUnit().designation + " " +combatResult.getUnit().subDesignation;

                if (combatResult.isDestroyed()) {
                    defender +=  i18NBundle.format("destroyed", str) + "\n";
                    //defender += combatResult.getUnitName() + " was destroyed" + "\n";
                }
                if (combatResult.isStepLosses()) {
                    defender +=  i18NBundle.format("loststep", str) + "\n";
                    //defender += combatResult.getUnitName() + " took a step loss" + "\n";
                }
            }
        }

        defenderResults.setText(defender);
        defenderResults.pack();
        GlyphLayout layout = defenderResults.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        defenderResults.setSize(width, height);
        defenderResults.setPosition(background.getX() + 5 , background.getY() + background.getHeight() - (height + 110));

        if (!group.isVisible()) {
            group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
        }
    }

    public void updateRussianCombatResults(ArrayList<CombatResults> combatResults){

        String defender = "";
        if (combatResults.size() < 1){
            defender += i18NBundle.get("nolosses");
        }else {
            for (int i = 0; i < combatResults.size(); i++) {
                CombatResults combatResult = combatResults.get(i);
                String str = combatResult.getUnit().designation + " " +combatResult.getUnit().subDesignation;

                if (combatResult.isDestroyed()) {

                    defender +=  i18NBundle.format("destroyed", str) + "\n";
                    //defender += combatResult.getUnitName() + " was destroyed" + "\n";
                }
                if (combatResult.isStepLosses()) {
                    defender +=  i18NBundle.format("loststep", str) + "\n";
                    //defender += combatResult.getUnitName() + " took a step loss" + "\n";
                }
            }
        }

        attackerResults.setText(defender);
        attackerResults.pack();
        GlyphLayout layout = attackerResults.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        attackerResults.setSize(width, height);
        attackerResults.setPosition(background.getX() + background.getWidth()/2 + 5 , background.getY() + background.getHeight() - (height + 110));

        if (!group.isVisible()) {
            group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
        }
        group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
    }

    public void hide(){
        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
    }

    public void show(){
        group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
    }


    private void initializeBackgroundImage(){

        background = new Image(new TextureRegion(UILoader.instance.combatDisplay.asset.get("barrageresultsgerman")));
        background.setHeight(250);
        background.setWidth(650);
        background.setPosition((Gdx.graphics.getWidth()/2 - background.getWidth()/2), (Gdx.graphics.getHeight()/2 - background.getHeight()/2));

        background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
  //                  NextPhase.instance.nextPhase(this);
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
        attackerResults = new Label("",style);
        attackerResults.setSize(30, 20);
        attackerResults.setPosition(background.getX() + 5 , background.getY() + background.getHeight() - 127);
        attackerResults.setVisible(true);

        attackerResults.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
 //                   NextPhase.instance.nextPhase(this);
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


        group.addActor(attackerResults);
    }

    private void initializeTitleLabel() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        title = new Label(i18NBundle.get("barragetitle"), style);
        title.setSize(30 , 20);
        title.setPosition(background.getX() + 265, background.getY() + background.getHeight() - 35);
        title.setVisible(true);
        group.addActor(title);
    }

    private void initializeDefenderResultsLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        defenderResults = new Label("",style);
        defenderResults.setSize(30, 20);
        defenderResults.setPosition(background.getX() + background.getWidth() + 5 , background.getY() + background.getHeight() - 127);
        defenderResults.setVisible(true);

        defenderResults.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    hide();
  //                  NextPhase.instance.nextPhase(this);
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

        group.addActor(defenderResults);
    }
}
