package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.MouseImage;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

/**
 *  put hilite display on the map
 */
public class HexHiliteDisplay {
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion backHilite =  textureAtlas.findRegion("hilitehex");
    static TextureRegion backHiliteReinDisplay =  textureAtlas.findRegion("hilitehexreindisplay");
    static TextureRegion backHiliteCannonRange =  textureAtlas.findRegion("hilitehexcannonrange");
    static TextureRegion backHiliteMove =  textureAtlas.findRegion("hilitehexmove3");
    static TextureRegion backHiliteExit =  textureAtlas.findRegion("hilitehexexit");
    static ArrayList<HexHiliteDisplay> arrHexHilite = new ArrayList<>();
    static Label.LabelStyle labelStyleName
            = new Label.LabelStyle(FontFactory.instance.yellowFont, Color.YELLOW);

    Image image;
    Label label;
    Label label2;
    HiliteHex.TypeHilite type;
    static ArrayList<Unit> arrUnitsToShade = new ArrayList<>();
    Hex hex;
    public HexHiliteDisplay(Hex hex, HiliteHex.TypeHilite typeIn){
        Vector2 pos = hex.getCounterPosition();
       type = typeIn;
       this.hex = hex;
      if (pos == null){
            int brk = 0;
        }
        String str = Float.toString(hex.getAiScore())+" x"+hex.xTable+" y"+hex.yTable;
        if (hex.isAxisOccupied()){
            str +="G";
        }
        if (hex.isAlliedOccupied()){
            str +="A";
        }
        String str2 = Float.toString(hex.getCalcMoveCost(0));

        label = new Label(str,labelStyleName);
        label.setPosition(pos.x-20, pos.y+40);
        label2 = new Label(str2,labelStyleName) ;
        label2.setPosition(pos.x, pos.y+50);


        pos.x -= 48;  //-48
        pos.y -= 18; //22
        float xdiv = hex.xTable;
 //       float adjustx = (32/xdiv) * .3125f;
        if (xdiv < 10) {
            pos.x -= 10;
        }else if (xdiv < 20){
            pos.x -=6;
        }else if (xdiv < 30){
        pos.x -=3;
        }

        if (type == HiliteHex.TypeHilite.ReinforceDisplay){
            image = new Image(backHiliteReinDisplay);
        }else if (type == HiliteHex.TypeHilite.Range) {
            image = new Image(backHiliteCannonRange);
        }
        else if (type == HiliteHex.TypeHilite.Hilite){
                image = new Image(backHilite);
        }else if (type == HiliteHex.TypeHilite.MoveExit) {
            if (SecondPanzerExits.instance.isInExit(hex)) {
                image = new Image(backHiliteExit);
            } else {
                type = HiliteHex.TypeHilite.Move;
                image = new Image(backHiliteMove);
            }
        }else if (type == HiliteHex.TypeHilite.Move){
            image = new Image(backHiliteMove);
        }else if (type != HiliteHex.TypeHilite.Debug) {
            image = new Image(backHiliteMove);
        }else if (type == HiliteHex.TypeHilite.Debug) {
            image = null;
        }else if (type != HiliteHex.TypeHilite.AI) {
            image = new Image(backHiliteCannonRange);
        }else if (type != HiliteHex.TypeHilite.ShowSupply) {
            image = new Image(backHiliteCannonRange);

        }
        if (image != null) {
            image.setTouchable(Touchable.enabled);

            image.setPosition(pos.x, pos.y);
            if (typeIn == HiliteHex.TypeHilite.Supply) {
                addListner();
            }
        }

        if (typeIn == HiliteHex.TypeHilite.Move){
            image.addAction(Actions.fadeOut(.01f));
            image.addAction(Actions.fadeIn(.5f/(hex.getCalcMoveCost(0)+1)));
        }
        if (image != null) {
            ardenne.instance.hexStage.addActor(image);
        }
        if (typeIn == HiliteHex.TypeHilite.Debug) {

            ardenne.instance.hexStage.addActor(label);
        }
        if (typeIn == HiliteHex.TypeHilite.Move) {
            if (ardenne.instance.getisShowMovepoints()) {

                ardenne.instance.hexStage.addActor(label2);
            }
        }

        arrHexHilite.add(this);
    }

    private void addListner() {
        image.addListener(new ClickListener() {

         public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
           MouseImage.instance.setMouseGerman();
           ArrayList<Hex> arrHex = Supply.instance.getunitsInRadius(hex);
             for (Unit unit:arrUnitsToShade){
                 if (!unit.getInSupplyThisTurn()) {
                     unit.getMapCounter().getCounterStack().shade();
                     if (unit.isExit){
                         Hex hex=unit.getHexOccupy();
                         SecondPanzerExits.instance.shade(hex);

                     }
                 }
             }
           for (Hex hex2:arrHex){
             for (Unit unit:hex2.getUnitsInHex()){
                 if (unit.isAxis){
                     if (unit.getMapCounter() != null){
                         if (unit.isExit){
                             Hex hex=unit.getHexOccupy();
                             SecondPanzerExits.instance.removeShade(hex);
                             arrUnitsToShade.add(unit);
                         }
                         if (unit.getMapCounter().getCounterStack().isShaded()){
                             arrUnitsToShade.add(unit);
                             unit.getMapCounter().getCounterStack().removeShade();
                         }
                     }
                 }
             }
           }
        }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                MouseImage.instance.mouseImageReset();
                for (Unit unit:arrUnitsToShade){
                    if (!unit.getInSupplyThisTurn()) {
                        unit.getMapCounter().getCounterStack().shade();
                        if (unit.isExit){
                            SecondPanzerExits.instance.shade(unit.getHexOccupy());
                        }

                    }
                }
                arrUnitsToShade.clear();
            }
    });
   }

    static void removeHexHilite(){
        for (HexHiliteDisplay hexHilite: arrHexHilite){
            if (hexHilite.image != null) {
                hexHilite.image.remove();
            }
            hexHilite.label.remove();
            hexHilite.label2.remove();
            if (hexHilite.type == HiliteHex.TypeHilite.Supply){
                MouseImage.instance.mouseImageReset();
            }
        }
        for (Unit unit:arrUnitsToShade){
            if (!unit.getInSupplyThisTurn()) {
                unit.getMapCounter().getCounterStack().shade();
            }
        }
        arrHexHilite.clear();
    }

}
