package com.example.photobattle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
//a
public class Map extends Activity {
	ListView s;
	HashMap<String, File> fichierJpeg;
	HashMap<String, Bitmap> thumbnail;
	ActionMode mActionMode;
	File currentSelectionFile;
	ImageView imageSelected;
	Button bDelete;
	Button importPicture;
	Button takePicture;
	Button edit;
	File nf;
	final static String CURRENT_FILE="selcted_file";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);
		createDirectory("photoBattle");
		createDirectory("photoBattle" +
                File.separator+ "Pictures");
		createDirectory(
                "photoBattle" +
                File.separator+ "Thumbnail");
		createDirectory(
                "photoBattle" +
                File.separator+ "Contours");
		thumbnail=new  HashMap<String, Bitmap>();
		 fichierJpeg=new  HashMap<String, File>();
		bDelete= (Button) findViewById(R.id.button_delete);
		imageSelected=(ImageView)   findViewById(R.id.imageSelected);
		s=(ListView) findViewById(R.id.scroll);
		s.setOnItemClickListener(new OnItemClickListener() {

		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
		        
		        imageSelected.setImageBitmap(BitmapFactory.decodeFile(fichierJpeg.get((String)parent.getItemAtPosition(position)).getAbsolutePath()));
		        currentSelectionFile=fichierJpeg.get((String)parent.getItemAtPosition(position));
		        view.setSelected(true);
		    }
		});	
		
		
		
		bDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currentSelectionFile!=null)
				{
					File p=new File(Environment.getExternalStorageDirectory() +
			                File.separator+ "photoBattle" +
			                File.separator+ "Thumbnail"+
			                File.separator+currentSelectionFile.getName());
					p.delete();
					currentSelectionFile.delete();
					loadList();
					
				}
			}
			
		});
		
		edit=(Button) findViewById(R.id.button_edit);
		edit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currentSelectionFile!=null)
				{
					Intent intentMyAccount = new Intent(getApplicationContext(), EditActivity.class);
					intentMyAccount.putExtra("selected_file", currentSelectionFile.getAbsolutePath());
			        startActivity(intentMyAccount);
				}
			}
			
		});
		
		importPicture= (Button) findViewById(R.id.import_picture);
		importPicture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 89);
			}
			
		});
		
		takePicture= (Button) findViewById (R.id.take_picture);
		takePicture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dispatchTakePictureIntent();
			}
			});
		//Récupération des fichiers jpeg dans le dossier
		
		loadList();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.layout.activity_menu_app, menu);
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
	            photoFile = createImageFile("photoBattle"+File.separator+"Pictures", createPictureName());
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, 55);
	            nf=photoFile;
	        }
	    }
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	  // TODO Auto-generated method stub
	     super.onActivityResult(requestCode, resultCode, data);

	     if (requestCode == 89){
	    	  savebitmap(data.getData());
	      }
	     if(nf!=null)
	     {
	    	 createThumbnail(nf.getAbsolutePath());
	     }
	     loadList();
	        
	}
	
	 private void savebitmap(Uri targetUri) {
	      OutputStream outStream = null;
	      
	      File file;
		try {
			file = createImageFile("photoBattle"+File.separator+"Pictures", createPictureName());
	         // make a new bitmap from your file
	         Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
	         outStream = new FileOutputStream(file);
	         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	         outStream.flush();
	         outStream.close();
	         createThumbnail(file.getAbsolutePath());
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

	   }
	 
	 
	 private File createImageFile(String rPath, String imageFileName) throws IOException {
		    // Create an image file name
		    File myDir = new File(Environment.getExternalStorageDirectory() +
	                File.separator + rPath); //pour créer le repertoire dans lequel on va mettre notre fichier
			boolean success=createDirectory(rPath);
			
			if (success)
			{
	         
			 File image = new File(myDir.getAbsolutePath()+ File.separator +imageFileName);
			 return image;
			}
			else 
			{
				Log.e("TEST1","ERROR DE CREATION DE DOSSIER");
			}
			return null;
				    
		}
	 
	 void createThumbnail(String path)
	 {
		 Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		 Bitmap bmp = Bitmap.createBitmap(100, 60, conf); // this creates a MUTABLE bitmap
		 Canvas canvas = new Canvas(bmp);
		 Bitmap imageBitmap=BitmapFactory.decodeFile(path);
		 int height=60;
		 int width=(int)(imageBitmap.getWidth()*((double)height/imageBitmap.getHeight()));
         imageBitmap=getResizedBitmap(imageBitmap, width, height);
         canvas.drawBitmap(imageBitmap, (int)(100-width)/2, 0, null);
         
         OutputStream outStream = null;
	      
	      File file;
         try {
        	 file = createImageFile("photoBattle"+File.separator+"Thumbnail", new File(path).getName());
			outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	         outStream.flush();
	         outStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
	 }
	 
	 boolean createDirectory(String rPath)
	 {
		 File myDir = new File(Environment.getExternalStorageDirectory() +
	                File.separator + rPath); //pour créer le repertoire dans lequel on va mettre notre fichier
		 boolean success =true;
		 if (!myDir.exists()) 
			{
			success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
			}
		 return success;
	 }
	 
	 String createPictureName()
	 {
		 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		 String imageFileName = "JPEG_" + timeStamp + ".jpg";	
		 return imageFileName;
	 }
	 
	 public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
	 
	 void loadList()
	 {
		 s.removeAllViewsInLayout();
		 File f2 = new File(Environment.getExternalStorageDirectory() +
	                File.separator + "photoBattle" +
	                File.separator + "Pictures");
		 File[] fichiers = f2.listFiles();
		 thumbnail.clear();
		 fichierJpeg.clear();
		 for(int i=0; i<fichiers.length;i++)
		 {
			String ext="";
			int k=fichiers[i].getName().length()-1;
			char p=fichiers[i].getName().charAt(k);
			while(p!='.' && k>0)
			{
				ext+=p;
				k--;
				p=fichiers[i].getName().charAt(k);
			}
			 if(ext.equals("gpj"))
			 {
				 fichierJpeg.put(fichiers[i].getName(),fichiers[i]);
	            Bitmap imageBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
		                File.separator + "photoBattle" +
		                File.separator + "Thumbnail"+File.separator+fichiers[i].getName());
	            thumbnail.put(fichiers[i].getName(), imageBitmap);

			 }
			 
		 }
		
		 Bitmap list2[] = new Bitmap[fichierJpeg.size()];
		 String list3[] = new String[fichierJpeg.size()];
		 MonAdaptateurDeListe adapter =new MonAdaptateurDeListe(this, thumbnail.keySet().toArray(list3), thumbnail.values().toArray(list2));
		 s.setAdapter(adapter);
	 }
	 
	 void restart()
	 {
		 startActivity(getIntent());
		 finish(); 
	 }
	public void onRestart(){
		super.onRestart();
		loadList();
	}
	 
}
