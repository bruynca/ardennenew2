package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitHex;

import java.util.ArrayList;
import java.util.Observable;

import jdk.javadoc.internal.tool.JavadocTodo;

public class AIFaker extends Observable {
    static public AIFaker instance;
    ArrayList<Unit> arrGermanOnBoardUnits = new ArrayList<Unit>();
    ArrayList<UnitHex> arrUnitHex = new ArrayList<>();
    static public Hex Wiltz = Hex.hexTable[19][14];
    static public Hex MartleBurg = Hex.hexTable[9][23];
    static public Hex EttleBruck = Hex.hexTable[28][23];
    static public Hex hexBlockWiltz = Hex.hexTable[24][15];
    static boolean isThreadAlive = false;
    int numThreads = 10;
    float numIterations= 0;
    float  numIterationsStart = 0;
    boolean isAllies;
    ArrayList<AIOrders> arrOrders;

    AIFaker() {
        instance = this;
    }
    public void setUnits(ArrayList<Unit> arrIn){
        arrGermanOnBoardUnits.clear();
        arrGermanOnBoardUnits.addAll(arrIn);
    }
    public void setUnitHex(ArrayList<UnitHex> arrIn){
        arrUnitHex.clear();
        arrUnitHex.addAll(arrIn);
    }

    /**
     *  start multithreaded score penetration
     *  use 10 thread
     *  make sure
     *   added observer if expecting message
     *   added array of units set
     *   add array of hex targets
     *
     * @param arrAIOrders
     * @param type of scoring
     */
    public void startScoringOrders(ArrayList<AIOrders> arrAIOrders, AIScorer.Type type, boolean isAllies) {
        Gdx.app.log("AIFakers", "startScoringOrders type="+type);
        this.isAllies = isAllies;
        if (Gdx.app.getType() != Application.ApplicationType.Desktop){
            numThreads =2;
            AIUtil.reduceIterations(arrAIOrders);
        }
        numIterationsStart =arrAIOrders.size()/numThreads;
        numIterations = 1;

        EventAI.instance.startCount(1);

        arrOrders = arrAIOrders;
        AIScorer.instance.initialize(type);
        ArrayList<AIOrders>[] aiArray = new ArrayList[numThreads];
        for (int i=0;i < numThreads;i++){;
           aiArray[i] = new  ArrayList<>();
        }
        initThreadCounters();
        isThreadAlive = true;

        /**
         * divide up the AIOrders into ten
         */
        int i=0;
        for (AIOrders aiO:arrAIOrders){
            aiArray[i].add(aiO);
            i++;
            if (i == numThreads){
                i=0;
            }
        }
        /**
         *  start 10 threads  empty ones included
         *  make sure we do not use thread zero its for game flow
         */
        i =0; //
        arrGermanOnBoardUnits = new ArrayList<>();
        arrGermanOnBoardUnits.addAll((Unit.getOnBoardAxis()));
        /**
         *  todo check this out
         */
        if (GameSetup.instance.getScenario().ordinal() > 0){
            ArrayList<Unit> arrSave = new ArrayList<>();
            for (Unit unit:arrGermanOnBoardUnits){
                if (SecondPanzerExits.instance.isInSecond(unit)){
                    arrSave.add(unit);
                }
                if (GameSetup.instance.getScenario().ordinal() == 2){
                    if (LehrExits.instance.isInLehr(unit)){
                        arrSave.add(unit);
                    }
                }
            }
            arrGermanOnBoardUnits.clear();
            arrGermanOnBoardUnits.addAll(arrSave);
        }

        ArrayList<Unit> arrGermanPass = new ArrayList<>();
        arrGermanPass.addAll(arrGermanOnBoardUnits);
        for (ArrayList<AIOrders> aiArr:aiArray){
            generateScore(aiArray[i], arrGermanPass,type,i+1);
            i++;
        }
        return;

    }

