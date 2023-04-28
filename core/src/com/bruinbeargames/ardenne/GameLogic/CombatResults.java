package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class CombatResults {
    static public ArrayList<CombatResults> arrCombatAllied = new ArrayList<>();
    static public ArrayList<CombatResults> arrCombatAxis = new ArrayList<>();
    private Unit unit;
    private boolean stepLosses;
    private boolean destroyed;
    private boolean isAllies;
    private boolean retreated;
    private int hexesRetreated;
    private boolean canAdvance;
    private boolean canContinueMovement;

    CombatResults(Unit unit){
        this.unit = unit;
        if (unit.isAllies){
            arrCombatAllied.add(this);
        }else {
            arrCombatAxis.add(this);
        }
    }
    public Unit getUnit() {
        return unit;
    }
    public boolean isStepLosses() {
        return stepLosses;
    }
    public void setStepLosses(boolean stepLosses) {
        this.stepLosses = stepLosses;
    }
    public boolean isDestroyed() {
        return destroyed;
    }
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        setStepLosses(false);
        setHexesRetreated(0);

    }
    public boolean isRetreated() {
        return retreated;
    }
    public void setRetreated(boolean retreated) {
        this.retreated = retreated;
    }
    public int getHexesRetreated() {
        return hexesRetreated;
    }
    public void setHexesRetreated(int hexesRetreated) {
        this.hexesRetreated = hexesRetreated;
    }
    public boolean isCanAdvance() {
        return canAdvance;
    }
    public void setCanAdvance(boolean canAdvance) {
        this.canAdvance = canAdvance;
    }
    public boolean isCanContinueMovement() {
        return canContinueMovement;
    }
    public void setCanContinueMovement(boolean canContinueMovement) {
        this.canContinueMovement = canContinueMovement;
    }
    static public CombatResults find(Unit unit){
        for (CombatResults cr:arrCombatAllied){
            if (cr.unit == unit){
                return cr;
            }
        }
        for (CombatResults cr:arrCombatAxis){
            if (cr.unit == unit){
                return cr;
            }
        }
        CombatResults cr = new CombatResults(unit);
        return cr;
    }
    static public void init(){
        arrCombatAxis.clear();
        arrCombatAllied.clear();
    }
  }
