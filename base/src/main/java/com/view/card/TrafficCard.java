package com.view.card;

import android.content.Context;

import com.view.R;


/**
 * Created by zhangjunjun on 2017/3/29.
 */

public class TrafficCard extends BaseCard {

    public TrafficCard(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_traffic_card;
    }

    @Override
    protected void setBackgroundResource() {
        setBackgroundResource(R.drawable.fm_n);
    }
}
