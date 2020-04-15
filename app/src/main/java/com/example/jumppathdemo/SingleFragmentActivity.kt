package com.example.jumppathdemo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.jumppathdemo.pathstat.IGetPathStatInfo
import com.example.jumppathdemo.pathstat.PathStatInfo

class SingleFragmentActivity : FragmentActivity(),IGetPathStatInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        val fragment = ViewPagerFragment()
        val bt = supportFragmentManager.beginTransaction()
        bt.add(R.id.rootView, fragment)
        bt.commitNow()
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo(false)
    }
}
