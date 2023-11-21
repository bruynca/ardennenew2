package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.UI.EventConfirm;

import java.util.Observable;
import java.util.Observer;


/**
 *  add a opoup to determine if the user wants to exit the game
 */

/**
 *  NOT TO BE USED
 */
public class CatchBackButton implements Observer {
    I18NBundle i18NBundle;
    public  CatchBackButton(){
        i18NBundle = GameMenuLoader.instance.localization;
        EventConfirm.instance.addObserver((java.util.Observer) this);
        EventConfirm.instance.show(i18NBundle.format("confirmexit"));
    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        /**
         *  if yes then end game
         */
        if (((ObserverPackage) o).type == ObserverPackage.Type.ConfirmYes) {
            EventConfirm.instance.deleteObserver((java.util.Observer) this);
            ardenne.instance.setIsResumed(false);
            ardenne.instance = null;
            Gdx.app.exit();
        }
        if (((ObserverPackage) o).type == ObserverPackage.Type.ConfirmNo) {
            EventConfirm.instance.deleteObserver((java.util.Observer) this);
        }
    }
}
