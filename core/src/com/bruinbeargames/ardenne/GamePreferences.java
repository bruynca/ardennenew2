package com.bruinbeargames.ardenne;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
* Singleton Object
* Used to store and get User preferences / Common Game file locations and other sundry information
*/
public class GamePreferences {

    public  static GamePreferences instance;
    private static Preferences prefs = Gdx.app.getPreferences("ardenne_prefs");
    private static Preferences prefsAnalytics = Gdx.app.getPreferences("ardenne_analytics_prefs");
    private static Preferences mobileprefs = Gdx.app.getPreferences("ardenne_prefs");
    private static Preferences mobileprefsAnalytics = Gdx.app.getPreferences("ardenne_analytics_prefs");
    private static String buildNumber = "1.0.0.0";
    private static String gameDir = "bruinbeargames/ardenne/savedgames/";

    public GamePreferences() {

        instance = this;
    }

    public static GamePreferences getInstance(){
        if(instance == null){
            instance = new GamePreferences();
        }
        return instance;
    }


    public static void saveWindowSize(int width, int height) {

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putInteger("screensize_width", width);
            prefs.putInteger("screensize_height", height);
            prefs.flush();
        }else{
            mobileprefs.putInteger("screensize_width", width);
            mobileprefs.putInteger("screensize_height", height);
            mobileprefs.flush();
        }
    }
    public static void saveVolume(float  level){
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putFloat("volume", level);
            prefs.flush();
        }else{
            mobileprefs.putFloat("volume", level);
            mobileprefs.flush();
        }
    }
    public static float getVolume(){
        return prefs.getFloat("volume",1f);
    }

	/**
	* Defaults to Monitor screen size if cannot find a saved User Preference
	*/
    public static Vector2 getWindowSize(){

        Vector2 windowSize = new Vector2(prefs.getInteger("screensize_width", Gdx.graphics.getDisplayMode().width), prefs.getInteger("screensize_height", Gdx.graphics.getDisplayMode().height));
        return windowSize;
    }


    public static void saveFullScreen(boolean fullscreen){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("fullscreen", fullscreen);
            prefs.flush();
        }else{
            mobileprefs.putBoolean("fullscreen", fullscreen);
            mobileprefs.flush();
        }
    }

