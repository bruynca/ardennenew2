package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexUnits;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AIOrdersCombat {
    HexUnits hexAttackedByUnits; // unit
    ArrayList<String> arrScoreMessage = new ArrayList<>();
    private float scoreMain = 0;
    AIOrdersCombat(Unit unit,Hex hexAttack){
        hexAttackedByUnits = new HexUnits(hexAttack, unit);
    }
    AIOrdersCombat(ArrayList<Unit> arrIn, Hex hexIn){
        hexAttackedByUnits = new HexUnits(hexIn,arrIn);
    }
    public void addAttackingUnit(Unit unit){
        hexAttackedByUnits.addUnit(unit);
    }
    public void removeAttackingUnit(Unit unit){
        hexAttackedByUnits.remove(unit);
    }
    public void addMessage(String message){
        arrScoreMessage.add(message);
    }
    public void setScore(float score){
        scoreMain = score;
    }
    public float getScore(){
        return scoreMain;
    }
    public ArrayList<Unit> getUnits(){
        return hexAttackedByUnits.getArrUnits();
    }


}
