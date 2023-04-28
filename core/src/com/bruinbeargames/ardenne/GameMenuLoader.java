package com.bruinbeargames.ardenne;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashMap;
import java.util.Map;

public class GameMenuLoader implements Disposable, AssetErrorListener {

    public static final GameMenuLoader instance = new GameMenuLoader();

    public GameMenu gameMenu;
    public BitmapFont font;
    public I18NBundle localization;
    public Flags flags;
    public Skin setUpSkin;
    public Setup skinAtlas;
    public GameButtons gameButtons;
    public Victory victory;

    private GameMenuLoader() {
    }

    public void assignAssets(AssetManager assetManager){

        TextureAtlas atlas = assetManager.get("menus/gamemenu.txt");
        gameMenu = new GameMenu(atlas);
        atlas = assetManager.get("menus/flags.txt");
        flags = new Flags(atlas);
        atlas = assetManager.get("menus/gameselectionbuttons.txt");
        gameButtons = new GameButtons(atlas);
        atlas = assetManager.get("menus/victory.txt");
        victory = new Victory(atlas);

        localization = assetManager.get("i18n/language", I18NBundle.class);
        font = assetManager.get("fonts/chinese24.fnt", BitmapFont.class);
    }

    public class Victory {

        public final Map<String, TextureAtlas.AtlasRegion> asset = new HashMap<>();

        public Victory(TextureAtlas atlas) {

            asset.put("background", atlas.findRegion("background"));
            asset.put("United Kingdom", atlas.findRegion("United Kingdom"));
            asset.put("german", atlas.findRegion("german"));
            asset.put("United States", atlas.findRegion("United States"));
            asset.put("russian", atlas.findRegion("russian"));
        }
    }

    public class GameMenu {

        public Map<String, TextureAtlas.AtlasRegion> asset = new HashMap<>();

        public GameMenu(TextureAtlas atlas) {

            asset.put("background", atlas.findRegion("background"));
            asset.put("selectedbutton", atlas.findRegion("selectedbutton"));
            asset.put("unselectedbutton", atlas.findRegion("unselectedbutton"));
            asset.put("tooltip", atlas.findRegion("tooltip"));
            asset.put("checkbox", atlas.findRegion("checkbox"));
            asset.put("checkboxselected", atlas.findRegion("checkboxselected"));
            asset.put("checkboxover", atlas.findRegion("checkboxover"));
            asset.put("checkboxselectedover", atlas.findRegion("checkboxselectedover"));
            asset.put("rightarrow", atlas.findRegion("rightarrow"));
            asset.put("leftarrow", atlas.findRegion("leftarrow"));
            asset.put("rightarrowselected", atlas.findRegion("rightarrowselected"));
            asset.put("leftarrowselected", atlas.findRegion("leftarrowselected"));
            asset.put("alliedturn", atlas.findRegion("alliedturn"));
            asset.put("axisturn", atlas.findRegion("axisturn"));
            asset.put("moveicon", atlas.findRegion("moveicon"));
            asset.put("autosetup", atlas.findRegion("autosetup"));
            asset.put("autosetupdisabled", atlas.findRegion("autosetupdisabled"));
            asset.put("autosetuppressed", atlas.findRegion("autosetuppressed"));
            asset.put("redo", atlas.findRegion("redo"));
            asset.put("redodisabled", atlas.findRegion("redodisabled"));
            asset.put("redopressed", atlas.findRegion("redopressed"));
            asset.put("restart", atlas.findRegion("restart"));
            asset.put("restartdisabled", atlas.findRegion("restartdisabled"));
            asset.put("restartpressed", atlas.findRegion("restartpressed"));
            asset.put("save", atlas.findRegion("save"));
            asset.put("savedisabled", atlas.findRegion("savedisabled"));
            asset.put("savepressed", atlas.findRegion("savepressed"));
            asset.put("loadsetup", atlas.findRegion("loadsetup"));
            asset.put("loadsetupdisabled", atlas.findRegion("loadsetupdisabled"));
            asset.put("loadsetuppressed", atlas.findRegion("loadsetuppressed"));
            asset.put("done", atlas.findRegion("done"));
            asset.put("donedisabled", atlas.findRegion("donedisabled"));
            asset.put("donepressed", atlas.findRegion("donepressed"));
            asset.put("window", atlas.findRegion("window"));
            asset.put("eventsbackground", atlas.findRegion("eventsbackground"));

            asset.put("back", atlas.findRegion("back"));
            asset.put("backpressed", atlas.findRegion("backpressed"));
            asset.put("fastforward", atlas.findRegion("fastforward"));
            asset.put("fastforwardpressed", atlas.findRegion("fastforwardpressed"));
            asset.put("pause", atlas.findRegion("pause"));
            asset.put("pausepressed", atlas.findRegion("pausepressed"));
            asset.put("play", atlas.findRegion("play"));
            asset.put("playpressed", atlas.findRegion("playpressed"));
            asset.put("replay", atlas.findRegion("replay"));
            asset.put("replaypressed", atlas.findRegion("replaypressed"));
            asset.put("rewind", atlas.findRegion("rewind"));
            asset.put("rewindpressed", atlas.findRegion("rewindpressed"));
            asset.put("stop", atlas.findRegion("stop"));
            asset.put("stoppressed", atlas.findRegion("stoppressed"));
            asset.put("pbembackground", atlas.findRegion("pbembackground"));

            asset.put("button", atlas.findRegion("button"));
            asset.put("buttonpressed", atlas.findRegion("buttonpressed"));

            asset.put("sliderbackground", atlas.findRegion("sliderbackground"));
            asset.put("sliderknob", atlas.findRegion("sliderknob"));

            asset.put("close", atlas.findRegion("close"));
            asset.put("help", atlas.findRegion("help"));
            asset.put("tab", atlas.findRegion("tab"));
            asset.put("tabselected", atlas.findRegion("tabselected"));
        }
    }

    public class Flags {

        public final Map<String, TextureAtlas.AtlasRegion> assets = new HashMap<>();

        public Flags(TextureAtlas atlas) {

            assets.put("china", atlas.findRegion("china"));
            assets.put("france", atlas.findRegion("france"));
            assets.put("germany", atlas.findRegion("germany"));
            assets.put("spain", atlas.findRegion("spain"));
            assets.put("usa", atlas.findRegion("usa"));

            assets.put("chinaover", atlas.findRegion("chinaover"));
            assets.put("franceover", atlas.findRegion("franceover"));
            assets.put("germanyover", atlas.findRegion("germanyover"));
            assets.put("spainover", atlas.findRegion("spainover"));
            assets.put("usaover", atlas.findRegion("usaover"));

        }
    }

    public class Setup {

        public final Map<String, TextureAtlas.AtlasRegion> assets = new HashMap<>();

        public Setup(TextureAtlas atlas) {

            assets.put("default-window", atlas.findRegion("default-window"));

        }
    }

    public class GameButtons {

        public final Map<String, TextureAtlas.AtlasRegion> assets = new HashMap<>();

        public GameButtons(TextureAtlas atlas) {

            assets.put("game1", atlas.findRegion("game1"));
            assets.put("game2", atlas.findRegion("game2"));
            assets.put("game1pressed", atlas.findRegion("game1pressed"));
            assets.put("game2pressed", atlas.findRegion("game2pressed"));
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

    }

    @Override
    public void dispose() {
        int i=0;


    }
}
