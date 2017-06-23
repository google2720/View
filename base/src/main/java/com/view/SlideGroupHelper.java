package com.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
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
                   // view.setOnLongClickListener(longListener);
                    setupDragSort(view);
                }

                mLayoutTransition = new LayoutTransition();
                //去掉淡入淡出动画
                mLayoutTransition.disableTransitionType(LayoutTransition.APPEARING);
                mLayoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
                //add和remove的其余动画效果
                // mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
                //加入导致被影响的其余child做的动画，默认是移动，即被挤开。
                mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
                mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
                slideGroup.setLayoutTransition(mLayoutTransition);
            }
        });
    }



    public  void setupDragSort(View view) {
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View view, DragEvent event) {
                ViewGroup viewGroup = (ViewGroup)view.getParent();
                DragState dragState = (DragState)event.getLocalState();
                switch (event.getAction()) {
                    // 开始拖拽
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (view == dragState.view) {
                            view.setVisibility(View.INVISIBLE);
                        }
                        break;
                    // 结束拖拽
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (view == dragState.view) {
                            view.setVisibility(View.VISIBLE);
                        }
                        break;

                    // 拖拽进某个控件后，退出
                    case DragEvent.ACTION_DRAG_EXITED:

                        break;
                    // 拖拽进某个控件后，保持
                    case DragEvent.ACTION_DRAG_LOCATION: {
                        if (view == dragState.view){
                            break;
                        }
                        int index = viewGroup.indexOfChild(view);
                        if (   (index > dragState.index && event.getY() > view.getHeight() / 2)
                            || (index < dragState.index && event.getY() < view.getHeight() / 2)

                            || (index < dragState.index && event.getX() < view.getWidth() / 2)
                            || (index < dragState.index && event.getX() < view.getWidth() / 2)
                                )
                        {
                            swapViews(viewGroup, view, index, dragState);
                        } else {
                            swapViewsBetweenIfNeeded(viewGroup, index, dragState);
                        }
                        break;
                    }

                    // 推拽进入某个控件
                    case DragEvent.ACTION_DRAG_ENTERED:

                        break;

                    case DragEvent.ACTION_DROP:
                        if (view != dragView) {
                         //   swapViewGroupChildren(viewGroup, view, dragState.view);
                        }
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


    private  void swapViewsBetweenIfNeeded(ViewGroup viewGroup, int index,
                                                 DragState dragState) {
        if (index - dragState.index > 1) {
            int indexAbove = index - 1;
            swapViews(viewGroup, viewGroup.getChildAt(indexAbove), indexAbove, dragState);
        } else if (dragState.index - index > 1) {
            int indexBelow = index + 1;
            swapViews(viewGroup, viewGroup.getChildAt(indexBelow), indexBelow, dragState);
        }
    }


    private  void swapViews(ViewGroup viewGroup, final View view, int index,
                                  DragState dragState) {
        swapViewsBetweenIfNeeded(viewGroup, index, dragState);
        swapViewGroupChildren(viewGroup, view, dragState.view);
        dragState.index = index;
    }


    public  void swapViewGroupChildren(ViewGroup viewGroup, View firstView, View secondView) {

        int firstIndex = viewGroup.indexOfChild(firstView);
        int secondIndex = viewGroup.indexOfChild(secondView);
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


    private View.OnLongClickListener longListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            KLog.d("onLongClick tag :" + v.getTag());
            KLog.d("long view  x : " + v.getX() + " --" + " y : " + v.getY());
            handleDragDown(v);
            return true;
        }
    };



    private void handleDragDown(View v) {
        isDrag = true;
        dragView = v;
        //开启mDragItemView绘图缓存
        dragView.setDrawingCacheEnabled(true);
        //获取mDragItemView在缓存中的Bitmap对象
        Bitmap mDragBitmap = Bitmap.createBitmap(dragView.getDrawingCache());
        //释放绘图缓存，避免出现重复的镜像
        dragView.destroyDrawingCache();
        createDragImage(mDragBitmap, (int) dragView.getX(), (int) dragView.getY());
        dragView.setVisibility(View.INVISIBLE);
    }

    public void handleDragUp(){
        dragView.setVisibility(View.VISIBLE);
        removeDragImage();
        isDrag = false;
    }


    public void handleDragMove(int moveX, int moveY) {
        if(dragView!=null) {
            mWindowLayoutParams.x = moveX;
            mWindowLayoutParams.y = moveY;
            mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
        }
    }



    int interceptX, interceptEndX;
    private SlideGroup.disPathEvent disPathEvent = new SlideGroup.disPathEvent() {
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
            if(!isDrag) {
                scrollBy((int) distanceX);
            }else {
                handleDragMove((int)e2.getX(),(int) e2.getY());
            }
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







    private boolean isDrag;
    private View dragView;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private ImageView mDragImageView;
    private WindowManager mWindowManager;

    /**
     * 创建拖动的镜像
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        if(mWindowLayoutParams==null) {
            mWindowLayoutParams = new WindowManager.LayoutParams();
        }
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX;
        mWindowLayoutParams.y = downY;
        mWindowLayoutParams.alpha = 0.65f; //透明度
        mWindowLayoutParams.width = bitmap.getWidth();
        mWindowLayoutParams.height = bitmap.getHeight();
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(mContext);
        mDragImageView.setImageBitmap(bitmap);
        if(mWindowManager==null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }


    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    private void Log(String info) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, info);
        }
    }

}
