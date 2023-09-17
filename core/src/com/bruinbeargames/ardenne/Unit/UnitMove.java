package com.bruinbeargames.ardenne.Unit;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.AI.AIUtil;
import com.bruinbeargames.ardenne.ErrorGame;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexCanNotCross;
import com.bruinbeargames.ardenne.Hex.HexHelper;

import java.util.ArrayList;

public class UnitMove {

	public Unit unit;
	int moveLength;
	boolean isAllowedMOA;
	boolean checkTerrain;
	boolean checkAdjacent = false;
	ArrayList<Hex>[] arrHexSolution = new ArrayList[11];
	ArrayList<Float>[] arrHexCost = new ArrayList[11];
	ArrayList<Hex>[] arrHexAdjacentEnemy = new ArrayList[11];
	ArrayList<Float>[] arrBestScore= new ArrayList[11];
	Hex hexStart;
	boolean isFakeAI = false;
	int thread;

	public UnitMove(Unit unit, int moveLength, boolean isAllowedMOA, boolean checkTerrain, int thread) {
//		Gdx.app.log("UnitMove","Constructor unit="+unit+" Lenght= "+moveLength+" isAllowedMOA="+isAllowedMOA+" checkTerrain="+checkTerrain);
		this.unit = unit;
		this.moveLength = moveLength;
		this.isAllowedMOA = isAllowedMOA;
		this.checkTerrain = checkTerrain;
		this.thread = thread;
		hexStart = unit.getHexOccupy();
		isFakeAI = false;
		for (int i=0; i<11;i++){;
			arrHexAdjacentEnemy[i] = new ArrayList<>();
			arrHexCost[i] = new ArrayList<>();
			arrHexSolution[i] = new ArrayList<>();
			arrBestScore[i] = new ArrayList<>();
		}
		reDO();
	}


	public UnitMove(Unit unit, int moveLength, boolean isAllowedMOA, boolean checkTerrain, Hex hexStart, int thread) {
//		Gdx.app.log("UnitMove","Constructor unit="+unit+" Lenght= "+moveLength+" isAllowedMOA="+isAllowedMOA+" checkTerrain="+checkTerrain+" Hex Start="+hexStart);
		this.unit = unit;
		this.moveLength = moveLength;
		this.isAllowedMOA = isAllowedMOA;
		this.checkTerrain = checkTerrain;
		this.hexStart = hexStart;
		this.thread = thread;
		isFakeAI = false;
		for (int i=0; i<11;i++){;
			arrHexAdjacentEnemy[i] = new ArrayList<>();
			arrHexCost[i] = new ArrayList<>();
			arrHexSolution[i] = new ArrayList<>();
			arrBestScore[i] = new ArrayList<>();
		}

		reDO();
	}


	/**
	 *  Redo agin so that the calcmovecost are reset
	 */
	public void reDO(){
//		Gdx.app.log("UnitMove", "reDo");

		Hex.loadCalcMoveCost(thread);
		if (unit.getCurrenAttackFactor() < 1){
			isAllowedMOA = false;
		}
		searchHexes(hexStart,moveLength,isAllowedMOA, checkTerrain,isFakeAI, thread);

		if (checkAdjacent) {
			arrHexSolution[thread].addAll(arrHexAdjacentEnemy[thread]);
		}
		HexHelper.removeDupes(arrHexSolution[thread]);
	}
	public ArrayList<Hex> getMovePossible() {
		return getMovePossible(0);
	}


	public ArrayList<Hex> getMovePossible(int threadUse){
		ArrayList<Hex> arrReturn = new ArrayList<>();
		/**
		 * remove hexes that will overstack except for supply
		 */
		ArrayList<Hex> arrRemove = new ArrayList<>();
		for (Hex hex:arrHexSolution[threadUse]) {
			int stackPossible = hex.getStacksIn();
			if (unit.isTransport){
				stackPossible = 0;
			}else {
				stackPossible += unit.getCurrentStep();
			}
			if (stackPossible > Hex.stackMax){
				arrRemove.add(hex);
			}
		}
		arrReturn.addAll(arrHexSolution[threadUse]);
		arrReturn.removeAll(arrRemove);
		HexHelper.removeDupes(arrReturn);
		return arrReturn;
	}



