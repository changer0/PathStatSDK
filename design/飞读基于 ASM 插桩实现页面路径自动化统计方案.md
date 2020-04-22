## 需求背景

背景：飞读用户目前在端内页面流转路径是模糊态，为了便于掌握用户在端内轨迹和流量的路径，需要在端内老埋点内新增参数用以获知用户在应用的流量走向和习惯。

需求地址：http://tapd.oa.com/OPPO_Cooperation/prong/stories/view/1110103571857809492?url_cache_key=2b387c03d743af62e2a1566cb2bdaa6a&action_entry_type=stories

**PathStatSDK 就是为了在尽量减少对代码的浸入前提下，实现路径统计的自动化上报。**

## PathStatSDK 统计场景

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416125115.png)

## 流程设计：

![流程设计](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416150000.png)

## 基于 ASM 插桩实现方案

### 旧版方案存在的问题

为了解决获取 ViewPager 嵌套 Fragment 的问题，需要让使用方继承自固定的 ViewPager，另外还需要让 ViewPager 的 Adapter 实现自己特定的接口来获取这个 Adapter，对代码侵入太大，而且不灵活。

另外，要想监听 Fragment 的生命周期方法，需要使用方手动在 BaseFragment 中调用回调！

而且还有一种情况，例如飞读主页面中的 Tab 切换，需要手动触发上报，比较不灵活。

通过观察嵌套在 ViewPager Fragment 的方法发现，Fragment 的 setUserVisibleHint 方法会在 ViewPager 切换时调用且只有在显示时设置的参数值为 true。

基于此，我们可以采用目前流行的**面向切向编程**(AOP)方式来实现，通过 Hook 系统代码并进行插桩。

### 代码插桩

#### 使用 Transform API 获取 class 文件

为了实现代码插桩，我们需要自定义一个插件，并创建一个自定义的 Transform 类，用来读取打包生成的 class 文件。

![Transform](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200419105223.png)

> 每个 Transform 其实都是一个 Gradle 的 Task ， Android 编译器中的 TaskManager 会将每个 Transform 串联起来。第一个 Transform 接收来自 javac 编译的结果，以及拉取到本地的第三方依赖和 resource 资源。这些编译的中间产物在 Transform 链上流动，每个 Transform 节点都可以对 class 进行处理再传递到下一个 Transform 。我们自定义的 Transform 会插入到链的最前面。

#### 通过 ASM 实现插桩

实现流程：

![实现流程](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200422102512.png)


具体需要插桩的方法：

为了监听 Fragment 的生命周期方法，需要在 Fragment 类的生命周期方法后插入一段代码，用于监听 Fragment 的声明周期：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200422102603.png)

另外，也需要监听它的 setUserVisibleHint 方法的回调，实现 ViewPager 嵌套 Fragment 的曝光监听：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200422102635.png)

通过这样的方案就可以解决 ViewPager 嵌套 Fragment 的这种情况的曝光，以及像飞读主页面中 Tab 切换的曝光（都回调 setUserVisibleHint 方法）。

### 跨进程支持

PathStatSDK 一开始时持有上报序号 order，以及会话 sessionId，但是如果对于跨进程的页面，order 和 sessionId 会在每个进程各维护一个；

为了解决跨进程问题，通过 AIDL 实现一个 PageState.aidl，用来跨进程获取 sessionId 和 order 等页面状态信息。

在 PathStatSDK 初始化时绑定一个 PageStateService，用于保存这些信息，后续页面数据状态的变更全部通过该服务。

> 其中 sessionId 只允许在主进程创建一个，用于保证全局唯一。

### 类图

![类图](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200422145402.png)

> 插桩过程较为复杂 ~ ^ ~，后面专门为它做次分享！

### 特殊场景

问题1： 需要明确一下，独立的 Fragment 页面曝光时只会走生命周期方法，而 ViewPager 嵌套的 Fragment 即会走生命周期方法也会走 setUserVisibleHint 方法，所以需要解决重复曝光的问题。

解决：通过观察 Fragment 生命周期方法和 setUserVisibleHint 的调用时机，发现 setUserVisibleHint 执行顺序在 onStart 之前，所以可以通过在 setUserVisibleHint 中添加已曝光标记来解决，为了彻底解决 ViewPager 嵌套 Fragment 的场景，我把所涉及的场景统统列举了下来：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200420144236.png)

解决如下：

```
public fun onFragmentStart(fragment: Fragment?) {
    if (!serviceConnected) {
        serviceConnectListenerList.add(object : ServiceConnectListener {
            override fun onConnected() {
                onFragmentStart(fragment)
                serviceConnectListenerList.remove(this)
            }
        })
        return
    }
    if (fragment === null) {
        return
    }
    if (!fragment.userVisibleHint) {
        return//非显示状态
    }
    val alreadyStat = fragment.arguments?.getBoolean(fragmentAlreadyStat)?:false
    //如果已经被 setUserVisibleHint 托管，就无需走 onStart 曝光
    if (alreadyStat) {
        return
    }
    statPathInfo(analyseStatPathInfo(fragment))
}
```


```
public fun onFragmentStop(fragment: Fragment?) {
    //Fragment 退出时，将已经上报字段置为 false
    fragment?.arguments?.putBoolean(fragmentAlreadyStat, false)
}
```

```
/**
 * ViewPager 嵌套 Fragment 曝光
 */
public fun onFragmentSetUserVisibleHint(fragment: Fragment, isVisibleToUser: Boolean) {
    if (!serviceConnected) {
        serviceConnectListenerList.add(object : ServiceConnectListener {
            override fun onConnected() {
                onFragmentSetUserVisibleHint(fragment, isVisibleToUser)
                serviceConnectListenerList.remove(this)
            }
        })
        return
    }
    if (isVisibleToUser) {
        statPathInfo(analyseStatPathInfo(fragment))
        //通知 onStart 已被 setUserVisibleHint 托管
        var arguments = fragment.arguments
        if (arguments === null) {
            arguments = Bundle()
            fragment.arguments = arguments
        }
        arguments.putBoolean(fragmentAlreadyStat, true)
    }
}
```

问题2：在实际接入发现，有些非用作页面的 Fragment 会被使用，例如：

```
PathStatSDK: 上报序号：15,
上报 pn：com.bumptech.glide.manager.SupportRequestManagerFragment，
SessionId：0f92784c-0c11-4979-96a1-031f664ac350
```

解决：对此我们采用白名单和黑名单方式，白名单指得是可以上报包名前缀，黑名单指得是不可上报的黑名单前缀，例如：

**白名单：**

你可以指定某个包名下的页面上报，其他包名下不上报

```
pathConfig.addPageNameWhiteList("com.example.jumppathdemo")
```

> 特别的，如果不设置白名单，则表示所有的类都会上报

**黑名单：**

你也可以指定某个某个包名下的类不上报：

```
pathConfig.addPageNameBlackList("com.xx.xx")
```

### 附：PathStatPlugin 设计类图

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200422092950.png)

## Demo 地址

http://gitlab.inner.yuewen.local/zhanglulu/PathStatSDKDemo