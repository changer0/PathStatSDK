package com.qq.reader.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


/**
 * Created by wangyudong on 2017/7/26.
 */

public class RankBoardViewPage extends RankBaseViewPager {

    private final static String Tag = "ViewPager";
    private WebAdViewPager.ShouldIntercept mShouldIntercept;
    private boolean isHorizontalScroll = true;
    public RankBoardViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public RankBoardViewPage(Context context) {
        super(context);
    }

    public void setCanHorizontalScroll(boolean isCanScroll) {
        isHorizontalScroll = isCanScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("RankBoardViewPage", "onTouchEvent ev= " + ev.getAction());
        return super.onTouchEvent(ev);
    }

    public void setShouldIntercept(WebAdViewPager.ShouldIntercept shouldIntercept){
        mShouldIntercept
                = shouldIntercept;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("RankBoardViewPage", "this = "+this+  ", onInterceptTouchEvent ev= " + ev.getAction());
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
                boolean ret  = true;
                try{
                    ret = super.onInterceptTouchEvent(ev);
                }catch(Exception e){
                    e.printStackTrace();
                }
                Log.d("RankBoardViewPage", "ret = " + ret);
                return ret;

            }else{
                return false;
            }
        }
    }
    public interface ShouldIntercept{
        public boolean shouldIntercept();
        public void onTouchUp();
    }
}
