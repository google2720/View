package yunovo.com.lanucher.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import yunovo.com.lanucher.base.lifecycle.FragmentParam;

/**
 * Created by zhangjunjun on 2017/3/29.
 */

public abstract class FragmentBaseActivity extends AppCompatActivity {
    private final static String LOG_TAG = FragmentBaseActivity.class.getSimpleName();

    public static boolean DEBUG = true;
    protected BaseFragment mCurrentFragment;
    private boolean mCloseWarned;

    /**
     * return the string id of close warning
     * <p/>
     * return value which lower than 1 will exit instantly when press back key
     *
     * @return
     */
    protected abstract String getCloseWarning();

    protected abstract int getFragmentContainerId();

    public void pushFragmentToBackStack(Class<?> cls, Object data) {
        FragmentParam param = new FragmentParam();
        param.cls = cls;
        param.data = data;
        goToThisFragment(param);
    }

    public void pushFragmentToBackStack(String frtName,Object data) {
        FragmentParam param = new FragmentParam();
        try {
            param.to  = (BaseFragment) Class.forName(frtName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        param.fragmentName = frtName;
        param.data = data;
        goToThisFragment(param);
    }


    protected String getFragmentTag(FragmentParam param) {
        StringBuilder sb = new StringBuilder(param.cls.toString());
        return sb.toString();
    }

    private void goToThisFragment(FragmentParam param) {
        int containerId = getFragmentContainerId();
        if (param.cls == null && param.to==null) {
            return;
        }
        try {
            String fragmentTag="";
            if(!TextUtils.isEmpty(param.fragmentName)) {
                fragmentTag  = param.fragmentName;
            }else if(param.cls!=null) {
                fragmentTag = getFragmentTag(param);
            }

            FragmentManager fm = getSupportFragmentManager();
            if (DEBUG) {
                Log.d(LOG_TAG, "before operate, stack entry count: "+fm.getBackStackEntryCount());
            }

            BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(fragmentTag);
            if (fragment == null) {
                fragment = param.to;
                if(fragment==null) {
                    fragment = (BaseFragment) param.cls.newInstance();
                }
            }

            if (mCurrentFragment != null && mCurrentFragment != fragment) {
                mCurrentFragment.onLeave();
            }
            fragment.onEnter(param.data);

            FragmentTransaction ft = fm.beginTransaction();
            //切换动画
            // ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);

            if (fragment.isAdded()) {
                Log.d(LOG_TAG, "%s has been added, will be shown again: "+fragmentTag);
                ft.show(fragment);
            }
            else {
                Log.d(LOG_TAG, "%s is added: "+fragmentTag);
                ft.add(containerId, fragment, fragmentTag);
            }
            mCurrentFragment = fragment;
            ft.addToBackStack(fragmentTag);
            ft.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mCloseWarned = false;
    }



    public void goToFragment(Class<?> cls, Object data) {
        if (cls == null) {
            return;
        }
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(cls.toString());
        if (fragment != null) {
            mCurrentFragment = fragment;
            fragment.onBackWithData(data);
        }
        getSupportFragmentManager().popBackStackImmediate(cls.toString(), 0);
    }

    public void popTopFragment(Object data) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate();
        if (tryToUpdateCurrentAfterPop() && mCurrentFragment != null) {
            mCurrentFragment.onBackWithData(data);
        }
    }

    public void popToRoot(Object data) {
        FragmentManager fm = getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1) {
            fm.popBackStackImmediate();
        }
        popTopFragment(data);
    }

    /**
     * 处理返回逻辑
     * 如果back按下,但是应该留在当前activity,返回true
     * @return
     */
    protected boolean processBackPressed() {
        return false;
    }


    /**
     * 处理返回动作
     */
    @Override
    public void onBackPressed() {

        // process back for fragment
        if (processBackPressed()) {
            return;
        }

        // process back for fragment
        boolean enableBackPressed = true;
        if (mCurrentFragment != null) {
            enableBackPressed = !mCurrentFragment.processBackPressed();
        }
        if (enableBackPressed) {
            int cnt = getSupportFragmentManager().getBackStackEntryCount();
            //如果fragment栈中fragment只有或少于一个,当前Activity是否是最后一个Activity
            if (cnt <= 1 && isTaskRoot()) {
                //取得警告信息
                String closeWarningHint = getCloseWarning();
                if (!mCloseWarned && !TextUtils.isEmpty(closeWarningHint)) {
                    Toast toast = Toast.makeText(this, closeWarningHint, Toast.LENGTH_SHORT);
                    toast.show();
                    mCloseWarned = true;
                } else {
                    doReturnBack();
                }
            } else {
                mCloseWarned = false;
                doReturnBack();
            }
        }
    }


    /**
     * 在pop后，尝试更新当前 mCurrentFragment
     * @return
     */
    private boolean tryToUpdateCurrentAfterPop() {
        FragmentManager fm = getSupportFragmentManager();
        int cnt = fm.getBackStackEntryCount();
        if (cnt > 0) {
            //根据序号返回后台堆栈中的BackStackEntry对象，最底的序号为0。
            String name = fm.getBackStackEntryAt(cnt - 1).getName();
            Fragment fragment = fm.findFragmentByTag(name);
            if (fragment != null && fragment instanceof BaseFragment) {
                mCurrentFragment = (BaseFragment) fragment;
            }
            return true;
        }
        return false;
    }

    /**
     * 执行回退
     */
    protected void doReturnBack() {
        //返回堆栈的总数目
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else {
            //弹出堆栈中的一个并且立即显示
            getSupportFragmentManager().popBackStackImmediate();
            if (tryToUpdateCurrentAfterPop() && mCurrentFragment != null) {
                mCurrentFragment.onBack();
            }
        }
    }

    public void hideKeyboardForCurrentFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showKeyboardAtView(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void forceShowKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    protected void exitFullScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
