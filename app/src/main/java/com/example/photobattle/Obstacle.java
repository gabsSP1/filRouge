package com.example.photobattle;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Valentin on 11/05/2016.
 */
public class Obstacle extends AnimatedSprite {

    private static final int height =100;
    private static final int width =100;
    private Body body;
    private float vX;
    private float vY;
    private Player player;
    private GameScene scene;
    boolean filsCree;
    private long nano;

    private final static float VITESSE_Y_MAX = 5.0f;
    private final static float VITESSE_X_MAX = 5.0f;
    private final static long COUNTDOWN = 4;

    PhysicsWorld physicsWorld;

    public Obstacle (float pX, float pY, VertexBufferObjectManager vbo, ITiledTextureRegion textureRegion, Camera camera, PhysicsWorld physicsWorld, Player player, GameScene scene)
    {
        super(pX, pY, textureRegion, vbo);
        this.player = player;
        this.physicsWorld = physicsWorld;
        vX = (float)(Math.random()*VITESSE_X_MAX);
        vY = (float)(Math.random()*VITESSE_Y_MAX);
        this.scene = scene;
        filsCree = false;
        nano = System.nanoTime();
    }

    public Body getBody() {
        return body;
    }

    public void launchPhysics()
    {
        createPhysicsObstacle(physicsWorld);
    }

    private void createPhysicsObstacle(PhysicsWorld physicsWorld)
    {
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setUserData("obst");
        body.setFixedRotation(false);
        for(int i=0; i<body.getFixtureList().size();i++){
            body.getFixtureList().get(i).setSensor(true);
        }

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
            @Override
            public void onUpdate(float pSecondsElapsed) {

                super.onUpdate(pSecondsElapsed);
                int x = (int) (getpX() + vX);
                int y = (int) (getpY() + vY);
                setpY(y);
                setpX(x);
                checkDeath(x, y);
                if (!filsCree && (System.nanoTime() - nano)/1000000000 > COUNTDOWN) {
                    filsCree = true;
                    createChild();
                }
            }
        });
    }

    private void createChild()
    {
        scene.addObstacle(this);
    }

    private void checkDeath(int x, int y)
    {
        int pX = player.getpX();
        int pY = player.getpY();

        int pWidth = player.getpWidth();
        int pHeight = player.getpHeight();

        if(!( pX > x+width  ||  pX+pWidth < x  ||  pY > y+height  ||  pY+pHeight < y ))
        {
            player.die();
        }

    }

    public void setRunning()
    {

        final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };

        animate(PLAYER_ANIMATE, 0, 2, true);
    }

    public void setpX(int x)
    {
        if(x < 0)
        {
            x = 0;
            vX = -vX;
        }
        if(x+width > Game.CAMERA_WIDTH)
        {
            x = Game.CAMERA_WIDTH -width;
            vX = - vX;
        }
        body.setTransform((x + width / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y, 0);
    }

    public void setpY(int y)
    {
        if(y < 0)
        {
            y = 0;
            vY = -vY;
        }
        if(y+width > Game.CAMERA_HEIGHT)
        {
            y = Game.CAMERA_HEIGHT -height;
            vY = - vY;
        }
        body.setTransform(body.getPosition().x, (y + height / 2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
    }

    public int getpX()
    {
        return (int)(body.getPosition().x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- width /2);
    }

    public int getpY()
    {
        return (int)(body.getPosition().y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- height /2);
    }

    public static int getpWidth()
    {
        return width;
    }

    public static int getpHeight()
    {
        return height;
    }

    public static float getVXMAX()
    {
        return VITESSE_X_MAX;
    }

    public static float getVYMAX()
    {
        return VITESSE_Y_MAX;
    }

}
