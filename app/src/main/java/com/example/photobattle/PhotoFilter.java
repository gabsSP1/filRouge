package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by gab on 09/03/2016.
 */
//Partie de Hugo Monyac

class PhotoFilter {


    static Bitmap  chgtoBandW(Bitmap img){
        //img.setImageBitmap(source)
        Bitmap mapnew= Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        int color = 0;
        int[][] bmx= new int[img.getWidth()][img.getHeight()];
        for(int i=0;i<img.getWidth();i++)
        {
            for(int j= 0;j<img.getHeight();j++)
            {
                int colorOfPixel = img.getPixel(i, j);
                mapnew.setPixel(i,j,colorOfPixel);

            }
        }
        mapnew = DoFullFilter(mapnew);
        return mapnew;
    }

    public static Bitmap DoHorizontalFilter(Bitmap BitmapGray){
        int w, h, threshold;


        h= BitmapGray.getHeight();
        w= BitmapGray.getWidth();
        threshold = 100;

        Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);

        for(int x = 0; x < w-1; ++x) {
            for(int y = 0; y < h; ++y) {

                int pixel = BitmapGray.getPixel(x, y);
                int pixelsuivant = BitmapGray.getPixel(x+1,y);

                int gray = (Color.red(pixel) +Color.blue(pixel)+Color.green(pixel))/3;
                int gNext = (Color.red(pixelsuivant) +Color.blue(pixelsuivant)+Color.green(pixelsuivant))/3;
                if((gray-gNext)*(gray-gNext)< threshold){
                    BitmapBiner.setPixel(x, y, 0xFFFFFFFF);
                } else{
                    BitmapBiner.setPixel(x, y, 0xFF000000);
                }

            }
        }
        return BitmapBiner;

    }
    public static Bitmap DoFullFilter(Bitmap BitmapGray){
        int w, h, threshold;

        h= BitmapGray.getHeight();
        w= BitmapGray.getWidth();

        Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);
        Bitmap bHor = DoHorizontalFilter(BitmapGray);
        Bitmap bVer = DoVerticalFilter(BitmapGray);
        for(int x = 0; x < w; ++x) {
            for(int y = 0; y < h; ++y) {
                int horp = bHor.getPixel(x,y);
                int verp = bVer.getPixel(x,y);
                if(horp != verp){
                    BitmapBiner.setPixel(x,y,0xFF000000);
                }
                else
                    BitmapBiner.setPixel(x,y,0xFFFFFFFF);
            }
        }
        return BitmapBiner;

    }
    public static Bitmap DoVerticalFilter(Bitmap BitmapGray){
        int w, h, threshold;

        h= BitmapGray.getHeight();
        w= BitmapGray.getWidth();

        threshold = 100;

        Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);

        for(int x = 0; x < w; ++x) {
            for(int y = 0; y < h-1; ++y) {

                int pixel = BitmapGray.getPixel(x, y);
                int pixelsuivant = BitmapGray.getPixel(x,y+1);

                int gray = (Color.red(pixel) +Color.blue(pixel)+Color.green(pixel))/3;
                int gNext = (Color.red(pixelsuivant) +Color.blue(pixelsuivant)+Color.green(pixelsuivant))/3;
                if((gray-gNext)*(gray-gNext)< threshold){
                    BitmapBiner.setPixel(x, y, 0xFFFFFFFF);
                } else{
                    BitmapBiner.setPixel(x, y, 0xFF000000);
                }

            }
        }
        return BitmapBiner;

    }

    public static Bitmap grayScale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    /*

    //Create a thumbnail in the directory /thumbnail of the file indicated by the parameter path
    public  static  void createThumbnail(Bitmap imageBitmap, String pictureName) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(100, 60, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        int height = 60;
        int width = (int) (imageBitmap.getWidth() * ((double) height / imageBitmap.getHeight()));
        imageBitmap = getResizedBitmap(imageBitmap, width, height);
        canvas.drawBitmap(imageBitmap, (int) (100 - width) / 2, 0, null);
        FileManager.saveBitmap(bmp, FileManager.THUMBNAIL_PATH, pictureName);

    }*/

    //resize a bitmap (bad quality, ideal to make light thumbnail image of a bigger picture)
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}


