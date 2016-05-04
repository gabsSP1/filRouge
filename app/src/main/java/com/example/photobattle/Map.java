package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.WeakHashMap;

/**
 * Created by Valentin on 29/02/2016.
 * La map. Comme quoi, c'est bien pensé.
 */
enum pix {VIDE, GROUND};

public class Map implements Serializable {
    private static final String TAG = Map.class.getSimpleName();
    private transient pix obstacles [][];
    byte[] bytePicture;
    byte[] byteContours;
    transient Bitmap contours;
    transient Bitmap photoOriginal;

    public Map(String pictureName)
    {

        contours =  BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+pictureName
                ,  BazarStatic.reqHeight);
        photoOriginal =BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+pictureName
                ,  BazarStatic.reqHeight);

        if(BazarStatic.onLine) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            contours.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteContours = stream.toByteArray();
            System.out.println(byteContours.length);
            for (int i = 0; i < byteContours.length; i++) {
//            System.out.println(byteContours[i]);
            }

            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            photoOriginal.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            bytePicture = stream2.toByteArray();
            computeObstacle();
        }




    }



    public void computeObstacle()
    {
        int width = contours.getWidth();
        int height = contours.getHeight();
        obstacles = new pix [width][height];
        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++) {

                obstacles[i][j] = pix.VIDE;
            }
        }
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                if(contours.getPixel(i,j) != Color.WHITE) {
                    obstacles[i][j] = pix.GROUND;
                }
            }
        }
    }

    public Bitmap getContours() {
        return contours;
    }




    public pix[][] getObstacles(){return obstacles;}


    public void convert() {
        this.contours = BitmapFactory.decodeByteArray(byteContours, 0, byteContours.length);
        this.photoOriginal = BitmapFactory.decodeByteArray(bytePicture, 0, bytePicture.length);
        computeObstacle();
    }

    public Bitmap getPhotoOriginal() {
        return photoOriginal;
    }

    public void recycle()
    {
        contours.recycle();
        photoOriginal.recycle();
        contours = null;
        photoOriginal =null;


        System.gc();
    }


}