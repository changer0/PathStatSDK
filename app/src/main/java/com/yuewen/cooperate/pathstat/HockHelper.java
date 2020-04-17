package com.yuewen.cooperate.pathstat;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qq.reader.view.SlipedFragmentStatePagerAdapter;

/**
 * Created by zhanglulu on 2020/4/17.
 * for Hock ViewPager 的 setAdapter 方法
 */
public class HockHelper {
    private static final String TAG = "TrackHelper";
    public static void setViewPagerAdapter(Object obj) {
        ViewPager viewPager = ((ViewPager) obj);
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        Log.d(TAG, "我拿到 Adapter 了！！！" + pagerAdapter);
        final SlipedFragmentStatePagerAdapter adapter = (SlipedFragmentStatePagerAdapter) pagerAdapter;
        if (viewPager.getCurrentItem() == 0) {
            statPathInfo(adapter, 0);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "我拿到 position 了: " + position);
                statPathInfo(adapter, 0);
            }
        });
    }

    private static void statPathInfo(SlipedFragmentStatePagerAdapter adapter, int pos) {
        Fragment fragment = adapter.getItem(pos);
        PathStatInfo pathStatInfo = PathStatSDK.get().analyseStatPathInfo$app_debug(fragment);
        PathStatSDK.get().statPathInfo(pathStatInfo);
    }
}
