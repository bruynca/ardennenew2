package com.bruinbeargames.ardenne.UI;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.ardenne;

import java.util.Observable;

public class EventConfirm extends Observable {


        private Group group;
        private Image backgroundImage;
        private Label textLabel;
        private Button yesButton;
        private Button noButton;
        private I18NBundle i18NBundle;
        private TextButton.TextButtonStyle textButtonStyle;

        static public EventConfirm instance;

        public EventConfirm(){

            instance = this;
            group = new Group();
            i18NBundle= GameMenuLoader.instance.localization;
            initializeStyles();
            initializeImageBackGround();
            initializeTextLabel();
            initializeYesButton();
            initializeNoButton();
            group.setVisible(false);
        }

        public void show(String text){
            textLabel.setText(text);
            textLabel.pack();
            GlyphLayout layout = textLabel.getGlyphLayout();
            float height = layout.height;
            float width = layout.width;
            textLabel.setSize(width + 10, height + 10);
            backgroundImage.setHeight(height + 40);
            backgroundImage.setWidth(width + 40);
            if(backgroundImage.getWidth() < 220){
                backgroundImage.setWidth(220);
            }
            if(backgroundImage.getHeight() < 150){
                backgroundImage.setHeight(150);
            }

            backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (backgroundImage.getWidth()/2),
                    (Gdx.graphics.getHeight() / 2) - (backgroundImage.getHeight()/2));
            textLabel.setPosition((backgroundImage.getX() + backgroundImage.getWidth()/2) - (textLabel.getWidth()/2), backgroundImage.getY() +  (backgroundImage.getHeight() - height - (20)));
            yesButton.setPosition(backgroundImage.getX() + (10), backgroundImage.getY() +  (10));
            noButton.setPosition(backgroundImage.getX() + backgroundImage.getWidth() - 10 - 100, backgroundImage.getY() +  (10));
            group.setVisible(true);
            ardenne.instance.guiStage.addActor(group);
        }

        private void initializeStyles(){

            textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("button"))),
                    new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("buttonpressed"))),
                    null,
                    Fonts.getFont24());
            if(!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
                textButtonStyle.font.getData().scale(1f);
            }
        }

        private void initializeImageBackGround() {


            NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("pbembackground"), 12, 12, 12, 12);
            backgroundImage = new Image();
            backgroundImage.setDrawable(new NinePatchDrawable(np));
            backgroundImage.setHeight(360 );
            backgroundImage.setWidth(500 );
            backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (250 / 1f),
                    (Gdx.graphics.getHeight() / 2) - (140 / 1f));
            backgroundImage.setVisible(true);
            group.addActor(backgroundImage);
        }

        private void initializeTextLabel(){
            Label.LabelStyle style = new Label.LabelStyle();
            style.font = Fonts.getFont24();
            if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)){
                style.font.getData().scale(1f);
            }
            textLabel = new Label("",style);
            textLabel.setSize(480/1f, 260/1f);
            textLabel.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 - textLabel.getWidth()/2, backgroundImage.getY() + backgroundImage.getHeight()/2 - textLabel.getWidth()/2);
            textLabel.setVisible(true);

            group.addActor(textLabel);
        }

        private void initializeYesButton(){

            yesButton = new TextButton(i18NBundle.get("yes"),textButtonStyle);
            yesButton.setSize(104/1f, 34/1f);
            yesButton.setVisible(true);
            yesButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!event.getType().equals("touchUp")) {
                        group.setVisible(false);
                        group.remove();
                        setChanged();
                        notifyObservers(new ObserverPackage(ObserverPackage.Type.ConfirmYes,null,0,0));

                    }
                }
            });

            group.addActor(yesButton);
        }

        private void initializeNoButton(){

            noButton = new TextButton(i18NBundle.get("no"),textButtonStyle);
            noButton.setSize(104, 34);
            noButton.setVisible(true);
            noButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!event.getType().equals("touchUp")) {
                        group.setVisible(false);
                        group.remove();
                        setChanged();
                        notifyObservers(new ObserverPackage(ObserverPackage.Type.ConfirmNo,null,0,0));

                    }
                }
            });

            group.addActor(noButton);
        }
    }

