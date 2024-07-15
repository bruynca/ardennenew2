package com.bruinbeargames.ardenne.Unit;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.MoreGermanAmmo;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexSurround;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;

/**
 * Unit represents the counters that you punch out on a  war game
 *
 * @author Casey
 *
 */


public class Unit {

	/**
	 * All the units loaded in the game
	 */
	private static int cntUnit=0;
	/**
	 *  STATIC ARRAY Fields
	 */
	private static ArrayList<Unit> arrGameCombatUnits = new ArrayList<>();
	private static ArrayList<Unit> arrGameOtherUnits = new ArrayList<>();
	static TextureAtlas textureAtlas;
	static TextureRegion tStar;
	static public Unit unitAirplane = null;
	static public Unit unitDummy;


	/**
	 *  Nationality fields
	 */
	public boolean isAllies;
	public boolean isAxis;
	/**
	 *  String descriptions
	 */
	public String designation; // i.e 2nd SS division
	public String subDesignation; // i.e 2nd SS division
	/**
	 *  Units size  can only be 1
	 */
	public boolean isDivision = false;
	public boolean isBrigade= false;
	public boolean isRegiment= false;
	public boolean isBattalion=false;
	public boolean isAdHoc=false;

	public String type= "";
	/**
	 *  Processing fields
	 */
	public int ID;
	public boolean isGroundCombat;
	public boolean isMechanized;
	public boolean isHQ;
	public boolean isArtillery;
	public boolean isTransport;
	public boolean isExit;

	public int entryTurn;

	public int entryNum;

	public boolean canAttackThisTurn = true;
	public boolean hasBeenAttackedThisTurn = false;
	public boolean hasAttackedThisTurn = false;

	private int atStartStep;
	private int currentStep;
	private int currentAttackFactor;
	private int currentDefenseFactor;
	private int currentMoveFactor;
	private int atStartAttackFactor;
	private int atStartDefenseFactor;
	private int atStartMoveFactor;
	/**
	 * artillery
	 */
	private int range;
	private int attackFactorLimbered;
	private int attackFactorUnLimbered;
	private int moveAtStartUnLimbered;
	private int moveAtStartLimbered;

	private boolean isAirplane = false;

	public static void setZOCMobileArtillery() {
		for (Unit unit : Unit.getOnBoardAllied()) {
			if (unit.isMobileArtillery) {
				unit.setExertZoc();
			}
		}
		for (Unit unit : Unit.getOnBoardAxis()) {
			if (unit.isMobileArtillery) {
				unit.setExertZoc();
			}
		}
	}

	public static void loadTexture() {
		textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
		tStar =  textureAtlas.findRegion("star");
	}

	public int getCurrentMoveNoBarrage() {
		return attackFactorLimbered;
	}
	public int getCurrentMoveBarrage() {
		return attackFactorUnLimbered;
	}
	public boolean isMobileArtillery = false;
	int xSetup;
	int ySetup;

	private int turnMoved =0;
	private String variant; // Y - in variant   N - removed when variant played
	private Counter counter;
	private Hex hexOccupy = null;
	private Hex hexOccupyPrev = null;
	private boolean isOnBoard;
	private  boolean isInSupplyThisTurn = true;

	public static void shadeAllAllies() {
		for (Unit unit:getOnBoardAllied())
		{
			if (unit.isOnBoard  && unit.counter.getCounterStack() != null)
			{
				unit.counter.getCounterStack().shade();
			}
		}
	}
	public static void shadeAllAxis() {
		for (Unit unit:getOnBoardAxis())
		{
			if (unit.isOnBoard  && unit.counter.getCounterStack() != null)
			{
				unit.counter.getCounterStack().shade();
			}
		}
	}
	public int getArtAmmo() {
		return artAmmo;
	}

	public void setArtAmmo(int artAmmo) {
		this.artAmmo = artAmmo;
	}
	public void setCurrentAttackFactor(int attackFactor) {
		currentAttackFactor = attackFactor;
	}
	private int artAmmo;



	protected boolean isEliminated = false;
	private boolean isExertZOC = false;
	SupplyUnit supplyUnit;
	private boolean isDisorganized =false;

