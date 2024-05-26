package com.bruinbeargames.ardenne;

import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AIReinforcementNew {
    static public AIReinforcementNew instance;
    ArrayList<Hex> arrReinforceAreas = new ArrayList<>();
    Hex hexBastogneReinforceEntry = Hex.hexTable[0][19];
    Hex hexMartelangeReinforceEntry = Hex.hexTable[9][24];
    Hex hexEttlebruckReinforceEntry = Hex.hexTable[28][24];


    public AIReinforcementNew(){
        instance = this;
        arrReinforceAreas.add(hexBastogneReinforceEntry);
        arrReinforceAreas.add(hexMartelangeReinforceEntry);
        arrReinforceAreas.add(hexEttlebruckReinforceEntry);
    }
    public int doReinforcementAllies(){
        ArrayList<Unit> arrUnits = new ArrayList<>();
        for (Hex hex : arrReinforceAreas) {
            arrUnits.addAll(Reinforcement.instance.getReinforcementsAvailable(hex, true));
            }
        if (arrUnits.size() == 0){
            NextPhase.instance.nextPhase();
        }

        return 1;
    }
}
