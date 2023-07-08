package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.CenterScreen;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.GamePreferences;
import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHelper;
import com.bruinbeargames.ardenne.Map;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.UI.BombardDisplay;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.WinBombard;
import com.bruinbeargames.ardenne.UI.WinToolTip;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.Counter;
import com.bruinbeargames.ardenne.Unit.CounterStack;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *  Barrage steps:
 *  1. Initialize  detemine if the side(passed by isAllies) has capability to do  a barrage
 *  	if yes then get all counters, hilite them and wait for input else go to next doSeaMove
 *  2. if user or ai
 */
public class Barrage {

	static public Barrage instance;
	private I18NBundle i18NBundle;

	static public ArrayList<Stack> arrStack = new ArrayList<>();
	boolean isAllies;
	ArrayList<Unit> arrUnitToShoot = new ArrayList<>(); // used to determine hexes
	ArrayList<Unit> arrUnitShootAt = new ArrayList<>(); // used to determine hexes

	ArrayList<Hex> arrArtilleryTargets =  new ArrayList<>();
	ArrayList<Unit> arrArtilleryShooters =  new ArrayList<>();

	TextureRegion crossHairs =new TextureRegion(UILoader.instance.combatDisplay.asset.get("target"));
	TextureRegion gun = new TextureRegion(UILoader.instance.combatDisplay.asset.get("barrage"));
	static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");

	TextureRegion explosion1 =textureAtlas.findRegion("explosion1");
	TextureRegion explosion2 =textureAtlas.findRegion("explosion2");
	TextureRegion explosion3 =textureAtlas.findRegion("explosion3");
	TextureRegion explosion4 =textureAtlas.findRegion("explosion4");

	WinBombardShooters winBombardShooters;
	ArrayList<Integer> integerArrayListTurnRestrictAllies = new ArrayList<>();
	ArrayList<Integer> integerArrayListTurnRestrictAxis = new ArrayList<>();
	public ArrayList<TargetShooterSave> targetShooterSaveArrayListAllies = new ArrayList<>();
	public ArrayList<TargetShooterSave> targetShooterSaveArrayListAxis = new ArrayList<>();
	ArrayList<Stack> stackArrayList = new ArrayList<>();
	ArrayList<ClickListener> arrClicks = new ArrayList<>();

	private Stack redisplayStack;
	TextTooltip.TextTooltipStyle tooltipStyle;



