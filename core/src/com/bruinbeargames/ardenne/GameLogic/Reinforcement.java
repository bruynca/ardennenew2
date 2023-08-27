package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.GameSetup;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HiliteHex;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.UI.WinReinDisplay;
import com.bruinbeargames.ardenne.UI.WinReinforcements;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.Unit.UnitMove;

import java.util.ArrayList;

public class Reinforcement {
    public static Reinforcement instance;
    ArrayList<Unit> arrReinforcements = new ArrayList<>(); // allies only
    ArrayList<Unit> arrRemoves = new ArrayList<>(); // allies only
    final String strID = "<ID value=\"";
    HiliteHex hiliteHex;
    int[][] hexDesc = {{33,24},{28,24},{9,24},{0,19}};
    String[] strHex = {"east","southeast","south","southwest"};
    ArrayList<Hex> arrHexDesc = new ArrayList<>();
    public Hex hexBastogneReinforce;
    int cnteastThisTurn = 0;
    int cntSouthEastThisTurn =0;
    int cntSouthThisTurn =0;
    int cntSouthWestThisTurn = 0;



    public Reinforcement(){
        instance = this;
        arrReinforcements.addAll(Unit.getReinforcements());
        int i=0;
        for (String str:strHex){
            int x = hexDesc[i][0];
            int y= hexDesc[i][1];
            Hex hex = Hex.hexTable[x][y];
            arrHexDesc.add(hex);
            i++;
        }
        hexBastogneReinforce = arrHexDesc.get(3);
    }
    public ArrayList<Unit> getReinforcementsAvailable(int turn){
        ArrayList<Unit> arrWork = new ArrayList<>();
        for (Unit unit:arrReinforcements)
        {
            if (unit.entryNum <= turn)
            {
                arrWork.add(unit);
            }
        }
        return arrWork;
    }
    public ArrayList<Unit> getReinforcementsAvailable(){
        ArrayList<Unit> arrWork = new ArrayList<>();
        for (Unit unit:arrReinforcements)
        {
            if (unit.entryNum >= NextPhase.instance.getTurn() &&
               unit.entryNum <= GameSetup.instance.getScenario().getLength())
            {
                arrWork.add(unit);
            }
        }
        return arrWork;
    }
    public ArrayList<Hex> getReinforcementHexAvailable(){
        ArrayList<Unit> arrUnits = getReinforcementsAvailable();
        ArrayList<Hex> arrHex = new ArrayList<>();
        for (Unit unit:arrUnits) {
                Hex hexCheck = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
                if (!arrHex.contains(hexCheck)) {
                    arrHex.add(hexCheck);
                }
        }
        return arrHex;

    }
    public void removeReinforcement(Unit unit){
        arrReinforcements.remove(unit);
        arrRemoves.add(unit);
        if (unit.getEntryX() == 33 && unit.getEntryY() == 24) {
            cnteastThisTurn++;
        } else if (unit.getEntryX() == 28 && unit.getEntryY() == 24) {
            cntSouthEastThisTurn++;
        } else if (unit.getEntryX() == 9 && unit.getEntryY() == 24) {
            cntSouthThisTurn++;
        } else if (unit.getEntryX() == 0 && unit.getEntryY() == 19) {
            cntSouthWestThisTurn++;
        }
    }
    public String saveRemoved(){
        StringBuffer str = new StringBuffer();
        str.append("<reinforcements>");
        for (Unit unit:arrRemoves){
            str.append("<unit>");
            str.append("<ID value=\"");
            str.append(String.format("%02d", unit.ID));
            str.append( "\"/>");
            str.append("</unit>");
        }
        str.append("</reinforcements>");
        return str.toString();
    }

    WinReinforcements winReinforcements;
    public WinReinforcements getScreen(){
        return winReinforcements;
    }
    public void showWindow() {
        winReinforcements = new WinReinforcements();

    }

    /**
     *  show reinforcements hex user can click on
     */
    public void showDisplay() {
        hiliteHex = new HiliteHex(getReinforcementHexAvailable(), HiliteHex.TypeHilite.ReinforceDisplay,null);
        return;
    }
    public void endDisplay(){
        hiliteHex.remove();
    }
    WinReinDisplay winReinDisplay;

    public void showReinDisplay(Hex hex) {
        if (winReinDisplay != null){
            winReinDisplay.end();
        }
        winReinDisplay = new WinReinDisplay(hex);
    }

    public boolean isDisplay() {
        if (winReinDisplay !=null){
            if (winReinDisplay.isVisble()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Unit> getReinforcementsAvailable(Hex hex,boolean byTurn) {
        ArrayList<Unit> arrUnit =  new ArrayList<>();
        int x = hex.xTable;
        int y= hex.yTable;
        int turn = NextPhase.instance.getTurn();
        for (Unit unit:getReinforcementsAvailable()){
            if (byTurn && unit.entryNum > turn){
                break;
            }
            if (unit.getEntryY() == y && unit.getEntryX() == x){
                arrUnit.add(unit);
            }
        }
        return arrUnit;
    }
    public String getName(Hex hex){
        int ix =arrHexDesc.indexOf(hex);
        if (ix <0){
            return "";
        }else{
            return strHex[ix];
        }
    }

    /**
     *  Used by AI
     *  keep in sync with WinReinforcements
     * @param unit
     * @return
     */
    public  ArrayList<Hex> findHexesToPlaceThisUnit(Unit unit) {
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        Hex hex = Hex.hexTable[unit.getEntryX()][unit.getEntryY()];
        if (hex.canOccupy(unit)) {
            UnitMove unitMove = new UnitMove(unit, unit.getCurrentMovement() -1,false,true,hex,0);
            arrHexReturn.addAll(unitMove.getMovePossible());
            arrHexReturn.add(hex);
            return arrHexReturn;
        }
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

    public void resetUnitsLoaded() {
        cntSouthWestThisTurn = 0;
        cntSouthThisTurn = 0;
        cnteastThisTurn = 0;
        cntSouthEastThisTurn = 0;

    }

    public int getCost(Unit unit) {
        if (unit.getEntryX() == 33 && unit.getEntryY() == 24) {
            return cnteastThisTurn;
        } else if (unit.getEntryX() == 28 && unit.getEntryY() == 24) {
            return cntSouthEastThisTurn;
        } else if (unit.getEntryX() == 9 && unit.getEntryY() == 24) {
            return cntSouthThisTurn;
        } else if (unit.getEntryX() == 0 && unit.getEntryY() == 19) {
            return cntSouthWestThisTurn;
        }
        return 0;
    }
}
