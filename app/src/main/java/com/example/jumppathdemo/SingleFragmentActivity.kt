package com.example.jumppathdemo
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.jumppathdemo.pathstat.IGetPathInfo
import com.example.jumppathdemo.pathstat.PathInfo

class SingleFragmentActivity : FragmentActivity(),IGetPathInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        val fragment = ViewPagerFragment()
        val bt = supportFragmentManager.beginTransaction()
        bt.add(R.id.rootView, fragment)
        bt.commitNow()
    }

    override fun getPathInfo(): PathInfo {
        return PathInfo(false)
    }
}
