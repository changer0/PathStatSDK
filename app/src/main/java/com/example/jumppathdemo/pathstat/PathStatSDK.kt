package com.example.jumppathdemo.pathstat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.util.*


/**
 * Created by zhanglulu on 2020/4/15.
 * for 跳转路径统计 SDK <br/>
 * 使用前请在 Application 初始化中调用 init()
 */
private const val TAG = "PathStatSDK"

class PathStatSDK private constructor() : Application.ActivityLifecycleCallbacks {
    companion object {
        fun get() : PathStatSDK {
            return Holder.instance
        }
    }
    private object Holder {
        val instance = PathStatSDK()
    }
    /**
     * 路径统计会话 ID
     */
    public val sessionId = UUID.randomUUID().toString()
    /**
     * 当前序号
     */
    public var curOrder = 0

    /**
     * 当前 Activity
     */
    private var curActivity: Activity? = null

    /**
     * 当前 Activity 数量
     */
    private var activityNum: Int = 0

    /**
     * 初始化方法
     */
    public fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 升序号
     */
    private fun ascendOrder(): Int {
        return ++curOrder
    }

    //----------------------------------------------------------------------------------------------
    // Activity 生命周期回调
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityNum++
    }
    override fun onActivityStarted(activity: Activity) {
        if (curActivity === activity) {
            //如果两次 Started 对象相同则不统计
            return
        }
        statPathInfo(analyseActivityStatPathInfo(activity))
        curActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {

    }
    override fun onActivityPaused(activity: Activity) {

    }
    override fun onActivityStopped(activity: Activity) {

    }
    override fun onActivityDestroyed(activity: Activity) {
        activityNum--
        Log.d(TAG, "onActivityDestroyed, activityNum: $activityNum")
        if (activityNum == 0) {
            release()//当 Activity 不存在时，释放
        }
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }
    // 生命周期回调 end
    //----------------------------------------------------------------------------------------------

    /**
     * 解析 Activity 中的 PathStatInfo
     */
    private fun analyseActivityStatPathInfo(activity: Activity):PathStatInfo {
        return if (activity is IGetPathStatInfo) {
            (activity as IGetPathStatInfo).getPathStatInfo()
        } else {
            //统计信息未设置，使用默认的 Activity 类名
            PathStatInfo(activity.javaClass.name)
        }
    }

    /**
     * 上报 PathInfo
     * 外部可手动调用，强制上报，针对一些非 Activity 切换场景
     */
    public fun statPathInfo(pathStatInfo: PathStatInfo) {
        val ascendOrder = ascendOrder()
        Log.d(TAG, "上报序号：${ascendOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${sessionId}")
    }

    /**
     * 释放
     */
    private fun release() {
        curOrder = 0
        curActivity = null
    }
}