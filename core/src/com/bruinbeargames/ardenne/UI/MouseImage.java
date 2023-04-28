package com.bruinbeargames.ardenne.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class MouseImage {

    static public MouseImage instance;

    private Pixmap mouseDragged;
    private Pixmap mouseUp;
    private Pixmap mouseDown;
    private Pixmap mouseLeft;
    private Pixmap mouseRight;
    private Pixmap mouseHand;
    private Pixmap mouseGerman;
    private Pixmap mouseRussian;
    private Pixmap mouseHotseat;
    private Cursor cursor;
    private boolean ignore;

    public MouseImage(){

        instance = this;

        mouseDragged = new Pixmap(Gdx.files.internal("effects/mousedrag.png"));
        mouseUp = new Pixmap(Gdx.files.internal("effects/mouseup.png"));
        mouseDown = new Pixmap(Gdx.files.internal("effects/mousedown.png"));
        mouseLeft = new Pixmap(Gdx.files.internal("effects/mouseleft.png"));
        mouseRight = new Pixmap(Gdx.files.internal("effects/mouseright.png"));
        mouseHand = new Pixmap(Gdx.files.internal("effects/mousehand.png"));
        mouseGerman = new Pixmap(Gdx.files.internal("effects/mousegerman.png"));
        mouseRussian = new Pixmap(Gdx.files.internal("effects/mouserussian.png"));
        mouseHotseat = new Pixmap(Gdx.files.internal("effects/mousehotseat.png"));

    }

    public void setIgnore(boolean ignore){
        this.ignore = ignore;
    }

    public void mouseImageReset(){
        if (!ignore) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    public void setMouseDragged(){
        cursor = Gdx.graphics.newCursor(mouseDragged, 16, 16);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseUp() {
        cursor = Gdx.graphics.newCursor(mouseUp, 16, 0);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseDown() {
        cursor = Gdx.graphics.newCursor(mouseDown, 16, 31);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseLeft() {
        cursor = Gdx.graphics.newCursor(mouseLeft, 0, 16);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseRight() {
        cursor = Gdx.graphics.newCursor(mouseRight, 31, 16);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseHand() {
        cursor = Gdx.graphics.newCursor(mouseHand, 0, 0);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseGerman() {
        cursor = Gdx.graphics.newCursor(mouseGerman, 16, 16);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseRussian() {
        cursor = Gdx.graphics.newCursor(mouseRussian, 16, 16);
        Gdx.graphics.setCursor(cursor);
    }

    public void setMouseHotseat() {
        cursor = Gdx.graphics.newCursor(mouseHotseat, 16, 16);
        Gdx.graphics.setCursor(cursor);
    }
}
