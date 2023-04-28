package com.bruinbeargames.ardenne.Hex;

import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class HexUnits {
    Hex hex;
    ArrayList<Unit> arrUnits = new ArrayList<>();
    int count;
    static public ArrayList<HexUnits> arrHexUnits = new ArrayList<>();

    public HexUnits(Hex hex, Unit unit) {
        this.hex = hex;
        arrUnits.add(unit);
        count = 1;
        arrHexUnits.add(this);
    }
    public HexUnits(Hex hex, ArrayList<Unit> arrIn) {
        this.hex = hex;
        arrUnits.addAll(arrIn);
    }
    public void addUnit(Unit unit){
        arrUnits.add(unit);
    }

    static public void add(Hex hex, Unit unit) {
        boolean isfound = false;
        for (HexUnits hu : arrHexUnits) {
            isfound = false;
            if (hu.hex == hex) {
                if (!hu.arrUnits.contains(unit)) {
                    hu.arrUnits.add(unit);
                    isfound = true;
                    break;
                }
            }
        }
        if (!isfound) {
            HexUnits hexUnits = new HexUnits(hex, unit);
        }
    }
    static public void init(){
        arrHexUnits.clear();
    }
    static public void sortbyNumberOfUnits(ArrayList<HexUnits> arrIn){
        ArrayList<HexUnits> arrWork =  new ArrayList<>();
        arrWork.addAll(arrIn);
        arrIn.clear();
        for (HexUnits hu:arrWork){
            int i=0;
            for (i=0; i<arrHexUnits.size();i++){
                if (hu.arrUnits.size() > arrHexUnits.get(i).arrUnits.size()){
                    break;
                }
            }
            arrIn.add(i,hu);
        }
        int i=0;
    }

    public ArrayList<Unit> getArrUnits() {
        return arrUnits;
    }

    public Hex getHex() {
        return hex;
    }

    public void remove(Unit unit) {
        arrUnits.remove(unit);
    }
}
