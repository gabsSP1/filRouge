package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.io.File;

public class MenuApp extends Activity{
	Button b1;
	Button settings;
	Animation animStart;
	Animation animSettings;
	Button join;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.overridePendingTransition(0, 0);
		super.onCreate(savedInstanceState);
		FullScreencall();
		setContentView(R.layout.activity_menu_app);
		FileManager.initialyzeTreeFile();
		b1=(Button) findViewById(R.id.play_menu);
		animStart = AnimationUtils.loadAnimation(this, R.anim.anim_button);
		//b1.setAnimation(myAnim);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("appui play");
				v.startAnimation(animStart);

			}

		});
		join =(Button) findViewById(R.id.join);
		join.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentMyAccount = new Intent(getApplicationContext(), JoinActivity.class);
				startActivity(intentMyAccount);
			}
		});
		animStart.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Intent intentMyAccount = new Intent(getApplicationContext(), ChooseMap.class);
				startActivity(intentMyAccount);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		settings=(Button) findViewById(R.id.Settings);
		animSettings=AnimationUtils.loadAnimation(this, R.anim.anim_button);
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(animSettings);
				System.out.println("appui settings");
			}
		});

	}
	public void onRestart()
	{
		super.onRestart();
		/*View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		decorView.setSystemUiVisibility(uiOptions);*/

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_app, menu);
		return true;
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
