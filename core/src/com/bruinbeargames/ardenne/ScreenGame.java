package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ScreenGame {
    protected float zoom = 1.4f; // level of screen size
    private float oldZoom;
    final float zoomMin = .7f;

    private float zoomChange = .1F;
    //	private float zoomChangedDesktop = .05f;
    boolean hasZoomed = false;
    public OrthographicCamera cameraBackGround; // camera for the background
    float scrollMaxX;
    float scrollMaxY;

    final float minHexesOnScreen = 5;
    protected Viewport view;
    public Texture texture;
    public float screenSize; // in Inches
    protected Sprite backSprite;
    protected float smallHeight;
    protected float smallWidth;
    public float backGroundTextureHeight;
    public float backGroundTextureWidth;
    /**
     * HexTexture is the values of The hexes on the texture
     */
    public float hexTextureWidth = 4014F;
    public float hexTextureHeight = 3697F;
    protected float backGroundTextureRatio;

    public static ScreenGame instance;
    Stage mapStage;
    Stage hexStage;
    boolean isMinZoom = false;
    boolean isPanAllowed = true;
    float screenWidth;
    float screenHeight;
    float screenRatio;
    final float heightTopMessage = 50;
    final float heightBottomMessage = 50;
    float xAdjustSprite = 0;
    float yAdjustSprite = 0;
    private float zoomMax;

    ///
    public ScreenGame(Stage inStage)
    {
        Gdx.app.log("ScreenGame", "Constructor");
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            zoomChange = .05f;
        }
        instance = this;
        mapStage = ardenne.instance.mapStage;
        hexStage = ardenne.instance.hexStage;

        texture = SplashScreen.instance.getMap();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backSprite = new Sprite(texture);
//		zoom = CalculateZoomParms();
        zoom =1.6F;
        Resize();

    }

    /**
     * Fit the background into screen This the max we can zoom otherwise we get bars
     *
     * @return
     */
    private float CalculateZoomParms()
    {
        // TODO Auto-generated method stub
        // add 10% for message on top
        // add 10% for message on Bottom
        backGroundTextureHeight = backSprite.getHeight();
        backGroundTextureWidth = backSprite.getWidth();
        float maxZoomHeight = backGroundTextureHeight/screenHeight;

        float maxZoomWidth = backGroundTextureWidth/screenWidth;
        if (maxZoomHeight > maxZoomWidth)
        {
            zoomMax = maxZoomWidth;
        }
        else
        {
            zoomMax = maxZoomHeight;
        }
        return zoomMax;
    }

    /**
     * Render the scene
     */

    public void render(SpriteBatch batch)
    {
        //       batch.setProjectionMatrix(cameraBackGround.combined);
        //       batch.begin();
        backSprite.draw(batch);
        //       batch.end();
    }

    /**
     * REsize trigger happened
     */
    public void Resize()
    {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
//		float dpi = Gdx.graphics.getDensity(); // 160 dpi = 1, 120 = .75
//		float ppi = Gdx.graphics.getPpiX();
//		screenSize = (dpi * 160); // not used at present
//		Gdx.app.log("ScreenGame", "dpi =" + screenSize);
//		Gdx.app.log("Graphics", "Resize");
        Gdx.app.log("Graphics", "Display Height = " + String.valueOf(height));
        Gdx.app.log("Graphics", "Display Width = " + String.valueOf(width));
        screenWidth = width;
        screenHeight = height;
//
        cameraBackGround = new OrthographicCamera(screenWidth, screenHeight);

        CalculateZoomParms();
        backSprite.setOrigin(0, 0);
        Gdx.app.log("Graphics", "Sprite Width = " + String.valueOf(backSprite.getWidth()));
        Gdx.app.log("Graphics", "Sprite Height = " + String.valueOf(backSprite.getHeight()));
        backSprite.setSize(backGroundTextureWidth, backGroundTextureHeight);
        float w = texture.getWidth();
        float h = texture.getHeight();
        float[] workFloat = GetSmallSize(w, h);
        smallWidth = workFloat[0];
        smallHeight = workFloat[1];
        if ((screenWidth * zoom) > backGroundTextureWidth
                && (screenHeight * zoom)  > backGroundTextureHeight)
        {
            Gdx.app.log("ScreenGame", "Set to Small ScreenGame");
            zoom *= 1.1F;
            isMinZoom = true;
            isPanAllowed = false;
            // do not allow zoom at
            // if (isLastBeforeMin)
            // {
            // isMinZoom = true;
            // isLastBeforeMin = false;
            // isPanAllowed = false;
            // }
            // else
            // {
            // isLastBeforeMin = true;
            // isPanAllowed = false;
            //
            // }
        } else
        {
            isPanAllowed = true;
        }
        // camera = new OrthographicCamera(smallWidth, smallHeight);

        // if (hasZoomed) // dont want to zoom
        // {
        // zoom = oldZoom;
        // ((OrthographicCamera)mapStage.getCamera()).zoom = zoom;
        // ((OrthographicCamera)mapStage.getCamera()).update();
        // ((OrthographicCamera)mapStage.getCamera()).position.x = camera.position.x;
        // ((OrthographicCamera)mapStage.getCamera()).position.y = camera.position.y;
        // ((OrthographicCamera)mapStage.getCamera()).update();

        // }
        // }
        // else
        // {
        Gdx.app.log("ScreenGame", "Zoom=" + zoom);

        cameraBackGround.zoom = zoom;
        cameraBackGround.update();
        // Do the Zoom for UI Map
        ((OrthographicCamera) mapStage.getCamera()).zoom = zoom;
        ((OrthographicCamera) mapStage.getCamera()).update();
        ((OrthographicCamera) mapStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) mapStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) mapStage.getCamera()).update();
        ((OrthographicCamera) hexStage.getCamera()).zoom = zoom;
        ((OrthographicCamera) hexStage.getCamera()).update();
        ((OrthographicCamera) hexStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) hexStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) hexStage.getCamera()).update();

        scrollMaxX = (backGroundTextureWidth - (screenWidth * zoom)) / 2;
        scrollMaxY = (backGroundTextureHeight - (screenHeight * zoom)) / 2;
        // }
        backSprite.setPosition(-((backSprite.getWidth() / 2) + xAdjustSprite),
                -((backSprite.getHeight() / 2) + yAdjustSprite));
        hasZoomed = false;

    }
    public OrthographicCamera getCamera(){
        return cameraBackGround;
    }

    /**
     * Pan ScreenGame by adjusting Camera
     *
     * @param deltaX
     * @param deltaY
     */
    float totPanX = 0;
    float totPanY = 0;

    public boolean[] panCamera(float deltaX, float deltaY)
    {
        // Gdx.app.log("ScreenGame", "PanCamera deltaX =" + deltaX + " y=" + deltaY);
        // Gdx.app.log("ScreenGame", "PanCamera positionX =" + camera.position.x + " y=" +
        // camera.position.y);
        // Gdx.app.log("ScreenGame", "zoom =" + camera.zoom);

        if (!isPanAllowed)
        {
            Gdx.app.log("ScreenGame", "PanCamera Not allowed");
            boolean[] hasPanned =  new boolean[2];
            return hasPanned;
        }

        cameraBackGround.position.add(-deltaX * cameraBackGround.zoom, deltaY * cameraBackGround.zoom, 0);
        boolean[] hasPanned =  CheckOverPan();
        return hasPanned;

    }

    private boolean[] CheckOverPan()
    {
        boolean isXOver = false;
        boolean isYOver = false;
        float negMaxX = scrollMaxX ;
        // float negMaxX = scrollMaxX + ((borderLength * 4)* zoom/2);
        float maxX = scrollMaxX;
        float maxY = scrollMaxY;
        float negMaxY = scrollMaxY;

        if (cameraBackGround.position.x < 0F && cameraBackGround.position.x < -negMaxX)
        {
            cameraBackGround.position.x = -negMaxX;
            isXOver = true;
        }
        if (cameraBackGround.position.y < 0F && cameraBackGround.position.y < -negMaxY)
        {
            cameraBackGround.position.y = -negMaxY;
            isYOver = true;
        }
        if (cameraBackGround.position.x >= 0F && cameraBackGround.position.x > maxX)
        {
            cameraBackGround.position.x = maxX;
            isXOver = true;
        }
        if (cameraBackGround.position.y >= 0F && cameraBackGround.position.y > maxY)
        {
            cameraBackGround.position.y = maxY;
            isYOver = true;
        }
        cameraBackGround.update();
        ((OrthographicCamera) mapStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) mapStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) mapStage.getCamera()).update();
        ((OrthographicCamera) hexStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) hexStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) hexStage.getCamera()).update();
        boolean[] bReturn = new boolean[2];
        bReturn[0]=isXOver;
        bReturn[1] = isYOver;
        return bReturn;

    }

    public void TextureDispose()
    {
        // TODO Auto-generated method stub
        texture.dispose();
    }

    /**
     * For present screen get camera size that will allow no distortion for small
     *
     * @param w
     *            texture width
     * @param h
     *            texture height
     * @return float[0] camera width float[1] camera height
     */
    public static float[] GetSmallSize(float w, Float h)
    {
        float[] retFloat = new float[2];
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float ratioScreen = width / height;
        float ratioTexture = w / h;
        if (ratioTexture > ratioScreen)
        {
            Gdx.app.log("Graphics", "Texture greate");
            retFloat[0] = w;
            retFloat[1] = w / ratioScreen;
        } else
        {
            retFloat[1] = h;
            retFloat[0] = ratioScreen * h;
        }
        return retFloat;
    }

    boolean isZoom = false;

    public void ZoomBigger()
    {
        Gdx.app.log("ScreenGame", "Zoom Bigger New");
        // SetLastHex();
        zoom -= zoomChange;
        if (zoom < zoomMin)
        {
            zoom = zoomMin;
        }
        Zoom();
    }
    public void ZoomSmaller()
    {
        Gdx.app.log("ScreenGame", "Zoom Smaller zoom New=" + zoom);

        zoom += zoomChange;
        if (zoom > zoomMax)
        {
            zoom = zoomMax;
        }
        Zoom();
        // Resize();

    }


    private void Zoom()
    {
        if ((screenWidth * zoom) > backGroundTextureWidth
                && (screenHeight * zoom) > backGroundTextureHeight)
        {
            Gdx.app.log("Zoom", "Set to Small ScreenGame");
            zoom *= 1.1F;
            isMinZoom = true;
            isPanAllowed = false;
            // do not allow zoom at
            // if (isLastBeforeMin)
            // {
            // isMinZoom = true;
            // isLastBeforeMin = false;
            // isPanAllowed = false;
            // }
            // else
            // {
            // isLastBeforeMin = true;
            // isPanAllowed = false;
            //
            // }
        } else
        {
            isPanAllowed = true;
        }

        cameraBackGround.zoom = zoom;
        cameraBackGround.update();
        ((OrthographicCamera) mapStage.getCamera()).zoom = zoom;
        ((OrthographicCamera) mapStage.getCamera()).update();
        ((OrthographicCamera) mapStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) mapStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) mapStage.getCamera()).update();
        ((OrthographicCamera) hexStage.getCamera()).zoom = zoom;
        ((OrthographicCamera) hexStage.getCamera()).update();
        ((OrthographicCamera) hexStage.getCamera()).position.x = cameraBackGround.position.x;
        ((OrthographicCamera) hexStage.getCamera()).position.y = cameraBackGround.position.y;
        ((OrthographicCamera) hexStage.getCamera()).update();
        scrollMaxX = (backGroundTextureWidth - (screenWidth * zoom)) / 2;
        scrollMaxY = (backGroundTextureHeight - (screenHeight * zoom)) / 2;
        CheckOverPan();

    }


    public Vector2 GetBackGroundSize()
    {
        Vector2 v2Return = new Vector2(backGroundTextureWidth, backGroundTextureHeight);
        return v2Return;
    }



    public Camera GetCamera()
    {
        // TODO Auto-generated method stub
        return cameraBackGround;
    }

    /**
     *  How much of the screen can we use for game display
     * @return rectangle of usabe  space
     */
    public Rectangle GetUsableScreenRectangle()
    {
        final float screenPercent = .7F;
        float xUsable = screenWidth * screenPercent;
        float yUsable = screenHeight * screenPercent;
        float startX =  (screenWidth - xUsable)/2;
        float starty = (screenHeight - yUsable) /2;
        Rectangle rectangle = new Rectangle(startX, starty, xUsable, yUsable);
        return rectangle;
    }



}