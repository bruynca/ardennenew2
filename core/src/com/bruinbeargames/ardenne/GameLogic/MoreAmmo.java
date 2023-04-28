package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventOK;

import java.util.Observable;
import java.util.Observer;

/**
 *  handle message from Event OK
 */

public class MoreAmmo implements Observer {
    boolean isAllies;
    public static MoreAmmo instance;
    public MoreAmmo(){
        instance = this;
    }
    public void display(boolean isAllies){
        this.isAllies = isAllies;
        EventOK.instance.addObserver(this); // message for moreAmmo

    }

    @Override
    public void update(Observable observable, Object o) {
        if (((ObserverPackage) o).type == ObserverPackage.Type.OK) {
            EventOK.instance.deleteObserver(this);
            if (isAllies) {
                CardHandler.instance.alliedCardPhase(NextPhase.instance.getTurn());
            } else {
                CardHandler.instance.germanCardPhase(NextPhase.instance.getTurn());
            }
        }
    }
}
