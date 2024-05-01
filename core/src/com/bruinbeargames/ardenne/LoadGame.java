package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.bruinbeargames.ardenne.GameLogic.Airplane;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.CardsforGame;
import com.bruinbeargames.ardenne.GameLogic.ExitWest;
import com.bruinbeargames.ardenne.GameLogic.HooufGas;
import com.bruinbeargames.ardenne.GameLogic.LehrExits;
import com.bruinbeargames.ardenne.GameLogic.MoreGermanAmmo;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameLogic.SecondPanzerExits;
import com.bruinbeargames.ardenne.GameLogic.SignPost;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameLogic.Weather;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.UI.BottomMenu;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.VictoryPopup;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class LoadGame {
	static public boolean isLoading = false;
	/**
	 * Keep this in sync with savegame class
	 * @param gameToLoad  the xml file for the game load
	 * @param isResume if true do not set game setup info
	 */
public LoadGame(String gameToLoad, boolean isResume) {
	isLoading = true;
    Gdx.app.log("LoadGame","loading ="+gameToLoad);
	XmlReader reader = new XmlReader();
	Element root = null;
	if (!isResume) {
		FileHandle fileHandle = Gdx.files.external(gameToLoad);
		root = reader.parse(fileHandle);
	}else{
		FileHandle fileHandle = SaveGame.getResume();
		root = reader.parse(fileHandle);
	}

    int turn = 0;
    if (root.hasChild("turn")){
		turn = Integer.parseInt(root.getChildByName("turn").getAttribute("value"));
		NextPhase.instance.setTurn(turn);

	}else {
		turn =1;
		new ErrorGame("No Turn XML", this);
	}
    int weather;
	if (root.hasChild("weather")){
		weather = Integer.parseInt(root.getChildByName("weather").getAttribute("value"));
		Weather.instance.loadWeather(weather);
	}

    int phase=0;

    if (root.hasChild("phase")){
	    phase = Integer.parseInt(root.getChildByName("phase").getAttribute("value"));
    }else {
//    	new ErrorGame("No Phase XML", this);
    }
	if (root.hasChild("uid")){
		String uid  = root.getChildByName("uid").getAttribute("value");
		NextPhase.instance.setProgramUID(uid);
	}

    if (root.hasChild("scenario")){
	    int scene = Integer.parseInt(root.getChildByName("scenario").getAttribute("value"));
    	GameSetup.instance.setScenario(GameSetup.Scenario.values()[scene]);
    }
	if (root.hasChild("bombers")){
		int cntAllied = Integer.parseInt(root.getChildByName("bombers").getAttribute("value"));
		Weather.instance.setCurrentBombers(cntAllied);
	}

	if (root.hasChild("cntdebug")){
		int cntDebugIn = Integer.parseInt(root.getChildByName("cntdebug").getAttribute("value"));
		NextPhase.instance.cntDebug = cntDebugIn;
	}else {
		//new ErrorGame("No Debug CountIn XML", this);
	}
	if (root.hasChild("airplane")){
		int airCnt = Integer.parseInt(root.getChildByName("airplane").getAttribute("value"));
		Airplane.instance.load(airCnt);
	}else {
		//new ErrorGame("No Debug CountIn XML", this);
	}

    /**
     * see Unit.getXMLUnits on how xml is loaded
     */
     Element units =   root.getChildByName("units");
     Array<Element> xmlUnitAll = units.getChildrenByName("unit");
     ArrayList<Unit> arrCheck = new ArrayList<>();
     for (Element xmlunit: xmlUnitAll)
    {
   	    int ID  = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
 	  	if (ID == 17){
 	  		int i=0;

		}
 	    Unit unit=Unit.getUnitByID(ID);
		Gdx.app.log("LoadGame", "ID=" + ID);
		if (unit == null){
			Gdx.app.log("LoadGame", "ID is null");
			continue;
		}

		if (arrCheck.contains(unit))
 	    {
 	    	break;
 	    }
 	    else {
 	    	arrCheck.add(unit);
 	    }
		int hexX = Integer.parseInt(xmlunit.getChildByName("hexX").getAttribute("value"));
		int hexY = Integer.parseInt(xmlunit.getChildByName("hexY").getAttribute("value"));
		Hex hex = Hex.hexTable[hexX][hexY];
		int stepNum = Integer.parseInt(xmlunit.getChildByName("currentStep").getAttribute("value"));

		unit.setStep(stepNum);
		int move = Integer.parseInt(xmlunit.getChildByName("currentMove").getAttribute("value"));
		unit.setCurrentMovement(move);
		int attack = Integer.parseInt(xmlunit.getChildByName("currentAttack").getAttribute("value"));
		unit.setCurrentAttackFactor(attack);
		int defense = Integer.parseInt(xmlunit.getChildByName("currentDefense").getAttribute("value"));
		unit.setCurrentDefenseFactor(defense);
		attack = Integer.parseInt(xmlunit.getChildByName("atstartAttack").getAttribute("value"));
		unit.setAtStartAttackFactor(attack);
		defense = Integer.parseInt(xmlunit.getChildByName("atstartDefense").getAttribute("value"));
		unit.setAtStartDefenseFactor(defense);
		int turnMoved = Integer.parseInt(xmlunit.getChildByName("turnMoved").getAttribute("value"));
		unit.setMovedThisTurn(turnMoved);
		boolean isDG = Boolean.parseBoolean(xmlunit.getChildByName("dg").getAttribute("value"));
		if (isDG) {
			unit.setDisorganized();
		}
		if (unit.isArtillery) {
			boolean isLimber = Boolean.parseBoolean(xmlunit.getChildByName("limber").getAttribute("value"));
			if (isLimber) {
				unit.setArtilleryLimbered();
			} else {
				unit.setArtilleryUnLimbered();
			}
		}
		if (xmlunit.hasChild("attacked")) {
			unit.hasAttackedThisTurn = Boolean.parseBoolean(xmlunit.getChildByName("attacked").getAttribute("value"));
		}
		if (xmlunit.hasChild("beenattacked")) {
			unit.hasBeenAttackedThisTurn = Boolean.parseBoolean(xmlunit.getChildByName("beenattacked").getAttribute("value"));
		}
		if (xmlunit.hasChild("canattack")) {

			unit.canAttackThisTurn = Boolean.parseBoolean(xmlunit.getChildByName("canattack").getAttribute("value"));
		}
		if (xmlunit.hasChild("eliminated")) {
			boolean isEliminated = Boolean.parseBoolean(xmlunit.getChildByName("eliminated").getAttribute("value"));
			if (isEliminated)
			{
				unit.eliminate(false);
			}
		}
		if (!unit.isEliminated()){
			unit.placeOnBoard(hex);
		}


	}
     if (root.hasChild("cardsforgame")) {
		 Element hexs = root.getChildByName("cardsforgame");
		 ArrayList<CardsforGame> arrGermans = new ArrayList<>();
		 ArrayList<CardsforGame> arrAllied = new ArrayList<>();

		 Array<Element> xmlHexAll = hexs.getChildrenByName("card");
		 for (Element xmlhex : xmlHexAll) {
		 	String desc  = xmlhex.getAttribute("value");
		 	CardsforGame card = CardHandler.instance.getByKey(desc);
		 	if (card.getAllied()){
		 		arrAllied.add(card);
			}else{
		 		arrGermans.add(card);
			}
		 }
		 CardHandler.instance.setAlliedChosen(arrAllied);
//		 CardHandler.instance.doInEffectAlliedCards();
		 CardHandler.instance.setGermanChosen(arrGermans);
//		 CardHandler.instance.doInEffectGermanCards();


	 }
	if (root.hasChild("cardsplayed")) {
		Element hexs = root.getChildByName("cardsplayed");
		ArrayList<CardsforGame> arrGermans = new ArrayList<>();
		ArrayList<CardsforGame> arrAllied = new ArrayList<>();

		Array<Element> xmlHexAll = hexs.getChildrenByName("card");
		for (Element xmlhex : xmlHexAll) {
			String desc  = xmlhex.getChildByName("name").getAttribute("value");
			CardsforGame card = CardHandler.instance.getByKey(desc);
			String updateTurn = xmlhex.getChildByName("updateturn").getAttribute("value");
			card.setTurnPlayed(Integer.parseInt(updateTurn));
			String endTurn = xmlhex.getChildByName("turnend").getAttribute("value");
			card.setTurnEnd(Integer.parseInt(endTurn));
			if (desc.contains("hooufgas")){
				String hougas = xmlhex.getChildByName("houfgas").getAttribute("value");
				if (hougas.contains("true")){
					HooufGas.instance.setBroken(true);
				}else{
					HooufGas.instance.setBroken(false);
				}
			}
			if (desc.contains("moreammo")){
				MoreGermanAmmo.instance.setON();
				for (Unit unit : Unit.getOnBoardAxis()) {
					unit.setArtAmmo(2);
				}
			}
			if (desc.contains("szorney2")){
				if (card.getTurnEnd() >= turn){
					CardHandler.instance.setJunctionSet(false);
					SignPost.instance.remove(turn);
				}

			}
			if (card.getAllied()){
				arrAllied.add(card);
			}else{
				arrGermans.add(card);
			}
		}
		CardHandler.instance.setGermanPlayed(arrGermans);
		CardHandler.instance.setAlliedPlayed(arrAllied);
		CardHandler.instance.loadGame();

	}
	if (root.hasChild("bridges")) {
		Element hexs = root.getChildByName("bridges");
		Array<Element> xmlHexAll = hexs.getChildrenByName("bridge");
		for (Element xmlhex : xmlHexAll) {
			int xa = Integer.parseInt(xmlhex.getChildByName("xPosa").getAttribute("value"));
			int ya = Integer.parseInt(xmlhex.getChildByName("yPosa").getAttribute("value"));
			int xb = Integer.parseInt(xmlhex.getChildByName("xPosb").getAttribute("value"));
			int yb = Integer.parseInt(xmlhex.getChildByName("yPosb").getAttribute("value"));
			Hex hexA = Hex.hexTable[xa][ya];
			Hex hexB = Hex.hexTable[xb][yb];
			Bridge bridge = Bridge.findBridge(hexA,hexB);
			bridge.blowUp();
		}
	}
	boolean initAllies = true;
	boolean initAxis = true;
	if (root.hasChild("targetshootersaves")) {
		Element target = root.getChildByName("targetshootersaves");
		Array<Element> xmltargetAll = target.getChildrenByName("targetshootersave");
		for (Element xmltarget : xmltargetAll) {
			int xa = Integer.parseInt(xmltarget.getChildByName("xPosa").getAttribute("value"));
			int ya = Integer.parseInt(xmltarget.getChildByName("yPosa").getAttribute("value"));
			Hex hex = Hex.hexTable[xa][ya];
			int airCnt = Integer.parseInt(xmltarget.getChildByName("aircnt").getAttribute("value"));
			ArrayList<Unit> arrUnits = new ArrayList();
			xmlUnitAll = xmltarget.getChildrenByName("units");

			for (Element xmlunit: xmlUnitAll)
			{
				Element unitxml =xmlunit.getChildByName("unit");
				int ID = Integer.parseInt(unitxml.getChildByName("ID").getAttribute("value"));
				Unit unit= Unit.getUnitByID(ID);
				arrUnits.add(unit);
			}
			if (arrUnits.get(0).isAllies && initAllies){
				initAllies = false;
				Barrage.instance.intialize(true,false);
			}
			if (arrUnits.get(0).isAxis && initAxis){
				initAxis = false;
				Barrage.instance.intialize(false,false);
			}
			Barrage.instance.createTargetShooterSave(hex,arrUnits,airCnt, false);
		}


	}




	if (root.hasChild("hexsattacked")) {
		Element hexs = root.getChildByName("hexsattacked");
		Array<Element> xmlHexAll = hexs.getChildrenByName("hex");
		for (Element xmlhex : xmlHexAll) {
			int x = Integer.parseInt(xmlhex.getChildByName("xPos").getAttribute("value"));
			int y = Integer.parseInt(xmlhex.getChildByName("yPos").getAttribute("value"));
			Hex.hexTable[x][y].setHasBeenAttackedThisTurn(true);
		}
	}

	Element reinforcements =   root.getChildByName("reinforcements");
	if (reinforcements != null) {

		xmlUnitAll = reinforcements.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			 int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			 Unit unit= Unit.getUnitByID(ID);
			 if (unit != null) {
				 Reinforcement.instance.removeReinforcement(unit);
			 }
		}
	 }
	Element exitLehr =   root.getChildByName("exitlehr");
	if (exitLehr != null) {

		xmlUnitAll = exitLehr.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			if (unit != null) {
				ExitWest.instance.addLehr(unit);
			}
		}
	}
	Element exit2ndpanzer =   root.getChildByName("exit2ndpanzer");
	if (exit2ndpanzer != null) {

		xmlUnitAll = exit2ndpanzer.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			if (unit != null) {
				ExitWest.instance.add2ndPanzer(unit);
			}
		}
	}
	Element panzerexit1 =   root.getChildByName("2ndpanzerexit1");
	if (panzerexit1 != null) {

		xmlUnitAll = panzerexit1.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			SecondPanzerExits.instance.exitUnitLoad(SecondPanzerExits.instance.hexExit1,unit);
		}
	}
	Element panzerexit2 =   root.getChildByName("2ndpanzerexit2");
	if (panzerexit2 != null) {

		xmlUnitAll = panzerexit2.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			if (unit != null) {
				SecondPanzerExits.instance.exitUnitLoad(SecondPanzerExits.instance.hexExit2,unit);
			}

		}
	}
	Element lpanzerexit1 =   root.getChildByName("lehrpanzerexit1");
	if (lpanzerexit1 != null) {

		xmlUnitAll = lpanzerexit1.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			if (unit != null) {
				LehrExits.instance.exitUnitLoad(LehrExits.instance.hexExit1,unit);
			}
		}
	}
	Element lpanzerexit2 =   root.getChildByName("lehrpanzerexit2");
	if (lpanzerexit2 != null) {

		xmlUnitAll = lpanzerexit2.getChildrenByName("unit");
		for (Element xmlunit: xmlUnitAll)
		{
			int ID = Integer.parseInt(xmlunit.getChildByName("ID").getAttribute("value"));
			Unit unit= Unit.getUnitByID(ID);
			if (unit != null) {
				LehrExits.instance.exitUnitLoad(LehrExits.instance.hexExit1,unit);
			}

		}
	}



	/**
     * Get all Supply source that are german, game automatically sets all hexs to russian
     */

	/**
	 *  only set game specific info if its a regular saved game
	 */
	boolean boolval;
