package com.example.photobattle;

import android.app.Activity;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import java.util.LinkedList;
import java.util.List;

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
    public static LinkedList<Obstacle> obstacleList;

    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------

    public GameScene(Engine engine, Game activity, Camera camera,  VertexBufferObjectManager vobm)
    {
        super();
        System.out.println("dzqqzz");
        this.engine = engine;
        this.activity = activity;
        this.vbom = vbom;
        this.camera = camera;
        if(!BazarStatic.onLine)
        {
            createScene();
        }
        else
        {
                Client.sendReady(Connect_activity.socket);
        }
    }

    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------
    public  void createScene() {
        createHUD();
        createPhysics();
        persoOne = new Player(50f, 50f, vbom, activity.getPlayerOneTextureRegion(), camera, physicsWorld, this, true);
        this.attachChild(persoOne);

        if (BazarStatic.onLine)
        {
            persoTwo = new Player(50f, 50f, vbom, activity.getPlayerTwoTextureRegion(), camera, physicsWorld, this, false);
            this.attachChild(persoTwo);
            lauchCountDown();
        }
        else
        {
            obstacleList = new LinkedList<>();
            Obstacle ob = new Obstacle(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT/2, vbom, activity.getObstacleTextureRegion(), camera, physicsWorld, persoOne, this);
            this.attachChild(ob);
            obstacleList.add(ob);
        }
        engine.setScene(this);


    }

    public void addObstacle(Obstacle parent)
    {
        int x = parent.getpX();
        int y = parent.getpY();

        Obstacle ob = new Obstacle(x, y, vbom, activity.getObstacleTextureRegion(), camera, physicsWorld, persoOne, this);
        this.attachChild(ob);
        ob.launchPhysics();
        obstacleList.add(ob);
        System.out.println("Nouvel obstacle crée x: "+x+" ; y: "+y+";");
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
        persoOne.detachSelf();
        for(int i =0; i < obstacleList.size(); i++)
        {
            obstacleList.get(i).detachSelf();
        }
//        persoTwo.dispose();
        this.detachSelf();
        this.dispose();
    }

    private void loadLevel(int levelID)
    {

        final FixtureDef FIXTURE_DEF = createFixtureDef(0, 0.01f, 0.5f);




    }

    public void lauchCountDown() {
        textPosition(activity.text);
        this.attachChild(activity.text);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activity.text.setText("2");
        textPosition(activity.text);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activity.text.setText("1");
        textPosition(activity.text);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        textPosition(activity.text);
        this.detachChild(activity.text);
        this.attachChild(activity.textGo);
        textPosition(activity.textGo);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.detachChild(activity.textGo);
        persoOne.launchPhysics();
        if(BazarStatic.onLine)
            persoTwo.launchPhysics();
        else
            obstacleList.getFirst().launchPhysics();

        activity.gameLoaded =true;
    }


    private void textPosition(Text text)
    {
        text.setX(Game.CAMERA_WIDTH/2-text.getScaleCenterX());
        text.setY(Game.CAMERA_HEIGHT/2-text.getScaleCenterY());
    }

    public void endPartySolo()
    {
        engine.stop();
        textPosition(activity.text);
        this.attachChild(activity.text);
        activity.text.setText("YOU DIED");
        textPosition(activity.text);
        try {
        Thread.sleep(2500);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        this.detachChild(activity.text);
        activity.finish();
    }
}
