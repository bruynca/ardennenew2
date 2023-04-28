package com.bruinbeargames.ardenne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

public class UILoader {


    public static final UILoader instance = new UILoader();

    public Buttons buttons;
    public BottomMenu bottomMenu;
    public CombatDisplay combatDisplay;
    public UnitSelection unitSelection;
    public TopMenu topMenu;

    private UILoader(){

    }

    public void assignAssets(AssetManager assetManager){

        TextureAtlas atlas = assetManager.get("menus/buttons.txt");
        buttons = new Buttons(atlas);
        atlas = assetManager.get("menus/bottommenu.txt");
        bottomMenu = new BottomMenu(atlas);
        atlas = assetManager.get("effects/combatdisplay.txt");
        combatDisplay = new CombatDisplay(atlas);
        atlas = assetManager.get("menus/unitselection.txt");
        unitSelection = new UnitSelection(atlas);
        atlas = assetManager.get("menus/topmenu.txt");
        topMenu = new TopMenu(atlas);

    }

    public class Buttons {

        public final Map<String, TextureAtlas.AtlasRegion> button = new HashMap<>();

        public Buttons(TextureAtlas atlas) {

            button.put("buttonexitoff", atlas.findRegion("buttonexitoff"));
            button.put("buttonexiton", atlas.findRegion("buttonexiton"));
            button.put("buttonmapoff", atlas.findRegion("buttonmapoff"));
            button.put("buttonmapon", atlas.findRegion("buttonmapon"));
            button.put("buttonsaveoff", atlas.findRegion("buttonsaveoff"));
            button.put("buttonsaveon", atlas.findRegion("buttonsaveon"));
            button.put("buttonobjectiveoff", atlas.findRegion("buttonobjectiveoff"));
            button.put("buttonobjectiveon", atlas.findRegion("buttonobjectiveon"));
            button.put("supplyoff", atlas.findRegion("supplyoff"));
            button.put("supplyon", atlas.findRegion("supplyon"));
            button.put("menuon", atlas.findRegion("menuon"));
            button.put("menuoff", atlas.findRegion("menuoff"));
            button.put("effectsoff", atlas.findRegion("effectsoff"));
            button.put("effectson", atlas.findRegion("effectson"));
            button.put("manualoff", atlas.findRegion("manualoff"));
            button.put("manualon", atlas.findRegion("manualon"));
            button.put("musicoff", atlas.findRegion("musicoff"));
            button.put("musicon", atlas.findRegion("musicon"));
            button.put("keyboardoff", atlas.findRegion("keyboardoff"));
            button.put("keyboardon", atlas.findRegion("keyboardon"));
        }
    }

    public class BottomMenu {

        public Map<String, TextureAtlas.AtlasRegion> button = new HashMap<>();

        public BottomMenu(TextureAtlas atlas) {

            button.put("germansupply", atlas.findRegion("germansupply"));
            button.put("germansupplypressed", atlas.findRegion("germansupplypressed"));
            button.put("hidemenu", atlas.findRegion("hidemenu"));
            button.put("hidemenupressed", atlas.findRegion("hidemenupressed"));
            button.put("showmenu", atlas.findRegion("showmenu"));
            button.put("nextphase", atlas.findRegion("nextphase"));
            button.put("nextphasepressed", atlas.findRegion("nextphasepressed"));
            button.put("russiansupply", atlas.findRegion("russiansupply"));
            button.put("russiansupplypressed", atlas.findRegion("russiansupplypressed"));
            button.put("border", atlas.findRegion("border"));

            button.put("germanreinforcement", atlas.findRegion("germanreinforcement"));
            button.put("germanreinforcementpressed", atlas.findRegion("germanreinforcementpressed"));
            button.put("russianreinforcement", atlas.findRegion("russianreinforcement"));
            button.put("russianreinforcementpressed", atlas.findRegion("russianreinforcementpressed"));
            button.put("russiansea", atlas.findRegion("russiansea"));
            button.put("russianseapressed", atlas.findRegion("russianseapressed"));
            button.put("turnbox", atlas.findRegion("turnbox"));
            button.put("save", atlas.findRegion("save"));
            button.put("savepressed", atlas.findRegion("savepressed"));
            button.put("load", atlas.findRegion("load"));
            button.put("loadpressed", atlas.findRegion("loadpressed"));
        }
    }

    public class UnitSelection{

        public Map<String, TextureAtlas.AtlasRegion> asset = new HashMap<>();

        public UnitSelection(TextureAtlas atlas) {

            asset.put("window", atlas.findRegion("window"));
            asset.put("windowtransparent", atlas.findRegion("windowtransparent"));
            asset.put("ok", atlas.findRegion("ok"));
            asset.put("okpressed", atlas.findRegion("okpressed"));
            asset.put("cancel", atlas.findRegion("cancel"));
            asset.put("cancelpressed", atlas.findRegion("cancelpressed"));
            asset.put("line", atlas.findRegion("line"));
        }
    }

