package com.yuewen.cooperate.pathstat

import androidx.fragment.app.Fragment

/**
 * Created by zhanglulu on 2020/4/16.
 * for 针对 ViewPagerFragmentAdapter 的接口，需要使用方接入即可
 */
interface IPathStatViewPagerFragmentAdapter {
    public fun getItem(pos: Int): Fragment
}