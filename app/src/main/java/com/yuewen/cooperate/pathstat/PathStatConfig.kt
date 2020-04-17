package com.yuewen.cooperate.pathstat

import android.app.Application

/**
 * Created by zhanglulu on 2020/4/17.
 * for 上报配置
 */
class PathStatConfig (var application: Application, var statListener: (statInfo: PathStatInfo) -> Unit) {
    var customViewPagerClass: MutableList<String>? = null
    var customViewPager: ((Any) -> Unit)? = null
}