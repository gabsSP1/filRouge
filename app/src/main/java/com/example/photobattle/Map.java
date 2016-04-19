package com.example.photobattle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by Valentin on 29/02/2016.
 * La map. Comme quoi, c'est bien pensé.
 */
enum pix {VIDE, GROUND};

public class Map implements Serializable {
    private static final String TAG = Map.class.getSimpleName();

    private Bitmap contours;
    private Bitmap photoOriginal;
    private Rect zone;
    private pix obstacles [][];
    private int width;
    private int height;
    private float density;
    private String pictureName;
    BitmapFactory.Options bmOptions;
    private static ByteBuffer dst1;
    private static byte[] bytesar1;
    private static ByteBuffer dst2;
    private static byte[] bytesar2;
    private static ByteBuffer dst1r;
    private static byte[] bytesar1r;
    private static ByteBuffer dst2r;
    private static byte[] bytesar2r;
    public Map(String pictureName)
    {
        this.pictureName = pictureName;
        contours = BitmapFactory.decodeFile(FileManager.THRESHOLD_PATH+ File.separator+pictureName);
        photoOriginal = BitmapFactory.decodeFile(FileManager.PICTURE_PATH+ File.separator+pictureName);
    }


    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(contours, dpToPixel(zone.left), dpToPixel(zone.top), null);
    }

    public void init(MainGamePanel mainGamePanel)
    {



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
            newDimensions = new Rect(0, (int)((maxHeight-previousHeight*ratioWidth)/2), maxWidth, (int)((maxHeight+previousHeight*ratioWidth)/2));
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


    private void writeObject(ObjectOutputStream out) throws IOException {

//        out.writeChars(pictureName);

        out.writeInt(contours.getRowBytes());
        out.writeInt(contours.getHeight());
        out.writeInt(contours.getWidth());




        int bmSize = contours.getRowBytes() * contours.getHeight();
       if(dst1==null || bmSize > dst1.capacity())
           dst1= ByteBuffer.allocate(bmSize);

        out.writeInt(dst1.capacity());

        dst1.position(0);

        contours.copyPixelsToBuffer(dst1);
        if(bytesar1==null || bmSize > bytesar1.length)
            bytesar1=new byte[bmSize];

        dst1.position(0);
        dst1.get(bytesar1);


        out.write(bytesar1, 0, bytesar1.length);





        out.writeInt(photoOriginal.getRowBytes());
        out.writeInt(photoOriginal.getHeight());
        out.writeInt(photoOriginal.getWidth());
        bmSize = photoOriginal.getRowBytes() * photoOriginal.getHeight();
        if(dst2==null || bmSize > dst2.capacity())
            dst2= ByteBuffer.allocate(bmSize);

        out.writeInt(dst2.capacity());

        dst2.position(0);

        photoOriginal.copyPixelsToBuffer(dst2);
        if(bytesar2r==null || bmSize > bytesar2r.length)
            bytesar2r=new byte[bmSize];

        dst2.position(0);
        dst2.get(bytesar2r);


        out.write(bytesar2r, 0, bytesar2r.length);

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{

//        pictureName=in.read();


        int nbRowBytes=in.readInt();
        int height=in.readInt();
        int width=in.readInt();

        int bmSize=in.readInt();
        System.out.println(bmSize);


        if(bytesar2r==null || bmSize > bytesar2r.length)
            bytesar2r= new byte[bmSize];


        int offset=0;

        while(offset < bmSize-in.available()){
            offset+= in.read(bytesar2r, offset, in.available());
        }
        in.read(bytesar2r, offset, bmSize-offset);


        if(dst2r==null || bmSize > dst2r.capacity())
            dst2r= ByteBuffer.allocate(bmSize);
        dst2r.position(0);
        dst2r.put(bytesar2r);
        dst2r.position(0);
        contours=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        contours.copyPixelsFromBuffer(dst2r);



         nbRowBytes=in.readInt();
         height=in.readInt();
         width=in.readInt();

         bmSize=in.readInt();



        if(bytesar1r==null || bmSize > bytesar1r.length)
            bytesar1r= new byte[bmSize];


         offset=0;

        while(in.available()>0){
            offset+= in.read(bytesar1r, offset, in.available());
        }


        if(dst1r==null || bmSize > dst1r.capacity())
            dst1r= ByteBuffer.allocate(bmSize);
        dst1r.position(0);
        dst1r.put(bytesar1r);
        dst1r.position(0);
        photoOriginal=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        photoOriginal.copyPixelsFromBuffer(dst1r);
        //in.close();
    }

}
