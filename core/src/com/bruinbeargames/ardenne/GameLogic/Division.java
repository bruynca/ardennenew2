package com.bruinbeargames.ardenne.GameLogic;

public class Division {
    static public Division instance;
    String[][] strDivisions = {
            {"FJ 5","5th Parachute Division"},
            {"VG 276","276th Volksgrenadier Division"},
            {"VG 352","352nd Volksgrenadier Division"},
            {"VG 26","26th Volksgrenadier Division"},
            {"Pz Lehr","Panzer Lehr Division"},
            {"2nd Pz","2nd Panzer Division"},
            {"Inf 26","26th Infantry Division -Yankee"},
            {"Inf 28","28th Infantry Division Keystone"},
            {"Arm 9","9th Armored Division -Phantom"},
            {"Arm 10","10th Armored Division -Tiger"},
            {"101 Abn","101st Airborne Division -Screaming Eagles"},
            {"Arm 4","4th Armored Division"},
            {"Inf 80","80th Infantry Division -Blue Ridge"},
            {"Inf 5","5th Infantry Division -Red Diamond"},
    };
    public Division(){
        instance = this;
    }
    public String getName(String lookUp){
        for (int i= 0; i< strDivisions.length; i++){
            if (strDivisions[i][0].contentEquals(lookUp)){
                return strDivisions[i][1];
            }
        }
        return "";
    }
}
