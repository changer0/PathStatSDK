package com.example.jumppathdemo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuewen.cooperate.pathstat.PathStatInfo
import kotlinx.android.synthetic.main.fragment_item.*

/**
 * Created by zhanglulu on 2020/4/15.
 * for Fragment
 */
class ViewPagerFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_item, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_text.text = arguments?.getString("text")

        fragment_text.setOnClickListener {
            startActivity(Intent(activity, SingleFragmentActivity::class.java))
        }
    }

    override fun getPathStatInfo(): PathStatInfo {
        return PathStatInfo("当前子 Fragment: "+arguments?.getString("text").toString())
    }

}