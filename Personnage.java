package com.example.valentin.filrouge;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.example.valentin.filrouge.R;

/**
 * Created by Valentin on 24/02/2016.
 */
public class Personnage {

    private static final String TAG = Personnage.class.getSimpleName();

    private final float GRAVITY = 0.5f;
    private final float VITESSE_Y_MAX = 30.0f;
    private final float VITESSE_X_MAX = 20.0f;

    private int x;
    private int y;
    private float vX;
    private float vY;
    private Bitmap sprite;
    private Map map;


    public Personnage(Bitmap image, int x, int y, Map map)
    {
        this.x = x;
        this.y = y;
        vX = 0.0f;
        vY = 0.0f;
        sprite = image;
        this.map = map;
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

    public void setX(int x) {
        if(x > map.right())
            x = map.left();
        else if(x < map.left())
            x = map.right();
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y)
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

    public void setVX(float vX)
    {
        if(vX > VITESSE_X_MAX)
            vX = VITESSE_X_MAX;
        this.vX = vX;
    }

    public float getVY() {
        return vY;
    }

    public void setVY(float vY)
    {
        if(vY > VITESSE_Y_MAX)
            vY = VITESSE_Y_MAX;
        this.vY = vY;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, map.coordXToPixel(x), map.coordYToPixel(y), null);
    }

    public void update()
    {
        setVY(vY + GRAVITY);
        setY((int)(y+vY));
        setX((int) (x + vX));
        move(x,y);
    }

    public void init()
    {
        x = (map.left()+map.right())/2;
        y = (map.top()+map.bottom())/2;
    }



    public void move(int x, int y) {
        pix[][] obstacles = map.getObstacles();
        int height = map.coordYToPixel(sprite.getHeight());
        int width = map.coordXToPixel(sprite.getWidth());


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
                    if(i >= map.right())
                        m = i - map.width();

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
                                 k = l;
                                if (l >= map.bottom())
                                    k = l - map.height();
                                if (obstacles[m][k] == pix.GROUND) {

                                    goRight = i - x + 1;
                                    break outloop;
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
                            goLeft = x+width-i+1;
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
                                 k = l;
                                if (j >= map.bottom())
                                    k = j - map.height();
                                if (obstacles[m][k] == pix.GROUND) {

                                    goLeft = x - i - 1;
                                    break outloop;
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
                    if(i > map.right())
                        m = i - map.width();
                    for (int j = y-(int)vY; j <= y; j--) {
                        int k = j;
                        if (j >= map.bottom())
                            k = j - map.height();
                        if (obstacles[m][k] == pix.GROUND) {
                            goDown = y+height-j+1;
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
