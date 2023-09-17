package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.Map;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.Phase;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

public class WinReinforcements {
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion close =  textureAtlas.findRegion("close");
    TextureRegion reinforce1 =  textureAtlas.findRegion("hilitehexreindisplay2");
    TextureRegion reinforce2 =  textureAtlas.findRegion("hilitehexreindisplay3");
    Window window;
    Stage stage;
    Label label;
    Table table;
    ArrayList<Counter> arrCounterSave = new ArrayList<>();
    ArrayList<Unit> arrUnits = new ArrayList<>();
    ArrayList<Unit> arrUnitsEast = new ArrayList<>();
    ArrayList<Unit> arrUnitsSouthEast = new ArrayList<>();
    ArrayList<Unit> arrUnitsSouth = new ArrayList<>();
    ArrayList<Unit> arrUnitsSouthWest = new ArrayList<>();


    Counter prevCounter;
    HiliteHex hiliteHex;
    float winWidth = 500; // 900 original
    float winHeight = 200; // 650 original
    final float counterSize =70f;



    private I18NBundle i18NBundle;
    TextTooltip.TextTooltipStyle tooltipStyle;
    private EventListener hitOK;
    Unit unitWorkOn;
    UnitMove unitMove;
    Image imageReinforce;
    boolean isMovableReinforcement = true;
    boolean isStillReinforcements = true;
    boolean isWindowActive = false;
    boolean isAI=false;

    public WinReinforcements(boolean isAI) {
        isWindowActive = true;
        this.isAI = isAI;
        stage= ardenne.instance.guiStage;
        arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(NextPhase.instance.getTurn()));
        sortUnits();
        i18NBundle = GameMenuLoader.instance.localization;
        TurnCounter.instance.updateText(i18NBundle.format("reinphase"));

        tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
        tooltipStyle.background = new NinePatchDrawable(np);
        np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        String title = i18NBundle.format("reinforceheading");
        window = new Window(title, windowStyle);
        Label lab = window.getTitleLabel();
        lab.setAlignment(Align.center);
        Image image = new Image(close);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (arrUnits.size() > 0 && isStillReinforcements){
                    EventPopUp.instance.show(i18NBundle.format("exitreinforcements"));
                    isStillReinforcements = false;
                    return;
                }
                for (Unit unit:arrUnits){
                    Reinforcement.instance.removeReinforcement(unit);
                }
                end();
            }
        });
        window.getTitleTable().add(image);
        hitOK = new TextTooltip(
                i18NBundle.format("endreinforcements"),
                tooltipStyle);
        image.addListener(hitOK);

        window.setModal(false);
        window.setTransform(true);

        int heightWindow = (Counter.sizeOnMap + 100);
        window.setSize(winWidth,heightWindow);
        window.setPosition(100,100);
        showWindow();
    }

    private void sortUnits() {
        for (Unit unit:arrUnits) {
            if (unit.getEntryX() == 33 && unit.getEntryY() == 24) {
                arrUnitsEast.add(unit);
            } else if (unit.getEntryX() == 28 && unit.getEntryY() == 24) {
                arrUnitsSouthEast.add(unit);
            } else if (unit.getEntryX() == 9 && unit.getEntryY() == 24) {
                arrUnitsSouth.add(unit);
            } else if (unit.getEntryX() == 0 && unit.getEntryY() == 19) {
                arrUnitsSouthWest.add(unit);
            }
        }
    }
    private int getLargestArray(){
        int ret = 0;
        if (arrUnitsSouthWest.size() > ret){
            ret = arrUnitsSouthWest.size();
        }
        if (arrUnitsSouthEast.size() > ret){
            ret = arrUnitsSouthEast.size();
        }
        if (arrUnitsEast.size() > ret){
            ret = arrUnitsEast.size();
        }
        if (arrUnitsSouth.size() > ret){
            ret = arrUnitsSouth.size();
        }
        return ret;

    }

    public void showWindow() {
 //       window.debug();
        Vector2 v2 = GamePreferences.getWindowLocation("reinforcements");
//        if (GameSetup.instance.isGermanVersusAI()){
//            v2.x = 0;
//            v2.y = 0;
//        }
        if (v2.x == 0 && v2.y == 0) {
            window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
            v2.x  = Gdx.graphics.getWidth() - window.getWidth();
            float xMove = Gdx.graphics.getWidth() - window.getWidth();
            window.addAction(Actions.moveTo(xMove, 0, .3F));
        }else{
            window.setPosition(v2.x, v2.y);

        }
        int cnt = getLargestArray();
        if (cnt > 1) {
            winWidth = cnt * (Counter.sizeOnMap + 5) + 100;
        }else {
            winWidth =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
        }
        Label.LabelStyle labelStyle = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
        if (arrUnitsSouthWest.size() > 0) {
            label = new Label(i18NBundle.get("southwest"), labelStyle);
            window.add(label).colspan(cnt);
            window.row();
            Table table = loadTable(arrUnitsSouthWest);
            window.add(table);
            window.row();
            float cntCounters = arrUnitsSouthWest.size();
            int displayRows = (int) (cntCounters / 6 + .9F);
            winHeight = ((displayRows) * (counterSize + 2)) + 100;
            int work;

        }

        if (arrUnitsSouth.size() > 0)
        {
            label = new Label(i18NBundle.get("south"), labelStyle);
            window.add(label).colspan(cnt).padTop(26);
            window.row();
            Table table =  loadTable(arrUnitsSouth);
            window.add(table);

            window.row();

            //           window.add(table).padTop(15).al;
            float cntCounters = arrUnitsSouth.size();
            int displayRows = (int) (cntCounters / 6 + .9F);
            winHeight += ((displayRows) * (counterSize + 2)) +100;

        }

        if (arrUnitsSouthEast.size() > 0)
        {
            label = new Label(i18NBundle.get("east"), labelStyle);
            window.add(label).colspan(cnt).padTop(26);
            window.row();
            Table table = loadTable(arrUnitsSouthEast);
            window.add(table);
            window.row();
            //           window.add(table).padTop(15).al;
            float cntCounters = arrUnitsSouthEast.size();
            int displayRows = (int) (cntCounters / 6 + .9F);
            winHeight += ((displayRows) * (counterSize + 2)) +100;

        }
        if (arrUnitsEast.size() > 0)
        {
            label = new Label(i18NBundle.get("southeast"), labelStyle);
            window.add(label).colspan(cnt).padTop(26);
            window.row();
            Table table =loadTable(arrUnitsEast);
            window.add(table);
            //           window.add(table).padTop(15).al;
            float cntCounters = arrUnitsEast.size();
            int displayRows = (int) (cntCounters / 6 + .9F);
            winHeight += ((displayRows) * (counterSize + 2)) +100;


        }



        if (winHeight == 0){
            winHeight += ((1) * (counterSize + 2)) + 60;

        }
        window.setSize(winWidth,winHeight);

        stage.addActor(window);
        BottomMenu.instance.setEnablePhaseChange(false);
    }
    /**
     * Create a table of all the units associated with a specific setup
     * area This may have to be adjusted depending how many units
     *
     * @param arrUnits
     * @return Table
     */
    private Table loadTable(ArrayList<Unit> arrUnits) {
        Table table = new Table();
        final int size =100;
        int i = 0;
        for (final Unit unit : arrUnits) {
            final Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
            arrCounterSave.add(counter);
            counter.stack.setTransform(true);
            float ratio =(float) size/Counter.size;
            counter.stack.setScale(ratio);
            counter.getCounterStack().adjustFont(.8f);
            counter.getCounterStack().getStack().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    hitUnit(unit, counter, unitWorkOn);
 /*                   if (unitWorkOn != null) {

                        if (unit == unitWorkOn) {
                            unChooseUnit(counter);
                            return;
                        }
                        if (unit != unitWorkOn) {
                            unChooseUnit(prevCounter);
                       }
                    }
                    unitWorkOn = unit;
                    prevCounter = counter;
                    ArrayList<Hex> arrHex = findHexesToPlaceThisUnit(unit);
                    if (arrHex.size() == 0){
                        EventPopUp.instance.show(i18NBundle.get("noplacereinforcement"));
                        return;
                    }
                    Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
                    displayEntry(hex);
                    Vector2 v2 = hex.getCounterPosition();
                    counter.getCounterStack().hilite();
                    hiliteHex = new HiliteHex(arrHex, HiliteHex.TypeHilite.Reinforcement, null);*/
                }
            });
            counter.getCounterStack().getStack().setSize(counterSize, counterSize);

            table.add(counter.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap);
        }
        return table;

    }
    public Counter getCounter(Unit unit){
        for (Counter counter:arrCounterSave){
            if (counter.getUnit() == unit){
                return counter;
            }
        }
        return null;
    }

    public void hitUnit(Unit unit, Counter counter, Unit unitWork) {
        if (unitWork != null) {

            if (unit == unitWork) {
                unChooseUnit(counter);
                return;
            }
            if (unit != unitWork) {
                unChooseUnit(prevCounter);
            }
        }
        unitWorkOn = unit;
        prevCounter = counter;
        ArrayList<Hex> arrHex = findHexesToPlaceThisUnit(unit);
        if (arrHex.size() == 0){
            EventPopUp.instance.show(i18NBundle.get("noplacereinforcement"));
            return;
        }
        Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
        displayEntry(hex);
        Vector2 v2 = hex.getCounterPosition();
        counter.getCounterStack().hilite();
        hiliteHex = new HiliteHex(arrHex, HiliteHex.TypeHilite.Reinforcement, null);

    }

    private void displayEntry(Hex hex) {
        if (imageReinforce !=null){
            imageReinforce.remove();
            imageReinforce = null;
        }
        if (hex == Reinforcement.instance.hexBastogneReinforce){
            imageReinforce = new Image(reinforce2);
        }else{
            imageReinforce = new Image(reinforce1);
        }
        Vector2 pos = hex.getCounterPosition();
        pos.x -= 48;
        pos.y -= 22; //16
        imageReinforce.setPosition(pos.x, pos.y);
        ardenne.instance.mapStage.addActor(imageReinforce);


    }

    public  ArrayList<Hex> findHexesToPlaceThisUnit(Unit unit) {
        int cost = Reinforcement.instance.getCost(unit);
        cost++;
        isMovableReinforcement = true;
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
        if (!Map.instance.onScreen(hex)) {
            CenterScreen.instance.start(hex);
        }
        if (hex.canOccupy(unit)) {
            unitMove = new UnitMove(unit, unit.getCurrentMovement() - cost,false,true,hex,0);
            arrHexReturn.addAll(unitMove.getMovePossible());
            arrHexReturn.add(hex);
            return arrHexReturn;
        }
        isMovableReinforcement = false;
        /**
         *  entry place blocked
         */
        ArrayList<Hex> arrSurr = hex.getSurround();
        for (Hex hex2:arrSurr) {
            if (hex2.canOccupy(unit)) {
                arrHexReturn.add(hex2);
            }
        }
        return arrHexReturn;
    }
    public void unChooseUnit(Counter counter)
    {
        counter.getCounterStack().removeHilite();
        hiliteHex.remove();
        if (imageReinforce != null) {
            imageReinforce.remove();
        }
        imageReinforce.remove();
        unitWorkOn = null;

    }
    public void cancel(){
        hiliteHex.remove();
        if (imageReinforce != null) {
            imageReinforce.remove();
        }
        prevCounter.getCounterStack().removeHilite();
    }
    public void end(){
        remove();
        if (imageReinforce != null) {
            imageReinforce.remove();
        }
        if (NextPhase.instance.getPhase() == Phase.ALLIED_REINFORCEMENT.ordinal()){
            NextPhase.instance.nextPhase();
        }
    }
    public boolean isWindowStillActive(){
        return isWindowActive;
    }
    public void doMove(Hex hex, ObserverPackage op){
        if ( op != null && hitWindow(op)) {  // ai coming hrough
            return;
        }
        hiliteHex.remove();
        prevCounter.getCounterStack().getStack().setVisible(false);
        Reinforcement.instance.removeReinforcement(unitWorkOn);
        Hex hexPlace = Hex.hexTable[unitWorkOn.getEntryX()][unitWorkOn.getEntryY()];
        if (isMovableReinforcement){
            unitWorkOn.placeOnBoard(hexPlace);
            ArrayList<Hex> arrHex = unitMove.getLeastPath(hex,false,null);
            Move.instance.actualMove(unitWorkOn,arrHex, Move.AfterMove.ToReinforcement, false);
            unitWorkOn.setMovedThisTurn(NextPhase.instance.getTurn());
        }else{
            unitWorkOn.placeOnBoard(hex);
            unitWorkOn.setMovedThisTurn(NextPhase.instance.getTurn());
            afterMove(unitWorkOn);
        }
    }

    private boolean hitWindow(ObserverPackage op) {
        float  winStartx = window.getX();
        float  winEndx = window.getX()+window.getWidth();
        float  winStarty = window.getY();
        float  winEndy = window.getY()+window.getHeight();
        int reverse = Gdx.graphics.getHeight() - op.y;
        if (op.x < winStartx || op.x > winEndx || reverse < winStarty || reverse > winEndy) {
            return false;
        }
        return true;
    }
    public void afterMove(Unit unit){
        for (Counter counter:arrCounterSave){
            if (counter.getUnit() == unit){
 //               counter.getCounterStack().getStack().setVisible(false);
                arrUnits.remove(unit);
                if (arrUnits.size() == 0){
                    end();
                }
            }
        }
        if (imageReinforce != null) {
            imageReinforce.remove();
        }


    }

    public void remove() {
        int lastX = (int) window.getX();
        int lastY = (int) window.getY();
        if (!GameSetup.instance.isGermanVersusAI()) {
            GamePreferences.setWindowLocation("reinforcements", lastX, lastY);
        }
        if (hiliteHex != null){
            hiliteHex.remove();
        }
        if (imageReinforce != null) {
            imageReinforce.remove();
        }
        window.clear();
        window.remove();
        isWindowActive = false;
        BottomMenu.instance.setEnablePhaseChange(true);
    }
    public void fadeWindow(){

// Create an AlphaAction to fade the actor to 50% opacity over a duration of 2 seconds
        AlphaAction fadeAction = Actions.alpha(0.5f, 2.0f);

// Create a SequenceAction to chain the fade action with other actions if needed
        SequenceAction sequenceAction = Actions.sequence(fadeAction);

// Add the sequence action to the actor
        window.addAction(sequenceAction);
        Action action = Actions.fadeOut(4F);
    }


}






