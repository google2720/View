package com.view;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * Created by zhangjunjun on 2017/6/26.
 */

public class SlideGroupShadowBuilder extends View.DragShadowBuilder {

    View view;
    int x,y;
    public SlideGroupShadowBuilder(View view){
        super(view);
        this.view = view;
    }

    public void setOffset(int x,int y) {
        this.x = x;
        this.y = y;
    }

    @SuppressLint("NewApi")
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.x = view.getWidth();
        shadowSize.y = view.getHeight();
        shadowTouchPoint.x = x-view.getLeft();
        shadowTouchPoint.y = y-view.getTop();
    }
    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
