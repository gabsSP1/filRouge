package com.example.photobattle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.camera2.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuApp extends Activity{

	File myDir;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	 
		// On met l'application en plein écran et sans barre de titre
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_menu_app);
		Button b2=(Button) findViewById(R.id.button2);
		Button b3=(Button) findViewById(R.id.button3);
		b2.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dispatchTakePictureIntent();
				
			}
			
		});
		
		b3.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        Intent intentMyAccount = new Intent(getApplicationContext(), Map.class);
		        startActivity(intentMyAccount);
				
			}
			
		});
	 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_app, menu);
		return true;
	}
	
	static final int REQUEST_TAKE_PHOTO = 1;

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	String mCurrentPhotoPath;

	
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + ".jpeg";
       
	    File myDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "photoBattle"); //pour créer le repertoire dans lequel on va mettre notre fichier
		Boolean success=true;
		if (!myDir.exists()) 
		{
		success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
		}
		if (success)
		{
         
		 File image = File.createTempFile(
			        imageFileName,  /* prefix */
			        ".jpg",         /* suffix */
			        myDir      /* directory */
			    );
		 mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		 return image;
		}
		else 
		{
			Log.e("TEST1","ERROR DE CREATION DE DOSSIER");
		}
		return null;
			    
	}
	
}
