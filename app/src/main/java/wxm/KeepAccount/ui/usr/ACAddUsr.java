package wxm.KeepAccount.ui.usr;

import android.content.res.Configuration;

import wxm.androidutil.ExActivity.BaseAppCompatActivity;

/**
 * 添加用户
 */
public class ACAddUsr extends BaseAppCompatActivity {
    private final FrgUsrAdd mFGUsrAdd = new FrgUsrAdd();

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACAddUsr";
        mFGHolder = mFGUsrAdd;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        mFGUsrAdd.refreshUI();
    }
}
