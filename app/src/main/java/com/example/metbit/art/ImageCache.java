package com.example.metbit.art;

import android.graphics.Bitmap;

public class ImageCache {
    private static Bitmap bitmap;

    public static void setBitmap(Bitmap bmp) {
        bitmap = bmp;
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void clear() {
        bitmap = null;
    }
}
