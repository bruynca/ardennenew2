package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LoadGamePopUp {

    private Image backGroundImage;
    private Label phaseText;
    private ScrollPane scrollPane;
    private boolean isDisplayed = false;
    private Group group;

    public LoadGamePopUp(Stage stage){

        this.group = new Group();

        initializeImageBackGround();
        initializeScrollPane();

        group.setVisible(false);
        stage.addActor(group);
    }

    public void show(){
        group.setVisible(true);
    }

    private void initializeImageBackGround(){

        NinePatch np = new NinePatch(GameMenuLoader.instance.gameMenu.asset.get("tooltip"),5, 5, 5, 5);
        backGroundImage = new Image();
        backGroundImage.setDrawable(new NinePatchDrawable(np));
        backGroundImage.setVisible(true);

        backGroundImage.setHeight(200);
        backGroundImage.setWidth(200);
        backGroundImage.setPosition((Gdx.graphics.getWidth()/2) - ((backGroundImage.getWidth()/2)),
                (Gdx.graphics.getHeight()/2) - ((backGroundImage.getHeight()/2)));

        group.addActor(backGroundImage);
        //group.addActor(phaseText);

    }

    private void initializeScrollPane(){

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Fonts.getFont24();
        listStyle.fontColorSelected.set(Color.WHITE);
        listStyle.fontColorUnselected.set(Color.BLACK);
        listStyle.selection = new TextureRegionDrawable(new TextureRegion(GameMenuLoader.instance.gameMenu.asset.get("checkboxselected")));
        List<String> list = new List<>(listStyle);
        String[] strings = new String[20];
        for (int i = 0, k = 0; i < 20; i++) {
            strings[k++] = "String: " + i;

        }
        list.setItems(strings);
        scrollPane = new ScrollPane(list);
        scrollPane.debug();
        scrollPane.setBounds(0, 0, backGroundImage.getWidth() - 10, backGroundImage.getHeight() - 10);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(backGroundImage.getX() + 10,
                backGroundImage.getY() + 10);
        scrollPane.setTransform(true);
        ScrollPane.ScrollPaneStyle style  = scrollPane.getStyle();
        group.addActor(scrollPane);
    }
}
