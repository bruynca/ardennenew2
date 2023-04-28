package com.bruinbeargames.ardenne.Hex;

import java.util.ArrayList;

public class River {

    static public River instance;
    ArrayList<ArrayList> arrArrABank = new ArrayList<ArrayList>();
    ArrayList<ArrayList> arrArrBBank = new ArrayList<ArrayList>();
    ArrayList<Hex> arrLeftOurRiver  = new ArrayList<>();
    boolean isFound = false;
    public River(ArrayList<ArrayList> arrArrA, ArrayList<ArrayList> arrArrB)
    {
        instance = this;
        arrArrABank.addAll(arrArrA);
        arrArrBBank.addAll(arrArrB);
        if (!isFound) {
            for (ArrayList arr : arrArrABank) {
                Hex hex = (Hex) arr.get(0);
                if (hex.riverStr.contains("1A")) {
                    arrLeftOurRiver.addAll(arr);
                    isFound = true;
                }
            }
        }
    }
    public boolean isStreamBetween(Hex hex1, Hex hex2)
    {
        if (!hex1.getSurround().contains(hex2)){
            return false;
        }
        for (ArrayList<Hex> arr:arrArrABank) {
            if (arr.contains(hex1)) {
                int ix = arrArrABank.indexOf(arr);
                if (arrArrBBank.get(ix).contains(hex2)) {
                    return true;
                }
            }
        }
        for (ArrayList<Hex> arr:arrArrBBank) {
            if (arr.contains(hex1)) {
                int ix = arrArrBBank.indexOf(arr);
                if (arrArrABank.get(ix).contains(hex2)) {
                    return true;
                }
            }
        }

        return false;
    }
    public boolean isEastOfOur(Hex hexLookup){
        for (Hex hex:arrLeftOurRiver){
            if (hexLookup.yTable == hex.yTable){
                if (hexLookup.xTable >= hex.xTable){
                    return true;
                }
            }
        }
        return false;
    }

}
