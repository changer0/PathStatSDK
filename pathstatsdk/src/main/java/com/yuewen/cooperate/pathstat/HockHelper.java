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
    public static void setViewPagerAdapter(Object viewPagerObj) {
        Log.d(TAG, "成功 Hock：" + viewPagerObj);
        if (viewPagerObj instanceof ViewPager) {
            ViewPager viewPager = ((ViewPager) viewPagerObj);
            final PagerAdapter pagerAdapter = viewPager.getAdapter();
            Log.d(TAG, "我拿到 Adapter 了！！！" + pagerAdapter);

            if (viewPager.getCurrentItem() == 0) {
                statPathInfo(pagerAdapter, 0);

            }
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                @Override
                public void onPageScrollStateChanged(int state) {}

                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "我拿到 position 了: " + position);
                    statPathInfo(pagerAdapter, position);
                }
            });
        } else {
            Function1<Object, Unit> customViewPager = PathStatSDK.get().getConfig().getCustomViewPager();
            if (customViewPager != null) {
                customViewPager.invoke(viewPagerObj);
            } else {
                Log.e(TAG, "setViewPagerAdapter: 你使用了非原生 ViewPager！，请配置 Config");
            }
        }
        //这个 obj 其实是 ViewPager，但是考虑到用户完全自定义的情况需要做特殊处理
//        try {
//            final Class<?> viewPagerClass = viewPagerObj.getClass();
//            Method getAdapterMethod = viewPagerClass.getDeclaredMethod("getAdapter");
//
//            final PagerAdapter pageAdapter = (PagerAdapter) getAdapterMethod.invoke(viewPagerObj);
//            Class<? extends PagerAdapter> pageAdapterClass = pageAdapter.getClass();
//
//            Method getCurrentItemMethod = viewPagerClass.getDeclaredMethod("getCurrentItem");
//            Integer curItem = (Integer) getCurrentItemMethod.invoke(viewPagerObj);
//            final Method getItemMethod = pageAdapterClass.getDeclaredMethod("getItem");
//            if (curItem == 0) {
//                statCustomViewPager(viewPagerClass, pageAdapter, getItemMethod);
//            }
//
//            //设置监听
//            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
//                @Override
//                public void onPageScrollStateChanged(int state) {}
//                @Override
//                public void onPageSelected(int position) {
//                    Log.d(TAG, "我拿到 position 了: " + position);
//                    try {
//                        statCustomViewPager(viewPagerClass, pageAdapter, getItemMethod);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            Method addOnPageChangeListenerMethod = viewPagerClass.getDeclaredMethod("addOnPageChangeListener");
//            addOnPageChangeListenerMethod.invoke(viewPagerObj, onPageChangeListener);
//        } catch (Exception e) {
//            Log.e(TAG, "setViewPagerAdapter: ？？？你需要考虑下面几个个问题：\n" +
//                    "1，是不是没用系统的 ViewPager " +
//                    "2，是不是没有在 gradle 和 init 时配置 customViewPagerClass " +
//                    "3.你完全重写的 ViewPager 是不是没有 Adapter 的 getter 和 setter 方法 以及 addOnPageChangeListener 方法");
//            e.printStackTrace();
//        }

    }

    private static void statCustomViewPager(Class<?> viewPagerClass, PagerAdapter pageAdapter, Method getItemMethod) throws IllegalAccessException, InvocationTargetException {
        Object objItem = getItemMethod.invoke(pageAdapter, 0);
        PathStatInfo pathStatInfo = null;
        if (objItem instanceof IGetPathStatInfo) {
            pathStatInfo = ((IGetPathStatInfo) objItem).getPathStatInfo();
        } else {
            pathStatInfo = new PathStatInfo(viewPagerClass.getName());
        }
        PathStatSDK.get().statPathInfo(pathStatInfo);
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
}