	private void searchHexes(Hex hexSearch, float moveCostLeft, boolean isMobileAssualt, boolean checkTerrain, boolean isFakeAI, int thread)
	{
		if (moveCostLeft == 0)
		{
			return;
		}
//		if (hexSearch.getHexCoords().equals("7,20")){
//			int j=0;
//		}

//		ArrayList<Hex> arrSurr = hexSearch.getSurround();
		float  moveCost;
		if (hexSearch == null){
			int bk=0;
		}
		for (Hex hex: hexSearch.arrSurroundHex)

		{
//			if (hex.getHexCoords().equals("2,13")){
//				int j=0;
//			}

			if (hex == null){
				int j=0;
			}
			if (hex.xTable >= 0 && hex.xTable < Hex.xEnd && hex.yTable >= 0 && hex.yTable < Hex.yEnd){
				float flt = Move.instance.cost(unit, hexSearch, hex, isMobileAssualt, checkTerrain, thread);
				float k = flt;
				if (checkAdjacent && k == 777) {
					if (!arrHexAdjacentEnemy[thread].contains(hex)) {
						arrHexAdjacentEnemy[thread].add(hex);
					}

				}
				if (Move.isMOAEncountered){
					if (moveCostLeft >= k) {
						k = moveCostLeft;
					}
				}
				if (moveCostLeft >= k)
				{
					moveCost = moveCostLeft - k;
					if (arrHexSolution[thread].contains(hex)) // done hex before ?
					{
						if (hex.getCalcMoveCost(thread) < moveCost)
						{
							hex.setCalcMoveCost(moveCost, thread);
							int ix = arrHexSolution[thread].indexOf(hex);

							float f = arrBestScore[thread].get(ix);
							if (moveCost > f) {

								arrBestScore[thread].set(ix, moveCost);
							}
							searchHexes(hex, moveCost, isMobileAssualt, checkTerrain,isFakeAI,thread);
						}
					}
					else
					{
						arrHexSolution[thread].add(hex);
						arrBestScore[thread].add(moveCost);
						hex.setCalcMoveCost(moveCost, thread);
						arrHexCost[thread].add(hex.getCalcMoveCost(thread));
						searchHexes(hex, moveCost, isMobileAssualt, checkTerrain, isFakeAI, thread);
					}
				}
			}
		}
	}
	/**
	 * find path forward to this destination from start hex
	 * if the calculated movement paths may have been overwritten
	 * set redo to true to get fresh calculated path
	 * @return arryroute in hexes
	 */
	public ArrayList<Hex> getLeastPath(Hex hexEnd, boolean isRedoCalc, ArrayList<Hex> arrReference) {
		Gdx.app.log("UnitMove","getLeastPath unit="+unit+" hex Start"+unit.getHexOccupy()+" hexEnd="+hexEnd);
		ArrayList<Hex> arrReturn= new ArrayList<>();
		boolean isMOASpecial = false;
		if (arrReference != null){
			if (hexEnd.getUnitsInHex().size() > 0){
				if (hexEnd.getUnitsInHex().get(0).isAllies && unit.isAxis||
						hexEnd.getUnitsInHex().get(0).isAxis && unit.isAllies){
					isMOASpecial = true;
				}
			}
		}
		if (unit.getID() == 43) {
			int b=0;

		}
		if (isRedoCalc) {
			Gdx.app.log("UnitMove","getLeastPath reDo");
			reDO(); // get correct calcmove
		}
		if (!arrHexSolution[0].contains(hexEnd) && hexEnd != hexStart){
			// find closest
			hexEnd =  AIUtil.findClosestHex(arrHexSolution[0], hexEnd);
			if (hexEnd == null) {
				Gdx.app.log("UnitMove", "Has something changed ");
				Gdx.app.log("UnitMove", "Hex =" + hexEnd + "Is no longer in Solution");
				Gdx.app.log("UnitMove", "unit=" + unit + " Length= " + moveLength + " isAllowedMOA=" + isAllowedMOA + " checkTerrain=" + checkTerrain + " isFakeAI" + isFakeAI + " hexSolution length=" + arrHexSolution[thread].size() + " hexEnd=" + hexEnd);
				ErrorGame errorGame = new ErrorGame("Trying to  find Route but hex not in solution",this);
			}
		}

//		boolean isStartTidal = false;
//		boolean isStartTidal2 = false;
//		if (hexStart == hexO1){
//			isStartTidal = true;
//		}
//		if (hexStart == hexI1 || hexStart == hexI2){
//			isStartTidal2 = true;
//		}

		ArrayList<Hex> arrReturnBackWards = new ArrayList<>();
		arrReturnBackWards.add(hexEnd);
		Hex hexEndSearch = hexEnd;
		Hex hexMOAEnd = hexEnd;

		while (hexEndSearch != hexStart) {
			ArrayList<Hex> arrWork = hexEndSearch.getSurround();


			float cost = 0;//hexEndSearch.getCalcMoveCost(0);
			if (arrWork.contains(hexStart) && HexCanNotCross.checkCanCross(hexStart,hexEndSearch))

//			if (arrWork.contains(hexStart) && !(isStartTidal && (hexEndSearch == hexI1 || hexEndSearch == hexI2))&&
//		    !(isStartTidal2 &&(hexEndSearch==hexO1)))
	//		if (arrWork.contains(hexStart) && !HexHelper.canNotCrossInto(hexStart, hexEndSearch))

			{
				hexEndSearch = hexStart;
			}else {
				for (Hex hex:arrWork) {
					if (moaCheckHex(isMOASpecial, hex, hexMOAEnd,hexEnd, arrReference)){
//						if (((hex == hexI1 || hex == hexI2) && arrReturnBackWards.get(arrReturnBackWards.size() - 1) == hexO1) ||
//								(hex == hexO1 && (arrReturnBackWards.get(arrReturnBackWards.size() - 1) == hexI1 || arrReturnBackWards.get(arrReturnBackWards.size() - 1) == hexI2))) {
						Hex hexPrev = arrReturnBackWards.get(arrReturnBackWards.size() - 1);
//						if (!HexCanNotCross.checkCanCross(hex, hexPrev)){
//							int i=0;
						if (hex.getCalcMoveCost(0) > cost) {// && hex.getCalcMoveCost() <= tempcost) {
							hexEndSearch = hex;
							cost = hex.getCalcMoveCost(0);

						}
					}
				}
				if (arrReturnBackWards.contains(hexEndSearch)){
					Gdx.app.log("UnitMove","Trying to add Duplicate hex="+hexEndSearch);
					for (Hex hex:arrReturnBackWards){
						Gdx.app.log("UnitMove","Hex in Array="+hex);
					}
					ErrorGame errorGame = new ErrorGame("In Loop check Console", this);
				}else {
					arrReturnBackWards.add(hexEndSearch);
				}

			}
		}
		int start =arrReturnBackWards.size() -1;
		for (int i=start; i >= 0; i-- ) {
			arrReturn.add(arrReturnBackWards.get(i));
		}
		Gdx.app.log("UnitMove","getLeastPath ="+arrReturn);
		return arrReturn;
	}

