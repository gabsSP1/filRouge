package com.example.photobattle;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.util.List;

import Joystick.JoystickMovedListener;
import Joystick.JoystickView;


/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends SimpleBaseGameActivity {
    public static int CAMERA_WIDTH;
    public static int CAMERA_HEIGHT;
    private GameScene scene ;
    private TextureRegion backgroundTextureRegion;
    private BitmapTextureAtlas backgroundTexture;
    private ITiledTextureRegion playerOneTextureRegion;
    private ITiledTextureRegion playerTwoTextureRegion;
    private BitmapTextureAtlas playerTexture1;
    private BitmapTextureAtlas playerTexture2;
    private Button quit;

    private JoystickView joystickView;


    Camera camera;


    public EngineOptions onCreateEngineOptions() {
        if(!BazarStatic.onLine)
        {
            BazarStatic.map = new Map(BazarStatic.nomMap);
        }
        CAMERA_WIDTH = BazarStatic.map.getPhotoOriginal().getWidth();
        CAMERA_HEIGHT = BazarStatic.map.getPhotoOriginal().getHeight();
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

    }

    public void onCreateResources()   {
//        FullScreencall();
        // on crée la ressource contenant le texte
        BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(BazarStatic.map.getPhotoOriginal());
         backgroundTexture = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
        backgroundTexture.addTextureAtlasSource(source, 0, 0);

        backgroundTextureRegion = (TextureRegion) TextureRegionFactory.createFromSource(backgroundTexture, source, 0, 0);
    }

    public Scene onCreateScene() {

//        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("/");
        playerTexture1 = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
        playerTexture2 = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
        playerOneTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTexture1, this, "personnage.png", 0, 0, 1, 1);
        playerTwoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTexture2, this, "personnage.png", 0, 0, 1, 1);
        playerTexture1.load();
        playerTexture2.load();

        this.mEngine.registerUpdateHandler(new FPSLogger());

        scene = new GameScene(mEngine, this, camera, getVertexBufferObjectManager());

        scene.setBackground(new Background(255, 255, 255));

        SpriteBackground sprite = new SpriteBackground(new Sprite(0,0, backgroundTextureRegion, this.getVertexBufferObjectManager()));
        backgroundTexture.load();
        scene.setBackground(sprite);

        return scene;

    }

    public void onPopulateScene() throws Exception {
        // TODO Auto-generated method stub

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
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }


    @Override
    protected void onSetContentView() {

        final RelativeLayout relativeLayout = new RelativeLayout(this);
        final FrameLayout.LayoutParams relativeLayoutLayoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        this.mRenderSurfaceView = new RenderSurfaceView(this);
        this.mRenderSurfaceView.setRenderer(this.mEngine, this);


        final android.widget.RelativeLayout.LayoutParams surfaceViewLayoutParams = new RelativeLayout.LayoutParams(BaseGameActivity.createSurfaceViewLayoutParams());
        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        relativeLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);


        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas = new RelativeLayout.LayoutParams(300, 300);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonLayoutParmas.setMargins(105, 0, 0 , 5);

        Button button =new Button(this);
        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas2 = new RelativeLayout.LayoutParams(300, 300);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas2.setMargins(105, 0, 0 , 5);
        relativeLayout.addView(button, buttonLayoutParmas2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameScene.persoOne.jump();
            }
        });

        joystickView = new JoystickView(this);
                relativeLayout.addView(joystickView, buttonLayoutParmas);

        joystickView.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(int pan, int tilt) {

                    GameScene.persoOne.setVX(pan);

            }

            @Override
            public void OnReleased() {

            }

            @Override
            public void OnReturnedToCenter() {

            }
        });
        this.setContentView(relativeLayout, relativeLayoutLayoutParams);

        Sound.playFightMusic(Game.this);
        quit = (Button) findViewById(R.id.quit);
        quit.setText("X");
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client.sendQuit();
                Game.this.finish();
            }
        });
        ClientThread.setGameActivity(this);
        Client.sendReady();
    }

    public ITiledTextureRegion getPlayerOneTextureRegion() {
        return playerOneTextureRegion;
    }

    public ITiledTextureRegion getPlayerTwoTextureRegion() {
        return playerTwoTextureRegion;
    }

    public void onPause()
    {
        super.onPause();
        if(isApplicationBroughtToBackground(this))
        {
            Sound.pauseMusic();
        }
    }

    public void onResume()
    {
        super.onResume();
        if(isAppOnForeground(this))
        {
            Sound.resumeMusic();
        }
    }

    public static boolean isApplicationBroughtToBackground(final Context context)
    {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            final ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isAppOnForeground(final Context context)
    {
        final ActivityManager activityManager = (ActivityManager)     context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
        {
            return false;
        }
        final String packageName = context.getPackageName();
        for (final ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
        {
            if ((appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) && appProcess.processName.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }
}