package yunovo.com.lanucher.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangjunjun on 2017/3/30.
 */

public class CustomLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "CustomLayoutManager";
    private int horizontalScrollOffset=0;
    private int totalWidth = 0;
    private int pageSize = 0;
    private int index = 0;
    private int columns = 0;


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }


    public CustomLayoutManager(int columns) {
        this.columns = columns;
        this.pageSize = getItemCount()/columns;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item，直接返回
        if (getItemCount() <= 0) return;
        // 跳过preLayout，preLayout主要用于支持动画
        if (state.isPreLayout()) {
            return;
        }

        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler);
        //定义水平方向的偏移量
        int offsetX = 0;
        totalWidth = 0;

        for (int i = 0; i < getItemCount(); i++) {
            //这里就是从缓存里面取出
            View view = recycler.getViewForPosition(i);
            //将View加入到RecyclerView中
            addView(view);
            //对子View进行测量
            measureChildWithMargins(view, 0, 0);
            //把宽高拿到，宽高都是包含ItemDecorate的尺寸
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            //将View布局            left    top   ritht         bootm
            layoutDecorated(view, 0+offsetX, 0, offsetX+width, height);
            //将水平方向偏移量增大width
            offsetX += width;
            totalWidth +=width;
        }
        totalWidth = Math.max(totalWidth, getHorizontalSpace());
    }

    @Override
    public boolean canScrollHorizontally() {
        return  true;
    }


    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State
            state) {
        int travel = dx;

        //如果滑动到最左边
        if (horizontalScrollOffset + dx < 0) {
            travel = -horizontalScrollOffset;
        } else if (horizontalScrollOffset + dx > totalWidth - getHorizontalSpace()) {//如果滑动到最右边
            travel = totalWidth - getHorizontalSpace() - horizontalScrollOffset;
        }
        //将水平方向的偏移量+travel
        horizontalScrollOffset += travel;

        // 平移容器内的item
        offsetChildrenHorizontal(-travel);
        return travel;
    }


}
