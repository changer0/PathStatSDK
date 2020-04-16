package com.example.jumppathdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jumppathdemo.pathstat.IGetPathInfo
import com.example.jumppathdemo.pathstat.PathInfo
import kotlinx.android.synthetic.main.activity_b.*

class B : AppCompatActivity(), IGetPathInfo {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b)
        jump.setOnClickListener {
            startActivity(Intent(this, C::class.java))
        }
    }

    override fun getPathInfo(): PathInfo {
        return PathInfo("B")
    }
}
