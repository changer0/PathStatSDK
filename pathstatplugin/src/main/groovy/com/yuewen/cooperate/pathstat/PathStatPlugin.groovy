package com.yuewen.cooperate.pathstat

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.yuewen.cooperate.pathstat.PathStatTransform

/**
 * 用于 PathStatSDK 的插件
 */
class PathStatPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "===================="
        println "PathStatPlugin Start"
        println "===================="
        def android = project.extensions.getByType(AppExtension)
        //注册 Transform，操作 class 文件
        android.registerTransform(new PathStatTransform())
    }
}