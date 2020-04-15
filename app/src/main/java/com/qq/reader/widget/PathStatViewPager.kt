package com.qq.reader.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import com.example.jumppathdemo.pathstat.PathStatSDK
import com.qq.reader.view.PathStatFragmentPageAdapter

/**
 * Created by zhanglulu on 2020/4/15.
 * for
 */
private const val TAG = "PathStatViewPager"
class PathStatViewPager: WebAdViewPager {

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
        if (adapter !is PathStatFragmentPageAdapter) {
            return
        }
        val pathStatAdapter = adapter as PathStatFragmentPageAdapter
        val curFragment = pathStatAdapter.getItem(pos)
        val pathStatInfo = PathStatSDK.get().analyseFragmentStatPathInfo(curFragment)
        PathStatSDK.get().statPathInfo(pathStatInfo)

    }

}