package com.bruinbeargames.ardenne;

import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class WinModal {
    Window window;
    Window window2;
    boolean isWindowSet = false;
    static public WinModal instance;
    public  WinModal(){
        instance = this;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = FontFactory.instance.largeFont;
        window = new Window("",windowStyle);
        window.setModal(true);
        window2 = new Window("",windowStyle);
        window2.setModal(true);

    }
    public void set(){
        if (!isWindowSet) {
            ardenne.instance.guiStage.addActor(window2);
            ardenne.instance.mapStage.addActor(window);
            isWindowSet = true;
        }
    }
    public void release(){
        if (isWindowSet) {
            window.remove();
            window2.remove();
            isWindowSet = false;
        }

    }
}