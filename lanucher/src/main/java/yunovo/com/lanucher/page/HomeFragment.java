package yunovo.com.lanucher.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import yunovo.com.lanucher.HomePageAdapter;
import yunovo.com.lanucher.R;
import yunovo.com.lanucher.base.BaseFragment;
import yunovo.com.lanucher.helper.CustomScrollHelper;
import yunovo.com.lanucher.helper.MyItemDecoration;
import yunovo.com.lanucher.helper.OnStartDragListener;
import yunovo.com.lanucher.helper.SimpleItemTouchHelperCallback;

/**
 * Created by zhangjunjun on 2017/3/29.
 */

public class HomeFragment extends BaseFragment {

    private RecyclerView homeRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private CustomScrollHelper mCustomScrollHelper;
    private ValueAnimator mAnimator;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home,container,false);
        setupViews(view);
        return view;
    }



    public  int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void setupViews(View view) {

        homeRecyclerView = (RecyclerView) view.findViewById(R.id.home_page);
        HomePageAdapter adapter = new HomePageAdapter(getActivity(), new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

            }
        });

        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //CustomLayoutManager layoutManager = new CustomLayoutManager(4);
        //homeRecyclerView.setLayoutManager(layoutManager);


        homeRecyclerView.setLayoutManager(linearLayoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(homeRecyclerView);


        homeRecyclerView.addItemDecoration(new MyItemDecoration());


        //mCustomScrollHelper = new CustomScrollHelper(homeRecyclerView);
       // adapter.setCustomScrollHelper(mCustomScrollHelper);


     /*  GroupSnapHelper groupSnapHelper = new GroupSnapHelper(4);
       groupSnapHelper.attachToRecyclerView(homeRecyclerView);*/


    }



}
