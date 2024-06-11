package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.bruinbeargames.ardenne.AI.AIFaker;
import com.bruinbeargames.ardenne.Fonts;
import com.bruinbeargames.ardenne.GameMenuLoader;
import com.bruinbeargames.ardenne.ardenne;

/**
 *  Show AI is running
 */
public class EventAI{

    private Group group;
    private Image backgroundImage;
    private Label textLabel;
    private Button okButton;
    private String text;
    private I18NBundle i18NBundle;
    private TextButton.TextButtonStyle textButtonStyle;
    private String s1 =".     ";
    private String s2 ="..    ";
    private String s3 ="...   ";
    private String s4 ="....  ";
    private String s5 ="..... ";
    private String s6 ="......";
    private int cntTic = 0;

    public static EventAI instance;

    public EventAI(){
        instance = this;
        group = new Group();
        i18NBundle= GameMenuLoader.instance.localization;
        initializeStyles();
        initializeImageBackGround();
        initializeTextLabel();
        group.setVisible(false);
    }

    public void show(String text) {
        this.text = text;
        textLabel.setText(text+s1);
        textLabel.pack();
        GlyphLayout layout = textLabel.getGlyphLayout();
        float height = layout.height;
        float width = layout.width;
        textLabel.setSize(width + 10, height + 10);
        backgroundImage.setHeight(height + 40);
        backgroundImage.setWidth(width + 40);
        if(backgroundImage.getWidth() < 220){
            backgroundImage.setWidth(220);
        }
        if(backgroundImage.getHeight() < 100){
            backgroundImage.setHeight(100);
        }

//        backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (backgroundImage.getWidth()/2),
//                (Gdx.graphics.getHeight() / 2) - (backgroundImage.getHeight()/2));
        float x = (Gdx.graphics.getWidth()-backgroundImage.getWidth())/2;
        float y = (Gdx.graphics.getHeight()-backgroundImage.getHeight())/2;
//        backgroundImage.setPosition(0,Gdx.graphics.getHeight() - backgroundImage.getHeight());
        backgroundImage.setPosition(x,y);
        textLabel.setPosition((backgroundImage.getX() + backgroundImage.getWidth()/2) - (textLabel.getWidth()/2), backgroundImage.getY() +  (backgroundImage.getHeight() - height - (20)));
        group.setVisible(true);
        group.toFront();
        ardenne.instance.guiStage.addActor(group);
        ardenne.instance.setAIRender(true);
        percent =0;

 /* attempt for modal       NinePatch np = new NinePatch(UILoader.instance.unitSelection.asset.get("window"), 10, 10, 33, 6);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.getFont24(), Color.WHITE, new NinePatchDrawable(np));
        final Window win = new Window(" xxxxxx",windowStyle);
        win.setVisible(false);
        win.setHeight(2000);
        win.setWidth(4000);
        win.setPosition(0,0);
        win.setModal(true);
        win.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.getType().equals("touchUp")) {
                    group.setVisible(false);
                    group.remove();
                    setChanged();
                    notifyObservers(new ObserverPackage(ObserverPackage.Type.OK,null,0,0));
                    win.remove() ;
                }
            }
        });
        ardenne.instance.guiStage.addActor(win); */

    }

    private void initializeStyles(){

        textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("button"))),
                new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("buttonpressed"))),
                null,
                Fonts.getFont24());
        if(!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            textButtonStyle.font.getData().scale(0f);
        }
    }
    private void initializeImageBackGround() {


        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("pbembackground"), 12, 12, 12, 12);
        backgroundImage = new Image();
        backgroundImage.setDrawable(new NinePatchDrawable(np));
        backgroundImage.setHeight(281 );
        backgroundImage.setWidth(510 );
        backgroundImage.setPosition((Gdx.graphics.getWidth() / 2) - (250 / 1f),
                (Gdx.graphics.getHeight() / 2) - (140 / 1f));
        group.addActor(backgroundImage);
    }
    private void initializeTextLabel(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = Fonts.getFont24();
        if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)){
            style.font.getData().scale(0f);
        }
        textLabel = new Label("",style);
        textLabel.setSize(490/1f, 260/1f);
        textLabel.setPosition(backgroundImage.getX() + backgroundImage.getWidth()/2 - textLabel.getWidth()/2, backgroundImage.getY() + backgroundImage.getHeight()/2 - textLabel.getWidth()/2);
        textLabel.setVisible(true);

        group.addActor(textLabel);
    }


    public void hide(){
        group.setVisible(false);
        group.remove();
        ardenne.instance.setAIRender(false);
    }


    public void toFront() {
        group.toFront();
    }

    public void tick() {
        cntTic++;
        String textNew = text+"";
        if (percent > 0){
            int done =  AIFaker.instance.getPercentDone();
            textNew = text +" "+done+"%";
        }
 /*       switch(cntTic){
            case 0:textNew=text+" 100";
                break;
            case 1:textNew=text+" 99";
                break;
            case 2:textNew=text+s3;
                break;
            case 3:textNew=text+s4;
                break;
            case 4:textNew=text+s5;
                break;
            case 5:textNew=text+s6;
                break;
        } */

        if (cntTic > 4){
            cntTic = 0;
        }
        textLabel.setText(textNew);

    }
    int percent = 0;
    public void startCount(int percent) {
        this.percent = percent;
    }
}
