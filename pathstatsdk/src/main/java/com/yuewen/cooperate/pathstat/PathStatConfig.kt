package com.yuewen.cooperate.pathstat

import android.app.Application

/**
 * Created by zhanglulu on 2020/4/17.
 * for 上报配置
 */
class PathStatConfig (var application: Application, var statListener: (statInfo: PathStatInfo) -> Unit) {
    /**
     * 黑名单包名
     */
    private var packageNameBlackList = mutableListOf<String>()

    /**
     * 白名单包名
     */
    private var packageNameWhiteList = mutableListOf<String>()

    init {
        //需要固定的可以往这儿添加
        packageNameBlackList.add("com.bumptech.glide.manager.SupportRequestManagerFragment")
    }
    public fun addPageNameBlackList(className: String) {
        packageNameBlackList.add(className)
    }
    public fun addPageNameWhiteList(className: String) {
        packageNameWhiteList.add(className)
    }

    fun containsPackageBlackList(pageName: String): Boolean {
        for (p in packageNameBlackList) {
            if (pageName.startsWith(p)) {
                return true
            }
        }
        return false
    }

    fun containsPackageWhiteList(pageName: String): Boolean {
        //特别的，如果用户不设置白名单，默认的都上报
        if (packageNameWhiteList.size <= 0) {
            return true
        }
        for (p in packageNameWhiteList) {
            if (pageName.startsWith(p)) {
                return true
            }
        }
        return false
    }
}