package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Observable;

public class LanguageSupport extends Observable {

    private AssetManager assetManager;
    private int screenWidth;
    private Group group;


    public LanguageSupport(){
        Stage stage = ardenne.instance.guiStage;
        screenWidth = Gdx.graphics.getWidth();
        group = new Group();

        group.addActor(initializeChinaButton());
        group.addActor(initializeFranceButton());
        group.addActor(initializeGermanyButton());
        group.addActor(initializeSpainButton());
        group.addActor(initializeUSAButton());

        stage.addActor(group);
    }

    public void show(boolean displayFlags){

        group.setVisible(displayFlags);
    }

    private Button initializeUSAButton() {

        Button.ButtonStyle style = new Button.ButtonStyle ();

        style.up = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("usa")));
        style.down = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("usa")));
        style.checked = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("usa")));
        style.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("usaover")));

        Button usaButton = new Button(style);
        usaButton.setSize(60 / 1,40/ 1);
        usaButton.setVisible(true);

        usaButton.setPosition(screenWidth - (308 / 1),
                10  / 1);

        usaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    assetManager = new AssetManager();
                    assetManager.load("i18n/language", I18NBundle.class);
                    assetManager.finishLoading();
                    GameMenuLoader.instance.localization = assetManager.get("i18n/language", I18NBundle.class);
                    setChanged();
                    notifyObservers(EventMessages.LANGUAGE_CHANGED);

                }
            }
        });

        return usaButton;
    }

    private Button initializeFranceButton() {

        Button.ButtonStyle style = new Button.ButtonStyle ();

        style.up = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("france")));
        style.down = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("france")));
        style.checked = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("france")));
        style.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("franceover")));


        Button franceButton = new Button(style);
        franceButton.setSize(60  / 1,40  / 1);
        franceButton.setVisible(true);

        franceButton.setPosition(screenWidth - (246 / 1),
                10  / 1);

        franceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    assetManager = new AssetManager();
                    assetManager.load("i18n/language_fr", I18NBundle.class);
                    assetManager.finishLoading();
                    GameMenuLoader.instance.localization = assetManager.get("i18n/language_fr", I18NBundle.class);
                    setChanged();
                    notifyObservers(EventMessages.LANGUAGE_CHANGED);

                }
            }
        });

        return franceButton;
    }

    private Button initializeChinaButton() {

        Button.ButtonStyle style = new Button.ButtonStyle ();

        style.up = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("china")));
        style.down = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("china")));
        style.checked = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("china")));
        style.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("chinaover")));

        Button chinaButton = new Button(style);
        chinaButton.setSize(60 *(1 / 1),40 *(1 / 1));
        chinaButton.setVisible(true);

        chinaButton.setPosition(screenWidth - (184 / 1),
                10 / 1);

        chinaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    assetManager = new AssetManager();
                    assetManager.load("i18n/language_cn", I18NBundle.class);
                    assetManager.finishLoading();
                    GameMenuLoader.instance.localization = assetManager.get("i18n/language_cn", I18NBundle.class);
                    setChanged();
                    notifyObservers(EventMessages.LANGUAGE_CHANGED);
                }
            }
        });

        return chinaButton;
    }

    private Button initializeSpainButton() {

        Button.ButtonStyle style = new Button.ButtonStyle ();

        style.up =  new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("spain")));
        style.down =  new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("spain")));
        style.checked =  new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("spain")));
        style.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("spainover")));

        Button spainButton = new Button(style);
        spainButton.setSize(60  / 1,40  / 1);
        spainButton.setVisible(true);

        spainButton.setPosition(screenWidth - (122 / 1),
                10  / 1);

        spainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    assetManager = new AssetManager();
                    assetManager.load("i18n/language_es", I18NBundle.class);
                    assetManager.finishLoading();
                    GameMenuLoader.instance.localization = assetManager.get("i18n/language_es", I18NBundle.class);
                    setChanged();
                    notifyObservers(EventMessages.LANGUAGE_CHANGED);

                }
            }
        });

        return spainButton;
    }

    private Button initializeGermanyButton() {

        Button.ButtonStyle style = new Button.ButtonStyle ();

        style.up = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("germany")));
        style.down = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("germany")));
        style.checked = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("germany")));
        style.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.flags.assets.get("germanyover")));

        Button germanyButton = new Button(style);
        germanyButton.setSize(60 / 1,40  / 1);
        germanyButton.setVisible(true);

        germanyButton.setPosition(screenWidth - (60 / 1),
                10 / 1);

        germanyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {

                    assetManager = new AssetManager();
                    assetManager.load("i18n/language_de", I18NBundle.class);
                    assetManager.finishLoading();
                    GameMenuLoader.instance.localization = assetManager.get("i18n/language_de", I18NBundle.class);
                    setChanged();
                    notifyObservers(EventMessages.LANGUAGE_CHANGED);
                }
            }
        });

        return germanyButton;
    }
}

