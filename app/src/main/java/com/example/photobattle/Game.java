package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;


/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends Activity {
    MainGamePanel mainGamePanel;

    private static final String TAG = Game.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FullScreencall();
        onWindowFocusChanged(true);
        Intent intent = getIntent();
        String s="";
//        if (intent != null) {
//
//            s = intent.getStringExtra("selected_file");
//
//        }
        setContentView(R.layout.game_layout);
//        mainGamePanel = (MainGamePanel) findViewById(R.id.panel);
//        mainGamePanel.setMap(new Map(s));
        Log.d(TAG, "View added");
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "Stopping...");
        super.onStop();
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
