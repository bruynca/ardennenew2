package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Hex.Hex;

public class HooufGas {
    static public HooufGas instance;
    boolean isHooufGas = false;
    boolean isInvalidated = false;
    boolean isPlayed =  false;
    public HooufGas(){
        instance = this;
    }

    public void setHooufGas() {
        isHooufGas = true;
        isPlayed = true;
        Supply.instance.addHooufGas();
    }

    public boolean isHooufGas() {
        return isHooufGas;
    }
    public boolean checkHooufgas(){
        if (isInvalidated){
            return false;
        }
        Hex hexHouf = Hex.hexTable[12][3];
        if (hexHouf.isAxisEntered()){
            return true;
        }else{
            if (isPlayed){
                isInvalidated = true;
                Supply.instance.removeHooufgas();
            }
            return false;
        }
    }
    public boolean getIsInvalidated(){
        return isInvalidated;
    }

    public void setInvalidated(boolean invalidated) {
        isInvalidated = invalidated;
    }
}
