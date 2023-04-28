package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bruinbeargames.ardenne.Hex.Hex;

public class Map {

  /**
   * These fileds were used to draw hexes on screen not used anymore
   */
  // Stage stage;
  Polygon polycheck[][] = new Polygon[Hex.xEnd][Hex.yEnd];
  Image image;

  static private float yZeroHex = -2; // row zero top side y
  static private float xZeroHex = 53; // row zero top side x
  static private float oddstartLeft = 3; // row zero middle x
  static private float evenStartTop = 86; // row 1 top side y
  static private float hexHeight = 176.78F; // from top to bottom hex
  static private float hexHeightHalf = 88.4F; // from top to pixel before next
  static private float hexLengthLarge = 203F; // hex length on x axis large
  static private float hexLengthSmall = 102F; // hex length
  static private float hexlenght3 = 153f; // x length from start of odd x to end of even x
  static Polygon[][] hexSearchArray = new Polygon[Hex.xEnd][Hex.yEnd];
  static private int xLimit = Hex.xEnd;
  static private int yLimit = Hex.yEnd;
  static public int xHalfScreenSize = 3174;
  static public int yHalfScreenSize = 2254;
  // difference in ratio between screen sizes for PC vs Android
  static public float diffUser = 1;
  static public float diffUserI = 1;
  static public Map instance;

  public Map()
  {
    instance = this;
    // CreateImage();
  }

  /**
   * Initialize the polygon array which contains screen pixel values for each hex
   * no parameters
   */

  static public void InitializeHexSearch()
  {
    //
    // 2 3
    // 1 4
    // 6 5
    //
    //
    //
    // ScreenGame size is assumed to be 3000 1656 for PC This what we use at ratio 1
    // For Android is 2048 1130 at special ratio to match the polygon
    //
    switch (Gdx.app.getType())
    {
      case Desktop:
        diffUser = 1F;
        // texture = new Texture(Gdx.files.internal("Guad.jpg"));
        xHalfScreenSize = (int) (ScreenGame.instance.backGroundTextureWidth / 2);
        yHalfScreenSize = (int) (ScreenGame.instance.backGroundTextureHeight / 2);

        // desktop specific code
        break;
      case Android:
        // android specific code =1f;
        // diffUser = 1F;
        // diffUser = diffScreenSize;
        // diffUserI = 1/diffScreenSize;
        xHalfScreenSize = (int) (ScreenGame.instance.backGroundTextureWidth / 2);
        yHalfScreenSize = (int) (ScreenGame.instance.backGroundTextureHeight / 2);

        // xHalfScreenSize = 1024;
        // yHalfScreenSize = 565;
        break;
      case iOS:
        // iOS specific code
        break;
      case HeadlessDesktop:
        // Headless desktop specific code
        break;
      case Applet:
        // Applet specific code
        break;
      default:
        // Other platforms specific code
    }

    // Build Polygon array for PC ScreenGame
    float[] xInitHex = new float[]
            { oddstartLeft, xZeroHex, xZeroHex + hexLengthSmall, oddstartLeft + hexLengthLarge, xZeroHex + hexLengthSmall,
                    xZeroHex };
    float[] yInitHexOdd = new float[]
            { yZeroHex + hexHeightHalf, yZeroHex, yZeroHex, yZeroHex + hexHeightHalf, yZeroHex + hexHeight,
                    yZeroHex + hexHeight };
    float[] yInitHexEven = new float[]
            { evenStartTop + hexHeightHalf, evenStartTop, evenStartTop, evenStartTop + hexHeightHalf,
                    evenStartTop + hexHeight, evenStartTop + hexHeight };
    int xEnd = Hex.xEnd;
    int yEnd = Hex.yEnd;
    int x, y;
    float[] xWork = (float[]) xInitHex.clone();
    float[] yWork;
    boolean odd = true;
    for (x = 0; x < xEnd; x++) // assume x=0 is idd because it is really x=1
    {
      if (odd)
      {
        yWork = (float[]) yInitHexOdd.clone();
        odd = false;
      } else
      {
        yWork = (float[]) yInitHexEven.clone();
        odd = true;
      }

      for (y = 0; y < yEnd; y++)
      {
        float[] vertices = new float[12];
        for (int i = 0; i < 6; i++)
        {
          vertices[i * 2] = xWork[i];
          vertices[i * 2 + 1] = yWork[i];
        }
        // hexSearchArray[x][y] = new Polygon(xWork, yWork, 6);
        Polygon p = new Polygon(vertices);
        hexSearchArray[x][y] = p;
        for (int j = 0; j < 6; j++)
        {
          yWork[j] += hexHeight;
        }
      }
      for (int j = 0; j < 6; j++)
      {
        xWork[j] += hexlenght3;
      }

    }
    CalculateXWidthInPixels();
  }

