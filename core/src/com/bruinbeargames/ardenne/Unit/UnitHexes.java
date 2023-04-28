package com.bruinbeargames.ardenne.Unit;

import com.bruinbeargames.ardenne.Hex.Hex;

import java.util.ArrayList;

public class UnitHexes {
    public Unit unit;
    public ArrayList<Hex> arrHexes = new ArrayList<>();
    public UnitHexes(Unit unitIn, ArrayList<Hex> arrHexIn){
        unit = unitIn;
        arrHexes.addAll(arrHexIn);
    }

}