	/**
	 * Constructor
	 * All units are loaded at start of the game
	 */
	public Unit(Element childXML, boolean isAllies, boolean isSpecial)
	{
		/**
		 * update ID and increment so that each unit is unique
		 */
		ID = cntUnit;
		cntUnit++;
		/**
		 *  update nationality and set if axis or allies
		 */
		if (isAllies) {
			isAxis = false;
			this.isAllies = true;
		}else{
			this.isAllies = false;
			isAxis = true;
		}
		if (isSpecial){
			isAirplane = true;
			currentAttackFactor =5;
			return;
		}


		designation = childXML.getChildByName("unitDesignation").getAttribute("value");
		subDesignation = childXML.getChildByName("unitSubDesignation").getAttribute("value");
		String teString = childXML.getChildByName("unitGroundCombat").getAttribute("value");
		isMechanized = Boolean.parseBoolean(childXML.getChildByName("unitMechanized").getAttribute("value"));
		isGroundCombat = Boolean.parseBoolean(childXML.getChildByName("unitGroundCombat").getAttribute("value"));
		if (isGroundCombat) {
			arrGameCombatUnits.add(this);
		}
		else {
			arrGameOtherUnits.add(this);
		}

		type = childXML.getChildByName("unitType").getAttribute("value");
		if (type.compareTo("HQ" )== 0){
			isHQ = true;
		}
		if (type.compareTo("Transport" )== 0){
			isTransport = true;
		}
		if (type.compareTo("Exit" )== 0){
			isExit = true;
		}
		if (type.compareTo("Artillery") == 0 || type.compareTo("Neber") == 0){
			isArtillery = true;
			attackFactorLimbered = Integer.parseInt(childXML.getChildByName("attackFactorMove").getAttribute("value"));
			attackFactorUnLimbered = Integer.parseInt(childXML.getChildByName("attackFactorNoMove").getAttribute("value"));
			moveAtStartUnLimbered = Integer.parseInt(childXML.getChildByName("movementFactorBarrage").getAttribute("value"));
			moveAtStartLimbered = Integer.parseInt(childXML.getChildByName("movementFactor").getAttribute("value"));
			if (moveAtStartUnLimbered > 0){
			    isMobileArtillery = true;
//				isExertZOC = true;

			}
			if (isAllies){
				artAmmo = 2;
			}else{
				if (MoreGermanAmmo.instance != null && MoreGermanAmmo.instance.isMoreGermanAmmo()){
					artAmmo = 2;
				}else {
					artAmmo = 1;
				}
			}
            setArtilleryLimbered();;

		}else{
			isArtillery = false;
			atStartAttackFactor = Integer.parseInt(childXML.getChildByName("attackFactor").getAttribute("value"));
			currentAttackFactor = atStartAttackFactor;
			atStartDefenseFactor = Integer.parseInt(childXML.getChildByName("defenseFactor").getAttribute("value"));
			currentDefenseFactor = atStartDefenseFactor;
			atStartMoveFactor = Integer.parseInt(childXML.getChildByName("movementFactor").getAttribute("value"));
			currentMoveFactor = atStartMoveFactor;
			isExertZOC = true;
		}
		//
		String strSize = childXML.getChildByName("unitSize").getAttribute("value");
		switch (strSize) {
			case "Division":
				isDivision = true;	isBattalion = false; isAdHoc = false; isRegiment = false; isBrigade = false;
				break;
			case "Regiment":
				isDivision = false;	isBattalion = false; isAdHoc = false; isRegiment = true; isBrigade = false;
				break;
			case "KG":
				isDivision = false;	isBattalion = false; isAdHoc = false; isRegiment = true; isBrigade = false;
				break;
			case "Battalion":
				isDivision = false;	isBattalion = true; isAdHoc = false; isRegiment = false; isBrigade = false;
				break;
			case "Brigade":
				isDivision = false;	isBattalion = false; isAdHoc = false; isRegiment = false; isBrigade = true;
				break;
			case "Ad Hoc":
				isDivision = false;	isBattalion = false; isAdHoc = true; isRegiment = false; isBrigade = false;
				break;
			default:
				isDivision = false;	isBattalion = false; isAdHoc = false; isRegiment = false; isBrigade = false;
				Gdx.app.log("Unit", "Constructor ID Not Matched="+strSize);
				break;
		}
		/**
		 *  Unit either is setup at start or has setup
		 */
		if (childXML.hasChild("variant")){
			variant = childXML.getChildByName("variant").getAttribute("value");
		}else {
			variant = "";
		}
		xSetup = Integer.parseInt(childXML.getChildByName("entryAreaX").getAttribute("value"));
		ySetup = Integer.parseInt(childXML.getChildByName("entryAreaY").getAttribute("value"));
		atStartStep = Integer.parseInt(childXML.getChildByName("unitStepAtStart").getAttribute("value"));
		entryNum = Integer.parseInt(childXML.getChildByName("unitEntry").getAttribute("value"));
		currentStep = atStartStep;
		supplyUnit = new SupplyUnit(this);
		Gdx.app.log("Unit"," id="+getID()+ "  -"+designation+subDesignation);


//		Hex hex = Hex.hexTable[xSetup][ySetup];
//		placeLogic(hex);
	}
	private boolean isLimbered;

