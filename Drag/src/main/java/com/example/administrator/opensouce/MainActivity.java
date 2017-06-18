package com.example.administrator.opensouce;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHANNELREQUEST = 1;
    private static String[] titles = {"首页", "第二页", "末尾"};
    private ArrayList<ChannelItem> userChannelList;
    private ImageView button_more_columns;
    private LineTabIndicator lineTabIndicator;
    private MPagerAdapter adapter;
    private int count;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViews() {
        lineTabIndicator = (LineTabIndicator) findViewById(R.id.lineTab);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        button_more_columns = (ImageView) findViewById(R.id.button_more_columns);
        button_more_columns.setOnClickListener(this);
        adapter = new MPagerAdapter();
        viewpager.setAdapter(adapter);
        lineTabIndicator.setViewPager(viewpager);
        //滑动监听
        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_MOVE){
                    if (lineTabIndicator.isClickTo){
                        lineTabIndicator.isClickTo=false;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_more_columns:
                Intent intent_channel = new Intent(getApplicationContext(), ChannelActivity.class);
                startActivityForResult(intent_channel, CHANNELREQUEST);
                break;
        }

    }

    private class MPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(MainActivity.this);

            tv.setText("现在是" + userChannelList.get(position).getName());
            container.addView(tv);
            return tv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return userChannelList.get(position).getName();
        }
    }

    private void initData() {
        initColumnData();
    }


    /**
     * 获取Column栏目 数据
     */
    private void initColumnData() {
        userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(AppApplication.getApp().getSQLHelper()).getUserChannel());
        count = userChannelList.size();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHANNELREQUEST:
                lineTabIndicator.setFirstIn(false);
                if (data != null && data.getStringExtra("position") != null) {
                    String position = data.getStringExtra("position");
                    lineTabIndicator.isClickTo=true;
                    viewpager.setCurrentItem(Integer.parseInt(position));
                    lineTabIndicator.tabSelect(Integer.parseInt(position));
                }
                initColumnData();
                initTabColumn();
                lineTabIndicator.tabSelect(viewpager.getCurrentItem());
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        count = userChannelList.size();
        adapter.notifyDataSetChanged();
        lineTabIndicator.setViewPager(viewpager);
//        viewpager.setCurrentItem(0);
    }

}
