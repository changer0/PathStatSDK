package com.yuewen.cooperate.pathstat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import java.util.*


/**
 * Created by zhanglulu on 2020/4/15.
 * for 跳转路径统计 SDK <br/>
 * 使用前请在 Application 初始化中调用 init()
 */
private const val TAG = "PathStatSDK"

class PathStatSDK private constructor() : Application.ActivityLifecycleCallbacks {
    companion object {
        @JvmStatic
        fun get() : PathStatSDK {
            return Holder.instance
        }
    }
    private object Holder {
        val instance = PathStatSDK()
    }

    /**
     * 配置信息
     */
    public lateinit var config: PathStatConfig
    /**
     * 路径统计会话 ID
     */
    public var sessionId = UUID.randomUUID().toString()
    /**
     * 当前序号
     */
    public var curOrder = 0

    /**
     * 当前 Activity
     */
    private var curActivity: Activity? = null

    /**
     * Activity 数量
     */
    private var activityNum: Int = 0

    /**
     * 当前 Fragment
     */
    private var curFragment: Fragment? = null
    /**
     * Fragment 数量
     */
    private var fragmentNum: Int = 0

    /**
     * 初始化方法
     */
    public fun init(config: PathStatConfig) {
        this.config = config
        config.application.registerActivityLifecycleCallbacks(this)
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
        statPathInfo(analyseStatPathInfo(activity))
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

    //----------------------------------------------------------------------------------------------
    // Fragment 生命周期回调
    private val fragmentSetUserVisibleHintTag = "path_stat_fragment_set_user_visible"
    public fun onFragmentCreate(fragment: Fragment?) {
        fragmentNum++
    }
    public fun onFragmentStart(fragment: Fragment?) {
        if (fragment === null) {
            return
        }
        if (curFragment === fragment) {
            return
        }


        val fragmentSetUserVisibleHint = fragment.arguments?.getBoolean(fragmentSetUserVisibleHintTag)?:false
        //如果已经被 setUserVisibleHint 托管，就无需走 onStart 曝光
        if (fragmentSetUserVisibleHint) {
            return
        }
        statPathInfo(analyseStatPathInfo(fragment))
        curFragment = fragment
    }
    public fun onFragmentDestroy(fragment: Fragment?) {
        fragmentNum--
        Log.d(TAG, "onFragmentDestroy fragmentNum: $fragmentNum")
        if (fragmentNum == 0) {
            curFragment = null//防止泄露
        }
    }

    /**
     * ViewPager 嵌套 Fragment 曝光
     */
    public fun onFragmentSetUserVisibleHint(fragment: Fragment, isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            statPathInfo(analyseStatPathInfo(fragment))
        }
        //通知 onStart 已被 setUserVisibleHint 托管
        var arguments = fragment.arguments
        if (arguments === null) {
            arguments = Bundle()
            fragment.arguments = arguments
        }
        arguments.putBoolean(fragmentSetUserVisibleHintTag, true)
    }
    // Fragment 生命周期回调 end
    //----------------------------------------------------------------------------------------------

    /**
     * 解析 PathStatInfo
     */
    public fun analyseStatPathInfo(target: Any):PathStatInfo {
        return if (target is IGetPathStatInfo) {
            target.getPathStatInfo()
        } else {
            //统计信息未设置，使用默认的 Fragment 类名
            PathStatInfo(target.javaClass.name)
        }
    }

    /**
     * 释放
     */
    private fun release() {
        curOrder = 0
        curActivity = null
        //返回键退出时，sessionId 需要重新生成
        sessionId = UUID.randomUUID().toString()
    }
    /**
     * 上报 PathInfo
     * 外部可手动调用，强制上报，针对一些非 Activity 切换场景
     */
    public fun statPathInfo(pathStatInfo: PathStatInfo): Boolean {
        if (pathStatInfo.needStat.not()) {
            //无需上报
            return false
        }
        val ascendOrder = ascendOrder()
        pathStatInfo.curOrder = ascendOrder
        pathStatInfo.sessionId = sessionId
        Log.d(TAG, "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${pathStatInfo.sessionId}")
        config.statListener(pathStatInfo)
        return true
    }
}