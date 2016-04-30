package com.example.photobattle;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Tom on 22/04/2016.
 */
public class Sound {

    private static MediaPlayer soundEffects = null;
    private static MediaPlayer musicPlayer = null;
    private static float musicVolume = 1;

    public static void playSound(Context cont,int resId) {
        if(soundEffects != null) {
            soundEffects.stop();
            soundEffects.release();
            //mPlayer.setLooping(false);
        }
        soundEffects = MediaPlayer.create(cont,resId);
        soundEffects.start();
    }

    public static void playMenuMusic(Context cont)
    {
        if(musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
        musicPlayer = MediaPlayer.create(cont,R.raw.mus_menu);
        musicPlayer.start();
        musicPlayer.setLooping(true);
    }

    public static void playFightMusic(Context cont)
    {
        if(musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
        musicPlayer = MediaPlayer.create(cont,R.raw.mus_fight);
        musicPlayer.start();
        musicPlayer.setLooping(true);
    }

    public static void pauseMusic() {
        musicPlayer.pause();
    }

    public static void resumeMusic()
    {
        musicPlayer.start();
    }

    public static float muteMusic() {
        if(musicVolume == 1) {
            musicPlayer.setVolume(0, 0);
            musicVolume = 0;
            System.out.println("Music muted");
        }
        else
        {
            musicPlayer.setVolume(1,1);
            musicVolume = 1;
            System.out.println("Music unmuted");
        }
        return musicVolume;
    }
}
