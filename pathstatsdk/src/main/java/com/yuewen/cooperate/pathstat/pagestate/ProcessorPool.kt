package com.yuewen.cooperate.pathstat.pagestate

import android.app.Application
import android.content.ContentResolver
import android.net.Uri

/**
 * @author zhanglulu on 2020/8/20.
 * for 页面状态辅助类
 */
object ProcessorPool {
    private val sMethodMap = HashMap<String, IProcessor>()

    fun getMethod(methodName: String): IProcessor? {
        return sMethodMap[methodName]
    }
    fun register(processor: IProcessor) {
        sMethodMap[processor.getMethodName()] = processor
    }
}