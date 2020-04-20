package com.yuewen.cooperate.pathstat

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.pathstatsdk.PageState
import java.util.*
import kotlin.collections.ArrayList


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
     * 服务是否连接成功
     */
    private var serviceConnected = false

    /**
     * 页面状态数据
     */
    private var pageState: PageState? = null

    /**
     * 没有连接时的 行为
     */
    private var serviceConnectListenerList = mutableListOf<ServiceConnectListener>()
    /**
     * 配置信息
     */
    public lateinit var config: PathStatConfig
    /**
     * 初始化方法
     */
    public fun init(config: PathStatConfig) {
        this.config = config
        config.application.registerActivityLifecycleCallbacks(this)
        bindService(config.application)
    }

    //----------------------------------------------------------------------------------------------
    // Activity 生命周期回调
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (!serviceConnected) {
            serviceConnectListenerList.add(object : ServiceConnectListener {
                override fun onConnected() {
                    onActivityCreated(activity, savedInstanceState)
                    serviceConnectListenerList.remove(this)
                }
            })
            return
        }
        val activityNum = pageState!!.activityNum+1
        pageState!!.activityNum = activityNum
        Log.d(TAG, "onActivityCreated，activityNum: $activityNum")
    }
    override fun onActivityStarted(activity: Activity) {
        if (!serviceConnected) {
            Log.e(TAG, "onActivityStarted Service 未连接！")
            serviceConnectListenerList.add(object : ServiceConnectListener {
                override fun onConnected() {
                    Log.e(TAG, "onActivityStarted Service 监听连接成功！")
                    onActivityStarted(activity)
                    serviceConnectListenerList.remove(this)
                }
            })
            return
        }
        statPathInfo(analyseStatPathInfo(activity))
    }
    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (!serviceConnected) {
            serviceConnectListenerList.add(object : ServiceConnectListener {
                override fun onConnected() {
                    onActivityDestroyed(activity)
                    serviceConnectListenerList.remove(this)
                }
            })
            return
        }
        val activityNum = pageState!!.activityNum-1
        pageState!!.activityNum = activityNum
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
    //是否已经上报
    private val fragmentAlreadyStat = "path_stat_fragment_already_stat"
    public fun onFragmentCreate(fragment: Fragment?) {
        //暂时预留
    }
    public fun onFragmentStart(fragment: Fragment?) {
        if (!serviceConnected) {
            serviceConnectListenerList.add(object : ServiceConnectListener {
                override fun onConnected() {
                    onFragmentStart(fragment)
                    serviceConnectListenerList.remove(this)
                }
            })
            return
        }
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
        if (!serviceConnected) {
            serviceConnectListenerList.add(object : ServiceConnectListener {
                override fun onConnected() {
                    onFragmentSetUserVisibleHint(fragment, isVisibleToUser)
                    serviceConnectListenerList.remove(this)
                }
            })
            return
        }
        if (isVisibleToUser) {
            val alreadyStat = statPathInfo(analyseStatPathInfo(fragment))
            //通知 onStart 已被 setUserVisibleHint 托管
            var arguments = fragment.arguments
            if (arguments === null) {
                arguments = Bundle()
                fragment.arguments = arguments
            }
            arguments.putBoolean(fragmentAlreadyStat, alreadyStat)
        }
    }
    // Fragment 生命周期回调 end
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    // PathStatSDKStateService 用于保存当前 SDK 的所有状态

    private val serviceConnection = object :ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            serviceConnected = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            pageState = PageState.Stub.asInterface(service)
            if (Utils.isMainProcess(config.application)) {
                pageState!!.sessionId = UUID.randomUUID().toString()
            }
            serviceConnected = true
            val tempListener = ArrayList<ServiceConnectListener>(serviceConnectListenerList)
            for (serviceConnectListener in tempListener) {
                serviceConnectListener.onConnected()
            }
            Log.d(TAG, "Service 绑定成功，Utils.isMainProcess: ${Utils.isMainProcess(config.application)}")
        }

    }
    private fun bindService(application: Application) {
        //子线程中绑定服务
        Thread(Runnable {
            val intent = Intent(application,  PageStateService::class.java)
            application.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)
            Log.d(TAG, "开始 bindService：${Utils.isMainProcess(config.application)}")
        }).start()
    }
    // PathStatSDKStateService end
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
        pageState!!.order = 0
        if (Utils.isMainProcess(config.application)) {
            //主进程赋值一次
            pageState!!.sessionId = UUID.randomUUID().toString()
        }
    }
    /**
     * 上报 PathInfo
     * 外部可手动调用，强制上报，针对一些非 Activity 切换场景
     */
    public fun statPathInfo(pathStatInfo: PathStatInfo): Boolean {
        if (serviceConnected.not()) {
            Log.e(TAG, "Service 未连接，正常不应该出现该场景！")
            return false
        }
        if (pageState == null) {
            Log.e(TAG, "pathStatSDKState 为空，正常不应该出现该场景！")
            return false
        }
        if (pathStatInfo.needStat.not()) {
            //无需上报
            return false
        }
        //如果该类名在黑名单内，不上报
        if (config.containsPackageBlackList(pathStatInfo.pn)) {
            return false
        }
        //如果该类名不在白名单内，不上报
        if (!config.containsPackageWhiteList(pathStatInfo.pn)) {
            return false
        }
        //上报序号增 1
        pathStatInfo.curOrder =  pageState!!.order+1
        pageState!!.order = pathStatInfo.curOrder
        pathStatInfo.sessionId = pageState!!.sessionId
        Log.d(TAG, "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${pathStatInfo.sessionId}")
        config.statListener(pathStatInfo)
        return true
    }
}