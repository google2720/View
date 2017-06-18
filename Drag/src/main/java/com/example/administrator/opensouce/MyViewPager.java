package com.example.administrator.opensouce;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/5/3 0003.
 */
public class MyViewPager extends ViewPager {

    private OnTouchEvent onTouchEvent;

    public interface OnTouchEvent {
        void onScroll();
    }

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLise(OnTouchEvent onTouchEvent) {
        this.onTouchEvent = onTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (onTouchEvent != null) {
                    onTouchEvent.onScroll();
                }
                break;
        }


        return super.onTouchEvent(ev);
    }
}
