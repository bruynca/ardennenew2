package com.bruinbeargames.ardenne.Hex;

import java.util.ArrayList;

public class HexCount {
    static ArrayList<Hex> arrHex = new ArrayList<>();
    static ArrayList<Integer> arrInt = new ArrayList<>();
    static public void init(){
        arrHex.clear();
        arrInt.clear();
    }
    public HexCount(Hex hex){
        int ix = arrHex.indexOf(hex);
        if (ix == -1){
            arrHex.add(hex);
            arrInt.add(1);
        }else{
            Integer iN = arrInt.get(ix);
            iN++;
            arrInt.set(ix,iN);
        }
    }

    public static ArrayList<Integer> getArrInt() {
        return arrInt;
    }

    public static ArrayList<Hex> getArrHex() {
        return arrHex;
    }
}
