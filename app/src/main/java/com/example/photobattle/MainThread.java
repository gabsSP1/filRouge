package com.example.photobattle;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

/**
 * Created by Valentin on 26/02/2016.
 * La boucle du jeu : update puis draw
 */
public class MainThread extends Thread
{

    private static final String TAG = MainThread.class.getSimpleName();

    private final static int 	MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int	MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;


    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;
    private boolean running;


    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    public void run()
    {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        long beginTime;		// the time when the cycle begun
        long timeDiff;		// the time it took for the cycle to execute
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        gamePanel.init();

        while (running)
        {
            canvas = null;
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    this.gamePanel.update();
                    this.gamePanel.render(canvas);

                    timeDiff = System.currentTimeMillis() - beginTime;
                    // calculate sleep time
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0) {
                        // if sleepTime > 0 we're OK
                        try {
                            // send the thread to sleep for a short period
                            // very useful for battery saving
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {}
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        // we need to catch up
                        // update without rendering
                        this.gamePanel.update();
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                }
            } finally
            {
                if(canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
