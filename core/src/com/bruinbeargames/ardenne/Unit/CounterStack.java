package com.bruinbeargames.ardenne.Unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.bruinbeargames.ardenne.FontFactory;
import com.bruinbeargames.ardenne.SplashScreen;

import java.util.ArrayList;

public class CounterStack {
    Unit unit;
    Label labelName;
    Label labelSub;
    Image imgSil;
    Stack stack;
    Label labelPoints;
    Image hilite;
    Image moved;
    Image backGround;
    Image division;
    Image step;
    boolean isHilited;
    boolean isShaded;
    static TextureAtlas textureAtlas = SplashScreen.instance.unitsManager.get("units/germancounteratlas.txt");
    static TextureRegion backGerman =  textureAtlas.findRegion("germanNew");
    static TextureRegion backFj =  textureAtlas.findRegion("luftwaffeNew");
    static TextureRegion backAllied =  textureAtlas.findRegion("alliedNew");
    static TextureRegion hilitePic =  textureAtlas.findRegion("hilite");
    static TextureRegion movePic =  textureAtlas.findRegion("moved");
    static TextureRegion abn101 =  textureAtlas.findRegion("abn101");
    static TextureRegion arm4 =  textureAtlas.findRegion("arm4");
    static TextureRegion arm9 =  textureAtlas.findRegion("arm9");
    static TextureRegion arm10 =  textureAtlas.findRegion("arm10");
    static TextureRegion fj5 =  textureAtlas.findRegion("fj5");
    static TextureRegion inf26 =  textureAtlas.findRegion("inf26");
    static TextureRegion inf28 =  textureAtlas.findRegion("inf28");
    static TextureRegion inf80 =  textureAtlas.findRegion("inf80");
    static TextureRegion pz2 =  textureAtlas.findRegion("pz2");
    static TextureRegion pzLehr =  textureAtlas.findRegion("pzlehr");
    static TextureRegion artCorp =  textureAtlas.findRegion("artcorp");
    static TextureRegion silamericaninf =  textureAtlas.findRegion("silamericaninf");
    static TextureRegion silarm =  textureAtlas.findRegion("silarm");
    static TextureRegion silart =  textureAtlas.findRegion("silart");
    static TextureRegion silgermanart =  textureAtlas.findRegion("silgermanart");
    static TextureRegion silgermaninf =  textureAtlas.findRegion("silgermaninf");
    static TextureRegion siljagd =  textureAtlas.findRegion("siljagd");
    static TextureRegion silmarder =  textureAtlas.findRegion("silmarder");
    static TextureRegion silmobileart =  textureAtlas.findRegion("silmobileart");
    static TextureRegion silnber =  textureAtlas.findRegion("silneber");
    static TextureRegion silpanzergrenadier =  textureAtlas.findRegion("silpanzergrenadier");
    static TextureRegion silpuma =  textureAtlas.findRegion("silpuma");
    static TextureRegion silstug =  textureAtlas.findRegion("silstug");
    static TextureRegion siltank =  textureAtlas.findRegion("siltank");
    static TextureRegion silAtank =  textureAtlas.findRegion("silsherman");
    static TextureRegion silAirArt =  textureAtlas.findRegion("silmairart");
    static TextureRegion silantiTank =  textureAtlas.findRegion("silantitank");
    static TextureRegion vg26 =  textureAtlas.findRegion("vg26");
    static TextureRegion vg276 =  textureAtlas.findRegion("vg276");
    static TextureRegion vg352 =  textureAtlas.findRegion("vg352");
    static TextureRegion onestep =  textureAtlas.findRegion("onestep");
    static TextureRegion twostep =  textureAtlas.findRegion("twostep");
    static TextureRegion threestep =  textureAtlas.findRegion("threestep");
    static TextureRegion truck =  textureAtlas.findRegion("truck");
    static TextureRegion pzarmy =  textureAtlas.findRegion("5pzarmy");

