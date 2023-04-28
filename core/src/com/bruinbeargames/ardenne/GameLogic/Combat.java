package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Timer;
import com.bruinbeargames.ardenne.AI.AIOrders;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HexHelper;
import com.bruinbeargames.ardenne.NextPhase;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SaveGame;
import com.bruinbeargames.ardenne.UI.AttackArrows;
import com.bruinbeargames.ardenne.UI.CombatDisplay;
import com.bruinbeargames.ardenne.UI.EventOK;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.TurnCounter;
import com.bruinbeargames.ardenne.UI.WinCRT;
import com.bruinbeargames.ardenne.UILoader;
import com.bruinbeargames.ardenne.Unit.ClickAction;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.WinModal;
import com.bruinbeargames.ardenne.ardenne;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Combat implements Observer {

    static public Combat instance;
    boolean isAllies;
    boolean isAI;
    private I18NBundle i18NBundle;

    public ArrayList<Hex> arrHexDefender = new ArrayList<>();
    ArrayList<Stack> arrCombatImageStack = new ArrayList<>();
    ArrayList<ClickListener> arrCombatImageStackListners = new ArrayList<>();

//    WinCombat wincombat;
    Hex hexTarget;
    public ArrayList<Hex> arrHexAttackers = new ArrayList<>();
    ArrayList<Hex> arrHexCheckFirstTime = new ArrayList<>();
    ArrayList<Unit> arrTempAttackers = new ArrayList<>();
    Hex hexHilite;
    //	HiliteHex hiliteHex;
    Attack attack;
    boolean isCancelAttack = false;
    boolean isAttackArrows = false;
    TextureRegion combat = new TextureRegion(UILoader.instance.combatDisplay.asset.get("combat"));
    TextButton attackButton;
    TextButton cancelButton;
    static Vector2 positionWinCombat = new Vector2(-100, 0);
    static Vector2 positionWinEnemy = new Vector2(-100, 0);

    public Combat() {
        instance = this;        i18NBundle= GameMenuLoader.instance.localization;

        initializeButtons();
    }
    /**
     * Initialize the Combat Phase
     *
     * @param isAllies
     */
    public void Intialize(boolean isAllies, boolean isAI) {
        Gdx.app.log("Combat", "Initialize");
        this.isAllies = isAllies;
        this.isAI = isAI;
        Unit.initUnShade();
        Hex.initCombatFlags();
        doCombatPhase();

    }
    public void doCombatPhase() {
          Gdx.app.log("Combat", "doCombatphase");
        SaveGame.SaveLastPhase(" Last Turn", 2);

        arrHexDefender.clear();
        arrHexCheckFirstTime.clear();
        if (isAllies){
            Unit.shadeAllAllies();
        }else{
            Unit.shadeAllAxis();
        }
        CombatDisplay.instance.end();
        if (isAllies) {
            TurnCounter.instance.updateText(i18NBundle.get("combata"));
        }else{
            TurnCounter.instance.updateText(i18NBundle.get("combatg"));

        }
        ArrayList<Unit> arrUnitWorkFindHexesCanAttack;
    /**
     *  get attacking units
     */
        if (isAllies) {
        arrUnitWorkFindHexesCanAttack = Unit.getOnBoardAllied();
        } else {
        arrUnitWorkFindHexesCanAttack = Unit.getOnBoardAxis();
        }
    /**
     * get hexes that the units can attack
     */
        for (Unit unit : arrUnitWorkFindHexesCanAttack) {
            Hex hexUnit = unit.getHexOccupy();
            if (unit.canAttackThisTurn && !unit.isArtillery&& !unit.isEliminated() && !unit.isDisorganized()&& unit.getCurrenAttackFactor() > 0) {
                ArrayList<Hex> arrHexWork = hexUnit.getSurround();
                for (Hex hex : arrHexWork) {
                        if (hex.getUnitsInHex().size() > 0) {
                            if (isAllies && hex.checkAxisInHex()|| !isAllies && hex.checkAlliesInHex()) {
                                if (!hex.isHasBeenAttackedThisTurn()) {
                                    arrHexDefender.add(hex);
                                }
                            }
                        }
                }
            }else{
                unit.getMapCounter().getCounterStack().shade();
            }
        }
         HexHelper.removeDupes(arrHexDefender);
        if (arrHexDefender.size() > 0){
            designateWhoCanBeAttacked(arrHexDefender);
        }else{
            if (isAI){
 //               String str = i18NBundle.get("aicombatdone");
 //               EventOK.instance.addObserver((this));
//                EventOK.instance.show(str);
                return;
            }

            String str = i18NBundle.get("nomorecombat");
            EventPopUp.instance.show(str);
            return;
        }
    }
    public void initializeButtons() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("attackbutton"))),
                new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("attackbuttonover"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("attackbuttonover")));
        if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            textButtonStyle.font.getData().scale(1f);
        }

        attackButton = new TextButton(GameMenuLoader.instance.localization.get("attack"), textButtonStyle);
