package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.AI.AIUtil;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.Losses;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Map;
import com.bruinbeargames.ardenne.ScreenGame;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

public class Hex {
        //
        // these constants are map area that has playable part
        // rest of map is informational
        //   the hex table are goes x from 0 to 76 y from 0 to 21
        //
        protected  static int xStart = 1; //
        public static int xEnd = 41;
        protected  static int yStart = 1;
        public static int yEnd = 25;
        public static Hex[][] hexTable;
        private static ArrayList<Hex> arrHexMap =  new ArrayList<Hex>();
        public static ArrayList<Hex> arrAIHex = new ArrayList<>();
        public static ArrayList<Hex> arrRoadsandPaths = new ArrayList<>();
        public static Hex hexWiltz;
        public static Hex hexBastogne1;
        public static Hex hexBastogne2;
        public static Hex hexEttlebruck;
        public static Hex hexMartelange;
        static public ArrayList<Hex> arrMajorCities = new ArrayList<>();

 //       public Hex[] tableSurroundHex = new Hex[6];
        //
        //       1  2
        //     0      3
        //       5  4

 //       private ArrayList<Hex> arrLeft = new ArrayList();
 //       private ArrayList<Hex> arrRight = new ArrayList();


    static public final int stackMax =6;
    public int xTable; // whete in hexTable this hex is
    public int yTable; //
    public boolean[] isAlliedZOC = new boolean[11];
    public boolean[] isAxisZOC = new boolean[11];
    private boolean isAlliedZOCOccupied = false;
    private boolean isAxisZOCOccupied = false;
    private boolean isAxisEntered = false;
    private boolean isAlliedEntered = false;
    private int aiScore=0;
    private int aiScoreFaker=0;
    public int aiScoreGen = 0;

    public ArrayList<Unit> arrUnitsInHex = new ArrayList<>();
    public static void initHex(){
        for (Hex hex:arrHexMap){
            hex.arrUnitsInHex = new ArrayList<>();
            hex.isAlliedOccupied[0] = false;
            hex.isAxisOccupied[0] = false;
            hex.isAxisZOC[0] = false;
            hex.isAlliedZOC[0] = false;
        }
    }
    public int getAiScore(){
        return aiScore;
    }
    public static void initAI(){
        for (Hex hex:arrHexMap){
            hex.aiScore = 0;
        }
    }
    public static void initAIFaker(){
        for (Hex hex:arrHexMap){
            hex.aiScoreFaker = 0;
        }
    }

    public void setAiScoreFaker(int aiScoreFakerNew) {
        if (aiScoreFakerNew > this.aiScoreFaker) {
            this.aiScoreFaker = aiScoreFaker;
        }
    }

    public static void initOccupied(){
        for (Hex hex:arrHexMap){
            hex.establishOccupied();
        }


    }

    public static void initThreadHex(int thread){
        for (Hex hex:arrHexMap){
            hex.isAlliedOccupied[thread] = false;
            hex.isAxisOccupied[thread] = false;
            hex.isAxisZOC[thread] = false;
            hex.isAlliedZOC[thread] = false;
        }

    }
    public static void fakeClearMoveFields(boolean isRegular, boolean isAI, int thread){
        for (Hex hex:arrHexMap){
            if (isRegular) {
                hex.isAlliedOccupied[0] = false;
                hex.isAxisOccupied[0] = false;
                hex.isAxisZOC[0] = false;
                hex.isAlliedZOC[0] = false;
            }
            if (isAI) {
                hex.isAlliedOccupied[thread] = false;
                hex.isAxisOccupied[thread] = false;
                hex.isAxisZOC[thread] = false;
                hex.isAlliedZOC[thread] = false;
            }
       }
    }
    public static void fakeRecalcZOC(boolean isRegular, boolean isAI, int thread){
        for (Hex hex:arrHexMap){
            if (isRegular) {
                hex.setZOCs();
            }
            if (isAI) {
                hex.setFakeZOC(thread);
            }
        }
    }
    public static void fakeCopyMoveFields(int threadFrom, int threadTO){
        for (Hex hex:arrHexMap) {
            hex.isAlliedOccupied[threadTO] = hex.isAlliedOccupied[threadFrom];
            hex.isAxisOccupied[threadTO] = hex.isAxisOccupied[threadFrom];
            hex.isAxisZOC[threadTO] = hex.isAxisZOC[threadFrom];
            hex.isAlliedZOC[threadTO] = hex.isAlliedZOC[threadFrom];
        }

    }

    public static ArrayList<Hex> getAllHex() {
        ArrayList<Hex> arrWork = new ArrayList<>();
        arrWork.addAll(arrHexMap);
        return arrWork;

    }


    public static ArrayList<Hex> getFakesAlliedOccupied(int thread) {
        ArrayList<Hex> arrReturn = new ArrayList<>();
        for (Hex hex:arrHexMap){
            if (hex.isAlliedOccupied[thread]){
                arrReturn.add(hex);
            }
        }
        return arrReturn;
    }
    public static ArrayList<Hex> getFakesAlliedZoc(int thread) {
        ArrayList<Hex> arrReturn = new ArrayList<>();
        for (Hex hex:arrHexMap){
            if (hex.isAlliedZOC[thread]){
                arrReturn.add(hex);
            }
        }
        return arrReturn;
    }

    public static ArrayList<Hex> getAllTownsCities() {
        ArrayList<Hex> arrReturn = new ArrayList<>();
        for (Hex hex:arrHexMap){
            if (hex.isCity){
                arrReturn.add(hex);
            }
            if (hex.isTown){
                arrReturn.add(hex);
            }
        }
        return arrReturn;

    }

    public static void initTempAI() {
        for (Hex hex:arrHexMap){
             hex.aiScoreGen = hex.aiScore;
        }

    }

    /**
     *  All hexes get an add temp score of 1 if
     *  they are not occupied by germans
     *  and they can attack germans
     *  and Germans are in a high aiscore hex
     */
    public static void addAIScoreSurroundGerman() {
        for (Hex hex:arrHexMap) {
            if (hex.aiScore > 1 && hex.isAxisOccupied()) {
                    for (Hex hexCheck : hex.getSurround()) {
                        hexCheck.aiScoreGen +=hex.aiScore;
                    }
            }
        }
    }
    public static void addAISecondPanzerLehrOccupied(){
        boolean isSecondLook = false;
        boolean isLehrLook = false;
        if (GameSetup.instance.getScenario().ordinal() > 0){
            isSecondLook = true;
            if (GameSetup.instance.getScenario().ordinal() > 1){
                isLehrLook = true;
            }
        }
        for (Hex hex:arrHexMap) {
            if (hex.aiScore > 1) {
                if (hex.isAxisOccupied()) {
                    boolean isAdd= false;
                    for (Unit unit:hex.getUnitsInHex()) {
                        if (isSecondLook && SecondPanzerExits.instance.isInSecond(unit)) {
                            isAdd = true;
                        }
                        if (isLehrLook && LehrExits.instance.isInLehr(unit)) {
                            isAdd = true;
                        }
                    }
                    if (isAdd) {
                        for (Hex hexA:hex.getSurround()) {
                            hexA.aiScoreGen++;
                        }
                    }

                }
            }
        }


    }

    public boolean isAxisEntered() {
        return isAxisEntered;
    }

    public boolean isAlliedZOC() {
        return isAlliedZOC[0];
    }

    public boolean isAxisZOC() {
        return isAxisZOC[0];
    }

    public boolean isAlliedZOCOccupied() {
        return isAlliedZOCOccupied;
    }

