package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import de.golfgl.gdxgameanalytics.GameAnalytics;

public class Analytics extends GameAnalytics{

    private Thread.UncaughtExceptionHandler desktopUncaughtExceptionHandler;

    static public Analytics instance;

    public Analytics(String buildNumber) {
        instance = this;
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            // Create prefs file and uuid (if does not already exist)
           GamePreferences.instance.createAnalyticsData();


            setPlatformVersionString("1");
            setGameBuildNumber(buildNumber);

  /*          Base64 ed = new Base64();
            byte[] decodedBytes = ed.decodeBase64("ad763a3e836d88ae66319cfbd22668ae");
            String decodedString = new String(decodedBytes);
            setPrefs(Settings.instance.getPrefsAnalytics());
            setGameKey(decodedString);
            decodedBytes = ed.decodeBase64("1ad3c184eee91420ff5372eaf69fecd63091ad63");
            decodedString = new String(decodedBytes);
            setGameSecretKey(decodedString);*/
            setPrefs(GamePreferences.instance.getPrefsAnalytics());

            setGameKey("3b239a4b6e29101275d543b34a1080b0");
            setGameSecretKey("36d8d378ef1e4b8f9907c669800792af02c81019");

            startSession();
        }
    }

    public void gameStarted(){

    }

    public void setUpType(String type){
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            submitDesignEvent("SetUp Type:" + type);
            flushQueueImmediately();
        }
    }

    public void languageChosen(String language){
        if (!Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            submitDesignEvent("Language:" + language);
            flushQueueImmediately();
        }
    }

    /**
     * Registers a handler for catching all uncaught exceptions to send them to GA.
     */
    public void registerUncaughtExceptionHandler() {

        // don't register twice
        if (desktopUncaughtExceptionHandler != null)
            return;

        desktopUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                try {
                    sendThrowableAsErrorEventSync(e);

                } catch (Throwable ignore) {
                    // ignore
                } finally {
                    // Let Android show the default error dialog
                    desktopUncaughtExceptionHandler.uncaughtException(t, e);
                }
            }
        });
    }

}

