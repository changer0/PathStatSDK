package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yuewen.cooperate.pathstat.IGetPathStatInfo
import com.yuewen.cooperate.pathstat.PathStatInfo
import com.yuewen.cooperate.pathstat.PathStatSDK
import kotlinx.android.synthetic.main.activity_a.*

class A : AppCompatActivity(), IGetPathStatInfo{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)
        jump.setOnClickListener {
            startActivity(Intent(this, B::class.java))
            Log.d("MyApp", "APP_SESSION_ID: ${PathStatSDK.get().sessionId}")
        }
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo("A")
    }
}
