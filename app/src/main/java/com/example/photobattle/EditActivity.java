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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class EditActivity extends BaseActivity {
	Bitmap background;
	Bitmap original;
	File fbackground;
	Button back;
	Button eraser;
	Button position;
	Button pen;
	LinearLayout l;
	EditText mapName;
	ImageView p;
	boolean erasemode;
	SeekBar s1;
	SeekBar sizeEraser;
	int siz;
	int[] location = new int[2];
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
		FullScreencall();
		setContentView(R.layout.activity_edit);
		Intent intent = getIntent();
		if (intent != null) {
			fbackground = new File(intent.getStringExtra("selected_file"));
			background = BitmapFactory.decodeFile(FileManager.THRESHOLD_PATH+File.separator+fbackground.getName());
			original =  BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+fbackground.getName()
					,  BazarStatic.reqHeight);
		}
		erasemode = false;
		eraser = (Button) findViewById(R.id.eraser_edit);
		eraser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				erasemode = !erasemode;
			}
		});
		back=(Button) findViewById(R.id.back_edit);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		p = (ImageView) findViewById(R.id.pictureEdit);
		p.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("coucou");
				if(erasemode) {
					if(event.getAction() == MotionEvent.ACTION_MOVE) {
						System.out.println("x " + event.getX());
						System.out.println("y " + event.getY());
						siz = sizeEraser.getProgress();
						background = erase((int) event.getX(), (int) event.getY(), background);
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
		try{
			ReadSettings(this);
		}
		catch (Exception e){
			Toast.makeText(this, "je peux pas lire les fichiers",Toast.LENGTH_SHORT).show();
		}


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
				background = doFilter(s1.getProgress(), s1.getProgress()+50, background);
				p.setImageBitmap(background);
			}
		});

		p.setImageBitmap(background);
		//ReadSettings(this);
	}


	private void showDialog() throws Resources.NotFoundException {
		new AlertDialog.Builder(this)
				.setTitle("Save Changes")
				.setMessage("Do you want to save your changes")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String data = "";
						data += s1.getProgress()+"\r\n";
						data += sizeEraser.getProgress()+"\r\n";
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
		mt.recycle();
		mt = Bitmap.createBitmap(mImg.cols(), mImg.rows(),Bitmap.Config.ARGB_8888);
		Mat invertcolormatrix = new Mat(edges.rows(),edges.cols(), edges.type(), new Scalar(255, 255, 255));
		Core.subtract(invertcolormatrix, edges, edges);
		Utils.matToBitmap(edges, mt);
		return mt;
	}
	public Bitmap erase(int x, int y, Bitmap bm) {
		Bitmap mut = bm.copy(Bitmap.Config.ARGB_8888, true);
		//x = x+500;
		//y = y +300;
		for(int i  = -siz/2 ; i < siz/2 ; i++){
			for(int j  = -siz/2 ; j < siz/2 ; j++){
				//Log.i("PhotoBattle","point " + (x+i)+" "+(y+j)+ " disparait");
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
	public void ReadSettings(Context context){
		File dat = new File(FileManager.DATA_PATH, mapName.getText().toString()+".jpg.dat");
		char[] inputBuffer = new char[255];
		if (dat.exists()) {


				try {
					FileInputStream fstream = new FileInputStream(dat);
					// Get the object of DataInputStream
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));

					String mot = br.readLine();
					int precision = Integer.parseInt(mot);
					mot = br.readLine();
					int gomme = Integer.parseInt(mot);
//					cpt++;
//					mot = "";
//					while (cpt < data.length() && data.charAt(cpt) != ' ') {
//						mot += data.charAt(cpt);
//						cpt++;
//					}
//					int posx1 = Integer.parseInt(mot);
//					cpt++;
//					mot = "";
//					while (cpt < data.length() && data.charAt(cpt) != ' ') {
//						mot += data.charAt(cpt);
//						cpt++;
//					}
//					int posy1 = Integer.parseInt(mot);
//					cpt++;
//					mot = "";
//					while (cpt < data.length() && data.charAt(cpt) != ' ') {
//						mot += data.charAt(cpt);
//						cpt++;
//					}
//					int posx2 = Integer.parseInt(mot);
//					cpt++;
//					mot = "";
//					while (cpt < data.length() && data.charAt(cpt) != ' ') {
//						mot += data.charAt(cpt);
//						cpt++;
//					}
//					int posy2 = Integer.parseInt(mot);
//					cpt++;

					sizeEraser.setProgress(gomme);
					s1.setProgress(precision);

					//affiche le contenu de mon fichier dans un popup surgissant

				}  catch (IOException e) {
					e.printStackTrace();
				}
			}



	}
}
