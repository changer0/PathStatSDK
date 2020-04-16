package com.example.jumppathdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.jumppathdemo.pathstat.IGetPathInfo
import com.example.jumppathdemo.pathstat.PathInfo
import com.example.jumppathdemo.pathstat.PathStatSDK

/**
 * Created by zhanglulu on 2020/4/15.
 * for
 */
private const val TAG = "BaseFragment"
open class BaseFragment: Fragment(), IGetPathInfo {
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

    override fun getPathInfo(): PathInfo {
        return PathInfo(javaClass.name)
    }
}