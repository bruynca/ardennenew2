package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.bruinbeargames.ardenne.GameLogic.Barrage;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.ScreenGame;
import com.bruinbeargames.ardenne.Unit.Unit;
import com.bruinbeargames.ardenne.ardenne;


public class Explosions {
    public static Explosions instance;
    // Constant rows and columns of the sprite sheet
    private static final int FRAME_COLS = 8, FRAME_ROWS = 6;
    private Vector2 position;
    // A variable for tracking elapsed time for the animation
    float stateTime;
    private Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
    private Texture walkSheet;
    private TextureRegion[] walkFrames;
    private boolean started;
    private Matrix4 matrix4;
    Barrage.TargetShooterSave targetShooterSave;

    public Explosions() {
/*        ScriptEngineManager mgr = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = mgr.getEngineFactories();
        for (ScriptEngineFactory factory : factories)
        {
            String engName = factory.getEngineName();
            String engVersion = factory.getEngineVersion();
            String langName = factory.getLanguageName();
            String langVersion = factory.getLanguageVersion();
            System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
            List<String> engNames = factory.getNames();
        }*/

        instance = this;
        // Load the sprite sheet as a Texture
        walkSheet = new Texture(Gdx.files.internal("effects/explosion.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / FRAME_COLS,
                walkSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        position = new Vector2();
        position.x = 500;
        position.y = 500;
        matrix4 = ((OrthographicCamera) ardenne.instance.mapStage.getCamera()).combined;


    }

    public void



    setPosition (Barrage.TargetShooterSave targetShooterSave){
        this.targetShooterSave = targetShooterSave;
        this.position =          targetShooterSave.hexTarget.getCounterPosition();
        this.position.x = this.position.x - 50;
        this.position.y -= 50;
 //       if (!Map.onScreen(targetShooterSave.hexTarget)){
 //           CenterScreen.instance.start(targetShooterSave.hexTarget);
 //       }
    }


    public float start(float speed) {
        // Initialize the Animation with the frame interval and array of frames
        walkAnimation = new Animation<TextureRegion>(speed, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        stateTime = 0f;
        started = true;
        SoundsLoader.instance.playArtillerySound();
        ardenne.instance.isUpdateExplosion = true;
        int i=0;
        for (Unit unit:targetShooterSave.hexTarget.getUnitsInHex()){

            if (i==0) {
                unit.getMapCounter().getCounterStack().removeBack();
                i++;
            }else{
                unit.getMapCounter().getCounterStack().getStack().setVisible(false);
            }
        }
        return walkAnimation.getAnimationDuration();


    }

    public void update(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        batch.setProjectionMatrix(ScreenGame.instance.cameraBackGround.combined);

        if (started) {

            stateTime += delta; // Accumulate elapsed animation time


            // Get current frame of animation for the current stateTime
            TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, false);
            if (walkAnimation.isAnimationFinished(stateTime)){
                started = false;
                int i=0;
                for (Unit unit:targetShooterSave.hexTarget.getUnitsInHex()){
                    if (i==0) {
                        unit.getMapCounter().getCounterStack().addBack();
                        i++;
                    }else{
                        unit.getMapCounter().getCounterStack().getStack().setVisible(true);
                    }
                }

                targetShooterSave.showResult();
            }

            batch.draw(currentFrame,
                    (position.x),
                    (position.y),
                    0,
                    0,
                    currentFrame.getRegionWidth(),
                    currentFrame.getRegionHeight(),
                    1,
                    1,
                    0);

        }

    }
}

