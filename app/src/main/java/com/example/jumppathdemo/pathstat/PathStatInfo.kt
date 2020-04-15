package com.example.jumppathdemo.pathstat

/**
 * Created by zhanglulu on 2020/4/15.
 * for 路径统计信息
 */
class PathStatInfo(var pn: String = "") {

    /**
     * 是否需要上报
     */
    constructor(needStat: Boolean) : this() {
        this.needStat = needStat
    }

    /**
     * 是否需要上报
     */
    var needStat: Boolean = true
}