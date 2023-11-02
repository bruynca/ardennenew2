package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Fonts {


    private static Fonts instance;
    private static BitmapFont font24;

    private Fonts(){

    }

    public static Fonts getInstance(){
        if(instance == null){
            instance = new Fonts();
        }
        return instance;
    }
    public void scaleEdge(float amt){

    }

    public static void setScale(float scale){
        BitmapFont.BitmapFontData data = font24.getData();
        data.scale(-.03f);
        //font24.getData().setScale(1f/scale);
  //      font24.getData().scale(-5f);
//        font24.getData().setScale(scaleX * scale, scaleY* scale);
        scaleX = font24.getData().scaleX;
        scaleY = font24.getData().scaleY;
        Gdx.app.log("Font", "After ScaleX="+scaleX);
        Gdx.app.log("Font", "After ScaleY="+scaleX);


    }


    public static BitmapFont getFont24() {
        return font24;
   //         return SplashScreen.instance.font;
    }
    static float scaleX = 0;
    static  float scaleY= 0;
    public static void loadFont(){

        font24 = GameMenuLoader.instance.font;
        scaleX = font24.getData().scaleX;
        scaleY = font24.getData().scaleY;
        Gdx.app.log("Font", "ScaleX="+scaleX);
        Gdx.app.log("Font", "ScaleY="+scaleX);


    }


}