	public static ArrayList<Unit> getTransports(boolean isAllies) {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameOtherUnits){
			if (unit.isTransport && unit.isAllies == isAllies){
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getExits(boolean isAllies) {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameOtherUnits){
			if (unit.isExit && unit.isAllies == isAllies){
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}

	public boolean isLimbered(){
		return isLimbered;
	}
	public void setArtilleryLimbered(){
		Gdx.app.log("Unit", "Set Limbered ="+this);

		if (!isArtillery){
			return;
		}
		if (artAmmo > 0) {
			currentAttackFactor = attackFactorLimbered;
		}else{
			currentAttackFactor = 0;
		}
		currentMoveFactor = moveAtStartLimbered;
		isLimbered = true;
		if (getMapCounter() != null) {
			this.getMapCounter().getCounterStack().setPoints();
		}

	}
	public void checkInitLimber(){
		ArrayList<Hex> arrHex =  HexSurround.GetSurroundMapArr(getHexOccupy(),getRange());
		boolean isLimber = true;
		for (Hex hex:arrHex){
			if (isAxis){
				if (hex.checkAlliesInHex()){
					isLimber =false;
					break;
				}
			}else{
				if (hex.checkAxisInHex()){
					isLimber =false;
					break;
				}
			}
		}
		if(isLimber){
			setArtilleryLimbered();
		}else{
			setArtilleryUnLimbered();
		}
	}
	public void setArtilleryUnLimbered(){
		Gdx.app.log("Unit", "Set UnLimbered ="+this);
		if (!isArtillery){
			return;
		}
		 if (artAmmo > 0) {
			 currentAttackFactor = attackFactorUnLimbered;
		 }else{
		 	currentAttackFactor = 0;
		 }
		currentMoveFactor = moveAtStartUnLimbered;
		isLimbered = false;
		 if (getMapCounter() != null) {
			 this.getMapCounter().getCounterStack().setPoints();
		 }

	}

	/**
	 *
	 */
	public void resetArtillery(){
		Gdx.app.log("Unit   resetArtillery", "unit= "+ this);

		if (isLimbered){
			setArtilleryLimbered();
		}else{
			setArtilleryUnLimbered();
		}
		if (!isAirplane) {
			if (this.getMapCounter() != null) {
				this.getMapCounter().getCounterStack().setPoints();
			}
		}
	}


    public static ArrayList<Unit> getOnBoardAlliedNotEliminated() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:getOnBoardAllied()){
			if (!unit.isEliminated){
				arrReturn.add(unit);
			}
		}
		return arrReturn;
    }



    /**
	 * Place a unit on the game board
	 * All logic for placement driven here
	 * Invoking routine should have verified that the unit can be placed on this hex
	 * Logged at this point for saved games
	 * @param hex
	 */
	public void placeOnBoard(Hex hex) {
		if (isOnBoard)
		{
			Gdx.app.log("Unit", "placeOnBoard on board already unit="+this.toString());
//			int fail = 9/0;
			return;
		}
		placeLogic(hex);
		// log this for saved game

	}
	public void removeFromBoard(){
		if (isOnBoard){
			hexOccupy.leaveHex(this);
			counter.getCounterStack().getStack().remove();

//			counter = null;
			isOnBoard = false;
		}
	}
	public void fadeOut(){
		counter.getCounterStack().getStack().addAction(Actions.fadeIn((.15f)));
		counter.getCounterStack().getStack().addAction(Actions.fadeOut(.75f));

	}
	public boolean isOnBoard(){
		return isOnBoard;
	}
	public void setOffBoard(){
		isOnBoard = false;
	}
	public void placeOnBoard() {
		if (isOnBoard || hexOccupy == null)
		{
			Gdx.app.log("Unit", "placeOnBoard error unit="+this.toString());
			int fail = 9/0;
		}
		placeLogic(hexOccupy);
	}
	public Counter getMapCounter()
	{
		if (counter != null) {
			return counter;
		}else{
			return unitDummy.getMapCounter();
		}
	}

	private void placeLogic(Hex hexPlace) {
		hexOccupyPrev = hexOccupy;
		hexOccupy = hexPlace;
		isOnBoard = true;
		counter = new Counter(this, Counter.TypeCounter.MapCounter);
		if (this != unitDummy) {
			counter.place(hexPlace);
			hexPlace.enterHex(this);
		}else{
			int b=0;
		}
	}

	/**
	 * DO NOT USE THIS to Move a unit
	 * @param hexTO
	 */
	public void moveForHexProcessing(Hex hexTO)
	{
		/**
		 *  check if there is a counter
		 *   for some processing
		 */
		if (counter != null) {
			counter.place(hexTO);
		}
		hexOccupyPrev = hexOccupy;
		hexOccupy = hexTO;
	}
	public static  void  removeFromBoard(Unit unit) {
		Gdx.app.log("Unit", "removeFromBoard ="+unit);
		if (unit.hexOccupy != null) {
			unit.hexOccupy.leaveHex(unit);
		}
		unit.isOnBoard = false;
		if (unit.counter != null) {
			unit.counter.remove();
		}
		//unit.counter = null;
		if (unit.hexOccupy != null) {
			Counter.rePlace(unit.hexOccupy);
		}
		arrGameCombatUnits.remove(unit);

	}

	public boolean canStepLoss() {
		if (currentStep == 1 ){
			return false;
		}
		return true;
	}
	public int getCurrentStep()
	{
		return currentStep;
	}

	public void eliminate(boolean isExit){
        Gdx.app.log("Unit","eliminate ="+this);
        /**
         *  changed to remove isEliminated out of timer
         *   other program logic depends on it being set immediatly
         */
		if (!isExit) {
			if (GameSetup.instance.getScenario().ordinal() > 0) {
				SecondPanzerExits.instance.checkEliminate(this);
			}
			if (GameSetup.instance.getScenario().ordinal() > 1) {
				LehrExits.instance.checkEliminate(this);
			}
		}
		isEliminated = true;
		float x=0; float y=0;
		if (counter != null) {
			x = counter.getCounterStack().getStack().getX();
			y = counter.getCounterStack().getStack().getY();
		}
		Hex hex=hexOccupy;
		hex.leaveHex(this);
		removeFromBoard(this);
		if (x > 0 && y>0) {

			Counter work = new Counter(this, Counter.TypeCounter.GUICounter);
			final Stack stack = work.getCounterStack().getStack();
			stack.setPosition(x, y);
			stack.addAction(Actions.fadeOut(1.5f));
			ardenne.instance.mapStage.addActor(stack);
			Timer.schedule(new Timer.Task() {
							   @Override
							   public void run() {
								   //                              cntProcess++;
								   //                              final int copy = cntProcess;
								   stack.remove();
							   }
						   }
					, 1f                    //    (delay)
			);
		}

	}

	public void revive(){
		isEliminated =false;
	}
	public void undoSetup() {
		removeFromBoard(this);
		// log before we change hexOccupy
//		com.brnsft.yobowargames.krim.Utilities.Logger.instance.undoplaceUnit(this, hexOccupy);
		hexOccupy = hexOccupyPrev;

	}
	/**
	 * Reduce the unit by 1 step
	 */
	public void reduceStep() {
		Gdx.app.log("Unit","reduceStep="+this);
		if (counter != null) {
			getHexOccupy().moveUnitToFront(this);
			Counter.rePlace(getHexOccupy());
		}
		currentStep--;
		if (currentStep == 0){
			ErrorGame errorGame=  new ErrorGame("Trying to reduce past 1",this);
		}
		float defend = currentDefenseFactor;
		float attack = currentAttackFactor;
		currentDefenseFactor = (int)((defend/2)+.5);
		currentAttackFactor = (int) ((attack/ 2) +.5);

		defend = atStartDefenseFactor;
		attack = atStartAttackFactor;
		atStartDefenseFactor = (int)((defend/2)+.5);
		atStartAttackFactor = (int) ((attack/ 2) +.5);
		if (currentDefenseFactor < 0){
			currentDefenseFactor =0;
		}
		if (currentAttackFactor < 0){
			currentAttackFactor =0;
		}
		if (atStartDefenseFactor < 1){
			atStartDefenseFactor =1;
		}
		if (atStartAttackFactor < 1){
			atStartAttackFactor =1;
		}
		if (counter != null) {
			Stack stack = counter.getCounterStack().stack;
			counter.getCounterStack().setStep(stack);
			counter.getCounterStack().setPoints();
			final Image image = new Image(tStar);
			image.setPosition(stack.getX()+10,stack.getY()+10);
			image.addAction(Actions.fadeIn((.05f)));
			image.addAction(Actions.fadeOut(.5f));
			ardenne.instance.mapStage.addActor(image);
			Timer.schedule(new Timer.Task(){
							   @Override
							   public void run() {
								   //                              cntProcess++;
								   //                              final int copy = cntProcess;
								   image.remove();
							   }
						   }
					, 2f        			//    (delay)
			);

		}

	}


	public void setCurrentMovement(int newMove) {
		currentMoveFactor = newMove;
		if (currentMoveFactor < 0){
			currentMoveFactor = 0;
		}
	}
	public void resetCurrentMove(){
		if (isArtillery){
			if (isLimbered){
				currentMoveFactor = moveAtStartLimbered;
			}else{
				currentMoveFactor = moveAtStartUnLimbered;
			}
		}else {
			currentMoveFactor = atStartMoveFactor;
		}
	}


    public boolean getInSupplyThisTurn() {
		return isInSupplyThisTurn;
	}
	public void setInSupplyThisTurn() {
		isInSupplyThisTurn = true;
	}
	public void setOffSupplyThisTurn() {
		isInSupplyThisTurn = false;

	}


	public int getID()
	{
		return ID;
	}
	public int getCurrentMovement()
	{
		return currentMoveFactor;
	}
	public int getCurrentDefenseFactor()
	{
		return currentDefenseFactor;
	}

	public int getCurrenAttackFactor(){
		return currentAttackFactor;
	}
	public int getAtStartAttackFactor(){
		return atStartAttackFactor;
	}

	public Hex getHexOccupy()
	{
		return hexOccupy;
	}
	public void setMovedThisTurn(int turn) {
		turnMoved =  turn;
	}
	public int  getMovedLast() {
		return turnMoved;
	}
	public boolean isEliminated(){
		return isEliminated;
	}
	public boolean isExertZOC(){
		return isExertZOC;
	}
	public void setExertZoc(){
		isExertZOC = true;
	}




	/**
	 *  Unit tostring display info about unit for debugging
	 */
	@Override
	public String toString() {
		return String.format(designation+" "+ID+" ");
	}
	/**
	 *  Get a unit by its ID
	 * @param idSearch
	 * @return
	 */
	public static Unit getUnitByID(int idSearch)
	{
		for (Unit unit:arrGameCombatUnits){
			if (unit.getID() == idSearch){
				return unit;
			}
		}
		return null;
	}

	/**
	 *  START OF UNIT STATIC METHODS
	 */


	/**
	 * Create xml string of units
	 * string will include
	 *
	 * ID
	 * Current Hex
	 * CurrentStep
	 *
	 * This will need to be modified
	 *
	 * @param arrUnitsIn
	 * @return
	 */
	public static String getXMLUnits(ArrayList<Unit> arrUnitsIn)
	{
		final String strUnits = "<units>";
		final String strUnitsTerm = "</units>";
		final String strUnit = "<unit>";
		final String strUnitTerm = "</unit>";
		final String strHexX = "<hexX value=\"";
		final String strHexY = "<hexY value=\"";
		final String strCanAttack = "<canattack value=\"";
		final String strAttacked = "<attacked value=\"";
		final String strBeenAttacked = "<beenattacked value=\"";
		final String strEliminated = "<eliminated value=\"";
		final String strTerm = "\"/>";
		final String strCurrentStep = "<currentStep value=\"";
		final String strCurrentMove = "<currentMove value=\"";
		final String strCurrentAttack = "<currentAttack value=\"";
		final String strCurrentDefense = "<currentDefense value=\"";
		final String strAtStartAttack = "<atstartAttack value=\"";
		final String strAtStartDefense = "<atstartDefense value=\"";
		final String strLimbered = "<limber value=\"";

		final String strMoveTurn = "<turnMoved value=\"";
		final String strOTTurn = "<turnOT value=\"";
		final String strOperationalSupply = "<supply value=\"";
		final String strThisTurnSupply = "<turnsupply value=\"";
		final String strDG = "<dg value=\"";

		final String strID = "<ID value=\"";


		StringBuffer sXML =  new StringBuffer();
		sXML.append(strUnits);
		for (Unit unit:arrUnitsIn)
		{
			sXML.append(strUnit);
			sXML.append(strID);
			sXML.append(String.format("%02d", unit.ID));
			sXML.append(strTerm);
			sXML.append(strHexX);
			if (unit.getHexOccupy() == null){
				unit.hexOccupy =  Hex.hexTable[0][0];
				int i =0;
			}
			sXML.append(unit.hexOccupy.xTable);
			sXML.append(strTerm);
			sXML.append(strHexY);
			sXML.append(unit.hexOccupy.yTable);
			sXML.append(strTerm);
			sXML.append(strCurrentStep);
			sXML.append(String.format("%02d", unit.currentStep));
			sXML.append(strTerm);
			sXML.append(strCurrentMove);
			sXML.append(String.format("%02d", unit.getCurrentMovement()));
			sXML.append(strTerm);
			sXML.append(strCurrentAttack);
			sXML.append(String.format("%02d", unit.getCurrenAttackFactor()));
			sXML.append(strTerm);
			sXML.append(strCurrentDefense);
			sXML.append(String.format("%02d", unit.getCurrentDefenseFactor()));
			sXML.append(strTerm);
			sXML.append(strAtStartAttack);
			sXML.append(String.format("%02d", unit.atStartAttackFactor));
			sXML.append(strTerm);
			sXML.append(strAtStartDefense);
			sXML.append(String.format("%02d", unit.atStartDefenseFactor));
			sXML.append(strTerm);
			sXML.append(strMoveTurn);
			sXML.append(String.format("%02d", unit.turnMoved));
			sXML.append(strTerm);

			sXML.append(strCanAttack);
			if (unit.canAttackThisTurn) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}
			sXML.append(strTerm);
			sXML.append(strLimbered);
			if (unit.isLimbered()) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}
			sXML.append(strTerm);
			sXML.append(strAttacked);
			if (unit.hasAttackedThisTurn) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}
			sXML.append(strTerm);
			sXML.append(strBeenAttacked);
			if (unit.hasBeenAttackedThisTurn) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}

			sXML.append(strTerm);
			sXML.append(strEliminated);
			if (unit.isEliminated) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}

			sXML.append(strTerm);

			sXML.append(strThisTurnSupply);
			if (unit.isInSupplyThisTurn) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}
			sXML.append(strTerm);

			sXML.append(strDG);
			if (unit.isDisorganized) {
				sXML.append("true");
			}else{
				sXML.append("false");
			}

			sXML.append(strTerm);

			sXML.append(strUnitTerm);
		}
		sXML.append(strUnitsTerm);
		return sXML.toString();
	}

	/**
	 * Load Unit data  that was saved with getXMLUnits
	 * @param xmlSetup - string of xml
	 */
	public static void loadUnitHex(String xmlSetup) {
		XmlReader reader = new XmlReader();
		Element root = reader.parse(xmlSetup);
		Array<Element> xmlUnitAll = root.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID  = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit=getUnitByID(ID);
			String strHex = xmlunit.getChildByName("hex").getAttribute("value");
			Hex  hex = null;
			unit.hexOccupy = hex;

		}
	}



	/**
	 *  KRIM
	 */
	public static void loadAllUnits() {
		arrGameOtherUnits = new ArrayList<>();
		arrGameCombatUnits = new ArrayList<>();
		/**
		 *  Load all Unit xml files for this game
		 */
		FileHandle fileHandle;
		fileHandle = Gdx.files.internal("units/german.xml");
		loadNationUnits(fileHandle, false);
		fileHandle = Gdx.files.internal("units/allies.xml");
		loadNationUnits(fileHandle, true);
		unitAirplane = new Unit(null,true,true);
		for (Unit unit:arrGameCombatUnits){
			if (unit.designation.contains("Dummy")){
				unitDummy = unit;
				unit.placeLogic(Hex.hexTable[0][19]);
			}
		}
		arrGameCombatUnits.remove(unitDummy);
	//	counter = new Counter(this, Counter.TypeCounter.MapCounter);



	}
	private static  void loadNationUnits(FileHandle fileHandle,boolean isAllies)
	{
		XmlReader reader = new XmlReader();
		Element root = reader.parse(fileHandle);
		Array<Element> xmlUnitAll = root.getChildrenByName("unit");
		Unit unit;
		for (Element xmlunit: xmlUnitAll)
		{
			unit = new Unit(xmlunit, isAllies, false);
		}

	}
	/**
	 * Get the Setup units
	 * This will select Units that have an entry value of zero(on Board at start)
	 * and is the the appropriate side
	 * @param wantAllied
	 * @return
	 */
	public static ArrayList<Unit> getSetupUnits(boolean wantAllied) {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.entryNum == 1 && unit.isAllies == wantAllied) // must be on map at start
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getUnitsForTurn(int turn) {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.entryNum == turn) // must be on map at start
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getReinforcements() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.entryNum > 1)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	/**
	 * Get all onboard allied combat units
	 * @return
	 */
	public static ArrayList<Unit> getOnBoardAllied() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isOnBoard  && unit.isAllies&& !unit.isEliminated)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getAllAllied() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isAllies&& !unit.isEliminated)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	/**
	 * Get all onboard allied combat units
	 * @return
	 */
	public static ArrayList<Unit> getAllied() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isAllies) {
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	/**
	 * Get all onboard axis combat units
	 * @return
	 */

	public static ArrayList<Unit> getOnBoardAxis() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isOnBoard  && unit.isAxis && !unit.isEliminated)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getAxis() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isAxis)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static ArrayList<Unit> getOnBoardAllies() {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isOnBoard  && unit.isAllies && !unit.isEliminated)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;
	}
	public static void  loadUnits(ArrayList<Unit> arrLoad){
		for (Unit unit:arrLoad){
			Hex hex = Hex.hexTable[unit.xSetup][unit.ySetup];
			unit.placeLogic(hex);
		}

	}




	public static ArrayList<Unit> getEliminated(boolean isAllies) {
		ArrayList<Unit> arrReturn = new ArrayList<>();
		for (Unit unit:arrGameCombatUnits)
		{
			if (unit.isEliminated && unit.isAllies == isAllies)
			{
				arrReturn.add(unit);
			}
		}
		return arrReturn;

	}



	public static void initOutOfSupplyThisTurn(boolean isAlliesFlag)
	{
		for (Unit unit:arrGameCombatUnits){
			if (unit.isAllies == isAlliesFlag) {
				unit.setOffSupplyThisTurn();
			}
		}
	}
	public static void initLimber(boolean isAllied)
	{
		if (isAllied){
			for (Unit unit:Unit.getAllied()){
				if (unit.isArtillery){
					unit.setArtilleryLimbered();
				}
			}
		}else{
			for (Unit unit:Unit.getAxis()){
				if (unit.isArtillery){
					unit.setArtilleryLimbered();
				}
			}
		}
	}

	public static void initCanAttack()
	{
		for (Unit unit:arrGameCombatUnits){
			unit.canAttackThisTurn = true;
//			unit.hasBeenAttackedThisTurn = false;
		}
	}

	public static void initUnShade()
	{
		for (Unit unit:arrGameCombatUnits){
			if (unit.getMapCounter() != null){
				unit.getMapCounter().getCounterStack().removeShade();
			}
		}
	}

	public static void initShade(boolean isAllies)
	{
		for (Unit unit:arrGameCombatUnits){
			if (unit.isAllies == isAllies) {
				if (unit.getMapCounter() != null) {
					unit.getMapCounter().getCounterStack().shade();
				}
			}
		}
	}

	public static void reIntializeBoard()
	{

		for (Unit unit:arrGameCombatUnits){
			unit.removeFromBoard(unit);
			unit = null;
		}
		for (Unit unit:arrGameOtherUnits){
			unit.removeFromBoard(unit);
			unit = null;
		}
		arrGameCombatUnits.clear();
		arrGameOtherUnits.clear();
		cntUnit = 0;
		Hex.loadHexes();
		/**
		 *  just in case want to play another game
		 *  set setting to vanilla not what was in the loadgame
		 *
		 */

	}


	public int getRange() {
		if (type.contains("Neber") || designation.contains("155")){
			return 4;
		}
		return 3;
	}
	int disorganizedAttackSave;
	int disOrganizedDefendSave;
	int disOrganizedMove;
	public void setDisorganized() {
		if (!isEliminated) {
			isDisorganized = true;
			disorganizedAttackSave = currentAttackFactor;
			currentAttackFactor = 0;
			disOrganizedDefendSave = currentDefenseFactor;
			disOrganizedMove = currentMoveFactor;
			float defend = currentDefenseFactor;
			float move = currentMoveFactor;
			isExertZOC = false;
			currentDefenseFactor = (int) ((defend / 2F) + .5f);
			currentMoveFactor = (int) ((move / 2F) + .5f);
			if (getHexOccupy() != null) {
				getHexOccupy().setZOCs();
			}
			if (getMapCounter() != null) {
				getMapCounter().getCounterStack().setPoints();
			}
		}
	}

	public void setOffDisorganized() {
		isDisorganized = false;
		currentAttackFactor = disorganizedAttackSave;
		isExertZOC = true;
		currentDefenseFactor = disOrganizedDefendSave;
		currentMoveFactor = disOrganizedMove;
		if (getHexOccupy() != null){
			getHexOccupy().setZOCs();
		}
		if (getMapCounter() != null) {
			getMapCounter().getCounterStack().setPoints();
		}

	}
	static public ArrayList<Unit> getAxisDisorganized(){
		ArrayList<Unit> arrWork = new ArrayList<>();
		for (Unit unit: getOnBoardAxis()){
			if (unit.isDisorganized){
				arrWork.add(unit);
			}
		}
		return arrWork;
	}
	static public void initUntouchable(boolean isAllies){
		ArrayList<Unit> arrUnits = new ArrayList<>();
		if (!isAllies){
			arrUnits.addAll(getOnBoardAxis());
		}else{
			arrUnits.addAll(getOnBoardAllied());
		}
		for (Unit unit:arrUnits){
			if (unit.getMapCounter() != null){
				unit.getMapCounter().getCounterStack().stack.setTouchable(Touchable.disabled);
			}
		}
	}
	static public void initTouchable(boolean isAllies){
		ArrayList<Unit> arrUnits = new ArrayList<>();
		if (!isAllies){
			arrUnits.addAll(getOnBoardAxis());
		}else{
			arrUnits.addAll(getOnBoardAllied());
		}
		for (Unit unit:arrUnits){
			if (unit.getMapCounter() != null){
				unit.getMapCounter().getCounterStack().stack.setTouchable(Touchable.enabled);
			}
		}
	}

	static public ArrayList<Unit> getAlliedDisorganized(){
		ArrayList<Unit> arrWork = new ArrayList<>();
		for (Unit unit: getOnBoardAllied()){
			if (unit.isDisorganized){
				arrWork.add(unit);
			}
		}
		return arrWork;
	}
	public void setCanAttackThisTurnOff() {
		canAttackThisTurn = false;
	}

	public void setHasbeenAttackedThisTurn() {
		hasBeenAttackedThisTurn = true;
	}
	public void setHasbeenAttackedThisTurnOff() {
		hasBeenAttackedThisTurn = false;
	}
	public boolean getHasbeenAttackedThisTurn(){
		return hasBeenAttackedThisTurn;
	}
	public void setHasAttackedThisTurn() {
		hasAttackedThisTurn = true;
	}
	public void setHasAttackedThisTurnOff() {
		hasAttackedThisTurn = false;
	}
	public boolean getHasAttackedThisTurn() {
		return hasAttackedThisTurn;
	}

	public void setStep(int stepNum) {
		currentStep = stepNum;
	}

	public void setCurrentDefenseFactor(int defense) {
		currentDefenseFactor = defense;
	}

	public boolean isDisorganized() {
		return isDisorganized;
	}
	public static void resetID(){
		cntUnit = 0;
	}

	/**
	 *  reduce attack by 1/4 and defense by 1/2
	 */
	public void reduceCombat() {
		float attack = currentAttackFactor;
		float newAttack = (attack/4) +.5F;
		currentAttackFactor = (int)newAttack;
		float defense = currentDefenseFactor;
		float newDefense = (defense/2) +.5F;
		currentDefenseFactor = (int)newDefense;
		if (currentDefenseFactor < 1){
			currentDefenseFactor =1;
		}
		if (currentAttackFactor < 1){
			currentAttackFactor =1;
		}
		if (atStartDefenseFactor < 1){
			atStartDefenseFactor =1;
		}
		if (atStartAttackFactor < 1){
			atStartAttackFactor =1;
		}
		getMapCounter().getCounterStack().setPoints();
	}

	public void setAtStartAttackFactor(int attack) {
		atStartAttackFactor = attack;
	}

	public void setAtStartDefenseFactor(int defense) {
		atStartDefenseFactor = defense;
	}

	public int getEntryX() {
		return xSetup;
	}

	public int getEntryY() {
		return ySetup;
	}

	public void resetAttack() {
		currentAttackFactor = atStartAttackFactor;
		currentDefenseFactor = atStartDefenseFactor;
	}
}



