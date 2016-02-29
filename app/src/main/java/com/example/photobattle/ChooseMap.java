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
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.widget.Toast;
//a
public class ChooseMap extends Activity {
	ListView s;
	HashMap<String, File> fichierJpeg;
	HashMap<String, Bitmap> thumbnail;
	ActionMode mActionMode;
	File currentSelectionFile;
    private ImageView image;
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
                    (new File(Environment.getExternalStorageDirectory() +
                            File.separator + "photoBattle" +
                            File.separator + "Contours"+File.separator+currentSelectionFile.getName())).delete();
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
					intentMyAccount.putExtra("selected_file", Environment.getExternalStorageDirectory()+File.separator +"photoBattle"+File.separator+"Contours"+File.separator+currentSelectionFile.getName());
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
    //gère la prise de la photo
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
	    /* Toast toast = Toast.makeText(getApplicationContext()
	    		 , Integer.toString(resultCode), Toast.LENGTH_SHORT);
	    		 					toast.show();*/
	     if (requestCode == 89 && data!=null){
	    	  savebitmap(data.getData());
	      }
	     if(nf!=null)
	     {
		     if(nf.getTotalSpace()>20)
		     {
		    	 createThumbnail(nf.getAbsolutePath());
                 createImageContours(nf.getAbsolutePath());
		     }
		     else
		     {
		    	 nf.delete();
		     }
		     nf=null;
	     }
	     loadList();
	        
	}


    //gère l'import de photo
	 private void savebitmap(Uri targetUri) {
	      OutputStream outStream = null;
	      
	      File file;
		try {
			file = createImageFile("photoBattle"+File.separator+"Pictures", createPictureName());
	         Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
	         outStream = new FileOutputStream(file);
	         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	         outStream.flush();
	         outStream.close();
	         createThumbnail(file.getAbsolutePath());
            createImageContours(file.getAbsolutePath());
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

	   }
	 
	 //create a file on the indicated path with te indicated name
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


    //Create a thumbnail in the directory /thumbnail of the file indicated by the parameter path
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


    //Create a directory to the indicated path
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


    //Create a name of file that doesn't already exists
	 String createPictureName()
	 {
		 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		 String imageFileName = "JPEG_" + timeStamp + ".jpg";	
		 return imageFileName;
	 }


    //resize a bitmap (bad quality, ideal to make light thumbnail image of a bigger picture)
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


    //Load the list of the picture
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
				 if(fichiers[i].getTotalSpace()>20)
				 {
				 fichierJpeg.put(fichiers[i].getName(),fichiers[i]);
	            Bitmap imageBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
		                File.separator + "photoBattle" +
		                File.separator + "Thumbnail"+File.separator+fichiers[i].getName());
	            thumbnail.put(fichiers[i].getName(), imageBitmap);
				 }
				 else
				 {
					 (new File(Environment.getExternalStorageDirectory() +
				                File.separator + "photoBattle" +
				                File.separator + "Thumbnail"+File.separator+fichiers[i].getName())).delete();
                     (new File(Environment.getExternalStorageDirectory() +
                             File.separator + "photoBattle" +
                             File.separator + "Contours"+File.separator+fichiers[i].getName())).delete();
					 fichiers[i].delete();
					 
				 }

			 }
			 imageSelected.setImageBitmap(null);
			 
		 }
		
		 Bitmap list2[] = new Bitmap[fichierJpeg.size()];
		 String list3[] = new String[fichierJpeg.size()];
		 MonAdaptateurDeListe adapter =new MonAdaptateurDeListe(this, thumbnail.keySet().toArray(list3), thumbnail.values().toArray(list2));
		 s.setAdapter(adapter);
	 }

	public void onRestart(){
		super.onRestart();
		loadList();
	}



	//Partie de Hugo Monyac

    void createImageContours(String path)
    {
        Bitmap bm=BitmapFactory.decodeFile(path);
        bm=grayScale(bm);
        bm=chgtoBandW(bm);
        OutputStream outStream = null;

        File file;
        try {
            file = createImageFile("photoBattle" +
                    File.separator+ "Contours", new File(path).getName());
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


	Bitmap chgtoBandW(Bitmap img){
		//img.setImageBitmap(source)
		Bitmap mapnew= Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

		int color = 0;
		int[][] bmx= new int[img.getWidth()][img.getHeight()];
		for(int i=0;i<img.getWidth();i++)
		{
			for(int j= 0;j<img.getHeight();j++)
			{
				int colorOfPixel = img.getPixel(i, j);
				mapnew.setPixel(i,j,colorOfPixel);

			}
		}
		mapnew = DoFullFilter(mapnew);
        return mapnew;
	}

	public Bitmap DoHorizontalFilter(Bitmap BitmapGray){
		int w, h, threshold;

		h= BitmapGray.getHeight();
		w= BitmapGray.getWidth();

		threshold = 100;

		Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);

		for(int x = 0; x < w-1; ++x) {
			for(int y = 0; y < h; ++y) {

				int pixel = BitmapGray.getPixel(x, y);
				int pixelsuivant = BitmapGray.getPixel(x+1,y);

				int gray = (Color.red(pixel) +Color.blue(pixel)+Color.green(pixel))/3;
				int gNext = (Color.red(pixelsuivant) +Color.blue(pixelsuivant)+Color.green(pixelsuivant))/3;
				if((gray-gNext)*(gray-gNext)< threshold){
					BitmapBiner.setPixel(x, y, 0xFFFFFFFF);
				} else{
					BitmapBiner.setPixel(x, y, 0xFF000000);
				}

			}
		}
		return BitmapBiner;

	}
	public Bitmap DoFullFilter(Bitmap BitmapGray){
		int w, h, threshold;

		h= BitmapGray.getHeight();
		w= BitmapGray.getWidth();

		Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);
		Bitmap bHor = DoHorizontalFilter(BitmapGray);
		Bitmap bVer = DoVerticalFilter(BitmapGray);
		for(int x = 0; x < w; ++x) {
			for(int y = 0; y < h; ++y) {
				int horp = bHor.getPixel(x,y);
				int verp = bVer.getPixel(x,y);
				if(horp != verp){
					BitmapBiner.setPixel(x,y,0xFF000000);
				}
				else
					BitmapBiner.setPixel(x,y,0xFFFFFFFF);
			}
		}
		return BitmapBiner;

	}
	public Bitmap DoVerticalFilter(Bitmap BitmapGray){
		int w, h, threshold;

        h= BitmapGray.getHeight();
		w= BitmapGray.getWidth();

		threshold = 100;

		Bitmap BitmapBiner = Bitmap.createBitmap(BitmapGray);

		for(int x = 0; x < w; ++x) {
			for(int y = 0; y < h-1; ++y) {

				int pixel = BitmapGray.getPixel(x, y);
				int pixelsuivant = BitmapGray.getPixel(x,y+1);

				int gray = (Color.red(pixel) +Color.blue(pixel)+Color.green(pixel))/3;
				int gNext = (Color.red(pixelsuivant) +Color.blue(pixelsuivant)+Color.green(pixelsuivant))/3;
				if((gray-gNext)*(gray-gNext)< threshold){
					BitmapBiner.setPixel(x, y, 0xFFFFFFFF);
				} else{
					BitmapBiner.setPixel(x, y, 0xFF000000);
				}

			}
		}
		return BitmapBiner;

	}

    public Bitmap grayScale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
