package com.example.photobattle;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Tom on 22/04/2016.
 */
public class Sound {

    private static MediaPlayer soundEffects = null;
    private static MediaPlayer musicPlayer = null;

    public static void playSound(Context cont,int resId) {
        if(soundEffects != null) {
            soundEffects.stop();
            soundEffects.release();
            //mPlayer.setLooping(false);
        }
        soundEffects = MediaPlayer.create(cont,resId);
        soundEffects.start();
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
}
