package com.yuewen.cooperate.pathstat

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process


/**
 * Created by zhanglulu on 2020/4/20.
 * for
 */
object Utils {
    /**
     * 当前进程是否是主进程
     * @return
     */
    public fun isMainProcess(application: Application): Boolean {
        try {
            val am = application
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfos =
                am.runningAppProcesses
            val mainProcessName: String = application.packageName
            val myPid = Process.myPid()
            if (processInfos != null) {
                for (info in processInfos) {
                    if (info.pid == myPid) {
                        if (mainProcessName == info.processName) {
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
        return false
    }


}