package com.bruinbeargames.ardenne.Hex;

import java.util.ArrayList;

public class HexHandler {



    static public int xCount = 118; // entire game map x value
    static public int yCount = 32;// entire hgame map y value
    //		static public Hex [,] hexTable; // this is hex table of playable game
    static int[][] oddArray =  { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 }, { -1, -1 } };
    static int[][] evenArray = { { 0, -1 }, { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 } };


    static public int[] displayToTable(int displayOffsett)
    {
        int x, y;
        y = displayOffsett / xCount;
        x = displayOffsett % xCount;
        return limitCheck(x, y);
    }
    /// <summary>
/// Check x and y are within limits of playable board
/// </summary>
/// <param name="x"></param>
/// <param name="y"></param>
/// <returns>null if outside limit or array of x and y </returns>
    private static int[] limitCheck(int x, int y)
    {
        //
        // if real number is within limits
        // subtract 1 from coordinate because playablable game does not use 0,0
        if (x >= Hex.xStart && x <= Hex.xEnd &&
                y >= Hex.yStart && y <= Hex.yEnd)
        {
            x--;
            y--;
            int[] intReturn = new int[2];
            intReturn[0] = x;
            intReturn[1] = y;
            return intReturn;
        }
        else
        {
            return null;
        }
    }
    //static public void occupyHex(Unit occUnit)
//{
//	// find x,y if unit is on the playable map
//	int[] xyUnit = opsToTable(occUnit.mapPlace);
//	if (xyUnit == null)
//	{
//		return;
//	}
//	int x = xyUnit[0];
//	int y = xyUnit[1];
//	Hex.hexTable[x, y].OccupyHex(occUnit);
//}
/// <summary>
/// Get all surrounding hexes on the mapboard
/// </summary>
/// <param name="sourceHex">"starting Hex to get surrounds"</param>
/// <returns>"hex array with direction 0, 1, 2, 3, 4, 5 order </returns>
    public static Hex[] getSurround(Hex sourceHex)
    {
        Hex[] workHex = new Hex[6];
        int[][]calcArray;
        int xWork, yWork;
        if (sourceHex == null)
        {
            return workHex;
        }
        int xHex = sourceHex.xTable;
        int yHex = sourceHex.yTable;
        int xRem = xHex % 2; // odd or even hex
        // set array to add based on odd or even
        if (xRem == 0)
        {
            calcArray = oddArray;
        }
        else
        {
            calcArray = evenArray;
        }
        int hexCount = 0;
        // go through array addin x aand y from hex
        for (int i = 0; i < 6; i++)
        {
            xWork = xHex + calcArray[i][0];
            yWork = yHex + calcArray[i][1];
            // check if on map if it is add to hex array
            if ((xWork > -1) && (xWork < Hex.xEnd) && (yWork > -1) && (yWork < Hex.yEnd))
            {
                workHex[hexCount++] = Hex.hexTable[xWork][yWork];
            }
            else
            {
                workHex[hexCount++] = null;
            }
        }
        return workHex;
    }
    public static ArrayList<Hex> getSurroundArr(Hex hexIn){
        ArrayList<Hex> arrReturn = new ArrayList<Hex>();
        for (Hex hex:getSurround(hexIn)){
            if (hex != null) {
                arrReturn.add(hex);
            }
        }
        return arrReturn;
    }

    /// <summary>
/// Calculates shortest distance in hexes between 2 hex objects
/// </summary>
/// <param name="hexStart"></param>
/// <param name="hexFinish"></param>
/// <returns>int shortest distance</returns>
    static public int shortestLine(Hex hexStart, Hex hexFinish)
    {

        int xS = hexStart.xTable;
        int yS = hexStart.yTable;
        if (hexFinish == null)
        {
            int i=0;
            i=1;

        }

        int xF = hexFinish.xTable;
        int yF = hexFinish.yTable;
        int hexCount = 0;
        int xRem, offTable=0;
        int[][] calcArray;
        // loop through until we have moved to where we want to go
        while ((xS != xF) || (yS != yF))
        {
            xRem = xS % 2; // odd or even hex
            // set array to add based on odd or even
            if (xRem == 0)
            {
                calcArray = evenArray;
            }
            else
            {
                calcArray = oddArray;
            }
            if (xS > xF)
            // must be 4 or 5
            {
                if (yS > yF)
                {
                    offTable = 5;
                }
                else
                {
                    offTable = 4;
                }
            }
            else
            {
                if ( xS < xF) // must be 1 or 2
                {
                    if (yS > yF)
                    {
                        offTable = 1;// used to be 1
                    }
                    else
                    {
                        offTable = 2;//used to be 2
                    }

                }
                else
                {
                    if (xS == xF)
                    {
                        if (yS > yF)
                        {
                            offTable = 0;
                        }
                        else
                        {
                            offTable = 3;
                        }

                    }
                }
            }
            xS += calcArray[offTable][0];
            yS += calcArray[offTable][1];
            hexCount++;
        }
        return hexCount;
    }
    static public void ClearSearchValue()
    {
        for (int x =0; x < Hex.xEnd; x++)
        {
            for (int y = 0; y < Hex.yEnd; y++)
            {
//			Hex.hexTable[x][y].searchValue = 0;
            }
        }
    }
    /// <summary>
/// Get all surrounding hexes on the mapboard
/// </summary>
/// <param name="sourceHex">"starting Hex to get surrounds"</param>
/// <returns>"hex array with direction 0, 1, 2, 3, 4, 5 order </returns>
    public static Hex[] getSurroundSea(Hex sourceHex)
    {
        Hex[] workHex = new Hex[6];
        int[][] calcArray;
        int xWork, yWork;
        if (sourceHex == null)
        {
            return workHex;
        }
        int xHex = sourceHex.xTable;
        int yHex = sourceHex.yTable;
        int xRem = xHex % 2; // odd or even hex
        // set array to add based on odd or even
        if (xRem == 0)
        {
            calcArray = evenArray;
        }
        else
        {
            calcArray = oddArray;
        }
        int hexCount = 0;
        // go through array addin x aand y from hex
        for (int i = 0; i < 6; i++)
        {
            xWork = xHex + calcArray[i][0];
            yWork = yHex + calcArray[i][1];
            // check if on map if it is add to hex array
            if ((xWork > -1) && (xWork < Hex.xEnd) && (yWork > -1) && (yWork < Hex.yEnd))
            {
                workHex[hexCount++] = Hex.hexTable[xWork][yWork];
            }
            else
            {
                workHex[hexCount++] = null;
            }
        }
        return workHex;
    }


}
