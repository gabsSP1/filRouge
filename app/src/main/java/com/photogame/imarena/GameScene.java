package com.photogame.imarena;

import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.LinkedList;

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


        if (BazarStatic.onLine)
        {
            createHUD();
            createPhysics();
            if(BazarStatic.host) {
                persoOne = new Player(BazarStatic.map.xposJ1, BazarStatic.map.yposJ1, vbom, activity.getPlayerOneTextureRegion(), camera, physicsWorld, this, true);


                persoTwo = new Player(BazarStatic.map.xposJ2, BazarStatic.map.yposJ2, vbom, activity.getPlayerTwoTextureRegion(), camera, physicsWorld, this, false);
            }

            else
            {
                persoTwo = new Player(BazarStatic.map.xposJ1, BazarStatic.map.yposJ1, vbom, activity.getPlayerTwoTextureRegion(), camera, physicsWorld, this, false);


                persoOne  = new Player(BazarStatic.map.xposJ2, BazarStatic.map.yposJ2, vbom, activity.getPlayerOneTextureRegion(), camera, physicsWorld, this, true);
            }

                this.attachChild(persoOne);
            this.attachChild(persoTwo);
            engine.setScene(this);
            lauchCountDown();
        }
        else
        {
            createHUD();
            createPhysics();
            persoOne =  new Player(BazarStatic.map.xposJ1, BazarStatic.map.yposJ1, vbom, activity.getPlayerOneTextureRegion(), camera, physicsWorld, this, true);
            this.attachChild(persoOne);


            obstacleList = new LinkedList<>();
            Obstacle ob = new Obstacle(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT/2, vbom, activity.getObstacleTextureRegion(), camera, physicsWorld, persoOne, this);
            this.attachChild(ob);
            obstacleList.add(ob);
            engine.setScene(this);
        }



    }

    public void stopEngine()
    {
        engine.stop();
    }

    public void addObstacle(Obstacle parent)
    {
        activity.scoreSolo++;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.refreshScore();
            }
        });
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
        physicsWorld.setContactListener(contactListener());
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
        if(!BazarStatic.onLine) {
            for (int i = 0; i < obstacleList.size(); i++) {
                obstacleList.get(i).detachSelf();
            }
        }
//        persoTwo.dispose();
        this.detachSelf();
        this.dispose();
    }

    private void loadLevel(int levelID)
    {

        final FixtureDef FIXTURE_DEF = createFixtureDef(0, 0.01f, 0.5f);




    }

    public void endPartyMulti()
    {
        activity.endGame = true;
        detachAll();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

//stuff that updates ui
                activity.textDie.setVisibility(View.VISIBLE);
                activity.adView.setVisibility(View.VISIBLE);
                activity.restart.setText("retart");
                activity.restart.setVisibility(View.VISIBLE);
                activity.quit.setVisibility(View.VISIBLE);

            }
        });
    }

    public void lauchCountDown() {
        activity.text.setText("3");
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

        /*persoOne.getBody().setActive(false);
        for(Obstacle obs : obstacleList)
        {
            obs.getBody().setActive(false);
        }*/

//        engine.stop();
        activity.endGame = true;
        detachAll();
        if(activity.scoreSolo > BazarStatic.map.getHighScore())
        {
            FileManager.saveScoreSolo(activity.scoreSolo);
            BazarStatic.map.setHighScore(activity.scoreSolo);
            System.out.println("highScore");
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

//stuff that updates ui
                activity.refreshScore();
                activity.textDie.setText("Game Over");
                activity.textDie.setVisibility(View.VISIBLE);
                activity.adView.setVisibility(View.VISIBLE);
                activity.restart.setText("retart");
                activity.restart.setVisibility(View.VISIBLE);
                activity.quit.setVisibility(View.VISIBLE);



            }
        });



    }

    public void detachAll()
    {
        if(!(BazarStatic.onLine)) {
            for (Obstacle obs : obstacleList) {
                detachSprite(obs, obs.getBody());
            }
        }
        else
        {
            detachSprite(persoTwo, persoTwo.getBody());
        }
        detachSprite(persoOne, persoOne.getBody());

    }

    public void detachSprite(final Sprite sprite, final Body body){
        engine.runOnUpdateThread(new Runnable(){

            @Override
            public void run() {

                physicsWorld.unregisterPhysicsConnector(physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(sprite));
                physicsWorld.destroyBody(body);
//                detachChild(sprite);
            }});
    }


    private ContactListener contactListener() {
        ContactListener contactListener = new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                    if ((x2.getBody().getUserData().equals("player1") && x1.getBody().getUserData().equals("obstacle")) || (x1.getBody().getUserData().equals("player1") && x2.getBody().getUserData().equals("obstacle")))
                    {
                        if (!BazarStatic.onLine)
                        {
                            persoOne.die();
                        }
                    }
                    else if((x2.getBody().getUserData().equals("player1") && x1.getBody().getUserData().equals("player2")) || (x1.getBody().getUserData().equals("player1") && x2.getBody().getUserData().equals("player2")))
                    {
                         if((persoOne.getpY()+persoOne.getHeight() - persoTwo.getpY()<15) && (persoOne.getpY()+persoOne.getHeight() - persoTwo.getpY()>-15 && persoOne.getVY()>0))
                            {
                                System.out.println("win");
                                Client.sendWin();
//                                engine.stop();
                            }

                    }
                }


            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        };
        return contactListener;
    }



}