    public boolean isAxisZOCOccupied() {
        return isAxisZOCOccupied;
    }

    public boolean isAxisOccupied() {
        return isAxisOccupied[0];
    }

    public boolean isAlliedOccupied() {
        return isAlliedOccupied[0];
    }
    public boolean isJunction(){
        return isJunction;
    }

    public  boolean[] isAxisOccupied = new boolean[11];
    public  boolean[] isAlliedOccupied = new boolean[11];

    public boolean isHasBeenAttackedThisTurn() {
        return hasBeenAttackedThisTurn;
    }

    public void setHasBeenAttackedThisTurn(boolean hasBeenAttackedThisTurn) {
        this.hasBeenAttackedThisTurn = hasBeenAttackedThisTurn;
    }

    private boolean hasBeenAttackedThisTurn = false;

    public float getCalcMoveCost(int thread) {
        return calcMoveCost[thread];
    }

    public void setCalcMoveCost(float calcMoveCost, int thread) {
        this.calcMoveCost[thread] = calcMoveCost;
    }

    float[] calcMoveCost = new  float[11];

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    /**
     *  used by
     */
    private int range;
    public void setAI(int score){
        aiScore = score;
    }
    public Hex(int xIn, int yIn)
        {
            xTable = xIn;
            yTable = yIn;
            hexTable[xIn][yIn] = this;
        }
    public boolean checkAlliesInHex(){
        for (Unit unit:arrUnitsInHex){
            if (unit.isAllies && !unit.isEliminated()){
                return true;
            }
        }
        return false;
    }
    public boolean checkAxisInHex(){
        for (Unit unit:arrUnitsInHex){
            if (unit.isAxis && !unit.isEliminated()){
                return true;
            }
        }
        return false;
    }
    public void moveUnitToBack(Unit unit) {
        if (!unit.isEliminated() && !arrUnitsInHex.contains(unit)) {
            new ErrorGame("Move To Back unit that is not present", this);
        }
        arrUnitsInHex.remove(unit);
        arrUnitsInHex.add(unit);

    }

    public void enterHex(Unit unit) {
        /**
         * Add unit as first entry in the array
         * It has to match the display first unit will be on top
         * Changes for Mobile Assualt
         */
 //      Gdx.app.log("Hex","enterHex unit="+unit+" Hex= "+this+" id="+unit.getID());
        if (unit.isAxis){
            isAxisEntered = true;
        }
        if (unit.isAllies){
            isAxisEntered = false;
        }
        arrUnitsInHex.add(0, unit);
        if (unit.isAxis && !checkAlliesInHex()) {
            isAxisOccupied[0] = true;
            isAlliedOccupied[0] = false;
        }
        if (unit.isAllies && !checkAxisInHex()) {
            isAlliedOccupied[0] = true;
            isAxisOccupied[0] = false;
        }
        moveUnitToBack(unit);
        if (xTable == 40 && yTable ==22){
            int bg =0;
        }
        setZOCs();

//        this is the hard move for unit
        unit.moveForHexProcessing(this);
    }
    public boolean leaveHex(Unit unit) {
 //       Gdx.app.log("Hex","leaveHex unit="+unit+" Hex= "+this+" id="+unit.getID());
       if (xTable ==25 && yTable ==2){
           int bk =0;
       }
        if (arrUnitsInHex.contains(unit)) {
            arrUnitsInHex.remove(unit);
        } else {
            if (unit.isEliminated()){
            }else {
                return false;
            }
        }
        establishOccupied();
        setZOCs();
        return true;
    }

    /**
     * to be invoked after arrUnits in hex set
     * do this for all surrounding hexes as well
     */
    public void setZOCs() {
        ArrayList<Hex> arrHexCheck = getSurround();
        arrHexCheck.add(this);
        for (Hex hexCheck:arrHexCheck){
            ArrayList<Hex> arrSurround = hexCheck.getSurround();
            hexCheck.isAlliedZOC[0] = false;
            hexCheck.isAxisZOC[0] = false;
            hexCheck.isAxisZOCOccupied = false;
            hexCheck.isAlliedZOCOccupied = false;
            if (hexCheck.xTable == 35 && hexCheck.yTable == 22){
                int bk=0;
            }

            for (Hex hex:arrSurround){
                if ((hexCheck.isStreamBank() && hex.isStreamBank()) &&
                        !Bridge.isBridge(hexCheck,hex) && River.instance.isStreamBetween(hex,hexCheck)){
                    // do nothing
                }else{
                    for (Unit unit:hex.getUnitsInHex()) {
                        if (unit.isAllies && unit.isExertZOC()) {
                            hexCheck.isAlliedZOC[0] = true;
                        }
                        if (unit.isAxis && unit.isExertZOC()) {
                            hexCheck.isAxisZOC[0] = true;
                        }
                    }
                }
            }
        }
        for (Unit unit:arrUnitsInHex){
            if (unit.isAxis && isAlliedZOC[0]){
                isAlliedZOCOccupied = true;
            }else if(unit.isAllies && isAxisZOC[0]){
                isAxisZOCOccupied = true;
            }
        }
    }
    public void setFakeZOC(int thread){
        ArrayList<Hex> arrHexCheck = getSurround();
        arrHexCheck.add(this);
        for (Hex hexCheck:arrHexCheck){
            ArrayList<Hex> arrSurround = hexCheck.getSurround();
            hexCheck.isAlliedZOC[thread] = false;
            hexCheck.isAxisZOC[thread] = false;
            for (Hex hex:arrSurround){

                if ((hexCheck.isStreamBank() && hex.isStreamBank()) &&
                        !Bridge.isBridge(hexCheck,hex) && River.instance.isStreamBetween(hex,hexCheck)){
                    // do nothing
                }else{
                    for (Unit unit:hex.getUnitsInHex()) {
                        if (hex.isAlliedOccupied[thread]  && unit.isExertZOC()) {
                            hexCheck.isAlliedZOC[thread] = true;
                        }
                        if (hex.isAxisOccupied[thread] && unit.isExertZOC()) {
                            hexCheck.isAxisZOC[thread] = true;
                        }
                    }
                }
            }
        }

    }
    public void resetZOC(int thread){
        isAlliedZOC[thread] = false;
        isAxisZOC[thread] = false;
    }
    public boolean getAlliedZoc(int thread){
        return isAlliedZOC[thread];
    }
    public boolean getAxisZoc(int thread){
        return isAxisZOC[thread];
    }

    /**
     *  for Mobile assault  after a unit leaves
     *  set flags who occupeid it
     */
    public void establishOccupied(){
        /**
         *  No default
         */
        isAxisOccupied[0] = false;
        isAlliedOccupied[0] = false;
        if (checkAxisInHex()){
            isAxisOccupied[0] = true;
            isAlliedOccupied[0] = false;
        }
        if (checkAlliesInHex()){
            isAlliedOccupied[0] = true;
            isAxisOccupied[0] = false;
        }

    }