/**
	* Defaults to fullscreen mode if cannot find a saved User Preference
	*/
    public static boolean isFullScreen(){
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("fullscreen", true);
        }else{
            return mobileprefs.getBoolean("fullscreen", true);
        }
    }

    public static FileHandle getSaveScreenShotsFileLocation(){
        Date date = new Date(TimeUtils.millis());
        return Gdx.files.external("/Krim/Screenshots/" + date.getTime() + ".png");
    }

    public static FileHandle getSaveLogFileLocation(){
        Date date = new Date(TimeUtils.millis());
        return Gdx.files.external("/Krim/Logs/log.txt");
    }

    public static FileHandle getSaveSetUpAxisLocation(){
        return Gdx.files.local("/savedsetup/setupsaveaxishex.xml");
    }

    public static FileHandle getSaveSetUpAxisLocationRegular(){
        return Gdx.files.local("/savedsetup/setupsaveaxishexregular.xml");
    }

    public static FileHandle getAutoSetUpAxisFileLocation(){
        return Gdx.files.local("/units/autosetupaxisunits.xml");
    }

    public static FileHandle getAutoSetUpSovietFileLocation(){
        return Gdx.files.local("/units/autosetupunits.xml");
    }

    public static FileHandle getSaveSetUpFileLocation(){
        return Gdx.files.local("/savedsetup/setupsavehex.xml");
    }

    public static FileHandle getLoadBustardLocation(){
        return Gdx.files.local("/savedsetup/bustardsetup.xml");
    }

    public static FileHandle getSaveGamesLocation(String filename) {

        Gdx.files.external(gameDir).mkdirs();

        return Gdx.files.external(gameDir + filename);


    }

    public static FileHandle getSaveGamesLocation() {
        Gdx.files.external(gameDir).mkdirs();
        return Gdx.files.external(gameDir);


    }
    public static FileHandle getSaveAIDebug(String fileName) {

        File folder = new File("../aidebug/");
        if (!folder.exists()) {
            new File("../aidebug/").mkdir();
        }
        return Gdx.files.local("../aidebug/"+fileName);


    }
    public static FileHandle getSaveAIDebug() {

        File folder = new File("../aidebug/");
        if (!folder.exists()) {
            new File("../aidebug/").mkdir();
        }
        return Gdx.files.local("../aidebug/");


    }

    public static FileHandle getPBEMGamesLocation(String pbemFileName) {

        File folder = new File("../pbem/");
        if (!folder.exists()) {
            new File("../pbem/").mkdir();
        }
        return Gdx.files.local("../pbem/" + pbemFileName);


    }

    public static String getGameManualURL(){
        return "http://www.yobowargames.com/wp-content/uploads/2018/05/VV_D1_Standard_Game_Rules.pdf";
    }

    public static void logToConsole(boolean log){

        prefs.putBoolean("logtoconsole", log);
        prefs.flush();
    }

    public static boolean isLogToConsole(){

        return prefs.getBoolean("logtoconsole", true);
    }

    public static void logToFile(boolean log){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("logtofile", log);
            prefs.flush();
        }else{
            mobileprefs.putBoolean("logtofile", log);
            mobileprefs.flush();
        }
    }

    public static boolean isLogToFile(){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("logtofile", false);
        }else{
            return mobileprefs.getBoolean("logtofile", false);

        }

    }

    public static void logToInDevConsole(boolean log){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("logtodevconsole", log);
            prefs.flush();
        }{
            mobileprefs.putBoolean("logtodevconsole", log);
            mobileprefs.flush();
        }
    }

    public static boolean isLogToDevConsole(){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("logtofile", false);
        }else{
            return mobileprefs.getBoolean("logtofile", false);
        }
    }

    public static void aiEvpatoriaHitTurn1(boolean log){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("aiev", log);
            prefs.flush();
        }else{
            mobileprefs.putBoolean("aiev", log);
            mobileprefs.flush();
        }
    }

    public static boolean isaiEvpatoriaHitTurn1(){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("aiev", false);
        }else{
            return mobileprefs.getBoolean("aiev", false);

        }

    }

    public static void aiKerchHitTurn1(boolean log){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("aikerch", log);
            prefs.flush();
        }else{
            mobileprefs.putBoolean("aikerch", log);
            mobileprefs.flush();
        }
    }


    public static boolean isaiKerchHitTurn1(){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("aikerch", false);
        }else{
            return mobileprefs.getBoolean("aikerch", false);

        }
   }
    public static void aiWonShortScene(boolean log){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            prefs.putBoolean("aiwonshort", log);
            prefs.flush();
        }else{
            mobileprefs.putBoolean("aiwonshort", log);
            mobileprefs.flush();
        }
    }


    public static boolean isaiWonShortScene(){

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefs.getBoolean("aiwonshort", false);
        }else{
            return mobileprefs.getBoolean("aiwonshort", false);

        }
    }

    public static void createAnalyticsData(){

        // Only create uuid once so test if already exists
        Map tmpmap;
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            tmpmap = prefsAnalytics.get();
        }else{
            tmpmap = mobileprefsAnalytics.get();
        }
        if (tmpmap.isEmpty()) {

            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
                prefsAnalytics.putString("user_id", randomUUIDString);
                prefsAnalytics.flush();
            }else{
                mobileprefsAnalytics.putString("user_id", randomUUIDString);
                mobileprefsAnalytics.flush();
            }

        }

    }

    public static Preferences getPrefsAnalytics(){
        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            return prefsAnalytics;
        }else{
            return mobileprefsAnalytics;
        }
    }

    public static String getBuildNumber() {
        return buildNumber;
    }

    public static void setWindowLocation(String strWindow, int xPos, int yPos) {
        prefs.putInteger(strWindow+"x",xPos);
        prefs.putInteger(strWindow+"y",yPos);
        prefs.flush();
    }

    public static Vector2 getWindowLocation(String strWindowName) {
        int x  = prefs.getInteger(strWindowName+"x");
        int y  = prefs.getInteger(strWindowName+"y");
        Vector2 v2 = new Vector2(x,y);
        return v2;
    }
    public static  boolean getPhaseInfo(String str){
        boolean ret = prefs.getBoolean(str);
        return  ret;
    }
    public static void setPhaseInfo(Phase phase){
        prefs.putBoolean(phase.toString(),true);
        prefs.flush();
    }
    public static void setOther(String strOther){
        prefs.putBoolean(strOther,true);
        prefs.flush();
    }

    public static FileHandle getPBEMScenariosLocation() {
        return Gdx.files.local("pbemTempData/");
    }

    public static FileHandle getScenariosLocation() {

        return Gdx.files.local("scenarios/");

    }
    public static void initAIPreferences(){
        aiEvpatoriaHitTurn1(false);
        aiKerchHitTurn1(false);
        aiWonShortScene(false);
    }
}

