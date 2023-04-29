package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.ardenne;

import java.util.Observable;

public class EventPopUp extends Observable {

    static public EventPopUp instance;
    private Group group;
    private Image backgroundImage;
    private Label textLabel;
    private boolean done;
    private CheckBox dontShowAgain;
    private CheckBox.CheckBoxStyle checkBoxStyle;
    public TextButton.TextButtonStyle textButtonStyle;
    private I18NBundle i18NBundle;
    private TextTooltip.TextTooltipStyle tooltipStyle;
    boolean isSpecial = false;
    boolean isShowing = false;
    Stage stage;




    public EventPopUp(Stage stage) {

        instance = this;
        group = new Group();
        this.stage = stage;
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);


        initializeImageBackGround();
        initializeTextLabel();
        initializeStyles();
        initializCheckBox();

        group.setVisible(false);

    }

    public void show(String text) {
        if (!isHelp) {
            dontShowAgain.setVisible(false);
        }
        group.toFront();
        group.setZIndex(999);
        textLabel.setText(text);
        textLabel.pack();
        GlyphLayout layout = textLabel.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
  //      height += 100;
        backgroundImage.setHeight(height + 90);
        backgroundImage.setWidth(width + 40);
        backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (backgroundImage.getWidth() / 2),
                (Gdx.graphics.getHeight() / 2) - (backgroundImage.getHeight() / 2));
        backgroundImage.setVisible(true);
        textLabel.setSize(width, height);
        textLabel.setPosition(backgroundImage.getX() + (backgroundImage.getWidth() / 2) - (textLabel.getWidth() / 2),
                backgroundImage.getY() + ((backgroundImage.getHeight() / 2)+25) - (textLabel.getHeight() / 2));

        group.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.5f)));
        isShowing = true;
        ardenne.instance.guiStage.addActor(group);

    }

    public void hide() {
        if (isSpecial){
            hideImmediate();

            return;
        }
        isShowing = false;
        setChanged();
        notifyObservers((new ObserverPackage(ObserverPackage.Type.EVENTPOPUPHIDE,null,0,0)));

//        group.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.visible(false)));
       group.setVisible(false);
        dontShowAgain.setChecked(false);
        dontShowAgain.setVisible(false);
        group.remove();
        stage.addActor(group);

    }
    public void hideImmediate() {
        isShowing = false;

        setChanged();
        notifyObservers((new ObserverPackage(ObserverPackage.Type.EVENTPOPUPHIDE,null,0,0)));

        dontShowAgain.setVisible(false);
        group.setVisible(false);
        if (isSpecial){
            isSpecial = false;
            NextPhase.instance.continuePhaseFirstTime();
        }
    }
    public void setSpecial(){
        isSpecial = true;
    }

    private void initializeImageBackGround() {

        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("eventsbackground"), 10, 10, 10, 10);

        backgroundImage = new Image(np);
        backgroundImage.setHeight(281);
        backgroundImage.setWidth(500);
        backgroundImage.setVisible(false);
        backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (250 / 1),
                (Gdx.graphics.getHeight() / 2) - (140 / 1));

        backgroundImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        group.addActor(backgroundImage);
    }

    private void initializeTextLabel() {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            style.font.getData().scale(1);
        }
        textLabel = new Label("", style);
        textLabel.setSize(480 / 1, 260 / 1);
        textLabel.setPosition(backgroundImage.getX() + backgroundImage.getWidth() / 2 - textLabel.getWidth() / 2, backgroundImage.getY() + backgroundImage.getHeight() / 2 - textLabel.getWidth() / 2);

        textLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                int bt=0;
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

        group.addActor(textLabel);
    }
    private void initializCheckBox(){

        dontShowAgain = new CheckBox("  " + i18NBundle.get("dontshowagain"), checkBoxStyle);
//        dontShowAgain.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 + (20 / 1), scenariosScrollPane.getY() - 230);
        dontShowAgain.setTransform(true);
        dontShowAgain.setScale(.9f);
        dontShowAgain.setVisible(true);
        dontShowAgain.setChecked(false);

        dontShowAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    if (dontShowAgain.isChecked()) {
                        HelpPage.instance.dontShowAgain();
                        dontShowAgain.setChecked(false);
                        hideImmediate();
                    }
                }
            }
        });

        dontShowAgain.addListener(new TextTooltip(
                i18NBundle.get("dontshowagai2"),
                tooltipStyle));

        group.addActor(dontShowAgain);
        dontShowAgain.setVisible(false);

    }

    private void initializeStyles() {

        textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("unselectedbutton"))),
                new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton")));

        checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkboxselected")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkbox")));
        checkBoxStyle.font = Fonts.getFont24();
    }
    boolean isHelp = false;
    public void showHelp(String str) {
        isHelp = true;
        show(str);
        float y = backgroundImage.getY();
        float y2= textLabel.getY();
        dontShowAgain.setVisible(true);
        dontShowAgain.setPosition(textLabel.getX()  + textLabel.getWidth() - 250 ,backgroundImage.getY() +20);
        isHelp = false;
    }

    public boolean isShowing() {
        return isShowing;
    }
    public void toFront(){
        group.toFront();
    }
}
