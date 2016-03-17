package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;

/**
 * Created by Valentin on 29/02/2016.
 * La map. Comme quoi, c'est bien pensé.
 */
enum pix {VIDE, GROUND};

public class Map
{
    private static final String TAG = Map.class.getSimpleName();

    private Bitmap contours;
    private Bitmap photoOriginal;
    private Rect zone;
    private pix obstacles [][];
    private int width;
    private int height;
    private float density;
    private MainGamePanel mainGamePanel;
    private String pictureName;

    public Map(String pictureName, MainGamePanel mainGamePanel)
    {
        this.pictureName = pictureName;
        this.mainGamePanel = mainGamePanel;
    }


    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(contours, dpToPixel(zone.left), dpToPixel(zone.top), null);
    }

    public void init()
    {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(FileManager.PICTURE_PATH+File.separator+pictureName, bmOptions);
        int scaleFactorPhoto = Math.min(bmOptions.outWidth / mainGamePanel.getWidth(), bmOptions.outHeight / mainGamePanel.getHeight());
        BitmapFactory.decodeFile(FileManager.THRESHOLD_PATH+File.separator+pictureName, bmOptions);
        int scaleFactorContours = Math.min(bmOptions.outWidth / mainGamePanel.getWidth(), bmOptions.outHeight / mainGamePanel.getHeight());

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactorPhoto;

        photoOriginal = BitmapFactory.decodeFile(FileManager.PICTURE_PATH+ File.separator+pictureName, bmOptions);

        bmOptions.inSampleSize = scaleFactorContours;

        contours = BitmapFactory.decodeFile(FileManager.THRESHOLD_PATH+ File.separator+pictureName, bmOptions);

        density = mainGamePanel.getDensity();
        zone = resizeKeepRatio(contours.getWidth(), contours.getHeight(), mainGamePanel.getWidth(), mainGamePanel.getHeight());
        contours = Bitmap.createScaledBitmap(contours, zone.width(), zone.height(), true);
        photoOriginal=Bitmap.createScaledBitmap(photoOriginal, zone.width(), zone.height(), true);

        height = pixelToDp(mainGamePanel.getHeight());
        width = pixelToDp(mainGamePanel.getWidth());
        obstacles = new pix [width][height];

        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++) {

                obstacles[i][j] = pix.VIDE;
            }
        }

        for(int i = 0; i < contours.getWidth(); i++)
        {
            for(int j = 0; j < contours.getHeight(); j++)
            {
                if(contours.getPixel(i,j) != Color.WHITE)
                    obstacles[pixelToDp(i+zone.left)][pixelToDp(j+zone.top)] = pix.GROUND;
            }
        }

        zone = new Rect(pixelToDp(zone.left), pixelToDp(zone.top), pixelToDp(zone.right), pixelToDp(zone.bottom));


    }

    public Rect resizeKeepRatio(int previousWidth, int previousHeight, int maxWidth, int maxHeight)
    {

        Log.d(TAG, "previousWidth : " + previousWidth + "; previousHeight : " + previousHeight + "; maxWidth : " + maxWidth + "; maxHeight : " + maxHeight + ";");
        Rect newDimensions;
        float ratioWidth;
        float ratioHeight;

        ratioWidth = (float)maxWidth/(float)previousWidth;
        ratioHeight = (float)maxHeight/(float)previousHeight;

        Log.d(TAG, "ratioWidth : " +ratioWidth+ "; ratioHeight : "+ratioHeight+";");



        Log.d(TAG, "(2) ratioWidth : " + ratioWidth + "; (2) ratioHeight : " + ratioHeight + ";");

        if(ratioWidth<ratioHeight)
        {
            newDimensions = new Rect(0, (int)((maxHeight-previousHeight*ratioWidth)/2), maxWidth, (int)((maxHeight+previousHeight*ratioWidth/2)));
        }
        else
        {
            newDimensions = new Rect((int)((maxWidth-previousWidth*ratioHeight)/2), 0, (int)((maxWidth+previousWidth*ratioHeight)/2), maxHeight);
        }

        Log.d(TAG,"Top : " +newDimensions.top+"; Left : " +newDimensions.left+"; Right : " +newDimensions.right+"; Bottom : " +newDimensions.bottom+";");
        return newDimensions;
    }


    public int dpToPixel(int dp)
    {
        return (int)(dp*density);
    }

    public int pixelToDp(int pixel)
    {
        return (int)(pixel/density);
    }


    public int left(){ return zone.left;}
    public int right(){ return zone.right;}
    public int top(){ return zone.top;}
    public int bottom(){ return zone.bottom;}
    public int width(){ return zone.width();}
    public int height(){ return zone.height();}
    public int getScreenWidth() {return width;}
    public int getScreenHeigth() {return height;}
    public pix[][] getObstacles(){return obstacles;}

}
