package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.AI.AIMain;
import com.bruinbeargames.ardenne.GameLogic.AdvanceAfterCombat;
import com.bruinbeargames.ardenne.GameLogic.Airplane;
import com.bruinbeargames.ardenne.GameLogic.ArtillerySet;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameLogic.BarrageExplode;
import com.bruinbeargames.ardenne.GameLogic.BlowBridge;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.GameLogic.DefendResult;
import com.bruinbeargames.ardenne.GameLogic.Division;
import com.bruinbeargames.ardenne.GameLogic.ExitWest;
import com.bruinbeargames.ardenne.GameLogic.FixBridge;
import com.bruinbeargames.ardenne.GameLogic.HooufGas;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.LehrHalts;
import com.bruinbeargames.ardenne.GameLogic.LimberArtillery;
import com.bruinbeargames.ardenne.GameLogic.MobileAssualt;
import com.bruinbeargames.ardenne.GameLogic.MoreAmmo;
import com.bruinbeargames.ardenne.GameLogic.MoreGermanAmmo;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerHalts;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerLoses;
import com.bruinbeargames.ardenne.GameLogic.SignPost;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameLogic.Weather;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.UI.BombardDisplay;
import com.bruinbeargames.ardenne.UI.BottomMenu;
import com.bruinbeargames.ardenne.UI.BridgeExplosion;
import com.bruinbeargames.ardenne.UI.CombatDisplay;
import com.bruinbeargames.ardenne.UI.CombatDisplayResults;
import com.bruinbeargames.ardenne.UI.DiceEffect;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.Explosions;
import com.bruinbeargames.ardenne.UI.FlyingShell;
import com.bruinbeargames.ardenne.UI.HelpPage;
import com.bruinbeargames.ardenne.UI.SoundSlider;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.VictoryPopup;
import com.bruinbeargames.ardenne.UI.WinAIDisplay;
import com.bruinbeargames.ardenne.UI.WinBombard;
import com.bruinbeargames.ardenne.UI.WinCRT;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.CounterStack;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.UUID;

public class NextPhase {

    static public NextPhase instance;
    private int phase = -1;
    public final  Phase[] Phases;
    public boolean isAlliedPlayer = true;
    private AIMain aiMain;
    private boolean isDebug = false;
    boolean isFirstTime = true;
    boolean isLocked =  false;
    Hex hexNull;
    int turn = 1;
    TurnCounter turnCounter;
    public Weather weather;
    WinCRT winCRT = new WinCRT();
    private I18NBundle i18NBundle;
    boolean isAIControl = false;
    boolean isInBarrage = false;

    String programUID = "";

    DiceEffect diceEffect = new DiceEffect();
    Explosions explosions = new Explosions();




