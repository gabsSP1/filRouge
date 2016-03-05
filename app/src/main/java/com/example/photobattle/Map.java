package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Valentin on 29/02/2016.
 * La map. Comme quoi, c'est bien pensé.
 */
enum pix {VIDE, GROUND};

public class Map
{
    private static final String TAG = Map.class.getSimpleName();

    private Bitmap bm;
    private Rect zone;
    private pix obstacles [][];
    private int width;
    private int height;
    private float ratioHeight;
    private float ratioWidth;
    private MainGamePanel mainGamePanel;

    public Map(Bitmap bm, MainGamePanel mainGamePanel)
    {
        this.bm = bm;
        this.mainGamePanel = mainGamePanel;
        this.bm = bm;
    }


    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bm, (int)(zone.left/ratioWidth), (int)(zone.top/ratioHeight), null);
    }

    public void init()
    {
        float densityScreen = mainGamePanel.getDensity();
        zone = resizeKeepRatio(bm.getHeight(), bm.getHeight(), mainGamePanel.getWidth(), mainGamePanel.getHeight());
        bm = Bitmap.createScaledBitmap(bm, zone.width(), zone.height(), true);

        ratioWidth = mainGamePanel.getWidth()/(300*densityScreen);
        ratioHeight = mainGamePanel.getHeight()/(300*densityScreen);

        height = (int)(mainGamePanel.getHeight() * ratioHeight);
        width = (int)(mainGamePanel.getWidth() * ratioWidth);
        obstacles = new pix [width][height];

        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++) {

                obstacles[i][j] = pix.VIDE;
            }
        }

        for(int i = 0; i < bm.getWidth(); i++)
        {
            for(int j = 0; j < bm.getHeight(); j++)
            {
                if(bm.getPixel(i,j) != Color.WHITE)
                    obstacles[(int)((i+zone.left)*ratioWidth)][(int)((j+zone.top)*ratioHeight)] = pix.GROUND;
            }
        }

        zone = new Rect((int)(zone.left*ratioWidth), (int)(zone.top*ratioHeight), (int)(zone.right*ratioWidth), (int)(zone.bottom*ratioHeight));


    }

    private Rect resizeKeepRatio(int previousWidth, int previousHeight, int maxWidth, int maxHeight)
    {

        Log.d(TAG, "previousWidth : " + previousWidth + "; previousHeight : " + previousHeight + "; maxWidth : " + maxWidth + "; maxHeight : " + maxHeight + ";");
        Rect newDimensions;
        float ratioWidth;
        float ratioHeight;

        ratioWidth = (float)maxWidth/(float)previousWidth;
        ratioHeight = (float)maxHeight/(float)previousHeight;

        Log.d(TAG, "ratioWidth : " +ratioWidth+ "; ratioHeight : "+ratioHeight+";");

        if(ratioWidth < 1 && ratioHeight < 1)
        {
            float temp = ratioWidth;
            ratioWidth = ratioHeight;
            ratioHeight = temp;
        }

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


    public int coordXToPixel(int coordX)
    {
        return (int)(coordX/ratioWidth);
    }

    public int coordYToPixel(int coordY)
    {
        return (int)(coordY/ratioHeight);
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
