package com.bruinbeargames.ardenne.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexSurround;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class Counter {
    public static final int size =130;
    public static final int sizeOnMap = 100;
    TypeCounter type;
    Unit unit;
    public Stack stack;
    CounterStack counterStack;
    CounterExplode counterExplode;
    CounterDescription counterDesc;
    ClickAction clickAction = null;
    Image imgClickAction;
    HiliteHex hiliteHexArtillery;


    static ArrayList<Stack> arrStacks = new ArrayList<>();


    final static  int counterStackAdjust = 4;
    public Counter(Unit unit, TypeCounter type){
        this.unit = unit;
        this.type = type;
        stack = new Stack();
        counterStack = new CounterStack(unit,stack);
        if (type == TypeCounter.MapCounter)
        {
            addListnersStack(this);
        }
   }

    public void addToStage(Stage stage){
        ardenne.instance.mapStage.addActor(stack);
        arrStacks.add(stack);
  //      for (Actor actor:counterStack.arrActors){
  //          stage.addActor(actor);
  //      }
    }


    public void place(Hex hexPlace) {
        Vector2 v2 = hexPlace.getCounterPosition();
        int adjust = hexPlace.getUnitsInHex().size() * counterStackAdjust;
        v2.x -= adjust;
        v2.y += adjust;
        stack.setPosition(v2.x, v2.y);
        addToStage(ardenne.instance.mapStage);
        rePlace(hexPlace);
    }

    private void addListnersStack(Counter counterIn) {
//        Gdx.app.log("Counter ", "add Listner unit="+counterIn.unit);
        final Counter counter = counterIn;
        stack.addListener(new ClickListener() {

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("Counter ", "enter unit="+unit);
                checkExplode(counter);
                checkDesc(counter);
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("Counter", "exit unit="+unit);
                checkImplode();
                checkDescGone();
            }
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
  //              Gdx.app.log("Counter","TouchDown unit="+unit);
                if (event.getButton( ) == Input.Buttons.RIGHT)
                {
                    Gdx.app.log("Counter","Right");
                    cycleUnits();
                    if (NextPhase.instance.getPhase() == Phase.ALLIED_COMBAT.ordinal() ||
                        NextPhase.instance.getPhase() == Phase.GERMAN_COMBAT.ordinal()){
                        if (Combat.instance.isAttackArrows()) {
                            Combat.instance.refreahAttackCancel();
                        }
                    }
                }else  if (event.getButton( ) == Input.Buttons.LEFT){
                    if (unit.isArtillery && NextPhase.instance.isArtillery()){
                        ArrayList<Hex> arrHex =  HexSurround.GetSurroundMapArr(unit.getHexOccupy(),unit.getRange());
                        hiliteHexArtillery = new HiliteHex(arrHex, HiliteHex.TypeHilite.Range, null);
                    }

                }
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
   //             Gdx.app.log("Counter","TouchUp");
                if (event.getButton( ) == Input.Buttons.LEFT)
                {
   //                 Gdx.app.log("Counter","Left TouchUp unit="+unit);
                    if (clickAction != null){
                        clickAction.click();
                    }else{
   //                     Gdx.app.log("Counter","Left TouchUp null unit="+unit);
                    }
                    if (unit.isArtillery && NextPhase.instance.isArtillery()){
                        hiliteHexArtillery.remove();
                    }
                }
            }

      });
    }
    public void checkExplode(Counter counter){
        if (counterExplode == null) {
            counterExplode = new CounterExplode(counter);
        }

    }
    public void checkImplode(){
        if (counterExplode != null){
            counterExplode.end();
            counterExplode = null;
        }

    }
    public void checkDesc(Counter counter){
        if (counterDesc == null) {
            counterDesc = new CounterDescription(counter);
        }

    }
    public void checkDescGone(){
        if (counterDesc != null){
            counterDesc.end();
            counterDesc = null;
        }

    }
    public void cycleUnits(){
        unit.getHexOccupy().cycleUnits();
        Counter.rePlace(unit.getHexOccupy());
    }

    public void remove() {
        stack.remove();
    }

    public void addClickAction(ClickAction clickAction){
        this.clickAction = clickAction;
    }
    public void removeClickAction(){
 //       Gdx.app.log("Counter","removeClickAction="+unit);
        clickAction = null;
    }
    public ClickAction getClickAction(){
        return clickAction;
    }

    public void updateImage() {
    }


    static public void   rePlace(Hex hex) {

        Vector2 pos = hex.getCounterPosition();
        /**
         * remove first seems to be bug if you remove and then put back in drawing is incorrect
         */
        for (Unit unit:hex.getUnitsInHex()){
            if (unit.getMapCounter() != null) {
                unit.getMapCounter().stack.remove();
            }
        }
        /**
         *  go backward so that the first unit in array will be drawn last
         *  and so will be on top
         */
        for (int i = hex.getUnitsInHex().size()-1; i >= 0; i--)
        {
            Unit unitCheck =  hex.getUnitsInHex().get(i);
            if (unitCheck.getMapCounter() != null) {
                Counter counter = unitCheck.getMapCounter();
                //           counter.place(hex);
                pos.x -= counterStackAdjust;
                pos.y += counterStackAdjust;
                counter.stack.setPosition(pos.x, pos.y);
                counter.addToStage(ardenne.instance.mapStage);
                pos.x -= counterStackAdjust;
                pos.y += counterStackAdjust;
            }
        }

    }
    public CounterStack getCounterStack(){
        return counterStack;
    }

    public Unit getUnit() {
        return unit;
    }


    public enum TypeCounter {MapCounter, GUICounter};
}
