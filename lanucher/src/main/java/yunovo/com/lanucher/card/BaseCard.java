package yunovo.com.lanucher.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import yunovo.com.lanucher.R;

/**
 * Created by zhangjunjun on 2017/3/29.
 */

public abstract class BaseCard extends FrameLayout {

    private LayoutInflater layoutInflater;
    private FrameLayout rootCardView;

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
        rootCardView = (FrameLayout) findViewById(R.id.card_root);
    }

    protected abstract int getLayoutId();

    public BaseCard setWidth(int w) {
        ViewGroup.LayoutParams layoutParams = rootCardView.getLayoutParams();
        layoutParams.width = w;
        rootCardView.setLayoutParams(layoutParams);
        return this;
    }
}
