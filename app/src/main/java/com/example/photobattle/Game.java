package com.example.photobattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;



/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends Activity {

//test dsqds
    private static final String TAG = Game.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        if (intent != null) {
            setContentView(new MainGamePanel(this, intent.getStringExtra("selected_file")));
        }
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



}
