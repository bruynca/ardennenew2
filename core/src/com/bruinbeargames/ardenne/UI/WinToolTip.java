package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.ardenne;

public class WinToolTip {
    /**
     *
     * @param str to be displayed
     * @param v2 where to be displayed
     */
    Label label;
    public WinToolTip(String str){
        Label.LabelStyle labelStyle = new  Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        labelStyle.background = new NinePatchDrawable(np);
        label = new Label(str,labelStyle);
    }

    public void show(Vector2 v2) {
        label.setPosition(v2.x,v2.y);
        ardenne.instance.guiStage.addActor(label);
    }

    public void remove() {
        label.remove();
    }
}