//        attackButton.setSize(169 , 49 );
        attackButton.setSize(200 , 70 );
        attackButton.getLabelCell().padRight(20);
        attackButton.getLabel().setFontScale(2f);


        textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("cancelbutton"))),
                new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("cancelbuttonover"))),
                null,
                Fonts.getFont24());
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(UILoader.instance.combatDisplay.asset.get("cancelbuttonover")));
       if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
           textButtonStyle.font.getData().scale(1f);
       }

     cancelButton = new TextButton(GameMenuLoader.instance.localization.get("cancel"), textButtonStyle);
//     cancelButton.setSize(169, 49);
     cancelButton.setSize(200,70);
     cancelButton.getLabelCell().padLeft(20);
     cancelButton.getLabel().setFontScale(2F);
    }

    private void designateWhoCanBeAttacked(ArrayList<Hex> arrHexDefender) {
        Gdx.app.log("Combat", "designateWhoCanBeAttacked");

        clearStacks();
        ArrayList<Hex> arrHexSchedule = new ArrayList<>();
        for (Hex hex : arrHexDefender) {
            if (isAI){
                createCombatImage(hex, false);
            }else {
                arrHexSchedule.add(hex);
            }
        }
        if (arrHexSchedule.size() > 0 ){
            WinModal.instance.set();
            scheduleAttackHilite(arrHexSchedule);
        }
    }
    private void scheduleAttackHilite(final ArrayList<Hex> arrHexToHilite) {
        final Hex hex = arrHexToHilite.get(0);
        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               SoundsLoader.instance.playLimber();
                               createCombatImage(hex, true);
                               arrHexToHilite.remove(hex);
                               if (arrHexToHilite.size() == 0) {
                                   WinModal.instance.release();
                                   return;
                               } else {
                                   scheduleAttackHilite(arrHexToHilite);
                               }
                           }
                       }
                , .16F        //    (delay)
        );
    }

    public void clearStacks() {
        Gdx.app.log("Combat", "clearStacks");

        for (Stack stack : arrCombatImageStack) {
            stack.remove();
        }
        arrCombatImageStack.clear();
    }
    public void createCombatImage(final Hex hex, boolean isClick) {
        Gdx.app.log("Combat", "createCombatImage");

        Image image = new Image(combat);
        final Stack stack = new Stack();
        arrCombatImageStack.add(stack);
        stack.add(image);
        stack.setSize(136, 156);
        Vector2 pos = hex.getCounterPosition();
        stack.setPosition(pos.x - 10, pos.y - 7);
//        stack.setPosition(pos.x - 18, pos.y - 20);

        final Hex hexClick = hex;
        if (isClick) {
            ClickListener clickListener = new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    attack = new Attack(hex, isAllies,false,isAI,null);
                    createAttackDisplay(hexClick);
                }
            };
            stack.addListener(clickListener);
            arrCombatImageStackListners.add(clickListener);
        }else{
        }