    /**
     * Run through the orders to score them  - this is multi threaded
     * @param arrAIInput the AIorders for scoring
     * @param type  of scoring
     * @param thread wich thread to use
     */
    public void generateScore(final ArrayList<AIOrders> arrAIInput, final ArrayList<Unit> arrGerman, final AIScorer.Type type, final int thread ) {
     //   Gdx.app.log("AIFakers", "generateScore thread="+thread+" Count="+arrAIInput.size());
        final Thread tM= Thread.currentThread();
        startThreadCounters(thread-1);
        final int counter = arrAIInput.size();



        new Thread(new Runnable() {
            @Override
            public void run() {
                Hex.fakeClearMoveFields(false,true,thread);
                /**
                 *  go through iterations updateing  score
                 */
                int i=0;
                int k=0;
                int j=0;
                for (AIOrders aiO:arrAIInput){
                    if (!isThreadAlive){
                        return;
                    }
                    k++;
                    j++;
                    if (k > 1) {
  //                     Gdx.app.log("AIFakers", "Thread "+thread+" Type="+type+" At ="+j);
                        k=0;
                    }
                    int score =0;
                    AIFaker.setFakeOccupied(aiO, true, thread);
                    AIFaker.setFakeZoc(aiO, thread);
 //                   Gdx.app.log("AIFakers", "Thread "+thread+" Type="+type+" At ="+j);

                    switch(type) {
                        case Supply:
                            score = AISupply.instance.getScore(thread);
                            int newScore = score + aiO.getScoreMain();
                            aiO.setScoreMain(newScore);
                            break;
                        case ReinBastogneAttack:
                        case ReinEttlebruck:
                            score = AIScorer.instance.getScore(type, arrGerman, aiO, thread);
                            aiO.setScoreMain(score);
                            // do scoring
                            break;
                        case ReinAndMoveOther:
                            int tempScore = aiO.getScoreMain();
                            score = AIScorer.instance.getScore(type, arrGerman, aiO, thread);
                            aiO.setScoreMain(score + tempScore);
                            // do scoring
                            break;
                        case ReinMartelange:
                            score = AIScorer.instance.getScore(type, arrGerman, aiO, thread);
                            int supplyScore = AISupply.instance.getScore(thread);
                            supplyScore *= 10;
                            if (score > 0) {
                                int b = 0;
                            }
                            aiO.setScoreMain(score + supplyScore);

                            // do scoring
                            break;
                        case NonPenetrate:
                        default:
                            score = AIScorer.instance.getScore(type, arrGerman, aiO, thread);
                            if (score > 0){
                                int b=0;
                            }
                            aiO.addToScoreMain(score);
                    }
                    AIFaker.setFakeOccupied(aiO, false, thread);
                    AIFaker.resetZOC(aiO, thread);
                    if (thread == 1){
                        numIterations++;
                    }

                }
                /**
                 *  done for this thread   notify  the AIMover
                 */

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        fakeDone(thread-1, type);
                    }
                });
            }
        }).start();
    }


    public static void resetZOC(AIOrders aiO, int thread) {
        for (Hex hex:aiO.arrHexMoveTo){
            ArrayList<Hex> arrHex = hex.getSurround();
            for (Hex hexZoc:arrHex){
                hexZoc.resetZOC(thread);
            }
        }
    }

    public static void setFakeZoc(AIOrders aiO, int thread) {
        for (Hex hex:aiO.arrHexMoveTo){
            hex.setFakeZOC(thread);
        }
    }

    public static void setFakeOccupied(AIOrders aiO, boolean b, int thread) {
        for (Hex hex:aiO.arrHexMoveTo){
            if (hex == null){
                int z=0;
            }
            hex.setFakeOccupiedAllies(b, thread);
        }
    }
    public static void setFakeAxisOccupied(AIOrders aiO, boolean b, int thread) {
        for (Hex hex:aiO.arrHexMoveTo){
            hex.setFakeOccupiedAXis(b, thread);
        }
    }

    private boolean[] threadStarted = new boolean[10];
    public void initThreadCounters(){
        for (boolean boo:threadStarted){
            boo= false;
        }
    }
    public void startThreadCounters(int thread){
        threadStarted[thread] = true;
    }

    /**
     *  Check if all threads complete.
     *  if Yes continue with move processing
     * @param threadComplete
     * @param type
     */
    public void fakeDone(int threadComplete, AIScorer.Type type){
        Gdx.app.log("AIFaker", "fakedone thread="+threadComplete+" type="+type);

        threadStarted[threadComplete] = false;
        for (boolean bo:threadStarted){
            if (bo == true) {
                return;
            }
        }
        /**
         * finalize the move
         */

        switch (type){
            case GermanPenetration:
            case NonPenetrate:
            case GermanRegular:
                // do nothing
                if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Intro) {
                    AIScenario1.instance.doNext(type, arrOrders);
                }
                if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.SecondPanzer) {
                    AIScenario1.instance.doNext(type, arrOrders);
                }
                if (isAllies && GameSetup.instance.getScenario() == GameSetup.Scenario.Lehr) {
                    AIScenario1.instance.doNext(type, arrOrders);
                }
                break;
            case ReinBastogneOcupy:
            case ReinBastogneAttack:
            case ReinMartelange:
            case ReinEttlebruck:
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.BastogneReinScored, null, 0, 0));
                break;
            case Supply:
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.SupplyDone, null, 0, 0));
                break;
            case AttackBastogne:
            case AttackWiltz:
            case GermanMoveScenario1:
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.FakeScenario1Done, null, 0, 0));
                break;
            case ReinAndMoveOther:
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.FakerDone, null, 0, 0));
                break;
            case NewProcess:
                setChanged();
                notifyObservers(new ObserverPackage(ObserverPackage.Type.FakerDone, null, 0, 0));
                break;

        }
    }
    public void killFaker(){
        isThreadAlive = false;
    }


    public int getPercentDone() {
        float p = numIterations/numIterationsStart * 100f;
        int percent = (int)p;
        if (percent == 0){
            percent = 1;
        }
        return percent;
    }
}
