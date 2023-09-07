package com.bruinbeargames.ardenne;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bruinbeargames.ardenne.AI.AIFaker;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.UI.BridgeExplosion;
import com.bruinbeargames.ardenne.UI.DiceEffect;
import com.bruinbeargames.ardenne.UI.EventAI;
import com.bruinbeargames.ardenne.UI.EventPopUp;
import com.bruinbeargames.ardenne.UI.Explosions;
import com.bruinbeargames.ardenne.UI.FlyingShell;
import com.bruinbeargames.ardenne.UI.WinAIDisplay;
import com.bruinbeargames.ardenne.UI.WinUnitDisplay;
import com.kotcrab.vis.ui.VisUI;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;

import de.golfgl.gdxgameanalytics.GameAnalytics;

public class ardenne extends Observable implements ApplicationListener, GestureDetector.GestureListener, InputProcessor {
	SpriteBatch batch;
	Texture img;
	boolean isWriteTerrain = false;
	public Stage guiStage;
	public  Stage mapStage;
	public  Stage hexStage;
//	public Stage mainmenuStage;
	public ScreenGame screen;
    public FontFactory fontFactory;
    public SplashScreen splashScreen;
//	TNLog tnlog;
	Game game;
	boolean isMainMenu = false;
	public boolean isNoInput = true;
	private boolean isSaveGame = false;
	InputMultiplexer im;
	GestureDetector gd;
	static public ardenne instance;
	MusicGame music;
	ShapeRenderer shapeRenderer;
	SoundEffects soundEffects;
	Map map;
	public boolean isUpdateDice = false;
	public boolean isUpdateExplosion = false;
	public boolean isUpdateShell = false;
	public boolean isBridgeExplosion = false;
	public boolean isScroll = false;
	private I18NBundle i18NBundle;
	private boolean aiRender = false;
	private int cntAiRender =0;

	//
//	Loader loader;

	@Override
	public void create () {
		Gdx.app.log("Create", "Create");
		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int taskBarHeight =0;

		if (GamePreferences.isFullScreen()) {
			Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
			Gdx.graphics.setFullscreenMode(mode);
			Gdx.graphics.setVSync(true);
			//Gdx.graphics.setWindowedMode(1920, 1080);
			int width = Gdx.graphics.getHeight();
			if (width > 1620){
				Gdx.graphics.setWindowedMode(2880,1620);
			}


	} else {
		Gdx.app.log("Bastogne", "Starting Windowed");

		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
			taskBarHeight = scrnSize.height - winSize.height;

			Gdx.graphics.setFullscreenMode(mode);
		//    Gdx.graphics.setWindowedMode((int)GamePreferences.getWindowSize().x, (int)GamePreferences.getWindowSize().y - 75);
		Gdx.graphics.setUndecorated(false);
		Gdx.graphics.setTitle("Bastogne Breakout");
		Gdx.graphics.setResizable(false);
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		if (width > 1920) {
			Gdx.graphics.setWindowedMode(1920, 1080);
		} else {
			Gdx.graphics.setWindowedMode(width, height - (taskBarHeight+ 10));
		}
	}
		instance = this;
/*		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
//		Gdx.graphics.setFullscreenMode(mode);
		Gdx.graphics.setWindowedMode((int)GamePreferences.getWindowSize().x, (int)GamePreferences.getWindowSize().y - 75);
		Gdx.graphics.setUndecorated(false);
		Gdx.graphics.setTitle("Ardennes");
		VisUI.load();
		int y = Gdx.graphics.getHeight();
		int x = Gdx.graphics.getWidth();
		if (x > 1920) {
			Gdx.graphics.setWindowedMode(1920, 1080);
//			Gdx.graphics.setWindowedMode(2880, 1620);
		}else{
			Gdx.graphics.setWindowedMode(x,y);

		}
		//		Gdx.graphics.setResizable(false); */
		guiStage = new Stage(new ScreenViewport());
		GameAnalytics gameAnalytics = new GameAnalytics();
		Analytics analytics;
		analytics = new Analytics(GamePreferences.instance.getBuildNumber());
		if (!GamePreferences.isDEbug) {
			DoRedirectConsole();
			analytics.registerUncaughtExceptionHandler();
		}


		music = new MusicGame();
		guiStage = new Stage(new ScreenViewport());
		mapStage = new Stage(new ScreenViewport());
		hexStage = new Stage(new ScreenViewport());
		gd = new GestureDetector(this);
		batch = new SpriteBatch();
//		Hex.loadHexes();
//		GamePreferences gamePreferences = new GamePreferences();

		CreateInputProcessors();
		GamePreferences gamePreferences = new GamePreferences();
		splashScreen = new SplashScreen();
		i18NBundle = GameMenuLoader.instance.localization;
		VisUI.load();
		//	VisUI.load(VisUI.SkinScale.X2);
		VisUI.setDefaultTitleAlign(Align.center);



//		WinDebug winDebug = new WinDebug(mapStage,guiStage, skin);

	}

