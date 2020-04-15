package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jumppathdemo.pathstat.IGetPathStatInfo
import com.example.jumppathdemo.pathstat.PathStatInfo
import com.example.jumppathdemo.pathstat.PathStatSDK
import kotlinx.android.synthetic.main.activity_b.*

class B : AppCompatActivity(), IGetPathStatInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        jump.setOnClickListener {
            startActivity(Intent(this, C::class.java))
        }
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo("B")
    }
}
