package com.view.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;


/**
 * Created by zhangjunjun on 2017/3/29.
 */

public abstract class BaseCard extends FrameLayout {

    private LayoutInflater layoutInflater;

    public BaseCard(Context context) {
        super(context);
        init();
    }

    public BaseCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(getLayoutId(),this,true);
        setBackgroundResource();
    }

    protected abstract int getLayoutId();


    protected abstract void setBackgroundResource();

}
