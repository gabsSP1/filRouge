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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	boolean inEdit;
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
			background = BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+fbackground.getName()
					, 1080 );
			original =  BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+fbackground.getName()
					,  1080);
		}
		inEdit = false;
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
		mapName = (EditText) findViewById(R.id.mapName);
		mapName.setTextColor(Color.BLUE);
		mapName.setText(fbackground.getName().substring(0, fbackground.getName().indexOf('.')));
		mapName.setVisibility(View.INVISIBLE);
		mapName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub7
				return false;
			}
		});
		s1= (SeekBar) findViewById(R.id.param1);
		sizeEraser= (SeekBar) findViewById(R.id.sizing);
		s1.setMax(5);
		sizeEraser.setMax(500);
		try{
			ReadSettings(this);
		}
		catch (Exception e){
			Toast.makeText(this, "je peux pas lire les fichiers",Toast.LENGTH_SHORT).show();
		}

		sizeEraser.setProgress(100);
		s1.setProgress(2);

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
				background = doFilter(s1.getProgress()*50, s1.getProgress()*50+50, background);
				p.setImageBitmap(background);
			}
		});

		p.setImageBitmap(background);
		//ReadSettings(this);
	}
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
			// when user first touches the screen to swap
			case MotionEvent.ACTION_DOWN: {
				inEdit = !inEdit;
				if (inEdit) {
					mapName.setVisibility(View.VISIBLE);
				} else {
					mapName.setVisibility(View.INVISIBLE);
				}
				if(erasemode) {
					p.getLocationOnScreen(location);
					xmap = location[0];
					ymap = location[1];
					Log.i("PhotoBattle", "You touched the map bro !");
					//p.getLocationOnScreen(location);

					/*DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int width=dm.widthPixels;
					int height=dm.heightPixels;
					int dens=dm.densityDpi;
					double wi=(double)width/(double)dens;
					double hi=(double)height/(double)dens;
					double x = Math.pow(wi,2);
					double y = Math.pow(hi,2);
					double screenInches = Math.sqrt(x+y);*/

					widthmap = (int) background.getWidth();
					heightmap = (int) background.getHeight();
					int x = (int) touchevent.getX()*1920/950;
					int y = (int) touchevent.getY()*1081/550;
					Log.i("PhotoBattle"," tes coordonnées : x " + x + " - y " + y +" et celle de la map : x "+xmap+" y "+ymap+" longueur " + widthmap + " hauteur "+heightmap );
					background = erase(x-350,y-200,background);
					background = erase(x,y,background);
					if(x <  + heightmap && x > xmap && y < ymap + widthmap && y > ymap)
					{
						Log.i("PhotoBattle","You touched the map bro !");
					}
				}
			}
			case MotionEvent.ACTION_BUTTON_PRESS: {
				if(erasemode) {
					int x = (int) touchevent.getX();
					int y = (int) touchevent.getY();
					if(x < xmap + heightmap && x > xmap && y < ymap + widthmap && y > ymap)
					{
						Log.i("PhotoBattle","You touched the map bro !");
					}
				}
			}
		}
		return false;
	}
	public void onStop()
	{
		super.onStop();
	}
	private void showDialog() throws Resources.NotFoundException {
		new AlertDialog.Builder(this)
				.setTitle("Save Changes")
				.setMessage("Do you want to save your changes")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String data = "";
						data += s1.getProgress()+" ";
						data += sizeEraser.getProgress()+" ";
						WriteSettings(EditActivity.this, data +"3  ");
						ReadSettings(EditActivity.this);
						File picture = new File(FileManager.PICTURE_PATH, fbackground.getName());
						File n = (new File(FileManager.THRESHOLD_PATH, mapName.getText().toString() + ".jpg"));
						if (!n.exists()) {
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
		Imgproc.Canny(mImg, edges, s1.getProgress()*50, s1.getProgress()*50+50);
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
		for(int i  = 0 ; i < siz ; i++){
			for(int j  = 0 ; j < siz ; j++){
				//Log.i("PhotoBattle","point " + (x+i)+" "+(y+j)+ " disparait");
				if(x+i < mut.getWidth() && y + j < mut.getHeight() && x+i > 0 && y+j > 0)
					mut.setPixel(x+i,y+j,Color.WHITE);
			}
		}
		p.setImageBitmap(mut);
		return mut;
	}
	public void WriteSettings(Context context, String data){
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try{
			File datas = new File(FileManager.PICTURE_PATH, fbackground.getName());
			File n = (new File(FileManager.DATA_PATH, mapName.getText().toString() + ".dat"));
			if (!n.exists()) {
				fbackground.renameTo(n);
				datas.renameTo(new File(FileManager.PICTURE_PATH, mapName.getText().toString() + ".jpg"));
			}
			String nom= mapName.getText().toString(); //le nom de la map que l'on édite
			fOut = context.openFileOutput(nom+".dat",MODE_APPEND);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.flush();
			//popup surgissant pour le résultat
			Toast.makeText(context, "Settings saved",Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(context, "Settings not saved or file does not exist",Toast.LENGTH_SHORT).show();
		}
		finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();
			}
		}
	}
	public String ReadSettings(Context context){
		FileInputStream fIn = null;
		InputStreamReader isr = null;
		char[] inputBuffer = new char[255];
		String data = null;
		try{
			String nom= mapName.getText().toString(); //le nom de la map que l'on édite
			fIn = context.openFileInput(nom+".dat");
			isr = new InputStreamReader(fIn);
			isr.read(inputBuffer);
			data = new String(inputBuffer);
			int cpt = 0;
			String mot ="";
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int precision = Integer.parseInt(mot);
			cpt++;
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int gomme = Integer.parseInt(mot);
			cpt++;
			mot ="";
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int posx1 = Integer.parseInt(mot);
			cpt++;
			mot ="";
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int posy1 = Integer.parseInt(mot);
			cpt++;
			mot ="";
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int posx2 = Integer.parseInt(mot);
			cpt++;
			mot ="";
			while(data.charAt(cpt) != ' ' && cpt < data.length()){
				mot += data.charAt(cpt);
				cpt++;
			}
			int posy2 = Integer.parseInt(mot);
			cpt++;

			sizeEraser.setProgress(gomme);
			s1.setProgress(precision);

			//affiche le contenu de mon fichier dans un popup surgissant
			Toast.makeText(context, " "+data,Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
			s1.setProgress(2);
			sizeEraser.setProgress(100);
		}
            /*finally*/ {
			try {
				isr.close();
				fIn.close();
			} catch (IOException e) {
				Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
			}
		} /**/
		return data;
	}
}
