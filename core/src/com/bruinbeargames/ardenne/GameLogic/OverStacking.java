package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.AI.AIUtil;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.NextPhase;

import java.util.ArrayList;

/**
 * This class is not used  ovestacking not allowed
 */
public class OverStacking {
    ArrayList<Hex> arrOverstacked = new ArrayList<>();
    public static OverStacking instance;
    OverStacking(){
        instance = this;
    }

    /**
     *  check overstacked hex to fix
     */
    public void check(){
        arrOverstacked.clear();
        arrOverstacked.addAll(Hex.checkStacking());
        AIUtil.RemoveDuplicateHex(arrOverstacked);
        displayWindow(arrOverstacked);
    }

    private void displayWindow(ArrayList<Hex> arrOverstacked) {
        if (arrOverstacked.size() == 0){
            NextPhase.instance.nextPhase();
            return;
        }else{
            Hex hexOverStack = arrOverstacked.get(0);
            arrOverstacked.remove(hexOverStack);
 //           WinOverstack winOverstack = new WinOverstack(hexOverStack);
        }

    }
}