    //    static TextureRegion ammoall =  textureAtlas.findRegion("ammoall");
//    static TextureRegion ammowarn =  textureAtlas.findRegion("ammowarn");
//    static TextureRegion ammonone =  textureAtlas.findRegion("ammonone");
//    static TextureRegion hqvg276 =  textureAtlas.findRegion("twoseventysix");
 //   static TextureRegion hqvg352 =  textureAtlas.findRegion("threehundredfifty");
    static Label.LabelStyle labelStyleName
            = new Label.LabelStyle(FontFactory.instance.largeFont, Color.RED);

    static Label.LabelStyle labelStyleName2 = new Label.LabelStyle(FontFactory.instance.jumboFont, Color.WHITE);
    static ArrayList<CounterStack> arrHilited = new ArrayList<>();
    static ArrayList<CounterStack> arrShaded = new ArrayList<>();

    ArrayList<Actor> arrActors = new ArrayList<>();
    static boolean isFirstCall = true;

    CounterStack(Unit unit, Stack stack){

        this.unit = unit;
        this.stack = stack;
        if (unit.isAxis){
            createAxisStack(stack);
        }else{
            createAllied(stack);
        }

    }

    private void createAxisStack(Stack stack) {
        if (unit.designation.contains("FJ")){
            backGround  = new Image(backFj);

        }else{
            backGround = new Image(backGerman);
        }
        if (unit.subDesignation.contains("38")){
            int b=0;
        }
        stack.add(backGround);
//        stack.setScale(.8f);
        stack.setSize(Counter.size,Counter.size);
        division = getDivision(unit);
        division.setTouchable(Touchable.disabled);
        stack.addActor(division);
        arrActors.add(division);
        if (!unit.isTransport) {
            setStep(stack);
        }
        imgSil = getGermanSilhouttes(textureAtlas);
        imgSil.setTouchable(Touchable.disabled);
 //       imgSil.setAlign(Align.top);
 //       imgSil.setScale(1,.3F);
        stack.addActor(imgSil);
        arrActors.add(imgSil);
        setPoints();
 /*       if (!unit.isHQ) {
            setSupplyAmmo();
            if (unit.isMechanized) {
                setSupplyGas();
            }
        }*/
    }

    private Image getDivision(Unit unit) {
        Image image;
        switch (unit.designation){
            case "VG 352":
                image=new Image(vg352);
                break;
            case "VG 26":
                image=new Image(vg26);
                break;
            case "FJ 5":
                image=new Image(fj5);
                break;
            case "VG 276":
                image=new Image(vg276);
                break;
            case "2nd Pz":
                image=new Image(pz2);
                break;
            case "Pz Lehr":
                image=new Image(pzLehr);
                break;
            case "Supply":
                image=new Image(pzarmy);
                break;
            default:
                image=new Image(artCorp);
                break;


        }
        //       image.setScale(1.2F);
        return image;


    }

