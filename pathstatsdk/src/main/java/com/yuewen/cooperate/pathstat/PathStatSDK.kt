package com.yuewen.cooperate.pathstat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import com.yuewen.cooperate.pathstat.pagestate.PageState

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
     * 页面状态数据
     */
    private lateinit var pageState: PageState
    /**
     * 配置信息
     */
    private lateinit var config: PathStatConfig

    /**
     * 初始化方法
     */
    public fun init(config: PathStatConfig) {
        this.config = config
        config.application.registerActivityLifecycleCallbacks(this)
        pageState = PageState(config.application)
        Log.d(TAG, "init: 当前是否主进程：${Utils.isMainProcess(config.application)}")
        reset()//首次进入先重置一下 SessionId 和 order
    }

    //----------------------------------------------------------------------------------------------
    // Activity 生命周期回调
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val activityNum = pageState.obtainActivityNumAndAscend()
        Log.d(TAG, "onActivityCreated，activityNum: $activityNum")
    }
    override fun onActivityStarted(activity: Activity) {
        statPathInfo(analyseStatPathInfo(activity))
    }
    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {
        val activityNum = pageState.obtainActivityNumAndDescend()
        Log.d(TAG, "onActivityDestroyed, activityNum: $activityNum")
        activityNum?.apply {
            if (this <= 0) {
                reset()//当 Activity 不存在时，释放(< 0 为异常场景)
            }
        }
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    // 生命周期回调 end
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    // Fragment 生命周期回调
    //是否已经上报
    private val fragmentAlreadyStat = "path_stat_fragment_already_stat"
    public fun onFragmentCreate(fragment: Fragment?) {
        //暂时预留
    }
    public fun onFragmentStart(fragment: Fragment?) {
        if (fragment === null) {
            return
        }
        if (!fragment.userVisibleHint) {
            return//非显示状态
        }
        val alreadyStat = fragment.arguments?.getBoolean(fragmentAlreadyStat)?:false
        //如果已经被 setUserVisibleHint 托管，就无需走 onStart 曝光
        if (alreadyStat) {
            return
        }
        statPathInfo(analyseStatPathInfo(fragment))
    }

    public fun onFragmentStop(fragment: Fragment?) {
        //Fragment 退出时，将已经上报字段置为 false
        fragment?.arguments?.putBoolean(fragmentAlreadyStat, false)
    }

    public fun onFragmentDestroy(fragment: Fragment?) {
        //暂时预留
    }

    /**
     * ViewPager 嵌套 Fragment 曝光
     */
    public fun onFragmentSetUserVisibleHint(fragment: Fragment, isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            statPathInfo(analyseStatPathInfo(fragment))
            //通知 onStart 已被 setUserVisibleHint 托管
            var arguments = fragment.arguments
            //fix Fragment already active and state has been saved
            if (arguments === null && !fragment.isStateSaved) {
                arguments = Bundle()
                fragment.arguments = arguments
            }
            arguments?.putBoolean(fragmentAlreadyStat, true)
        }
    }
    // Fragment 生命周期回调 end
    //----------------------------------------------------------------------------------------------

    /**
     * 解析 PathStatInfo
     */
    public fun analyseStatPathInfo(target: Any):PathStatInfo {

        val pathStatInfo = if (target is IGetPathStatInfo) {
            target.getPathStatInfo()
        } else {
            //统计信息未设置，使用默认的 Fragment 类名
            PathStatInfo(target.javaClass.name)
        }
        pathStatInfo.className = target.javaClass.name
        return pathStatInfo
    }

    /**
     * 重置状态：包括 order 置为 0；重置 sessionId
     */
    private fun reset() {
        if (Utils.isMainProcess(config.application)) {
            pageState.reset()
        }
    }

    /**
     * 上报 PathInfo
     * 外部可手动调用，强制上报，针对一些非 Activity 切换场景
     */
    public fun statPathInfo(pathStatInfo: PathStatInfo) {
        if (pathStatInfo.needStat.not()) {
            //无需上报
            return
        }
        // className 可能为空，手动上报！！
        if (pathStatInfo.className.isNotEmpty()) {
            //如果该类名在黑名单内，不上报
            if (config.containsPackageBlackList(pathStatInfo.className)) {
                return
            }
            //如果该类名不在白名单内，不上报
            if (!config.containsPackageWhiteList(pathStatInfo.className)) {
                return
            }
        }
        //未实现接口警告
        if (TextUtils.equals(pathStatInfo.className, pathStatInfo.pn)) {
            Log.w(TAG, "注意：${pathStatInfo.className} 未实现 IGetPathStatInfo 接口，将使用类名进行上报！")
        }
        //上报序号增 1
        val order = pageState.obtainOrderAndAscend()
        val sessionId = pageState.obtainSessionId()
        notNull(sessionId, order) {
            pathStatInfo.curOrder =  order!!
            pathStatInfo.sessionId = sessionId!!
            Log.d(TAG, "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${pathStatInfo.sessionId}")
            config.statListener(pathStatInfo)
        }
    }
    inline fun <R> notNull(vararg args: Any?, block: () -> R) =
        when (args.filterNotNull().size) {
        args.size -> block()
        else -> null
    }
}