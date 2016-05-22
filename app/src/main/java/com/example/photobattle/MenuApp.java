package com.example.photobattle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.List;

public class MenuApp extends BaseActivity{
	Button b1;
	Button settings;
	Animation animStart;
	Animation animSettings;
	Button join;
	Button mute;
    boolean pause;
	InterstitialAd mInterstitialAd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.overridePendingTransition(0, 0);
		super.onCreate(savedInstanceState);
		FullScreencall();
        setContentView(R.layout.activity_menu_app);
        pause = false;
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"p.TTF");
		((TextView)findViewById(R.id.title)).setTypeface(tf);
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if(!pause) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                FullScreencall();
            }
        });
		requestNewInterstitial();
//		mInterstitialAd.show();

//		mInterstitialAd.setAdListener(new AdListener() {
//			@Override
//			public void onAdClosed() {
//			}
//		});

		FileManager.initialyzeTreeFile();

		Sound.playMenuMusic(this.getApplicationContext());

		b1=(Button) findViewById(R.id.play_menu);
		animStart = AnimationUtils.loadAnimation(this, R.anim.anim_button);
		//b1.setAnimation(myAnim);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("appui play");
				Sound.playSound(MenuApp.this, R.raw.open);
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
		mute = (Button) findViewById(R.id.sound);
		mute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(animSettings);
				if(Sound.muteMusic()==1)
					mute.setBackgroundResource(R.drawable.full_sound);
				else
					mute.setBackgroundResource(R.drawable.mute);
			}
		});

	}
	public void onResume()
	{
		super.onResume();
        FullScreencall();
        pause = false;

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

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C7B8E8FD2DBFCD9EA1412F167AD58A33").addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
	}

    @Override
    public void onPause() {
        super.onPause();
        pause =true;
    }
}
