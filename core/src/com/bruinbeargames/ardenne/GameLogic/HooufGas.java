package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Hex.Hex;

public class HooufGas {
    static public HooufGas instance;
    boolean isHooufGas = false;
    boolean isPlayed =  false;
    boolean isAxisEntered = false; // spoils it for Americans
    Hex hexHouf = Hex.hexTable[12][3];

    public HooufGas(){
        instance = this;
    }

    public void setHooufGas() {
        isHooufGas = true;
        isPlayed = true;
        isAxisEntered = true;
        Supply.instance.addHooufGas();
    }

    public boolean isHooufGas() {
        return isHooufGas;
    }
    public void checkHooufgas(){
        if (hexHouf.isAxisEntered()){
                isHooufGas = true;
                isAxisEntered = true;
                Supply.instance.addHooufGas();
        }
    }

    /**
     *  weird logic because of history
     * @param inHouf
     */
    public void setBroken(boolean inHouf) {
        isHooufGas = inHouf;
        if (isHooufGas){
            checkHooufgas();
        }else {
            Supply.instance.removeHooufgas();
        }
    }
}
