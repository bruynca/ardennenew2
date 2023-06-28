package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.Move;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Unit;

public class AIAdvance {
    public AIAdvance(Attack attack){
        Gdx.app.log("AIAdvance", "advance on  :"+ attack.getHexTarget());

        if (attack.getAttackers().size() == 0){
            return;
        }
        Unit unitAdvance = attack.getAttackers().get(0);
        int cntAttack = 0;
        for (Unit unit:attack.getAttackers()){
            if (unit.getCurrenAttackFactor() > cntAttack){
                unitAdvance = unit;
                cntAttack = unit.getCurrenAttackFactor();
            }
        }
        Move.instance.moveUnitAfterAdvance(unitAdvance, attack.getHexTarget());
    }
}
