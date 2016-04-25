package com.example.photobattle;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;

import Joystick.JoystickMovedListener;
import Joystick.JoystickView;


/*
Lance le jeu, et surtout le MainGamePanel
 */
public class Game extends SimpleBaseGameActivity {
    public final static int CAMERA_WIDTH = BazarStatic.map.getPhotoOriginal().getWidth();
    public final static int CAMERA_HEIGHT = BazarStatic.map.getPhotoOriginal().getHeight();
    private TextureRegion backGroundRegion;
    private GameScene scene ;
    public BitmapTextureAtlas gameTextureAtlas;
    TextureRegion BackGroundRegion;
    public static ITiledTextureRegion PlayerRegion;
    public  BitmapTextureAtlas pixelTexture;
    private JoystickView joystickView;
    static int dirX;
    static int dirY;
    BitmapTextureAtlas texture;

    Camera camera;


    public EngineOptions onCreateEngineOptions() {

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);

    }

    public void onCreateResources()   {
//        FullScreencall();
        // on crée la ressource contenant le texte
        BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(BazarStatic.map.getPhotoOriginal());
         texture = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
        texture.addTextureAtlasSource(source, 0, 0);

        BackGroundRegion = (TextureRegion) TextureRegionFactory.createFromSource(texture, source, 0, 0);
    }

    public Scene onCreateScene() {

//        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("/");
        gameTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
        PlayerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, this, "personnage.png", 0, 0, 1, 1);
        gameTextureAtlas.load();

        pixelTexture = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT);

        int width = BazarStatic.map.getContours().getWidth();
        int height = BazarStatic.map.getContours().getHeight();
//        pixelsRegion =new ArrayList<>();
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                if (BazarStatic.map.getContours().getPixel(i, j) != Color.WHITE)
//                pixelsRegion.add( BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(pixelTexture, this, "1x1.png", 0, 0, 1, 1));
//            }
//
//        }
//
//        pixelTexture.load();
        this.mEngine.registerUpdateHandler(new FPSLogger());

        scene = new GameScene(mEngine, this, camera, getVertexBufferObjectManager());

        scene.setBackground(new Background(255, 255, 255));

        SpriteBackground sprite = new SpriteBackground(new Sprite(0,0, BackGroundRegion, this.getVertexBufferObjectManager()));
        texture.load();
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


//        FrameLayout frameLayout = new FrameLayout(this);
//        adView = new AdView(this, AdSize.BANNER, "xxxxxxx");
//        adView.refreshDrawableState();
//        frameLayout.addView(adView);

        surfaceViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

//
        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas = new RelativeLayout.LayoutParams(300, 300);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonLayoutParmas.setMargins(105, 0, 0 , 5);
//        Button down = new Button(this);

//
//
//        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas2 = new RelativeLayout.LayoutParams(110, 110);
//        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        buttonLayoutParmas2.setMargins(105, 0, 0 , 205);
//        Button up = new Button(this);
//        relativeLayout.addView(up, buttonLayoutParmas2);
//
//
//        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas3 = new RelativeLayout.LayoutParams(110, 110);
//        buttonLayoutParmas3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        buttonLayoutParmas3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        buttonLayoutParmas3.setMargins(205, 0, 0 , 105);
//        Button right = new Button(this);
//        relativeLayout.addView(right, buttonLayoutParmas3);
//
//        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas4 = new RelativeLayout.LayoutParams(110, 110);
//        buttonLayoutParmas4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        buttonLayoutParmas4.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        buttonLayoutParmas4.setMargins(5, 0, 0 , 105);
//        Button left = new Button(this);
//        relativeLayout.addView(left, buttonLayoutParmas4);


//        buttonLayoutParmas.setMargins(220, 0, 5 , 220);
//        Button up = new Button(this);
//        relativeLayout.addView(up, buttonLayoutParmas);
        Button button =new Button(this);
        android.widget.RelativeLayout.LayoutParams buttonLayoutParmas2 = new RelativeLayout.LayoutParams(300, 300);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayoutParmas2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        buttonLayoutParmas2.setMargins(105, 0, 0 , 5);
        relativeLayout.addView(button, buttonLayoutParmas2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameScene.player.jump();
            }
        });
         joystickView = new JoystickView(this);
                relativeLayout.addView(joystickView, buttonLayoutParmas);
        joystickView.setOnJostickMovedListener(new JoystickMovedListener() {
            @Override
            public void OnMoved(int pan, int tilt) {

                    GameScene.player.setVX(pan);

            }

            @Override
            public void OnReleased() {

            }

            @Override
            public void OnReturnedToCenter() {

            }
        });
        this.setContentView(relativeLayout, relativeLayoutLayoutParams);


    }


}