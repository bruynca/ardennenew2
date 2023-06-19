package com.bruinbeargames.ardenne.GameLogic;


import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.WinModal;

import java.util.ArrayList;

public class LimberArtillery {
    static public LimberArtillery instance;
    private I18NBundle i18NBundle;
    boolean isAllies;
    boolean isAI;


    public LimberArtillery(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

    }
    public void initializeLimber(boolean isAllies, boolean isAI){
        this.isAllies = isAllies;
        this.isAI = isAI;
        ArrayList<Unit> arrUnitWork;
        if (isAllies) {
            arrUnitWork = Unit.getOnBoardAllied();
        } else {
            arrUnitWork = Unit.getOnBoardAxis();
        }
        ArrayList<Unit> arrUnitToLimber = new ArrayList<>();
        for (Unit unit:arrUnitWork){
            unit.getMapCounter().getCounterStack().shade();
            if (unit.isArtillery){
                arrUnitToLimber.add(unit);
            }
        }
        if (arrUnitToLimber.size() >0 ){
            if (isAllies) {
                TurnCounter.instance.updateText(i18NBundle.get("aartilleryphase"));
            }else{
                TurnCounter.instance.updateText(i18NBundle.get("gartilleryphase"));
            }
        }else{
            if (!isAllies) {
                NextPhase.instance.nextPhase();
                return;
            }else{
                NextPhase.instance.nextPhase();
                return;

            }
        }
        WinModal.instance.set();
        scheduleLimberHilite(arrUnitToLimber);

  //      for (Unit unit : arrUnitToLimber) {
  //      }
        /**
         * hilites hexes with sound
         */




    }

    private void scheduleLimberHilite(final ArrayList<Unit> arrUnitToLimber) {
        final Unit unitWork = arrUnitToLimber.get(0);
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               SoundsLoader.instance.playLimber();
                               unitWork.getMapCounter().getCounterStack().removeShade();
                               unitWork.getHexOccupy().moveUnitToFront(unitWork);
                               Counter.rePlace(unitWork.getHexOccupy());
                               ClickAction clickAction = new ClickAction(unitWork, ClickAction.TypeAction.Limber);
                               arrUnitToLimber.remove(unitWork);
                               if (arrUnitToLimber.size() == 0 ){
                                   WinModal.instance.release();
                                   return;
                               }else{
                                   scheduleLimberHilite(arrUnitToLimber);
                               }
                           }
                       }
                , .16F        //    (delay)
        );


    }
}