	public Barrage() {
		instance = this;
		i18NBundle= GameMenuLoader.instance.localization;
		tooltipStyle = new TextTooltip.TextTooltipStyle();
		tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
		NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
		tooltipStyle.background = new NinePatchDrawable(np);
		np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);


	}

	public static void clearTargets() {
		for (Stack stack:arrStack){
			stack.remove();
			stack.clear();
		}
		arrStack.clear();
	}

	public ArrayList<Hex> getTargets(){
		ArrayList<Hex> arrReturn = new ArrayList<>();
		arrReturn.addAll(arrArtilleryTargets);
		return arrReturn;
	}


	/**
	 * Initialize the Barrage
 	 * @param alliesIn
	 * @param isNotAI
	 */
	public void intialize(boolean alliesIn, boolean isNotAI) {
		Gdx.app.log("Barrage", "Initialize");
		/**
		 *
		 */
		this.isAllies = alliesIn;
		Unit.initUnShade();
		Unit.initShade(isAllies);
		if (isAllies) {
			targetShooterSaveArrayListAllies = new ArrayList<>();
		}else{
/*			for (TargetShooterSave tg:targetShooterSaveArrayListAxis){
				tg.removeStack();
				tg.removeListner();
			} */ // not used for save game
			targetShooterSaveArrayListAxis = new ArrayList<>();
		}
		arrArtilleryShooters = new ArrayList<>();
		arrArtilleryTargets = new ArrayList<>();
		clearCrossHairs(); // just in case left by previous

		getCanBombard(isAllies);
		if (isAllies) {
			TurnCounter.instance.updateText(i18NBundle.get("abarrage"));
		}else{
			TurnCounter.instance.updateText(i18NBundle.get("gbarrage"));

		}
		String str;
		if (arrArtilleryShooters.size() > 0 || Airplane.instance.getCount() > 0) {
		}else{
			if (isAllies) {
				str = i18NBundle.get("noartilleyinrangea");
			}else{
				str = i18NBundle.get("noartilleyinrangeg");
			}
			if (isAllies && GameSetup.instance.isGermanVersusAI()){
				return;
			}
			if (!isAllies && GameSetup.instance.isAlliedVersusAI()){
				return;
			}
			EventPopUp.instance.show(str);
			return;
		}

		/**
		 *  put artillery counters on top
		 */
		for (Unit unit:arrArtilleryShooters){
			if (unit.getMapCounter() != null){
				unit.getMapCounter().getCounterStack().removeShade();
			}
			Hex hex = unit.getHexOccupy();
			hex.moveUnitToFront(unit);
			Counter.rePlace(hex);
			/**
			 *  put target shooter back on top
			 */
			for (TargetShooterSave targetShooterSave:targetShooterSaveArrayListAllies){
				if (targetShooterSave.hexTarget == hex){
					targetShooterSave.reDisplay();
				}
			}
			for (TargetShooterSave targetShooterSave:targetShooterSaveArrayListAxis){
				if (targetShooterSave.hexTarget == hex){
					targetShooterSave.reDisplay();
				}
			}

		}
		arrClicks.clear();
		createShootAtMarkers();
	}

	/**
	 *  Check any rules or make sure there is an artillery unit on board  to go ahead
	 *  	arrArtilleryShooters created and filled
	 * 		arrArtilleryTargets created and filled
	 * @param isAllies
	 *
	 */
	private void getCanBombard(boolean isAllies) {

		arrArtilleryShooters = new ArrayList<>();
		arrArtilleryTargets = new ArrayList<>();
		arrUnitToShoot = new ArrayList<>();
		ArrayList<Unit> arrUnitCheck = new ArrayList<>();
		arrUnitShootAt = new ArrayList<>();
		if (isAllies) {
			arrUnitCheck = Unit.getOnBoardAllied();
			arrUnitShootAt = Unit.getOnBoardAxis();
		} else {
			arrUnitCheck = Unit.getOnBoardAxis();
			arrUnitShootAt = Unit.getOnBoardAllied();
		}
		/**
		 *  Check Conditions to be done later
		 *  1.Tactical supply
		 *  2. if Adjacnt to non artillery must have  non artillry with it see 13.7
		 */
		for (Unit unit:arrUnitCheck){
			if (unit.isArtillery){
				if (unit.getCurrenAttackFactor() >0) {
					arrUnitToShoot.add(unit);
				}
			}
		}
		getDisplayArrays();
		return;
	}

	public ArrayList<Hex> getArrArtilleryTargets() {
		return arrArtilleryTargets;
	}

	public ArrayList<Unit> getArrArtilleryShooters() {
		return arrArtilleryShooters;
	}

	private void getDisplayArrays() {
		/**
		 *  check if any in range of defender
		 */
		for (Unit unit : arrUnitToShoot) {
			if (unit.isArtillery) {
				ArrayList<Hex> arrHexTargets = isInRange(unit.getRange(), unit.getHexOccupy(), arrUnitShootAt);
				if (arrHexTargets.size() > 0) {
					arrArtilleryTargets.addAll(arrHexTargets);
					arrArtilleryShooters.add(unit);
				}
				HexHelper.removeDupes(arrArtilleryTargets);
			}
		}
		if (isAllies){
			if (Airplane.instance.getCount() > 0) {
				for (Unit unit:Unit.getOnBoardAxis()){
					arrArtilleryTargets.add(unit.getHexOccupy());
				}
			}
			HexHelper.removeDupes(arrArtilleryTargets);
		}
	}



	/**
	 *  Find all hexes that this hex can can hit that has enemy in it
	 * @param range
	 * @param hex
	 * @param arrUnitShootAt
	 * @return array of hexes
	 */
	public ArrayList<Hex> isInRange(int range, Hex hex, ArrayList<Unit> arrUnitShootAt) {
		ArrayList<Hex> arrReturn = new ArrayList<>();
		ArrayList<Hex> arrHexSearch = HexHelper.getSurroundinghexes(hex,range);
		for (Unit unit:arrUnitShootAt){
			if (arrHexSearch.contains(unit.getHexOccupy()))
			{
				arrReturn.add(unit.getHexOccupy());
			}
		}
		return arrReturn;
	}

	/**
	 *  Get all artillery
	 * @return
	 */
	public ArrayList<Unit> getArtilleryInRange(boolean isAllies) {

		return null;
	}

	/**
	 *  Create the display of markers which will drive interface
	 *  it arrArtillerTargets and arrArtilleryShooters
	 * */

	public void createShootAtMarkers() {
		if (isAllies && Airplane.instance.getCount() > 0){

		}else {
			if (arrArtilleryShooters.size() > 0 && arrArtilleryTargets.size() > 0) {
			} else {
				//NextPhase.instance.nextPhase(this);();
				return;
			}
		}
		stackArrayList = new ArrayList<>();
		arrClicks = new ArrayList<>();
		/**
		 *  Remove any hexes already targets  as they may have been readded on the calcs
		 */
		if (isAllies) {
			for (TargetShooterSave targetShooterSave : targetShooterSaveArrayListAllies){
				arrArtilleryTargets.remove(targetShooterSave.hexTarget);
			}
		}else {
			for (TargetShooterSave targetShooterSave : targetShooterSaveArrayListAxis){
				arrArtilleryTargets.remove(targetShooterSave.hexTarget);
			}

		}

		for (Hex hex : arrArtilleryTargets) {
			createCrossHairForHex(hex, true);
		}
		if (arrArtilleryTargets.size() > 0) {
			boolean isOneInView = false;
			for (Hex hex : arrArtilleryTargets) {
				createCrossHairForHex(hex, true);
			}
			for (Hex hex : arrArtilleryTargets) {
				if (Map.instance.onScreen(hex)){
					isOneInView = true;
					break;
				}
			}
			if (!isOneInView){
				CenterScreen.instance.start(arrArtilleryTargets.get(0));
			}
		}

	}
	private void createCrossHairForHex(Hex hex, boolean addClick){
		Image image = new Image(crossHairs);
		final Stack stack = new Stack();

		stackArrayList.add(stack);
//		arrClicks.clear();
		stack.add(image);
		stack.setSize(156, 136);
		Vector2 pos = hex.getCounterPosition();
		stack.setPosition(pos.x - 25, pos.y + 5);
		stack.setUserObject(hex);

		final Hex hexClick = hex;

		ClickListener leftclick = (new ClickListener(Input.Buttons.LEFT) {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				showInRange(hexClick);
			}
		});


		arrClicks.add(leftclick);

		if (addClick) {
			stack.addListener(leftclick);
		}

		ardenne.instance.mapStage.addActor(stack);
	}

	public void redisplay(){
		for (Stack stack : stackArrayList){
			stack.setZIndex(999);
		}
	}

	public void createCannon(Hex hexClick){
		ArrayList<Unit> unitArrayListShootersFinal = new ArrayList<>();
		for (Counter counter:winBombardShooters.counterArrayList){
			if (counter.getCounterStack().isHilited()){
				unitArrayListShootersFinal.add(counter.getUnit());
			}
		}
		int cntAir =0;
		if (isAllies) {
			for (AirplaneStack air : winBombardShooters.airplaneArrayList){
				if (air.isHilited){
					cntAir++;
				}
			}
		}
		if (unitArrayListShootersFinal.size() == 0 && cntAir == 0){
			cancelBarrage();
			return;
		}

		/**
		 *  create
		 */
		createTargetShooterSave(hexClick,unitArrayListShootersFinal, cntAir,false);
		/**
		 *  remove selected
		 */
		arrUnitToShoot.removeAll(unitArrayListShootersFinal); // take out of the pool
		arrUnitShootAt.removeAll(hexClick.getUnitsInHex());
		/**
		 *  redo display arrays
		 */
		arrArtilleryShooters = new ArrayList<>();
		arrArtilleryTargets = new ArrayList<>();

		getDisplayArrays();
		/**
		 *  redisplay
		 */
//		winBombardShooters.end();
		winBombardShooters = null;
		clearCrossHairs();
		createShootAtMarkers();

	}

	/**
	 *  user clicked crosshairs show all artillery in range
	 * @param hex
	 */
	private void showInRange(Hex hex){
		clearCrossHairs();
		createCrossHairForHex(hex,false);
		ArrayList<Unit> unitArrayList = getAllShooters(hex);
		winBombardShooters = new WinBombardShooters(this, unitArrayList, hex);
	}
	public void clearCrossHairs(){
		for (Stack stack:stackArrayList){
			stack.remove();
		}
		stackArrayList.clear();
		arrClicks.clear();
	}

	/**
	 * Get all shooters in range of Hex
	 * @param hex
	 * @return
	 */
	public ArrayList<Unit> getAllShooters(Hex hex) {
		ArrayList<Unit> unitArrayList = new ArrayList<>();
		for (Unit unit:arrArtilleryShooters){
			ArrayList<Hex> arrHexSearch = HexHelper.getSurroundinghexes(unit.getHexOccupy(), unit.getRange());
			if (arrHexSearch.contains(hex)){
				unitArrayList.add(unit);
			}
		}
		return unitArrayList;
	}

	/**
	 *  Cancel barrage that the window is working on
	 */
	private void cancelBarrage() {
		winBombardShooters.cancel();
		winBombardShooters.end();
		for (AirplaneStack air:winBombardShooters.airplaneArrayList){
			if (air.isHilited){
				Airplane.instance.add(1);
			}
		}
		winBombardShooters.airplaneArrayList.clear();
		clearCrossHairs();
		createShootAtMarkers();
	}

	/**
	 *  Create
	 * @param hexClick
	 * @param unitArrayListShootersFinal
	 */
	public void createTargetShooterSave(Hex hexClick, ArrayList<Unit> unitArrayListShootersFinal, int cntAir, boolean isAI) {
		TargetShooterSave targetShooterSave = new TargetShooterSave(hexClick,unitArrayListShootersFinal, isAllies, cntAir, isAI);
		if (cntAir > 0 || arrArtilleryShooters.get(0).isAllies){
			targetShooterSaveArrayListAllies.add(targetShooterSave);
		}else{
			targetShooterSaveArrayListAxis.add(targetShooterSave);
		}
		//repositionGunsOnDisplay();
	}

	/**
	 * User clicked on gun representing a bombardment
	 * @param targetShooterSaveIn
	 */
	private void removeGun(TargetShooterSave targetShooterSaveIn) {

		if (targetShooterSaveIn.isAllies != this.isAllies){
			return;
		}
		if (targetShooterSaveIn.isAllies){
			targetShooterSaveArrayListAllies.remove(targetShooterSaveIn);
			Airplane.instance.add(targetShooterSaveIn.cntAir);
		}else{
			targetShooterSaveArrayListAxis.remove(targetShooterSaveIn);
		}
		targetShooterSaveIn.removeStack();
		if (targetShooterSaveArrayListAxis.size() > 0|| targetShooterSaveArrayListAllies.size() >0) {
			// repositionGunsOnDisplay();
		}
		/**
		 *  add selected
		 */
		arrUnitToShoot.addAll(targetShooterSaveIn.arrShooters); // take out of the pool
		for (Unit unit:targetShooterSaveIn.arrShooters){
			unit.getMapCounter().getCounterStack().removeShade();
		}
		arrUnitShootAt.addAll(targetShooterSaveIn.hexTarget.getUnitsInHex());
		/**
		 *  redo display arrays
		 */
		arrArtilleryShooters = new ArrayList<>();
		arrArtilleryTargets = new ArrayList<>();
		getDisplayArrays();
		/**
		 *  redisplay
		 */
		if (winBombardShooters != null) {
			winBombardShooters.end();
			winBombardShooters = null;

		}
		clearCrossHairs();
		createShootAtMarkers();
	}

	/**
	 *  put the guns above window in two rows
	 *  first Axis then
	 *  Allies
	 */
	private void repositionGunsOnDisplay() {
		float x = Gdx.graphics.getWidth() - (Counter.sizeOnMap + 5); // see window
		float y = Counter.sizeOnMap   + 40; // see window height
		/**
		 * Go Backward
		 */
		for (int i= targetShooterSaveArrayListAxis.size()-1; i >= 0; i-- ){

			TargetShooterSave targetShooterSave = targetShooterSaveArrayListAxis.get(i);
			targetShooterSave.setStackPosition(x,y);
			x -= (Counter.sizeOnMap + 5);
		}
		y += Counter.sizeOnMap   + 40;
		for (int i= targetShooterSaveArrayListAllies.size()-1; i >= 0; i-- ){

			TargetShooterSave targetShooterSave = targetShooterSaveArrayListAxis.get(i);
			targetShooterSave.setStackPosition(x,y);
			x -= (Counter.sizeOnMap + 5);
		}

	}


	public void disableBarrageListners() {
		for (int i=0; i< stackArrayList.size(); i++){
			stackArrayList.get(i).removeListener(arrClicks.get(i));
		}
	}
	static Label.LabelStyle labelStyleNameTarget
			= new Label.LabelStyle(FontFactory.instance.yellowFont, Color.YELLOW);

	static ArrayList<Actor> arrActors = new ArrayList<>();
	public static void clearResults(){
		for (Actor actor:arrActors){
			actor.remove();
		}
		arrActors.clear();
	}

	/**
	 * TargetShooterSave class used to store the barrages set up
	 */
	public class TargetShooterSave{
		public Hex hexTarget;
		ArrayList<Unit> arrShooters = new ArrayList<>();
		Stack stack;
		Image image;
		ClickListener listner;
		boolean isAllies;
		TargetShooterSave targetShooterSaveIn;
		int dieRoll; // set sfter exploded
		int adjust;
		int artilleryCount = 0;
		boolean isOver10Plus = false;
		int cntAir;
		String resultBombard;

		public boolean isAI;


		/**
		 *
		 */
		int[] dieTableRow = new int[6];
		int shooterCnt = 0;



		TargetShooterSave(Hex hex, ArrayList<Unit> arrShootersIn, boolean isAllies, int cntAir, boolean isAI){
			targetShooterSaveIn = this;
			this.hexTarget = hex;
			this.cntAir = cntAir;
			this.isAllies = isAllies;
			this.arrShooters.addAll(arrShootersIn);
			this.isAI = isAI;

			image = new Image(gun);
            stack = new Stack();
			stack.add(image);
			arrStack.add(stack);
			stack.setUserObject(this);
			stack.setSize( 156,136);
			Vector2 pos = hex.getCounterPosition();
			stack.setPosition(pos.x - 25, pos.y + 5);

//			stack.setPosition(pos.x - 25, pos.y - 20);
			listner = new ClickListener(Input.Buttons.LEFT) {
				@Override public void clicked (InputEvent event, float x, float y)
				{
					Barrage.instance.removeGun(targetShooterSaveIn);
				}
			};

			stack.addListener(new ClickListener(Input.Buttons.LEFT) {
				@Override public void clicked (InputEvent event, float x, float y)
				{
					Barrage.instance.removeGun(targetShooterSaveIn);
				}


                @Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){

					TargetShooterSave targetShooterSave = (TargetShooterSave)stack.getUserObject();
					int factor =0;
					for (Unit unit:targetShooterSave.arrShooters){
						factor += unit.getCurrenAttackFactor();
					}
					BombardDisplay.instance.initialize(targetShooterSave.hexTarget,targetShooterSave.isAllies);
//					BombardDisplay.instance.updateBarrage(factor);
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){

					BombardDisplay.instance.remove();
					WinBombard.instance.end();
					super.exit(event, x, y, pointer, toActor);
				}
			});

			ardenne.instance.mapStage.addActor(stack);

			int column = shooterCnt/2;    //  + 1;
			if (column > 5){
				column = 5;//
			}
			// fill in the column
			for (int i =0; i < 6;i++){
		//		dieTableRow[i] = BarrageExplode.barrageTable[i][column];
			}
			//BombardDisplay.instance.create(this);


		}

		public void setStackPosition(float x, float y){
			stack.setPosition(x,y);
		}
		public void removeStack(){
			stack.remove();
		}
		public void removeListner(){
			stack.removeListener(listner);

		}
		public void showResult(){
			String str;
			String strTranslate="";
			Image image=null;
			Stack stack = null;
			str = resultBombard;
			if (resultBombard.contains("DG")){
				str ="SU";
				strTranslate= "su0";
				image = new Image(explosion2);
			}
			if (resultBombard.contains("1")){
				str ="SU-1";
				strTranslate= "su1";
				image = new Image(explosion3);


			}
			if (resultBombard.contains("2")){
				str ="SU-2";
				strTranslate= "su2";
				image = new Image(explosion4);

			}
			if (resultBombard.contains("NE")) {
				strTranslate= "noeffect";
				image = new Image(explosion1);

			}

			Label label = new Label(str, labelStyleNameTarget);
			label.setFontScale(1f);
			label.setColor(Color.YELLOW);
			Vector2 vector2 = new Vector2();
			if (hexTarget.getUnitsInHex().size() == 0){
				return;
			}else{
				Unit unit=hexTarget.getUnitsInHex().get(0);
				vector2.x = unit.getMapCounter().getCounterStack().getStack().getX();
				vector2.y = unit.getMapCounter().getCounterStack().getStack().getY();
			}
			stack = new Stack();
			stack.setPosition(vector2.x-5, vector2.y-5 ); /// 38   50
			stack.add(image);
			label.setAlignment(Align.center);
			stack.add(label);
			ardenne.instance.mapStage.addActor(stack);
			arrActors.add(label);
			arrActors.add(image);
			arrActors.add(stack);

			final String strTool = i18NBundle.get(strTranslate);
			/**
			 *  add our special Tooltip listner
			 */
			final WinToolTip winToolTip =  new WinToolTip(strTool);
			stack.addListener(new ClickListener() {

				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					Vector2 v2 = Map.ConvertToScreen(hexTarget); // do here because of pan
					winToolTip.show(v2);
				}
				public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					winToolTip.remove();
				}
			});

			/**
			 * do the results
			 */
			if (resultBombard.contains("NE")) {
				return;
			}
			if(resultBombard.contains("1")){
				Losses losses = new Losses(hexTarget.getUnitsInHex(),1);
				for (Unit unit : hexTarget.getUnitsInHex()) {
					unit.setDisorganized();
				}

			}
			if(resultBombard.contains("2")){
				Losses losses = new Losses(hexTarget.getUnitsInHex(),2);
				for (Unit unit : hexTarget.getUnitsInHex()) {

					unit.setDisorganized();
				}

			}
			if (resultBombard.contains("DG")) {
				for (Unit unit : hexTarget.getUnitsInHex()) {
					unit.setDisorganized();
				}
			}
			/**
			 *  so loss display doesnt wipe out
			 */

			stack.remove();
			ardenne.instance.mapStage.addActor(stack);




