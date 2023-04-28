package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.EventOK;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
// todo add logic to do actual limber
public class AILimber implements Observer {
    /**
     *  do the AI to limber or unlimber artillery
     */
    static public AILimber instance;
    ArrayList<Unit> arrArtillery = new ArrayList<>();
    ArrayList<Hex>[] arrMaxMove; // longest distance for each unit
    /**
     * AIOrders sorted in highest score  bombard
     */
    ArrayList<AIOrders> arrAIBasicBombard = new ArrayList<>(); // aiorders best bombard
    ArrayList<AIOrders> arrAIBasicMoveTo = new ArrayList<>(); // aiorders move to
    ArrayList<AIOrders> arrAIStayBombard = new ArrayList<>(); // if stay best bombard
    private I18NBundle i18NBundle;


    AILimber(){
        instance = this;
        i18NBundle= GameMenuLoader.instance.localization;

    }

    public void doAlliedLimberCheck() {
        /**
         * get all artillery units
         */
        EventAI.instance.show(i18NBundle.format("ailimber"));
        TurnCounter.instance.updateText(i18NBundle.get("aartilleryphase"));


        for (Unit unit : Unit.getOnBoardAllied()) {
            if (unit.isArtillery && !unit.isEliminated()) {
                arrArtillery.add(unit);
            }
        }
        if (arrArtillery.size() == 0 ){
            NextPhase.instance.nextPhase();
            return;
        }
        if (NextPhase.instance.getTurn() < 3){
            limberAll();
            return;
        }
        ArrayList<AIOrders> arrBasic =  loadarrAIBasic(); // movement and move and bombard
        ArrayList<AIOrders> arrStay = loadBestBombard(); // stay and bombard
        EventOK.instance.addObserver(this);
        EventAI.instance.hide();
        EventOK.instance.show(i18NBundle.format("ailimberend"));

        return;
    }

    /**
     *  limber all the artillery
     */
    private void limberAll() {
        int i=0;
        for (Unit unit:arrArtillery){
            unit.setArtilleryLimbered();
            unit.getMapCounter().getCounterStack().setPoints();
            Counter.rePlace(unit.getHexOccupy());
            if (i==0){
                i++;
                CenterScreen.instance.start(unit.getHexOccupy());
            }
        }
        EventOK.instance.addObserver(this);
        EventOK.instance.show(i18NBundle.format("ailimberend"));

    }

    private ArrayList<AIOrders> loadBestBombard() {
        arrAIStayBombard.clear();
        ArrayList<Hex> arrPosition = new ArrayList<>();
        for (Unit unit:arrArtillery){
            arrPosition.add(unit.getHexOccupy());
        }
        ArrayList<Hex> arrNoCombatHex = new ArrayList<>();
        arrAIStayBombard.addAll(AIBarrageHandler.instance.getMostDamageChances(arrArtillery,arrPosition,arrNoCombatHex));
        return arrAIBasicBombard;
    }

    /**
     *  load arrAIBasic with all combinations where this can move to in max movement
     *  tomove and bombard will be loaded
     *
     *
     */
    private ArrayList<AIOrders> loadarrAIBasic() {
        /**
         * get distance they can go at max   AiOrders
         */
        int thread = 0;
        boolean isArtillery = true;
        arrAIBasicMoveTo.clear();
        arrAIBasicMoveTo.addAll(AIUtil.GetIterations(arrArtillery,thread, isArtillery,Hex.getAllHex(),null,null));
        /**
         * for each iteration get bombard iteration
         */
        ArrayList<AIOrders> arrWorkAI = new ArrayList<>();
        ArrayList<Hex> arrNoCombatHex = new ArrayList<>();
        for (AIOrders aiO:arrAIBasicMoveTo){
            ArrayList<AIOrders> aiArr = AIBarrageHandler.instance.getMostDamageChances(aiO.arrUnit,aiO.arrHexMoveTo,arrNoCombatHex);
            if (aiArr != null){
                arrWorkAI.addAll(aiArr);
            }
        }
        /**
         * Sort into highest score order
         */
        arrAIBasicBombard.clear();
        int i=0;
        for (AIOrders ai:arrWorkAI) {
            for (i = 0; i < arrAIBasicBombard.size(); i++) {
                if (ai.scoreBombard > arrAIBasicBombard.get(i).scoreBombard){
                    break;
                }
            }
            arrAIBasicBombard.add(i,ai);
        }
        return arrAIBasicBombard;
    }

    /**
     *  get Best move to places for Artillery which will put them in range
     * @param arrArtillery
     * @return
     */
    public AIOrders getBestBombard(ArrayList<Unit> arrArtillery){
        /**
         * get distance they can go at max   AiOrders
         */
        int thread = 0;
        boolean isArtillery = true;
        ArrayList<AIOrders> arrAIBasicMoveTo = new ArrayList<>();
        arrAIBasicMoveTo.addAll(AIUtil.GetIterations(arrArtillery,thread, isArtillery,Hex.getAllHex(),null,null));
        /**
         *  lets score the aiorders for most it can bombard
         */
        for (AIOrders ai: arrAIBasicMoveTo){
            for (int ix=0;ix< ai.arrUnit.size();ix++){;
                ArrayList<Hex> arrHex = AIBarrageHandler.instance.getArrayOfHexesCanBombard(ai.arrUnit.get(ix),ai.arrHexMoveTo.get(ix));
                int score=0;
                if (arrHex != null){
                    for (Hex hex : arrHex) {
                        score += hex.getAttackPointsInHex();
                    }
                }
                ai.setScoreBombard(score);
            }
        }

        /**
         * score based on defensive terrain
         */
        for (AIOrders ai: arrAIBasicMoveTo){
                int score = ai.getScoreBombard();
                if (score > 4) {
                    for (Hex hex : ai.arrHexMoveTo) {
                        if (hex.isTown()) {
                            score += 5;
                        }
                        if (hex.isForest()) {
                            score += 5;
                        }
                        if (hex.isCity()) {
                            score += 8;
                        }
                    }

                    ai.setScoreBombard(score);
                }
        }

        ArrayList<AIOrders> arrWorkAI = new ArrayList<>();

        /**
         * Sort into highest score order
         */
        ArrayList<AIOrders> arrFiNal = new ArrayList();
        int i=0;
        for (AIOrders ai:arrAIBasicMoveTo) {
            for (i = 0; i < arrFiNal.size(); i++) {
                if (ai.scoreBombard > arrFiNal.get(i).scoreBombard){
                    break;
                }
            }
            arrFiNal.add(i,ai);
        }
        return arrFiNal.get(0);

    }

    @Override
    public void update(Observable observable, Object arg) {
        ObserverPackage oB = (ObserverPackage) arg;

        /**
         *  if confimed kick off next phase
         */
        if (((ObserverPackage) arg).type == ObserverPackage.Type.OK) {
            EventOK.instance.deleteObserver(this);
            NextPhase.instance.nextPhase();
        }


        }
}
