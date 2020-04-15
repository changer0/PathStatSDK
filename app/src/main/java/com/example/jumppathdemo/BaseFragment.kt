package com.example.jumppathdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.jumppathdemo.pathstat.IGetPathStatInfo
import com.example.jumppathdemo.pathstat.PathStatInfo
import com.example.jumppathdemo.pathstat.PathStatSDK

/**
 * Created by zhanglulu on 2020/4/15.
 * for
 */
private const val TAG = "BaseFragment"
open class BaseFragment: Fragment(), IGetPathStatInfo {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PathStatSDK.get().onFragmentCreate(this)
    }

    override fun onStart()  {
        super.onStart()
        Log.d(TAG, this.javaClass.name)
        PathStatSDK.get().onFragmentStart(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PathStatSDK.get().onFragmentDestroy(this)
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo(javaClass.name)
    }
}