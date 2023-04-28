package com.bruinbeargames.ardenne.GameLogic;

import com.badlogic.gdx.audio.Sound;
import com.bruinbeargames.ardenne.SplashScreen;

public class SoundsLoader  {

    public static SoundsLoader instance;

    private static Sound diceRollingSound;
    private static Sound battleSounds;
    private static Sound artillerySounds;
    private static Sound truckSounds;
    private static Sound combatSounds;
    private static Sound moveSounds;
    private static Sound explosionSounds;
    private static Sound trainSounds;
    private static Sound rainSounds;
    private static Sound snowSounds;
    private static Sound limber;
    private static Sound march;
    private static Sound stuka;
    private static Sound mortar;


    private static float volume = 0.5f;
    private  long id;
    private boolean playSounds = true;
    private long battleSoundsID;
    private long diceRollingSoundID;
    private long artillerySoundsID;
    private long truckSoundsID;
    private long marchSoundsID;
    private long combatSoundsID;
    private long trainSoundsID;
    private long rainSoundsID;
    private long snowSoundsID;
    private long moveSoundsID;
    private long explosionSoundsID;
    private long limberSoundID;
    private long mortarSoundID;
    private long stukaSoundID;


    public  SoundsLoader() {
        instance = this;
        diceRollingSound = SplashScreen.instance.soundsManager.get("sounds/die_roll.mp3", Sound.class);
        battleSounds = SplashScreen.instance.soundsManager.get("sounds/backgroundbattle.mp3", Sound.class);
        artillerySounds = SplashScreen.instance.soundsManager.get("sounds/artillery.mp3", Sound.class);
        truckSounds = SplashScreen.instance.soundsManager.get("sounds/trucks.mp3", Sound.class);
        combatSounds = SplashScreen.instance.soundsManager.get("sounds/combat.mp3", Sound.class);
        moveSounds = SplashScreen.instance.soundsManager.get("sounds/movement.mp3", Sound.class);
        explosionSounds = SplashScreen.instance.soundsManager.get("sounds/bridgeblow.mp3", Sound.class);
        limber = SplashScreen.instance.soundsManager.get("sounds/limber.mp3", Sound.class);
        march = SplashScreen.instance.soundsManager.get("sounds/march.mp3", Sound.class);
        stuka = SplashScreen.instance.soundsManager.get("sounds/stuka.mp3", Sound.class);
        mortar = SplashScreen.instance.soundsManager.get("sounds/mortar.mp3", Sound.class);


    }


    public void playRainSound(){
        if (isPlaySounds()){
            rainSoundsID = rainSounds.loop(volume);
        }
    }

    public void stopRainSound(){
        rainSounds.stop();
    }

    public void playSnowSound(){
        if (isPlaySounds()){
            snowSoundsID = snowSounds.loop(volume);
        }
    }

    public void stopSnowSound(){
        snowSounds.stop();
    }

    public void playCombatSound(){
        if (isPlaySounds()) {
            combatSoundsID = combatSounds.play(volume);
        }
    }

    public void playDiceRollingSound(){
        if (isPlaySounds()) {
            diceRollingSoundID = diceRollingSound.play(volume);
        }
    }

    public void playMovementSound(){
        if (isPlaySounds()) {
            moveSoundsID = moveSounds.play(volume);
        }
    }
    boolean isBattleplaying = false;
    public void playBattleSound(){
        if (isPlaySounds() && !isBattleplaying) {
            battleSoundsID = battleSounds.loop(volume - .2f);
            isBattleplaying = true;
        }
    }

    public void playArtillerySound(){
        if (isPlaySounds()) {
            artillerySoundsID = artillerySounds.play(volume + .5f);
        }
    }

    public void playTrucksSound(){
        if (isPlaySounds()) {
            truckSoundsID = truckSounds.play(volume + .2f);
        }
    }

    public void playTrainSound(){
        if (isPlaySounds()) {
            trainSoundsID = trainSounds.play(volume );
        }
    }

    public void playExplosionSound(){
        if (isPlaySounds()) {
            explosionSoundsID = explosionSounds.play(volume );
        }
    }
    public void playLimber(){
        if (isPlaySounds()) {
            limberSoundID = limber.play(volume );
        }
    }
    public void playMarch(){
        if (isPlaySounds()) {
            marchSoundsID = march.play(volume );
        }
    }
    public void playStuka(){
        if (isPlaySounds()) {
            stukaSoundID = stuka.play(volume );
        }
    }
    public void playMortar(){
        if (isPlaySounds()) {
            mortarSoundID = mortar.play(volume );
        }
    }

    public void setVolume(float level){
        volume = level;
        battleSounds.setVolume(battleSoundsID, volume);
        artillerySounds.setVolume(artillerySoundsID, volume);
        truckSounds.setVolume(truckSoundsID, volume);
        diceRollingSound.setVolume(diceRollingSoundID, volume);
        combatSounds.setVolume(combatSoundsID, volume);
        moveSounds.setVolume(snowSoundsID, volume);
        limber.setVolume(limberSoundID, volume);
        if (volume == 0){
            playSounds = false;
            battleSounds.stop();
            isBattleplaying = false;
        }else{
            playSounds = true;
            playBattleSound();
        }
    }


    public boolean isPlaySounds() {
        return playSounds;
    }
    public void stopSounds(){
        battleSounds.stop();
        isBattleplaying  = false;
        artillerySounds.stop();
        truckSounds.stop();
        diceRollingSound.stop();
        combatSounds.stop();
        moveSounds.stop();
        explosionSounds.stop();
        limber.stop();
        march.stop();
        mortar.stop();
        stuka.stop();

    }
}



