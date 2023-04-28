package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HexHelper {

    private HexHelper() {

    }





    public static ArrayList<Vector2> getPointsOnALine(Vector2 startPoint, Vector2 endPoint) {

        ArrayList<Vector2> points = new ArrayList<>();

        int x1 = Math.round(startPoint.x);
        int y1 = Math.round(startPoint.y);
        int x2 = Math.round(endPoint.x);
        int y2 = Math.round(endPoint.y);

        int d = 0;

        int dy = Math.abs(y2 - y1);
        int dx = Math.abs(x2 - x1);

        int dy2 = (dy << 1); // slope scaling factors to avoid floating
        int dx2 = (dx << 1); // point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        if (dy <= dx) {
            for (; ; ) {
                //plot(g, x1, y1);
                points.add(new Vector2(x1, y1));
                if (x1 == x2)
                    break;
                x1 += ix;
                d += dy2;
                if (d > dx) {
                    y1 += iy;
                    d -= dx2;
                }
            }
        } else {
            for (; ; ) {
                //plot(g, x1, y1);
                points.add(new Vector2(x1, y1));
                if (y1 == y2)
                    break;
                y1 += iy;
                d += dx2;
                if (d > dy) {
                    x1 += ix;
                    d -= dy2;
                }
            }
        }

        return points;
    }

    public static float findRange(Hex startHex, Hex endHex) {

        // convert to cube format
        int x = startHex.xTable;
        int z = startHex.yTable - (startHex.xTable + (startHex.xTable & 1)) / 2;
        int y = -x - z;
        Vector3 vstartHex = new Vector3(x, y, z);

        x = endHex.xTable;
        z = endHex.yTable - (endHex.xTable + (endHex.xTable & 1)) / 2;
        y = -x - z;
        Vector3 vendHex = new Vector3(x, y, z);

        float ans = (Math.abs(vstartHex.x - vendHex.x) + Math.abs(vstartHex.y - vendHex.y) + Math.abs(vstartHex.z - vendHex.z)) / 2;

        return ans;

    }

    /**
     *  Create an array which has hexes with least distance from start first
     * @param hexStart
     * @param arrHexIn
     * @return
     */
    public static ArrayList<Hex> getClosestDistanceMap(Hex hexStart, ArrayList<Hex> arrHexIn){
        ArrayList<Hex> arrHexWork = new ArrayList<>();
        ArrayList<Hex> arrReturn = new ArrayList<>();
        ArrayList<Float> arrDistance = new ArrayList<>();
        arrHexWork.addAll(arrHexIn);
        removeDupes(arrHexWork);
        int i=0;
        for (Hex hex:arrHexWork){
            float dist = HexHelper.findRange(hexStart, hex);
            for (i = 0; i < arrDistance.size(); i++) {
                if (dist < arrDistance.get(i)) {
                    break;
                }
            }
            arrReturn.add(i,hex);
            arrDistance.add(i,dist);
        }
        return arrReturn;
    }

    public static Hex getHex(int col, int row) {
        String strHex = col + "," + row;
        return Hex.hexTable[col][row];

    }

    /**
     *  get closes Hex  in an array
     * @param hexIn
     * @param arrMovePossible
     * @return closes hex
     */
    public static Hex getCloset(Hex hexIn, ArrayList<Hex> arrMovePossible) {
        ArrayList<Hex> arrReturn = new ArrayList<>();
        ArrayList<Float> arrDistance =  new ArrayList<>();
        int i;
        for (Hex hex: arrMovePossible) {
            float distance = HexHelper.findRange(hex, hexIn);
            for (i = 0; i < arrDistance.size(); i++) {
                if (distance < arrDistance.get(i)) {
                    break;
                }
            }
            arrDistance.add(i, distance);
            arrReturn.add(i, hex);
        }
        return arrReturn.get(0);
    }

    /**
     * Get all surrounding hexes
     *
     * @param hex
     * @param range
     * @return an array of hexes which have had their range updated.
     */
    public static ArrayList<Hex> getSurroundinghexes(Hex hex, int range) {
        int length = range;
        ArrayList<Hex> arrReturn = new ArrayList<Hex>();
        hex.setRange(0);
        if (length == 0) {
            arrReturn.add(hex);
            return arrReturn;
        }
        for (Hex hexw:hex.getSurround()){
            hexw.setRange(1);
        }
        if (length == 1) {
            return hex.getSurround();
        }
        ArrayList<Hex>[] arrHexList = new ArrayList[length];


        for (int i = 0; i < length; i++) {
            if (i == 0) {
                arrHexList[i] = hex.getSurround();
            } else {
                arrHexList[i] = new ArrayList<Hex>();
                for (Hex hex1 : arrHexList[i - 1]) {
                    for (Hex hex2:hex1.getSurround()){
                        if (!arrReturn.contains(hex2)){
                            arrHexList[i].add(hex2);
                            hex2.setRange(i+1);
                        }
                    }
                }
            }
            arrReturn.addAll(arrHexList[i]);
        }
        /**
         *  remove duplicates
         */
        removeDupes(arrReturn);
        return arrReturn;
    }


    public static void removeDupes(ArrayList<Hex> arrHexIn) {
        Set<Hex> aSet = new HashSet<Hex>();
        aSet.addAll(arrHexIn);
        arrHexIn.clear();
        arrHexIn.addAll(aSet);


    }

    public static void removeAxis(ArrayList<Hex> arrHexIn) {
        ArrayList<Hex> arrRemove = new ArrayList<>();
        for (Hex hex:arrHexIn){
            if (hex.checkAxisInHex()){
                arrRemove.add(hex);
            }
        }
        arrHexIn.removeAll(arrRemove);
    }
    public static void removeAllies(ArrayList<Hex> arrHexIn) {
        ArrayList<Hex> arrRemove = new ArrayList<>();
        for (Hex hex:arrHexIn){
            if (hex.checkAlliesInHex()){
                arrRemove.add(hex);
            }
        }
        arrHexIn.removeAll(arrRemove);
    }

    public class HexDistance{
        Hex hex;
        float distance;
        HexDistance(Hex hexIN, float distIn){
            hex = hexIN;
            distance = distIn;
        }
    }


}