//			BarrageExplode.instance.processNext();
		}

		public void calcShooterCnt(){
			artilleryCount +=cntAir * 5;
			for (Unit unitWork:arrShooters){
				shooterCnt +=unitWork.getCurrenAttackFactor();
				artilleryCount +=unitWork.getCurrenAttackFactor();
			}
			if (shooterCnt > 10){
				int above = shooterCnt - 10;
				adjust =  -(above/2);
				isOver10Plus = true;
			}



		}

		public void reDisplay() {
			removeStack();
			ardenne.instance.mapStage.addActor(stack);
		}

		public ArrayList<Unit> getArrShooters() {
			return arrShooters;
		}

		public void updateResult(String resultTable) {
			resultBombard = resultTable;
		}

		public int getaircnt() {
			return cntAir;
		}
	}


	class WinBombardShooters implements Observer{
		TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
		TextureRegion ok =  textureAtlas.findRegion("ok");
		TextureRegion info = textureAtlas.findRegion("info");

		TextTooltip.TextTooltipStyle tooltipStyle;
		Window window;
		Button buttonDone;
		Button buttonClear;
		Stack russianAircraft;
        Stack germanAircraft;
        Stack line;
		Stage stage;
		final int shim =3;
		int cntCountersToProcess =0;
		I18NBundle i18NBundle;
		NinePatch np;
		ArrayList<Unit> unitArrayListShooters;
		ArrayList<Counter> counterArrayList = new ArrayList<>();
		ArrayList<AirplaneStack> airplaneArrayList = new ArrayList<>();
		Barrage barrage;
		BombardDisplay  bombardDisplay;
		int cntBarrage = 0;
		Hex hexTarget;
		private EventListener hitOK;
	//	private EventListener windowInfo;


		WinBombardShooters(Barrage barrage,ArrayList<Unit> unitArrayList, Hex hexTarget) {

			this.unitArrayListShooters = unitArrayList;
			this.barrage = barrage;
			this.hexTarget = hexTarget;
			ardenne.instance.addObserver(this);
			stage = ardenne.instance.guiStage;
			i18NBundle = GameMenuLoader.instance.localization;

			tooltipStyle = new TextTooltip.TextTooltipStyle();
			tooltipStyle.label = new Label.LabelStyle(Fonts.getFont24(), Color.WHITE);
			NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"), 2, 2, 2, 2);
			tooltipStyle.background = new NinePatchDrawable(np);

			String title = i18NBundle.get("windowbarrage");
           // initializeAircraftImages();

			np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
			Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
			window = new Window(title, windowStyle);
			Label lab = window.getTitleLabel();
			lab.setAlignment(Align.center);
			/**
			 *  ok button
			 */
			Image image = new Image(ok);
			final Hex hexShoot = hexTarget;
			image.setScale(1.5f);
			image.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					WinBombard.instance.end();
					end();
					createCannon(hexShoot);
				}
			});
			window.getTitleTable().add(image);
			hitOK = new TextTooltip(
					i18NBundle.format("stackbarrage"),
					tooltipStyle);

			image.addListener(hitOK);
			/**
			 *  info button
			 */
