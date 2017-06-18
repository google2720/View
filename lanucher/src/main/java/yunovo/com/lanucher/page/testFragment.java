package yunovo.com.lanucher.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import yunovo.com.lanucher.R;
import yunovo.com.lanucher.base.BaseFragment;
import yunovo.com.lanucher.card.MyViewPage;

/**
 * Created by zhangjunjun on 2017/4/12.
 */

public class testFragment extends BaseFragment {

    MyViewPage myViewPage;

    private final int imageIds[] = { R.drawable.a1, R.drawable.a2,
            R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6 };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_test,container,false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        myViewPage = (MyViewPage) view.findViewById(R.id.myviewpager);

        for (int i = 0; i < imageIds.length; i++)
        {
            ImageView imageView = new ImageView(getActivity());
//            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(300, 400);
//            view.setLayoutParams(layoutParams);

            view.setBackgroundResource(imageIds[i]);
            myViewPage.addView(view);

        }

//        myViewPage.addView(new MusicCard(getActivity()),0);
//        myViewPage.addView(new TimeCard(getActivity()),1);
//        myViewPage.addView(new TrafficCard(getActivity()),2);
//        myViewPage.addView(new WeChatCard(getActivity()),3);
    }
}
