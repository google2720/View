package yunovo.com.lanucher.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import yunovo.com.lanucher.base.FragmentContainerActivity;


public class IntentUtils {

    public static void startFragment(Activity context, Bundle bundle, Class<?> cls) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        intent.putExtra(FragmentContainerActivity.PARAM_KEY,bundle);
        intent.putExtra(FragmentContainerActivity.PAGE_NAME, cls.getName());
        context.startActivity(intent);
    }

    public static void startFragment(Activity context, Class<?> cls) {
        Intent intent = new Intent(context, FragmentContainerActivity.class);
        intent.putExtra(FragmentContainerActivity.PAGE_NAME,cls.getName());
        context.startActivity(intent);
    }

}
