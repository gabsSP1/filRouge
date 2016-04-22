package com.example.photobattle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.List;

/**
 * Created by Tom on 22/04/2016.
 */
public class BaseActivity extends FragmentActivity {

    public void onPause()
    {
        super.onPause();
        if(isApplicationBroughtToBackground(this))
        {
            Sound.pauseMusic();
        }
    }

    public void onResume()
    {
        super.onResume();
        if(isAppOnForeground(this))
        {
            Sound.resumeMusic();
        }
    }

    public static boolean isApplicationBroughtToBackground(final Context context)
    {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            final ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isAppOnForeground(final Context context)
    {
        final ActivityManager activityManager = (ActivityManager)     context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
        {
            return false;
        }
        final String packageName = context.getPackageName();
        for (final ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
        {
            if ((appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) && appProcess.processName.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }
}
