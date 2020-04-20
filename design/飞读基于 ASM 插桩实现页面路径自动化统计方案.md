## 需求背景

背景：飞读用户目前在端内页面流转路径是模糊态，为了便于掌握用户在端内轨迹和流量的路径，需要在端内老埋点内新增参数用以获知用户在应用的流量走向和习惯。

需求地址：http://tapd.oa.com/OPPO_Cooperation/prong/stories/view/1110103571857809492?url_cache_key=2b387c03d743af62e2a1566cb2bdaa6a&action_entry_type=stories

**PathStatSDK 就是为了在尽量减少对代码的浸入前提下，实现路径统计的自动化上报。**

## PathStatSDK 统计场景

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416125115.png)

## 流程设计：

![流程设计](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416150000.png)

## PathStatSDK 实现方案

## 旧版方案

**Activity 跳转**

借助 Application 的 registerActivityLifecycleCallbacks 方法，监听所有 Activity 的生命周期方法，下面是两个页面跳转时生命周期的回调方法：

A 页面跳转 B 页面

![A->B](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416150117.png)

B 页面返回 A 页面

![B->A](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416150129.png)

通过上面的 Activity 的生命周期的流程，我们可以在当前页面的 onStart 时进行进行全局的监听，实现 Activity 的跳转路径的统计。

**Fragment 页面**

对于 Fragment 页面，可以参考 Activity 的方式，监听 Fragment 的 onStart 方法，但是当我们使用 ViewPager 加载 Fragment 时，由于 ViewPager 的预加载功能，会提前执行 Fragment 的生命周期方法：

![ViewPager 预加载](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416152533.png)

为此我们可以直接继承 ViewPager，实现一个我们可以控制的 PathStatViewPager，通过监听 OnPageChangeListener 实现上报。

**手动上报**

即便是解决了 Activity 和 Fragment 的曝光问题，但是针对一些特殊页面，例如：阅读尾页和搜索结果页，在 Android 中都是通过 View 实现的，而数据侧则会理解为一个独立的页面，所以也会暴露一个手动上报的方法。

> 特别的，针对 **H5** 页面的上报，可以通过实现 JS 接口调用手动上报即可

![搜索结果页](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416152755.png)


### 类图设计

![类图](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200416155120.png)

### 特殊场景

问题1：当 ViewPager 嵌套 Fragment 时，使用 PathStatViewPager 与 onStart 生命周期重复上报问题。

解决：

为了避免这种场景的重复上报，PathStatSDK.kt 中会通过递归查找当前 ViewPager 是否使用是的我们的专用于上报的 ViewPager。

```
private fun isViewPageFragment(view: View?):Boolean {
    if (view == null) {
        return false
    }
    if (view is PathStatViewPager) {
        return true
    }
    if (view.parent !is View) {
        return false
    }
    return isViewPageFragment(view.parent as View)

}
```

问题2：当 ViewPager 嵌套 Fragment 时，外层嵌套的 Activity/Fragment 重复上报问题。

解决：

可以让该 Activity/Fragment 实现 IGetPathStatInfo 接口，并传入 needStat = false。

问题3：当用户点击 Home 键时，为了防止当前页面的 Activity/Fragment 的 onStart 方法上报，需要记录一个当前的 Activity/Fragment 变量，由于 PathStatSDK 是单例所以就会引起泄漏。

解决：

记录 Activity/Fragment 的数量，当数量为 0 时，当前变量指向 null。


## 基于 ASM 插桩实现方案

### 旧版方案存在的问题

为了解决获取 ViewPager 嵌套 Fragment 的问题，需要让使用方继承自固定的 ViewPager，另外还需要让 ViewPager 的 Adapter 实现自己特定的接口来获取这个 Adapter，对代码侵入太大，而且不灵活。

另外，要想监听 Fragment 的生命周期方法，需要使用方手动在 BaseFragment 中调用回调！

而且还有一种情况，例如飞读主页面中的 Tab 切换，需要手动触发上报，比较不灵活。

通过观察嵌套在 ViewPager Fragment 的方法发现，Fragment 的 setUserVisibleHint 方法会在 ViewPager 切换时调用且只有在显示时设置的参数值为 true。

基于此，我们可以采用目前流行的**面向切向编程**(AOP)方式来实现，通过 Hock 系统代码并进行插桩。

### 代码插桩

#### 使用 Transform API 获取 class 文件

为了实现代码插桩，我们需要自定义一个插件，并创建一个自定义的 Transform 类，用来读取打包生成的 class 文件。

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200419105223.png)

> 每个 Transform 其实都是一个 Gradle 的 Task ， Android 编译器中的 TaskManager 会将每个 Transform 串联起来。第一个 Transform 接收来自 javac 编译的结果，以及拉取到本地的第三方依赖和 resource 资源。这些编译的中间产物在 Transform 链上流动，每个 Transform 节点都可以对 class 进行处理再传递到下一个 Transform 。我们自定义的 Transform 会插入到链的最前面。

#### 通过 ASM 实现插桩

实现流程：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200419113731.png)


具体需要插桩的方法：

为了监听 Fragment 的生命周期方法，需要在 Fragment 类的生命周期方法后插入一段代码，用于监听 Fragment 的声明周期：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200419195251.png)

另外，也需要监听它的 setUserVisibleHint 方法的回调，实现 ViewPager 嵌套 Fragment 的曝光监听：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200418223539.png)

通过这样的方案就可以解决 ViewPager 嵌套 Fragment 的这种情况的曝光，以及像飞读主页面中 Tab 切换的曝光（都回调 setUserVisibleHint 方法）。

### 类图

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200418225109.png)

> 插桩过程较为复杂 ~ ^ ~，后面专门为它做次分享！

### 特殊场景

问题1： 需要明确一下，独立的 Fragment 页面曝光时只会走生命周期方法，而 ViewPager 嵌套的 Fragment 即会走生命周期方法也会走 setUserVisibleHint 方法，所以需要解决重复曝光的问题。

解决：通过观察 Fragment 生命周期方法和 setUserVisibleHint 的调用时机，发现 setUserVisibleHint 执行顺序在 onStart 之前，所以可以通过在 setUserVisibleHint 中添加已曝光标记来解决，为了彻底解决 ViewPager 嵌套 Fragment 的场景，我把所涉及的场景统统列举了下来：

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200420144236.png)

解决如下：

```
public fun onFragmentStart(fragment: Fragment?) {
    if (fragment === null) {
        return
    }
    if (curFragment === fragment) {
        return//相同 Fragment
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
    curFragment = fragment
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
    if (isVisibleToUser) {
        val alreadyStat = statPathInfo(analyseStatPathInfo(fragment))
        //通知 onStart 已被 setUserVisibleHint 托管
        var arguments = fragment.arguments
        if (arguments === null) {
            arguments = Bundle()
            fragment.arguments = arguments
        }
        arguments.putBoolean(fragmentAlreadyStat, alreadyStat)
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

![](https://gitee.com/luluzhang/ImageCDN/raw/master/blog/20200420120447.png)

## Demo 地址

http://gitlab.inner.yuewen.local/zhanglulu/PathStatSDKDem