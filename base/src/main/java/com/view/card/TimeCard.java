package com.view.card;

import android.content.Context;

import com.view.R;


/**
 * Created by zhangjunjun on 2017/3/29.
 */

public class TimeCard extends BaseCard {


    public TimeCard(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_time_card;
    }

    @Override
    protected void setBackgroundResource() {
        setBackgroundResource(R.drawable.kaolafm_n);
    }

}
