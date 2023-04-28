package com.bruinbeargames.ardenne.Unit;

public class SupplyUnit {
    final static int maxBattles = 6;
    final static  int halfBattles =3;


    int battles;
    int artilleryShellsLeft;
    int movementLeft;
    Unit unit;
    SupplyUnit(Unit unit){
        this.unit = unit;
        battles = maxBattles;
        movementLeft = unit.getCurrentMovement();
        if (unit.isArtillery){
            if (unit.isAllies){
                artilleryShellsLeft = 2;
            }else{
                artilleryShellsLeft =1;
            }
        }
        movementLeft = unit.getCurrentMovement();
    }
    public Status getAmmoStatus(){
        if (unit.isArtillery){
            if (artilleryShellsLeft == 0){
                return Status.None;
            }else if (artilleryShellsLeft == 1){
                return Status.Half;
            }else{
                return Status.All;
            }
        }
        if (battles == 0){
            return Status.None;
        }else if (battles <= halfBattles){
            return Status.Half;
        }else{
            return Status.All;
        }
    }
    public Status getGasStatus(){
        if (unit.getCurrentMovement() == movementLeft){
            return Status.All;

        }else{
            if (movementLeft > 0){
                return Status.Half;
            }else{
                return Status.None;
            }
        }

    }
    public enum Status {None,Half,All};

    }

