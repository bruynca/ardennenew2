package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class HexHiliteEdge {
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion edg0 =  textureAtlas.findRegion("hexedgemove0");
    static TextureRegion edg1 =  textureAtlas.findRegion("hexedgemove1");
    static TextureRegion edg2 =  textureAtlas.findRegion("hexedgemove2");
    static TextureRegion edg3 =  textureAtlas.findRegion("hexedgemove3");
    static TextureRegion edg4 =  textureAtlas.findRegion("hexedgemove4");
    static TextureRegion edg5 =  textureAtlas.findRegion("hexedgemove5");
    ArrayList<Hex> arrHex = new ArrayList<>();
    ArrayList<HexBorders> arrHexWithBorders = new ArrayList<>();
    ArrayList<Image> arrImages = new ArrayList<>();
    HexHiliteEdge(ArrayList<Hex> arrIn){
        arrHex.addAll(arrIn);
        arrHexWithBorders = new ArrayList<>();
        createBorders();
    }
    public void createBorderImage(Hex hex){
            for (HexBorders hexBorders:arrHexWithBorders){
                if (hex == hexBorders.hex){
                    display(hexBorders);
                    return;
                }
            }
    }

    private void display(HexBorders hexBorders) {
        Vector2 pos = hexBorders.hex.getCounterPosition();
        pos.x -= 48;
        pos.y -= 22; //16
        for (int imgNum:hexBorders.arrInts){
            Image image = null;
            switch (imgNum){
                case 0:
                    image = new Image(edg0);
                    break;
                case 1:
                    image = new Image(edg1);
                    break;
                case 2:
                    image = new Image(edg2);
                    break;
                case 3:
                    image = new Image(edg3);
                    break;
                case 4:
                    image = new Image(edg4);
                    break;
                case 5:
                    image = new Image(edg5);
                    break;
            }
            image.setPosition(pos.x,pos.y);
            image.addAction(Actions.fadeOut(.01f));
            image.addAction(Actions.fadeIn(.5f/hexBorders.hex.getCalcMoveCost(0)));
            ardenne.instance.hexStage.addActor(image);
            arrImages.add(image);
        }
       /**
         *  see HexHilteDisplay for positioning  and actions
         */

    }
    public void remove(){
        for (Image image:arrImages){
            image.remove();
        }
    }



    private void createBorders() {

        for (Hex hexCheck:arrHex){
            if (hexCheck.xTable == 27 && hexCheck.yTable == 6){
                int brk=0;
            }
            Hex[] hexTab =HexHandler.getSurround(hexCheck);
            for (int i=0; i< 6; i++){
                if (hexTab[i] == null){
                    addBorder(hexCheck,i);
                }else{
                    if (!arrHex.contains(hexTab[i])){
                        addBorder(hexCheck,i);
                    }
                }
            }

        }
    }
    private void addBorder(Hex hex, int border){
        for (HexBorders hexBorders:arrHexWithBorders){
            if (hexBorders.hex == hex){
                hexBorders.addBorder(border);
                return;
            }
        }
        HexBorders hexBorders = new HexBorders(hex,border);
        arrHexWithBorders.add(hexBorders);
        return;
    }

    class HexBorders{
        Hex hex;
        ArrayList<Integer> arrInts = new ArrayList<>();
        HexBorders(Hex hex, int border){
            this.hex = hex;
            arrInts.add(border);
        }
        void addBorder(int border){
            arrInts.add(border);
        }

    }

}
