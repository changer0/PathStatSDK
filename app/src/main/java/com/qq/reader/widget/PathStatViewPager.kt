package com.qq.reader.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yuewen.cooperate.pathstat.IPathStatViewPagerFragmentAdapter
import com.yuewen.cooperate.pathstat.PathStatSDK

/**
 * Created by zhanglulu on 2020/4/15.
 * for
 */
private const val TAG = "PathStatViewPager"
class PathStatViewPager: ViewPager {

    constructor(context: Context): super(context) {

    }
    constructor(context: Context, attr: AttributeSet): super(context, attr) {

    }
    init {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
            override fun onPageSelected(position: Int) {
                Log.d(TAG, "position:$position")
                statPathInfo(position)
            }
        })
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        Log.d(TAG, "currentItem: $currentItem")
        if (currentItem == 0) {
            statPathInfo(0)
        }
    }

    private fun statPathInfo(pos: Int) {
        if (adapter !is IPathStatViewPagerFragmentAdapter) {
            return
        }
        val pathStatAdapter = adapter as IPathStatViewPagerFragmentAdapter
        val curFragment = pathStatAdapter.getItem(pos)
        val pathStatInfo = PathStatSDK.get().analyseStatPathInfo(curFragment)
        PathStatSDK.get().statPathInfo(pathStatInfo)

    }

}