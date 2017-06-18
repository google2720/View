package yunovo.com.lanucher.card;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Scroller;

import yunovo.com.lanucher.R;


public class MyViewPage extends ViewGroup {

    public static final String TAG = "MyViewPage";


    private GestureDetector gestureDetector;
    private Scroller myScroller;
    protected boolean isFling;
    private LayoutTransition mLayoutTransition;
    private int currentId;



    public MyViewPage(Context context) {
        super(context);
        InitView(context);
    }

    public MyViewPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        InitView(context);
    }

    public MyViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }


    /**
     * 长按的view
     */
    private View pressView;
    /**
     * 长按下的坐标X
     */
    private int longPressX;
    /**
     * 长按下的坐标Y
     */
    private int longPressY;
    /**
     * 拖拽时的Bitmap
     */
    private Bitmap mDragBitmap;
    /**
     * 是否处于拖拽中
     */
    private  boolean isDrag;
    /**
     * 状态栏高度
     */
    private int statusBarHeight;
    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;
    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;
    /**
     * 距离屏幕顶部的偏移量
     */
    private int mOffset2Top;
    /**
     * 距离屏幕左边的偏移量
     */
    private int mOffset2Left;

    private VelocityTracker velocityTracker;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private int mPointerId;
    float mVelocityX;
    float velocityY ;

    private Handler mHandler = new Handler();

    private void InitView(Context context) {

/*        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if(child==pressView)
                {
                    Log.d(TAG,"onChildViewAdded");
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if(child==pressView)
                {
                    Log.d(TAG,"onChildViewRemoved");
                }
                addView(child, index);
            }
        });*/

        mMaximumFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMinimumFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        mLayoutTransition = new LayoutTransition();
        setLayoutTransition(mLayoutTransition);
        mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);


        setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG,"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG,"onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        myScroller = new Scroller(context);
        statusBarHeight = getStatusHeight(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        gestureDetector = new GestureDetector(context, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                isDrag = true;
                longPressX = (int) e.getX();
                longPressY = (int) e.getY();

                Log.d(TAG,"onLongPress longPressX :"+longPressX);
                Log.d(TAG,"onLongPress longPressY :"+longPressY);

                pressView = findDragView((int) e.getX());

                dragCoverPosition = pressPosition;
                dragLastPosition = pressPosition;
                //pressView.setVisibility(INVISIBLE);

                Log.d(TAG,"onLongPress getTop:"+pressView.getTop() +" getLeft:"+pressView.getLeft());

                int top = pressView.getTop();
                int left = pressView.getLeft();

                left = left - pageWidth*currentId;

                Log.d(TAG,"pageWidth:"+pageWidth);
                Log.d(TAG,"onLongPress left:"+left);
                Log.d(TAG,"currentId :"+currentId);

                mOffset2Top = (int) (e.getRawY() - longPressY);
                mOffset2Left = (int)(e.getRawX()-longPressX);
                mPoint2ItemTop = longPressY - top-mOffset2Top;
                mPoint2ItemLeft = longPressX - left-mOffset2Left;

                //开启mDragItemView绘图缓存
                pressView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                mDragBitmap = Bitmap.createBitmap(pressView.getDrawingCache());
                //这一步很关键，释放绘图缓存，避免出现重复的镜像
                pressView.destroyDrawingCache();
                createDragImage(mDragBitmap,longPressX,longPressY);

            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                scrollBy((int) distanceX, 0);
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                isFling = true;
                mVelocityX= velocityX;
                if (e1.getX() > e2.getX()) {
                    moveNext();
                } else {
                    movePrev();
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
    }



    /**
     * //程序流程3：重写onTouchEvent
     */
    private int down_x;

    private int up_x;
    private boolean isUp;



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down_x = (int) event.getX();
                mPointerId = event.getPointerId(0);
      /*          View view = getChildAt(0);
                removeView(view);
                addView(view, 1);*/

                break;

            case MotionEvent.ACTION_MOVE:

                if(isDrag) {
                    onDragItem((int) event.getX(), (int) event.getY());
                    onSwapItem((int) event.getX(), (int) event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                if(isDrag) {
                    mWindowLayoutParams.x = (int) pressView.getX()-currentId*pageWidth;
                    mWindowLayoutParams.alpha = 1f;
                    mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
                    pressView.setVisibility(VISIBLE);
                    pressView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeDragImage();
                        }
                    },600);
                    isDrag = false;
                }
                 velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                 mVelocityX = velocityTracker.getXVelocity(mPointerId);
                 velocityY = velocityTracker.getYVelocity(mPointerId);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }

                if (!isFling) {
                    up_x = (int) event.getX();
                    if (down_x - up_x > getWidth() / 2) {
                        moveNext();
                    } else if (up_x - down_x > getWidth() / 2) {
                        movePrev();
                    } else {
                        moveCurr(currentId);
                    }
                }
                isFling = false;
                isUp = false;
                break;

        }

        return true;
    }




    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    private ImageView mDragImageView;
    private WindowManager mWindowManager;
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX-mPoint2ItemLeft;
        mWindowLayoutParams.y = downY-mPoint2ItemTop;
         mWindowLayoutParams.alpha = 0.65f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mWindowLayoutParams.windowAnimations = R.anim.aplpha_in;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable,100);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pressView.setVisibility(INVISIBLE);
        }
    };


    private View swapView;
    private int dragLastPosition =-1;
    private int dragCoverPosition =-1;
    private void onSwapItem(int moveX,int moveY)
    {

              if(!isDragAutoScroll) {
                  dragCoverPosition = findDrawPosition(moveX - mPoint2ItemLeft + childWidth/3);
                  if (dragCoverPosition != dragLastPosition) {

                      dragLastPosition = dragCoverPosition;
                      swapView = getChildAt(dragCoverPosition);

                      if(pressPosition!=dragCoverPosition) {
                          swapChildView(pressView, swapView, pressPosition, dragCoverPosition);
                          pressPosition = dragCoverPosition;
                      }

                  }
              }

    }




    private int index;
    public void swapChildView(final View press, View cover, int indexPress, final int indexCover)
    {
//            try {
//                Log.d(TAG, "交换 indexPress:" + indexPress + "  indexCover: "+indexCover);
//               removeView(press);
//               // index = indexCover;
//                addView(press, indexCover);
//
//            }catch (Exception e) {
//                Log.d(TAG, "removeView:" + e.toString());
//           }

        int left,right,top,bottom;
        left = press.getLeft();
        right = press.getRight();
        top = press.getTop();
        bottom = press.getBottom();

        press.layout(cover.getLeft(), cover.getTop(), cover.getRight(), cover.getBottom());
        cover.layout(left, right, top, bottom);

        Log.d(TAG,"getChildCount: "+getChildCount());

        invalidate();
    }


    int dragLeft = 1;
    int dragRight = 2;
    int dragDirection;
    int lastDragX;
    boolean isDragAutoScroll = false;
    private void onDragItem(int moveX, int moveY) {

        if (moveX - lastDragX > 5) {
            dragDirection = dragRight;
            //Log.d(TAG, "右划");
        } else if (lastDragX - moveX > 5) {
            dragDirection = dragLeft;
            //Log.d(TAG, "左划");
        }

        mWindowLayoutParams.x = moveX - mPoint2ItemLeft;
       //mWindowLayoutParams.y = moveY - mPoint2ItemTop;

        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置

        if(!isDragAutoScroll) {
            if (dragDirection == dragRight) {
                if (mWindowLayoutParams.x >= 3*childWidth+childWidth/4 && currentId!=totalPage-1) {
                    isDragAutoScroll = true;
                    moveNext();
                    Log.d(TAG, "drag 下一页");
                }
            } else {
                if (mWindowLayoutParams.x <= 0 && currentId!=0) {
                    isDragAutoScroll = true;
                    movePrev();
                    Log.d(TAG, "drag 上一页");
                }
            }

        }
        lastDragX = moveX;
    }



    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    int card1X,card2X,card3X,card4X;
    int currPageCard;
    int pressPosition;
    private View findDragView(int  x) {
        pressPosition = findDrawPosition(x);
        View view  = getChildAt(pressPosition);
        return view;
    }


    private int findDrawPosition(int x)
    {
        int dragX = x;
        card1X = childWidth;
        card2X = childWidth*2;
        card3X = childWidth*3;
        card4X = childWidth*4;

        if(dragX<card1X) {
            currPageCard = 0;
        }else if(dragX<card2X) {
            currPageCard = 1;
        }else if(dragX<card3X)
        {
            currPageCard = 2;
        }else if(dragX<card4X) {
            currPageCard = 3;
        }
        int position = currentId*pageNumber+currPageCard;

        if(position >= childCount) {
            position = childCount-1;
        }
        return position;

    }



    //程序流程15：解决ListView不能上下滑动的问题
    //事件的分发原则：如果上下滑则不中断事件传给子ListView,如果左右滑，则中断事件不传给ListView
    private int downY;
    private int endY;
    private int downX;
    private int endX;
    private boolean isVerScroll;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isVerScroll = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                downX = (int) ev.getX();

                /**
                 * 解决按下下，onTouch得不到 down事件造成的bug
                 */
                gestureDetector.onTouchEvent(ev);

                break;

            case MotionEvent.ACTION_MOVE:
                endY = (int) ev.getY();
                endX = (int) ev.getX();

                int disX = Math.abs(endX - downX);
                int disY = Math.abs(endY - downY);

                if (disX > disY && disX > 10) {
                    isVerScroll = true;
                } else {
                    isVerScroll = false;
                }

                break;


            case MotionEvent.ACTION_UP:

                break;
        }

        //如果垂直滑动，则不中断事件
        return isVerScroll;
    }



    private void moveNext() {
        if (currentId < totalPage - 1) {
            currentId++;
        }
        moveCurr(currentId);
    }

    private void movePrev() {
        if (currentId > 0) {
            currentId--;
        }
        moveCurr(currentId);
    }

    public void moveCurr(int Id) {
        Log.d(TAG,"moveCurr: "+Id);
        // 程序流程7： 处理切换到下一页面时的速度，不要太快 速度 ＝ 距离 / 时间
        // scrollBy(currentId*getWidth() - getScrollX(), 0);
        int distance = Id * pageWidth - getScrollX();
        // 从外部调用时，把内部currentId更新
        currentId = Id;
        // 设置运行的时间
        // scrollBy(currentId*getWidth() - getScrollX(), 0);
        //时间 = 速度 * 距离 int duration =  Math.abs(distance) /mVelocityX;
        Log.d(TAG,"mVelocityX: "+ mVelocityX);
        Log.d(TAG,"distance: "+distance);
        Log.d(TAG,"时间: "+ (distance * 1000) / mVelocityX);
        myScroller.startScroll(getScrollX(), 0, distance, 0, (int) ((distance * 1000) / mVelocityX));
        // 刷新当前view onDraw执行
        invalidate();

    }




    //程序流程14：测量View大小

    /**
     * 计算 控件大小，
     * 做为viewGroup 还有一个责任，，：计算 子view的大小
     */

    int childWidth;
    int childHeight;
    int totalWidth;
    int firstSpace=22;
    int space=7;
    int pageNumber=4;
    int totalPage;
    int pageWidth;
    int childCount;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int rW = MeasureSpec.getSize(widthMeasureSpec);
        int rH = MeasureSpec.getSize(heightMeasureSpec);

        childWidth = (rW-35)/pageNumber;
        childHeight = rH;

        childCount = getChildCount();

        totalPage = childCount%pageNumber==0 ? childCount/pageNumber:childCount/pageNumber+1;
        pageWidth = childWidth*pageNumber;

        for (int i = 0; i < getChildCount(); i++) {
            BaseCard baseCard = (BaseCard) getChildAt(i);
            baseCard.setWidth(childWidth-space);
            baseCard.measure(widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) baseCard.getLayoutParams();
            layoutParams.left = (childWidth) * i+22;
        }

        totalWidth = childWidth*childCount;
        setMeasuredDimension(totalWidth,rH);
    }



    /**
     * //程序流程2： 把资源排列好，为每一个子View确定位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            /**
             * 父view 会根据子view的需求，和自身的情况，来综合确定子view的位置,(确定他的大小)
             */
            // 指定子view的位置 , 左， 上， 右， 下，
            // 是指view距离parent的距离，取确定view在viewGround坐标系中的位置
            // left top right bottom
            LayoutParams lParams = (LayoutParams) child.getLayoutParams();
            child.layout(lParams.left, lParams.top, lParams.left + childWidth,
                    lParams.top + childHeight);

        }
    }



    // 程序流程11：留一个接口，用来设置指示器
    /**
     * 设置RadioGroup的回调接口
     *
     * @author Administrator //
     */
    private OnPageChangerListener onPageChangerListener;

    public interface OnPageChangerListener {

        public void onPageChange(int item);
    }

    public void setOnPageChangerListener(OnPageChangerListener listener) {
        this.onPageChangerListener = listener;
    }

    /**
     * invalidate也会导致 copmuteScroll执行
     */
    @Override
    public void computeScroll() {
        // 程序流程8： 每次切换一小段距离
        if (myScroller.computeScrollOffset()) {
            int newX = myScroller.getCurrX();
            scrollTo(newX, 0);
            invalidate();
        } else {
            isDragAutoScroll = false;
            if (onPageChangerListener != null)
                onPageChangerListener.onPageChange(currentId);
        }
    }



    public static class LayoutParams extends MarginLayoutParams{

        public int left = 0;
        public int top = 0;

        public LayoutParams(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(int arg0, int arg1) {
            super(arg0, arg1);
        }

        public LayoutParams(ViewGroup.LayoutParams arg0) {
            super(arg0);
        }

    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

}
