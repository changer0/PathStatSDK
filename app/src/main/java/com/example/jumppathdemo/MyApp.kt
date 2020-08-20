package com.example.jumppathdemo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.qq.reader.widget.WebAdViewPager
import com.yuewen.cooperate.pathstat.*

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
        val pathConfig = PathStatConfig(this) { pathStatInfo ->
            var pid = 0
            if (pathStatInfo is ReadPathStatInfo) {
                pid = pathStatInfo.pid
            }
            Toast.makeText(
                this@MyApp,
                "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，pid: ${pid}, SessionId：${pathStatInfo.sessionId}",
                Toast.LENGTH_SHORT
            ).show()
        }
        pathConfig.addPageNameBlackList("com.qq.reader")
        //pathConfig.addPageNameWhiteList("com.example.jumppathdemo")
        PathStatSDK.get().init(pathConfig)
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
        Log.d(TAG, "isAppIsInBackground: ${Utility.isAppIsInBackground(activity.applicationContext)}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "onActivityDestroyed: ${activity.javaClass.name}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(TAG, "onActivitySaveInstanceState: ${activity.javaClass.name}")
    }
}