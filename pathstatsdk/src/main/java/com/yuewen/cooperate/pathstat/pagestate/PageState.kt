package com.yuewen.cooperate.pathstat.pagestate

import android.app.Application
import android.net.Uri
import android.os.Bundle
import com.yuewen.cooperate.pathstat.Utils
import java.util.*

/**
 * @author zhanglulu on 2020/8/20.
 * for 页面状态保存
 */
class PageState(val application: Application) {

    companion object {
        const val RESET = "RESET"
        const val GET_SESSION_ID = "GET_SESSION_ID"
        const val GET_ORDER_AND_ASCEND = "GET_ORDER_AND_ASCEND"
        const val GET_ACTIVITY_NUM_AND_ASCEND = "GET_ACTIVITY_NUM_AND_ASCEND"
        const val GET_ACTIVITY_NUM_AND_DESCEND = "GET_ACTIVITY_NUM_AND_DESCEND"

        const val RESULT = "result"
    }
    private val resolver = application.contentResolver

    private val providerUri = Uri.parse("content://com.yuewen.cooperate.pathstat.provider")

    /**
     * 会话 ID
     */
    private var sessionId: String = ""
    /**
     * 当前序号
     */
    private var order: Int = 0
    /**
     * Activity 数量
     */
    private var activityNum: Int = 0


    init {
        //注意方法回调都是在主进程！！！！！
        ProcessorPool.register(object :IProcessor {
            override fun getMethodName(): String {
                return RESET
            }
            override fun process(arg: String?, extras: Bundle?): Bundle? {
                order = 0
                sessionId = UUID.randomUUID().toString()
                return null
            }
        })
        ProcessorPool.register(object : IProcessor {
            override fun getMethodName(): String {
                return GET_SESSION_ID
            }
            override fun process(arg: String?, extras: Bundle?): Bundle? {
                val bundle = Bundle()
                bundle.putString(RESULT, sessionId)
                return bundle
            }
        })
        ProcessorPool.register(object : IProcessor {
            override fun getMethodName(): String {
                return GET_ORDER_AND_ASCEND
            }
            override fun process(arg: String?, extras: Bundle?): Bundle? {
                val bundle = Bundle()
                bundle.putInt(RESULT, ++order)
                return bundle
            }
        })
        ProcessorPool.register(object : IProcessor {
            override fun getMethodName(): String {
                return GET_ACTIVITY_NUM_AND_ASCEND
            }
            override fun process(arg: String?, extras: Bundle?): Bundle? {
                val bundle = Bundle()
                bundle.putInt(RESULT, ++activityNum)
                return bundle
            }
        })
        ProcessorPool.register(object : IProcessor {
            override fun getMethodName(): String {
                return GET_ACTIVITY_NUM_AND_DESCEND
            }
            override fun process(arg: String?, extras: Bundle?): Bundle? {
                val bundle = Bundle()
                bundle.putInt(RESULT, --activityNum)
                return bundle
            }
        })
    }

    /**
     * 重置
     */
    fun reset() {
        call(RESET)
    }

    /**
     * 获取 SessionID
     */
    fun obtainSessionId(): String? {
        return call(GET_SESSION_ID)?.getString(RESULT)
    }

    /**
     * 获取序号并 + 1
     */
    fun obtainOrderAndAscend() : Int? {
        return call(GET_ORDER_AND_ASCEND)?.getInt(RESULT)
    }

    /**
     * 获取 Activity 个数并 + 1
     */
    fun obtainActivityNumAndAscend() : Int? {
        return call(GET_ACTIVITY_NUM_AND_ASCEND)?.getInt(RESULT)
    }

    /**
     * 获取 Activity 个数并 - 1
     */
    fun obtainActivityNumAndDescend() : Int? {
        return call(GET_ACTIVITY_NUM_AND_DESCEND)?.getInt(RESULT)
    }

    private fun call(method: String): Bundle?{
        if (Utils.isMainProcess(application)) {
            return ProcessorPool.getMethod(method)?.process(null, null)
        }
        return resolver.call(providerUri, method, null, null)
    }
}