    public NextPhase() {
        Gdx.app.log("NextPhase", "Constructor");


        instance = this;
        Phases =  Phase.values();
//        GamePreferences gamePreferences = new GamePreferences();
        Move move = new Move();
        Barrage barrage = new Barrage();
        Combat combat = new Combat();
        Airplane  airSupport = new Airplane();
        Reinforcement reinforcement = new Reinforcement();
        DefendResult defendResult = new DefendResult();
        AdvanceAfterCombat advanceAfterCombat = new AdvanceAfterCombat();
        MobileAssualt mobileAssualt = new MobileAssualt();
        ArtillerySet artillerySet = new ArtillerySet();
        turnCounter = new TurnCounter();
        weather = new Weather();
        turnCounter.instance.updateTurn(turn, weather.getCurrentType());
        LimberArtillery limberArtillery = new LimberArtillery();
        BottomMenu bottomMenu = new BottomMenu();
        CombatDisplay combatDisplay = new CombatDisplay();
        CombatDisplayResults combatDisplayResults = new CombatDisplayResults();
  //      MouseImage mouseImage   = new MouseImage();
        Supply supply = new Supply();
        BombardDisplay bombardDisplay = new BombardDisplay();
        Gdx.app.log("NextPhase", "Constructor End");
        WinBombard winBombard = new WinBombard();
        HelpPage helpPage = new HelpPage();
        Division division = new Division();
        BlowBridge blowBridge = new BlowBridge();
        FixBridge fixBridge = new FixBridge();
        MoreAmmo moreAmmo = new MoreAmmo();
        EventAI eventAI = new EventAI();
        SignPost signPost = new SignPost();
        SecondPanzerHalts secondPanzerHalts = new SecondPanzerHalts();
        LehrHalts lehrHalts = new LehrHalts();
        HooufGas hooufGas = new HooufGas();
        SecondPanzerLoses secondPanzerLoses = new SecondPanzerLoses();
        Explosions explosions = new Explosions();
        FlyingShell flyingShell = new FlyingShell();
        BridgeExplosion bridgeExplosion = new BridgeExplosion();
        MoreGermanAmmo moreGermanAmmo = new MoreGermanAmmo();
        SoundSlider soundSlider = new SoundSlider();
        VictoryPopup victoryPopup = new VictoryPopup();
        ExitWest exitWest = new ExitWest();
        WinAIDisplay winAIDisplay = new WinAIDisplay();
        SecondPanzerExits s = new SecondPanzerExits();
        LehrExits l = new LehrExits();
        aiMain = new AIMain();

        //      CardHandler cardHandler = new CardHandler(); not here but gamesetup
        i18NBundle= GameMenuLoader.instance.localization;
        for (Actor actor:ardenne.instance.hexStage.getActors()){
            actor.remove();
        }
        System.gc();



    }
    public void nextPhase() {
        /**
         *  Next Turn
         */
        /**
         *  show debugging how we got here
         */
        String calling3 = Thread.currentThread().getStackTrace()[2].getClassName().toString();
        String calling4 = Thread.currentThread().getStackTrace()[2].getMethodName().toString();
        if (phase != -1) {
            Gdx.app.log("nextPhase", " in Old =" + Phases[phase].toString());
            Gdx.app.log("nextPhase", " class invoking   ="+ calling3);
            Gdx.app.log("nextPhase", " method invoking  ="+ calling4);
        }
        cntDebug++;
        phase++;
        if (Phases[phase] == Phase.NEXT_TURN)
        {
            Gdx.app.log("nextPhase","Next Turn=");
            /**
             *  check for end of game
             */
            if (turn == GameSetup.instance.getScenario().getLength()){
                String winner = VictoryPopup.instance.determineVictor();
                BottomMenu.instance.setEnablePhaseChange(false);
                AccessInternet.updateGame(turn, winner);
                return;
            }
            /**
             *  check for loss because units not exitted
             *  do this before the turn changes
             */
            if (turn > 3) {
                boolean iswarned = false;
                if (GameSetup.instance.getScenario().ordinal() > 0) {
                    if (SecondPanzerExits.instance.checkExits()){
                        String winner = VictoryPopup.instance.determineVictor();
                        BottomMenu.instance.setEnablePhaseChange(false);
                        AccessInternet.updateGame(turn, winner);
                        return;
                    }
                }
                if (GameSetup.instance.getScenario().ordinal() > 1 && !iswarned) {
                    if (LehrExits.instance.checkExits()){
                        String winner = VictoryPopup.instance.determineVictor();
                        BottomMenu.instance.setEnablePhaseChange(false);
                        AccessInternet.updateGame(turn, winner);
                        return;
                    }
                }
            }

            /**
             *  check for end of game no exit
             */

            phase = 0;
            Unit.initCanAttack();
            turn++;

            weather.nextTurn();
            for (Unit unit:Unit.getOnBoardAxis()){
                unit.setHasAttackedThisTurnOff();
                unit.setHasbeenAttackedThisTurnOff();
            }
            for (Unit unit:Unit.getOnBoardAllied()){
                unit.setHasAttackedThisTurnOff();
                unit.setHasbeenAttackedThisTurnOff();
            }
            turnCounter.instance.updateTurn(turn, weather.getCurrentType());
            turnCounter.instance.updateText(i18NBundle.get("nextturn"));
            if (turn > 1){
                AccessInternet.updateGame(turn, "");
            }
        }
        if (turn == 1 && phase == 0){
            createProgramUID();
            AccessInternet.registerGame(false);
            EventPopUp.instance.setSpecial();
            if (GameSetup.instance.getScenario() == GameSetup.Scenario.SecondPanzer){
                EventPopUp.instance.show(i18NBundle.get("event2"));
            }else if (GameSetup.instance.getScenario() == GameSetup.Scenario.Lehr){
                EventPopUp.instance.show(i18NBundle.get("event3")); }
            else{
                EventPopUp.instance.show(i18NBundle.get("event1"));
            }

            return;
        }
        Reinforcement.instance.resetUnitsLoaded();


        BottomMenu.instance.showBottomMenu();
        /**
         *  warning for exitted units
         */
        if (turn > 3 && phase == 0) {
            boolean iswarned = false;
            if (GameSetup.instance.getScenario().ordinal() > 0) {
                if (SecondPanzerExits.instance.checkExits()){
                    String str = i18NBundle.format("2ndmustexit");
                    EventPopUp.instance.show(str);
                    iswarned = true;
                }
            }
            if (GameSetup.instance.getScenario().ordinal() > 1 && !iswarned) {
                if (LehrExits.instance.checkExits()){
                    String str = i18NBundle.format("2ndmustexit");
                    EventPopUp.instance.show(str);
                }
            }
        }
        if (GamePreferences.isDEbug) {
            SaveGame.SaveDebug("Debug " + cntDebug + " Turn=" + getTurn() + " " + Phases[phase].toString() + "  ", cntDebug);
        }
        SaveGame.SaveLastPhase(" Last Phase",2);
        if (phase == 0){
            BottomMenu.instance.enablePhaseChange();
            return;
        }
        HelpPage.instance.nextPhase();
        setPhase();

    }

