package com.example.jumppathdemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.yuewen.cooperate.pathstat.IGetPathStatInfo
import com.yuewen.cooperate.pathstat.PathStatInfo
import com.yuewen.cooperate.pathstat.PathStatSDK

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