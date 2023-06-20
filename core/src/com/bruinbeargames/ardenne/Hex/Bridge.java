package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.BlowBridge;
import com.bruinbeargames.ardenne.NextPhase;

import java.util.ArrayList;

public class Bridge {
    public static  ArrayList<Bridge> arrBridges = new ArrayList();
    Hex hex1;
    Hex hex2;
    boolean isBlown = false;
    int turnBlown = 99;
    int turnFixed = 99;
    int dieNeedAbove = 9;
    private  Bridge(Hex hexa, Hex hexb)
    {
        hex1 = hexa;
        hex2= hexb;
        hexa.updateBridge();
        hexb.updateBridge();
        isBlown = false;
        arrBridges.add(this);
    }
    public static Bridge createBridge(Hex hexa, Hex hexb){
        for (Bridge bridge:arrBridges){
            if (bridge.hex1 == hexa && bridge.hex2 == hexb ){
                return bridge;
            }
            if (bridge.hex2 == hexa && bridge.hex1 == hexb ){
                return bridge;
            }
        }
        Bridge bridge = findBridge(hexa, hexb);
        if (bridge == null) {
            bridge = new Bridge(hexa, hexb);
        }
        return bridge;
    }
    public static Bridge findBridge(Hex hexa, Hex hexb){

        for (Bridge bridge:arrBridges){
            if (bridge.hex1 == hexa && bridge.hex2 == hexb ){
                return bridge;
            }
            if (bridge.hex2 == hexa && bridge.hex1 == hexb ){
                return bridge;
            }
        }
        return null;
    }

    public static Bridge findBridge(Hex hex) {

        for (Bridge bridge:arrBridges){
            if (bridge.hex1 == hex || bridge.hex2 == hex ){
                return bridge;
            }
        }
        return null;

    }

    public int getDieNeedAbove(){
        Gdx.app.log("Bridge","die needed="+dieNeedAbove);

        return dieNeedAbove;
    }
    public  void roleAttempt(){
        Gdx.app.log("Bridge","Roll Attempt before needed="+dieNeedAbove);

        dieNeedAbove -=3;
        if (dieNeedAbove < 2){
            dieNeedAbove = 2;
        }
        Gdx.app.log("Bridge","Roll Attempt after needed="+dieNeedAbove);

    }

    public Hex getHex1() {
        return hex1;
    }

    public Hex getHex2() {
        return hex2;
    }

    static  public int[][] noBridgePossible ={{20,15},{21,14},{16,18},{15,18},{15,19},{16,19},{17,18},{18,19},{8,24},{7,23},
            {07,22},{8,22},{27,23},{27,23},{37,22},{37,21},{36,21},{20,15},{21,14},{28,16},{32,21},{33,21},{34,21}};
    static public   ArrayList<Hex> arrNoBridges = new ArrayList<Hex>();
    static public void createNoBridgeArray(){
        /**
         *  create array of hexes that can not be checked for bridge
         */
        for (int i=0; i <noBridgePossible.length; i++)
        {
            int x = noBridgePossible[i][0];
            int y = noBridgePossible[i][1];
            Hex hex= Hex.hexTable[x][y];
            arrNoBridges.add(hex);
        }

    }
    static public void  checkCanCreateBridges(Hex hex){
        if (hex.isStreamBank){
            if (hex.isRoad || hex.isPath){
                for (Hex hex2:hex.getSurround()){
                    if (hex2.isStreamBank &&(hex2.isRoad || hex2.isPath)){
                        if (arrNoBridges.contains(hex) || arrNoBridges.contains(hex2))
                        {

                        }else {
                            if (River.instance.isStreamBetween(hex, hex2)) {
                                Bridge bridge = createBridge(hex, hex2);
                            }
                        }
                    }
                }
            }
        }
    }

    static public boolean isBridge(Hex hexa, Hex hexb)
    {
        for (Bridge bridge:arrBridges)
        {
            if ((bridge.hex1 == hexa && bridge.hex2 == hexb)|| (bridge.hex1 == hexb && bridge.hex2 == hexa))
            {
                if (!bridge.isBlown) {
                    return true;
                }
            }
        }
        return false;

    }
    static public boolean hasBridge(Hex hexa)
    {
        for (Bridge bridge:arrBridges)
        {
            if ((bridge.hex1 == hexa || bridge.hex2 == hexa)) {
                return true;
            }
        }
        return false;

    }
    public void blowUp(){
        isBlown = true;
        turnBlown = NextPhase.instance.getTurn();
        BlowBridge.instance.displayBlownBridge(this); // display here for saved games
    }
    public void fix(){
        isBlown = false;
        turnFixed = NextPhase.instance.getTurn();
    }
    public boolean getBlown(){
        return isBlown;
    }
    public int getTurnBlown(){
        return turnBlown;
    }
    static public void  clearBridges(){
        arrBridges.clear();
    }

}
