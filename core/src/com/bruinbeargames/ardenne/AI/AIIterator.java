package com.bruinbeargames.ardenne.AI;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class AIIterator {
    ArrayList<Hex>[] arrHexArray;
    ArrayList<Unit> arrUnits;
    ArrayList<Hex> arrHexPosition;
    AIOrders.Type type;
    int[] cntAt;
    int cntArrays;
    public AIIterator(ArrayList<Hex>[] arrHexArray, ArrayList<Unit> arrUnits, ArrayList<Hex> arrUnitsStartHex, AIOrders.Type type)
    {
        Gdx.app.log("AIIterator", "Constructor Size="+arrHexArray.length);

        this.arrHexArray = arrHexArray;
        this.arrUnits = arrUnits;
        this.type = type;
        arrHexPosition=arrUnitsStartHex;
        cntArrays = arrHexArray.length;
        cntAt = new int[cntArrays];
        cntArrays--; // make sure we point to correct
        /** identify
         *  the HQ's
         */

    }
    public AIOrders Iteration()
    {

        ArrayList<Hex> arrHexTemp = new ArrayList<Hex>();
        /**
         * make sure number of hexes units can move to is greater than the number of units
         */
        for (int i = 0; i< arrHexArray.length; i++)
        {
            arrHexTemp.add(arrHexArray[i].get(cntAt[i]));
        }
        AIOrders aiOrders = new AIOrders(type,arrHexTemp, arrUnits,arrHexPosition);
        return aiOrders;

    }
    public AIOrders doNext()
    {
        for (int i = cntArrays; i >= 0; i--)
        {
            cntAt[i]++;
//			if (cntAt[cntArrays] < arrHexArray[cntArrays].size())
            if (cntAt[i] < arrHexArray[i].size())
            {
                break;
            }
            if (i == 0)
            {
                Gdx.app.log("AiOrders", "End ");
                return null;
            }
            cntAt[i] = 0;
        }
//	   	Gdx.app.log("AiOrders", "CNTAT="+cntAt[0]);

//	   	Gdx.app.log("AiOrders", "CNTAT="+cntAt[0]+" "+cntAt[1]+" "+cntAt[2]+" "+cntAt[3]+" "+cntAt[4]+" "+cntAt[5]+" "+cntAt[6]+" "+cntAt[7]+" "+cntAt[8]);
        return Iteration();
    }
 }
