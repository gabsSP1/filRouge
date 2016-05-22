package com.example.photobattle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

public class EditActivity extends BaseActivity {



	Bitmap background;
	Bitmap original;
	File fbackground;
	Button back;
	Button eraser;
	Button position;
	Button reset;
	Button clean;
	LinearLayout l;
	EditText mapName;
	ImageView p;
	boolean erasemode = false;
	boolean positionmode = false;
	int pos = 0;
	SeekBar s1;
	SeekBar sizeEraser;
	int siz;
	int[] location = new int[2];
	String dataToWrite = "";
	String coordGomme = "";
	int xmap;
	int ymap;
	int widthmap;
	int heightmap;
	int xposJ1;
	int yposJ1;
	int xposJ2;
	int yposJ2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		FullScreencall();
		setContentView(R.layout.activity_edit);
		Intent intent = getIntent();
		final Animation anim= AnimationUtils.loadAnimation(this, R.anim.anim_button);
		if (intent != null) {
			fbackground = new File(intent.getStringExtra("selected_file"));
			background = BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+fbackground.getName()
					, BazarStatic.reqHeight );
			original =  BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+fbackground.getName()
					,  BazarStatic.reqHeight);
		}
		eraser = (Button) findViewById(R.id.eraser_edit);
		eraser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(anim);
				Toast.makeText(EditActivity.this, "Mode gomme", Toast.LENGTH_SHORT).show();
				erasemode = true;
				if(positionmode)
					positionmode  = false;
			}
		});
		back=(Button) findViewById(R.id.back_edit);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(anim);
				Sound.playSound(getApplicationContext(), R.raw.open);
				showDialog();
			}
		});
		clean = (Button) findViewById(R.id.clean);
		clean.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(anim);
				try{
					coordGomme = ReadSettings(EditActivity.this);
					background = doFilter(s1.getProgress(), s1.getProgress() + 50, original);
					dataToWrite = coordGomme;
					ReadSettings(getApplicationContext());
					p.setImageBitmap(background);
				}
				catch(Exception e){

				}


			}
		});
		position = (Button) findViewById(R.id.pos1);
		position.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(anim);
				Toast.makeText(EditActivity.this, "Mode positionnement des personnages", Toast.LENGTH_SHORT).show();
				positionmode = true;
				if(erasemode)
					erasemode  = false;
			}
		});
		reset = (Button) findViewById(R.id.reset);

		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(anim);
				s1.setProgress(100);
				dataToWrite = "";
				background = doFilter(100,150, original);
				p.setImageBitmap(background);

			}
		});
		p = (ImageView) findViewById(R.id.pictureEdit);
		p.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float eventX = event.getX();
				float eventY = event.getY();
				float[] eventXY = new float[]{eventX, eventY};

				Matrix invertMatrix = new Matrix();
				p.getImageMatrix().invert(invertMatrix);

				invertMatrix.mapPoints(eventXY);
				int x = Integer.valueOf((int) eventXY[0]);
				int y = Integer.valueOf((int) eventXY[1]);


				Drawable imgDrawable = p.getDrawable();
				Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();



				//Limit x, y range within bitmap
				if (x < 0) {
					x = 0;
				} else if (x > bitmap.getWidth() - 1) {
					x = bitmap.getWidth() - 1;
				}

				if (y < 0) {
					y = 0;
				} else if (y > bitmap.getHeight() - 1) {
					y = bitmap.getHeight() - 1;
				}
				if(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {

					if (erasemode && !positionmode) {
						siz = sizeEraser.getProgress();
						background = erase(x, y, background);
						dataToWrite += x + "\r\n" + y + "\r\n" + sizeEraser.getProgress() + "\r\n";
					}

				}

				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
				if (positionmode && !erasemode) {
					if (pos % 2 == 1) {
						xposJ2 = x;
						yposJ2 = y;
						pos++;
						Toast.makeText(EditActivity.this, "Position joueur 1 enregistrée", Toast.LENGTH_SHORT).show();
					} else {
						xposJ1 = x;
						yposJ1 = y;
						pos++;
						Toast.makeText(EditActivity.this, "Position joueur 2 enregistrée", Toast.LENGTH_SHORT).show();
					}
				}
				}

				return true;

			}
		});
		mapName = (EditText) findViewById(R.id.mapName);
		mapName.setTextColor(Color.BLUE);
		mapName.setText(fbackground.getName().substring(0, fbackground.getName().indexOf('.')));
		mapName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub7
				return false;
			}
		});
		s1= (SeekBar) findViewById(R.id.param1);
		sizeEraser= (SeekBar) findViewById(R.id.sizing);
		s1.setMax(500);
		sizeEraser.setMax(200);



		sizeEraser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				siz = sizeEraser.getProgress();
			}
		});
		s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				background = original.copy(Bitmap.Config.ARGB_8888, true);
				background = doFilter(s1.getProgress(), s1.getProgress() + 50, background);
				int d = 0;

				try{
					boolean a = erasemode;
					erasemode = true;
					int s = sizeEraser.getProgress();
					int v = s1.getProgress();
					coordGomme = ReadSettings(EditActivity.this);
					s1.setProgress(v);
					sizeEraser.setProgress(s);
					erasemode = a;

				}
				catch (Exception e){
					Toast.makeText(EditActivity.this, "pas encore de sauvegarde",Toast.LENGTH_SHORT).show();
				}

				try{
					int s = sizeEraser.getProgress();
					int v = s1.getProgress();
					syncEraser(dataToWrite);
					s1.setProgress(v);
					sizeEraser.setProgress(s);
				}
				catch (Exception e){
					Toast.makeText(EditActivity.this, "dt contient:"+dataToWrite,Toast.LENGTH_SHORT).show();
				}

				p.setImageBitmap(background);

			}
		});

		try{
			coordGomme = ReadSettings(this);
			dataToWrite = coordGomme;
			Toast.makeText(this,coordGomme,Toast.LENGTH_SHORT).show();
		}
		catch (Exception e){
			Toast.makeText(this, "je peux pas lire les fichiers",Toast.LENGTH_SHORT).show();
		}
		sizeEraser.setProgress(100);
		p.setImageBitmap(background);
		//ReadSettings(this);
	}


	private void showDialog() throws Resources.NotFoundException {
		new AlertDialog.Builder(this)
				.setTitle("Save Changes")
				.setMessage("Do you want to save your changes \n It will erase your HighScore !")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						(new File(FileManager.DATA_PATH + File.separator +mapName.getText().toString()+".txt")).delete();
						String data = "";
						data += s1.getProgress()+"\r\n";
						data += xposJ1+"\r\n"+yposJ1+"\r\n"+xposJ2+"\r\n"+yposJ2+"\r\n";
						//data += coordGomme;
						data += dataToWrite;
						WriteSettings(EditActivity.this, data);
						ReadSettings(EditActivity.this);
						File dat = new File(FileManager.DATA_PATH, fbackground.getName()+".dat");
						File picture = new File(FileManager.PICTURE_PATH, fbackground.getName());
						File n = (new File(FileManager.THRESHOLD_PATH, mapName.getText().toString() + ".jpg"));
						if (!n.exists()) {
							dat.renameTo(new File(FileManager.DATA_PATH, mapName.getText().toString() + ".jpg.dat"));
							fbackground.renameTo(n);
							picture.renameTo(new File(FileManager.PICTURE_PATH, mapName.getText().toString() + ".jpg"));

						}
						FileManager.saveBitmap(background, FileManager.THRESHOLD_PATH, fbackground.getName());
						EditActivity.this.finish();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								EditActivity.this.finish();
							}
						}
				).
				show();
	}
	public void FullScreencall() {
		if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if(Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
	public Bitmap doFilter(int x, int y, Bitmap mt){
		Mat mImg = new Mat();
		Utils.bitmapToMat(mt, mImg);
		Mat edges = new Mat();
		Imgproc.Canny(mImg, edges, s1.getProgress(), s1.getProgress()+50);
//		mt.recycle();
		mt = Bitmap.createBitmap(mImg.cols(), mImg.rows(),Bitmap.Config.ARGB_8888);
		Mat invertcolormatrix = new Mat(edges.rows(),edges.cols(), edges.type(), new Scalar(255, 255, 255));
		Core.subtract(invertcolormatrix, edges, edges);
		Utils.matToBitmap(edges, mt);
		return mt;
	}
	public Bitmap erase(int x, int y, Bitmap bm) {
		if(sizeEraser.getProgress() == 0)
			sizeEraser.setProgress(50);
		Bitmap mut = bm.copy(Bitmap.Config.ARGB_8888, true);
		for(int i  = -sizeEraser.getProgress()/2 ; i < sizeEraser.getProgress()/2 ; i++){
			for(int j  = -sizeEraser.getProgress()/2 ; j < sizeEraser.getProgress()/2 ; j++){
				if(x+i < mut.getWidth() && y + j < mut.getHeight() && x+i > 0 && y+j > 0)
					mut.setPixel(x+i,y+j,Color.WHITE);
			}
		}
		p.setImageBitmap(mut);
		return mut;
	}
	public void WriteSettings(Context context, String data){
		File dat = new File(FileManager.DATA_PATH, mapName.getText().toString()+".jpg.dat");
		try {
			FileWriter fileWriter = new FileWriter(dat) ;
			fileWriter.append(data);
			fileWriter.flush();
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String ReadSettings(Context context){
		File dat = new File(FileManager.DATA_PATH, mapName.getText().toString()+".jpg.dat");
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
				if((line = br.readLine()) != null){
					s1.setProgress(Integer.parseInt(line));
				}
				else
				{
					s1.setProgress(100);
				}
				if((line = br.readLine()) != null){
					xposJ1 = Integer.parseInt(line);
				}
				if((line = br.readLine()) != null){
					yposJ1 = Integer.parseInt(line);
				}
				if((line = br.readLine()) != null){
					xposJ2 = Integer.parseInt(line);
				}
				if((line = br.readLine()) != null){
					yposJ2 = Integer.parseInt(line);
				}
				coordGomme ="";
				line = "";
				while ((line = br.readLine()) != null && line != " ") {
					//System.out.println("deb-" + line + "-fin");
					coordGomme+=Integer.parseInt(line)+"\r\n";
					int x = Integer.parseInt(line);
					line = br.readLine();
					//System.out.println("deb-" +line+"-fin");
					coordGomme+=Integer.parseInt(line)+"\r\n";
					int y = Integer.parseInt(line);
					line = br.readLine();
					//System.out.println("deb-" +line+"-fin");
					coordGomme+=+Integer.parseInt(line)+"\r\n";
					int size = Integer.parseInt(line);
					sizeEraser.setProgress(size);
					background = erase(x,y,background);
				}

				in.close();


				//affiche le contenu de mon fichier dans un popup surgissant
				//Toast.makeText(context, " precision" + s1.getProgress() +" x1" + xposJ1 +" y1" + xposJ1 +" x2" + xposJ2 +" y2" + yposJ2 +"  "+coordGomme, Toast.LENGTH_SHORT).show();

			}  catch (IOException e) {
				e.printStackTrace();
			}
		}



		return coordGomme;
	}
	public void syncEraser(String c){
		if(c != null) {
			BufferedReader reader = new BufferedReader(new StringReader(c));
			try{
				String line = reader.readLine();
				while (line != null && line != " " && line != "" ) {
					int x = Integer.parseInt(line);
					line = reader.readLine();
					int y = Integer.parseInt(line);
					line = reader.readLine();
					int size = Integer.parseInt(line);
					sizeEraser.setProgress(size);
					line = reader.readLine();
					background = erase(x,y,background);
				}

			}
			catch (Exception e){

			}

		}
	}



}