//	if (!isSpecial) {
		if (ardenne.instance.isSetHotSeat){
			GameSetup.instance.setHotSeatGame(true);
			GameSetup.instance.setGermanVersusAI(false);
			GameSetup.instance.setAlliedVersusAI(false);
		}else {
			if (root.hasChild("hotseat")) {
				boolval = Boolean.parseBoolean(root.getChildByName("hotseat").getAttribute("value"));
				GameSetup.instance.setHotSeatGame(boolval);
			}

			if (root.hasChild("germanversusai")) {
				boolval = Boolean.parseBoolean(root.getChildByName("germanversusai").getAttribute("value"));
				GameSetup.instance.setGermanVersusAI(boolval);
			}
			if (root.hasChild("alliedversusai")) {
				boolval = Boolean.parseBoolean(root.getChildByName("alliedversusai").getAttribute("value"));
				GameSetup.instance.setAlliedVersusAI(boolval);
			}
		}
		if (root.hasChild("easygerman")) {
			boolval = Boolean.parseBoolean(root.getChildByName("easygerman").getAttribute("value"));
			GameSetup.instance.setEasyGerman(boolval);
		}
		if (root.hasChild("easyamerican")) {
			boolval = Boolean.parseBoolean(root.getChildByName("easyamerican").getAttribute("value"));
			GameSetup.instance.setEasyAmerican(boolval);
		}
		if (root.hasChild("balanced")) {
			boolval = Boolean.parseBoolean(root.getChildByName("balanced").getAttribute("value"));
			GameSetup.instance.setBalanced(boolval);
		}
		if (root.hasChild("scenario")) {
			int scene = Integer.parseInt(root.getChildByName("scenario").getAttribute("value"));
			GameSetup.instance.setScenario(GameSetup.Scenario.values()[scene]);
		}
		if (root.hasChild("houfflaize")) {
			boolval = Boolean.parseBoolean(root.getChildByName("houfflaize").getAttribute("value"));
			if (boolval) {
				Supply.instance.addHooufGas();
				HooufGas.instance.setHooufGas();
			} else {
				Supply.instance.removeHooufgas();
				HooufGas.instance.setBroken(boolval);
			}
		}

//	}



 	Gdx.app.log("LoadGame","Turn="+turn);
	TurnCounter.instance.updateTurn(turn, NextPhase.instance.weather.getCurrentType());

	setPhase(phase);


}
	/**
	 *  SetPhase and give control to the game  
	 * @param phase
	 */

	private void setPhase(int phase) {
		if (NextPhase.instance.turn > GameSetup.instance.getScenario().getLength()){
			String winner = VictoryPopup.instance.announceVictorAtEnd();
			BottomMenu.instance.setEnablePhaseChange(false);
			return;
		}
		NextPhase.instance.setPhaseDirect(phase);
		EventPopUp.instance.hide();
		NextPhase.instance.setPhase();

	}


		

	
	
}

