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
 * for Hock ViewPager 的 setAdapter 方法
 */
public class HockHelper {
    private static final String TAG = "HockHelper";

    /**
     * Hock ViewPager 的 setAdapter
     * @param viewPagerObj
     */
    public static void hockViewPagerSetAdapter(Object viewPagerObj) {
        Log.d(TAG, "成功 Hock ViewPager setAdapter：" + viewPagerObj);
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
    // Hock Fragment

    /**
     * hock Fragment 的 setUserVisibleHint
     * @param fragment
     * @param isVisibleToUser
     */
    public static void hockFragmentSetUserVisibleHint(Fragment fragment, boolean isVisibleToUser) {
        Log.d(TAG, "成功 Hock Fragment setUserVisibleHint：isVisibleToUser：" + isVisibleToUser);
        PathStatSDK.get().onFragmentSetUserVisibleHint(fragment, isVisibleToUser);
    }
    /**
     * Hock Fragment 的 onCreate 方法
     * @param fragment
     */
    public static void hockFragmentOnCreate(Fragment fragment) {
        Log.d(TAG, "成功 Hock Fragment onCreate：" + fragment);
        PathStatSDK.get().onFragmentCreate(fragment);
    }

    /**
     * Hock Fragment 的 onStart 方法
     * @param fragment
     */
    public static void hockFragmentOnStart(Fragment fragment) {
        Log.d(TAG, "成功 Hock Fragment onStart：" + fragment);
        PathStatSDK.get().onFragmentStart(fragment);
    }

    /**
     * Hock Fragment 的 onStart 方法
     * @param fragment
     */
    public static void hockFragmentOnStop(Fragment fragment) {
        Log.d(TAG, "成功 Hock Fragment onStop：" + fragment);
        PathStatSDK.get().onFragmentStop(fragment);
    }

    /**
     * Hock Fragment 的 onDestroy 方法
     * @param fragment
     */
    public static void hockFragmentOnDestroy(Fragment fragment) {
        Log.d(TAG, "成功 Hock Fragment OnDestroy：" + fragment);
        PathStatSDK.get().onFragmentDestroy(fragment);
    }
    // Hock Fragment end
    //----------------------------------------------------------------------------------------------

}