    public void setPoints(){

        String strPoints = null;
        if (unit.isArtillery){
            strPoints = " "+unit.getCurrenAttackFactor()+"    "+unit.getCurrentMovement();
        }else{
            strPoints = unit.getCurrenAttackFactor()+" "+unit.getCurrentDefenseFactor()+" "+unit.getCurrentMovement();
        }
        if (labelPoints != null){
            stack.removeActor(labelPoints);
            labelPoints = null;
        }
        labelPoints = new Label(strPoints,labelStyleName2);
        labelPoints.setTouchable(Touchable.disabled);
        labelPoints.setAlignment(Align.bottom);
        stack.addActor(labelPoints);
    }
    public void setStep(Stack stack){
        if (step != null){
            stack.removeActor(step);
        }
        if (unit.getCurrentStep() == 3){
            step = new Image(threestep);
        }else if (unit.getCurrentStep() == 2) {
            step = new Image(twostep);
        }else{
            step = new Image(onestep);
        }
        step.setTouchable(Touchable.disabled);
        stack.addActor(step);
    }
    /*
    public void setSupplyAmmo(){
        if (supplyAmmo != null){
            stack.removeActor(supplyAmmo);
        }
        if (unit.supplyUnit.getAmmoStatus() == SupplyUnit.Status.None){
            supplyAmmo = new Image(ammonone);
        }else if (unit.supplyUnit.getAmmoStatus() == SupplyUnit.Status.Half) {
            supplyAmmo = new Image(ammowarn);
        }else{
            supplyAmmo = new Image(ammoall);
        }
        supplyAmmo.setTouchable(Touchable.disabled);
        stack.add(supplyAmmo);
    }
    public void setSupplyGas(){
        if (unit.isAxis && !unit.isMechanized)
        {
            return;
        }
        if (supplyGas != null){
            stack.removeActor(supplyGas);
        }
        if (unit.supplyUnit.getGasStatus() == SupplyUnit.Status.None){
            supplyGas = new Image(gasnone);
        }else if (unit.supplyUnit.getGasStatus() == SupplyUnit.Status.Half) {
            supplyGas = new Image(gaswarn);
        }else{
            supplyGas = new Image(gasall);
        }
        supplyGas.setTouchable(Touchable.disabled);
        stack.add(supplyGas);
    }*/
    public void adjustFont(float adjust){
        labelPoints.setFontScale(adjust);
        if (labelName != null) { // allied
            labelName.setFontScale(adjust);
        }
    }
    private void createAllied(Stack stack) {
        backGround = new Image(backAllied);
        stack.add(backGround);
        stack.setSize(Counter.size,Counter.size);
        labelName= new Label(unit.designation,labelStyleName);
        labelName.setTouchable(Touchable.disabled);
        labelName.setAlignment(Align.top);
        stack.addActor(labelName);
        //       arrActors.add(labelName);
        imgSil = getAlliedilhouttes();
        imgSil.setTouchable(Touchable.disabled);
        stack.addActor(imgSil);
        arrActors.add(imgSil);
        setStep(stack);
        String str = String.valueOf(unit.getCurrentStep())+" ";
//        labelSub= new Label(str,labelStyleName);
//        labelSub.setTouchable(Touchable.disabled);
//
//        labelSub.setAlignment(Align.right);
//        labelSub.setPosition(30,0);/
//        labelSub.setFontScale(.8f);
//        stack.addActor(labelSub);

//        arrActors.add(labelName);

        String strPoints = null;
        if (unit.isArtillery){
            strPoints = " "+unit.getCurrenAttackFactor()+"    "+unit.getCurrentMovement();
        }else{
            strPoints = unit.getCurrenAttackFactor()+" "+unit.getCurrentDefenseFactor()+" "+unit.getCurrentMovement();
        }
        labelPoints = new Label(strPoints,labelStyleName2);
        labelPoints.setTouchable(Touchable.disabled);
        labelPoints.setAlignment(Align.bottom);
        stack.addActor(labelPoints);

//        arrActors.add(labelPoints);
    }

    private Image getGermanSilhouttes(TextureAtlas tex) {
        Image image;
        if (unit.isTransport){
            image = new Image(truck);
            return image;
        }
        switch (unit.type){
            case "Tank":
                image=new Image(siltank);
                break;
            case "Grenadier":
                image=new Image(silpanzergrenadier);
                break;
            case "Infantry":
                image=new Image(silgermaninf);
                break;
            case "Airborne":
                image=new Image(silgermaninf);
                break;
            case "AntiTank":
                image=new Image(siljagd);
                break;
          case "Recon":
                image=new Image(silpuma);
                break;
           case "Artillery":
                if (unit.isMobileArtillery) {
                    image = new Image(silmarder);
                }else{
                    image= new Image(silgermanart);
                }
                break;
             case "Neber":
                image=new Image(silnber);
                break;
            case "Werfer":
                image=new Image(silnber);
                break;

            default:
                image=new Image(silstug);
                break;


        }
 //       image.setScale(1.2F);
        return image;

    }
/*
    private Image getGermanHQs(TextureAtlas tex) {
        Image image = new Image();
        switch (unit.designation) {
            case "VG 276":
                image = new Image(hqvg276);
                break;
            case "VG 352":
                image = new Image(hqvg352);
                break;

            default:
                image = new Image(silstug);
                break;

        }
        return image;
    }*/

