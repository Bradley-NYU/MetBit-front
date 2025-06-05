package com.example.metbit.common;


import android.content.Context;
import android.media.MediaPlayer;

public class AudioManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;

    public static void play(Context context, int resId) {
        stop(); // 防止重叠
        mediaPlayer = MediaPlayer.create(context, resId);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                stop();
            });
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public static void toggle() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                mediaPlayer.start();
                isPlaying = true;
            }
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public static boolean isPlaying() {
        return isPlaying;
    }
}
