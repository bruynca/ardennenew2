package com.bruinbeargames.ardenne.Hex;

import com.badlogic.gdx.Gdx;
import com.bruinbeargames.ardenne.GameLogic.Reinforcement;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class HiliteHex implements Observer {
    static public HiliteHex instance;
    ArrayList<Hex> arrHex = new ArrayList<>();
    private static ArrayList<HexHiliteDisplay> arrHexHilite = new ArrayList<>();
    TypeHilite type;
    ClickAction clickAction;
    ObserverPackage op;
    HexHiliteEdge hexHiliteEdge;
    public HiliteHex(ArrayList<Hex> arrHexIn, TypeHilite typeIn, ClickAction clickActionIn){
       instance = this;
        arrHex.clear();
        arrHex.addAll(arrHexIn);
        type = typeIn;
        clickAction = clickActionIn;
        arrHexHilite.clear();
//        if (typeIn == TypeHilite.Move) {
//            hexHiliteEdge = new HexHiliteEdge(arrHexIn);
//        }

        for (Hex hex:arrHex){
            HexHiliteDisplay hh = new HexHiliteDisplay(hex, typeIn);
            arrHexHilite.add(hh);
//            if (typeIn == TypeHilite.Move) {
//                hexHiliteEdge.createBorderImage(hex);
//            }

        }

        if (typeIn != TypeHilite.Debug) {
            ardenne.instance.addObserver(this);
        }
    }
    @Override
    public void update(Observable observable, Object o) {

        op = (ObserverPackage) o;
        Hex hex = op.hex;
        checkHit(hex);
  }



    public void checkHit(Hex hex) {
        if (arrHex.contains(hex)){
            Gdx.app.log("HiliteHex", "Got Hit");
            processHit(hex);
            clickAction = null;
        }else{
            Gdx.app.log("HiliteHex", "No Hit");

            if (clickAction != null){
                if (clickAction.typeAction == ClickAction.TypeAction.Move) {
                    cancelHit();
                    clickAction = null;
                }
            }else if (type == TypeHilite.Supply){
                Supply.instance.cancelSupplyPosition();
                if (Supply.instance.getSupplyWindow() != null){
                    Supply.instance.getSupplyWindow().cancelHilite();
                }
            }else if (type == TypeHilite.ReinforceDisplay){
                /**
                 *  check if popup is on
                 *  is it in middle of display  --- let display handle it
                 *  else end
                 */
                if (EventPopUp.instance.isShowing()){
                    return;
                }
                if (Reinforcement.instance.isDisplay()){
                    return;
                }
                Reinforcement.instance.endDisplay();
            }
        }
    }

    private void processHit(Hex hex) {
        if (type == TypeHilite.Supply){
            Supply.instance.process(hex);
        }else if(type==TypeHilite.SupplyAmerican) {
            return;
        }else if(type==TypeHilite.Reinforcement) {
            Reinforcement.instance.getScreen().doMove(hex, op);
        }else if (type ==TypeHilite.None || type==TypeHilite.Range) {
        }else if (type == TypeHilite.ReinforceDisplay) {
            Reinforcement.instance.showReinDisplay(hex);
        }
        else{
            if (clickAction != null) {
                clickAction.process(hex, false, type);
            }
        }
    }

    private void cancelHit(){
        clickAction.cancel();
    }


    public void remove() {

        HexHiliteDisplay.removeHexHilite();
//        hexHiliteEdge.remove();
        arrHex.clear();
        clickAction = null;
        ardenne.instance.deleteObserver(this);

    }

    public static void removeAll() {
        HexHiliteDisplay.removeHexHilite();
    }



    public  enum TypeHilite {Move,Advance,Supply,SupplyAmerican, None, Reinforcement,Range,ReinforceDisplay,
        Debug,AI,ShowSupply, MoveExit }{

    }
    
}
