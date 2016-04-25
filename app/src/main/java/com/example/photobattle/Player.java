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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class Player extends AnimatedSprite
{
    private final int height =128;
    private final int width =64;;
    private Body body;
    private float vX;
    private float vY;
    private Map map;
    private etatPerso etat;

    private final float GRAVITY = 1f;
    private final float VITESSE_Y_MAX = 10.0f;
    private final float VITESSE_X_MAX = 10f;

    enum etatPerso{ON_GROUND, JUMP};
    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------

    public Player(float pX, float pY, VertexBufferObjectManager vbo, ITiledTextureRegion textureRegion, Camera camera, PhysicsWorld physicsWorld, boolean j1)
    {
        super(pX, pY, textureRegion, vbo);
        map =BazarStatic.map;
        vX = 0.0f;
        vY = 0.0f;
        etat = etatPerso.JUMP;
        if(j1)
        {
            createPhysicsJ1(physicsWorld);
        }
        else
        {
            createPhysicsJ2(physicsWorld);
        }
    }


    private void createPhysicsJ2(PhysicsWorld physicsWorld)
    {
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setUserData("per2");
        body.setFixedRotation(false);

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);

            }
        });
    }

    private void createPhysicsJ1(PhysicsWorld physicsWorld)
    {
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setUserData("per");
        body.setFixedRotation(false);

        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);
                int x=(int)(getpX()+ vX);
                 setVY((vY + GRAVITY));
                int y =(int)(getpY()+ vY);
                if(BazarStatic.onLine) {
                    Client.sendCoordinates(getpX(), getpY(), true, Connect_activity.socket);

                }
                setpY(y);
                setpX(x);
                move(x,y);

            }
        });
    }

    private int getpX()
    {
        return (int)(body.getPosition().x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- width /2);
    }

    private int getpY()
    {
        return (int)(body.getPosition().y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- height /2);
    }


    public void setpX(int x)
    {
        if (x+ height < 0)
        {
            x=Game.CAMERA_WIDTH;
        }
        else if(getpX()>Game.CAMERA_WIDTH)
        {
            x=-height;
        }
        body.setTransform((x+width/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y, 0);
    }

    public void setpY(int y)
    {
        if (y+ height < 0)
        {
            y=Game.CAMERA_HEIGHT;
        }
        else if(getpY()>Game.CAMERA_HEIGHT)
        {
            y=-height;
        }
        body.setTransform(body.getPosition().x, (y+height/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
    }
    public void setRunning()
    {

        final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };

        animate(PLAYER_ANIMATE, 0, 2, true);
    }

    public void move(int x, int y) {
        pix[][] obstacles = map.getObstacles();


        if (x > 0 && y > 0) {



            if (vX < 0) {
                int stairs = 0;
                int goRight = 0;
                int goUp = 0;
                int[] nbStairs = new int[width];
                int indiceStairs = 0;
                outloop:
                for (int i = x - (int) vX; i >= x; i--) {
                    int m = i;
                    if(m >= Game.CAMERA_WIDTH)
                        m = m - Game.CAMERA_WIDTH;

                    for (int j = y; j < y + height/2; j++) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            goRight = i - x + 1;
                            break outloop;
                        }
                    }
                    for (int j = y + height / 2; j <= y + height; j++) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            if (indiceStairs == width)
                                indiceStairs = 0;
                            nbStairs[indiceStairs++] = stairs;

                            stairs = y+height - j;
                            for (int l = y - stairs; l < y; l++) {
                                for (int p = m; p <= m+width+1; p++)
                                {
                                    int z = p;
                                    if(z >= Game.CAMERA_WIDTH)
                                        z = p - Game.CAMERA_WIDTH;

                                    if(z <0)
                                        z = p + Game.CAMERA_WIDTH;
                                    k = l;
                                    if (l >= Game.CAMERA_HEIGHT)
                                        k = l - Game.CAMERA_HEIGHT;
                                    if (l < 0)
                                        k = l + Game.CAMERA_HEIGHT;
                                    if (obstacles[z][k] == pix.GROUND)
                                    {

                                        goRight = i - x + 1;
                                        break outloop;
                                    }
                                }
                            }
                        }
                    }

                }
                for (int n = 0; n < width; n++)
                    if (goUp < nbStairs[n])
                        goUp = nbStairs[n];
                y -= goUp;
                x += goRight;

            }

            else if (vX > 0) {
                int stairs = 0;
                int goLeft = 0;
                int goUp = 0;
                int[] nbStairs = new int[width];
                int indiceStairs = 0;
                outloop:
                for (int i = x + width - (int) vX; i <= x + width; i++) {
                    int m = i;
                    if(i >= Game.CAMERA_WIDTH)
                        m = i - Game.CAMERA_WIDTH;

                    for (int j = y; j < y + height/2; j++) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            goLeft = x+width-i;
                            break outloop;
                        }
                    }
                    for (int j = y + height/2; j <= y + height; j++) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            if (indiceStairs == width)
                                indiceStairs = 0;
                            nbStairs[indiceStairs++] = stairs;

                            stairs = y+height - j;
                            for (int l = y - stairs; l < y; l++) {
                                for(int p = m-1; p<= m+width; p++)
                                {
                                    int z = p;
                                    if(p < 0)
                                        z = p + Game.CAMERA_WIDTH;
                                    if(p >= Game.CAMERA_WIDTH)
                                        z = p - Game.CAMERA_WIDTH;
                                    k = l;
                                    if (l >= Game.CAMERA_HEIGHT)
                                        k = l - Game.CAMERA_HEIGHT;
                                    if(l < 0)
                                        k = l + Game.CAMERA_HEIGHT;
                                    if (obstacles[z][k] == pix.GROUND)
                                    {
                                        goLeft = x + width - i;
                                        break outloop;
                                    }
                                }
                            }
                        }
                    }

                }
                for (int n = 0; n < width; n++)
                    if (goUp < nbStairs[n])
                        goUp = nbStairs[n];
                y -= goUp;
                x -= goLeft;

            }


            if (vY < 0) {
                int goDown = 0;
                outloop:
                for (int i = x; i <= x + width; i++) {
                    int m = i;
                    if(i >= Game.CAMERA_WIDTH)
                        m = i - Game.CAMERA_WIDTH;
                    for (int j = y-(int)vY; j >= y; j--) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            goDown = j-y+1;
                            break outloop;
                        }
                    }
                }

                y += goDown;
            }
            else if (vY > 0) {
                int goUp = 0;
                outloop:
                for (int i = x; i <= x + width; i++) {
                    int m = i;
                    if(i >= Game.CAMERA_WIDTH)
                        m = i - Game.CAMERA_WIDTH;
                    for (int j = y + height-(int)vY; j <= y + height; j++) {
                        int k = j;
                        if (j >= Game.CAMERA_HEIGHT)
                            k = j - Game.CAMERA_HEIGHT;
                        if (obstacles[m][k] == pix.GROUND) {
                            goUp = y+height-j;
                            etat = etatPerso.ON_GROUND;
                            vY = 0.f;
                            break outloop;
                        }
                    }
                }

                y -= goUp;
            }


        }

         setpX(x);
        setpY(y);

    }

    public float getVX() {
        return vX;
    }

    public void setVX(float vX)
    {
        if(vX > VITESSE_X_MAX)
            vX = VITESSE_X_MAX;
        this.vX = vX;
    }

    public float getVY() {
        return vY;
    }

    private void setVY(float vY)
    {
        if(vY > VITESSE_Y_MAX)
            vY = VITESSE_Y_MAX;
        this.vY = vY;
    }

    public void goRight()
    {
        setVX(3.3f);
    }

    public void goLeft()
    {
        setVX(-3.3f);
    }

    public void idle()
    {
        setVX(0.0f);
    }

    public void jump()
    {
        if(vY==0)
        {
            setVY(-30f);
            etat = etatPerso.JUMP;
        }
//        setVY(-30f);
    }



}