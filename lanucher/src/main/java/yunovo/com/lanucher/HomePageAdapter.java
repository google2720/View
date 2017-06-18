/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yunovo.com.lanucher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yunovo.com.lanucher.card.MusicCard;
import yunovo.com.lanucher.card.NavCard;
import yunovo.com.lanucher.card.TimeCard;
import yunovo.com.lanucher.card.TrafficCard;
import yunovo.com.lanucher.card.WeChatCard;
import yunovo.com.lanucher.card.WeatherCard;
import yunovo.com.lanucher.helper.ItemTouchHelperAdapter;
import yunovo.com.lanucher.helper.ItemTouchHelperViewHolder;
import yunovo.com.lanucher.helper.OnStartDragListener;


/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static final String TAG = "HomePageAdapter";
    private final List<View> mItems = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private Context mContext;

    public HomePageAdapter(Context context, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;

        int w = getScreenWidth(mContext);
        int av = w/4-20;

        mItems.add( new TimeCard(mContext).setWidth(av));
        mItems.add(new MusicCard(mContext).setWidth(av));
        mItems.add(new WeatherCard(mContext).setWidth(av));
        mItems.add(new NavCard(mContext).setWidth(av));
        mItems.add(new WeChatCard(mContext).setWidth(av));
        mItems.add(new TrafficCard(mContext).setWidth(av));

        mItems.add(new NavCard(mContext).setWidth(av));
        mItems.add( new TimeCard(mContext).setWidth(av));
        mItems.add(new MusicCard(mContext).setWidth(av));
        mItems.add(new WeatherCard(mContext).setWidth(av));
        mItems.add(new TrafficCard(mContext).setWidth(av));
        Log.d(TAG,"HomePageAdapter : ");
    }



    public  int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    public  int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder : ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_container, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }



    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder : "+ position);
        holder.frameLayout.addView(mItems.get(position));

    }

    @Override
    public void onItemDismiss(int position) {
        Log.d(TAG,"onItemDismiss : "+ position);
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public FrameLayout frameLayout=null;


        public ItemViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG,"ItemViewHolder : ");
            frameLayout = (FrameLayout) itemView.findViewById(R.id.item_container);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }
}
