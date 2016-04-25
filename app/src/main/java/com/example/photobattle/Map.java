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
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by Valentin on 29/02/2016.
 * La map. Comme quoi, c'est bien pensé.
 */
enum pix {VIDE, GROUND};

public class Map implements Serializable {
    private static final String TAG = Map.class.getSimpleName();

    private transient Bitmap contours;
    private transient Bitmap photoOriginal;
    private Rect zone;
    private pix obstacles [][];
    private int width;
    private int height;
    private int widthPi;
    private int heightPi;
    private static float density;
    private String pictureName;
    byte[] bytePicture;
    byte[] byteContours;
    //    BitmapFactory.Options bmOptions;
//    private static ByteBuffer dst1;
//    private static byte[] bytesar1;
//    private static ByteBuffer dst2;
//    private static byte[] bytesar2;
//    private static ByteBuffer dst1r;
//    private static byte[] bytesar1r;
//    private static ByteBuffer dst2r;
//    private static byte[] bytesar2r;
    public Map(String pictureName)
    {
        this.pictureName = pictureName;
        contours =  BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+pictureName
                ,  1080);
        photoOriginal =BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+pictureName
                ,  1080);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        contours.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteContours = stream.toByteArray();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        photoOriginal.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        bytePicture = stream.toByteArray();
        width = contours.getWidth();
        height = contours.getHeight();
        obstacles = new pix [width][height];

        for(int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++) {

                obstacles[i][j] = pix.VIDE;
            }
        }
        int p = 0;
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                if(contours.getPixel(i,j) != Color.WHITE) {
                    obstacles[i][j] = pix.GROUND;
                    p++;
                }
            }
        }
        System.out.println(p);

    }



    public Bitmap getContours() {
        return contours;
    }




    public pix[][] getObstacles(){return obstacles;}


    public void convert() {
        this.contours = BitmapFactory.decodeByteArray(byteContours, 0, byteContours.length);
        this.photoOriginal = BitmapFactory.decodeByteArray(bytePicture, 0, bytePicture.length);
    }

    public Bitmap getPhotoOriginal() {
        return photoOriginal;
    }

    //    private void writeObject(ObjectOutputStream out) throws IOException {
//
//////        out.writeChars(pictureName);
////
////        out.writeInt(contours.getRowBytes());
////        out.writeInt(contours.getHeight());
////        out.writeInt(contours.getWidth());
////
////
////
////
////        int bmSize = contours.getRowBytes() * contours.getHeight();
////       if(dst1==null || bmSize > dst1.capacity())
////           dst1= ByteBuffer.allocate(bmSize);
////
////        out.writeInt(dst1.capacity());
////
////        dst1.position(0);
////
////        contours.copyPixelsToBuffer(dst1);
////        if(bytesar1==null || bmSize > bytesar1.length)
////            bytesar1=new byte[bmSize];
////
////        dst1.position(0);
////        dst1.get(bytesar1);
////
////
////        out.write(bytesar1, 0, bytesar1.length);
//
//
//
//
//
//        out.writeInt(contours.getRowBytes());
//        out.writeInt(contours.getHeight());
//        out.writeInt(contours.getWidth());
//        int bmSize = contours.getRowBytes() * contours.getHeight();
//        if(dst2==null || bmSize > dst2.capacity())
//            dst2= ByteBuffer.allocate(bmSize);
//
//        out.writeInt(dst2.capacity());
//
//        dst2.position(0);
//
//        contours.copyPixelsToBuffer(dst2);
//        if(bytesar2r==null || bmSize > bytesar2r.length)
//            bytesar2r=new byte[bmSize];
//
//        dst2.position(0);
//        dst2.get(bytesar2r);
//
//
//        out.write(bytesar2r, 0, bytesar2r.length);
//
//    }

//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
//
////        pictureName=in.read();
//
//
////        int nbRowBytes=in.readInt();
////        int height=in.readInt();
////        int width=in.readInt();
////
////        int bmSize=in.readInt();
////        System.out.println(bmSize);
////
////
////        if(bytesar2r==null || bmSize > bytesar2r.length)
////            bytesar2r= new byte[bmSize];
////
////
////        int offset=0;
////
////        while(in.available()>0){
////            offset+= in.read(bytesar2r, offset, in.available());
////        }
////        in.read(bytesar2r, offset, bmSize-offset);
////
////
////        if(dst2r==null || bmSize > dst2r.capacity())
////            dst2r= ByteBuffer.allocate(bmSize);
////        dst2r.position(0);
////        dst2r.put(bytesar2r);
////        dst2r.position(0);
////        contours=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
////        contours.copyPixelsFromBuffer(dst2r);
//
//
//
//        int nbRowBytes=in.readInt();
//        int height=in.readInt();
//        int width=in.readInt();
//
//        int bmSize=in.readInt();
//
//
//
//        if(bytesar1r==null || bmSize > bytesar1r.length)
//            bytesar1r= new byte[bmSize];
//
//
//        int offset=0;
//
//        while(in.available()>0){
//            offset+= in.read(bytesar1r, offset, in.available());
//        }
//
//
//        if(dst1r==null || bmSize > dst1r.capacity())
//            dst1r= ByteBuffer.allocate(bmSize);
//        dst1r.position(0);
//        dst1r.put(bytesar1r);
//        dst1r.position(0);
//        contours=Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
//        contours.copyPixelsFromBuffer(dst1r);
//        //in.close();
//    }

}