/*			windowInfo = new TextTooltip(
//					i18NBundle.format("selectartillery"),
					tooltipStyle);
			image = new Image(info);
			window.getTitleTable().add(image);
			image.addListener(windowInfo); */

//			window.addListener(windowOK);
			window.setModal(false);
			window.setTransform(true);
			cntCountersToProcess = unitArrayList.size();
			if (isAllies) {
				cntCountersToProcess += Airplane.instance.getCount();
			}
			int widthWindow;
			if (cntCountersToProcess > 1) {
				widthWindow = cntCountersToProcess * (Counter.sizeOnMap + 5) + 100;
			}else {
				widthWindow =  (int) (2.4 *(Counter.sizeOnMap + 1))  + 100;
			}
			int heightWindow = (Counter.sizeOnMap + 40);
			window.setSize(widthWindow,heightWindow);
			Vector2 v2 = new Vector2(hexTarget.getUnitsInHex().get(0).getMapCounter().stack.getX(),
					hexTarget.getUnitsInHex().get(0).getMapCounter().stack.getY());
			/**
			 *  Place all artillery inot windo
			 */
			int retBarrage = setCounters();
			bombardDisplay = new BombardDisplay();
            bombardDisplay.initialize(hexTarget, barrage.isAllies);
			bombardDisplay.updateBarrage(retBarrage);
			window.setPosition(100,100);

			showWindow();
		}
		private void showWindow() {
			Vector2 v2 = GamePreferences.getWindowLocation("barrage");
			if (v2.x == 0 && v2.y == 0) {
				window.setPosition(Gdx.graphics.getWidth() / 2 - window.getWidth() / 2, Gdx.graphics.getHeight() / 2 - window.getHeight() / 2);
				v2.x  = Gdx.graphics.getWidth() - window.getWidth();
				float xMove = Gdx.graphics.getWidth() - window.getWidth();
				window.addAction(Actions.moveTo(xMove, 0, .3F));
			}else{
				window.setPosition(v2.x, v2.y);

			}
			window.remove();
			stage.addActor(window);
		}

		/**
		 * create new UI counters with listners for our window
		 * These counters are differant than the map counters and are only used for this UI screen
		 */
		private int setCounters(){
			final int size =100;
			int retBarrage =0;
			for (Unit unit: unitArrayListShooters)
			{
				final Counter counter = new Counter(unit, Counter.TypeCounter.GUICounter);
				counter.stack.setTransform(true);
				float ratio =(float) size/Counter.size;
				counter.stack.setScale(ratio);
				counter.getCounterStack().adjustFont(.8f);
				counter.getCounterStack().hilite();

				unit.getMapCounter().getCounterStack().hilite();
				unit.getMapCounter().getCounterStack().shade();

				cntBarrage += counter.getUnit().getCurrenAttackFactor();

				counter.stack.addListener(new ClickListener(Input.Buttons.LEFT) {
					@Override public void clicked (InputEvent event, float x, float y)
					{
						if (counter.getCounterStack().isHilited()) {
							counter.getCounterStack().removeHilite();
							counter.getUnit().getMapCounter().getCounterStack().removeHilite();
							counter.getUnit().getMapCounter().getCounterStack().removeShade();
							cntBarrage -= counter.getUnit().getCurrenAttackFactor();
                            bombardDisplay.updateBarrage(cntBarrage);
						}
						else
						{
							counter.getCounterStack().hilite();
							counter.getUnit().getMapCounter().getCounterStack().hilite();
							counter.getUnit().getMapCounter().getCounterStack().shade();

							cntBarrage += counter.getUnit().getCurrenAttackFactor();
                            bombardDisplay.updateBarrage(cntBarrage);
						}
					}
				});
//				counter.stack.setSize( Counter.sizeOnMap,Counter.sizeOnMap);
				window.add(counter.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap).padBottom(8);
				counterArrayList.add(counter);
			}
			if (barrage.isAllies && Airplane.instance.getCount() > 0){
				for (int i=0; i < Airplane.instance.getCount(); i++){
					final AirplaneStack airplaneStack = new AirplaneStack();

					airplaneStack.stack.addListener(new ClickListener(Input.Buttons.LEFT) {
						@Override public void clicked (InputEvent event, float x, float y)
						{
							if (airplaneStack.isHilited) {
								airplaneStack.unHilite();
								cntBarrage -= 5;
								bombardDisplay.updateBarrage(cntBarrage);
								Airplane.instance.add(1);
							}
							else
							{
								airplaneStack.hilite();
								cntBarrage += 5;
								bombardDisplay.updateBarrage(cntBarrage);
								Airplane.instance.remove(1);
							}
						}
					});

					window.add(airplaneStack.stack).width(Counter.sizeOnMap).height(Counter.sizeOnMap);
					airplaneArrayList.add(airplaneStack);


				}


			}
			return cntBarrage;
		}
		private void end(){
			if (window != null) {
				int lastX = (int) window.getX();
				int lastY = (int) window.getY();
				GamePreferences.setWindowLocation("barrage", lastX, lastY);
				window.remove();
			}
			for (Unit unit:arrArtilleryShooters){
				unit.getMapCounter().getCounterStack().removeHilite();
			}
			window.remove();
            BombardDisplay.instance.remove();
            WinBombard.instance.end();
 			ardenne.instance.deleteObserver(this);
		}
		@Override
		public void update(Observable o, Object arg) {
			ObserverPackage op;
			op = (ObserverPackage) arg;
			/**
			 *  check if user has clicked outside the window
			 *  which is  a cancel
			 */
			float  winStartx = window.getX();
			float  winEndx = window.getX()+window.getWidth();
			float  winStarty = window.getY();
			float  winEndy = window.getY()+window.getHeight();
			int reverse = Gdx.graphics.getHeight() - op.y;
			if (op.x < winStartx || op.x > winEndx || reverse < winStarty || reverse > winEndy) {
				cancelBarrage();
			}
		}


		public void cancel() {
			for (Counter counter:winBombardShooters.counterArrayList){
				if (counter.getCounterStack().isHilited()){
					Unit unit = counter.getUnit();
					unit.getMapCounter().getCounterStack().removeShade();
				}
			}

		}
	}
	class AirplaneStack{
		Stack stack;
		boolean isHilited;
		Image imgHilite = new Image(CounterStack.getHilite());
		final int size =100;

		AirplaneStack(){
			stack = new Stack();
			stack.setSize(Counter.size,Counter.size);
			stack.setTransform(true);
			float ratio =(float) size/Counter.size;
			stack.setScale(ratio);
			Image image = new Image(CounterStack.getAllied());
			stack.add(image);
			image = new Image(Airplane.instance.getTexture());
			stack.add(image);
		}
		void hilite(){
			stack.add(imgHilite);
			isHilited = true;
		}
		void unHilite(){
			stack.removeActor(imgHilite);
			isHilited = false;
		}
	}

}