    private Image getAlliedilhouttes() {
        Image image;
        switch (unit.type){
            case "Grenadier":
                image=new Image(silarm);
                break;
            case "Infantry":
                image=new Image(silamericaninf);
                break;
            case "Airborne":
                image=new Image(silamericaninf);
                break;
            case "Tank":
                image=new Image(silarm);
                break;
            case "AntiTank":
                image=new Image(silantiTank);
                break;
            case "Recon":
                image=new Image(silpuma);
                break;
            case "Artillery":
                if (unit.designation.contains("101 Abn")){
                        image = new Image(silAirArt);
                }else if (unit.designation.contains("155 grp")){
                    image = new Image(silart);
                 }else{
                    image= new Image(silmobileart);
                }
                break;

            default:
                image=new Image(silstug);
                break;
       }
        //       image.setScale(1.2F);
        return image;

    }

    private void placeLabelName(Stack stack) {
        float stackX = stack.getX();
        float stackY = stack.getY();
        int x = (int)stackX + 6; // font size
        labelName.setPosition(x,stackY+80);
    }

    private void placeSil(Stack stack) {
        float stackX = stack.getX();
        float stackY = stack.getY();
        int x = (int)stackX + 15; // font size
        imgSil.setPosition(x,stackY+55);
    }
    public Stack getStack(){
        return stack;
    }


    private void placeLabelPoints(Stack stack) {
        float stackX = stack.getX();
        float stackY = stack.getY();
        int x = (int)stackX + 10; // font size
        labelPoints.setPosition(x,stackY+10);

    }
    public void shade()
    {
//		Gdx.app.log("Counter", "Shadeunit="+getUnit().toString());

        if (moved == null) {
            moved = new Image(movePic);
        }
        stack.add(moved);
        arrShaded.add(this);
        removeHilite(); // just in case
        isShaded = true;
    }

    /**
     *  unblacken the counter to show this counter is in play
     */
    public void removeShade()
    {
        stack.removeActor(moved);
        isShaded = false;
        arrShaded.remove(this);
    }
    /**
     *  Hilite with purpose for map counters
     */
    public void removeHilite() {
        stack.removeActor(hilite);
        arrHilited.remove(this);
        isHilited = false;
    }
    public void hilite() {
        if (hilite == null)
        {
            hilite = new Image(hilitePic);
        }
        stack.add(hilite);
        isHilited = true;
        arrHilited.add(this);
    }
    public static TextureRegion getAllied(){
        return backAllied;
    }

    public static TextureRegion getHilite() {
        return hilitePic;
    }

    public boolean isHilited(){
        return isHilited;
    }
    public boolean isShaded(){
        return isShaded;
    }
    public static void removeAllHilites(){
        ArrayList<CounterStack> arrWork = new ArrayList<>();
        arrWork.addAll(arrHilited);
        for (CounterStack cs:arrWork){
            cs.removeHilite();
        }
        arrHilited.clear();

    }
    public static void removeAllShaded(){
        ArrayList<CounterStack> arrWork = new ArrayList<>();
        arrWork.addAll(arrShaded);
        for (CounterStack cs:arrWork){
            cs.removeShade();
        }
        arrShaded.clear();

    }

    public void removeBack() {
        if (backGround != null) {
            backGround.setVisible(false);
        }
    }
    public void addBack() {
        if (backGround != null) {
            backGround.setVisible(true);
        }
    }

}