//		stack.addAction(Actions.sequence(Actions.fadeOut(0.05f), Actions.fadeIn(0.25f)));
        ardenne.instance.mapStage.addActor(stack);
    }
    public void setAttackForAI(Attack attack){
        this.attack = attack;
    }

    /**
     *
     * @param hex
     */
    public void createAttackDisplay(Hex hex) {
        Gdx.app.log("Combat", "createAttackDispay hex=" + hex);
        clearStacks();
        createCombatImage(hex, false);
        hexTarget = hex;
        /**
         * get all attackers
         */
        ArrayList<Hex> arrHexSurround = hex.getSurround();
        arrTempAttackers = new ArrayList<>();
        arrHexAttackers = new ArrayList<>();
        for (Hex hexCheck : arrHexSurround) {
            if (isAllies && hexCheck.checkAlliesInHex() || !isAllies && hexCheck.checkAxisInHex()) {
                for (Unit unit : hexCheck.getUnitsInHex()) {
                    if (unit.canAttackThisTurn && !unit.isArtillery && !unit.isEliminated()&& unit.getCurrenAttackFactor() > 0) {
                        arrTempAttackers.add(unit);
                        if (!arrHexAttackers.contains(unit.getHexOccupy())) {
                            arrHexAttackers.add(unit.getHexOccupy());
                        }
                        ClickAction clickAction = new ClickAction(unit, ClickAction.TypeAction.CombatClick);
                        unit.getMapCounter().getCounterStack().removeShade();
                    } else {
                        /**
                         *  so they dont get click
                         */
                        if (unit.isArtillery) {
                            unit.getHexOccupy().moveUnitToBack(unit);
                            unit.getMapCounter().rePlace(unit.getHexOccupy());
  //                          reDisplay();
                        }
                    }
                }
            }
        }

        hexTarget = hex;
        AttackArrows.getInstance().showArrows(arrHexAttackers, hexTarget);
        ardenne.instance.addObserver(this);
    }

    /**
     *   For AI
     * @return hexes that can be attacked
     */
    public


    ArrayList<Hex> getAttackedHexesForAI(boolean isAllies){
        ArrayList<Unit> arrUnitWorkFindHexesCanAttack;
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        if (isAllies) {
            arrUnitWorkFindHexesCanAttack = Unit.getOnBoardAllied();
        } else {
            arrUnitWorkFindHexesCanAttack = Unit.getOnBoardAxis();
        }
        /**
         * get hexes that the units can attack
         */
        for (Unit unit : arrUnitWorkFindHexesCanAttack) {
            Hex hexUnit = unit.getHexOccupy();
            if (unit.canAttackThisTurn && !unit.isArtillery && !unit.isEliminated() && !unit.isDisorganized() && unit.getCurrenAttackFactor() > 0) {
                ArrayList<Hex> arrHexWork = hexUnit.getSurround();
                for (Hex hex : arrHexWork) {
                    if (hex.getUnitsInHex().size() > 0) {
                        if (isAllies && hex.checkAxisInHex() || !isAllies && hex.checkAlliesInHex()) {
                            if (!hex.isHasBeenAttackedThisTurn()) {
                                arrHexReturn.add(hex);
                            }
                        }
                    }
                }
            }
        }
        return arrHexReturn;
    }
    public  ArrayList<Unit> getUnitsCanAttackForAI(Hex hex, boolean isAllies){
        ArrayList<Hex> arrHexSurround = hex.getSurround();
        ArrayList<Unit> arrReturn = new ArrayList<>();
        for (Hex hexCheck : arrHexSurround) {
            if (isAllies && hexCheck.checkAlliesInHex() || !isAllies && hexCheck.checkAxisInHex()) {
                for (Unit unit : hexCheck.getUnitsInHex()) {
                    if (unit.canAttackThisTurn && !unit.isArtillery && !unit.isEliminated()&& unit.getCurrenAttackFactor() > 0) {
                        arrReturn.add(unit);
                    }
                }
            }
        }
        return arrReturn;
    }

    public void refreahAttackCancel(){
        addAttackCancel(hexTarget);
    }
    private void addAttackCancel(Hex hex) {
        Gdx.app.log("Combat", "addAttackCancel");
        ardenne.instance.deleteObserver(this);
        isCancelAttack = true;
        isAttackArrows = true;
        Vector2 pos = hex.getCounterPosition();
        attackButton.setPosition(pos.x + 100, pos.y - 10);
        attackButton.clearListeners();
        attackButton.remove();
        if (!isAI) {
            attackButton.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("Combat", "addAttackCancel pressed");

                    if (attack.arrAttackers.size() == 0) {
                        //                   EventManager.instance.errorMessage(GameMenuLoader.instance.localization.get("error7"));
                        return;
                    } else {
                        cleanup(true);
                        attack.dieRoll();
                    }
                    return;
                }
            });
        }
        ardenne.instance.mapStage.addActor(attackButton);
        attackButton.toFront();
