package com.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhangjunjun on 2017/5/12.
 */

public class myView extends View {
    public myView(Context context) {
        this(context,null);
    }

    public myView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public myView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }



}
