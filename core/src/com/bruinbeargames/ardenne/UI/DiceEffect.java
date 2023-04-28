package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.bruinbeargames.ardenne.GameLogic.Attack;
import com.bruinbeargames.ardenne.GameLogic.SoundsLoader;
import com.bruinbeargames.ardenne.ObserverPackage;
import com.bruinbeargames.ardenne.SplashScreen;
import com.bruinbeargames.ardenne.ardenne;

import java.util.Observable;

public class
DiceEffect extends Observable {

    // Constant rows and columns of the sprite sheet
    private static final int FRAME_COLS = 5, FRAME_ROWS = 3;

    // Objects used
    private Animation<TextureRegion> walkRedAnimation; // Must declare frame type (TextureRegion)
    private final TextureRegion[] walkRedFrames;
    private TextureRegion redDiceFront;
    private Animation<TextureRegion> walkBlueAnimation; // Must declare frame type (TextureRegion)
    private final TextureRegion[] walkBlueFrames;
    private TextureRegion blueDiceFront;
    private static final float MOVEMENT = 30f;
    private boolean blueDiceRoll;
    private boolean redDiceRoll;
    private boolean blueDiceRollHit;
    private boolean redDiceRollHit;
    private boolean notifiedBlueDieRollFinished;
    private boolean notifiedRedDieRollFinished;
    private boolean finishedRedDiceRolling = false;
    private boolean finishedBlueDiceRolling = false;
    private final float animationTime = .06f;
    private boolean notified = false;
    private float resetDice = 2f;
    private float startX = 95f;
    private float startY = 110f; // was 110
    private Matrix4 matrix4;

    // A variable for tracking elapsed time for the animation
    private float stateTimeRedDice;
    private float stateTimeBlueDice;
    public static DiceEffect instance;
    static TextureAtlas tDiceRed = SplashScreen.instance.effectsManager.get("effects/dicefronts.txt");
    static TextureAtlas tDiceBlue = SplashScreen.instance.effectsManager.get("effects/dicefrontsblue.txt");
    Attack attack;
    public DiceEffect() {


        instance = this;

        // Load the sprite sheet as a Texture
        Texture walkRedSheet = new Texture(Gdx.files.internal("effects/dicerolling.png"));
        Texture walkBlueSheet = new Texture(Gdx.files.internal("effects/dicerollingblue.png"));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(walkRedSheet,
                walkRedSheet.getWidth() / FRAME_COLS,
                walkRedSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        walkRedFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkRedFrames[index++] = tmp[i][j];
            }
        }

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        tmp = TextureRegion.split(walkBlueSheet,
                walkBlueSheet.getWidth() / FRAME_COLS,
                walkBlueSheet.getHeight() / FRAME_ROWS);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        walkBlueFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkBlueFrames[index++] = tmp[i][j];
            }
        }
        matrix4 = ((OrthographicCamera) ardenne.instance.guiStage.getCamera()).combined;

        ardenne.instance.isUpdateDice = true;
    }

    public void rollRedDice(int finalNumber) {
        notifiedRedDieRollFinished = false;

        redDiceRoll = true;
        walkRedAnimation = new Animation<>(animationTime, walkRedFrames);
        walkRedAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        switch (finalNumber) {
            case 1:
                redDiceFront = tDiceRed.findRegion("one");
                break;
            case 2:
                redDiceFront = tDiceRed.findRegion("two");
                break;
            case 3:
                redDiceFront = tDiceRed.findRegion("three");
                break;
            case 4:
                redDiceFront = tDiceRed.findRegion("four");
                break;
            case 5:
                redDiceFront = tDiceRed.findRegion("five");
                break;
            case 6:
                redDiceFront = tDiceRed.findRegion("six");
                break;
        }

        // time to 0
        stateTimeRedDice = .2f;
        SoundsLoader.instance.playDiceRollingSound();

    }


    public void rollBlueDice(int finalNumber) {
        notifiedBlueDieRollFinished = false;
        blueDiceRoll = true;
        walkBlueAnimation = new Animation<>(animationTime, walkBlueFrames);
        walkBlueAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        switch (finalNumber) {
            
            case 1:
                blueDiceFront = tDiceBlue.findRegion("one");
                break;
            case 2:
                blueDiceFront = tDiceBlue.findRegion("two");
                break;
            case 3:
                blueDiceFront = tDiceBlue.findRegion("three");
                break;
            case 4:
                blueDiceFront = tDiceBlue.findRegion("four");
                break;
            case 5:
                blueDiceFront = tDiceBlue.findRegion("five");
                break;
            case 6:
                blueDiceFront = tDiceBlue.findRegion("six");
                break;
        }

        // time to 0
        stateTimeBlueDice = .1f;
    }

    public void update(Batch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        batch.setProjectionMatrix(matrix4);

        if (notified && resetDice > 0){
            resetDice -= delta;
        }else if (notified){
            resetState();
            setChanged();
        }

        if (redDiceRoll) {
            stateTimeRedDice += delta; // Accumulate elapsed animation time

            if (stateTimeRedDice < (15 * animationTime)) {
                // Get current frame of animation for the current stateTime
                TextureRegion currentFrame = walkRedAnimation.getKeyFrame(stateTimeRedDice, false);
                batch.draw(currentFrame,
                        startX/ 1 + (MOVEMENT * walkRedAnimation.getKeyFrameIndex(stateTimeRedDice)),
                        startY/ 1,
                        0,
                        0,
                        currentFrame.getRegionWidth(),currentFrame.getRegionHeight(),
                        1,
                        1,
                        0);
            }
            if (stateTimeRedDice > (15 * animationTime)) {
                batch.draw(redDiceFront,
                        startX/ 1 + (MOVEMENT * 16),
                        startY/ 1,
                        0,
                        0,
                        redDiceFront.getRegionWidth(),
                        redDiceFront.getRegionHeight(),
                        1,
                        1,
                        0);
                if (!notifiedRedDieRollFinished) {
                    finishedRedDiceRolling = true;
                }
            }
        }

        if (blueDiceRoll) {
            stateTimeBlueDice += delta;
            if (stateTimeBlueDice < (15 * animationTime)) {
                // Get current frame of animation for the current stateTime
                TextureRegion currentFrame = walkBlueAnimation.getKeyFrame(stateTimeBlueDice, false);
                batch.draw(currentFrame,
                        (startX+100)/ 1 + (MOVEMENT * walkBlueAnimation.getKeyFrameIndex(stateTimeBlueDice)),
                        startY/ 1,
                        0,
                        0,
                        currentFrame.getRegionWidth(),currentFrame.getRegionHeight(),
                        1,
                        1,
                        0);
            }
            if (stateTimeBlueDice > (15 * animationTime)) {
                batch.draw(blueDiceFront,
                        (startX+100)/ 1 + (MOVEMENT * 16),
                        startY/ 1,
                        0,
                        0,
                        blueDiceFront.getRegionWidth(),
                        blueDiceFront.getRegionHeight(),
                        1,
                        1,
                        0);
                if (!notifiedBlueDieRollFinished) {
                    if (!notified) {
                        finishedBlueDiceRolling = true;
                    }
                }
            }
        }


        if (finishedRedDiceRolling){
            notifiedRedDieRollFinished = true;

        }if (finishedBlueDiceRolling){
            notifiedBlueDieRollFinished = true;

        }
        if ((finishedBlueDiceRolling || finishedRedDiceRolling) && !notified){

            blueDiceRollHit = false;
            redDiceRollHit = false;
            notified = true;
            finishedRedDiceRolling = false;
            finishedBlueDiceRolling = false;
            resetDice = 1f;
            setChanged();
            notifyObservers(new ObserverPackage(ObserverPackage.Type.DiceRollFinished, null,0,0));
        }
    }

    private void resetState() {
        redDiceRoll = false;
        blueDiceRoll = false;
        stateTimeRedDice  = 0;
        stateTimeBlueDice  = 0;
        blueDiceRollHit = false;
        redDiceRollHit = false;
        notified = false;
    }

}


