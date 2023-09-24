package com.bruinbeargames.ardenne;

import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Unit.Unit;

import java.util.ArrayList;

public class ObserverPackage {

        public Type type;
        public Hex hex;
        public Unit unit;
        public int x;
        public int y;
        public ArrayList<Unit> arrUnits = new ArrayList<>();

        public ObserverPackage(Type type, Hex hex, int x, int y) {
            this.hex = hex;
            this.type = type;
            this.x =x;
            this.y = y;
        }
    public ObserverPackage(Type type, ArrayList<Unit> arIn) {
        arrUnits.addAll(arIn);
    }

    public ObserverPackage(Type type, Unit unit) {
        this.unit = unit;
        this.type = type;
        // TODO Auto-generated constructor stub
    }

    public ObserverPackage(Type type) {
        // TODO Auto-generated constructor stub
    }

    public enum Type {DiceRollFinished,MouseMoved, TouchUp,TouchUpMiddle,
        TouchUpShift, ESC,Moved,Stack,ConfirmYes,ConfirmNo,OK
        ,ScreenCentered,MoveFinished,CombatDisplayResults,AfterAttackDisplay,Advance,CardPlayed,
        FakeDone, EVENTPOPUPHIDE, SupplyDone,BastogneReinScored,EttleBruckRein,FakeScenario1Done,FakerDone
    }

    }



