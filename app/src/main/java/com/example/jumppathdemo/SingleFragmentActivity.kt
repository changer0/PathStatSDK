package com.example.jumppathdemo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.yuewen.cooperate.pathstat.IGetPathStatInfo
import com.yuewen.cooperate.pathstat.PathStatInfo

class SingleFragmentActivity : FragmentActivity(),IGetPathStatInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        val fragment = ViewPagerFragment()
        val bundle = Bundle()
        bundle.putString("text", "单个 Fragment")
        fragment.arguments = bundle
        val bt = supportFragmentManager.beginTransaction()
        bt.add(R.id.rootView, fragment)
        bt.commitNow()
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo(false)
    }
}
