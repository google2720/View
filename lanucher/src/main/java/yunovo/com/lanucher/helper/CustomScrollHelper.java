package yunovo.com.lanucher.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhangjunjun on 2017/3/30.
 */

public class CustomScrollHelper {

    private static final String TAG = "CustomScrollHelper";
    private RecyclerView mRecyclerView;
    private int currentIndex;
    private int offsetX = 0;
    private int startX = 0;
    private ValueAnimator mAnimator = null;

    private MyOnScrollListener mOnScrollListener = new MyOnScrollListener();
    private MyOnFlingListener mOnFlingListener = new MyOnFlingListener();
    private MyOnTouchListener mOnTouchListener = new MyOnTouchListener();

    public CustomScrollHelper(final RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        //处理滑动
        mRecyclerView.setOnFlingListener(mOnFlingListener);
        //设置滚动监听，记录滚动的状态，和总的偏移量
        mRecyclerView.setOnScrollListener(mOnScrollListener);
        //记录滚动开始的位置
        mRecyclerView.setOnTouchListener(mOnTouchListener);
    }


    public void updateLayoutManger() {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            startX = 0;
            offsetX = 0;
        }


    public class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //newState==0表示滚动停止，此时需要处理回滚
            Log.d(TAG,"newState:");
            if (newState == 0) {
                boolean move;
                int vX = 0, vY = 0;
                    int absX = Math.abs(offsetX - startX);
                    move = absX > recyclerView.getWidth() / 2;
                    if (move) {
                        vX = offsetX - startX < 0 ? -1000 : 1000;
                    }
                mOnFlingListener.onFling(vX, vY);
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //滚动结束记录滚动的偏移量
            offsetX += dx;
            Log.d(TAG,"offsetX:"+dx);

        }
    }



    public class MyOnFlingListener extends RecyclerView.OnFlingListener {

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            //获取开始滚动时所在页面的index
            int p = getStartPageIndex();

            //记录滚动开始和结束的位置
            int endPoint = 0;
            int startPoint = 0;

            startPoint = offsetX;
            if (velocityX < 0) {
                p--;
            } else if (velocityX > 0) {
                p++;
            }
            endPoint = p * mRecyclerView.getWidth();

            if (endPoint < 0) {
                endPoint = 0;
            }

            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = new ValueAnimator().ofInt(startPoint, endPoint);

                mAnimator.setDuration(300);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int nowPoint = (int) animation.getAnimatedValue();
                            int dx = nowPoint - offsetX;
                            mRecyclerView.scrollBy(dx, 0);
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                });
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }

            mAnimator.start();

            return true;
        }
    }



    public class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
               startX = offsetX;
            }
            return false;
        }
    }


    private int getStartPageIndex() {
        int  p = startX / mRecyclerView.getWidth();
        return p;
    }



}
