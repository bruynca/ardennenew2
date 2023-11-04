package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHandler;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class AttackArrows {

    private static AttackArrows instance;
    private Group group;
    private ArrayList<Image> arrImages = new ArrayList<>();
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion texArrow =  textureAtlas.findRegion("attackarrow0");


    private AttackArrows() {
        group = new Group();
    }

    public static AttackArrows getInstance() {
        if (instance == null) {
            instance = new AttackArrows();
        }
        return instance;
    }

    public void showArrows(ArrayList<Hex> arrHexes, Hex targetHex) {
        group.clear();
        group.remove();
        for (int i = 0; i < arrHexes.size(); i++) {
            Hex hex = (Hex) arrHexes.get(i);
            Image arrowImage = getAttackArrow(hex, targetHex);
             group.addActor(arrowImage);
        }
        ardenne.instance.mapStage.addActor(group);
        for (Hex hex:arrHexes){
            Counter.rePlace(hex);
        }
        int i=0;

    }

    public void removeArrows() {
        group.clear();
        group.remove();
    }

    public Image getAttackArrow(Hex fromHex, Hex toHex) {
        Hex[] tabHex = HexHandler.getSurround(toHex);
        // Work out attack direction arrow and add correct image
        Image i = new Image(texArrow);
        i.setScale(1.4f);
        if (tabHex[0] == fromHex) {
            i.setPosition(fromHex.getCounterPosition().x + 11, fromHex.getCounterPosition().y -55);

        } else if (tabHex[1] == fromHex) {
            i.rotateBy(-60F);
            i.setPosition(fromHex.getCounterPosition().x -75, fromHex.getCounterPosition().y + 45);

        } else if (tabHex[2] == fromHex) {
            i.rotateBy(-120F);
            i.setPosition(fromHex.getCounterPosition().x - 20, fromHex.getCounterPosition().y + 165);

        } else if (tabHex[3] == fromHex) {
            i.rotateBy(180F);
            i.setPosition(fromHex.getCounterPosition().x + 104, fromHex.getCounterPosition().y + 186);
        }else if (tabHex[4] == fromHex) {
            i.rotateBy(-240F);
            i.setPosition(fromHex.getCounterPosition().x + 188, fromHex.getCounterPosition().y + 85);

        }
        else if (tabHex[5] == fromHex) {
            i.rotateBy(-300F);
            i.setPosition(fromHex.getCounterPosition().x + 140, fromHex.getCounterPosition().y - 40);

        }
        return i;
    }
}
