package com.bruinbeargames.ardenne;

import com.badlogic.gdx.files.FileHandle;
import com.bruinbeargames.ardenne.GameLogic.Airplane;
import com.bruinbeargames.ardenne.GameLogic.CardHandler;
import com.bruinbeargames.ardenne.GameLogic.CardsforGame;
import com.bruinbeargames.ardenne.GameLogic.ExitWest;
import com.bruinbeargames.ardenne.GameLogic.HooufGas;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.GameLogic.Weather;
import com.bruinbeargames.ardenne.Hex.Bridge;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SaveGame {


	public SaveGame(String fileName) {
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH-mm-ss");
		Date date = new Date();
		fileName = fileName + dateFormat.format(date);
		StringBuffer saveGame = appendData();
		FileHandle file = GamePreferences.getSaveGamesLocation(fileName + ".xml");
		file.writeString(saveGame.toString(), false);
	}
	public static boolean canSaveGame(){
//		if (NextPhase.instance.getPhase() <= Phase.GERMAN_CARD.ordinal()){
//			return false;
//		}

		if (NextPhase.instance.isInBarrage()){
			return false;
		}
		if (NextPhase.instance.isAIControl){
			return false;
		}
		return true;
	}

	public static StringBuffer appendData() {

			StringBuffer saveGame = new StringBuffer();
			saveGame.append("<game>");

			saveGame.append("<turn value=");
			int turn =     NextPhase.instance.turn;
			saveGame.append("\""+String.format("%02d", turn)+"\" />");
		    int wType = Weather.instance.getCurrentType().ordinal();
			saveGame.append("<weather value=");
			saveGame.append("\""+String.format("%02d", wType)+"\" />");
			int phase = NextPhase.instance.getPhase();
			saveGame.append("<phase value=");
			saveGame.append("\""+String.format("%02d", phase)+"\" />");
			saveGame.append("<scenario value=");
			int  scenario = GameSetup.instance.getScenario().ordinal();
			saveGame.append("\""+String.format("%02d", scenario)+"\" />");
			saveGame.append("<bombers value=");
			int cntallied = Weather.instance.getCurrentBombers();
			saveGame.append("\""+String.format("%02d", cntallied)+"\" />");
			saveGame.append("<cntdebug value=");
			int cntDebug = NextPhase.instance.cntDebug;
			saveGame.append("\""+String.format("%02d", cntDebug)+"\" />");
			saveGame.append("<airplane value=");
			int cntAir = Airplane.instance.getCount();
			saveGame.append("\""+String.format("%02d", cntAir)+"\" />");
			ArrayList<CardsforGame> arrCards = new ArrayList<>();
			arrCards.addAll(CardHandler.instance.getArrCardsChosenAllied());
			arrCards.addAll(CardHandler.instance.getArrCardsChosenGerman());
			if (arrCards.size() > 0) {
				saveGame.append("<cardsforgame>");
				for (CardsforGame cardsforGame: arrCards){
					saveGame.append("<card value=");
					String desc = cardsforGame.getDecriptionKey();
					saveGame.append("\""+desc+"\"/>");
				}
				saveGame.append("</cardsforgame>");
		}
		arrCards.clear();
		arrCards.addAll(CardHandler.instance.getArrCardsPlayedAllied());
		arrCards.addAll(CardHandler.instance.getArrCardsPlayedGerman());
		if (arrCards.size() > 0) {
			saveGame.append("<cardsplayed>");
			for (CardsforGame cardsforGame: arrCards){
				saveGame.append("<card>");
				saveGame.append("<name value=");
				String desc = cardsforGame.getDecriptionKey();
				saveGame.append("\""+desc+"\"/>");
				saveGame.append("<updateturn value=");
				String turnplayed = Integer.toString(cardsforGame.getTurnPlayed());
				saveGame.append("\""+turnplayed+"\"/>");
				saveGame.append("<turnend value=");
				String turnend = Integer.toString(cardsforGame.getTurnEnd());
				saveGame.append("\""+turnend+"\"/>");
				if (desc.contains("hooufgas")){
					saveGame.append("<houfgas value=");
					String houfGas = null;
					if (HooufGas.instance.isHooufGas()) {
						houfGas = "true";
					}else{
						houfGas = "false";
					}
					saveGame.append("\""+houfGas+"\"/>");
				}
				saveGame.append("</card>");
			}
			saveGame.append("</cardsplayed>");
		}
/*		ArrayList<Barrage.TargetShooterSave> arrTarget = new ArrayList<>();
		arrTarget.addAll(Barrage.instance.targetShooterSaveArrayListAllies);
		arrTarget.addAll(Barrage.instance.targetShooterSaveArrayListAxis);
		if (arrTarget.size() > 0){
			saveGame.append("<targetshootersaves>");
			for (Barrage.TargetShooterSave tg:arrTarget){
				saveGame.append("<targetshootersave>");
				saveGame.append("<xPosa value=\"");
				saveGame.append(String.format("%02d", tg.hexTarget.xTable));
				saveGame.append("\"/>");
				saveGame.append("<yPosa value=\"");
				saveGame.append(String.format("%02d", tg.hexTarget.yTable));
				saveGame.append("\"/>");
				saveGame.append("<aircnt value=\"");
				saveGame.append(String.format("%02d", tg.getaircnt()));
				saveGame.append("\"/>");
				saveGame.append("<units>");

				for (Unit unit:tg.getArrShooters()){
					saveGame.append("<unit>");
					saveGame.append("<ID value=\"");
					saveGame.append(String.format("%02d", unit.ID));
					saveGame.append( "\"/>");
					saveGame.append("</unit>");

				}
				saveGame.append("</units>");
				saveGame.append("</targetshootersave>");
			}
			saveGame.append("</targetshootersaves>");
		} */
		ArrayList<Bridge> arrBridgesBlown = new ArrayList<>();
		for (Bridge bridge: Bridge.arrBridges){
			if (bridge.getBlown()){
				arrBridgesBlown.add(bridge);
			}
		}

		if (arrBridgesBlown.size() > 0) {
			saveGame.append("<bridges>");
			for (Bridge bridge: arrBridgesBlown){
				saveGame.append("<bridge>");
				saveGame.append("<xPosa value=\"");
				saveGame.append(String.format("%02d", bridge.getHex1().xTable));
				saveGame.append("\"/>");
				saveGame.append("<yPosa value=\"");
				saveGame.append(String.format("%02d", bridge.getHex1().yTable));
				saveGame.append("\"/>");
				saveGame.append("<xPosb value=\"");
				saveGame.append(String.format("%02d", bridge.getHex2().xTable));
				saveGame.append("\"/>");
				saveGame.append("<yPosb value=\"");
				saveGame.append(String.format("%02d", bridge.getHex2().yTable));
				saveGame.append("\"/>");
				saveGame.append("</bridge>");
			}
			saveGame.append("</bridges>");
		}
		if (ExitWest.instance.getExitLehr().size() > 0){
			saveGame.append("<exitlehr>");
			for (Unit unit:ExitWest.instance.getExitLehr()){
				saveGame.append("<unit>");
				saveGame.append("<ID value=\"");
				saveGame.append(String.format("%02d", unit.ID));
				saveGame.append( "\"/>");
				saveGame.append("</unit>");

			}
			saveGame.append("</exitlehr>");
		}
		if (ExitWest.instance.getExit2ndPanzer().size() > 0){
			saveGame.append("<exit2ndpanzer>");
			for (Unit unit:ExitWest.instance.getExit2ndPanzer()){
				saveGame.append("<unit>");
				saveGame.append("<ID value=\"");
				saveGame.append(String.format("%02d", unit.ID));
				saveGame.append( "\"/>");
				saveGame.append("</unit>");
			}
			saveGame.append("</exit2ndpanzer>");
		}

			/**
			 *  get both allied and axis
			 */
			ArrayList<Unit> arrUnits = Unit.getOnBoardAllied();
			arrUnits.addAll(Unit.getOnBoardAxis());
			arrUnits.addAll(Unit.getEliminated(true));
			arrUnits.addAll(Unit.getEliminated(false));

			saveGame.append(Unit.getXMLUnits(arrUnits));
			ArrayList<Hex> arrAttacked = Hex.getAttackedThisTurn();
			if (arrAttacked.size() > 0) {
				saveGame.append("<hexsattacked>");
				for (Hex hex : arrAttacked){
					saveGame.append("<hex>");
					saveGame.append("<xPos value=\"");
					saveGame.append(String.format("%02d", hex.xTable));
					saveGame.append("\"/>");
					saveGame.append("<yPos value=\"");
					saveGame.append(String.format("%02d", hex.yTable));
					saveGame.append("\"/>");
					saveGame.append("</hex>");
				}
				saveGame.append("</hexsattacked>");
			}
			saveGame.append(Reinforcement.instance.saveRemoved());
			saveGame.append("<hotseat value=\"");
			if (GameSetup.instance.isHotSeatGame()){
				saveGame.append("true");
			}else{
				saveGame.append("false");
			}
			saveGame.append("\"/>");

			saveGame.append("<germanversusai value=\"");
			if (GameSetup.instance.isGermanVersusAI()){
				saveGame.append("true");
			}else{
				saveGame.append("false");
			}
			saveGame.append("\"/>");

			saveGame.append("<alliedversusai value=\"");
			if (GameSetup.instance.isAlliedVersusAI()){
				saveGame.append("true");
			}else{
				saveGame.append("false");
			}
			saveGame.append("\"/>");
		saveGame.append("<easygerman value=\"");
		if (GameSetup.instance.isEasyGerman()){
			saveGame.append("true");
		}else{
			saveGame.append("false");
		}
		saveGame.append("\"/>");

		saveGame.append("<easyamerican value=\"");
		if (GameSetup.instance.isEasyAmerican()){
			saveGame.append("true");
		}else{
			saveGame.append("false");
		}
		saveGame.append("\"/>");
		saveGame.append("<balanced value=\"");
		if (GameSetup.instance.isBalanced()){
			saveGame.append("true");
		}else{
			saveGame.append("false");
		}
		saveGame.append("\"/>");
/**
 *  save Houflaize
 */
		saveGame.append("<houfflaize value=\"");
		if (Supply.instance.getHoouflaize()){
			saveGame.append("true");
		}else{
			saveGame.append("false");
		}
		saveGame.append("\"/>");

		int scene = GameSetup.instance.getScenario().ordinal();
		saveGame.append("<scenario value=");
		saveGame.append("\""+String.format("%02d", scene)+"\" />");
		saveGame.append("</game>");

		return saveGame;
		}


	static public void zipDebugFiles(boolean isStart) throws IOException {
		ArrayList<File> arrFile = new ArrayList<>();
		File folder = GamePreferences.getSaveAIDebug().file();
		if (folder.listFiles() == null || folder.listFiles().length == 0) {
			return;
		}
		if (folder.listFiles() != null) {
			for (File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {

				} else {
					if (fileEntry.toString().indexOf("xml") >= 0) {
						arrFile.add(fileEntry);// do something interesting here
					}
				}
			}
		}

		String name;
		if (isStart) {
			name = "StartGameSave.zip";
		} else {
			name = "EndGameSave.zip";
		}

		FileOutputStream fos = new FileOutputStream(name);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		for (File filetoZip : arrFile) {
			FileInputStream fis = new FileInputStream(filetoZip);
			ZipEntry zipEntry = new ZipEntry(filetoZip.getName());
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[10024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();
	}



	static public void SaveDebug(String filename, int cnt) {
		if (cnt == 1) { // start of game
			try {
				zipDebugFiles(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			File folder = GamePreferences.getSaveAIDebug().file();
			if (folder.listFiles() != null) {
				for (File fileEntry : folder.listFiles()) {
					if (fileEntry.isDirectory()) {

					} else {
						if (fileEntry.toString().contains("xml")) {
							fileEntry.delete();// do something interesting here
						}
					}
				}
			}
		}
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH-mm-ss");
		Date date = new Date();
		filename = filename + dateFormat.format(date)+".xml";
		StringBuffer saveGame = appendData();
		FileHandle file = GamePreferences.getSaveAIDebug(filename + ".xml");

		file.writeString(saveGame.toString(), false);


	}
	static public void SaveLastPhase(String filename, int cnt) {
//		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH-mm-ss");
		/**
		 *  Will allow save phase but not save turn for ai
		 */
		if (canSaveGame()) {
			Date date = new Date();
			StringBuffer saveGame = appendData();
			FileHandle file = GamePreferences.getSaveGamesLocation(filename + ".xml");
			file.writeString(saveGame.toString(), false);
		}
	}



}


