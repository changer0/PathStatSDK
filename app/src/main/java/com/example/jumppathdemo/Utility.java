package com.example.jumppathdemo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * @author zhanglulu on 2020/8/13.
 * for
 */
class Utility {
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        if (context == null) {
            return isInBackground;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return isInBackground;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (runningProcesses == null) {
                return isInBackground;
            }
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo != null && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && processInfo.pkgList != null) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess != null && activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                            break;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = null;
            if (taskInfo != null && taskInfo.get(0) != null) {
                componentInfo = taskInfo.get(0).topActivity;
            }
            if (componentInfo != null && componentInfo.getPackageName() != null && componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

}
