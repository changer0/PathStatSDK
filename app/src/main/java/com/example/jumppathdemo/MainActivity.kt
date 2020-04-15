package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jumppathdemo.pathstat.PathStatInfo
import com.example.jumppathdemo.pathstat.PathStatSDK
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
    }
}