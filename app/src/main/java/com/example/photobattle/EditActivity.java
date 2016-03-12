package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.File;

public class EditActivity extends Activity {
	Bitmap background;
	File fbackground;
	LinearLayout l;
	EditText mapName;
	ImageView p;
	boolean inEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);*/
		setContentView(R.layout.activity_edit);
		Intent intent = getIntent();
		if (intent != null) {

			fbackground = new File(intent.getStringExtra("selected_file"));
			background = BitmapFactory.decodeFile(fbackground.getAbsolutePath());
		}
		inEdit = false;
		p = (ImageView) findViewById(R.id.pictureEdit);
		mapName = (EditText) findViewById(R.id.mapName);
		mapName.setTextColor(Color.BLUE);
		mapName.setText(fbackground.getName().substring(0, fbackground.getName().indexOf('.')));
		mapName.setVisibility(View.INVISIBLE);

		mapName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub7
				File picture = new File(FileManager.PICTURE_PATH, fbackground.getName());
				File n = (new File(FileManager.THRESHOLD_PATH, mapName.getText().toString() + ".jpg"));
				if (!n.exists()) {

					fbackground.renameTo(n);
					picture.renameTo(new File(FileManager.PICTURE_PATH, mapName.getText().toString() + ".jpg"));
				} else {
					Toast toast = Toast.makeText(getApplicationContext()
							, "File already exists", Toast.LENGTH_SHORT);
					mapName.setText(fbackground.getName().substring(0, fbackground.getName().indexOf('.')));
					toast.show();
				}
				return false;
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			final View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}
}
