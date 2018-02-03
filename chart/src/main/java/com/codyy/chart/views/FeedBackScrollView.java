package com.codyy.chart.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 可左右滑动，且能传递滑动事件的HorizontalScrollView
 * Created by ldh on 2017/5/2.
 */

public class FeedBackScrollView extends HorizontalScrollView {
    private final static String TAG = "FeedBackScrollView";

    private OnScrollChangeListener mOnScrollChangeListener;

    private boolean mScrollByUser = true;

    public FeedBackScrollView(Context context) {
        super(context);
    }

    public FeedBackScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedBackScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FeedBackScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG,"onTouchEvent ev=" + ev);
        mScrollByUser = true;
        return false;
    }

    public void handleTouchEvent(MotionEvent ev){
        super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangeListener != null && mScrollByUser){
            mOnScrollChangeListener.onScrollChange(this,l,t,oldl,oldt);
        }
    }

    public interface OnScrollChangeListener{
        void onScrollChange(View view, int l, int t, int oldl, int oldt);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener){
        mOnScrollChangeListener = listener;
    }
}
