package com.yuewen.cooperate.pathstat

import android.app.Application

/**
 * Created by zhanglulu on 2020/4/17.
 * for 上报配置
 */
class PathStatConfig (var application: Application, var statListener: (statInfo: PathStatInfo) -> Unit) {
    /**
     * 需要屏蔽的类名
     */
    var avoidClassNames = mutableListOf<String>()
    init {
        //需要固定的可以往这儿添加
        avoidClassNames.add("com.bumptech.glide.manager.SupportRequestManagerFragment")
    }
    public fun addAvoidClassName(className: String) {
        avoidClassNames.add(className)
    }
}