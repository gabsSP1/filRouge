package com.example.photobattle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class EditActivity extends Activity {
	Bitmap background;
	Bitmap original;
	File fbackground;
	Button back;
	LinearLayout l;
	EditText mapName;
	ImageView p;
	boolean inEdit;
	SeekBar s1;
	SeekBar s2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        FullScreencall();
		setContentView(R.layout.activity_edit);
		Intent intent = getIntent();
		if (intent != null) {

			fbackground = new File(intent.getStringExtra("selected_file"));
			background = BazarStatic.decodeSampledBitmapFromResource(FileManager.THRESHOLD_PATH+File.separator+fbackground.getName()
			 ,  1080);
			original =  BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+fbackground.getName()
					,  1080);
		}
		inEdit = false;
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
		s2= (SeekBar) findViewById(R.id.param2);
		s1.setMax(500);
		s2.setMax(500);
		s2.setProgress(100);
		s1.setProgress(100);
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
				Mat mImg = new Mat();
				Utils.bitmapToMat(background, mImg);
				Mat edges = new Mat();
				Imgproc.Canny(mImg, edges, s1.getProgress(), s2.getProgress());
				background.recycle();
				background = Bitmap.createBitmap(mImg.cols(), mImg.rows(), Bitmap.Config.ARGB_8888);
				Mat invertcolormatrix = new Mat(edges.rows(), edges.cols(), edges.type(), new Scalar(255, 255, 255));
				Core.subtract(invertcolormatrix, edges, edges);
				Utils.matToBitmap(edges, background);
				p.setImageBitmap(background);
			}
		});
		s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				background = original.copy(Bitmap.Config.ARGB_8888, true);
				Mat mImg = new Mat();
				Utils.bitmapToMat(background, mImg);
				Mat edges = new Mat();
				Imgproc.Canny(mImg, edges, s1.getProgress(), s2.getProgress());
				background.recycle();
				background = Bitmap.createBitmap(mImg.cols(), mImg.rows(),Bitmap.Config.ARGB_8888);
				Mat invertcolormatrix = new Mat(edges.rows(),edges.cols(), edges.type(), new Scalar(255,255,255));
				Core.subtract(invertcolormatrix, edges, edges);
				Utils.matToBitmap(edges, background);
				p.setImageBitmap(background);
			}
		});
		p.setImageBitmap(background);
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
						File picture = new File(FileManager.PICTURE_PATH, fbackground.getName());
						File n = (new File(FileManager.THRESHOLD_PATH, mapName.getText().toString() + ".jpg"));
						if (!n.exists()) {

							fbackground.renameTo(n);
							picture.renameTo(new File(FileManager.PICTURE_PATH, mapName.getText().toString() + ".jpg"));
						}
						FileManager.saveBitmap(background, FileManager.THRESHOLD_PATH, fbackground.getName() );
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
}
