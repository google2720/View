package com.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.socks.library.KLog;

/**
 * Created by zhangjunjun on 2017/5/28.
 */

public class SlideGroupHelper {

    private static final String TAG = "SlideGroupHelper";

    private SlideGroup slideGroup;
    private Scroller mScroller;
    private Context mContext;
    private int downX, upX;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private int mCurrentPage = 0;
    private boolean isFling = false;
    private int mTouchSlop;
    boolean isDragAutoScroll = false;
    private LayoutTransition mLayoutTransition;


    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1;
        }
    }


    public SlideGroupHelper(SlideGroup slide) {
        slideGroup = slide;
        mContext = slideGroup.getContext();
        mScroller = new Scroller(mContext, new ScrollInterpolator());
        slideGroup.setDisPathEvent(disPathEvent);
        mTouchSlop = ViewConfiguration.getTouchSlop();
        slideGroup.post(new Runnable() {
            @Override
            public void run() {
                int childCount = slideGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View view = slideGroup.getChildAt(i);
                    view.setClickable(true);
                    view.setOnClickListener(listener);
                    view.setLongClickable(true);
                    setupDragSort(view);
                }

                mLayoutTransition = new LayoutTransition();
                //去掉淡入淡出动画
                mLayoutTransition.disableTransitionType(LayoutTransition.APPEARING);
                mLayoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
                //add和remove的其余动画效果
                //mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
                //加入导致被影响的其余child做的动画，默认是移动，即被挤开。
                mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
                mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
                slideGroup.setLayoutTransition(mLayoutTransition);
            }
        });
    }


    boolean isSwap=false;
    int firstLocation;
    public  void setupDragSort(View view) {
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View view, DragEvent event) {
                ViewGroup viewGroup = (ViewGroup)view.getParent();
                DragState dragState = (DragState)event.getLocalState();
                switch (event.getAction()) {
                    // 开始拖拽
                    case DragEvent.ACTION_DRAG_STARTED:
                        //Log.d(TAG,"tag: "+view.getTag() +"  STARTED");
                        if (view == dragState.view) {
                            view.setVisibility(View.INVISIBLE);
                        }
                        break;
                    // 结束拖拽
                    case DragEvent.ACTION_DRAG_ENDED:
                       // Log.d(TAG,"tag: "+view.getTag() +"  ENDED");
                        if (view == dragState.view) {
                            view.setVisibility(View.VISIBLE);
                        }
                        break;

                    // 拖拽进某个控件后，退出
                    case DragEvent.ACTION_DRAG_EXITED:
                      //  Log.d(TAG,"tag: "+view.getTag() +"  EXITED");
                        break;

                    // 拖拽进某个控件后，保持
                    case DragEvent.ACTION_DRAG_LOCATION: {

                        if(firstLocation!=(int) view.getTag() && !isDragAutoScroll) {
                            //  Log.d(TAG, "tag: " + view.getTag() + "  LOCATION");
                            //  Log.d(TAG,"dragState.view tag: "+dragState.view +"  LOCATION");
                            if (!isSwap && view != dragState.view) {
                                isSwap = true;
                                swapViewGroupChildren(viewGroup, view, dragState.view);
                            }
                        }
                        firstLocation = (int) view.getTag();
                        break;
                    }

                    // 推拽进入某个控件
                    case DragEvent.ACTION_DRAG_ENTERED:
                      //Log.d(TAG,"tag: "+view.getTag() +"  ENTERED");
                        isSwap = false;
                        break;

                    // 推拽进入某个控件，后在该控件内，释放。即把推拽控件放入另一个控件
                    case DragEvent.ACTION_DROP:

                        break;
                }
                return true;
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startDrag(null, new View.DragShadowBuilder(view), new DragState(view), 0);
                return true;
            }
        });
    }





    public  void swapViewGroupChildren(ViewGroup viewGroup, View firstView, View secondView) {
        int firstIndex = viewGroup.indexOfChild(firstView);
        int secondIndex = viewGroup.indexOfChild(secondView);
        Log.d(TAG,"firstIndex: "+firstIndex +"  secondIndex : "+secondIndex);

        if (firstIndex < secondIndex) {
            viewGroup.removeViewAt(secondIndex);
            viewGroup.removeViewAt(firstIndex);
            viewGroup.addView(secondView, firstIndex);
            viewGroup.addView(firstView, secondIndex);
        } else {
            viewGroup.removeViewAt(firstIndex);
            viewGroup.removeViewAt(secondIndex);
            viewGroup.addView(firstView, secondIndex);
            viewGroup.addView(secondView, firstIndex);
        }


    }


    private  class DragState {
        public View view;
        public int index;
        private DragState(View view) {
            this.view = view;
            index = ((ViewGroup)view.getParent()).indexOfChild(view);
        }
    }




    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KLog.d("tag :" + v.getTag());
        }
    };








    int interceptX, interceptEndX;
    private SlideGroup.disPathEvent disPathEvent = new SlideGroup.disPathEvent() {

        @Override
        public void dispatchDragEvent(DragEvent event) {
            Log.d(TAG,"drag View x:"+event.getX() + " drag View Y:"+event.getY());
            Log.d(TAG,"drag View mCurrentPage:"+slideGroup.pageWidth*(mCurrentPage+1));

            if(!isDragAutoScroll) {
                if (event.getX() > slideGroup.pageWidth - 100) {
                    moveNext();
                    isDragAutoScroll = true;
                }
                 else if (event.getX() < 20) {
                    movePrev();
                    isDragAutoScroll = true;
               }
            }
        }


        @Override
        public void onEvent(MotionEvent event) {
            onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if(mGestureDetector==null) {
                mGestureDetector = new GestureDetector(mContext,mGestureListener = new GestureListener());
            }
            mGestureDetector.onTouchEvent(ev);
            boolean isIntercept = false;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isIntercept = !mScroller.isFinished();
                    interceptX = (int) ev.getX();
                    break;

                case MotionEvent.ACTION_MOVE:
                    interceptEndX = (int) ev.getX();
                    int disX = Math.abs(interceptEndX - interceptX);
                    if (disX >= mTouchSlop) {
                        isIntercept = true;
                    }
                    else {
                        isIntercept = false;
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isIntercept = false;
                   // handleDragUp();
                    break;

            }
            return isIntercept;
        }


        @Override
        public void computeScroll() {
            if (computeScrollOffset()) {
                scrollTo();
                postInvalidate();
            } else {
                isDragAutoScroll = false;
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
            if (!mScroller.isFinished())
                mScroller.abortAnimation();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollBy((int) distanceX);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //快速滑动时直接下/上一页
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
