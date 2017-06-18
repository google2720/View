package yunovo.com.lanucher.card;

import android.content.Context;

import yunovo.com.lanucher.R;

/**
 * Created by zhangjunjun on 2017/3/29.
 */

public class NavCard extends BaseCard {

    public NavCard(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_nav_card;
    }
}
