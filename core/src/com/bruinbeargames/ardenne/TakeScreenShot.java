package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class TakeScreenShot {
    private static TakeScreenShot instance;

    private TakeScreenShot() {
    }

    public static TakeScreenShot getInstance() {
        if (instance == null) {
            instance = new TakeScreenShot();
        }

        return instance;
    }

    public static void saveScreenShot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        for(int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = -1;
        }

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(GamePreferences.getSaveScreenShotsFileLocation(), pixmap);
        pixmap.dispose();
    }
}