package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameLogic.MobileAssualt;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.CombatDisplayResults;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.WinReinforcements;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;
import com.bruinbeargames.ardenne.WinModal;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AIExecute {
    static public AIExecute instance;
    private I18NBundle i18NBundle;

    AIExecute() {
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;
    }

    /**
     * passed aiOrders move them for the AI
     *
     * @param aiOrders
     */

    public void moveAndMOA(AIOrders aiOrders) {

        AIExMove aiExMove = new AIExMove(aiOrders);

    }


    public void Combat(ArrayList<AIOrdersCombat> arrToBeScored) {
        AIExCombat aiExCombat = new AIExCombat(arrToBeScored);

    }
    public void Reinforcement(AIOrders aiOrders) {
        AIExReinforcement aiExReinforcement = new AIExReinforcement(aiOrders);

    }
    class AIExReinforcement implements Observer{
        AIOrders aiOrders;
        boolean isAllies;
        boolean isCenterOnDestinationHex = false;
        boolean isProcessLocked = false;
        Unit unitCurrent;
        Hex hexCurrent;
        Hex hexDestination;
        ClickAction clickAction;
        int index = 0;
        ArrayList<Unit> arrUnits = new ArrayList<>();
        ArrayList<Hex> arrHexes = new ArrayList<>();
        ArrayList<Hex> arrMOA = new ArrayList<>();
        AIExReinforcement aiExReinforcement;
        WinModal winModal;
        WinReinforcements winReinforcements;
        Object obj;
        boolean isMOA = false;
        //region
        public AIExReinforcement(AIOrders aiOrders)  {
            /**
             *
             */
            this.aiOrders = aiOrders;
            obj = this;
            isMOA = false;
            aiExReinforcement = this;
            arrUnits.addAll(aiOrders.arrUnit);
            arrHexes.addAll(aiOrders.arrHexMoveTo);
            isAllies = true;
            WinModal.instance.set();
            winReinforcements = Reinforcement.instance.getScreen();
            EventAI.instance.hide();
            EventAI.instance.show(i18NBundle.format("aireinmove"));
            winReinforcements.fadeWindow();
            doNextReinforcements();

        }

        private void doNextReinforcements() {
            Gdx.app.log("AIExReinforcement", "Do Next arrHex=" + arrUnits);

            if (arrHexes.size() == 0){
                WinModal.instance.release();
                EventAI.instance.hide();
                if (winReinforcements.isWindowStillActive()){
                    winReinforcements.end();
                }
                return;
            }
            hexCurrent = arrHexes.get(0);
            unitCurrent = arrUnits.get(0);
            hexDestination = arrHexes.get(arrHexes.size() -1);
            arrHexes.remove(0);
            arrUnits.remove(0);
            CenterScreen.instance.addObserver(this);
            isCenterOnDestinationHex = false;
            CenterScreen.instance.start(hexCurrent);
            return;
        }
        @Override
        public void update(Observable observable, Object arg) {
            ObserverPackage oB = (ObserverPackage) arg;

            /**
             *  if yes kick off processing for that type
             */
            if (((ObserverPackage) arg).type == ObserverPackage.Type.ScreenCentered) {
 //               CenterScreen.instance.deleteObserver(this);
                if (!isCenterOnDestinationHex) {
                    Gdx.app.log("AIExReinforcement", "Do Next arrHex=" + arrUnits);
                    Counter counter = winReinforcements.getCounter(unitCurrent);
                    winReinforcements.hitUnit(unitCurrent, counter, null);
                    /**
                     *  Center screen gets fired by a lot of process in program
                     *  make sure we dont start another unit until one is complete
                     */
                    isCenterOnDestinationHex = true;
                    isProcessLocked = true;
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           winReinforcements.doMove(hexCurrent, null);
                                           isProcessLocked = false;
                                           CenterScreen.instance.start(hexCurrent);
                                       }
                                   }
                            , 2.0f                    //    (delay)
                    );
                    return;
                }else{
                    if (!isProcessLocked) {
                        CenterScreen.instance.deleteObserver(this);
                        Timer.schedule(new Timer.Task() {
                                           @Override
                                           public void run() {
                                               doNextReinforcements();
                                           }
                                       }
                                , 1.0f                    //    (delay)
                        );

                    }
                }
            }
            return;
        }

    }






    class AIExCombat implements Observer {
        ArrayList<AIOrdersCombat> aiOrdersCombats = new ArrayList<>();
        boolean isAllies;
        AIExCombat aiExCombat;
        WinModal winModal;
        Hex hexAttack;
        ArrayList<Unit> arrUnitsOnAttack = new ArrayList<>();
        AIOrdersCombat aicWorkingOn;
        Attack attack;


        /**
         * contructor
         *
         * @param arrIn ArrayList Of AOIORDERS Combat
         */
        AIExCombat(ArrayList<AIOrdersCombat> arrIn) {
            aiExCombat = this;
            aiOrdersCombats.addAll(arrIn);
            if (aiOrdersCombats.get(0).getUnits().get(0).isAllies) {
                isAllies = true;
            } else {
                isAllies = false;
            }
            WinModal.instance.set();
            EventAI.instance.hide();
            EventAI.instance.show(i18NBundle.format("aicombating"));
            Timer.schedule(new Timer.Task() {
                               @Override
                               public void run() {
                                   EventAI.instance.hide();
                               }
                           }
                    , 1.5f                    //    (delay)
            );
            doNextCombat();


        }

        private void doNextCombat() {
            Gdx.app.log("AtExecute", "doNextCombat");

            if (aiOrdersCombats.size() == 0) {
                EventAI.instance.hide();
                WinModal.instance.release();
                NextPhase.instance.nextPhase();
                return;
            }
            aicWorkingOn = aiOrdersCombats.get(0);
            aiOrdersCombats.remove(0);
            hexAttack = aicWorkingOn.hexAttackedByUnits.getHex();
            arrUnitsOnAttack.clear();
            arrUnitsOnAttack.addAll(aicWorkingOn.getUnits());
            /**
             *  bombard was previous may have wiped out attack
             */
            int cntAttack = 0;
            for (Unit unit:arrUnitsOnAttack){
                cntAttack += unit.getCurrenAttackFactor();
            }
            if (cntAttack == 0){
                doNextCombat();
                return;
            }
            Gdx.app.log("AIExCombat ", "doNextCombat arrUnits=" + arrUnitsOnAttack);

            Combat.instance.createCombatImage(hexAttack,false);
            CenterScreen.instance.addObserver(this);
            CenterScreen.instance.start(hexAttack);

        }

        @Override
        public void update(Observable observable, Object arg) {
            ObserverPackage oB = (ObserverPackage) arg;

            /**
             *  if yes kick off processing for that type after screen has centered
             */
            if (((ObserverPackage) arg).type == ObserverPackage.Type.ScreenCentered) {
                CenterScreen.instance.deleteObserver(this);
                attack = new Attack(hexAttack, isAllies, false, true, null);
                Combat.instance.createAttackDisplay(hexAttack);
                Combat.instance.setAttackForAI(attack);
                Gdx.app.log("AIExCombat ", "update arrUnits=" + arrUnitsOnAttack);

                for (Unit unit : arrUnitsOnAttack) {
                    unit.getMapCounter().getCounterStack().hilite();
                    Combat.instance.addUnit(unit);
                }
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       attack.addObserver(aiExCombat);
                                       Combat.instance.cleanup(true);
                                       attack.dieRoll();
                                   }
                               }
                        , 1.75f                    //    (delay)
                );
                return;
                /**
                 *  no advance after  for now
                 */
            } else if (((ObserverPackage) arg).type == ObserverPackage.Type.Advance) {
                attack.deleteObserver(aiExCombat);
                ClickAction.cancelAll();
                doNextCombat();
            }else if (((ObserverPackage) arg).type == ObserverPackage.Type.AfterAttackDisplay) {
                attack.deleteObserver(aiExCombat);
                Combat.instance.cleanup(true);
                doNextCombat();
            }
        }
    }



    class AIExMove implements Observer {
        AIOrders aiOrders;
        boolean isAllies;
        Unit unitCurrent;
        Hex hexCurrent;
        Hex hexMOA;
        ClickAction clickAction;
        int index = 0;
        ArrayList<Unit> arrUnits = new ArrayList<>();
        ArrayList<Hex> arrHexes = new ArrayList<>();
        ArrayList<Hex> arrMOA = new ArrayList<>();
        AIExMove aiExMove;
        WinModal winModal;
        boolean isMOA = false;
        //region
        public AIExMove(AIOrders aiOrders) {
            /**
             *
             */
            this.aiOrders = aiOrders;
            isMOA = false;
            aiExMove = this;
            arrUnits.addAll(aiOrders.arrUnit);
            arrHexes.addAll(aiOrders.arrHexMoveTo);
            arrMOA.addAll(aiOrders.arrHexMobileAssault);
            if (aiOrders.arrUnit.get(0).isAllies) {
                isAllies = true;
            } else {
                isAllies = false;
            }
            WinModal.instance.set();
            EventAI.instance.hide();
            EventAI.instance.show(i18NBundle.format("aimoving"));
            doNextMove();

        }
        //endregion
        private void doNextMove() {
            /**
             * check if isMOA set
             *  if yes then MOA finished
             *  we need to check if succeeded and if the move to after
             *  MOA is available
             */
            if (isMOA && !unitCurrent.isEliminated()) { // previous was a mobile assualt
                Hex hexNew = checkCanGoMOA(hexMOA);
                if (hexNew != null) {  // we have a solution
                    hexCurrent = hexNew;
                    isMOA = false;
                    clickAction = new ClickAction(unitCurrent, ClickAction.TypeAction.Move);
                    CenterScreen.instance.addObserver(this);
                    CenterScreen.instance.start(unitCurrent.getHexOccupy());
                    return;
                }

            }
            if (arrUnits.size() == 0) {
                EventAI.instance.hide();
                WinModal.instance.release();
                NextPhase.instance.nextPhase();
                return;
            }

            unitCurrent = arrUnits.get(0);
            hexCurrent = arrHexes.get(0);
            if (arrMOA.get(0) != null) {
                hexMOA = arrMOA.get(0);
            }else{
                hexMOA = null;
            }
            arrUnits.remove(0);
            arrHexes.remove(0);
            arrMOA.remove(0);
            /**
             *  Check if MOA
             */
            if (hexMOA != null && hexMOA != hexCurrent) {
                isMOA = true;
                ArrayList<Hex> arrHexes = new ArrayList<>();
                arrHexes.add(hexCurrent);
                MobileAssualt.instance.doMobileInitialAssualtSetUp(arrHexes, unitCurrent, false);
                CenterScreen.instance.addObserver(this);
                CenterScreen.instance.start(hexCurrent);
                CombatDisplayResults.instance.addObserver(this);
                return;
            }

            isMOA = false;
            clickAction = new ClickAction(unitCurrent, ClickAction.TypeAction.Move);
            CenterScreen.instance.addObserver(this);
            CenterScreen.instance.start(unitCurrent.getHexOccupy());
            Gdx.app.log("AIExecute", "doNextMove Unit=" + unitCurrent + " To Hex" + hexCurrent);
        }

        /**
         * CHECK IF WE CAN GOT hexMOA
         * if not find another solution
         * if no solution return null;
         *
         * @param hexMOA
         * @return
         */
        private Hex checkCanGoMOA(Hex hexMOA) {
            UnitMove unitMove = new UnitMove(unitCurrent, unitCurrent.getCurrentMovement(), false, true, 0);
            ArrayList<Hex> arrDest = unitMove.getMovePossible();
            if (arrDest.contains(hexMOA)) {
                return hexMOA;
            }
            /**
             *  simple for now
             */
            for (Hex hex : hexMOA.getSurround()) {
                if (arrDest.contains(hex)) {
                    return hex;
                }

            }
            return null;
        }

        @Override
        public void update(Observable observable, Object arg) {
            ObserverPackage oB = (ObserverPackage) arg;

            /**
             *  if yes kick off processing for that type
             */
            if (((ObserverPackage) arg).type == ObserverPackage.Type.ScreenCentered) {
                CenterScreen.instance.deleteObserver(this);
                if (!isMOA) {
                    clickAction.moveSetup(unitCurrent);
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           Move.instance.addObserver(aiExMove);
                                           clickAction.process(hexCurrent, true);
                                       }
                                   }
                            , .5f                    //    (delay)
                    );
                } else {
                    MobileAssualt.instance.hit(hexCurrent, null, true);
                    Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           MobileAssualt.instance.endMOA();
                                       }
                                   }
                            , .75f                    //    (delay)
                    );
                }
                return;
            } else if (((ObserverPackage) arg).type == ObserverPackage.Type.MoveFinished) {
                Move.instance.deleteObserver(aiExMove);
                MobileAssualt.instance.endMOA();
                doNextMove();
                return;
            } else if (((ObserverPackage) arg).type == ObserverPackage.Type.CombatDisplayResults) {
                CombatDisplayResults.instance.deleteObserver(this);
                Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       CombatDisplayResults.instance.hide();
                                       Attack.instance.afterDisplay(this);
                                       doNextMove();
                                   }
                               }
                        , .75f                    //    (delay)
                );

                return;
            }


        }
    }
}


