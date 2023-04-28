package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class Airplane {
    static public Airplane instance;
    int count =0;
    TextureRegion texAirplane;
    ArrayList<Image> arrIMGAir = new ArrayList<>();
    private I18NBundle i18NBundle;
    private final TextTooltip.TextTooltipStyle tooltipStyle;


    public Airplane(){
        instance = this;
        TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
        texAirplane =  textureAtlas.findRegion("airplane");
        i18NBundle = GameMenuLoader.instance.localization;
        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);


    }
    public void load(int num){
        count = num;
        display();
    }
    public void add(int num){
        count += num;
        display();
    }
    public int getCount(){
        return count;
    }
    public TextureRegion getTexture(){
        return texAirplane;
    }

    private void display() {
        Vector2 v2 = TurnCounter.instance.getEndDisplay();
        for (Image img: arrIMGAir){
            img.remove();
        }
        arrIMGAir.clear();
        v2.y += 30;
        v2.x += 10;
        float xStart = v2.x;
        boolean isNeedNextRow= true;
        for (int i=0; i < count; i++){
            Image img = new Image(texAirplane);
            img.setPosition(v2.x, v2.y);
            img.setScale(.3f);
            ardenne.instance.guiStage.addActor(img);
            v2.x += img.getWidth() * .3f + 5;
            arrIMGAir.add(img);
            img.addListener(new TextTooltip(
                    i18NBundle.get("airplane"),
                    tooltipStyle));
            if (i > 7 && isNeedNextRow){
                v2.y -= img.getHeight() * .3f + 5;
                v2.x = xStart;
                isNeedNextRow = false;
            }
        }


    }

    public void remove(int i) {
        count -= i;
        if (count < 0){
            count = 0;
        }
        display();

    }
}
