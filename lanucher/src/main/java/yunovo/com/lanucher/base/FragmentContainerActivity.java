package yunovo.com.lanucher.base;

import android.graphics.PixelFormat;
import android.os.Bundle;

import yunovo.com.lanucher.R;
import yunovo.com.lanucher.page.HomeFragment;


public class FragmentContainerActivity extends FragmentBaseActivity {


    //页面类型KEY
    public final static String PAGE_NAME = "page_name";
    //参数KEY
    public final static String PARAM_KEY = "param_key";
    private Bundle parms = new Bundle();
    private String pageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragment_container);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        pageName = getIntent().getStringExtra(PAGE_NAME);
        parms = getIntent().getBundleExtra(PARAM_KEY);
        if(pageName==null || parms==null) {
            pushFragmentToBackStack(HomeFragment.class, "");
        }else {
            pushFragmentToBackStack(pageName, parms);
        }


    }


    @Override
    protected String getCloseWarning() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

}
