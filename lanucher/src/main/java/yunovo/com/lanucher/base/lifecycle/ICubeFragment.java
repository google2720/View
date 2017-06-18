package yunovo.com.lanucher.base.lifecycle;

/**
 * Created by zhangjunjun on 2016/7/8.
 * 提供了一些方法生成Fragment ,Activity入栈行为
 */
public interface ICubeFragment {

    void onEnter(Object data);

    void onLeave();

    void onBack();

    void onBackWithData(Object data);

    /**
     * 处理返回逻辑,如果back按下返回true
     * @return
     */
    boolean processBackPressed();
}
