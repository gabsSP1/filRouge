package com.example.photobattle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Valentin on 26/02/2016.
 * Là où on prévois les trucs.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = MainGamePanel.class.getSimpleName();



    private MainThread thread;
    private Personnage perso;
    private Map map;
    private float density;

    public MainGamePanel(Context context)
    {
        super(context);
        getHolder().addCallback(this);
        map = new Map(BitmapFactory.decodeResource(getResources(), R.drawable.map), this);
        perso = new Personnage(BitmapFactory.decodeResource(getResources(), R.drawable.personnage), 0, 0, map);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        density = context.getResources().getDisplayMetrics().density;



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while(retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch(InterruptedException e)
            {

            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_MOVE ||event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if(event.getY() > getHeight() - 100)
            {
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            }
            else
            {
                if(event.getX() < getWidth()/2)
                    perso.setVX(-10.0f);
                else
                    perso.setVX(10.0f);
                //Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());
            }
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            perso.setVX(0.0f);
            //Log.d(TAG, "STOP !");
            return false;
        }
        Log.d(TAG, "MotionEvent : " + event.getAction());
        return super.onTouchEvent(event);
    }

    public void init()
    {
        map.init();
        perso.init();
    }


    public void render(Canvas canvas)
    {

        //création d'une image buffer
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(map.width(), map.height(), conf); // this creates a MUTABLE bitmap
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.BLACK);
        map.draw(c);
        perso.draw(c);
        canvas.drawBitmap(bmp,0,0,null);

    }

    public void update()
    {
       perso.update();
    }

    public float getDensity() {return density;}



}
