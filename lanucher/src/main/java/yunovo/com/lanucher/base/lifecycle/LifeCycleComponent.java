package yunovo.com.lanucher.base.lifecycle;

/**
 * Created by zhangjunjun on 2016/7/8.
 * 生命周期接口
 */
public interface LifeCycleComponent {

    /**
     * UI变得部分不可见
     * like {@link android.app.Activity#onPause}
     */
     void onBecomesPartiallyInvisible();

    /**
     * UI从部分或者完全不可见变成可见
     * like {@link android.app.Activity#onResume}
     */
     void onBecomesVisible();

    /**
     * UI完全不可见
     * like {@link android.app.Activity#onStop}
     */
     void onBecomesTotallyInvisible();

    /**
     * UI从完全不可见变成可见
     * like {@link android.app.Activity#onRestart}
     */
     void onBecomesVisibleFromTotallyInvisible();

    /**
     * 销毁
     * like {@link android.app.Activity#onDestroy}
     */
     void onDestroy();

}
