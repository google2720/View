package com.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
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
    private int mTouchSlop;


    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t*t*t*t*t + 1;
        }
    }


    public SlideGroupHelper(SlideGroup slide) {
        slideGroup = slide;
        mContext = slideGroup.getContext();
        mScroller = new Scroller(mContext,new ScrollInterpolator());
        slideGroup.setDisPathEvent(disPathEvent);
        mTouchSlop = ViewConfiguration.getTouchSlop();


        slideGroup.post(new Runnable() {
            @Override
            public void run() {
                int childCount = slideGroup.getChildCount();
                for (int i= 0;i<childCount;i++) {
                    View view = slideGroup.getChildAt(i);
                    view.setClickable(true);
                    view.setOnClickListener(listener);
                }
            }
        });


    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("SlideGroupHelper","tag :" +v.getTag());
        }
    };


    int dnX,endX;
    private SlideGroup.disPathEvent disPathEvent = new SlideGroup.disPathEvent() {
        @Override
        public void onEvent(MotionEvent event) {
            onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {

            Log.d("SlideGroupHelper","ev :" +TouchEventUtil.getTouchAction(ev.getAction()));

            boolean isIntercept = false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isIntercept = !mScroller.isFinished();
                    dnX = (int) ev.getX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    endX = (int) ev.getX();
                    int disX = Math.abs(endX - dnX);
                    if (disX > mTouchSlop) {
                        isIntercept = true;
                    }
                    else {
                        isIntercept = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isIntercept = false;
                    break;
            }
            return isIntercept;
        }

        @Override
        public void computeScroll() {
            if (computeScrollOffset()) {
                scrollTo();
                postInvalidate();
            }
        }
    };

    private void onTouchEvent(MotionEvent event) {
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
           // Log("onDown :" + e.getX());
            if (!mScroller.isFinished())
                mScroller.abortAnimation();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //Log("onShowPress :" + e.getX());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log("onSingleTapUp :" + e.getX());
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollBy((int) distanceX);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
           // Log("onLongPress :" + e.getX());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //快速滑动时直接下/上一页
           // Log("onFling  velocityX :" + velocityX);
            if(Math.abs(velocityX)>1500) {
                isFling = true;
                if (e1 != null && e2 != null) {
                    if (e1.getX() > e2.getX()) {
                        moveNext(velocityX);
                    } else {
                        movePrev(velocityX);
                    }
                }
            }
            return false;
        }

        public boolean onUp(MotionEvent event) {
            //慢慢滑动时超过每页1/2时直接上/下一页
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
        int duration;
        if(velocityX!=0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocityX));
        }
        else {
            duration = Math.abs(distance);
        }
        mScroller.startScroll(getScrollX(), 0, distance, 0, duration);
        postInvalidate();
    }

    private void moveCurr(int page) {
        moveCurr(page,0);
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
