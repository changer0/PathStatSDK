package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qq.reader.activity.BlackListActivity
import com.yuewen.cooperate.pathstat.PathStatInfo
import com.yuewen.cooperate.pathstat.PathStatSDK
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jump.setOnClickListener {
            startActivity(Intent(this, A::class.java))
        }
        stat.setOnClickListener {
            PathStatSDK.get().statPathInfo(PathStatInfo("手动上报"))
        }
        viewpager.setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        singleFragment.setOnClickListener {
            startActivity(Intent(this, SingleFragmentActivity::class.java))
        }
        customViewPager.setOnClickListener {
            startActivity(Intent(this, CustomViewPagerActivity::class.java))
        }
        blackActivity.setOnClickListener {
            startActivity(Intent(this, BlackListActivity::class.java))
        }
        newProgressActivity.setOnClickListener {
            startActivity(Intent(this, NewProgressActivity::class.java))
        }
    }
}