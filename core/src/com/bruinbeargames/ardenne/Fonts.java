package com.bruinbeargames.ardenne;

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

    public static void setScale(float scale){
        font24.getData().setScale(1/scale);
    }

    public static BitmapFont getFont24() {
        return font24;
    }

    public static void loadFont(){

        font24 = GameMenuLoader.instance.font;
    }


}