package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.Hex.Hex;

public class SplashScreen {
    static public SplashScreen instance;
    Image splashImage;
    public Image splashMapImage;
    Texture bruinbearLogo;
    Logo logo;

    private final AssetManager assetManager;
    public AssetManager soundsManager;
    public AssetManager effectsManager;
    private AssetManager mapsManager;
    public  AssetManager unitsManager;
    public  AssetManager cardManager;
    private AssetManager UIManager;
    private SoundsLoader soundAssets;
    private TextureAtlas atlas;
    private TextureAtlas logoAtlas;
    private Texture map;
    private TextureRegion menuimage = null;
    private boolean beenHere;
    public final BitmapFont font = new BitmapFont();
    private final Stage stage;
    private float screenHeight;
    private float screenWidth;
    private String status;
    private boolean oldBuild;
    private LanguageSupport languageSupport;
    private GameMenu gameMenu;
    private Array<Actor> arrActorsMenu;
    Sound sound;


    public SplashScreen() {
        Gdx.app.log("SplashScreen", "COnstructor");

        instance = this;
        this.stage = ardenne.instance.guiStage;
        stage.clear();
//        Gdx.input.setInputProcessor(stage);

        assetManager = new AssetManager();

        // Load splash screen and music
        assetManager.load("sounds/bastogne.mp3", Sound.class);
        assetManager.finishLoading();
        sound = assetManager.get("sounds/bastogne.mp3");
        float level =  GamePreferences.getVolume();
        sound.play(level);
        assetManager.load("menus/splashscreen.jpg", Texture.class);
        assetManager.finishLoading();
        Texture splash = assetManager.get("menus/splashscreen.jpg", Texture.class);
        assetManager.load("menus/splashmap.png", Texture.class);
        assetManager.finishLoading();
        Texture splashmap = assetManager.get("menus/splashmap.png", Texture.class);
        assetManager.load("logo/logo.txt", TextureAtlas.class);
        assetManager.finishLoading();
        logoAtlas = assetManager.get("logo/logo.txt");
        assetManager.finishLoading();

        AssetManager gameMenuManager = new AssetManager();
        gameMenuManager.load("menus/gamemenu.txt", TextureAtlas.class);
        gameMenuManager.load("menus/flags.txt", TextureAtlas.class);
        gameMenuManager.load("menus/victory.txt", TextureAtlas.class);
        gameMenuManager.load("fonts/chinese24.fnt", BitmapFont.class);
        gameMenuManager.load("i18n/language", I18NBundle.class);
//        gameMenuManager.load("console/uiskin.atlas", TextureAtlas.class);
        gameMenuManager.load("menus/gameselectionbuttons.txt", TextureAtlas.class);

        gameMenuManager.finishLoading();
        GameMenuLoader.instance.assignAssets(gameMenuManager);
        atlas = gameMenuManager.get("menus/gamemenu.txt");
        GameMenuLoader.instance.localization = gameMenuManager.get("i18n/language", I18NBundle.class);

        soundsManager = new AssetManager();
        effectsManager = new AssetManager();
        mapsManager = new AssetManager();
        unitsManager = new AssetManager();
        UIManager = new AssetManager();
        cardManager = new AssetManager();

        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        switch(Gdx.app.getType()) {
            case Android:
                break;
            case iOS:
                break;
            case Desktop:
                parameters.generateMipMaps = true;
                parameters.textureMinFilter = Texture.TextureFilter.MipMapNearestLinear;
                break;
            default:
                break;
        }

        // Load Maps
        mapsManager.load("map/ardenne.jpg", Texture.class);

        effectsManager.load("effects/dicefronts.txt", TextureAtlas.class);
        effectsManager.load("effects/dicefrontsblue.txt", TextureAtlas.class);
        effectsManager.load("effects/supply.txt", TextureAtlas.class);
        effectsManager.load("effects/unitmarkers.txt", TextureAtlas.class);
        effectsManager.load("effects/textfield.txt", TextureAtlas.class);
        effectsManager.load("effects/attackarrows.txt", TextureAtlas.class);
//
        unitsManager.load("units/germancounteratlas.txt", TextureAtlas.class);
//        unitsManager.load("units/romanianunits.txt", TextureAtlas.class);
//
        UIManager.load("menus/buttons.txt", TextureAtlas.class);
        UIManager.load("menus/bottommenu.txt", TextureAtlas.class);
        UIManager.load("effects/combatdisplay.txt", TextureAtlas.class);
        UIManager.load("menus/unitselection.txt", TextureAtlas.class);
        UIManager.load("menus/topmenu.txt", TextureAtlas.class);
//
        soundsManager.load("sounds/die_roll.mp3", Sound.class);
        soundsManager.load("sounds/backgroundbattle.mp3", Sound.class);
        soundsManager.load("sounds/artillery.mp3", Sound.class);
        soundsManager.load("sounds/trucks.mp3", Sound.class);
        soundsManager.load("sounds/combat.mp3", Sound.class);
        soundsManager.load("sounds/movement.mp3", Sound.class);
        soundsManager.load("sounds/bridgeblow.mp3", Sound.class);
        soundsManager.load("sounds/limber.mp3", Sound.class);
        soundsManager.load("sounds/march.mp3", Sound.class);
        soundsManager.load("sounds/stuka.mp3", Sound.class);
        soundsManager.load("sounds/mortar.mp3", Sound.class);
        soundsManager.load("sounds/tada.mp3", Sound.class);
        TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
        param.minFilter = Texture.TextureFilter.Linear;

        cardManager.load("cards/2ndpanzerhalts.jpg", Texture.class, param);
        cardManager.load("cards/blownbridge.jpg", Texture.class,param);
        cardManager.load("cards/fixbridge.jpg", Texture.class,param);
        cardManager.load("cards/fritz1.jpg", Texture.class,param);
        cardManager.load("cards/hooufgas.jpg", Texture.class,param);
        cardManager.load("cards/moreammo.jpg", Texture.class,param);
        cardManager.load("cards/prayforweather.jpg", Texture.class,param);
        cardManager.load("cards/szorney1.jpg", Texture.class,param);
        cardManager.load("cards/szorney2.jpg", Texture.class,param);
        cardManager.load("cards/2ndpanzerloses2units.jpg", Texture.class,param);
        cardManager.load("cards/hilite.png", Texture.class,param);
        cardManager.load("cards/empty.png", Texture.class,param);
        if (Gdx.graphics.getHeight()< 800){
            //font.setOwnsTexture(true);
            //font.getData().setScale(1.2f,1.2f);
            //font.setUseIntegerPositions(true);
        }
        splashImage = new Image(splash);
        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight();
        splashImage.setWidth(x);
        splashImage.setHeight(y);
        stage.addActor(splashImage);
        splashMapImage = new Image(splashmap);
        splashMapImage.setWidth(x);
        splashMapImage.setHeight(y);
        float  duration = 2f;
        stage.addActor(splashMapImage);
        splashMapImage.addAction(Actions.alpha(0));
        splashMapImage.addAction(Actions.fadeIn(3f));
        logo = new Logo(logoAtlas, stage);


    }
    boolean isLoaded = false;
    public void checkLoad(Batch batch) {
        batch.begin();
        float progress = (mapsManager.getProgress() + effectsManager.getProgress() + unitsManager.getProgress() + UIManager.getProgress() + soundsManager.getProgress()+cardManager.getProgress())/5;
        if (!beenHere) {
 /*           Gdx.app.log("SplashScreen", "Progress ="+progress);
            Gdx.app.log("SplashScreen", "mapsManage ="+mapsManager.getProgress());
            Gdx.app.log("SplashScreen", "effectsMan ="+effectsManager.getProgress());
            Gdx.app.log("SplashScreen", "unitMKan   ="+unitsManager.getProgress());
            Gdx.app.log("SplashScreen", "UIManage   ="+UIManager.getProgress());
            Gdx.app.log("SplashScreen", "Sounds     ="+soundsManager.getProgress());
*/
            font.setColor(Color.WHITE);
            font.draw(batch, "Version:" + GamePreferences.getBuildNumber() + " Loading at " + Math.round((progress * 100)-18) + "%", 10,50 );
            logo.update(progress);
            //            music.setVolume(music.getVolume() + 0.01f);
        }else {
 //           font.setColor(Color.WHITE);
 //           font.draw(stateEngine.getBatch(), GamePreferences.getBuildNumber(), 20, 20);
        }
        if (mapsManager.update() && effectsManager.update() && soundsManager.update() && unitsManager.update() && UIManager.update()&& cardManager.update() && !beenHere) {
            map = mapsManager.get("map/ardenne.jpg", Texture.class);
//            Game game = new Game();
            ScreenGame screenGame = new ScreenGame(ardenne.instance.mapStage);
            CenterScreen centerScreen = new CenterScreen();
            Hex.loadHexes();
            Map map = new Map();
            SoundsLoader soundsLoader = new SoundsLoader();
            map.InitializeHexSearch();
            UILoader.instance.assignAssets(UIManager);
            screenHeight= Gdx.graphics.getHeight();
            screenWidth = Gdx.graphics.getWidth();
            gameMenu = new GameMenu(stage, sound,atlas,screenHeight, screenWidth);
            splashMapImage.addAction(Actions.fadeOut(.5f));
 //           gameMenu.addObserver(this);
            languageSupport = new LanguageSupport();
 //           languageSupport.addObserver(this);

            beenHere = true;
        }
        batch.end();
    }
    public Texture getMap(){
        return map;

    }
    public void reStart(){
        stage.addActor(splashImage);
        gameMenu = new GameMenu(stage, sound,atlas,screenHeight, screenWidth);
        System.gc();
    }
    public void end() {
        arrActorsMenu = stage.getActors();
        stage.clear();
        sound.stop();
    }
    public void sounOff(){
        sound.stop();
    }
}


