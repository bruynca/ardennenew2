package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.ardenne;

public class SoundSlider {
    private Slider soundSlider;
    private Image backGroundImage;
    private Button closeButton;
    private Group group;
    private Label soundLabel;
    private I18NBundle i18NBundle;
    public static SoundSlider instance;

    public SoundSlider(){
        instance = this;
        group = new Group();
        i18NBundle= GameMenuLoader.instance.localization;
        group.setVisible(false);
        initializeBackground();
        initializeSoundSlider();
        initializeSoundLabel();
        initializeCloseButton();

        ardenne.instance.guiStage.addActor(group);
    }

    public void show(boolean visible){
        group.setVisible(visible);
        group.toFront();
    }


    private void initializeBackground() {

        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 5, 5, 5, 5);
        backGroundImage = new Image();
        backGroundImage.setDrawable(new NinePatchDrawable(np));
        backGroundImage.setVisible(true);

        backGroundImage.setHeight(200);
        backGroundImage.setWidth(400);
        backGroundImage.setPosition((Gdx.graphics.getWidth() / 2) - ((backGroundImage.getWidth() / 2)),
                (Gdx.graphics.getHeight() / 2) - ((backGroundImage.getHeight() / 2)));

        group.addActor(backGroundImage);
    }

    private void initializeSoundLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();

        soundLabel = new Label(i18NBundle.get("soundvolume"),style);
        soundLabel.setSize(100/1, 40/1);
        soundLabel.setPosition(backGroundImage.getX() + backGroundImage.getWidth()/2 - soundLabel.getWidth()/2, backGroundImage.getY() + backGroundImage.getHeight() - soundLabel.getHeight() - 20);
        soundLabel.setVisible(true);

        soundLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    group.setVisible(false);
                }
            }
        });

        group.addActor(soundLabel);
    }

    private void initializeSoundSlider() {

        final Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("sliderbackground")));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("sliderknob")));

        soundSlider = new Slider(0f, 1f, .1f, false, sliderStyle);
        soundSlider.setWidth(310);
        soundSlider.setPosition(backGroundImage.getX() + (50 / 1), backGroundImage.getY() + backGroundImage.getHeight() - ((120) / 1));
        soundSlider.setValue(.5f);

        soundSlider.addListener(new InputListener() {
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        Gdx.app.log("SoundSlider","value="+soundSlider.getValue());
                                        Gdx.app.log("SoundSlider","value="+soundSlider.getVisualPercent());
                                        //              SoundsLoader.instance.setVolume(soundSlider.getValue());
                                        return true;
                                    }
                                    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                                        Gdx.app.log("SoundSliderUp","value="+soundSlider.getValue());
                                        Gdx.app.log("SoundSliderUp","value="+soundSlider.getVisualPercent());
                                        SoundsLoader.instance.setVolume(soundSlider.getValue());
                                        GamePreferences.saveVolume(soundSlider.getValue());

                                    }

                                }
        );

        group.addActor(soundSlider);
    }

    private void initializeCloseButton(){

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("unselectedbutton"))),
                new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("selectedbutton")));
        if(!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            textButtonStyle.font.getData().scale(1);
        }

        closeButton = new TextButton(i18NBundle.get("close"),textButtonStyle);
        closeButton.setSize(281/1, 44/1);
        closeButton.setPosition(backGroundImage.getX() + ((backGroundImage.getWidth()/2) - (closeButton.getWidth()/2)), backGroundImage.getY()  + ((10) / 1));
        closeButton.setVisible(true);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                group.setVisible(false);
                SoundsLoader.instance.stopSounds();
            }
        });


        group.addActor(closeButton);
    }

}
