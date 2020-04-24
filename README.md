# PathStatSDK 使用文档

> 用于统计用户跳转路径，支持跨进程页面路径统计，无缝接入

## 一、引入依赖

在根目录的 build.gradle 中配置插件：

``` groovy

buildscript {
    dependencies {
        classpath "com.yuewen.cooperate.pathstat:pathstatplugin:$new_version"
    } 
}
```

子模块 build.gradle 中配置：

``` groovy
//pathStat 插件
apply plugin: 'path-stat-plugin'
pathStatPlugin.isDebug = true

dependencies {
    implementation 'com.yuewen.cooperate.pathstat:pathstatsdk:$new_version'
}
```

## 二、Application 初始化

在 Application 的 onCreate 入口中初始化 PathStatSDK：

``` kotlin
val pathConfig = PathStatConfig(this) { pathStatInfo ->
    Toast.makeText( this,
        "上报序号：${pathStatInfo.curOrder}, 上报 pn：${pathStatInfo.pn}，SessionId：${pathStatInfo.sessionId}",Toast.LENGTH_SHORT
    ).show()
}
PathStatSDK.get().init(pathConfig)
```

> 需要使用方自己实现 statListener 接口，以自己工程的埋点上报方式完成上报！

## 三、特殊场景上报

1、 自定义 pn

``` kotlin
class A : AppCompatActivity(), IGetPathStatInfo{
    //...
    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo("A")
    }
}
```

2、 屏蔽当前页面上报

目前有两种方式：

第一种：当前 Activity/Fragment 实现 IGetPathStatInfo 接口，传入 needStat=false

``` kotlin
class ViewPagerActivity : FragmentActivity(), IGetPathStatInfo {
    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo(false)
    }
}
```

第二种：黑白名单设置方式，初始化时调用 PathStatConfig 时调用 addPageNameBlackList 方法，传入需要屏蔽的包名：

``` kotlin
val pathConfig = PathStatConfig(this){
    //...
}
pathConfig.addPageNameBlackList("com.xx.xx")
PathStatSDK.get().init(pathConfig)
```

或者如果只想在某些包名下的类进行上报，可以通过设置白名单的方式，特别的，如果用户不设置白名单，默认的都上报：

``` kotlin
val pathConfig = PathStatConfig(this){
    //...
}
pathConfig.addPageNameWhiteList("com.xx.xx")
PathStatSDK.get().init(pathConfig)
```

> 【注】有些非自定义的类只能采用第二种方式实现，例如第三方库中的一些 Fragment；另外，如果使用第二种方式需要注意 **避免混淆**！

3、 手动上报

针对某些特殊场景，提供手动上报方式，调用 statPathInfo 即可：

``` kotlin
PathStatSDK.get().statPathInfo(PathStatInfo("手动上报"))
```


## 四、注意事项

1、如果不自定义页面名称，将会返回当前页面对应类的包名+类名，所以如果不设置页面名称则需要要求当前页面对应的类不要混淆；

2、有些 Tab 切换 Fragment 的场景，例如主页面中点击底部 Tab 切换 Fragment，则需要使用方手动调用 Fragment 的 setUserVisibleHint 方法。

3、Log 过滤 Tag ：PathStatSDK

4、如果出现某个页面未实现 IGetPathStatInfo 接口，日志中将会抛出下面的警告：

```
PathStatSDK: 注意：com.example.jumppathdemo.MainActivity 未实现 IGetPathStatInfo 接口，将使用类名进行上报！
```