	private void CreateInputProcessors() {
		/**
		 *  no hex stage for now
		 */
		im = new InputMultiplexer(this);
		im.addProcessor(1, guiStage);
		im.addProcessor(2, mapStage);
		im.addProcessor(3,hexStage);
		im.addProcessor(4, gd);
		Gdx.input.setInputProcessor(im);


	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (ScreenGame.instance != null) {
			batch.setProjectionMatrix(ScreenGame.instance.cameraBackGround.combined);
			batch.begin();
			ScreenGame.instance.render(batch);
			batch.end();
		}
		hexStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		hexStage.draw(); // make sure done after sprite batch end
		mapStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		mapStage.draw(); // make sure done after sprite batch end
		guiStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		guiStage.draw(); // make sure done after sprite batch end
		if (aiRender){
			cntAiRender++;
			if (cntAiRender > 15) {
				EventAI.instance.tick();
				cntAiRender =0;
			}
		}
		if (ScreenGame.instance != null) {
			if (isUpdateExplosion || isScroll || isUpdateDice || isBridgeExplosion || isUpdateShell) {
				batch.setProjectionMatrix(ScreenGame.instance.cameraBackGround.combined);
				batch.begin();
				if (isUpdateExplosion) {
					Explosions.instance.update(batch);
				}
				if (isUpdateDice) { // changes viewpoint
					DiceEffect.instance.update(batch);
				}
				if (isBridgeExplosion) {
					BridgeExplosion.instance.update(batch);
				}
				if (isScroll) {
					CenterScreen.instance.update();
				}
				if (isUpdateShell){
					FlyingShell.instance.update();
				}

				batch.end();
			}
		}

		SplashScreen.instance.checkLoad(batch);
	}

	@Override
	public void dispose () {
		Gdx.app.log("ardenne", "dispoase");

		if (AIFaker.instance != null){
			AIFaker.instance.killFaker();
		}
		batch.dispose();
		if (img != null){
			img.dispose();
		}
		if (FontFactory.instance != null) {
			FontFactory.instance.dispose();
		}
		VisUI.dispose();
	}
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (isSaveGame){
			return false;
		}
		if (keycode == Input.Keys.LEFT){
			pan(0,0,100,0);
		}
		if (keycode == Input.Keys.RIGHT){
			pan(0,0,-100,00);
		}
		if (keycode == Input.Keys.UP){
			pan(0,0,0,100);
		}
		if (keycode == Input.Keys.DOWN){
			pan(0,0,0,-100);
		}
		if (keycode == Input.Keys.P) {
			TakeScreenShot.saveScreenShot();
		}
		if (keycode == Input.Keys.Z) {
			WinUnitDisplay winUnitDisplay = new WinUnitDisplay();
		}
		if (keycode == Input.Keys.PLUS) {
			ScreenGame.instance.ZoomBigger();
		}
		if (keycode == Input.Keys.MINUS) {
			ScreenGame.instance.ZoomSmaller();
		}

