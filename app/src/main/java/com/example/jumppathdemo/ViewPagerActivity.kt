package com.example.jumppathdemo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.jumppathdemo.pathstat.IGetPathInfo
import com.example.jumppathdemo.pathstat.IPathStatViewPagerFragmentAdapter
import com.example.jumppathdemo.pathstat.PathInfo
import com.qq.reader.view.SlipedFragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : FragmentActivity(), IGetPathInfo {
    override fun getPathInfo(): PathInfo {
        return PathInfo(false)
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
            bundle.putInt("tab", position)
            viewPagerFragment.arguments = bundle
            return viewPagerFragment
        }

        override fun getCount(): Int {
            return 3
        }

    }
}