    private void createProgramUID() {
        UUID uuid = UUID.randomUUID();
        programUID = UUID.randomUUID().toString().replace("-", "");
    }
    public String getProgramUID(){
        return programUID;
    }
    public void setProgramUID(String inUID){
        programUID = inUID;
    }

    public void continuePhaseFirstTime(){ // for initial start
        BottomMenu.instance.showBottomMenu();
        setPhase();
 //       SaveGame.SaveDebug("Debug "+cntDebug+" Turn="+getTurn()+" "+Phases[phase].toString()+"  ",cntDebug);
        HelpPage.instance.nextPhase();
    }


    public int getPhase() {
        return phase;
    }
    public void setTurn(int turnIn){
        turn = turnIn;
        if (turn > 1){
            Unit.setZOCMobileArtillery();
        }
    }


    /**
     *  Bypass doSeaMove handling and set directly
     *  used by loadgame
     * @param
     */
    public void setPhaseDirect(int phaseIn){
        phase = phaseIn;
    }

    public void setPhase() {
        isAIControl = true;
        isInBarrage = false;
        ClickAction.unLock();
        ClickAction.clearClickListeners();
        Hex.initOccupied(); // bug somewhere
        if (aiMain.isHandleAlliedPhase(phase)) {
            Gdx.app.log("nextPhase","AI in New ="+Phases[phase].toString());
            aiMain.handlePhase(phase);
            return;
        } else if (aiMain.isHandleAxisPhase(phase)) {
            Gdx.app.log("nextPhase","AI in New ="+Phases[phase].toString());
            aiMain.handlePhase(phase);
            return;
        }else {
            isAIControl = false;
            Gdx.app.log("nextPhase","next  ="+Phases[phase].toString());
            BottomMenu.instance.setEnablePhaseChange(true);

            switch (Phases[phase]) {
                case CARD_CLEANUP:
                    CardHandler.instance.cleanLastTurn(turn);
                    break;
                case ALLIED_CARD:
                    isAlliedPlayer = true;
                    CardHandler.instance.alliedCardPhase(turn);
                    break;
                case GERMAN_CARD:
                    isAlliedPlayer = false;
                    CardHandler.instance.germanCardPhase(turn);
                    break;
                case GERMAN_ROLL_BRIDGE:
                    isAlliedPlayer = false;
                    AccessInternet.updateGame(NextPhase.instance.turn, "");
                    FixBridge.instance.display(null);
                    break;
                case GERMAN_PRE_MOVEMENT:
                    isAlliedPlayer = false;
                    LimberArtillery.instance.initializeLimber(false,false);
                    break;
                case GERMAN_MOVEMENT:
                    isAlliedPlayer = false;
                    Move.instance.intializeMove(false, false, false);
  //                  endPhase(getPhase());
                    break;
                case GERMAN_POST_MOVEMENT:
                    EventPopUp.instance.hide();
                    isAlliedPlayer = false;
                    Move.instance.endMove(false, false);
//                    Hex.checkStacking();
 //                   Hex.checkStacking();
 //                   OverStacking.instance.check();
                    nextPhase();
                    break;
                case US_BARRAGE_DEFENSE:
                    isInBarrage = true;
                    isAlliedPlayer = true;
                    Barrage.instance.intialize(true,false);
 //                   endPhase(getPhase());
                    break;
                case GERMAN_BARRAGE_ATTACK:
                    isInBarrage = true;
                    isAlliedPlayer = false;
                    EventPopUp.instance.hide();
  //                  Unit.initUnShade();
                    Barrage.instance.intialize(false,false);
                    break;
                case GERMAN_BARRAGE_RESOLVE:
                    isInBarrage = true;

                    isAlliedPlayer = false;
                    //                  Unit.initUnShade();
                    BarrageExplode barrageExplode = new BarrageExplode();
                    break;
                case GERMAN_BARRAGE_END:
                    isInBarrage = true;

                    isAlliedPlayer = false;
                    BarrageExplode.End();
                    Barrage.clearTargets();
                    nextPhase();
                    break;
                case GERMAN_COMBAT:
                    isAlliedPlayer = false;
                    Combat.instance.Intialize(false, false);
                    break;
                case GERMAN_COMBAT_END:
                    isAlliedPlayer = false;
                    EventPopUp.instance.hide();
                    Combat.instance.cleanup(true);
                    nextPhase();
                    break;
                case GERMAN_EXPLOTATION:
                    Move.instance.intializeMove(false, false, true);
                    break;
                case GERMAN_POST_EXPLOTATION:
                    EventPopUp.instance.hide();
                    Move.instance.endMove(false, false);
                    Hex.checkStacking();
                    if (LehrHalts.instance.isLehrHalted()){
                        LehrHalts.instance.restore();
                    }
                    if (SecondPanzerHalts.instance.is2NDPanzerHalted()){
                        SecondPanzerHalts.instance.restore();
                    }
                    nextPhase();
                    break;
                case GERMAN_SUPPLY:
                    isAlliedPlayer = false;
                    Unit.initOutOfSupplyThisTurn(false);
                    Supply.instance.doGermanSupply();
                    break;
                case GERMAN_SUPPLY_END:
                    isAlliedPlayer = false;
                    Supply.instance.EndSupplyGerman();
// done in Supply                    nextPhase();
                    break;
                case GERMAN_END:
                    isAlliedPlayer = false;
                    Unit.initUnShade();
                    ClickAction.cancelAll();
                    nextPhase();
                    break;
                case ALLIED_PRE_MOVEMENT:
                    isAlliedPlayer = true;
                    LimberArtillery.instance.initializeLimber(true,false);
                    break;
                case ALLIED_REINFORCEMENT:
                    isAlliedPlayer = true;
                    if (getTurn() == 3){
                        Supply.instance.loadOtherUSSupply();
                    }
                    if (Reinforcement.instance.getReinforcementsAvailable(turn).size() > 0){
                        Reinforcement.instance.showWindow();
                    }else{
                        nextPhase();
                    }
                    break;

                case ALLIED_MOVEMENT:
                    isAlliedPlayer = true;
                    Move.instance.intializeMove(true, false,false);
                    break;
                case ALLIED_POST_MOVEMENT:
                    isAlliedPlayer = true;
                    EventPopUp.instance.hide();
                    Move.instance.endMove(false, false);
                    Hex.checkStacking();
                    nextPhase();
                    break;
                case BRIDGE_GERMAN:
                    HooufGas.instance.checkHooufgas();

                    nextPhase();
                    break;
                case BRIDGE_ALLIED:
                    nextPhase();
                    break;
                case GERMAN_BARRAGE_DEFEND:
                    isInBarrage = true;

                    isAlliedPlayer = false;

                    Barrage.instance.intialize(false,false);
                    break;
                case ALLIED_BARRAGE_ATTACK:
                    isInBarrage = true;

                    isAlliedPlayer = true;
                    EventPopUp.instance.hide();
                    Barrage.instance.intialize(true,false);
                    break;
                case ALLIED_BARRAGE_RESOLVE:
                    isInBarrage = true;

                    isAlliedPlayer = true;
                    barrageExplode = new BarrageExplode();
                    break;
                case ALLIED_BARRAGE_END:
                    isInBarrage = true;

                    isAlliedPlayer = true;
                    BarrageExplode.End();
                    Barrage.clearTargets();

                    nextPhase();
                    break;
                case ALLIED_COMBAT:
                    isAlliedPlayer = true;
                    Combat.instance.Intialize(true,false);
                    break;
                case ALLIED_COMBAT_END:
                    isAlliedPlayer = true;
                    EventPopUp.instance.hide();
                    Combat.instance.cleanup(true);
                    nextPhase();
                    break;
                case ALLIED_EXPLOTATION:
                    Move.instance.intializeMove(true, false,true);
                    break;
                case ALLIED_POST_EXPLOTATION:
                    EventPopUp.instance.hide();
                    Move.instance.endMove(false, false);
                    Hex.checkStacking();
                    nextPhase();
                    break;
                case ALLIED_SUPPLY:
                    isAlliedPlayer = true;
                    Unit.initOutOfSupplyThisTurn(true);
                    Supply.instance.doAlliedSupply();
                    break;

                case ALLIED_END:
                    isAlliedPlayer = true;
                    Supply.instance.endSupplyUS();
                    nextPhase();
                    break;
                case NEXT_TURN:
                    break;
                default:
                    Gdx.app.log("NexPhase", "Invalid Phase");
                    ErrorGame errorGame = new ErrorGame("invalid Phase", this);
            }

        }

    }
    public void endPhase(int phaseToEnd){
        Gdx.app.log("NexPhase", "endPhase"+Phases[getPhase()].toString());

        if (phaseToEnd != getPhase()){
            ErrorGame errorGame = new ErrorGame("EndPhase and current Phase do not Match",this);
        }
        CounterStack.removeAllHilites();
        CounterStack.removeAllShaded();


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.log("NextPhase", "Timer" );
                nextPhase();
            }

        }, .065F);
   }




    public void setDebug() {
        isDebug = true;
        cntDebug = 0;
        isLocked = true;
    }

    public int getTurn(){
        return turn;
    }
    int cntDebug = 0;

    public boolean isArtillery() {
        if (phase == Phase.GERMAN_PRE_MOVEMENT.ordinal() ||  phase ==Phase.ALLIED_PRE_MOVEMENT.ordinal()){
            return true;
        }
        return false;
    }
    public boolean isAlliedPlayer(){
        return isAlliedPlayer;
    }
    public AIMain getAiMain(){
        return aiMain;
    }
    public boolean isAIControl(){
        return isAIControl;
    }
    public boolean isInBarrage(){
        return isInBarrage;
    }
}