package com.example.photobattle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

/**
 * Created by gab on 19/04/2016.
 */
public class BazarStatic {
    public static boolean host;
    public static Map map;
    public static String nomMap;
    public static boolean onLine =false;
    public final static int reqHeight=720;

    public static Bitmap decodeSampledBitmapFromResource(String res
                                                         , int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        options.inJustDecodeBounds = true;
        System.out.println(res);
        Bitmap b=BitmapFactory.decodeFile(res);
        int reqWidth = b.getWidth()*reqHeight/b.getHeight();
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return  Bitmap.createScaledBitmap(BitmapFactory.decodeFile(res, options),reqWidth,reqHeight,false);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
