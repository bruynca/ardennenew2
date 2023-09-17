package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bruinbeargames.ardenne.GameLogic.Supply;
import com.bruinbeargames.ardenne.Hex.Hex;
import com.bruinbeargames.ardenne.Hex.HiliteHex;

import java.util.ArrayList;


public class WinDebug {
    TextButton textButton;
    Stage guiStage;
    Stage mapStage;
    public static WinDebug instance = null;
    static Label.LabelStyle labelStyleName
            = new Label.LabelStyle(FontFactory.instance.yellowFont, Color.YELLOW);

    TextButton.TextButtonStyle tx = GameSelection.instance.textButtonStyle;
     HiliteHex hiliteHex;

    public WinDebug()
    {
        if (instance != null)
        {
            return;
        }
        mapStage = ardenne.instance.mapStage;
        guiStage = ardenne.instance.guiStage;
        instance = this;
        textButton =  new TextButton("Debug",tx);
        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                ClickButton(event, x, y);
            }
        });
        Display();

    }
    public void Display()
    {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        // set position 3/4 of the width and at top
        float x =  (float) width * 1/10 + 100F;
        float y = (float) height - textButton.getHeight();
		textButton.remove();
        textButton.setPosition(x, y);
        guiStage.addActor(textButton);
        textButton.setVisible(false);
    }
    boolean isVisible = false;
    public void toggle(){
        if (isVisible){
            textButton.setVisible(false);
            isVisible = false;
        }else{
            textButton.setVisible(true);
            isVisible = true;
        }

    }
    boolean clicked = true;
    public void ClickButton(InputEvent event, float xIn, float yIN)
    {
        if (clicked){
            if (hiliteHex != null){;
                hiliteHex.remove();
                hiliteHex = null;
                return;
            }
 //           doShowJunctions();
            doShowHexes();
            ArrayList<Hex> arrHexSupply = new ArrayList<>();
            arrHexSupply.addAll(Supply.instance.getGermanBottlenecks());
            hiliteHex = new HiliteHex(arrHexSupply, HiliteHex.TypeHilite.ShowSupply, null);
            return;
        }
        if (clicked)
        {


 //           DoShowPath();
			doShowRivers();

            clicked = false;
        }
        else
        {
            clicked = true;
            for (Actor actor:arrActors)
            {
                actor.remove();
            }
        }


    }
    ArrayList<Actor> arrActors =  new ArrayList();
    private void doShowHexes(){
        ArrayList<Hex> arrHex = new ArrayList<>();
        for (int x=0; x< Hex.xEnd; x++)
        {
            for (int y=0; y < Hex.yEnd; y++)
            {
                arrHex.add(Hex.hexTable[x][y]);
            }
        }
        hiliteHex = new HiliteHex(arrHex, HiliteHex.TypeHilite.Debug,null);
    }

    private void doShowJunctions()
    {
        arrActors.clear();
        for (int y = 0; y < Hex.yEnd; y++)
        {
            for (int x = 0; x< Hex.xEnd; x++) {
                Hex hex = Hex.hexTable[x][y];
                if (hex.isJunction()) { // &&(hex.isPath || hex.isRoad)) {
//                    String str = x + "-" + y;
                    String str= "J";

                    Label label = new Label(str, labelStyleName);
                    label.setFontScale(1.5f);
                    label.setColor(Color.YELLOW);
                    Vector2 vector2 = hex.GetDisplayCoord();
                    label.setPosition(vector2.x + 30, vector2.y + 50);
                    mapStage.addActor(label);
                    arrActors.add(label);
                    //              }
                }
            }

        }

    }

    private void DoShowPath()
    {
        arrActors.clear();
        for (int y = 0; y < Hex.yEnd; y++)
        {
            for (int x = 0; x< Hex.xEnd; x++) {
                Hex hex = Hex.hexTable[x][y];
               if (hex.arrConnections.size() > 0) { // &&(hex.isPath || hex.isRoad)) {
//                    String str = x + "-" + y;
                    String str= "";
                    for (Integer in:hex.arrConnections){
                        str = str+"-"+in;
                    }

                    Label label = new Label(str, labelStyleName);
                    label.setFontScale(1f);
                    label.setColor(Color.YELLOW);
                    Vector2 vector2 = hex.GetDisplayCoord();
                    label.setPosition(vector2.x + 30, vector2.y + 50);
                    mapStage.addActor(label);
                    arrActors.add(label);
  //              }
               }
            }

        }

    }
    private void doShowRivers(){
        arrActors.clear();
        for (int y = 0; y < Hex.yEnd; y++)
        {
            for (int x = 0; x< Hex.xEnd; x++) {
                Hex hex = Hex.hexTable[x][y];
                if (hex.isStreamBank()) { // &&(hex.isPath || hex.isRoad)) {
//                    String str = x + "-" + y;
                    String str= hex.riverStr;

                    Label label = new Label(str, labelStyleName);
                    label.setFontScale(1f);
                    label.setColor(Color.YELLOW);
                    Vector2 vector2 = hex.GetDisplayCoord();
                    label.setPosition(vector2.x + 30, vector2.y + 50);
                    mapStage.addActor(label);
                    arrActors.add(label);
                    //              }
                }
            }
        }

    }

    /**
     private void DoSelectedRegions()
     {
     ArrayList<Hex> arrHex = new ArrayList<Hex>();
     AIRegion aiRegion = AIRegionFactory.instance.topOfMapInDanger;
     AIRegion aiRegionNew = AIRegionFactory.instance.GetPreviousXRegion(aiRegion);
     arrHex.addAll(aiRegionNew.GetRightBorderHex(3));
     //		arrHex.addAll(AIRegionFactory.instance.middleOfMapInDanger.arrHex);
     //		arrHex.addAll(AIRegionFactory.instance.bottomOfMapInDanger.arrHex);
     HiliteHex.instance.Activate(arrHex,Type.Debug.GetValue(), false);
     clicked = false;
     }
     private void DoAIRegion(int i, int j)
     {

     AIRegion ai = AIRegionFactory.instance.AIRegionTable[i][j];
     ArrayList<Hex> arrHex = ai.arrHex;
     HiliteHex.instance.Activate(arrHex, HiliteHex.Type.Debug.GetValue(), false);
     clicked = false;


     }
     public void DoRoadMoveCost()
     {
     // use main thread
     Hex.InitMoveCost(0);
     ArrayList<Hex> arrHexes = new ArrayList<Hex>();
     for (int y = 0; y < Hex.yEnd; y++)
     {
     for (int x = 0; x< Hex.xEnd; x++)
     {
     Hex hex = Hex.hexTable[x][y];
     if (hex.road != null)
     {
     hex.cntMoveCost[0] =hex.road.arrNextRoad.size();
     }
     arrHexes.add(hex);
     //				if (hex.isRoad)
     //				{
     //					hex.cntMoveCost++;
     //					arrHexes.add(hex);
     //					hex.cntMoveCost += hex.road.arrNextRoad.size();
     //				}
     }

     }
     HiliteHex.instance.Activate(arrHexes, HiliteHex.Type.Debug.GetValue(), false);
     clicked = false;

     }
     public void DoBridge()
     {
     // use main thread
     Hex.InitMoveCost(0);
     ArrayList<Hex> arrHexes = new ArrayList<Hex>();
     for (int y = 0; y < Hex.yEnd; y++)
     {
     for (int x = 0; x< Hex.xEnd; x++)
     {
     Hex hex = Hex.hexTable[x][y];
     if (hex.isBridge)
     {
     arrHexes.add(hex);
     }
     //				if (hex.isRoad)
     //				{
     //					hex.cntMoveCost++;
     //					arrHexes.add(hex);
     //					hex.cntMoveCost += hex.road.arrNextRoad.size();
     //				}
     }

     }
     HiliteHex.instance.Activate(arrHexes, HiliteHex.Type.Debug.GetValue(), false);
     clicked = false;

     }
     public void DoOccupied()
     {
     // use main thread
     Hex.InitMoveCost(0);
     ArrayList<Hex> arrHexes = new ArrayList<Hex>();
     for (int y = 0; y < Hex.yEnd; y++)
     {
     for (int x = 0; x< Hex.xEnd; x++)
     {

     Hex hex = Hex.hexTable[x][y];
     if (hex.occHexAllies[Settings.instance.threadAI])
     {
     arrHexes.add(hex);
     }
     //				if (hex.isRoad)
     //				{
     //					hex.cntMoveCost++;
     //					arrHexes.add(hex);
     //					hex.cntMoveCost += hex.road.arrNextRoad.size();
     //				}
     }

     }
     HiliteHex.instance.Activate(arrHexes, HiliteHex.Type.Debug.GetValue(), false);
     clicked = false;

     }

     public void Chosen(Hex hex)
     {
     int i=1;
     }

     */
}

