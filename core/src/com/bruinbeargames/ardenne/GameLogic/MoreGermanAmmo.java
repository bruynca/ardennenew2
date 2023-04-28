package com.bruinbeargames.ardenne.GameLogic;

public class MoreGermanAmmo {
    public static MoreGermanAmmo instance;
    boolean isMoreGermanAmmo = false ;
    public MoreGermanAmmo(){
        instance = this;
    }
    public void setON(){
        isMoreGermanAmmo = true;
    }
    public boolean isMoreGermanAmmo(){
        return isMoreGermanAmmo;
    }
}