//        Unit.stateEngine.getMapStage().addActor(attackButton);

        cancelButton.setPosition(pos.x - 190, pos.y - 10);
        cancelButton.clearListeners();
        cancelButton.remove();
        if (!isAI) {
            cancelButton.addListener(new ClickListener(Input.Buttons.LEFT) {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boolean isCancel = true;
                    cleanup(isCancel);
                    doCombatPhase();
                    return;
                }
            });
        }
      ardenne.instance.mapStage.addActor(cancelButton);
       cancelButton.toFront();

    }

    public void cleanup(boolean isCancel) {
        Gdx.app.log("Combat", "cleanupDisplay");
        if (isCancel){
                for (Unit unit: arrTempAttackers){
                    if (!unit.isEliminated()) {
                        unit.getMapCounter().getCounterStack().removeHilite();
                    }
                }
                ClickAction.cancelAll();
        }
        clearStacks();
        AttackArrows.getInstance().removeArrows();
        isAttackArrows = false;

        attackButton.clearListeners();
        attackButton.remove();
        cancelButton.clearListeners();
        cancelButton.remove();
  //      CombatDisplayResults.instance.hide();
        WinCRT.instance.end();
        CombatDisplay.instance.end();

        ardenne.instance.deleteObserver(this);

    }
    public boolean isAttackArrows(){
        return isAttackArrows;
    }

    public void removeUnit(Unit unit) {
        Gdx.app.log("Combat", "removeUnit");

        attack.removeAttacker(unit);
        if (attack.arrAttackers.size() == 0){
            attackButton.clearListeners();
            attackButton.remove();
            cancelButton.clearListeners();
            cancelButton.remove();
        }
    }
   public void addUnit(Unit unit) {
       Gdx.app.log("Combat", "addUnit");

       attack.addAttacker(unit, false);
       if (attack.arrAttackers.size() == 1){
           addAttackCancel(attack.hexTarget);
       }
       addAttackCancel(hexTarget);

   }

    /**
     *  Does not require Combat to be invoked
     * @param isAllies
     * @param aio
     * @return
     */
    public static ArrayList<Hex> getAttackedHexesForAI(boolean isAllies, AIOrders aio){
        ArrayList<Hex> arrHexReturn = new ArrayList<>();
        /**
         * get hexes that the units can attack
         */
        int ix=0;
        for (Unit unit : aio.getArrUnit()) {
            Hex hexUnit = aio.getArrHexMoveTo().get(ix);
            if (unit.canAttackThisTurn && !unit.isArtillery && !unit.isEliminated() && !unit.isDisorganized() && unit.getCurrenAttackFactor() > 0) {
                ArrayList<Hex> arrHexWork = hexUnit.getSurround();
                for (Hex hex : arrHexWork) {
                    if (hex.getUnitsInHex().size() > 0) {
                        if (isAllies && hex.checkAxisInHex() || !isAllies && hex.checkAlliesInHex()) {
                            if (!arrHexReturn.contains(hex)) {
                                arrHexReturn.add(hex);
                            }
                        }
                    }
                }
            }
            ix++;
        }
        return arrHexReturn;
    }

    @Override
    public void update(Observable observable, Object o) {
        ObserverPackage oB = (ObserverPackage) o;
        Hex hex = oB.hex;
        /**
         *  OK for AI
         */
        if (oB.type != ObserverPackage.Type.OK && isAI) {
            EventOK.instance.deleteObserver(this);
            NextPhase.instance.nextPhase();
            return;
        }
        /**
         * hex touched
         */
        if (oB.type != ObserverPackage.Type.TouchUp) {
            return;
        }
        if (arrHexAttackers.contains(hex) || hex == hexTarget) {
            return;
        }
        /**
         *  implied combatdisplayresult
         */
        cleanup(true);
        doCombatPhase();
    }


}

