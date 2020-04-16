package com.example.jumppathdemo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.jumppathdemo.pathstat.PathStatSDK

/**
 * Created by zhanglulu on 2020/4/15.
 * for
 */
private const val TAG = "MyApp"
class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: Application
        public fun get() :Application {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        PathStatSDK.get().init(this) { pathStatInfo->
            Toast.makeText(this,
                "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${pathStatInfo.sessionId}", Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, "APP_SESSION_ID: ${PathStatSDK.get().sessionId}")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: ${activity.javaClass.name}")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: ${activity.javaClass.name}")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "onActivityResumed: ${activity.javaClass.name}")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "onActivityPaused: ${activity.javaClass.name}")
    }
    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped: ${activity.javaClass.name}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "onActivityDestroyed: ${activity.javaClass.name}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState: ${activity.javaClass.name}")
    }
}