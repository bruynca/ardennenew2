package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameLogic.HooufGas;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.UI.BottomMenu;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.WinModal;

import java.util.Observable;
import java.util.Observer;

public class AIMain implements Observer {

    static public AIMain instance;
    private Phase[] Phases;
    private int phaseWork;
    private AIMain aiMain;
    private AICardHandler aicardHandler;
    private AIBarrageHandler aiBarrageHandler;
    private AIFaker aiFaker;
    private AILimber aiLimber;
    private AIScenario1 aiScenario1;
    private AIMover aiMover;
    private AIScorer aiScorer;
    private AIExecute aiExecute;
    private AICombat aiCombat;
    private AICombatScenario1 aiCombatScenario1;
    private AIReinforcement aiReinforcement;
    private AIReinforcementScenario1 aiReinforcementScenario1;
    private AIReinforcementScenarioOther aiReinforcementScenarioOther;
    private AISupply aiSupply;
    private AIScenario1Turn3to6 aiScenario1Turn3to6;
    private AIScenarioOther aiScenarioOther;
    private boolean isWaitForEvent = false;

    /**
     *  Hex for AI with their point value for deense
     */
    int[][] bestHexDefense ={{8,10,3},{8,11,4},{8,12,4},{14,10,3},{18,9,2},{11,7,3},{11,12,2},
                            {9,14,2},{9,23,3},{11,22,2},{6,18,1},{28,23,3},{28,22,2},
                            {19,14,3},{20,14,2},{24,15,2},{25,14,2},{28,13,2},{28,11,2},{27,8,3},
                            {26,8,3},{25,8,3},{18,16,1},{23,6,1},{28,24,1},{8,13,3}};

//    ArrayList<Hex> arrBestDefense = new ArrayList<>();
//    ArrayList<Unit> arrUnits = new ArrayList<>();


    public AIMain() {
        instance = this;
        Phases =  Phase.values();
        aicardHandler = new AICardHandler();
        aiBarrageHandler = new AIBarrageHandler();
        aiFaker = new AIFaker();
        aiLimber = new AILimber();
        aiScenario1 = new AIScenario1();
        aiMover = new AIMover();
        aiScorer = new AIScorer();
        aiExecute = new AIExecute();
        aiCombat = new AICombat();

        aiCombatScenario1 = new AICombatScenario1();
        aiReinforcement = new AIReinforcement();
        aiReinforcementScenario1 = new AIReinforcementScenario1();
        aiSupply = new AISupply();
        aiScenario1Turn3to6 = new AIScenario1Turn3to6();
        aiReinforcementScenarioOther = new AIReinforcementScenarioOther();
        aiScenarioOther = new AIScenarioOther();
        Hex.initAI();
        for (int[] hexI:bestHexDefense){
            Hex hex = Hex.hexTable[hexI[0]][hexI[1]];
            hex.setAI(hexI[2]);
        }

    }

    /**
     *  Should the AI Handle this allied Phase
     * @param phase
     * @return
     */
    public boolean isHandleAlliedPhase(int phase) {
        if (Phases[phase].isAllied() && GameSetup.instance.isGermanVersusAI() && Phases[phase].isAIPhase())
        {
            return true;
        }

        return false;
    }

    public boolean isHandleAxisPhase(int phase) {

        if (!Phases[phase].isAllied() && GameSetup.instance.isAlliedVersusAI()&& Phases[phase].isAIPhase())
        {
            return true;
        }
        return false;
    }


    /**
     * Phase mechanics to be handled by AI keep in sync with NextPhase
     *
     *
     * @param phase
     */
    public void handlePhase(int phase) {
        Gdx.app.log("AIMain", "handlePhase  Phase=" + Phases[phase].toString());
        //String sstr = Phases[phase].toString();
        phaseWork = phase;
        BottomMenu.instance.setEnablePhaseChange(false);
        if (EventPopUp.instance.isShowing()){
            EventPopUp.instance.addObserver(this);
            isWaitForEvent = true;
            return;
        }
        switch (Phases[phase]) {
            case ALLIED_CARD:
                aicardHandler.doAllied();
  //              NextPhase.instance.nextPhase();
  //              NextPhase handled in aiCardHandler
                break;
            case US_BARRAGE_DEFENSE:
                aiBarrageHandler.doAllied(false);
 //               NextPhase.instance.nextPhase();

                //                   endPhase(getPhase());
                break;
            case ALLIED_PRE_MOVEMENT:

                aiLimber.doAlliedLimberCheck();
                // next phase handled in doAlliedLimberCheck if nothing to do
                break;
            case ALLIED_REINFORCEMENT:
                int turn = NextPhase.instance.getTurn();
                if (Reinforcement.instance.getReinforcementsAvailable(turn).size() > 0){
 //                   AISupply.instance.doSupplyAnalysis();
                    WinModal.instance.set();
                    Reinforcement.instance.showWindow(true);
                    AIReinforcement.instance.reinforceAnalysis(true);
                    WinModal.instance.release();
                }else{
                    NextPhase.instance.nextPhase();
                }
                break;
            case ALLIED_MOVEMENT:
                Move.instance.doMovePhase(true,true, false);
                //AISupply.instance.doSupplyAnalysis();
                AIMover.instance.moveAnalysis(true);
                break;
            case ALLIED_POST_MOVEMENT:
                EventPopUp.instance.hide();
                Move.instance.endMove(false, false);
                Hex.checkStacking();
                NextPhase.instance.nextPhase();
                break;
            case BRIDGE_ALLIED:
                NextPhase.instance.nextPhase();
                break;
            case ALLIED_BARRAGE_ATTACK:
                EventPopUp.instance.hide();
                aiBarrageHandler.doAlliedOffense();
                break;
            case ALLIED_COMBAT:
                Combat.instance.Intialize(true, true);
                aiCombat.doCombatAnalysis(true);
                aiCombat.doCombatExecute();
                break;
            case ALLIED_COMBAT_END:
                EventPopUp.instance.hide();
                Combat.instance.cleanup(true);
                NextPhase.instance.nextPhase();
                break;
            case ALLIED_EXPLOTATION:
 //               Move.instance.intializeMove(true, false,true);
                NextPhase.instance.nextPhase();
                break;


            default:
                Gdx.app.log("NexPhase", "Invalid Phase =");
                ErrorGame errorGame = new ErrorGame("invalid Phase", this);
        }
        return;

    }

    public void chooseCards() {
        aicardHandler.initialize();

    }

    @Override
    public void update(Observable o, Object arg) {
        ObserverPackage oB = (ObserverPackage) arg;
        if (oB.type == ObserverPackage.Type.EVENTPOPUPHIDE){
            EventPopUp.instance.deleteObserver(this);
            if (isWaitForEvent){
                isWaitForEvent = false;
                handlePhase(phaseWork);
                return;
            }
        }

    }
}
