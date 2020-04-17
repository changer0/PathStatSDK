package com.example.jumppathdemo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.yuewen.cooperate.pathstat.IGetPathStatInfo
import com.yuewen.cooperate.pathstat.IPathStatViewPagerFragmentAdapter
import com.yuewen.cooperate.pathstat.PathStatInfo
import com.qq.reader.view.SlipedFragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : FragmentActivity(), IGetPathStatInfo {
    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo(false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        viewpager.adapter = MyAdapter(supportFragmentManager)
        viewpager.offscreenPageLimit = 1;
        viewpager.currentItem = 0
    }

    class MyAdapter(fm: FragmentManager) : SlipedFragmentStatePagerAdapter(fm),
        IPathStatViewPagerFragmentAdapter {
        override fun getItem(position: Int): BaseFragment {
            val viewPagerFragment = ViewPagerFragment()
            val bundle = Bundle()
            bundle.putString("text", position.toString())
            viewPagerFragment.arguments = bundle
            return viewPagerFragment
        }

        override fun getCount(): Int {
            return 3
        }

    }
}
