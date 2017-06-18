package com.example.administrator.opensouce;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LineTabIndicator extends HorizontalScrollView {

    private ColorTrackView left;
    private ColorTrackView right;
    //判断是否是点击上面的标题跳转的
    public boolean isClickTo;

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    //监听viewpager的滑动
    public OnPageChangeListener mOnPageChangeListener;
    private OnTabSelectedListener mTabSelectedListener;

    private LinearLayout mTabsContainer;
    private ViewPager mPager;

    private int tabCount;
    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint linePaint;
    private Paint diviPaint;

    private int indicatorColor = 0xFFF67E0A;//下划线（选择）
    private int underlineColor = 0x33000000;
    private int dividerColor = 0x00000000;//两个标签之间的分割线
    private int textSelectedColor = 0xFFF67E0A;//字体（选择）
    private int textUnselectColor = 0xFF000000;//字体（没有选中）

    private boolean enableExpand = true;//设置两个标签的padding
    private boolean enableDivider = true;//设置下划线
    private boolean indicatorOnTop = false;//是否画在文字的上面
    private boolean viewPagerScrollWithAnimation = true;
    private int tabTextSize = 16;
    private int scrollOffset = 52;
    private float indicatorHeight = 1.5f;
    private float underlineHeight = 1f;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;
    private int lastScrollX = 0;
    private int margin = 22;
    private boolean isFirstIn = true;

    public LineTabIndicator(Context context) {
        this(context, null);
    }

    public LineTabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineTabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        mTabsContainer.setLayoutParams(params);
        addView(mTabsContainer);

        scrollOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        dividerPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        indicatorHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                margin, dm);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.LineTabIndicator);
        underlineColor = ta.getColor(R.styleable.LineTabIndicator_underlineColor, underlineColor);
        indicatorColor = ta.getColor(R.styleable.LineTabIndicator_indicatorColor, indicatorColor);
        dividerColor = ta.getColor(R.styleable.LineTabIndicator_dividerColor, dividerColor);
        textSelectedColor = ta.getColor(R.styleable.LineTabIndicator_textSelectedColor, textSelectedColor);
        textUnselectColor = ta.getColor(R.styleable.LineTabIndicator_textUnselectColor, textUnselectColor);
        enableExpand = ta.getBoolean(R.styleable.LineTabIndicator_enableExpand, enableExpand);
        enableDivider = ta.getBoolean(R.styleable.LineTabIndicator_enableDivider, enableDivider);
        indicatorOnTop = ta.getBoolean(R.styleable.LineTabIndicator_indicatorOnTop, indicatorOnTop);
        dividerPadding = ta.getInteger(R.styleable.LineTabIndicator_dividerPadding1, dividerPadding);
        tabPadding = ta.getInteger(R.styleable.LineTabIndicator_tabPadding, tabPadding);
        ta.recycle();

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Style.FILL);

        diviPaint = new Paint();
        diviPaint.setAntiAlias(true);
        diviPaint.setStrokeWidth(dividerWidth);
    }

    public void setFirstIn(boolean firstIn) {
        isFirstIn = firstIn;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        linePaint.setColor(underlineColor);
        if (indicatorOnTop) {
            canvas.drawRect(0, 0, mTabsContainer.getWidth(), underlineHeight,
                    linePaint);
        } else {
            canvas.drawRect(0, height - underlineHeight,
                    mTabsContainer.getWidth(), height, linePaint);
        }

        linePaint.setColor(indicatorColor);
        float lineLeft;//线
        float lineRight;
        View currentTab = mTabsContainer.getChildAt(currentPosition);

        lineLeft = currentTab.getLeft();
        lineRight = currentTab.getRight();
        //第一个标签离左侧有10距离
        if (currentPosition == 0) {
            lineLeft = currentTab.getLeft() + 10;
        }

        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
                    * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
                    * lineRight);
        }
        //最后一个标签离右侧有10距离
        if (currentPosition == tabCount - 1) {
            lineRight = currentTab.getRight() - 10;
        }
        if (indicatorOnTop) {
            canvas.drawRect(lineLeft, 0, lineRight, indicatorHeight, linePaint);
        } else {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight,
                    height, linePaint);
        }

        if (enableDivider) {
            diviPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = mTabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
                        height - dividerPadding, diviPaint);
            }
        }
    }

    //关联viewpager
    public void setViewPager(ViewPager pager) {
        this.mPager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }
        pager.addOnPageChangeListener(new PageListener());

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public void setOnTabReselectedListener(OnTabSelectedListener listener) {
        mTabSelectedListener = listener;
    }

    public void notifyDataSetChanged() {

        mTabsContainer.removeAllViews();

        tabCount = mPager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            addTab(i, mPager.getAdapter().getPageTitle(i).toString());
        }
        TabView childAt1 = (TabView) mTabsContainer.getChildAt(0);
        if (isFirstIn) {
            childAt1.getTextView().setProgress(1.0f);//第一个默认为选中的颜色
        }
        updateTabStyles();
    }


    public LinearLayout getmTabsContainer() {
        return mTabsContainer;
    }

    private class TabView extends RelativeLayout {
        private ColorTrackView mTabText;

        public TabView(Context context) {
            super(context);
            init();
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TabView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            View views = View.inflate(getContext(), R.layout.viewpager_title, null);
            mTabText = (ColorTrackView) views.findViewById(R.id.trackview);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            params.leftMargin = margin / 2;
            params.rightMargin = margin / 2;
            this.addView(views, params);

        }

        public ColorTrackView getTextView() {
            return mTabText;
        }
    }

    private void addTab(final int position, String title) {
        TabView tab = new TabView(getContext());
        tab.getTextView().setText(title);
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int oldSelected = mPager.getCurrentItem();

                if (oldSelected != position && mTabSelectedListener != null) {
                    mTabSelectedListener.onTabSelected(position);
                }
                isClickTo = true;
                mPager.setCurrentItem(position, viewPagerScrollWithAnimation);
            }
        });

        if (!enableExpand) {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
        }
        mTabsContainer.addView(tab, position,
                enableExpand ? new LinearLayout.LayoutParams(0,
                        LayoutParams.MATCH_PARENT, 1.0f)
                        : new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT));
    }

    public void setTabText(int position, String text) {
        if (position < 0 || position > (mTabsContainer.getChildCount() - 1))
            throw new RuntimeException("tabs does not have this position.");

        View tab = mTabsContainer.getChildAt(position);
        if (tab instanceof ColorTrackView) {
            ((ColorTrackView) tab).setText(text);
        }
    }

    public boolean isIndicatorOnTop() {
        return indicatorOnTop;
    }

    public void setIndicatorOnTop(boolean indicatorOnTop) {
        this.indicatorOnTop = indicatorOnTop;
    }

    public boolean isEnableExpand() {
        return enableExpand;
    }

    public void setEnableExpand(boolean enableExpand) {
        this.enableExpand = enableExpand;
    }

    public boolean isEnableDivider() {
        return enableDivider;
    }

    public void setEnableDivider(boolean enableDivider) {
        this.enableDivider = enableDivider;
    }

    public void setViewPagerScrollWithAnimation(boolean enable) {
        this.viewPagerScrollWithAnimation = enable;
    }

    public boolean getViewPagerScrollWithAnimation() {
        return this.viewPagerScrollWithAnimation;
    }

    public void setCurrentItem(int item) {
        mPager.setCurrentItem(item, viewPagerScrollWithAnimation);
    }

    public void tabSelect(int index) {
        final int tabCount = mTabsContainer.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabsContainer.getChildAt(i);
            final boolean isSelected = (i == index);
            child.setSelected(isSelected);
            if (!isClickTo) {
                if (isSelected) {
                    ((TabView) child).getTextView().setTextChangeColor(textSelectedColor);
                } else {
                    ((TabView) child).getTextView().setTextOriginColor(textUnselectColor);
                    ((TabView) child).getTextView().setTextChangeColor(textSelectedColor);
                }
            } else {
                if (isSelected) {
                    ((TabView) child).getTextView().setTextOriginColor(textSelectedColor);
                } else {
                    ((TabView) child).getTextView().setTextOriginColor(textUnselectColor);
                    ((TabView) child).getTextView().setTextChangeColor(textSelectedColor);
                }
            }
        }
    }

    //刷新所有的颜色
    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            TabView childAt1 = (TabView) mTabsContainer.getChildAt(i);
            childAt1.getTextView().setBackgroundColor(Color.TRANSPARENT);
            childAt1.getTextView().setTextChangeColor(Color.BLACK);
            childAt1.getTextView().setTextOriginColor(Color.BLACK);
        }
        tabSelect(mPager.getCurrentItem());
    }

    private void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }

        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            if (positionOffset > 0 && !isClickTo) {//滑动改变字体的渐变颜色
                TabView childAt1 = (TabView) mTabsContainer.getChildAt(position);
                TabView childAt2 = (TabView) mTabsContainer.getChildAt(position + 1);
                left = childAt1.getTextView();
                right = childAt2.getTextView();
                left.setDirection(1);
                right.setDirection(0);
                left.setProgress(1 - positionOffset);
                right.setProgress(positionOffset);

            }
            scrollToChild(position, (int) (positionOffset * mTabsContainer
                    .getChildAt(position).getWidth()));

            invalidate();

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(mPager.getCurrentItem(), 0);
            }

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (right != null)
                right.setProgress(0);
            if (left != null)
                left.setProgress(0);
            tabSelect(position);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }
    }

}