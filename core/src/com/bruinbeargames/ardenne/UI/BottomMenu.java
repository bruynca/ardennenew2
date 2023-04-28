package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.ardenne;

public class BottomMenu {

    private Button nextPhase;
    private final TextTooltip.TextTooltipStyle tooltipStyle;
    private boolean menuVisible = true;
    private EventListener nextPhaseEventListener;
    private boolean enablePhaseChange = true;
    public static BottomMenu instance;
    private I18NBundle i18NBundle;
    private Group group;
    private String cantChangePhaseMessage = "";

    public BottomMenu() {
        Stage stage = ardenne.instance.guiStage;
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
        group = new Group();
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        initializeNextPhaseButton();
        stage.addActor(group);

    }

    public void show() {
        if (group.getY() <= 50 && !menuVisible) {
            menuVisible = true;
            group.addAction(Actions.moveBy(0, 150, 0.5f));
        }
    }


    public void hide() {
        if (group.getY() > -1 && menuVisible) {
            menuVisible = false;
            group.addAction(Actions.moveBy(0, -150, 0.5f));
        }
    }

    public void enablePhaseChange() {
    	enablePhaseChange = true;
    }

    public void showBottomMenu() {

        nextPhase.setVisible(true);
        nextPhase.setPosition(Gdx.graphics.getWidth() - 110, 10);
    }

    private void initializeNextPhaseButton() {

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(UILoader.instance.bottomMenu.button.get("nextphase")));
        style.down = new TextureRegionDrawable(new TextureRegion(UILoader.instance.bottomMenu.button.get("nextphasepressed")));
        style.checked = new TextureRegionDrawable(new TextureRegion(UILoader.instance.bottomMenu.button.get("nextphasepressed")));
        nextPhase = new Button(style);
        nextPhase.setHeight(100 / (1));
        nextPhase.setWidth(100 / (1));
        nextPhase.setVisible(false);

        nextPhase.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enablePhaseChange) {
                    nextPhase.setChecked(false);
                    Gdx.app.log("BottomMenu", "NextPhase Pressed");

                    NextPhase.instance.endPhase(NextPhase.instance.getPhase());
                }else if (!enablePhaseChange){
                    nextPhase.setChecked(false);
 //                   EventManager.instance.errorMessage(cantChangePhaseMessage);
                }
            }
        });

 /*       nextPhaseEventListener = new TextTooltip(
                i18NBundle.format("nextphasetooltip", i18NBundle.get("combat")),
                tooltipStyle);

        nextPhase.addListener(nextPhaseEventListener); */

        group.addActor(nextPhase);
    }

    public void changeNextPhaseTooltipText(String text) {

        Array<EventListener> listeners = nextPhase.getListeners();
        for (EventListener listener : listeners) {
            if (listener instanceof TextTooltip) {
                ((TextTooltip) listener).getContainer().getActor().setText(text);
                ((TextTooltip) listener).getContainer().getActor().setWrap(false);
                ((TextTooltip) listener).getContainer().getActor().setAlignment(Align.center);
                break;
            }
        }
    }

    public Button getNextPhase() {
        return nextPhase;
    }

    public void setEnablePhaseChange(boolean enablePhaseChange) {
        this.enablePhaseChange = enablePhaseChange;
    }

    public void setCantChangePhaseMessage(String cantChangePhaseMessage) {
        this.cantChangePhaseMessage = cantChangePhaseMessage;
    }
}




