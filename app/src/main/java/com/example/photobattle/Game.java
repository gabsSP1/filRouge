package com.example.photobattle;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Display;
import android.view.MotionEvent;
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
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.util.List;

import com.example.photobattle.Joystick.JoystickMovedListener;
import com.example.photobattle.Joystick.JoystickView;


/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends SimpleBaseGameActivity {
    public static int CAMERA_WIDTH;
    public static int CAMERA_HEIGHT;
    private static GameScene gameScene;
    private TextureRegion backgroundTextureRegion;
    private BitmapTextureAtlas backgroundTexture;
    private ITiledTextureRegion playerOneTextureRegion;
    private ITiledTextureRegion playerTwoTextureRegion;
    private BitmapTextureAtlas playerTexture1;
    private BitmapTextureAtlas playerTexture2;
    private Button quit;
    SpriteBackground sprite;

    private JoystickView joystickView;
    Font fontCountdown;

    Camera camera;

    Text text;
    Text textGo;
    Text textLoading;



    public EngineOptions onCreateEngineOptions() {
        CAMERA_WIDTH = BazarStatic.map.getContours().getWidth();
        CAMERA_HEIGHT = BazarStatic.map.getContours().getHeight();
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

    }

    public void onCreateResources()   {
        BitmapTextureAtlas fontTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        font = FontFactory.createFromAsset(this.getFontManager(), fontTextureAtlas,this.getAssets(),"p.TTF",45f,true, Color.BLACK);
        this.getEngine().getTextureManager().loadTexture(fontTextureAtlas);
        textLoading =  new Text(Game.CAMERA_WIDTH/2, Game.CAMERA_HEIGHT/2, font, "Loading...", this.getVertexBufferObjectManager());
        font.load();
    }

    public Scene onCreateScene() {

//        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("/");


        LoadingScene l = new LoadingScene(this);
        LoadGameScne loadGameScne = new LoadGameScne(this);
        loadGameScne.execute();
        return l;

    }


    public void onPopulateScene() throws Exception {
        // TODO Auto-generated method stub

    }

    public void onDestroy()
    {
        super.onDestroy();
        backgroundTexture.unload();
        playerTexture2.unload();
        playerTexture1.unload();
        playerTexture1 = null;
        playerOneTextureRegion = null;
        playerTwoTextureRegion =null;
        backgroundTextureRegion =null;
        gameScene.disposeScene();
        BazarStatic.map.recycle();
        BazarStatic.map = null;
        System.gc();
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
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        FullScreencall();
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        final FrameLayout.LayoutParams relativeLayoutLayoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        this.mRenderSurfaceView = new RenderSurfaceView(this);
        this.mRenderSurfaceView.setRenderer(this.mEngine, this);


        final android.widget.RelativeLayout.LayoutParams surfaceViewLayoutParams = new RelativeLayout.LayoutParams(BaseGameActivity.createSurfaceViewLayoutParams());
        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        relativeLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);


        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonLayoutParmas.setMargins(105, 0, 0 , 5);

        Button button =new Button(this);
        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas2 = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas2.setMargins(105, 0, 0, 5);
        relativeLayout.addView(button, buttonLayoutParmas2);

        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    GameScene.persoOne.jump();
                }
                return false;
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
        quit = new Button(this);
        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas3 = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas2.setMargins(105, 0, 0, 5);
        relativeLayout.addView(quit, buttonLayoutParmas3);
        quit.setText("X");
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BazarStatic.onLine) {
                    Client.sendQuit();
                }
                Game.this.finish();
            }
        });
        this.setContentView(relativeLayout, relativeLayoutLayoutParams);

        Sound.playFightMusic(Game.this);
        if(BazarStatic.onLine) {
            ClientThread.setGameActivity(this);
        }
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
        if(BazarStatic.onLine) {
            Client.sendQuit();
        }
        this.finish();
    }

            public void onResume()
    {
        super.onResume();
        if(isAppOnForeground(this))
        {
            Sound.resumeMusic(this,R.raw.mus_fight);
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

    public static void launchGame()
    {
        gameScene.createScene();
    }



    public Font font;

    private void loadMenuFonts()
    {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(this.getFontManager(), mainFontTexture, this.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();
    }


    public class LoadGameScne extends AsyncTask<Void, Void, Void>{
        Game game;
        LoadGameScne(Game game)
        {
            this.game = game;
        }
        @Override
        protected Void doInBackground(Void... params) {
            BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(BazarStatic.map.getPhotoOriginal());
            backgroundTexture = new BitmapTextureAtlas(game.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
            backgroundTexture.addTextureAtlasSource(source, 0, 0);

            backgroundTextureRegion = (TextureRegion) TextureRegionFactory.createFromSource(backgroundTexture, source, 0, 0);
            playerTexture1 = new BitmapTextureAtlas(game.getTextureManager(), 128, 256);
            playerTexture2 = new BitmapTextureAtlas(game.getTextureManager(), 128, 256);
            if(!BazarStatic.onLine)
            {
                BazarStatic.map.computeObstacle();
            }
            playerOneTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(game.playerTexture1, game, "personnage.png", 0, 0, 1, 1);
            playerTwoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(game.playerTexture2, game, "personnage.png", 0, 0, 1, 1);
            playerTexture1.load();
            playerTexture2.load();

            BitmapTextureAtlas fontTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            fontCountdown = FontFactory.createFromAsset(game.getFontManager(), fontTextureAtlas,game.getAssets(),"p.TTF",100f,true, Color.BLACK);
            game.getEngine().getTextureManager().loadTexture(fontTextureAtlas);

            text = new Text(Game.CAMERA_WIDTH/2, Game.CAMERA_HEIGHT/2, fontCountdown, "3", game.getVertexBufferObjectManager());
            textGo = new Text(Game.CAMERA_WIDTH/2, Game.CAMERA_HEIGHT/2, fontCountdown, "Go !", game.getVertexBufferObjectManager());
            game.fontCountdown.load();

            mEngine.registerUpdateHandler(new FPSLogger());

            gameScene = new GameScene(mEngine, game, camera, getVertexBufferObjectManager());

            gameScene.setBackground(new Background(255, 255, 255));

            sprite = new SpriteBackground(new Sprite(0,0, backgroundTextureRegion, game.getVertexBufferObjectManager()));
            backgroundTexture.load();
            gameScene.setBackground(sprite);
            if(!BazarStatic.onLine)
            gameScene.lauchCountDown();
            return null;
        }
    }

}