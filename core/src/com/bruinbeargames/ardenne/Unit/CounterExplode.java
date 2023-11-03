package com.bruinbeargames.ardenne.Unit;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Map;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

/**
 *  create a display of the counter stack 
 */
public class CounterExplode {
    Counter counter;
    ArrayList<Stack> arrCounters = new ArrayList<>();
    static final int size =80;

    boolean isCancel = false;
    CounterExplode(Counter counter){
        Gdx.app.log("CounterExplode ", "enter for All");

        this.counter = counter;


        if  (counter.unit.getHexOccupy().getUnitsInHex().size() > 1) {
            final Hex hex = counter.unit.getHexOccupy();
            Timer.schedule(new Timer.Task() {
                       @Override
                       public void run() {
                           showStack(hex);
                       }
                   }, .8F
            );
            final Counter checkCounter = counter;
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                            checkCounter.checkImplode();
                               }
                           }, 6F
            );


        }
    }

    private void showStack(Hex hex) {
        if (isCancel){
            return;
        }
        Vector2 v2 = Map.ConvertToScreen(hex);
        int y = Gdx.graphics.getHeight();
        int x = Gdx.graphics.getWidth();
        for (Unit unit:hex.getUnitsInHex()){
            Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
            counter.stack.setTransform(true);
            float ratio =(float) size/Counter.size;
            counter.stack.setScale(ratio);
  //          counter.stack.setSize(size,size);
            arrCounters.add(counter.stack);

        }
        int length = arrCounters.size() * size + size;
        /**
         * left or right display
         */
        float startX = v2.x + size;

        if (length + v2.x > x){
            startX -= length+10;
        }
        float startY = v2.y-size;
        Gdx.app.log("CounterExplode", "y="+startY);
        if (startY < 10){
            startY = 10;
        }
        if (startY + size > y){
            startY -= size;
        }
        Gdx.app.log("CounterExplode", "y="+startY);
        for (Stack stack:arrCounters){
            stack.setPosition(startX,startY);
            ardenne.instance.guiStage.addActor(stack);
            startX +=size;
        }
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            Gdx.app.log("CounterExplode ", "enter for android");

            counter.cycleUnits();
            ardenne.setIsStopPan(true);
            //fullScreen.remove();
            //initializeFullScreenCheckBox();
        }


    }

    public void end() {
        isCancel = true;
        for (Stack stack:arrCounters){
            stack.remove();
        }
        arrCounters.clear();
        ardenne.setIsStopPan(false);
    }
}
