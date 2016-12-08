package wxm.KeepAccount.ui.acutility;

import android.content.res.Configuration;

import wxm.KeepAccount.Base.define.BaseAppCompatActivity;
import wxm.KeepAccount.ui.fragment.utility.FrgUsrAdd;

/**
 * 添加用户
 */
public class ACAddUsr extends BaseAppCompatActivity {
    private FrgUsrAdd  mFGUsrAdd = new FrgUsrAdd();

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