    /**
     * Get all the units in the hex
     *
     * @return array of units in hex
     */
    public ArrayList<Unit> getUnitsInHex() {
        ArrayList<Unit> arrRetrun = new ArrayList<>();
        arrRetrun.addAll(arrUnitsInHex);
        return arrRetrun;
    }
    public int getAttackPointsInHex(){
        int spReturn =0;
        for (Unit unit:getUnitsInHex()){
            spReturn +=unit.getCurrenAttackFactor();
        }
        return spReturn;
    }
    public int getDefensePointsInHex(){
        int spReturn =0;
        for (Unit unit:getUnitsInHex()){
            spReturn +=unit.getCurrentDefenseFactor();
        }
        return spReturn;
    }
    /**
     * Put this unit to front of  the stack   make sure to do a rePlace on the counters
     * @param unit
     */
    public void moveUnitToFront(Unit unit) {
        if (!unit.isEliminated()){
            if (!arrUnitsInHex.contains(unit)) {
                new ErrorGame("Move To Front unit that is not present", this);
            }
        }
        arrUnitsInHex.remove(unit);
        arrUnitsInHex.add(0,unit);
        Counter.rePlace(this);

    }
    /**
     * put first unit to back
     */
    public void cycleUnits() {
        if (arrUnitsInHex.size() <= 1) {
            return;
        }
        Unit unit = arrUnitsInHex.get(0);
        arrUnitsInHex.remove(0);
        arrUnitsInHex.add(unit);
        Counter.rePlace(this);

    }

    public int getStacksIn() {
        int stackTotal = 0;
        for (Unit unit:arrUnitsInHex){
            stackTotal += unit.getCurrentStep();
        }
        return stackTotal;
    }

    public int getAttackPointIn() {
        int attackTotal = 0;
        for (Unit unit:arrUnitsInHex){
            if (!unit.isArtillery) {
                attackTotal += unit.getCurrenAttackFactor();
            }
        }
        return attackTotal;
    }




    /**
         * Use current Hex
         * @return vector2 pointing at bottom right point
         */
        public Vector2 GetDisplayCoord()
        {
            Polygon poly = Map.GetBackPoly(this);
            float[] vertices = poly.getVertices();
            Vector2 v1 = new Vector2(vertices[10],vertices[11]);
            Vector2 v2 = Map.BackToWorld(v1);
//		v2.y -= 30;
//		v2.x += 50;
            /**
             *  fudge because of map
             */
            float xAdjust = ((41f-xTable)/41f)*8.7f;
  //          xAdjust += 4f - ((xTable/xEnd) * 4f);
            xAdjust += 4f - ((xTable/xEnd) * 104f);
//            v2.x -=xAdjust;
//            v2.x -=xAdjust;
            v2.y -=4;
            return v2;

        }
        public Vector2 getCounterPosition(){
            Vector2 pos = GetDisplayCoord();
            pos.x += 14;
            pos.y +=14;
            return pos;
        }
        public Vector2 GetMidPoint()
        {
            Polygon poly = Map.GetBackPoly(this);
            float[] vertices = poly.getVertices();
            Vector2 v1 = new Vector2();
            v1.y = (vertices[0] + vertices[6]) / 2;
            v1.x = vertices[0];
            return v1;

        }

        public Vector2 GetDisplayCoordHex()
        {
            Polygon poly = Map.GetBackPoly(this);
            float[] vertices = poly.getVertices();
            Vector2 v1 = new Vector2(vertices[2],vertices[3]);
            Vector2 v2 = Map.BackToWorld(v1);
            v2.y -= 70; //65
            v2.x -= 6; //10

            return v2;

        }
        public String toString()
        {
            return new String(xTable+ " "+yTable);
        }
        /**
         *  Get Hex pointed to by screen location
         * @param x
         * @param y
         * @return Hex
         */
        static public Hex GetHexFromScreenPosition(float x, float y)
        {

            Vector3 worldCoordinates = ScreenGame.instance.GetCamera().unproject(new Vector3(x,y,0));
            Vector2 world = new Vector2(worldCoordinates.x, worldCoordinates.y);
            Vector2 back =  Map.WorldToBack(world);
            Gdx.app.log("Hex", "Get Hex looking at x ="+back.x+" y="+back.y);

            Vector2 pHex = Map.ConvertToHex(back);
            int xInt = (int)pHex.x;
            int yInt = (int)pHex.y;
            Hex hex = Hex.hexTable[xInt][yInt];
            return hex;
        }

    public boolean isRoad() {
        return isRoad;
    }


    public boolean isClear() {
        return isClear;
    }

    public boolean isTown() {
        return isTown;
    }

    public boolean isCity() {
        return isCity;
    }

    public boolean isPath() {
        return isPath;
    }

    public boolean isStreamBank() {
        return isStreamBank;
    }

    public boolean isForest() {
        return isForest;
    }

    /**
     *  the road or path network this Hex os on
     */
    public ArrayList<Integer> arrConnections = new ArrayList<Integer>();
        boolean isRoad = false;
        boolean isRoad1 = false;
        boolean isRoad2 = false;
        boolean isClear = true;
        boolean isTown = false;
        boolean isCity = false;
        boolean isPath = false;
        boolean isStreamBank = false;
        boolean isForest = false;
        boolean hasBridge = false;
        boolean isJunction = false;
        public ArrayList<Hex> arrSurroundHex = new ArrayList<Hex>();



        public static void loadHexes()
        {
            hexTable = new Hex[xEnd][yEnd];
            for (int x=0; x< xEnd; x++)
            {
                for (int y=0; y < yEnd; y++)
                {
                    hexTable[x][y] = new Hex(x, y);
                    arrHexMap.add(hexTable[x][y]);
                }
            }
            LoadRoads();
            LoadPaths();
            LoadCities();
            LoadTowns();
            LoadForest();
            LoadRiversStream();
            LoadSurround(); // must before bridgess
            LoadBridges();
            loadJunctions();
            for (Hex hex:arrHexMap){
                if (hex.isJunction || hex.isTown){
                    arrRoadsandPaths.add(hex);
                }
            }
            AIUtil.RemoveDuplicateHex(arrRoadsandPaths);
            loadAIHexesToCheck();
            hexBastogne1 = hexTable[8][11];
            arrMajorCities.add(hexBastogne1);
            hexBastogne2 = hexTable[8][12];
            arrMajorCities.add(hexBastogne2);
            hexWiltz = hexTable[19][14];
            arrMajorCities.add(hexWiltz);
            hexEttlebruck = hexTable[28][23];
            arrMajorCities.add(hexEttlebruck);
            hexMartelange = hexTable[9][23];
            arrMajorCities.add(hexEttlebruck);


        }

    private static void loadAIHexesToCheck() {
            arrAIHex.clear();
            for (Hex hex:arrHexMap){
                if (hex.isPath() || hex.isRoad() || hex.isCity || hex.isTown ){
                    arrAIHex.add(hex);
                    arrAIHex.addAll(hex.getSurround());
                }
            }
            AIUtil.RemoveDuplicateHex(arrAIHex);
    }

    /**
     *  return an array of hexes that have AISCoreGen greater ahn
     *  the minimum
     * @param minAiScore
     * @return
     */
    public static ArrayList<Hex> loadAIHexes(int minAiScore) {
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        for (Hex hex:arrHexMap){
            if (hex.aiScoreGen > minAiScore){
                arrHexReturn.add(hex);

            }
        }
        AIUtil.RemoveDuplicateHex(arrHexReturn);
        return arrHexReturn;
    }

