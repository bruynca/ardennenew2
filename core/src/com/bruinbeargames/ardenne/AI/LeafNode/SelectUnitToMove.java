package com.bruinbeargames.ardenne.AI.LeafNode;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.bruinbeargames.ardenne.AI.Blackboard.Blackboard;


public class SelectUnitToMove extends LeafTask<Blackboard> {

    @Override
    public Status execute() {

//        UnitStack selectedUnits = getObject().getRussianUnitGroups().get("9,19");
//        selectedUnits.markGroupAsSelected();
//        getObject().setSelectedUnits(selectedUnits);
        int i=0;

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Blackboard> copyTo(Task<Blackboard> task) {
        return task;
    }
}
