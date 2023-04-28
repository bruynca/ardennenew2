package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontFactory {
    static public FontFactory instance;
    public BitmapFont largeFont;
    public BitmapFont largeFontWhite;
    public BitmapFont jumboFont;
    public BitmapFont yellowFont;

    //	BitmapFontData usWhiteData;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
//	Texture[] texCombat = new Texture[10];

    public FontFactory()
    {
        /**
         *  kludge  creating too many fontfacotories
         */
            instance = this;
        /**
         * FontFactory called by uNFORTUNATLY
         */
//		TextOverlay textOverlay = new TextOverlay();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ariblk.TTF"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 26;
        parameter.color = Color.BLACK;
        parameter.genMipMaps = true;
//        parameter.borderColor = Color.WHITE;
//        parameter.borderWidth = ;
        largeFont = generator.generateFont(parameter); // font size 11 pixels

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size =  30;
        parameter.genMipMaps = true;

        parameter.color = Color.WHITE;
        parameter.borderColor = new Color(0,0,0,.5f);
        parameter.borderWidth = 3;

        jumboFont =  generator.generateFont(parameter); // font size 10 pixels

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 26;
        parameter.color = Color.YELLOW;
        parameter.borderColor = Color.RED;
        parameter.borderWidth = 4;

        parameter.genMipMaps = true;
//        parameter.borderColor = Color.WHITE;
//        parameter.borderWidth = ;
        yellowFont = generator.generateFont(parameter); // font size 11 pixels

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.color = Color.WHITE;
        parameter.genMipMaps = true;
//        parameter.borderColor = Color.WHITE;
//        parameter.borderWidth = ;
        largeFontWhite= generator.generateFont(parameter); // font size 11 pixels

        generator.dispose();
    }
    public void dispose()
    {
        largeFont.dispose();
        jumboFont.dispose();
    }
    public BitmapFont GetLargeFont()
    {
        return largeFont;
    }
    public BitmapFont GetMediumFont()
    {
        return jumboFont;
    }



}
