package com.bruinbeargames.ardenne;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import java.awt.Desktop;
import java.io.IOException;

public class Logo {

    private Stack logoStack;
  //  private Image imageYobo;
    private boolean firstTime = true;
    int listeCnt =0;

    public Logo(TextureAtlas textureAtlas, Stage stage){
        initializeLogoStack(textureAtlas);
  //      initializeImageYobo(textureAtlas);
  //      stage.addActor(imageYobo);
        stage.addActor(logoStack);
    }


    public void update(float percentage){
        percentage *=100;
        if (percentage > 5){
            logoStack.getChildren().get(0).setVisible(true);
        }
        if (percentage > 15){
            logoStack.getChildren().get(1).setVisible(true);
        }
        if (percentage > 25){
            logoStack.getChildren().get(2).setVisible(true);
        }
        if (percentage > 35){
            logoStack.getChildren().get(3).setVisible(true);
        }
        if (percentage > 45){
            logoStack.getChildren().get(4).setVisible(true);
        }
        if (percentage > 55){
            logoStack.getChildren().get(5).setVisible(true);
        }
        if (percentage > 65){
            logoStack.getChildren().get(6).setVisible(true);
        }
        if (percentage > 75){
            logoStack.getChildren().get(7).setVisible(true);
        }
        if (percentage > 85){
            logoStack.getChildren().get(8).setVisible(true);
        }
        if (percentage > 95){
            logoStack.getChildren().get(9).setVisible(true);
            logoStack.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    // When you click the button it will print this value you assign.
                    // That way you will know 'which' button was clicked and can perform
                    // the correct action based on it.
                    Gdx.app.log("Logo", "URL");
//				WinMMTable.instance.show();
                    if (Desktop.isDesktopSupported() && listeCnt == 0) {
                        try {
                            listeCnt++;
                            String strCheck = Gdx.files.local("url/bburl.html").readString();
                            String url_open =strCheck;
                            Desktop.getDesktop().browse(java.net.URI.create(url_open));
                        } catch (IOException ex) {
                           // SoundEffects.instance.PlayPopUp();

                            Dialogs.showDetailsDialog(ardenne.instance.guiStage, "Error", "Iternet Error",
                                    "Can not reach internet");
                            return;
                        }
                    }
                    ;

                };
            });
            logoStack.addListener(new ClickListener() {
                @Override
                public  void enter(InputEvent event, float x, float y,int pointer, Actor stack)
                {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                };
            });
            logoStack.addListener(new ClickListener() {
                @Override
                public  void exit(InputEvent event, float x, float y,int pointer, Actor stack)
                {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                };
            });

/*                if (firstTime) {
                firstTime = false;
                imageYobo.setVisible(true);
                imageYobo.addAction(moveBy(200, 0, 1));
            } */
        }

    }

    private void initializeLogoStack(TextureAtlas textureAtlas){

        logoStack = new Stack();
        Image image = new Image(new TextureRegion(textureAtlas.findRegion("10")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("20")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("30")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("40")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("50")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("60")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("70")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("80")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("90")));
        logoStack.add(image);
        image = new Image(new TextureRegion(textureAtlas.findRegion("100")));
        logoStack.add(image);
        logoStack.setSize(185,185);
        for (int i = 0; i<10; i++){
            logoStack.getChildren().get(i).setVisible(false);
        }
        float x = 20; //MainMenu.instance.imageLogo.getX();
        float y =  60; // MainMenu.instance.imageLogo.getY();
        // y += MainMenu.instance.imageLogo.getHeight() + 10;

        logoStack.setPosition(x,y);

    }



}



