package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.jumppathdemo.pathstat.IGetPathInfo
import com.example.jumppathdemo.pathstat.PathInfo
import com.example.jumppathdemo.pathstat.PathStatSDK
import kotlinx.android.synthetic.main.activity_a.*

class A : AppCompatActivity(), IGetPathInfo{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)
        jump.setOnClickListener {
            startActivity(Intent(this, B::class.java))
            Log.d("MyApp", "APP_SESSION_ID: ${PathStatSDK.get().sessionId}")
        }
    }

    override fun getPathInfo(): PathInfo {
        return PathInfo("A")
    }
}