    public static void initCombatFlags(){
            for (int x=0; x< xEnd; x++)
            {
                for (int y=0; y < yEnd; y++)
                {
                    hexTable[x][y].hasBeenAttackedThisTurn = false;
                }
            }
        }
    public static ArrayList<Hex> checkStacking(){
        I18NBundle i18NBundle;
        boolean isLosses = false;
         ArrayList<Hex> arrReturn = new ArrayList<>();
        for (int x=0; x< xEnd; x++)
        {
            for (int y=0; y < yEnd; y++)
            {
                if (x==29 & y==24){
                    int b=0;
                }
                if (hexTable[x][y].getStacksIn()> stackMax){
                    Hex hex=hexTable[x][y];
                    Losses losses = new Losses(hex.getUnitsInHex(), hex.getStacksIn() - stackMax );
                    isLosses =true;
                    arrReturn.add(hex);
                }
            }
        }
        if (isLosses){
            i18NBundle = GameMenuLoader.instance.localization;
            EventPopUp.instance.show(i18NBundle.format("removingoverstacks"));
        }
        return arrReturn;
    }

    public static  ArrayList<Hex> getAttackedThisTurn(){
            ArrayList<Hex> arrReturn = new ArrayList<>();
            for (int x=0; x< xEnd; x++)
            {
                for (int y=0; y < yEnd; y++)
                {
                    if (hexTable[x][y].hasBeenAttackedThisTurn){
                        arrReturn.add(hexTable[x][y]);
                    }
                }
            }
            return arrReturn;
        }

    private static void LoadSurround() {
        for (int x=0; x< xEnd; x++)
        {
            for (int y=0; y < yEnd; y++)
            {
                Hex hex = hexTable[x][y];
                hex.arrSurroundHex = HexHandler.getSurroundArr(hex);
            }
        }
    }
    public static void loadCalcMoveCost(int thread) {
        for (int x=0; x< xEnd; x++)
        {
            for (int y=0; y < yEnd; y++)
            {
                Hex hex = hexTable[x][y];
                for (int i=0; i <11; i++) {
                    hex.calcMoveCost[thread] = 0;
                }
            }
        }
    }

    public ArrayList<Hex> getSurround(){
        ArrayList<Hex> arrReturn = new ArrayList<Hex>();
        arrReturn.addAll(arrSurroundHex);
        return arrReturn;
}


