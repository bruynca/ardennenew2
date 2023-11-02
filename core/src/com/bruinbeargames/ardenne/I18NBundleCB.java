package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class I18NBundleCB extends I18NBundle {
    static Hashtable<String,String> chineseTranslation;
    static void loadLanguage(String lang){
        if (lang.contains("language_zh")){
            chineseTranslation = new Hashtable<>();
            FileHandle baseFileHandle = Gdx.files.internal("i18n/language_zh.properties");
           
           byte[] read = baseFileHandle.readBytes();
           ByteBuffer buffer =ByteBuffer.wrap(read);
           @SuppressWarnings("NewApi") String str = StandardCharsets.UTF_8.decode(buffer).toString();
            int b=0;
        }
    }
}
