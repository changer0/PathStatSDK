package com.yuewen.cooperate.pathstat

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.pathstatsdk.PageState

/**
 * 为了解决跨进程的场景，该 Service 用于保存当前 SDK 的状态
 * 页面状态数据
 */
class PageStateService : Service() {
    /**
     * 会话 ID
     */
    var sessionId: String = ""
    /**
     * 当前序号
     */
    var order: Int = 0
    /**
     * Activity 数量
     */
    var activityNum: Int = 0
    /**
     * Fragment 数量
     */
    var fragmentNum: Int = 0
    
    private val iBinder = object:  PageState.Stub() {
        override fun setSessionId(sessionId: String) {
            this@PageStateService.sessionId = sessionId
        }

        override fun getSessionId(): String {
            return this@PageStateService.sessionId
        }

        override fun getOrder(): Int {
            return this@PageStateService.order
        }

        override fun setOrder(order: Int) {
            this@PageStateService.order = order
        }

        override fun getFragmentNum(): Int {
            return this@PageStateService.fragmentNum
        }

        override fun setfragmentNum(num: Int) {
            this@PageStateService.fragmentNum = num
        }

        override fun getActivityNum(): Int {
            return this@PageStateService.activityNum
        }

        override fun setActivityNum(num: Int) {
            this@PageStateService.activityNum = num
        }
    }
    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }
}