    private static void LoadRiversStream()
        {
            ArrayList<ArrayList> arrArrA = new ArrayList<ArrayList>();
            ArrayList<ArrayList> arrArrB = new ArrayList<ArrayList>();
          //  River river = new River(true); // if stream set to true;
            ArrayList<Hex> arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i <stream1A.length; i++)
            {
                int x = stream1A[i][0];
                int y = stream1A[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "1A";
            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i <stream1B.length; i++)
            {

                int x = stream1B[i][0];
                int y = stream1B[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "1B";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i <stream2a.length; i++)
            {
                int x = stream2a[i][0];
                int y = stream2a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "2A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i <stream2b.length; i++)
            {
                int x = stream2b[i][0];
                int y = stream2b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "2B";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i <stream3a.length; i++)
            {
                int x = stream3a[i][0];
                int y = stream3a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "3A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i <stream3b.length; i++)
            {
                int x = stream3b[i][0];
                int y = stream3b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "3B";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i <stream4a.length; i++)
            {
                int x = stream4a[i][0];
                int y = stream4a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "4A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i <stream4b.length; i++)
            {
                int x = stream4b[i][0];
                int y = stream4b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "4b";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i < stream5a.length; i++)
            {
                int x = stream5a[i][0];
                int y = stream5a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "5A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i < stream5b.length; i++)
            {
                int x = stream5b[i][0];
                int y = stream5b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "5B";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i < stream6a.length; i++)
            {
                int x = stream6a[i][0];
                int y = stream6a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "6A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i < stream6b.length; i++)
            {
                int x = stream6b[i][0];
                int y = stream6b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "6B";

            }
            arrHex = new ArrayList<Hex>();
            arrArrA.add(arrHex);
            for (int i=0; i < stream7a.length; i++)
            {
                int x = stream7a[i][0];
                int y = stream7a[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "7A";

            }
            arrHex = new ArrayList<Hex>();
            arrArrB.add(arrHex);
            for (int i=0; i < stream7b.length; i++)
            {
                int x = stream7b[i][0];
                int y = stream7b[i][1];
                Hex hex= hexTable[x][y];
                arrHex.add(hex);
                hex.isStreamBank = true;
                hex.riverStr = "7B";

            }

            River river = new River(arrArrA,arrArrB);
        }
        public String riverStr;



        private static void LoadForest() {
            for (int i=0; i <forest.length; i++)
            {
                int x = forest[i][0];
                int y = forest[i][1];
                Hex hex= hexTable[x][y];
                hex.isForest = true;
            }


        }


        private static void LoadTowns() {
            for (int i=0; i <town.length; i++)
            {
                int x = town[i][0];
                int y = town[i][1];
                Hex hex= hexTable[x][y];
                hex.isTown = true;
            }

        }

    private static void LoadCities() {
        for (int i=0; i <cities.length; i++)
        {
            int x = cities[i][0];
            int y = cities[i][1];
            Hex hex= hexTable[x][y];
            hex.isCity = true;
        }

    }


        private static void LoadPaths() {
            for (int i = 0; i < path.length; i++)
            {
                int x = path[i][0];
                int y = path[i][1];
                Hex hex= hexTable[x][y];
                hex.isPath = true;
            }

        }

    public static void LoadBridges()
        {
            Bridge.arrBridges = new ArrayList<Bridge>();

            Bridge.createNoBridgeArray();
            for (int x=0; x< xEnd; x++)
            {
                for (int y=0; y < yEnd; y++)
                {
                    if (x==29 && y ==22){
                        int bk=0;
                    }
                    Hex hex = hexTable[x][y];
                    Bridge.checkCanCreateBridges(hex);
                }
            }
       }
       private static void loadJunctions(){
           for (int x=0; x< xEnd; x++)
           {
               for (int y=0; y < yEnd; y++)
               {
                   Hex hex = hexTable[x][y];
                   if (hex.arrConnections.size() > 1){
                       hex.isJunction = true;
                   }
               }
           }
           Hex.hexTable[4][19].isJunction = true;
           Hex.hexTable[19][19].isJunction = true;
           Hex.hexTable[27][8].isJunction = true;

       }
       public void updateBridge(){
            hasBridge = true;
       }

    /**
     *  to check if roads connected just check hexTo and hexFrom with some exceptios
     */
    static public boolean isRoadConnection(Hex startHex, Hex endHex){
        if (!startHex.getSurround().contains(endHex)){
            return false;
        }
        if (!(startHex.isRoad && endHex.isRoad)){
            return false;
        }
        /**
         *  checks here for cases
         */
        return true;
    }
    static public boolean isPathConnection(Hex startHex, Hex endHex){
        Hex hex11 = Hex.hexTable[33][12];
        Hex hex12= Hex.hexTable[34][12];
        Hex hex21= Hex.hexTable[32][03];
        Hex hex22 =Hex.hexTable[32][04];
        if (endHex == Hex.hexTable[29][12] && startHex == Hex.hexTable[30][13] ){
            int bb = 0;
        }

        if (!startHex.getSurround().contains(endHex)){
            return false;
        }
        if (!(startHex.isPath && endHex.isPath)){
            return false;
        }
        if ((startHex == hex11 && endHex == hex12) ||
            (startHex ==hex12 &&  endHex == hex11)){
            return false;
        }
        if ((startHex == hex21 && endHex == hex22) ||
                (startHex ==hex22 &&  endHex == hex21)){
            return false;
        }
        for (Integer in:startHex.arrConnections){
            if (endHex.arrConnections.contains(in)){
                return true;
            }
        }
        /**
         *  checks here for cases
         */
        return false;
    }



    private static void LoadRoads() {
            for (int i=0; i <roads.length; i++)
            {
                int x = roads[i][0];
                int y = roads[i][1];
                Hex hex= hexTable[x][y];
                hex.isRoad = true;
            }
            for (int i=0; i <path0.length; i++)
            {
                int x = path0[i][0];
                int y = path0[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(0)){
                    hex.arrConnections.add(0);
                }
            }
            for (int i = 0; i < path1.length; i++)
            {
                int x = path1[i][0];
                int y = path1[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;

                if (!hex.arrConnections.contains(1)){
                    hex.arrConnections.add(1);
                }

            }
            for (int i = 0; i < path2.length; i++)
            {
                int x = path2[i][0];
                int y = path2[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(2)){
                    hex.arrConnections.add(2);
                }

            }
            for (int i = 0; i < path3.length; i++)
            {
                int x = path3[i][0];
                int y = path3[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(3)){
                    hex.arrConnections.add(3);
                }

            }
            for (int i = 0; i < path4.length; i++)
            {
                int x = path4[i][0];
                int y = path4[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(4)){
                    hex.arrConnections.add(4);
                }

            }
            for (int i = 0; i < path5.length; i++)
            {
                int x = path5[i][0];
                int y = path5[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(5)){
                    hex.arrConnections.add(5);
                }

            }
            for (int i = 0; i < path6.length; i++)
            {
                int x = path6[i][0];
                int y = path6[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(6)){
                    hex.arrConnections.add(6);
                }

            }
            for (int i = 0; i < path7.length; i++)
            {
                int x = path7[i][0];
                int y = path7[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(7)){
                    hex.arrConnections.add(7);
                }

            }
            for (int i = 0; i < path8.length; i++)
            {
                int x = path8[i][0];
                int y = path8[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(8)){
                    hex.arrConnections.add(8);
                }

            }
            for (int i = 0; i < path9.length; i++)
            {
                int x = path9[i][0];
                int y = path9[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(9)){
                    hex.arrConnections.add(9);
                }

            }
            for (int i = 0; i < path10.length; i++)
            {
                int x = path10[i][0];
                int y = path10[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(10)){
                    hex.arrConnections.add(10);
                }

            }
            for (int i = 0; i < path11.length; i++)
            {
                int x = path11[i][0];
                int y = path11[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(11)){
                    hex.arrConnections.add(11);
                }

            }
            for (int i = 0; i < path12.length; i++)
            {
                int x = path12[i][0];
                int y = path12[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(12)){
                    hex.arrConnections.add(12);
                }

            }
            for (int i = 0; i < path13.length; i++)
            {
                int x = path13[i][0];
                int y = path13[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(13)){
                    hex.arrConnections.add(13);
                }

            }
            for (int i = 0; i < path14.length; i++)
            {
                int x = path14[i][0];
                int y = path14[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(14)){
                    hex.arrConnections.add(14);
                }

            }
            for (int i = 0; i < path15.length; i++)
            {
                int x = path15[i][0];
                int y = path15[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(15)){
                    hex.arrConnections.add(15);
                }

            }
            for (int i = 0; i < path16.length; i++)
            {
                int x = path16[i][0];
                int y = path16[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(16)){
                    hex.arrConnections.add(16);
                }

            }
            for (int i = 0; i < path18.length; i++)
            {
                int x = path18[i][0];
                int y = path18[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(18)){
                    hex.arrConnections.add(18);
                }

            }
            for (int i = 0; i < path19.length; i++)
            {
                int x = path19[i][0];
                int y = path19[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(19)){
                    hex.arrConnections.add(19);
                }

            }
            for (int i = 0; i < path20.length; i++)
            {
                int x = path20[i][0];
                int y = path20[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(20)){
                    hex.arrConnections.add(20);
                }

            }
            for (int i = 0; i < path21.length; i++)
            {
                int x = path21[i][0];
                int y = path21[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(21)){
                    hex.arrConnections.add(21);
                }

            }
            for (int i = 0; i < path22.length; i++)
            {
                int x = path22[i][0];
                int y = path22[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(22)){
                    hex.arrConnections.add(22);
                }

            }
            for (int i = 0; i < path23.length; i++)
            {
                int x = path23[i][0];
                int y = path23[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(23)){
                    hex.arrConnections.add(23);
                }

            }
            for (int i = 0; i < path24.length; i++)
            {
                int x = path24[i][0];
                int y = path24[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(24)){
                    hex.arrConnections.add(24);
                }

            }
            for (int i = 0; i < path26.length; i++)
            {
                int x = path26[i][0];
                int y = path26[i][1];

                Hex hex= hexTable[x][y];
                hex.isPath = true;
                if (!hex.arrConnections.contains(26)){
                    hex.arrConnections.add(26);
                }

            }
        for (int i = 0; i < path27.length; i++)
        {
            int x = path27[i][0];
            int y = path27[i][1];

            Hex hex= hexTable[x][y];
            hex.isPath = true;
            if (!hex.arrConnections.contains(26)){
                hex.arrConnections.add(26);
            }

        }









        }
    static int[][] path0={{12,00},{12,01},{12,02},{12,03},{12,04},{12,05},{11,05},{10,06},{10,07},{10,8},{10,9},{9,9},{9,10},{8,11},{8,12},{8,13},{8,14},{07,14},{06,15},{06,16},{06,17},{06,18},{07,18},{07,19},{07,20},{8,21},{8,22},{8,23},{9,23},{9,24},};
    static int[][] path1 ={{12,16},{00,8},{01,8},{02,9},{03,9},{04,10},
            {05,10},{06,11},{07,11},{8,12},{9,11},{10,11},{11,10},{12,11},{13,10},
            {14,10},{15,9},{16,10},{17,9},{18,9},{19,8},{20,8},{20,07},{21,06},
            {21,05},{22,05},{21,06},{23,04},{24,04},{24,03},{25,02},{26,02},{27,01},
            {27,00},{28,00},{27,02},{28,03},{28,04},{28,05},{28,06},{27,06},{27,07},
            {27,8},{27,9},{28,10},{28,11},{28,11},{28,12},{28,13},{28,14},{28,15},
            {27,15},{27,16},{27,17},{28,18},{28,19},{29,19},{30,20},{30,21},{30,22},
            {29,22},{28,23},{28,24},{00,00},{28,23},};
    static int[][] path2={{02,9},{02,8},{03,07},{04,07},{05,06},{06,06},{07,05},{8,06},{9,05},{10,05},{11,04},{11,03},{12,03},{12,02},{13,01},{14,01},{14,00},{00,00},};
    static int[][] path3={{06,06},{05,05},{05,04},{04,04},{04,03},{03,02},{02,02},{01,01},{00,01},{00,00},{07,06},{07,07},{07,8},{8,9},{8,10},{8,11}};
    static int[][] path4={{31,21},{31,21},{32,22},{33,22},{34,22},{35,22},{36,23},{37,22},{38,23},{39,22},{40,22},{30,21}};
    static int[][] path5={{36,23},{36,23},{35,23},{34,24},{33,24},};
    static int[][] path6={{31,21},{31,21},{31,21},{32,21},{33,21},{34,21},{35,20},{36,20},{36,21},{37,21},{38,21},{39,20},{40,20},};
    static int[][] path7={{36,18},{37,17},{37,16},{38,16},{38,15},{39,14},{40,14},};
    static int[][] path8={{36,18},{36,18},{35,17},{34,17},{33,17},{32,18},{32,19},{32,20},{32,21},};
    static int[][] path9={{37,18},{37,19},{38,20},{38,21},{36,18},{36,18},{36,17},{35,16},{34,16},{34,15},{34,14},{34,13},{34,12},{35,11},{36,11},{37,10},{37,9},{36,9},{36,9},{36,8},{36,07},{37,06},{38,07},};
    static int[][] path10={{8,12},{07,12},{06,13},{05,13},{04,14},{03,14},{02,15},{02,16},{02,17},{02,18},{01,18},{00,19},{0,20}};
    static int[][] path11={{3,24},{02,18},{02,18},{03,18},{04,19},{04,20},{04,21},{03,21},{03,22},{03,23},};
    static int[][] path12={{04,21},{04,21},{05,21},{06,22},{07,22},{07,23},{07,22},{8,24},
            {9,23},{10,23},{11,22},{12,22},{13,21},{14,21},{14,20},{15,19},{16,19},{17,18},{18,19},{19,19},{19,18},{20,18},{20,17},{20,16},{20,15},{19,14},
            {18,15},{18,16},{17,16},{16,17},{16,18},{15,18},{14,18},{13,17},{12,17},
            {11,16},{10,16},{9,15},{9,14},{9,13},{8,13},{19,20},{4,24},{5,23},{6,24},{3,23}};
    static int[][] path13={{28,11},{28,13},{27,13},{26,14},{25,14},{24,15},{23,14},{22,15},{21,14},{20,14},{19,14},};
    static int[][] path14={{38,00},{37,00},{36,01},{36,02},{35,02},{34,02},{33,02},{32,03},{31,03},{32,04},{32,05},{32,06},{31,06},{31,07},{30,8},{30,9},{29,9},{28,9},{27,8},};
    static int[][] path15={{30,8},{30,8},{31,8},{32,8},{33,07},{34,07},{35,07},{36,07},};
    static int[][] path16={{33,0},{33,1},{33,02},{34,03},{35,03},{36,03},{37,03},{37,04},{38,04},{39,04},{39,05},{40,05},};
    static int[][] path18={{28,23},{28,23},{27,22},{26,22},{25,22},{24,22},{23,22},{22,22},{21,21},{21,20},{20,20},{19,20},{18,21},{17,20},{16,21},{16,22},{16,23},{15,23},{14,23},{13,23},{12,23},{11,22},};
    static int[][] path19={{16,22},{16,22},{17,22},{17,23},{18,24},{19,24},};
    static int[][] path20={{19,24},{19,24},{20,24},{21,24},{22,24},{23,24},{24,24},{25,23},{26,24},{27,23},{28,23},};
    static int[][] path21={{04,18},{04,18},{04,17},{04,16},{05,15},{05,14},{05,13},};
    static int[][] path22={{8,13},{8,13},{10,13},{11,12},{12,12},{13,12},{14,13},{15,13},{15,14},{16,15},{17,15},{18,16},{18,16},};
    static int[][] path23={{19,14},{19,14},{19,13},{18,13},{18,12},{17,11},{17,10},{17,9},};
    static int[][] path24={{14,10},{14,10},{14,9},{14,8},{13,07},{12,07},{11,07},{10,07}};
    static int[][] path26={{21,06},{21,06},{22,07},{23,06},{24,07},{24,8},{25,8},{26,8},{27,8},};
    static int[][] path27={{34,12},{34,12},{33,11},{33,12},{32,13},{31,13},{30,13},{29,12},{29,11},{28,11},};

    static int[][] roads = {{9,24},{9,23},{8,23},{8,22},{8,21},{07,20},{07,19},{07,18},{06,18},{06,17},{06,16},
            {06,15},{07,14},{8,14},{8,13},{8,13},{8,12},{07,11},{06,11},{05,10},{04,10},{03,9},{02,9},
            {01,8},{00,8},{00,8},{9,10},{9,9},{10,9},{10,8},{10,07},{10,06},{11,05},{12,05},{12,04},{12,03},
            {12,02},{12,01},{12,01},{12,00},{9,11},{10,11},{11,10},{12,11},{13,10},{14,10},{15,9},{16,10},{17,9},
            {18,9},{19,8},{20,8},{20,07},{21,06},{21,06},{21,05},{22,05},{23,04},{24,04},{24,03},{25,02},{26,02},
            {27,01},{27,00},{28,00},{28,00},{27,02},{28,03},{28,04},{28,05},{28,06},{27,06},{27,07},{27,8},{26,8},
            {25,8},{24,8},{24,07},{23,06},{22,07},{27,8},{27,9},{28,10},{28,11},{28,11},{28,12},{28,13},{28,14},
            {28,15},{27,15},{27,16},{27,17},{28,18},{28,19},{29,19},{30,20},{30,21},{30,21},{29,22},{30,22},{28,24},
            {28,23},{00,00},};
    static int[][] path = {{31,13},{6,13},{7,12},{8,12},{00,00},{00,01},{01,01},{02,02},{03,02},{04,03},{04,04},{05,04},{05,05},{06,06},{07,06},
            {07,07},{07,8},{8,9},{8,10},{8,11},{02,9},{02,8},{03,07},{04,07},{05,06},{06,06},{07,05},{8,06}
            ,{9,05},{10,05},{11,04},{11,03},{12,03},{12,02},{13,01},{14,01},{14,00},{10,07},{11,07},{12,07},{13,07}
            ,{14,8},{14,9},{14,10},{17,9},{17,10},{17,11},{17,11},{18,12},{18,13},{19,13},{19,14},{20,14},{21,14}
            ,{22,15},{23,14},{24,15},{25,14},{18,15},{18,16},{17,15},{16,15},{15,14}
            ,{15,13},{14,13},{13,12},{12,12},{11,12},{10,13},{9,12},{8,13},{9,13},{9,14},{9,15},{10,16},
            {11,16},{12,17},{13,17},{14,18},{15,18},{16,18},{16,17},{17,16},{18,16},{18,15},{19,14},{20,15},{20,16}
            ,{20,17},{20,18},{19,18},{19,19},{18,19},{17,18},{16,19},{15,19},{14,20},{14,21},{13,21},{12,22},{11,22}
            ,{11,22},{12,23},{13,23},{14,23},{15,23},{16,23},{17,23},{17,22},{16,22},{16,21},{17,20},{18,21},{19,20}
            ,{20,20},{21,20},{21,21},{22,22},{23,22},{24,22},{24,24},{23,24},{22,24},{21,24},{20,24},{19,24},{18,24}
            ,{17,23},{17,22},{16,22},{16,21},{17,20},{18,21},{19,20},{16,23},{15,23},{14,23},{13,23},{12,23},{11,22}
            ,{10,23},{9,23},{8,24},{07,23},{07,22},{06,24},{05,23},{04,24},{03,23},{03,24},{03,23},{03,22},{03,21}
            ,{04,21},{05,21},{06,22},{07,22},{04,20},{04,19},{04,18},{04,17},{04,16},{05,15},{05,14},{05,13},{04,14}
            ,{03,14},{02,15},{02,16},{02,17},{02,18},{01,18},{00,19},{00,20},{22,24},{23,24},{24,24},{25,23},{26,24}
            ,{27,23},{28,23},{27,22},{26,22},{25,22},{24,22},{23,22},{22,22},{21,21},{21,20},{20,14},{21,14},{22,15}
            ,{23,14},{24,15},{25,14},{26,14},{27,13},{28,13},{31,21},{32,22},{33,22},{34,22},{35,22},{36,23},{37,22}
            ,{38,23},{39,22},{40,22},{35,23},{34,24},{33,24},{32,21},{33,21},{34,21},{35,20},{36,20},{36,21},{37,21}
            ,{38,21},{39,20},{40,20},{38,20},{37,19},{37,18},{36,18},{35,17},{34,17},{33,17},{32,18},{32,19},{32,20}
            ,{32,21},{37,17},{37,16},{38,16},{38,15},{39,14},{40,14},{36,17},{35,16},{34,16},{34,15},{34,14},{34,13}
            ,{32,13},{33,12},{30,13},{34,12},{33,11},{33,12},{32,13},{35,11},{36,11},{37,10},{37,9},{36,9}
            ,{36,8},{36,07},{37,06},{38,07},{35,07},{34,07},{33,07},{32,8},{31,8},{30,9},{30,8},{31,07},{31,06}
            ,{32,06},{32,05},{32,04},{31,03},{32,03},{33,02},{33,01},{33,00},{00,00},{34,02},{35,02},{36,02},{36,01}
            ,{37,00},{38,00},{00,00},{34,03},{35,03},{36,03},{37,03},{37,04},{38,04},{39,04},{39,05},{40,05},{27,8}
            ,{28,9},{29,9},{30,9},};
    static int[][] stream1A = {{40,23},{40,22},{39,21},{38,22},{37,21},{36,21},{36,20},{35,19},{35,18},{35,17},{34,17},{34,16},{34,15},{33,14},{32,15},{32,14},{32,13},{31,12},{31,11},
            {31,10},{31,9},{30,9},{30,8},{30,07},{31,06},{31,05},{31,04},{31,03},{31,02},{32,02},{32,01},{32,00}};
    static int[][] stream1B = {{31,00},{31,01},{30,02},{30,03},{30,04},{30,05},{30,06},{29,06},{29,07},{29,8},{29,9},{30,10},{30,11},{30,12},{30,13},{31,13},{31,14},{31,15},{32,16},
            {33,15},{33,16},{33,17},{34,18},{34,19},{34,20},{35,20},{35,21},{36,22},{37,22},{38,23},{39,22},{39,23},{40,24},{40,24},};
    static int[][] stream2a = {{25,05},{26,06},{26,07},{25,07},{26,8},{26,9},{25,9},{24,10},{24,11},
            {24,12},{23,12},{24,13},{24,14},{25,14},{25,15},{25,16},{26,17},{27,17},{28,18},{28,19},{29,19},{29,20},{29,21},{29,22},{29,23},{28,24},};
    static int[][] stream2b = {{25,8},{25,06},{24,07},{24,8},{25,8},{24,9},{23,9},{23,10},{23,11},{22,12},{22,13},{23,13},{23,14},{24,15},{24,16},{24,17},{25,17},
            {26,18},{27,18},{27,19},{28,20},{28,21},{28,22},{28,21},{28,23},{27,23},{27,24},};
    static int[][] stream3a = {{36,22},{35,21},{34,21},{33,21},{32,21},{31,21},{30,22},{29,22},};
    static int[][] stream3b = {{28,17},{29,23},{30,23},{31,22},{32,22},{33,22},{34,22},{35,22},{36,23},{37,22},};
    static int[][] stream4a = {{29,22},{29,21},{29,20},{29,19},{28,19},{28,18},{27,17},{26,17},{25,16},{24,17},{23,16},{22,17},
            {22,18},{21,18},{20,18},{19,18},{18,18},{17,17},{16,18},{15,18},{14,19},{13,19},{13,20},{12,21},{12,20},
            {11,19},{10,20},{9,20},{9,21},{9,22},{8,23},{8,22},{07,21},{06,21},};
    static int[][] stream4b= {{06,22},{07,22},{07,23},{8,24},{9,23},{10,23},{10,22},{10,21},{11,20},{11,21},
            {12,22},{13,21},{14,21},{14,20},{15,19},{16,19},{17,18},{18,19},{19,19},{20,19},{21,19},{22,19},
            {23,18},{23,17},{24,18},{25,17},{26,18},{27,18},{27,19},{28,20},{28,21},{28,22},{28,23},};
    static int[][] stream5a = {{24,15},{23,14},{22,15},{21,14},{20,14},{19,13},{18,14},{17,13},{16,13},};
    static int[][] stream5b = {{15,13},{16,14},{17,14},{18,15},{19,14},{20,15},{21,15},{22,16},{23,15},{24,16},};
    static int[][] stream6a = {{00,06},{01,05},{02,06},{03,05},{04,05},{05,04},{06,04},{07,03}
            ,{8,03},{9,02},{10,03},{11,02},{12,03},{13,03},{14,03},{15,02},{16,02},{17,01}
            ,{18,01},};
    static int[][] stream6b = {{03,8},{00,05},{01,04},{02,05},{03,04},{04,04},{05,03},{06,03},{07,02}
            ,{8,02},{9,01},{10,02},{11,01},{12,02},{13,02},{14,02},{15,01},{16,01},{17,00},
            {18,00},};
    static int[][] stream7a ={{04,00},{05,00},{05,01},{06,02},{06,03}};
    static int[][] stream7b ={{06,00},{06,01},{07,01},{07,02},};
    static int[][] forest = {
            {00,00},{00,01},{00,02},{01,02},{01,01},{01,00},{00,00},{02,00},{02,01},{02,02},{03,02},{03,01},{03,00},{00,00},
            {04,01},{04,00},{05,01},{05,00},{06,00},{00,00},{06,01},{06,02},{06,03},{07,02},{07,01},{06,01},{06,00},{10,02},{11,01},
            {10,01},{11,00},{12,00},{00,00},{10,00},{00,00},{12,00},{13,00},{13,01},{14,01},{14,00},{15,00},{15,01},{14,02},{13,01},
            {16,01},{17,00},{18,00},{00,00},{00,00},{16,00},{15,00},{19,00},{18,01},{17,01},{16,02},{15,02},{14,03},{15,03},{16,04},
            {17,05},{18,05},{18,04},{19,03},{20,03},{21,03},{21,04},{22,03},{21,02},{18,03},{17,02},{19,06},{24,01},{24,00},{25,00},
            {00,00},{25,01},{26,01},{26,02},{27,00},{28,00},{29,00},{30,01},{30,00},{00,00},{30,02},{29,02},{28,02},{28,03},{27,02},
            {26,03},{25,03},{26,05},{30,04},{30,03},{30,02},{30,01},{30,00},{29,03},{31,03},{31,04},{32,01},{32,00},{33,00},{34,00},
            {35,00},{34,01},{34,02},{34,03},{37,01},{37,02},{38,02},{38,03},{39,02},{39,03},{39,00},{40,00},{38,05},{37,05},{36,06},
            {35,05},{35,04},{36,04},{35,06},{34,07},{34,06},{33,05},{32,05},{31,04},{30,05},{30,06},{29,06},{28,07},{28,06},{28,8},
            {29,07},{29,8},{28,9},{31,8},{32,8},{31,07},{32,07},{33,07},{34,8},{34,07},{35,06},{36,06},{37,05},{38,05},{35,05},
            {35,04},{36,04},{34,06},{33,05},{40,9},{40,9},{40,10},{38,8},{37,8},{36,9},{39,10},{38,11},{37,11},{38,12},{39,11},
            {40,12},{35,10},{34,11},{34,10},{33,10},{32,10},{32,9},{31,8},{32,8},{31,07},{34,8},{34,07},{34,11},{32,11},{33,10},
            {32,10},{32,12},{31,11},{30,11},{30,12},{29,11},{29,10},{31,8},{30,8},{30,07},{29,8},{28,9},{28,8},{28,07},{29,06},
            {28,06},{25,06},{25,07},{26,9},{26,10},{26,11},{26,12},{25,11},{25,10},{25,9},{24,10},{24,11},{25,07},{24,07},{25,06},
            {24,06},{23,07},{22,8},{24,9},{24,10},{24,11},{23,11},{23,10},{23,9},{23,8},{22,8},{22,9},{22,10},{22,11},{21,11},
            {21,10},{20,11},{20,10},{21,9},{19,9},{19,11},{18,11},{17,11},{16,9},{16,8},{16,07},{19,06},{16,8},{16,07},{14,9},
            {14,8},{13,07},{13,06},{14,06},{14,05},{13,05},{10,07},{9,07},{9,06},{8,07},{9,07},{9,8},{9,9},{10,9},{10,10},
            {07,9},{06,9},{06,8},{06,07},{05,06},{05,05},{04,06},{03,05},{04,05},{04,04},{03,04},{02,05},{02,04},{00,07},{00,8},
            {03,8},{02,07},{01,06},{02,06},{03,05},{04,05},{05,05},{8,05},{9,04},
            {24,10},{8,04},{07,04},{06,05},{06,04},{07,03},{8,03},{9,02},{10,03},{11,03},{11,02},{06,05},{07,04},{06,04},{05,04},{9,03},{8,04},{07,03},{8,03},
            {9,02},{10,03},{11,02},{15,8},{15,07},{03,10},{04,11},{04,12},{00,12},{00,13},{00,14},{00,15},{01,14},{01,15},{02,15},{03,14},{04,15},{05,15},{06,16},{07,15},
            {8,16},{8,15},{07,13},{06,13},{06,12},{10,12},{11,11},{10,13},{10,14},{11,13},{12,13},{12,14},{13,13},{14,13},{14,14},{13,14},{14,15},{13,15},{12,16},{12,15},{11,15},
            {10,15},{10,16},{11,14},{15,12},{15,13},{14,13},{14,12},{15,11},{16,12},{17,12},{17,13},{18,13},{19,12},{20,13},{20,12},{21,13},{22,13},{22,12},{22,14},{23,14},{22,15},
            {21,14},{24,14},{24,15},{24,16},{23,15},{23,16},{22,16},{22,17},{21,16},{21,15},{20,15},{20,16},{20,17},{19,16},{19,15},{19,15},{18,16},{19,16},{18,17},{19,17},{18,18},
            {17,17},{24,18},{23,17},{23,18},{22,19},{21,19},{20,19},{19,19},{18,20},{17,19},{16,19},{16,20},{15,20},{15,19},{14,20},{14,21},{15,21},{15,22},{14,22},{14,23},{13,23},
            {12,23},{12,22},{13,20},{11,21},{11,20},{11,19},{12,19},{14,19},{13,18},{13,17},{12,18},{11,17},{10,18},{10,17},{9,18},{8,19},{8,20},{07,19},{06,20},{05,20},{05,20},
            {04,21},{03,21},{03,22},{02,22},{02,23},{01,22},{00,22},{00,23},{01,23},{02,24},{00,00},{01,24},{00,24},{00,00},{03,24},{00,00},{03,23},{03,22},{04,23},{05,23},{05,24},
            {00,00},{06,24},{07,24},{00,00},{8,24},{8,23},{12,24},{13,24},{00,00},{15,24},{16,24},{17,24},{17,23},{18,24},{19,24},{00,00},{20,24},{19,23},{19,22},{19,21},{18,21},
            {12,24},{13,23},{14,23},{15,22},{14,22},{15,21},{14,21},{14,20},{15,20},{16,20},{15,19},{18,20},{17,19},{17,19},{17,18},{18,19},{19,19},{19,24},{19,23},{19,23},{19,22},
            {19,21},{20,21},{20,22},{21,21},{22,22},{23,22},{22,23},{21,23},{21,22},{20,23},{20,24},{12,24},{13,23},{14,23},{15,22},{15,21},{15,20},{16,20},{17,19},{18,20},{19,19},
            {20,19},{21,19},{22,19},{23,18},{23,17},{24,18},{24,20},{24,21},{24,22},{24,24},{23,21},{23,20},{22,21},{21,18},{22,18},{21,17},{22,17},{23,16},{24,16},{23,15},{22,16},
            {19,16},{19,15},{18,16},{17,17},{12,15},{15,17},{14,17},{13,16},{16,17},{16,16},{17,16},{17,15},{16,15},{16,14},{15,14},{15,15},{14,16},{13,16},{16,17},{13,21},{03,19},{02,19},{01,18},{05,18},{04,17},
            {16,14},{22,17},{22,17},{00,00},{25,24},{26,24},{25,23},{25,21},{25,20},{25,19},{25,18},{25,17},{26,18},{26,21},{27,21},{26,24},{27,21},{28,20},{29,20},{29,23},{30,23},
            {30,24},{29,24},{00,00},{31,24},{00,00},{31,19},{31,18},{30,18},{30,17},{29,19},{30,20},{30,21},{29,20},{25,15},{26,15},{25,14},{25,13},{26,13},{27,13},{35,17},{35,16},
            {36,16},{35,15},{36,15},{37,15},{38,15},{37,14},{36,14},{35,14},{34,14},{34,13},{35,12},{39,14},{40,14},{39,13},{40,13},{39,12},{00,00},{37,24},{00,00},{36,24},{37,23},
            {38,23},{39,23},{39,22},{40,24},{40,23},{40,22},{40,21},{40,20},{38,20},{38,19},{38,18},{39,18},{36,14},{16,8},{07,00},{22,11},{15,16},{14,17},{22,16},{25,12},{28,13},{28,12},{29,12},{30,19},{26,24},{27,18},{27,19},{35,13},{36,13},{34,9},{28,9},{29,01},
            {25,05},{39,8},{4,23},{26,13},{27,12},
    };
    static int[][] town = {{11,07},{03,02},{03,07},{02,9},{06,06},{07,07},{11,07},{12,11},{14,10},{18,9},
            {20,05},{22,05},{24,04},{25,02},{27,01},{28,03},{28,06},{27,8},{28,11},{30,9},{33,07},{38,07},{37,10},{33,02},{37,10},{28,11},{31,13},{36,18},{38,21},{34,21},{25,22},{27,15},{26,14},{19,20},{11,22},{06,18},{04,14},
            {9,14},{11,12},{12,11},{14,10},{07,07},{02,9},{06,06},{07,07},{11,07},{03,07},{03,02},};
    static int[][][] bridges = {};
    static int[][] cities = {{8,12},{8,11},{31,21},{28,23},{9,23},{12,03},{25,8},{33,17},{19,14}};

    /**
     * can the unit occupy this hex
     * @param unit
     * @return
     */
    public boolean canOccupy(Unit unit) {
        if (unit.isAllies && checkAxisInHex()){
            return false;
        }
        if (unit.isAxis && checkAlliesInHex()){
            return false;
        }
        int stackInHex =  0;
        for (Unit unitch:getUnitsInHex()){
            stackInHex += unitch.getCurrentStep();
        }
        if ((unit.getCurrentStep() + stackInHex) > stackMax){
            return false;
        }
        return true;
    }

    public void setFakeOccupiedAllies(boolean isOccupied, int thread) {
        isAlliedOccupied[thread] = isOccupied;
    }

    public void setFakeOccupiedAXis(boolean isOccupied, int thread) {
        isAxisOccupied[thread] = isOccupied;
    }

}
