package com.photogame.imarena;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class Player extends AnimatedSprite
{
    private final int height =100;
    private final int width =70;
    private Body body;
    private float vX;
    private float vY;
    private Map map;
    private etatPerso etat;
    private GameScene scene;
    PhysicsWorld physicsWorld;
    boolean dead;
    private final float GRAVITY = 1f;
    private final float VITESSE_Y_MAX = 10.0f;
    private final float VITESSE_X_MAX = 10f;
    boolean j1;

    enum etatPerso{ON_GROUND, JUMP, DEAD};
    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------

    public Player(float pX, float pY, VertexBufferObjectManager vbo, ITiledTextureRegion textureRegion, Camera camera, PhysicsWorld physicsWorld, GameScene scene, boolean j1)
    {
        super(pX, pY, textureRegion, vbo);
        map =BazarStatic.map;
        vX = 0.0f;
        vY = 0.0f;
        this.scene = scene;
        etat = etatPerso.JUMP;
        this.physicsWorld = physicsWorld;
        this.j1= j1;
        dead =false;
        setCurrentTileIndex(4);

    }


    public Body getBody() {
        return body;
    }

    public void launchPhysics()
    {
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
        body.setUserData("player2");
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
        PolygonShape p = new PolygonShape();
        Vector2 v1 = new Vector2(20,0);
        Vector2 v2 = new Vector2(20,20);
        Vector2 v3 = new Vector2(40,20);
        Vector2 v4 = new Vector2(40,0);
        Vector2[] pVerctices = {v1, v2, v3, v4};
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyDef.BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setUserData("player1");
        body.setFixedRotation(false);


        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);
//                System.out.println(GameScene.persoTwo.getpX()+" "+GameScene.persoTwo.getpY()+" "+getpX()+getpY());
//                System.out.println((getpY()-height)-GameScene.persoTwo.getpY()+" "+(getpY()-height-GameScene.persoTwo.getpY())+" "+getpX()+getpY());
//                if(BazarStatic.onLine && (getpY()-height)-GameScene.persoTwo.getpY()<20 && getpY()-height-GameScene.persoTwo.getpY()>-20 )// && getpX()>(GameScene.persoTwo.getpX()-width) && getpX()<(GameScene.persoTwo.getpX()+width))
//                {
//                    Client.sendWin();
//                    scene.engine.stop();
//
////                    scene.activity.textDie.setText("You win !");
////                    scene.endPartyMulti();
//                    System.out.println("win");
//
//                }


                int x=(int)(getpX()+ vX);
                 setVY((vY + GRAVITY));
                int y =(int)(getpY()+ vY);
                if(BazarStatic.onLine) {
                    Client.sendCoordinates(getpX(), getpY(), true, Connect_activity.socket);

                }
                setpY(y);
                setpX(x);
                move(x, y);

            }
        });
    }

    public int getpX()
    {
        return (int)(body.getPosition().x*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- width /2);
    }

    public int getpY()
    {
        return (int)(body.getPosition().y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT- height /2);
    }


    public void setpX(int x)
    {
        if (x+ width/2 < 0)
        {
            x += Game.CAMERA_WIDTH;
        }
        else if(x>Game.CAMERA_WIDTH - width/2)
        {
            x -= Game.CAMERA_WIDTH;
        }
        body.setTransform((x+width/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y, 0);
    }

    public void setpY(int y)
    {
        if (y+ height/2 < 0)
        {
            y += Game.CAMERA_HEIGHT;
        }
        else if(y >Game.CAMERA_HEIGHT - height/2)
        {
            y -= Game.CAMERA_HEIGHT;
        }
        body.setTransform(body.getPosition().x, (y+height/2)/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
    }


    public void move(int x, int y) {
        pix[][] obstacles = map.getObstacles();






        if (vX < 0) {
            int stairs = 0;
            int goRight = 0;
            int goUp = 0;
            int[] nbStairs = new int[width];
            int indiceStairs = 0;
            outloop:
            for (int i = x - (int) vX; i >= x; i--) {
                int m = i;
                if(m < 0)
                    m += Game.CAMERA_WIDTH;
                if(m >= Game.CAMERA_WIDTH)
                    m -= Game.CAMERA_WIDTH;

                for (int j = y; j < y + height/2; j++) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
                    if (obstacles[m][k] == pix.GROUND) {
                        goRight = i - x;
                        break outloop;
                    }
                }
                for (int j = y + height / 2; j <= y + height; j++) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
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

                                    goRight = i - x;
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
                if(m < 0)
                    m += Game.CAMERA_WIDTH;
                if(i >= Game.CAMERA_WIDTH)
                    m = i - Game.CAMERA_WIDTH;

                for (int j = y; j < y + height/2; j++) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
                    if (obstacles[m][k] == pix.GROUND) {
                        goLeft = x+width-i;
                        break outloop;
                    }
                }
                for (int j = y + height/2; j <= y + height; j++) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
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
                if( i < 0)
                    m += Game.CAMERA_WIDTH;
                if(i >= Game.CAMERA_WIDTH)
                    m = i - Game.CAMERA_WIDTH;
                for (int j = y-(int)vY; j >= y; j--) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
                    if (obstacles[m][k] == pix.GROUND) {
                        goDown = j-y+1;
                        vY = 0.0f;
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
                if(i < 0)
                    m += Game.CAMERA_WIDTH;
                if(i >= Game.CAMERA_WIDTH)
                    m = i - Game.CAMERA_WIDTH;
                for (int j = y + height-(int)vY; j <= y + height; j++) {
                    int k = j;
                    if(j < 0)
                        k += Game.CAMERA_HEIGHT;
                    if (j >= Game.CAMERA_HEIGHT)
                        k -= Game.CAMERA_HEIGHT;
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
        if(etat == etatPerso.JUMP && vX>0 && vY<0)
        {
            setCurrentTileIndex(9);
        }
        else if(etat == etatPerso.JUMP && vX<0 && vY<0)
        {
            setCurrentTileIndex(1);
        }
        else if (vX<0)
        {
            setCurrentTileIndex(3);
        }
        else if (vX>0)
        {
            setCurrentTileIndex(7);
        }
        else if(vX == 0 && vY <0)
        {
            setCurrentTileIndex(5);
        }
        else if(vX == 0)
        {
            setCurrentTileIndex(4);
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

    public int getpWidth() {return width;}
    public int getpHeight() {return height;}

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
        if(etat == etatPerso.ON_GROUND)
        {
            setVY(-30f);
            etat = etatPerso.JUMP;
        }
//        setVY(-30f);
    }

    public void die()
    {
        if(!dead) {
            dead =true;
            System.out.println("Died");
            scene.endPartySolo();
        }
    }





}