package com.bruinbeargames.ardenne.Hex;

import java.util.ArrayList;

public class HexInt {
    public int count;
    public Hex hex;
    public HexInt(Hex hex, int inIn){
        count = inIn;
        this.hex  = hex;
    }

    /**
     * make sure hexes are unique
     * @param arrAirAllocate
     */

    public static void rationlize(ArrayList<HexInt> arrAirAllocate) {
        ArrayList<HexInt> arrRemove = new ArrayList<>();
        for (int i=0; i< arrAirAllocate.size(); i++){
            HexInt hiCheck = arrAirAllocate.get(i);
            for (int j=i+1; j< arrAirAllocate.size(); j++){
                HexInt hiCheck2 = arrAirAllocate.get(j);
                if (hiCheck.hex == hiCheck2.hex){
                    arrRemove.add(hiCheck2);
                    hiCheck.count += hiCheck2.count;
                }
            }
        }
        arrAirAllocate.removeAll(arrRemove);
    }
}