  /**
   *
   *
   * poly[2,3] poly[4,5]
   *
   * poly[0,1] poly[6,7]
   *
   * poly[10,11] poly[8,9]
   *
   *
   *
   */
  private static int CalculateXWidthInPixels()
  {
    Polygon poly = hexSearchArray[1][1];
    float[] verts = poly.getVertices();
    /**
     * Calculate width till next hex start
     */
    int width = (int) (verts[4] - verts[0]);
    return width;
  }

  public static Vector2 ConvertToHex(Vector2 inBack)
  {
    Vector2 retPoint = new Vector2(0, 0);
    boolean getOut = false;
    // Gdx.app.log("Hex Search","Projected at " + inBack.x + "," + inBack.y); //
    // pixels of bitmap center is 0,0

    for (int x = 0; x < xLimit; x++)
    {
      for (int y = 0; y < yLimit; y++)
      {
        if (hexSearchArray[x][y].contains(inBack.x, inBack.y))
        {
          retPoint.x = x;
          retPoint.y = y;
          getOut = true;
          break;
        }
      }
      if (getOut)
      {
        break;
      }
    }
    return retPoint;
  }
  /**
   * convert an hex point into screen values
   * input is point in game in Hexes
   * output is point on screen in Pixels
   * output will be set to center of the hex on screen.
   */
  public static Vector2 ConvertToScreen(Hex hex)
  {
    Polygon poly = Map.GetBackPoly(hex);
    float[] vertices = poly.getVertices();
    Vector2 v1 = new Vector2(vertices[2],vertices[3]);
    Vector2 v2 = Map.BackToWorld(v1);
    Vector3 worldCoords = new Vector3(v2.x,v2.y,0);
    Vector3 screenCoordinates = ScreenGame.instance.cameraBackGround.project(worldCoords);
    Vector2 v2Return = new Vector2(screenCoordinates.x,screenCoordinates.y);
    return v2Return;
  }


  public static Vector2 WorldToBack(Vector2 inVec2)
  {
    Vector2 retPoint = inVec2;
    // change for level
    retPoint.x += xHalfScreenSize;
    retPoint.y = Math.abs(retPoint.y - yHalfScreenSize);
    // Gdx.app.log("WorldToBack","Calculated at " + retPoint.x + "," + retPoint.y);
    // // pixels of bitmap center is 0,0
    return retPoint;
  }

  /**
   * Get Polygon on the BackGround
   *
   * @param hex
   * @return
   */
  public static Polygon GetBackPoly(Hex hex)
  {
    Polygon poly = hexSearchArray[hex.xTable][hex.yTable];
    return poly;

  }

  public static Vector2 BackToWorld(Vector2 v2In)
  {
    Vector2 v2Return = new Vector2(0, 0);
    v2Return.x = v2In.x - xHalfScreenSize;
    v2Return.y = yHalfScreenSize - v2In.y;
    return v2Return;
  }

  static public Vector2 GetScreenPositionFromHex(Hex hex)
  {
    Vector2 vector2 = hex.GetDisplayCoord();
    Vector3 worldCoordinates = ScreenGame.instance.GetCamera().project((new Vector3(vector2.x, vector2.y, 0)));
    Vector2 world = new Vector2(worldCoordinates.x, worldCoordinates.y);
    return world;

  }

  public static boolean onScreen(Hex hexTarget) {
    Vector2 v2 = Map.ConvertToScreen(hexTarget);
    if (v2.x < 0 || v2.x > Gdx.graphics.getWidth()){
      return false;
    }
    if (v2.y < 0 || v2.y > Gdx.graphics.getHeight()){
      return false;
    }
    return true;

  }
}