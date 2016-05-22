package com.example.photobattle;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends SimpleBaseGameActivity {
    boolean inGame=true;

    public static int CAMERA_WIDTH;
    public static int CAMERA_HEIGHT;
    private static GameScene gameScene;
    private TextureRegion backgroundTextureRegion;
    private BitmapTextureAtlas backgroundTexture;
    private ITiledTextureRegion playerOneTextureRegion;
    private ITiledTextureRegion playerTwoTextureRegion;
    private ITiledTextureRegion obstacleTextureRegion;
    private BitmapTextureAtlas playerTexture1;
    private BitmapTextureAtlas playerTexture2;
    private BitmapTextureAtlas obstacleTexture;
    TextView score;

    Button quit;
    public Button pause;
    boolean endGame =false;
    Button restart;
    SpriteBackground sprite;
    public boolean gameLoaded = false;
    private JoystickView joystickView;
    Font fontCountdown;
    AdView adView;
    Camera camera;
    int scoreGuest;
    int scoreHost;

    int nbVague;
    int scoreSolo = 1;

    Text text;
    Text textGo;
    Text textLoading;
    BitmapTextureAtlas fontTextureAtlas;
    BitmapTextureAtlas fontCountdowTextureAtlas;
    TextView textDie;

    public EngineOptions onCreateEngineOptions() {
        CAMERA_WIDTH = BazarStatic.map.getContours().getWidth();
        CAMERA_HEIGHT = BazarStatic.map.getContours().getHeight();
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    public void onCreateResources()   {
        fontTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
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


    public void onDestroy()
    {
        super.onDestroy();
        backgroundTexture.unload();
        playerTexture2.unload();
        playerTexture1.unload();
        obstacleTexture.unload();
        playerTexture1 = null;
        playerTexture2 = null;
        obstacleTexture = null;
        backgroundTexture = null;
        playerOneTextureRegion = null;
        playerTwoTextureRegion =null;
        obstacleTextureRegion = null;
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
        this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        FullScreencall();
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        final FrameLayout.LayoutParams relativeLayoutLayoutParams = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        this.mRenderSurfaceView = new RenderSurfaceView(this);
        this.mRenderSurfaceView.setRenderer(this.mEngine, this);


        final RelativeLayout.LayoutParams surfaceViewLayoutParams = new RelativeLayout.LayoutParams(BaseGameActivity.createSurfaceViewLayoutParams());
        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        relativeLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);


        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams buttonLayoutParmas = new RelativeLayout.LayoutParams(height/5, height/5);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonLayoutParmas.setMargins(50, 0, 0 , 50);
        joystickView = new JoystickView(this);
        joystickView.setAlpha(0.4f);
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




        final Button button =new Button(this);
        RelativeLayout.LayoutParams buttonLayoutParmas2 = new RelativeLayout.LayoutParams(height/5, height/5);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas2.setMargins(0, 0, 50, 50);
        relativeLayout.addView(button, buttonLayoutParmas2);
        button.setAlpha(0.5f);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN && inGame)
                {
                    GameScene.persoOne.jump();
                }
                return false;
            }
        });


        quit = new Button(this);
        quit.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams buttonLayoutParmas3 = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonLayoutParmas3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonLayoutParmas3.setMargins((int)(width/2-height/7.0), height/2, 0, 0);
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

        restart = new Button(this);
        restart.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams buttonLayoutParmas5 = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas5.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonLayoutParmas5.setMargins(0, height/2, (int)(width/2-height/7.0), 0);
        relativeLayout.addView(restart, buttonLayoutParmas5);
        restart.setText(">");
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreSolo = 1;

                adView.setVisibility(View.INVISIBLE);
                requestNewInterstitial();
                quit.setVisibility(View.INVISIBLE);
                restart.setVisibility(View.INVISIBLE);
                textDie.setVisibility(View.INVISIBLE);
                if(!endGame) {
                    textDie.setText("Pause...");
                    mEngine.start();
                    inGame = true;

                }
                else
                {
                    scoreSolo = 1;
                    refreshScore();
//                    gameScene.detachAll();
                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {

                            gameScene.detachChildren();
                            gameScene.reset();

//                    mEngine.start();
                            gameLoaded =false;
                            endGame =false;
                            if(!BazarStatic.onLine) {
                                gameScene.createScene();
                                gameScene.lauchCountDown();

                            }
                            else
                            {
                                Client.sendReady(Connect_activity.socket);
                            }
//                            mEngine.start();
                            return null;
                        }
                    }.execute();

                }
            }
        });


        textDie = new TextView(this);
        textDie.setText("Pause..");
        textDie.setVisibility(View.INVISIBLE);
        textDie.setTextColor(Color.BLACK);
        textDie.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        textDie.setTypeface(Typeface.createFromAsset(getAssets(),"p.TTF"));
        RelativeLayout.LayoutParams buttonLayoutParmas6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParmas6.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonLayoutParmas6.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonLayoutParmas6.setMargins(0, (int)(height/2-1.5*height/7.0), 0, 0);
        relativeLayout.addView(textDie, buttonLayoutParmas6);






        //Menu pause
        pause = new Button(this);
        pause.setClickable(false);
        RelativeLayout.LayoutParams buttonLayoutParmas4 = new RelativeLayout.LayoutParams(height/7, height/7);
        buttonLayoutParmas4.setMargins(0, 0, 0, 5);
        buttonLayoutParmas4.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonLayoutParmas4.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeLayout.addView(pause, buttonLayoutParmas4);
        pause.setText("||");
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameLoaded && !endGame) {
                    adView.setVisibility(View.VISIBLE);
                    textDie.setText("Pause...");
                    inGame = false;
                    quit.setVisibility(View.VISIBLE);
                    restart.setVisibility(View.VISIBLE);
                    textDie.setVisibility(View.VISIBLE);
                    if (!BazarStatic.onLine)
                        mEngine.stop();
                    relativeLayout.setBackgroundColor(Color.alpha(1));
                }

            }
        });

        score = new TextView(this);
        RelativeLayout.LayoutParams buttonLayoutParmasScore = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(BazarStatic.onLine)
        {
            refreshScore();
        }
        else
        {
            refreshScore();
        }
        buttonLayoutParmasScore.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonLayoutParmasScore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(score, buttonLayoutParmasScore);

        this.setContentView(relativeLayout, relativeLayoutLayoutParams);


        adView = new AdView(this);

        final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.TOP);

        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setVisibility(View.INVISIBLE);




        relativeLayout.addView(adView,adViewLayoutParams);
        requestNewInterstitial();

        Sound.playFightMusic(Game.this);
        if(BazarStatic.onLine) {
            ClientThread.setGameActivity(this);
        }





    }

    public ITiledTextureRegion getPlayerOneTextureRegion() {return playerOneTextureRegion;}

    public ITiledTextureRegion getPlayerTwoTextureRegion() {return playerTwoTextureRegion;}

    public ITiledTextureRegion getObstacleTextureRegion() {return obstacleTextureRegion;}

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
        FullScreencall();
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

    public void refreshScore()
    {
        if(BazarStatic.onLine) {
            score.setText("Guest " + scoreGuest + "-" + scoreHost + " Host");
        }
        else {
            score.setText("Score :"+scoreSolo+" High Socre"+BazarStatic.map.getHighScore());
        }
    }

    public Font font;



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

            backgroundTextureRegion =  TextureRegionFactory.createFromSource(backgroundTexture, source, 0, 0);
            playerTexture1 = new BitmapTextureAtlas(game.getTextureManager(), 900, 100);
            playerTexture2 = new BitmapTextureAtlas(game.getTextureManager(), 900, 100);
            obstacleTexture = new BitmapTextureAtlas(game.getTextureManager(), 700, 100);


            if(!BazarStatic.onLine)
            {
                BazarStatic.map.computeObstacle();
            }


            playerOneTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(game.playerTexture2, game, "sprite_klonk_2.png", 0, 0, 9, 1);
            playerTwoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(game.playerTexture2, game, "sprite_klonk_2.png", 0, 0, 9, 1);
            obstacleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(game.obstacleTexture, game, "sprite_bandelette_2.png", 0, 0, 7, 1);
            playerTexture1.load();
            playerTexture2.load();
            obstacleTexture.load();
            fontCountdowTextureAtlas = new BitmapTextureAtlas(game.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            fontCountdown = FontFactory.createFromAsset(game.getFontManager(), fontCountdowTextureAtlas,game.getAssets(),"p.TTF",100f,true, Color.BLACK);
            game.getEngine().getTextureManager().loadTexture(fontCountdowTextureAtlas);

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

    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("C7B8E8FD2DBFCD9EA1412F167AD58A33")
                .build();

        adView.loadAd(adRequest);
    }
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    public static GameScene getGameScene() {
        return gameScene;
    }
}