    public class TopMenu{

        public Map<String, TextureAtlas.AtlasRegion> asset = new HashMap<>();

        public TopMenu(TextureAtlas atlas) {

            asset.put("background", atlas.findRegion("background"));
            asset.put("german", atlas.findRegion("german"));
            asset.put("usa", atlas.findRegion("usa"));

        }
    }

    public class CombatDisplay {

        public Map<String, TextureAtlas.AtlasRegion> asset = new HashMap<>();

        public CombatDisplay(TextureAtlas atlas) {

            asset.put("backgroundgerman", atlas.findRegion("backgroundgerman"));
            asset.put("backgroundrussian", atlas.findRegion("backgroundrussian"));
            asset.put("germanbarragedisplay", atlas.findRegion("germanbarragedisplay"));
            asset.put("russianbarragedisplay", atlas.findRegion("russianbarragedisplay"));
            asset.put("combatresultsdisplaygerman", atlas.findRegion("combatresultsdisplaygerman"));
            asset.put("combatresultsdisplayrussian", atlas.findRegion("combatresultsdisplayrussian"));
            asset.put("barrageresultsgerman", atlas.findRegion("barrageresultsgerman"));
            asset.put("barrageresultsrussian", atlas.findRegion("barrageresultsrussian"));
            asset.put("target", atlas.findRegion("target"));
            asset.put("barrage", atlas.findRegion("barrage"));
            asset.put("artillery", atlas.findRegion("artillery"));
            asset.put("g200", atlas.findRegion("g200"));
            asset.put("broken", atlas.findRegion("broken"));
            asset.put("entrenchment", atlas.findRegion("entrenchment"));
            asset.put("fortificationattacker", atlas.findRegion("fortificationattacker"));
            asset.put("fortificationdefender", atlas.findRegion("fortificationdefender"));
            asset.put("germanaircraft", atlas.findRegion("germanaircraft"));
            asset.put("germansteploss", atlas.findRegion("germansteploss"));
            asset.put("germansteplossx", atlas.findRegion("germansteplossx"));
            asset.put("line", atlas.findRegion("line"));
            asset.put("supply", atlas.findRegion("supply"));
            asset.put("marsh", atlas.findRegion("marsh"));
            asset.put("hills", atlas.findRegion("hills"));
            asset.put("russianaircraft", atlas.findRegion("russianaircraft"));
            asset.put("russiansteploss", atlas.findRegion("russiansteploss"));
            asset.put("russiansteplossx", atlas.findRegion("russiansteplossx"));
            asset.put("town", atlas.findRegion("town"));
            asset.put("concentricattack", atlas.findRegion("concentricattack"));
            asset.put("outerglow", atlas.findRegion("outerglow"));
            asset.put("0:0", atlas.findRegion("0to0"));
            asset.put("1:1", atlas.findRegion("1to1"));
            asset.put("1:2", atlas.findRegion("1to2"));
            asset.put("1:3", atlas.findRegion("1to3"));
            asset.put("1:4", atlas.findRegion("1to4"));
            asset.put("1:5", atlas.findRegion("1to5"));
            asset.put("2:1", atlas.findRegion("2to1"));
            asset.put("3:1", atlas.findRegion("3to1"));
            asset.put("4:1", atlas.findRegion("4to1"));
            asset.put("5:1", atlas.findRegion("5to1"));
            asset.put("6:1", atlas.findRegion("6to1"));
            asset.put("7:1", atlas.findRegion("7to1"));
            asset.put("8:1", atlas.findRegion("8to1"));
            asset.put("9:1", atlas.findRegion("9to1"));
            asset.put("10:1", atlas.findRegion("10to1"));
            asset.put("11:1", atlas.findRegion("11to1"));
            asset.put("12;1", atlas.findRegion("12to1"));
            asset.put("13:1", atlas.findRegion("13to1"));
            asset.put("14:1", atlas.findRegion("14to1"));
            asset.put("15:1", atlas.findRegion("15to1"));
            asset.put("plane1", atlas.findRegion("plane1"));
            asset.put("plane2", atlas.findRegion("plane2"));
            asset.put("plane3", atlas.findRegion("plane3"));
            asset.put("plane4", atlas.findRegion("plane4"));
            asset.put("plane5", atlas.findRegion("plane5"));
            asset.put("combat", atlas.findRegion("combat"));
            asset.put("mobileassault", atlas.findRegion("mobileassault"));
            asset.put("attackbutton", atlas.findRegion("attackbutton"));
            asset.put("cancelbutton", atlas.findRegion("cancelbutton"));
            asset.put("attackbuttonover", atlas.findRegion("attackbuttonover"));
            asset.put("cancelbuttonover", atlas.findRegion("cancelbuttonover"));
            asset.put("romaniandefender", atlas.findRegion("romaniandefender"));
            asset.put("antitank", atlas.findRegion("antitank"));
            asset.put("flak", atlas.findRegion("flak"));
        }
    }
}