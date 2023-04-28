package com.bruinbeargames.ardenne.Hex;


public class HexCanNotCross {
    static private String[][] strTable ={{"6,7", "5,6"},
            {"6,7","5,7"},
            {"4,23","4,24"},
            {"6,26","7,25"},
            {"6,26","7,26"},
            {"8,24","8,23"},
            {"7,20","8,20"},
            {"5,20","6,20"},
            {"3,4","4,5"},
            {"4,4","4,5"},
            {"4,4","5,4"}};
    static public  void  load() {
/*        for (String[] str:strTable){
            Hex hex1 = Hex.hexTableMapUI.instance.getHexMap().get(str[0]);
            Hex hex2 = MapUI.instance.getHexMap().get(str[1]);
            HexCanNotCross hexCantCross = find(hex1);
            if (hexCantCross == null)
            {
                hexCantCross = new HexCanNotCross(hex1,hex2);
                arrCantCross.add(hexCantCross);
            }else{
                hexCantCross.add(hex2);
            }
            /**
             * do reverse
             */
 /*           hexCantCross = find(hex2);
            if (hexCantCross == null)
            {
                hexCantCross = new HexCanNotCross(hex2,hex1);
                arrCantCross.add(hexCantCross);
            }else{
                hexCantCross.add(hex1);
            }

        }
    }
    static ArrayList<HexCanNotCross> arrCantCross = new ArrayList<>();

    private static HexCanNotCross find(Hex hex1) {
        for(HexCanNotCross hexCanNotCross:arrCantCross) {
            if (hexCanNotCross.hex == hex1) {
                return hexCanNotCross;
            }
        }
        return null;
    }
    static public  boolean checkCanCross(Hex hexFrom, Hex hexTo){
        HexCanNotCross hexCanNotCross = find(hexFrom);
        if (hexCanNotCross  != null){
            if (hexCanNotCross.arrHexes.contains(hexTo)){
                return false;
            }
        }
        return true;
    }

    Hex hex;
     ArrayList<Hex> arrHexes = new ArrayList<>();
     HexCanNotCross(Hex hex, Hex hex2){
            this.hex = hex;
            arrHexes.add(hex2);
     }
     private void add(Hex hexAdd){
         if (!arrHexes.contains(hexAdd)){
             arrHexes.add(hexAdd);
         }
     }



}
*/
    }

    public static boolean checkCanCross(Hex hexStart, Hex hexEndSearch) {
        return true;
    }
}
