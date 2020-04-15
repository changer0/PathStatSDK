package com.qq.reader.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class WebAdViewPager extends RankBoardViewPage {
	private final static String Tag = "ViewPager";
	private ShouldIntercept mShouldIntercept;
    private boolean isHorizontalScroll = true;
	public WebAdViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WebAdViewPager(Context context) {
		super(context);
	}

    public void setCanHorizontalScroll(boolean isCanScroll) {
        isHorizontalScroll = isCanScroll;
    }

	public void setShouldIntercept(ShouldIntercept shouldIntercept){
		mShouldIntercept
		 = shouldIntercept;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!isHorizontalScroll){
            return false;
        }
		if(mShouldIntercept==null){
			return super.onInterceptTouchEvent(ev);
		}else{
			boolean shouldIntercept = mShouldIntercept.shouldIntercept();
			final int action  =  ev.getAction() & MotionEvent.ACTION_MASK;
			if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
				mShouldIntercept.onTouchUp();
			}
			if(shouldIntercept){
//				return super.onInterceptTouchEvent(ev);//may be pointerIndex out of range
				boolean ret  = false;
				try{
					ret = super.onInterceptTouchEvent(ev);
				}catch(Exception e){
					e.printStackTrace();
				}
				return ret;
				
			}else{
				return false;
			}
		}
	}
	public interface ShouldIntercept{
		public boolean  shouldIntercept();
		public void onTouchUp();
	}
}
