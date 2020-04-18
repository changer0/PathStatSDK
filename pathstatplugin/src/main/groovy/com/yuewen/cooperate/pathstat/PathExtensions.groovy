package com.yuewen.cooperate.pathstat

import org.gradle.api.Project

class PathExtensions {
    /**
     * 保存开发者自己构建的 ViewPager
     */
    def customViewPagerClass = String[]
    def isDebug = "false"
    PathExtensions(Project android) {

    }
}