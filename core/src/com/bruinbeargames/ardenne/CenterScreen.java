package com.bruinbeargames.ardenne;


import com.badlogic.gdx.math.Vector2;
import com.bruinbeargames.ardenne.Hex.Hex;

import java.util.Observable;

public class CenterScreen extends Observable {

    static public CenterScreen instance;


    private boolean timerStarted = false;
    private float x;
    private float y;
    private Hex hexGoTo;
    private Vector2 v2GoTo;
    boolean isScrolling = false;
    private final int pixelsToScroll = 16;
    boolean xScrollDone = false;
    boolean yScrollDone = false;
    boolean isStartOfScroll = false;
    boolean isXDeltaNegative = true;
    boolean isYDeltaNegative = true;
    WinModal winModal;

    public CenterScreen() {
        instance = this;
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    public void update() {
            float startX= ScreenGame.instance.cameraBackGround.position.x;
            float startY= ScreenGame.instance.cameraBackGround.position.y;
            float xDelta = v2GoTo.x - startX;
            float yDelta = v2GoTo.y - startY;
        /**
         *  check for the jitters
         */
        if (isStartOfScroll){
                if (xDelta < 0){
                    isXDeltaNegative = true;
                }else {
                    isXDeltaNegative = false;
                }
                if (yDelta < 0){
                    isYDeltaNegative = true;
                }else{
                    isYDeltaNegative = false;
                }
                isStartOfScroll = false;
            }else{
                if (isXDeltaNegative && xDelta >= 0){
                    xScrollDone = true;
                }
                if (!isXDeltaNegative && xDelta < 0){
                    xScrollDone = true;
                }
                if (isYDeltaNegative && yDelta >= 0){
                    yScrollDone = true;
                }
                if (!isYDeltaNegative && yDelta < 0){
                    yScrollDone = true;
                }

            }
            int xToScroll = (int)xDelta;
            int yToScroll = (int) (yDelta);
            int xScroll, yScroll;
            if (Math.abs(xToScroll) <= pixelsToScroll)
            {
                xScroll = 0;
                xScrollDone = true;
            }
            else if (xToScroll > pixelsToScroll)
            {
                xScroll = -pixelsToScroll;
            }
            else
            {
                xScroll = pixelsToScroll;
            }
            if (Math.abs(yToScroll) <=  pixelsToScroll)
            {
                yScroll = 0;
                yScrollDone = true;
            }
            else if (yToScroll > pixelsToScroll)
            {
                yScroll = pixelsToScroll;
            }
            else
            {
                yScroll = -pixelsToScroll;
            }
        /**
         * results 0 = if true x stopped     1 = if true y stopped
         */
        boolean[] results = ScreenGame.instance.panCamera(xScroll,yScroll);
        if ((xScrollDone || results[0]) &&(yScrollDone || results[1])){
            // done
            timerStarted = false;
            ardenne.instance.isScroll = false;
            isScrolling = false;
            WinModal.instance.release();
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.ScreenCentered,null,0,0));
       }

    }

    public void start(Hex hex) {
        hexGoTo = hex;
        timerStarted = true;
        v2GoTo = hex.GetDisplayCoord();
        isScrolling = true;
        xScrollDone = false;
        yScrollDone = false;
        timerStarted = true;
        isStartOfScroll = true;
        ardenne.instance.isScroll = true;
        WinModal.instance.set();

    }
}

