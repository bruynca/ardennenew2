package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AttackOdds {

        static public String[] result ={"A4","A3","A2D1","NE","A1D1","r1","D1","D1r1","D2r1","D2r2","D3r2"};
        static  public int[][][]combatResultTableAttacker = {
                {{11,16},{11,13},{11,12},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
                {{21,24},{14,21},{13,15},{11,12},{11,12},{11,11},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0},{0,0}},
                {{25,36},{22,35},{16,34},{13,26},{13,24},{12,22},{11,21},{11,16},{11,14},{11,13},{11,12},{11,12},{11,12}},
                {{41,53},{36,52},{35,52},{31,44},{25,34},{23,26},{22,24},{21,21}, {0,0},{0,0},{0,0},{0,0},{0,0}},
                {{54,61},{53,56},{53,54},{45,46},{35,43},{31,41},{25,35},{22,32},{15,25},{14,22},{13,16},{13,16},{13,14}},
                {{62,64},{61,62},{55,56},{47,55},{44,53},{42,51},{36,45},{33,42},{26,35},{23,32},{17,24},{17,24},{15,22}},
                {{65,66},{63,64},{61,63},{56,63},{54,62},{52,61},{46,56},{43,53},{36,51},{33,44},{25,36},{25,36},{23,34}},
                {{0,0}  ,{65,65},{64,64},{64,64},{63,64},{62,64},{61,63},{54,61},{52,61},{45,54},{37,52},{37,52},{35,46}},
                {{0,0}  ,{66,66},{65,65},{65,65},{65,65},{65,65},{64,65},{62,63},{62,63},{55,61},{53,56},{53,56},{47,55}},
                {{0,0}  ,{0,0},  {66,66},{66,66},{66,66},{66,66},{66,66},{64,65},{64,65},{62,64},{57,64},{57,65},{56,63}},
                {{0,0}  ,{0,0},  {0,0},  {0,0},  {0,0},  {0,0},  {0,0},  {66,66},{66,66},{65,66},{65,66},{65,66},{64,66}}};


        float[] odds = {.25f,.33f,.50f,1f,2f,3f,4f,5f,6f,7f,8f,9f};
        public static String[] oddsString = {"1:5","1:4","1:3","1:2","1:1","2:1","3:1","4:1","5:1","6:1","7:1","8:1","9:1"};
        public String oddactualString;
        public String oddUnConverted;
        static public int ixTableView;
        int ixTable; // index into table
        Attack attack;
        public float oddsCheck =0;
        AttackOdds(Attack attack){
            if (!attack.isAI) {
                Gdx.app.log("AttackOdds", "Constructor");
            }
            this.attack =attack;
            oddactualString = "0:0";
        }
        static public int[][] getDiceRow(int col){
            int[][] tabReturn = new int[result.length][2];
            for (int  i=0; i< result.length; i++){
                tabReturn[i][0] = combatResultTableAttacker[i][col][0];
                tabReturn[i][1] = combatResultTableAttacker[i][col][1];
            }
            return tabReturn;
        }
        public void update() {
            int shiftCRT = 0;

            if (isArmorEffect(attack.arrAttackers, attack.hexTarget)){
                attack.isMechAttack = true;
                shiftCRT--;
            }
            if (attack.hexTarget.isCity()){
                attack.isTown = true;
                shiftCRT -= 3;
            }else if(attack.hexTarget.isForest() && attack.hexTarget.isTown()){
                attack.isVillage = true;
                attack.isTrees = true;
                shiftCRT -=2;
            }else if (attack.hexTarget.isForest()){
                attack.isTrees = true;
                shiftCRT--;
            }else if (attack.hexTarget.isTown()){
                attack.isVillage = true;
                shiftCRT--;
            }
            oddsCheck = attack.attackStrength/attack.defenseStrength;
            boolean isFound =false;
            for (int i=0; i < odds.length; i++){
                if (oddsCheck < odds[i]){
                    isFound = true;
                    ixTable = i;
                    break;
                }
            }
            if (!isFound){
                ixTable = odds.length;
            }
            int ixUnconverted = ixTable;
            ixTable += shiftCRT;
            if (ixTable < 0){
                ixTable =0;
                ixUnconverted =0;
            }
            oddactualString = oddsString[ixTable];
            oddUnConverted = oddsString[ixUnconverted];
            ixTableView = ixTable;

        }
        String getResult(int dice1, int dice2){
            int check = dice1 * 10 + dice2;
            for (int i=0; i< combatResultTableAttacker.length; i++){
                int  low= combatResultTableAttacker[i][ixTable][0];
                int  hi= combatResultTableAttacker[i][ixTable][1];
                if (check >= low && check <=hi){
                    return result[i];
                }
            }
            return "NR";
        }

        private boolean isArmorEffect(ArrayList<Unit> arrAttackers, Hex hexAttack) {
            if (hexAttack.isForest() || hexAttack.isTown()|| hexAttack.isCity()){
                // ok
            }else{
                return false;
            }
            float  cnt = 0;
            for (Unit unit:arrAttackers){
                if (unit.isMechanized){
                    cnt++;
                }
            }
            float  half = (float) arrAttackers.size()/2;
            if (cnt >= half){
                return true;
            }
            return false;
        }
  }


