package com.yuewen.cooperate.pathstat.pagestate
import android.os.Bundle

/**
 * @author zhanglulu on 2020/8/20.
 * for 跨进程结接口
 */
interface IProcessor {
    fun getMethodName(): String
    fun process(arg: String?, extras: Bundle?): Bundle?
}