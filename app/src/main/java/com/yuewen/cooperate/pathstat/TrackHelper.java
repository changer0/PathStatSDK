package com.yuewen.cooperate.pathstat;

import android.util.Log;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by zhanglulu on 2020/4/17.
 * for
 */
public class TrackHelper {
    private static final String TAG = "TrackHelper";
    public static void setViewPagerAdapter(Object obj) {
        ViewPager viewPager = ((ViewPager) obj);
        PagerAdapter adapter = viewPager.getAdapter();
        Log.d(TAG, "我拿到 Adapter 了！！！" + adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "我拿到 position 了: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
