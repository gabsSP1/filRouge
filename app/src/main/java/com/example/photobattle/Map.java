package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

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
    WeakReference<Bitmap> contours;
    WeakReference<Bitmap> photoOriginal;
    Bitmap b;
    Bitmap c;

    public Map(String pictureName)
    {
        c= BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+pictureName
                ,  1080);
        b= BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+pictureName
                ,  1080);
        contours =  new WeakReference<Bitmap>(c);
        photoOriginal = new WeakReference<Bitmap>(b);
        System.out.println(photoOriginal.get());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        System.out.println(contours.get());
        contours.get().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteContours = stream.toByteArray();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        photoOriginal.get().compress(Bitmap.CompressFormat.PNG, 100, stream2);
        bytePicture = stream2.toByteArray();
        computeObstacle();



    }



    public void computeObstacle()
    {
        int width = contours.get().getWidth();
        int height = contours.get().getHeight();
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
                if(contours.get().getPixel(i,j) != Color.WHITE) {
                    obstacles[i][j] = pix.GROUND;
                }
            }
        }
    }

    public Bitmap getContours() {
        return contours.get();
    }




    public pix[][] getObstacles(){return obstacles;}


    public void convert() {
        this.contours = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(byteContours, 0, byteContours.length));
        this.photoOriginal = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(bytePicture, 0, bytePicture.length));
        computeObstacle();
    }

    public Bitmap getPhotoOriginal() {
        return photoOriginal.get();
    }

    public void recycle()
    {
        c.recycle();
        b.recycle();
        b=null;
        c=null;
        contours.clear();
        photoOriginal.clear();
        contours = null;
        photoOriginal =null;
        System.gc();
    }


}