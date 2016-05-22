package com.example.photobattle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    int xposJ1;
    int xposJ2;
    int yposJ1;
    int yposJ2;
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
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            photoOriginal.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            bytePicture = stream2.toByteArray();
            computeObstacle();
        }


        File dat = new File(FileManager.DATA_PATH, pictureName+".dat");
        String data = null;
        //char[] inputBuffer = new char[255];
        if (dat.exists()) {


            try {

                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(dat);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = "";
                if ((line = br.readLine()) != null) {
//                    s1.setProgress(Integer.parseInt(line));
                }
                if ((line = br.readLine()) != null) {
                    xposJ1 = Integer.parseInt(line);
                }
                if ((line = br.readLine()) != null) {
                    yposJ1 = Integer.parseInt(line);
                }
                if ((line = br.readLine()) != null) {
                    xposJ2 = Integer.parseInt(line);
                }
                if ((line = br.readLine()) != null) {
                    yposJ2 = Integer.parseInt(line);
                }


                in.close();


                //affiche le contenu de mon fichier dans un popup surgissant
                //Toast.makeText(context, " precision" + s1.getProgress() +" x1" + xposJ1 +" y1" + xposJ1 +" x2" + xposJ2 +" y2" + yposJ2 +"  "+coordGomme, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
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