package com.example.photobattle;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit);
		Intent intent= getIntent();
		if (intent != null) {
			
	           fbackground=new File(intent.getStringExtra("selected_file"));
	           background=BitmapFactory.decodeFile(fbackground.getAbsolutePath());
	       }
		inEdit=false;
		p = (ImageView) findViewById (R.id.pictureEdit);
		mapName= (EditText) findViewById(R.id.mapName);
		mapName.setTextColor(Color.BLUE);
		mapName.setText(fbackground.getName());
		mapName.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub7
				File thumbnail=new File(Environment.getExternalStorageDirectory() +
		                File.separator+"photoBattle"+File.separator+"Thumbnail",fbackground.getName());
				File n=(new File(Environment.getExternalStorageDirectory() +
		                File.separator+"photoBattle"+File.separator+"Pictures",mapName.getText().toString()));
				if(!n.exists())
				{fbackground.renameTo(n);
				thumbnail.renameTo(new File(Environment.getExternalStorageDirectory() +
		                File.separator+"photoBattle"+File.separator+"Thumbnail",mapName.getText().toString()));}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext()
, "File already exists", Toast.LENGTH_SHORT);
					mapName.setText(fbackground.getName());
					toast.show();
				}
				return false;
			}
			
		});
		mapName.setVisibility(100);
		mapName.setText(fbackground.getName());
		p.setImageBitmap(background);
		p.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				inEdit=!inEdit;
				if(inEdit)
				{
					mapName.setVisibility(0);
				}
				else
				{
					mapName.setVisibility(100);
				}
			}
			
		});
		
	}
}
