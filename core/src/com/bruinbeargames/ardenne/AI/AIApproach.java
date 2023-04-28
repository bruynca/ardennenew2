package com.bruinbeargames.ardenne.AI;

import com.bruinbeargames.ardenne.Hex.Hex;

import java.util.ArrayList;

/**
 *  approach routes to Bastogne along the 3 routes
 *  the instance is the factorymaker
 */
public class AIApproach {
    static public AIApproach instance;
    ArrayList<Approach> arrAproaches = new ArrayList<>();
    int[][] routeClerVaux ={{29,9},{27,8},{25,8},{21,6},{18,9},{17,9},{14,10},{12,11}};
    int[][] routeWiltz ={{24,15},{19,14},{18,16},{9,18},{11,12},{9,14}};
    int [][] routeEttlebruck = {{33,17},{35,20},{31,21},{28,23},{19,20},{19,24},
            {11,22},{9,23},{6,18}};
    AIApproach() {
        instance  = this;
        Approach approach = new Approach(routeClerVaux);
        arrAproaches.add( approach);
        approach = new Approach(routeWiltz);
        arrAproaches.add( approach);
        approach = new Approach(routeEttlebruck);
        arrAproaches.add( approach);
    }
    public class Approach{
        ArrayList<Hex> arrPoints = new ArrayList<>();
        ArrayList<Hex> arrUnitsInReach = new ArrayList<>();
        Approach(int[][] hexPoints){
            for (int[] in:hexPoints ){
                Hex hex = Hex.hexTable[in[0]][in[1]];
            }
        }
    }
}
