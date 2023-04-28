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
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SaveGame;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.ArrayList;

public class WinSaveGame {
    static TextureAtlas textureAtlas2 = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");

    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion ok =  textureAtlas.findRegion("ok");
    TextureRegion close =  textureAtlas.findRegion("close");

    TextTooltip.TextTooltipStyle tooltipStyle;
    Window window;
    Stage stage;
    I18NBundle i18NBundle;
    Label label;
    Label.LabelStyle labelStyleDisplay;


    ArrayList<Counter> counterArrayList = new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    Attack attack;
    Hex hex;
    static TextureRegion tfBack =  textureAtlas2.findRegion("tfback");
    static TextureRegion tfSelected =  textureAtlas2.findRegion("tfselected");
    static TextureRegion tfcusror =  textureAtlas2.findRegion("tfcusor");
    VisTextField visTextField;

    private EventListener hitOK;
    WinSaveGame(){
        i18NBundle = GameMenuLoader.instance.localization;
/**
 *  dont allow save game until in game proper
 */
 //       if (NextPhase.instance.getPhase() > Phase.GERMAN_CARD.ordinal())
        if (SaveGame.canSaveGame()){
            //continued
        }else{
            if (EventPopUp.instance.isShowing()) {
                EventPopUp.instance.hide();
            }
            EventPopUp.instance.show(i18NBundle.format("nosave"));
            return;
        }

        stage= ardenne.instance.guiStage;

        labelStyleDisplay  =new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        label = new Label(i18NBundle.format("savegamelabel") + ": ",labelStyleDisplay);
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("savewindow");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);
        visTextField = new VisTextField("");
        Image image = new Image(ok);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                String str = visTextField.getText();
                if (str != null) {
                SaveGame saveGame = new SaveGame(str);
                EventPopUp.instance.show(i18NBundle.format("gamesaved") + "\n\n" + str);
            }
                end();
            }
        });
        hitOK = new TextTooltip(
                i18NBundle.format("savehelp"),
                tooltipStyle);
        image.addListener(hitOK);
        window.getTitleTable().add(image);
        image = new Image(close);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    end();
            }
        });
        window.getTitleTable().add(image);

        window.setModal(false);
        window.setTransform(true);

        int widthWindow = 450;
        int heightWindow = 100;
        window.setSize(widthWindow,heightWindow);
        window.setPosition(100,100);
        showWindow();


    }

    private void end() {
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        GamePreferences.setWindowLocation("savegame", lastX, lastY);
        window.remove();
    }

    private void showWindow() {
        Vector2 v2 = GamePreferences.getWindowLocation("savegame");
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        window.remove();

        window.add(label);
        window.add(visTextField).align(Align.left);

        stage.addActor(window);

        visTextField.focusField();


    }

}