	/**
	 *  brute force find destination
	 * @param unit
	 * @param hexEnd
	 * @return
	 */
	static private boolean notFound = true;
	static private ArrayList arrRailSolution = new ArrayList();
	static private Hex hexEndOfTheLine = null;

	/**
	 *  Check if MOA overstacking can occur
	 *  only can happen if the target hex is next to an stack full and it has not
	 *  been included in the array of possible hexes
	 * @param isMOASpecial
	 * @param hex
	 * @param hexEndSearch
	 * @param hexEnd
	 * @param arrReference
	 * @return
	 */
	private boolean moaCheckHex(boolean isMOASpecial, Hex hex, Hex hexEndSearch, Hex hexEnd, ArrayList<Hex> arrReference){
		if (!isMOASpecial){
			return true;
		}
		if (!hexEnd.getSurround().contains(hex)){
			return true;
		}
		if (hexEndSearch == hexEnd && !arrReference.contains(hex)) {
			return false;
		}
		return true;
	}

	/**
	 * Get the closest hex in solution to passed hexsoes
	 * @param hexSearch
	 * @return hex in solution
	 */
	public Hex getClosest(Hex hexSearch, int thread) {
		if (arrHexSolution[thread].contains(hexSearch)){
			return hexSearch;
		}
		Hex hexReturn = null;
		float maxDist = 9999;
		for (Hex hex:arrHexSolution[thread]){
			float distance = HexHelper.findRange(hex,hexSearch);
			if (distance < maxDist){
				maxDist = distance;
				hexReturn = hex;
			}
		}
		if (hexReturn == null){
			ErrorGame errorGame = new ErrorGame("Closest Hex Not Found)", this);
		}
		return hexReturn;
	}
}
