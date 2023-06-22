package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class SignPost {
    static public SignPost instance;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion sign = textureAtlas.findRegion("bastognesign");
    ArrayList<Image> arrImage = new ArrayList<>();
    int turnPlayed= 0;

    public SignPost(){
        instance = this;
        for (int x = 0; x< Hex.xEnd; x++)
        {
            for (int y=0; y < Hex.yEnd; y++)
            {
                Hex hex = Hex.hexTable[x][y];
                if (hex.isJunction()){
                    Image image = new Image(sign);
                    Vector2 v1 = hex.getCounterPosition();
                    image.setPosition(v1.x-10, v1.y);
                    arrImage.add(image);
                }
            }
        }

    }
    public void display(int turn){
        turnPlayed = turn;
        for (Image image:arrImage)
        {
           ardenne.instance.hexStage.addActor(image);
        }
    }
    public void remove(int turn){
        for (Image image:arrImage){
            image.remove();
        }
    }

}
