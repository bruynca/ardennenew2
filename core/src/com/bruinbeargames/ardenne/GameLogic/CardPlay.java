package com.bruinbeargames.ardenne.GameLogic;

public class CardPlay {
    static public CardPlay instance;
    CardPlay(){
        instance = this;
    }
    public enum AmericanCards {FritzNurse, PattonPrayer,TwoPanzerDisappear,LehrDisappear,BlownBridge};
    public enum GermanCards {HoufGas, MoreAmmo, FixBridge,GriefBridge, GriefSignPost }
}

