package com.qq.reader.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;


import com.example.jumppathdemo.BaseFragment;

import java.util.ArrayList;
import java.util.List;


/***
 * 
 * 使用了两种策略防止频繁加载
 * 1、增加延迟加载，防止在快速滑动的时候频繁加载
 * 2、监听OnPageChangeListener，防止在缓慢滑动过程中加载
 */
public abstract class SlipedFragmentStatePagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentState";
    private static final boolean DEBUG = false;
    private boolean mIsScrolling = false;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private int mCurrPosition  = 0;
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			Log.d(TAG,"onPageSelected " + arg0);
			mCurrPosition = arg0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                Log.d(TAG, "onPagerScrollStateChanged "
                        + mCurrentPrimaryItem.getArguments().get("titlename"));

                mIsScrolling = false;

                BaseFragment fragment = getFragment(mCurrPosition);
                if (fragment != null) {
                    postExcuteWithDelay(fragment);
                    List<BaseFragment> fragments = getFragments();
                    if (fragments != null) {
                        for (int i = 0; i < fragments.size(); i++) {
                            BaseFragment item =
                                    fragments.get(i);
//                            if (i == mCurrPosition) {
//                                item.onPageSelected(true);
//                            } else {
//                                item.onPageSelected(false);
//                            }
                        }
                    }
                } else {
                    List<Fragment> list = mFragmentManager.getFragments();
                    Log.d(TAG, "list size  " + list.size() + "  current " + mCurrPosition);
                    for (Fragment f : list) {
                        if (f != null) {
                            postExcuteWithDelay((BaseFragment) f);
                        }
                    }
                }
            }else{
				mIsScrolling = true;
			}
		}
	};
    
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
    private ArrayList<BaseFragment> mFragments = new ArrayList<BaseFragment>();
    private Fragment mCurrentPrimaryItem = null;
    
    public SlipedFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public BaseFragment getItem(int position) {
    	return mFragments.get(position);
    }
    public BaseFragment getFragment(int position){
    	if(position < 0 || position > mFragments.size() - 1){
    		return null;
    	}
    	return mFragments.get(position);
    }
    public List<BaseFragment> getFragments(){
        return mFragments;
    }
    @Override
    public void startUpdate(ViewGroup container) {
    }
    public OnPageChangeListener getOnPageChangeListener(){
    	return mOnPageChangeListener;
    }
    
    @Override
    public void notifyDataSetChanged() {
    	super.notifyDataSetChanged();
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mFragments.size() > position) {
        	BaseFragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        BaseFragment fragment = getItem(position);
        Log.d("baoyue"," instantiateItem  " + fragment.getClass().getSimpleName());
//        Log.d(TAG,"instantIateImte " + position + " name   " + fragment.getArguments().get("titlename"));
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
//        fragment.setMenuVisibility(false);
//        if (mCurrPosition == position) {
////            fragment.onPageSelected(true);
//            fragment.setDisplay(true);
//        } else {
////            fragment.onPageSelected(false);
//            fragment.setDisplay(false);
//        }
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);
        if(!mIsScrolling){
//        	fragment.executeLoadDataWithDelay();
        	postExcute(fragment);
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);//根据FragmentPagerAdapter改造而来 by zhanglulu
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            BaseFragment fragment = (BaseFragment)object;
//            fragment.cancleLoadData();
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
                    + " v=" + ((Fragment)object).getView());
            while (mSavedState.size() <= position) {
                mSavedState.add(null);
            }
           if(mFragmentManager.getFragments().contains(fragment) ){
               mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
           }
           mFragments.set(position, null);

           mCurTransaction.remove(fragment);
        }catch (Exception e){
//            Log.printErrStackTrace(TAG, e, null, null);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);//根据FragmentPagerAdapter改造而来 by zhanglulu
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);//根据FragmentPagerAdapter改造而来 by zhanglulu
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            try {
                mFragmentManager.executePendingTransactions();
            } catch (Exception e) {
//                Log.printErrStackTrace("SlipedFragmentStatePagerAdapter", e, null, null);
            }
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i=0; i<mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                try {//TODO 下个版本用add判断，这个判断太暴力了
                    mFragmentManager.putFragment(state, key, f);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.printErrStackTrace(TAG,e,null,null);
                }
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
//    	Log.d(TAG,"restoreState ");
        mSavedState.clear();
        mFragments.clear();
        mFragmentManager.popBackStack();
    }
    
    private void postExcuteWithDelay(BaseFragment fragment){
//    	Log.d(TAG,"postExcuteWithDelay  "+ fragment.getArguments().get("titlename"));
//    	fragment.executeLoadDataWithDelay();
    }
    
    private void postExcute(BaseFragment fragment){
//    	Log.d(TAG,"postExcute "+ fragment + "  " + fragment.getArguments().get("titlename"));
//    	fragment.executeLoadData();
    }
}
