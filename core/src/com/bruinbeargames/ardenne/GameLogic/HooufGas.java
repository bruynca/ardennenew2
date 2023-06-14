package com.bruinbeargames.ardenne.GameLogic;

import com.bruinbeargames.ardenne.Hex.Hex;

public class HooufGas {
    static public HooufGas instance;
    boolean isHooufGas = false;
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
    public void checkHooufgas(){
        Hex hexHouf = Hex.hexTable[12][3];
        if (hexHouf.isAxisEntered()){
                isHooufGas = true;
                Supply.instance.addHooufGas();
        }
    }

    /**
     *  weird logic because of history
     * @param inHouf
     */
    public void setBroken(boolean inHouf) {
        isHooufGas = inHouf;
        if (!isHooufGas){
            Supply.instance.addHooufGas();
        }else {
            Supply.instance.removeHooufgas();
        }
    }
}
