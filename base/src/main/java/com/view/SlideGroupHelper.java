package com.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

/**
 * Created by zhangjunjun on 2017/5/28.
 */

public class SlideGroupHelper {

    private static final String TAG = "SlideGroupHelper";

    private SlideGroup slideGroup;
    private Scroller mScroller;
    private Context mContext;
    private int downX,upX;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private int mCurrentPage=0;
    private boolean isFling = false;


    public SlideGroupHelper(SlideGroup slideGroup) {
        this.slideGroup = slideGroup;
        mContext = slideGroup.getContext();
        mScroller = new Scroller(mContext);
        this.slideGroup.setDisPathEvent(disPathEvent);
    }


    private SlideGroup.disPathEvent disPathEvent = new SlideGroup.disPathEvent() {
        @Override
        public void onEvent(MotionEvent event) {
            onTouchEvent(event);
        }

        @Override
        public void computeScroll() {
            if (computeScrollOffset()) {
                scrollTo();
                postInvalidate();
            }
        }
    };

    
 


    private void onTouchEvent(MotionEvent event)
    {

        //使用手势识别器处理滑动作
        if(mGestureDetector==null) {
            mGestureDetector = new GestureDetector(mContext,mGestureListener = new GestureListener());
        }

        //抬起动作
        boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;
        if (!mGestureDetector.onTouchEvent(event) && detectedUp ) {
            mGestureListener. onUp(event);
        }


    }


    private class GestureListener implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {



            downX = (int) e.getX();
            Log("onDown :" + e.getX());
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log("onShowPress :" + e.getX());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log("onSingleTapUp :" + e.getX());
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log("onScroll e1 :" + e1.getX());
//            Log("onScroll e2 :" + e2.getX());
//            Log("onScroll :" + distanceX);
            scrollBy((int) distanceX);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log("onLongPress :" + e.getX());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log("onFling :" + e1.getX());
            isFling = true;
            if(e1!=null && e2!=null) {
                if (e1.getX() > e2.getX()) {
                    moveNext(velocityX);
                } else {
                    movePrev(velocityX);
                }
            }
            return false;
        }

        public boolean onUp(MotionEvent event) {
            upX = (int) event.getX();
            Log("onUp :" + upX);
            if(!isFling) {
                if (downX - upX > pageWidth() / 2) {
                    moveNext();
                } else if (upX - downX > pageWidth() / 2) {
                    movePrev();
                } else {
                    moveCurr(mCurrentPage);
                }
            }

            isFling = false;
            return false;
        }
    }



    private void movePrev() {
        if (mCurrentPage > 0) {
            mCurrentPage--;
        }
        moveCurr(mCurrentPage);
    }

    private void moveNext() {
        if (mCurrentPage < totalPage() - 1) {
            mCurrentPage++;
        }
        moveCurr(mCurrentPage);
    }

    private void movePrev(float velocityX) {
        if (mCurrentPage > 0) {
            mCurrentPage--;
        }
        moveCurr(mCurrentPage,velocityX);
    }

    private void moveNext(float velocityX) {
        if (mCurrentPage < totalPage() - 1) {
            mCurrentPage++;
        }
        moveCurr(mCurrentPage,velocityX);
    }

    private void moveCurr(int page,float velocityX)
    {
        int distance = page * pageWidth() - getScrollX();
        mCurrentPage = page;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        int duration = (int) (Math.abs(distance) * 1000 / Math.abs(velocityX));
        mScroller.startScroll(getScrollX(), 0, distance, 0, duration);
        postInvalidate();
    }

    private void moveCurr(int page) {
        int distance = page * pageWidth() - getScrollX();
        mCurrentPage = page;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        int duration = Math.abs(distance);
        mScroller.startScroll(getScrollX(), 0, distance, 0, duration);
        postInvalidate();
    }


    private void scrollBy(int distanceX) {
        slideGroup.scrollBy(distanceX, 0);
    }

    private boolean computeScrollOffset() {
        return mScroller.computeScrollOffset();
    }


    private void scrollTo() {
        slideGroup.scrollTo(mScroller.getCurrX(), 0);
    }

    private void postInvalidate() {
        slideGroup.postInvalidate();
    }

    private int totalPage() {
        return slideGroup.totalPage;
    }

    private int pageWidth() {
        return slideGroup.pageWidth;
    }


    private int getScrollX() {
        return slideGroup.getScrollX();
    }


    private void Log(String info) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, info);
        }
    }

}
