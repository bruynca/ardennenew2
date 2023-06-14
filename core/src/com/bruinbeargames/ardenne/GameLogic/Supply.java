package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.AI.AIUtil;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHelper;
import com.bruinbeargames.ardenne.Hex.HexUnits;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.Hex.River;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.EventOK;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.WinSupply;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Supply implements Observer{
    static public Supply instance;
    TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    TextureRegion supplyIcon =  textureAtlas.findRegion("supply");

    int[][] germanSupply ={{40,5},{40,20},{40,14}};
    int[][] supplyHexBottlenecks ={{31,21},{28,13},{28,23},{27,7},{27,8},{24,15},{25,8},
            {26,8},{21,6},{26,14},{25,14},{29,22},{30,22},{19,20},{20,14},{9,23}};

    private ArrayList<Hex> arrGermanSupply = new ArrayList();
    ArrayList<Hex> arrGermanBottlenecks = new ArrayList();
//    int[][] alliedSupply ={{8,11},{12,3},{0,8},{0,20},{3,24}};
    int[][] alliedSupply ={{0,19},{9,24},{28,24}};
    ArrayList<Image> arrSupplyImages = new ArrayList<Image>();
    ArrayList<Hex> arrAlliedSupply = new ArrayList();
    public final int initialRange = 35;
    public final int toUnit =10;
    private I18NBundle i18NBundle;
    ArrayList<Unit> arrTransports = new ArrayList<Unit>();
    Unit unitTransportWorkOn = null;
    //Counter counterWorkedOn = null;
    HiliteHex hiliteHex;
    ArrayList<Unit> arrUnitsSupply = new ArrayList<>();
    int[] move;
    int[] attack;
    int[] defend;
    ArrayList<Unit>[] arrUnitsBeingSupplied;
    WinSupply winSupply;
    private boolean isHoufflaize = false;
    private boolean isHoufflaizeOperational = false;
    public Hex hexHouf = Hex.hexTable[12][3];

    //   ArrayList<Counter> arrCounter = new ArrayList<>();


    public Supply(){
        instance = this;
        i18NBundle = GameMenuLoader.instance.localization;

        for (int[] in:germanSupply){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrGermanSupply.add(hex);
        }
        for (int[] in:supplyHexBottlenecks){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrGermanBottlenecks.add(hex);
        }
        arrAlliedSupply.add(Hex.hexTable[12][3]);
        Image image = new Image(supplyIcon);
        image.setScale(.8f);
        arrSupplyImages.add(image);
         for (Hex hex:arrGermanSupply){
            image = new Image(supplyIcon);
            image.setScale(.8f);
            arrSupplyImages.add(image);
        }


  /*      for (int[] in:alliedSupply){
            Hex hex = Hex.hexTable[in[0]][in[1]];
            arrAlliedSupply.add(hex);
            Image image = new Image(supplyIcon);
            image.setScale(.8f);
            arrSupplyImages.add(image);
        }*/

        i18NBundle= GameMenuLoader.instance.localization;

    }
    public ArrayList<Hex> getGermanSupply(){
        ArrayList<Hex> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrGermanSupply);
        return arrReturn;
    }
    public ArrayList<Hex> getGermanBottlenecks(){
        ArrayList<Hex> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrGermanBottlenecks);
        return arrReturn;
    }
    public ArrayList<Hex> getAlliedSupply(){
        ArrayList<Hex> arrReturn = new ArrayList<>();
        arrReturn.addAll(arrAlliedSupply);
        return arrReturn;
    }
    public void addHooufGas(){
        isHoufflaize = true;
    }
    public void removeHooufgas(){
        isHoufflaize = false;
    }
    public void doGermanSupply() {
        int i=0;
        for (Hex hex:arrGermanSupply){
            if (!hex.isAlliedOccupied()){
                Vector2 v2 = hex.getCounterPosition();
                arrSupplyImages.get(i).setPosition(v2.x + 10,v2.y +10-7);
                arrSupplyImages.get(i).setSize(120f,120f);
                ardenne.instance.mapStage.addActor(arrSupplyImages.get(i));
                i++;
            }
        }
        TurnCounter.instance.updateText(i18NBundle.get("germansupply"));
        Unit.initShade(false);
  //      arrCounter.clear();
        unitTransportWorkOn = null;
        arrTransports.clear();
        arrTransports = Unit.getTransports(false);

        /**
         *  initialize units being supplied
         *  and which supply truck is supplying them
         */
        arrUnitsSupply = Unit.getOnBoardAxis();
        for (Unit unit:arrUnitsSupply){
            if (unit.isDisorganized()){
                unit.setOffDisorganized();
            }
            if (unit.getHasAttackedThisTurn() || unit.getHasbeenAttackedThisTurn()){
                if (!unit.isArtillery){
                    unit.reduceCombat();
                }
            }
            if (River.instance.isEastOfOur(unit.getHexOccupy())){
                supplyUnit(unit);
            }
        }
        arrUnitsBeingSupplied = new ArrayList[arrUnitsSupply.size()];
        move = new int[arrUnitsSupply.size()];
        attack = new int[arrUnitsSupply.size()];
        defend = new int[arrUnitsSupply.size()];
        for (i=0; i<arrUnitsSupply.size(); i++){
            attack[i] = arrUnitsSupply.get(i).getCurrenAttackFactor();
            defend[i] = arrUnitsSupply.get(i).getCurrentDefenseFactor();
            move[i] = arrUnitsSupply.get(i).getCurrentMovement();
        }
        for (i=0; i<arrUnitsBeingSupplied.length; i++){
            arrUnitsBeingSupplied[i] = new ArrayList<>();
        }
        Unit.initUntouchable(false);
        winSupply = new WinSupply(arrTransports);
        if (isHoufflaize){
            CenterScreen.instance.start(Hex.hexTable[17][2]);
            EventPopUp.instance.show(  i18NBundle.get("Houfflaize"));
        }
        /**
         *  create the transport display
         */
/**
        int width = Gdx.graphics.getWidth();
        int height =  Gdx.graphics.getHeight() - 100;
        int i=1;
        for (final Unit unit:arrTransports) {
            final Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
            Stack stack = counter.getCounterStack().getStack();
            arrCounter.add(counter);
            ardenne.instance.guiStage.addActor(stack);
            float y = height - ((stack.getHeight() + 15)*i);
            float x = width - (stack.getWidth()+ 15);
            stack.setPosition(x, y);
            i++;
            counter.stack.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override public void clicked (InputEvent event, float x, float y)
                {
                    if (counter.getCounterStack().isHilited()) {
                        counter.getCounterStack().removeHilite();
                        unitTransportWorkOn = null;
                        cancelTransport(unit);
                    }
                    else
                    {
                        counter.getCounterStack().hilite();
   //                     if (unitTransportWorkOn != null){
   //                         cancelTransport(unit);
   //                         unitTransportWorkOn = unit;
   //                     }
      //                  counterWorkedOn = counter;
                        createHexChoice(unit);
                    }
               }
            })
       }*/
    }
    public ArrayList<Hex> createHexChoice(Unit unit, int thread, boolean isAI){
        unitTransportWorkOn = unit;
        ArrayList<Unit> arrTransport =  new ArrayList<>();
        arrTransport.addAll(Unit.getTransports(false));
        ArrayList<ArrayList> arrArrsSupply = new ArrayList<>();
        ArrayList<Hex> arrWork = new ArrayList<>();
        UnitMove unitMove = null;
        for (int i=0; i < arrTransport.size();i++) {
            unitMove = new UnitMove(arrTransport.get(i), initialRange, false, true, arrGermanSupply.get(i),0);
            arrWork.addAll(unitMove.getMovePossible());
        }
        if (isHoufflaizeOperational){
            unitMove = new UnitMove(arrTransport.get(0), initialRange, false, true, hexHouf,0);
            arrWork.addAll(unitMove.getMovePossible());
        }
        /**
         *  add stack to limit hexes
         */
        for (Hex hex:AIUtil.findHoles(arrWork,1)){
            if (hex.checkAxisInHex()){
                arrWork.add(hex);
            }
        }
        AIUtil.RemoveDuplicateHex(arrWork);
        if (!isAI) {
            hiliteHex = new HiliteHex(arrWork, HiliteHex.TypeHilite.Supply, null);
        }
        return arrWork;
    }

    private void supply(Unit unitCheck, Hex hexSupply, Unit unitTransportWorkOn, UnitMove unitMove) {

        int ix = arrUnitsSupply.indexOf(unitCheck);
        arrUnitsBeingSupplied[ix].add(unitTransportWorkOn);

        move[ix] = unitCheck.getCurrentMovement();
        supplyUnit(unitCheck);
        ArrayList<Hex> arrLeast =unitMove.getLeastPath(unitCheck.getHexOccupy(),true,null);
        final HiliteHex hiliteHex1 = new HiliteHex(arrLeast, HiliteHex.TypeHilite.None,null);
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                            hiliteHex1.remove();
                            startSupplying();
                           }


                       }, .25f
        );
        return;
    }
    private void supplyUnit(Unit unitCheck){
        unitCheck.resetCurrentMove();
        if (unitCheck.isArtillery){
            if (unitCheck.isAllies){
                unitCheck.setArtAmmo(2);
            }else{
                if (MoreGermanAmmo.instance.isMoreGermanAmmo()){
                    unitCheck.setArtAmmo(2);
                }else {
                    unitCheck.setArtAmmo(1);
                }
            }
            unitCheck.resetArtillery();
        }else{
            unitCheck.resetAttack();
        }
        unitCheck.setInSupplyThisTurn();

        unitCheck.getMapCounter().getCounterStack().setPoints();
        unitCheck.getMapCounter().getCounterStack().removeShade();
    }


    public void cancelTransport(Unit unitTransport) {
        for (int i=0; i< arrUnitsBeingSupplied.length;i++){
            arrUnitsBeingSupplied[i].remove(unitTransport);
            if (arrUnitsBeingSupplied[i].size()==0) {

                Unit unit = arrUnitsSupply.get(i);
                if (!River.instance.isEastOfOur(unit.getHexOccupy())) {
                    unit.setCurrentAttackFactor(attack[i]);
                    unit.setCurrentMovement(move[i]);
                    unit.getMapCounter().getCounterStack().setPoints();
                    unit.getMapCounter().getCounterStack().shade();
                }
            }
        }
        winSupply.reShow(unitTransport);
        if (unitTransport != null && unitTransport.isOnBoard()) {
            unitTransport.removeFromBoard();
        }

    }
    public void cancelSupplyPosition(){
        hiliteHex.remove();
    }


    /**
     * Hex chosen to put Supply Truck
     * @param hex
     */
    public void process(Hex hex){
        hiliteHex.remove();
        unitTransportWorkOn.placeOnBoard(hex);
        hex.moveUnitToFront(unitTransportWorkOn);
        ClickAction clickAction = new ClickAction(unitTransportWorkOn, ClickAction.TypeAction.Supply);
        winSupply.hideCurrent();
//        counterWorkedOn.getCounterStack().getStack().setVisible(false);
        /**
         * get surrounding hexes for selected hex
         */
        UnitMove unitMove = new UnitMove(unitTransportWorkOn, toUnit, false, true, hex,0);
        ArrayList<Hex> arrHexWork = unitMove.getMovePossible();
        for (Hex hex2:AIUtil.findHoles(arrHexWork,1)){
            if (hex2.checkAxisInHex()){
                arrHexWork.add(hex2);
            }
        }
        HexHelper.removeDupes(arrHexWork);
        if (isHoufflaize && winSupply.getNumSupplyTruck() == 1){
            if (arrHexWork.contains(Hex.hexTable[12][3])){
                winSupply.setHoufflaizeSet();
            }
        }
        /**
         *  supply units in radius
         */
        arrInRange.clear();
        for (Hex hex2:arrHexWork){
            if (hex2.checkAxisInHex()){
                for (Unit unitCheck:hex2.getUnitsInHex()){
                    if (!unitCheck.isTransport) {
                        arrInRange.add(unitCheck);
 //                       supply(unitCheck, hex, unitTransportWorkOn, unitMove);
                    }
                }
            }
        }
        if (isHoufflaize && winSupply.getNumSupplyTruck() == 1){

        }
        hex.moveUnitToFront(unitTransportWorkOn);
        loopHex = hex;
        loopUnitMove = unitMove;
        startSupplying();

        return;
    }
    ArrayList<Unit> arrInRange = new ArrayList<>();
    Hex loopHex = null;
    UnitMove loopUnitMove = null;


    private void startSupplying() {
        if (arrInRange.size() == 0){
            return;
        }else{
            Unit unitWork = arrInRange.get(0);
            arrInRange.remove(unitWork);
            supply(unitWork,loopHex,unitTransportWorkOn,loopUnitMove);
        }
    }

    HiliteHex hiWork = null;
    public ArrayList<Hex>  getunitsInRadius(Hex hex){
        if (hiWork != null){
            hiWork.remove();
        }
        UnitMove unitMove = new UnitMove(unitTransportWorkOn, toUnit, false, true, hex,0);
        ArrayList<Hex> arrHexWork = unitMove.getMovePossible();
        ArrayList<Hex> arrHexExtent = AIUtil.findHoles(arrHexWork,1);
        for (Hex hex2:arrHexExtent){
            if (hex2.checkAxisInHex()){
                arrHexWork.add(hex2);
            }
        }
        HexHelper.removeDupes(arrHexWork);
 //       hiWork = new HiliteHex(arrHexWork, HiliteHex.TypeHilite.None,null);

        if (isHoufflaize && winSupply.getNumSupplyTruck() == 1){
            if (arrHexWork.contains(Hex.hexTable[12][3])){
                winSupply.setHouffalizePossible();
            }else{
                winSupply.blinkHouffalizeOn();
            }
        }

        return arrHexWork;
    }
    private void oldSupply(){
        ArrayList<Unit> arrTransport =  new ArrayList<>();
        arrTransport.addAll(Unit.getTransports(false));
        ArrayList<ArrayList> arrArrsSupply = new ArrayList<>();
        for (int i=0; i < arrTransport.size();i++) {
            UnitMove unitMove = new UnitMove(arrTransport.get(i), initialRange, false, true, arrGermanSupply.get(i),0);
            ArrayList<Hex> arrWork = new ArrayList<>();
            arrWork.addAll(unitMove.getMovePossible());
            HexHelper.removeDupes(arrWork);
            arrArrsSupply.add(arrWork);
        }
      //  HiliteHex hiliteHex = new HiliteHex()
        ArrayList<Unit> arrUnits = Unit.getOnBoardAxis();
        HexUnits.init();
        /**
         * Create the Hex Units
         */
        for (Unit unit:arrUnits){
            UnitMove unitMove = new UnitMove(unit, toUnit, false, true,0);
            ArrayList<Hex> arrWork = new ArrayList<>();
            arrWork.addAll(unitMove.getMovePossible());
            HexHelper.removeDupes(arrWork);
            for (Hex hex:arrWork){
                HexUnits.add(hex,unit);
            }

        }
        ArrayList<Unit> arrSolution = new ArrayList<>();
        HexUnits.sortbyNumberOfUnits(HexUnits.arrHexUnits);
        for (HexUnits hU:HexUnits.arrHexUnits){

        }
    }
    public void removeHoufflaize(){
        isHoufflaizeOperational = false;
    }
    public void addHoufflaize(){
        isHoufflaizeOperational = true;
    }
    public void EndSupplyGerman(){
        /**
         *  remove Houfflaize  if put in
         */
        removeHoufflaize();
        for (Unit unit:arrTransports){
            unit.removeFromBoard();
        }
        for (Image image:arrSupplyImages){
            image.remove();
        }
        for (Unit unit:Unit.getOnBoardAxis()){
            if (unit.getCurrentMovement() == 0 && !unit.isArtillery){
                if (!unit.isMechanized){
                    unit.setCurrentMovement(3);

                }else{
                    unit.setCurrentMovement(1);
                }
                unit.getMapCounter().getCounterStack().setPoints();
            }
        }
        if (winSupply != null) {
                winSupply.end();
            }else{
                NextPhase.instance.nextPhase();
            }

        }
    public boolean getHoouflaize(){
        return isHoufflaize;
    }

    public void cancel(Unit unit) {
    }

    public void doAlliedSupply() {
        int i=0;
        if (arrAlliedSupply.size() == 0){
            EventOK.instance.addObserver(this);
            EventOK.instance.show(i18NBundle.format("noussupply"));
            return;
        }
        for (Hex hex:arrAlliedSupply){
            if (!hex.isAxisOccupied()){
                Vector2 v2 = hex.getCounterPosition();
                arrSupplyImages.get(i).setPosition(v2.x + 10,v2.y +10-7);
                arrSupplyImages.get(i).setSize(120f,120f);
                ardenne.instance.mapStage.addActor(arrSupplyImages.get(i));
                i++;
            }
        }
        Unit.initShade(true);
        TurnCounter.instance.updateText(i18NBundle.get("alliedsupply"));

        ArrayList<Unit> arrUsTransports = Unit.getTransports(true);
        UnitMove unitMove = null;
        ArrayList<Unit> arrUnitsWork =  Unit.getOnBoardAllies();
        for (Unit unit:arrUnitsWork){
            if (unit.isDisorganized()){
                unit.setOffDisorganized();
            }
            if (unit.getHasAttackedThisTurn() || unit.getHasbeenAttackedThisTurn()){
                if (!unit.isArtillery){
                    unit.reduceCombat();
                }
            }
        }

        ArrayList<Hex> arrWork = new ArrayList<>();
        for (i=0; i < arrAlliedSupply.size();i++) {
            unitTransportWorkOn = arrUsTransports.get(0);
            unitMove = new UnitMove(unitTransportWorkOn, initialRange, false, true, arrAlliedSupply.get(i),0);
            arrWork.addAll(unitMove.getMovePossible());
        }
        HexHelper.removeDupes(arrWork);
        hiliteHex = new HiliteHex(arrWork, HiliteHex.TypeHilite.SupplyAmerican, null);
        ArrayList<Unit> arrAllieas = Unit.getOnBoardAllied();
        for (Unit unit:arrAllieas){
            if (unit.isDisorganized()){
                unit.setOffDisorganized();
            }
            if (arrWork.contains(unit.getHexOccupy())){
                supplyUnit(unit);
                if (unit.getMapCounter().getCounterStack() != null){
                    unit.getMapCounter().getCounterStack().removeShade();
                }
            }else {
                if (unit.getMapCounter().getCounterStack() != null) {
                    unit.getMapCounter().getCounterStack().shade();
                }
            }

        }

    }
    public WinSupply getSupplyWindow(){
        return winSupply;
    }

    public void endSupplyUS() {
        for (Image image:arrSupplyImages){
            image.remove();
        }
        if (hiliteHex != null){
            hiliteHex.remove();
        }
        for (Unit unit:Unit.getOnBoardAllies()){

            if (unit.getCurrentMovement() == 0 && !unit.isArtillery){
                if (!unit.isMechanized){
                    unit.setCurrentMovement(3);

                }else{
                    unit.setCurrentMovement(1);
                }
                unit.getMapCounter().getCounterStack().setPoints();
            }
        }

    }

    /**
     *
     * @param isHouf
     */
    public void loadAllies(boolean isHouf) {
        arrAlliedSupply.clear();
        if (isHouf){
                arrAlliedSupply.add(Hex.hexTable[12][3]);
            }
        loadOtherUSSupply();
    }

    /**
     *  load otherUS Supply
     */

    public void loadOtherUSSupply() {
        if (NextPhase.instance.getTurn() > 2){
            for (int[] in:alliedSupply) {
                Hex hex = Hex.hexTable[in[0]][in[1]];
                arrAlliedSupply.add(hex);
                Image image = new Image(supplyIcon);
                image.setScale(.8f);
                arrSupplyImages.add(image);
            }
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        Hex hex = oB.hex;
        /**
         *  Hex touched
         */
        if (oB.type != ObserverPackage.Type.OK) {
            return;
        }
        EventOK.instance.deleteObserver(this);
        endSupplyUS();
        NextPhase.instance.nextPhase();

    }
}
