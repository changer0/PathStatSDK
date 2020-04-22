package com.yuewen.cooperate.pathstat;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by zhanglulu on 2020/4/17.
 * for Hook ViewPager 的 setAdapter 方法
 */
public class HookHelper {
    private static final String TAG = "HookHelper";

    /**
     * Hook ViewPager 的 setAdapter
     * @param viewPagerObj
     */
    public static void hookViewPagerSetAdapter(Object viewPagerObj) {
        Log.d(TAG, "成功 Hook ViewPager setAdapter：" + viewPagerObj);
// TODO: p_zlulzhang 2020/4/18 这个监听现在已没有意义，保留代码

//        if (viewPagerObj instanceof ViewPager) {
//            ViewPager viewPager = ((ViewPager) viewPagerObj);
//            final PagerAdapter pagerAdapter = viewPager.getAdapter();
//            Log.d(TAG, "我拿到 Adapter 了！！！" + pagerAdapter);
//
//            if (viewPager.getCurrentItem() == 0) {
//                statPathInfo(pagerAdapter, 0);
//
//            }
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//                @Override
//                public void onPageScrollStateChanged(int state) {}
//
//                @Override
//                public void onPageSelected(int position) {
//                    Log.d(TAG, "我拿到 position 了: " + position);
//                    statPathInfo(pagerAdapter, position);
//                }
//            });
//        } else {
//            Function1<Object, Unit> customViewPager = PathStatSDK.get().getConfig().getCustomViewPager();
//            if (customViewPager != null) {
//                customViewPager.invoke(viewPagerObj);
//            } else {
//                Log.e(TAG, "setViewPagerAdapter: 你使用了非原生 ViewPager！，请配置 Config");
//            }
//        }
    }

    private static void statPathInfo(PagerAdapter adapter, int pos) {
        Fragment item = null;
        if (adapter instanceof FragmentStatePagerAdapter) {
            item = ((FragmentStatePagerAdapter) adapter).getItem(pos);
        } else if (adapter instanceof FragmentPagerAdapter) {
            item  = ((FragmentPagerAdapter) adapter).getItem(pos);
        } else {
            Log.e(TAG, "statPathInfo: 既然使用了原生的 ViewPager 就请使用系统的 FragmentAdapter" );
            return;
        }
        PathStatInfo pathStatInfo = PathStatSDK.get().analyseStatPathInfo(item);
        PathStatSDK.get().statPathInfo(pathStatInfo);
    }

    //----------------------------------------------------------------------------------------------
    // Hook Fragment

    /**
     * hook Fragment 的 setUserVisibleHint
     * @param fragment
     * @param isVisibleToUser
     */
    public static void hookFragmentSetUserVisibleHint(Fragment fragment, boolean isVisibleToUser) {
        Log.d(TAG, "成功 Hook Fragment setUserVisibleHint：isVisibleToUser：" + isVisibleToUser);
        PathStatSDK.get().onFragmentSetUserVisibleHint(fragment, isVisibleToUser);
    }
    /**
     * Hook Fragment 的 onCreate 方法
     * @param fragment
     */
    public static void hookFragmentOnCreate(Fragment fragment) {
        Log.d(TAG, "成功 Hook Fragment onCreate：" + fragment);
        PathStatSDK.get().onFragmentCreate(fragment);
    }

    /**
     * Hook Fragment 的 onStart 方法
     * @param fragment
     */
    public static void hookFragmentOnStart(Fragment fragment) {
        Log.d(TAG, "成功 Hook Fragment onStart：" + fragment);
        PathStatSDK.get().onFragmentStart(fragment);
    }

    /**
     * Hook Fragment 的 onStart 方法
     * @param fragment
     */
    public static void hookFragmentOnStop(Fragment fragment) {
        Log.d(TAG, "成功 Hook Fragment onStop：" + fragment);
        PathStatSDK.get().onFragmentStop(fragment);
    }

    /**
     * Hook Fragment 的 onDestroy 方法
     * @param fragment
     */
    public static void hookFragmentOnDestroy(Fragment fragment) {
        Log.d(TAG, "成功 Hook Fragment OnDestroy：" + fragment);
        PathStatSDK.get().onFragmentDestroy(fragment);
    }
    // Hook Fragment end
    //----------------------------------------------------------------------------------------------

}