		if (keycode == Input.Keys.T) {
			if (NextPhase.instance != null){
			EventPopUp.instance.show(i18NBundle.format("turnshow",NextPhase.instance.getTurn(),GameSetup.instance.getScenario().getLength()));
			}
		}
		if (keycode == Input.Keys.M) {
			if (isShowMovePoints){
				isShowMovePoints = false;
			}else{
				isShowMovePoints = true;
			}
			TakeScreenShot.saveScreenShot();
		}
		if (keycode == Input.Keys.CONTROL_LEFT){
			if (WinAIDisplay.instance != null) {
				WinAIDisplay.instance.show();
			}
		}
		if ((keycode == Input.Keys.ALT_LEFT)) {
				isSetHotSeat = true;
		}
		return false;
	}
	boolean isSetHotSeat = false;
	boolean isShowMovePoints = false;
	public boolean getisShowMovepoints(){
		return isShowMovePoints;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		Gdx.app.log("Mouse Event", "TouchUp at " + screenX+ " y-"+screenY);
		if (isPan){
			isPan = false;
			return true;
		}
		Hex hex = null;
		if (Hex.hexTable != null) {
			hex = Hex.GetHexFromScreenPosition(screenX, screenY);
//			Gdx.app.log("Mouse Event", "Hex clicked=" + hex.xTable+ " y-"+hex.yTable);
			if (hex != null) {
				fireHex(hex,button,screenX,screenY);
			}
		}
		if (isWriteTerrain) {
			TerrainWriter(hex, button);
		}

		return false;
	}
	boolean isDragged = false;
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		Gdx.app.log("Mouse Event", "Touch Dragged");
		// TODO Auto-generated method stub
		isDragged = true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		Gdx.app.log("Mouse Event", "Scrolled special x="+amountX);
		Gdx.app.log("Mouse Event", "Scrolled special y="+amountY);

		if (isMainMenu) {
			return false;
		}
		if (ScreenGame.instance == null){
			return false;
		}
		if (amountY < 0) {

			ScreenGame.instance.ZoomSmaller();
		} else if (amountY > 0) {
			ScreenGame.instance.ZoomBigger();
		}
		return false;
	}

	public boolean scrolled(int amount) {
		Gdx.app.log("Mouse Event", "Scrolled");

		if (isMainMenu) {
			return false;
		}
		if (ScreenGame.instance == null){
			return false;
		}
		if (amount < 0) {

			ScreenGame.instance.ZoomSmaller();
		} else if (amount > 0) {
			ScreenGame.instance.ZoomBigger();
		}
		return false;

	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.log("Mouse Event", "Click at " + x+ " y-"+y);


		return false;
	}
	boolean isFirstTime = true;
	FileHandle logger;
	StringBuffer logBuffer;
	int cnt=0;
	private void TerrainWriter(Hex hex, int button)
	{
		Gdx.app.log("TerrainWriter", "Hex =" + hex.xTable+ " y-"+hex.yTable);
		if (isFirstTime)
		{
			logger = Gdx.files.local("AI" +
					"path");
			logger.writeString("static int[][] path"+cnt+"=", false);
			isFirstTime = false;
			cnt++;
		}

		if (button == Input.Buttons.RIGHT){
			logger.writeString("}static int[][] path"+cnt+"=", true);
			cnt++;

		}
		String str1;
		String str2;
		if (hex.xTable < 10 ){
			str1=  String.format("%01d", hex.xTable);
		}else{
			str1=  String.format("%02d", hex.xTable);
		}
		if (hex.yTable < 10 ){
			str2=  String.format("%01d", hex.yTable);
		}else{
			str2=  String.format("%02d", hex.yTable);
		}
		logger.writeString("{"+str1+","+str2+"},", true);
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	static boolean isPan =false;
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
	//	Gdx.app.log("Ardenne","Pan"+" x="+x+" y="+y+" deltaX="+deltaX+" deltay="+deltaY);
		if (CenterScreen.instance != null){
			if (CenterScreen.instance.isScrolling()){
				return false;
			}
		}
        if (ScreenGame.instance != null) {
            ScreenGame.instance.panCamera(deltaX, deltaY);
        }
        isPan = true;
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		Gdx.app.log("Zoom", "Initial=" + initialDistance);

		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		Gdx.app.log("pinch", "Initial=");

		return false;
	}

	@Override
	public void pinchStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
//		guiStage.getViewport().update(width, height, true);
//		mapStage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
	public void fireHex(Hex hex, int button, int screenX, int screenY) {
	/**
	 * this needs to be reworked as dragging happens all the time
	 * and so we need to hit The MOA icon 2 times
		if (isHandleHexFire && isDragged){
			isHandleHexFire = false;
			isDragged = false;
			return;
		} */
		if (hex != null) {
			if (hex != null) {

				Gdx.app.log("ardenne", "Hex Fired hex="+hex);
			}
			if (button == Input.Buttons.LEFT) {
				setChanged();
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
					notifyObservers(new ObserverPackage(ObserverPackage.Type.TouchUpShift, hex,screenX,screenY));
				} else {
					notifyObservers(new ObserverPackage(ObserverPackage.Type.TouchUp, hex,screenX,screenY));
				}
			}
			if (button == Input.Buttons.MIDDLE) {
				setChanged();
				notifyObservers(new ObserverPackage(ObserverPackage.Type.TouchUpMiddle, hex,screenX,screenY));
			}
			setChanged();
			//return true;
		}

	}

	boolean isHandleHexFire = false;


    public void setAIRender(boolean b) {
    	aiRender = b;
    	cntAiRender = 0;
    }
	public void setSaveGame(){
		isSaveGame = true;
	}
	public void setSaveGameOver(){
		isSaveGame = false;
	}
	private static void DoRedirectConsole() {
		// This pipes everything from stdout/stdout into output file, for debugging.
		PrintStream printStream = null;

		PrintStream originalOut = System.out;
		PrintStream originalErr = System.err;

		try {
			printStream = new PrintStream(new FileOutputStream("debuglog.txt"), true, "UTF-8");
			System.setOut(printStream);
			System.setErr(printStream);

		} catch (Exception e) {

			e.printStackTrace();


			// If there are problems, close it and go back to console output
			if (printStream != null) {
				printStream.flush();
				printStream.close();
			}
			System.setOut(originalOut);
			System.setErr(originalErr);
		}


	}
}
