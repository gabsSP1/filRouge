package com.example.photobattle;

import android.app.Activity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import static org.andengine.extension.physics.box2d.PhysicsFactory.*;

/**
 * Created by gab on 23/04/2016.
 */
public class GameScene extends Scene {

    protected Engine engine;
    protected Game activity;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;
    private HUD yourHud;
    private PhysicsWorld physicsWorld;
    public static Player persoOne;
    public static Player persoTwo;
    private Sprite[][] obstacles;

    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------

    public GameScene(Engine engine, Game activity, Camera camera,  VertexBufferObjectManager vobm)
    {
        super();
        this.engine = engine;
        this.activity = activity;
        this.vbom = vbom;
        this.camera = camera;
    }

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------
    public  void createScene() {
        createHUD();
        createPhysics();
        persoOne = new Player(50f, 50f, vbom, activity.getPlayerOneTextureRegion(), camera, physicsWorld, true);
        if (BazarStatic.onLine)
        {
            persoTwo = new Player(50f, 50f, vbom, activity.getPlayerTwoTextureRegion(), camera, physicsWorld, false);
            this.attachChild(persoTwo);
        }
        System.out.println(persoTwo);
        int width = BazarStatic.map.getContours().getWidth();
        int height = BazarStatic.map.getContours().getHeight();
        obstacles = new Sprite[width][height];


        this.attachChild(persoOne);

    }


    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }


    private void createHUD()
    {


    }

    public  void onBackKeyPressed()
    {

    }


    public  void disposeScene()
    {

    }

    private void loadLevel(int levelID)
    {

        final FixtureDef FIXTURE_DEF = createFixtureDef(0, 0.01f, 0.5f);




    }

}
