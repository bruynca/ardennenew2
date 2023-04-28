package com.bruinbeargames.ardenne.Hex;

import java.util.ArrayList;

public class HexSurround {

    public static Hex[][] GetSurroundMap(Hex startHex, int iRange)
    {
        Hex[][] retTable; // jagged array to contain hexes for surround
        // if within 7 of henderson copy the rolled version
        //		if (startHex == Hex.hendersonField && iRange == 7 && doneHenderson)
        //		{
        //			retTable = Hex.sevenHendersonHex;
        //			return retTable;
        //		}
        //		else
        //		{
        //			doneHenderson = true;
        //		}
        retTable = new Hex[iRange][]; // set up internal jagged array
        //  1 = first surround, 2 = next etc.
        retTable[0] = HexHandler.getSurround(startHex); // get initial surrounding hexes
        // go through the entire range creating surrond hexTable
        ArrayList<Hex> workList = new ArrayList<Hex>();
        ArrayList<Hex> totList = new ArrayList<Hex>();
        int prevTableLevel, prevTableSize;
        Hex[] workHexTab;
        for (int i = 1; i < iRange; i++)
        {
            prevTableLevel = i - 1; // get preceeding level
            prevTableSize = retTable[prevTableLevel].length; // get preceeding level size
            workList.clear(); // clear the array list
            for (int  j = 0; j < prevTableSize; j++)
            {
                workHexTab = HexHandler.getSurround(retTable[prevTableLevel][j]);
                for (int k = 0; k < 6; k++)
                {
                    if (workHexTab[k] == null)
                    {
                        //                           workList.Add(null); //keep track of nulls
                        //                           totList.Add(null);
                    }
                    else
                    {
                        if (!totList.contains(workHexTab[k]))
                        // if not in list
                        {
                            workList.add(workHexTab[k]); // add it to it
                            totList.add(workHexTab[k]); // add to our dupe checker
                            //                              MapDisplay.Character("p", Color.Coral, workHexTab[k].xTable, workHexTab[k].yTable);
                        }
                    }
                }
            }
            // everyting in array list lets move it to hexTable
            retTable[i] = new Hex[workList.size()];
            for (int l = 0; l < workList.size(); l++)
            {
                retTable[i][l] =  workList.get(l);
            }
        } // next table level

        return retTable;
    }


    public static ArrayList<Hex> GetSurroundMapArr(Hex startHex, int iRange)
    {
        ArrayList<Hex> arrReturn = new ArrayList<Hex>();
        Hex[][] hexTemp = GetSurroundMap(startHex, iRange);
        for (int i = 0; i < hexTemp.length; i++)
        {
            for (int j = 0; j < hexTemp[i].length; j++)
            {
                if (!arrReturn.contains(hexTemp[i][j])&& hexTemp[i][j] != null  )
                    arrReturn.add(hexTemp[i][j]);
            }
        }
        return arrReturn;
    }

    public static ArrayList<Hex> getSurrounMapArrNoNullArrayList(Hex startHex, int iRange)
    {
        ArrayList<Hex> arrReturn = new ArrayList<Hex>();
        ArrayList<Hex> arrTemp =  GetSurroundMapArr(startHex, iRange);
        for (int i = 0; i < arrTemp.size(); i++ )
        {
            if (arrTemp.get(i) != null)
            {
                Hex hex =  arrTemp.get(i);
                arrReturn.add(hex);
            }
        }
        return arrReturn;
    }

}
