package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.Combat;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AICombatScenario1 {
    static public AICombatScenario1 instance;
    private I18NBundle i18NBundle;
    boolean isAllies;
    ArrayList<AIOrdersCombat> arrToBeScored = new ArrayList<>();

    int greaterThanAttackFactor = 1;

    AICombatScenario1() {
        instance = this;
    }

    /**
     * AIMover will have done most of the work
     * we now have to score which attacks are worth while
     * assume doCombatPhase hase been initieted
     *
     * @param isAllies
     */
    public void doInitialCombatTurn1to3(boolean isAllies) {
        Gdx.app.log("AICombatScenario1", "doInitialCombatTurn1to3");
        this.isAllies = isAllies;
        arrToBeScored.clear();
        for (Hex hex : Combat.instance.getAttackedHexesForAI(isAllies)) {
            ArrayList<Unit> arrUnits = Combat.instance.getUnitsCanAttackForAI(hex,isAllies);
            AIOrdersCombat aiOrdersCombat = new AIOrdersCombat(arrUnits, hex);
            arrToBeScored.add(aiOrdersCombat);
        }
        /**
         *  remove any unit attacking from a major city
         */
        ArrayList<AIOrdersCombat> arrREmove = new ArrayList<>();
        for (AIOrdersCombat aiC:arrToBeScored){
            for (Unit unit: aiC.getUnits()){
                if (Hex.arrMajorCities.contains(unit.getHexOccupy())){
                    arrREmove.add(aiC);
                    break;
                }
            }
        }
        arrToBeScored.removeAll(arrREmove);

            /**
             * sort score into highest
             */
            ArrayList<AIOrdersCombat> arrSorted = scoreAndSort(arrToBeScored);
            /**
             *  remove units from the high to low
             *  if attack doesnt have units then take out of orders
             *  if attack less than 1 to 1 take out
             */
            ArrayList<Unit> arrUnitsToRemove = new ArrayList<>();
            ArrayList<AIOrdersCombat> arrViable = new ArrayList<>();
            for (AIOrdersCombat aic : arrSorted) {
                for (Unit unit : arrUnitsToRemove) {
                    aic.removeAttackingUnit(unit);
                }
                for (Unit unit : aic.hexAttackedByUnits.getArrUnits()) {
                    if (!arrUnitsToRemove.contains(unit)) {
                        arrUnitsToRemove.add(unit);
                    }
                }
                if (aic.hexAttackedByUnits.getArrUnits().size() > 0 &&  aic.getScore() > 1.0f) {
                    arrViable.add(aic);
                }

            }
            ArrayList<AIOrdersCombat> arrFinal = scoreAndSort(arrViable);
            int bk = 0;

            AICombat.instance.setArrToBeScored(arrFinal);

    }

    private ArrayList<AIOrdersCombat> scoreAndSort(ArrayList<AIOrdersCombat> arrIn) {
        ArrayList<AIOrdersCombat> arrReturn = new ArrayList<>();
        /**
         * Do the intial score
         */
        for (AIOrdersCombat aic : arrIn) {
            Attack attack = new Attack(aic.hexAttackedByUnits.getHex(), isAllies, false, true, null);
            for (Unit unit : aic.hexAttackedByUnits.getArrUnits()) {
                attack.addAttacker(unit, true);
            }
            float score = attack.getActualOdds();
            aic.setScore(score);
//           attack.cancel();
        }
        /**
         * sort score into highest
         */
        int i = 0;
        for (AIOrdersCombat aic : arrIn) {
            for (i = 0; i < arrReturn.size(); i++) {
                if (arrReturn.get(i).getScore() < aic.getScore()) {
                    break;
                }
            }
            arrReturn.add(i, aic);
        }
        return arrReturn;

    }
}
