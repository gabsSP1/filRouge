package com.photogame.imarena;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by gab on 08/03/2016.
 */
public class FileManager {

    public static final String GENERAL_PATH= Environment.getExternalStorageDirectory() + File.separator +"photoBattle";
    public static final String PICTURE_PATH= GENERAL_PATH+ File.separator +"Pictures";
    public static final String THRESHOLD_PATH= GENERAL_PATH+ File.separator +"Threshold";
    public static final String DATA_PATH = GENERAL_PATH+ File.separator +"Data";
    public static final String GAME_PICTURE_PATH = GENERAL_PATH+ File.separator +"GamePicture";
    public static final String SCORE_PATH= GENERAL_PATH+ File.separator +"Scores";
    private static int nbMap;


    public static void clearFiles()
    {

    }


    public static void saveScoreSolo(int i)
    {
        File dat = new File(FileManager.SCORE_PATH,BazarStatic.map.getName()+".txt");
        try {
            FileWriter fileWriter = new FileWriter(dat) ;
            fileWriter.append(Integer.toString(i));
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialyzeTreeFile()
    {
        createDirectory(SCORE_PATH);
        createDirectory(GENERAL_PATH);
        createDirectory(PICTURE_PATH);
        createDirectory(THRESHOLD_PATH);
        createDirectory(DATA_PATH);
        createDirectory(GAME_PICTURE_PATH);
    }

    //Create a directory to the indicated path
    public static boolean createDirectory(String rPath)
    {
        File myDir = new File(rPath); //pour créer le repertoire dans lequel on va mettre notre fichier
        boolean success =true;
        if (!myDir.exists())
        {
            success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        return success;
    }

    //gère l'import de photo
    public static void saveBitmap(Bitmap bmp, String path, String name) {
        OutputStream outStream = null;

        File file;
        try {
            file = new File(path + File.separator + name);
            //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/*
    //create a file on the indicated path with te indicated name
    public static File createImageFile(String rPath,  name) throws IOException
    {
        // Create an image file name
        setNbMap(nbMap+1);
        return image;
    }

    public static void setNbMap(int nbMaps)
    {
        nbMap=nbMaps;
        pictureName="map"+nbMap+".jpg";
    }
*/
}
