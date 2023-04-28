package com.bruinbeargames.ardenne;

import java.awt.Toolkit;

public class SoundEffects {
    static public SoundEffects instance;
    SoundEffects(){
        instance = this;
    }
    public static void WinAsterisk()
    {
        Runnable sound2 =
                (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.asterisk");
        if(sound2 != null) sound2.run();
//	         Toolkit.getDefaultToolkit().beep();

    }
    public void WinExit()
    {
        Runnable sound2 =
                (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exit");
        if(sound2 != null) sound2.run();
//	         Toolkit.getDefaultToolkit().beep();

    }
    public static void WinMenuPopup()
    {
        Runnable sound2 =   (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.menuPopup");
        if(sound2 != null) sound2.run();
//	         Toolkit.getDefaultToolkit().beep();

    }
}
