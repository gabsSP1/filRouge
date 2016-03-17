package com.example.photobattle;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.example.photobattle.*;

/**
 * Created by Valentin on 24/02/2016.
 * Le personnage. Avec une magnifique détection de collisions.
 */
public class Personnage {

    private static final String TAG = Personnage.class.getSimpleName();

    private final float GRAVITY = 0.2f;
    private final float VITESSE_Y_MAX = 10.0f;
    private final float VITESSE_X_MAX = 6.6f;

    enum etatPerso{ON_GROUND, JUMP};

    private etatPerso etat;
    private int x;
    private int y;
    private float vX;
    private float vY;
    private Bitmap sprite;
    private Map map;
    private float vitesseRatioX;
    private float vitesseRatioY;


    public Personnage(Bitmap image, int x, int y, Map map)
    {
        this.x = x;
        this.y = y;
        vX = 0.0f;
        vY = 0.0f;
        sprite = image;
        this.map = map;
        etat = etatPerso.JUMP;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public void setSprite(Bitmap sprite){
        this.sprite = sprite;
    }

    public int getX() {
        return x;
    }

    private void setX(int x) {
        if(x >= map.right())
            x -= map.width();
        else if(x < map.left())
            x = map.right();
        this.x = x;
    }

    public int getY() {
        return y;
    }

    private void setY(int y)
    {
        if(y > map.bottom())
            y = map.top();
        else if(y < map.top())
            y = map.bottom();
            this.y = y;
    }

    public float getVX() {
        return vX;
    }

    private void setVX(float vX)
    {
        if(vX > VITESSE_X_MAX*vitesseRatioX)
            vX = VITESSE_X_MAX*vitesseRatioX;
        this.vX = vX;
    }

    public float getVY() {
        return vY;
    }

    private void setVY(float vY)
    {
        if(vY > VITESSE_Y_MAX*vitesseRatioY)
            vY = VITESSE_Y_MAX*vitesseRatioY;
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
        if(etat != etatPerso.JUMP)
        {
            setVY(-5.0f);
            etat = etatPerso.JUMP;
        }
    }

    public void draw(Canvas canvas) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        canvas.drawBitmap(sprite, map.dpToPixel(x), map.dpToPixel(y), null);

        canvas.drawRect(map.dpToPixel(x), map.dpToPixel(y), map.dpToPixel(x)+sprite.getWidth(),map.dpToPixel(y)+sprite.getHeight(), p);

    }

    public void update()
    {
        setVY(vitesseRatioY*(vY + GRAVITY));
        setY((int)(y+vY*vitesseRatioY));
        setX((int) (x + vX*vitesseRatioX));
        move(x,y);
    }

    public void init()
    {
        x = map.left();
        y = map.top();
        vitesseRatioY = map.getScreenHeigth()/360;
        vitesseRatioX = map.getScreenWidth()/640;
        Log.d(TAG, "Sprite width :"+ sprite.getWidth()+"; height :" +sprite.getHeight()+";");
        //Rect zone = map.resizeKeepRatio(sprite.getHeight(), sprite.getHeight(), map.dpToPixel((map.getScreenWidth()*4)/640), map.dpToPixel((map.getScreenHeigth()*7)/360));
        sprite = Bitmap.createScaledBitmap(sprite, map.dpToPixel((map.getScreenWidth()*14)/640), map.dpToPixel((map.getScreenHeigth()*21)/360), true);
    }



    public void move(int x, int y) {
        pix[][] obstacles = map.getObstacles();
        int height = map.pixelToDp(sprite.getHeight());
        int width = map.pixelToDp(sprite.getWidth());


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
                    if(m >= map.right())
                        m = m - map.width();

                    for (int j = y; j < y + height/2; j++) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
                        if (obstacles[m][k] == pix.GROUND) {
                            goRight = i - x + 1;
                            break outloop;
                        }
                    }
                    for (int j = y + height / 2; j <= y + height; j++) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
                        if (obstacles[m][k] == pix.GROUND) {
                            if (indiceStairs == width)
                                indiceStairs = 0;
                            nbStairs[indiceStairs++] = stairs;

                            stairs = y+height - j;
                            for (int l = y - stairs; l < y; l++) {
                                for (int p = m; p <= m+width+1; p++)
                                {
                                    int z = p;
                                    if(z > map.right())
                                        z = m - map.width();
                                    k = l;
                                    if (l >= map.bottom())
                                        k = l - map.height();
                                    if (l < map.top())
                                        k = l + map.height();
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
                    if(i >= map.right())
                        m = i - map.width();

                    for (int j = y; j < y + height/2; j++) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
                        if (obstacles[m][k] == pix.GROUND) {
                            goLeft = x+width-i;
                            break outloop;
                        }
                    }
                    for (int j = y + height/2; j <= y + height; j++) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
                        if (obstacles[m][k] == pix.GROUND) {
                            if (indiceStairs == width)
                                indiceStairs = 0;
                            nbStairs[indiceStairs++] = stairs;

                            stairs = y+height - j;
                            for (int l = y - stairs; l < y; l++) {
                                for(int p = m-1; p<= m+width; p++)
                                {
                                    int z = p;
                                    if(p < map.left())
                                        z = p + map.width();
                                    k = l;
                                    if (l >= map.bottom())
                                        k = l - map.height();
                                    if(l < map.top())
                                        k = l + map.height();
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
                    if(i >= map.right())
                        m = i - map.width();
                    for (int j = y-(int)vY; j >= y; j--) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
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
                    if(i >= map.right())
                        m = i - map.width();
                    for (int j = y + height-(int)vY; j <= y + height; j++) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
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

        this.y = y;
        this.x = x;


    }
}
