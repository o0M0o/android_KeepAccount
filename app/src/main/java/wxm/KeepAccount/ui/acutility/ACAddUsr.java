package wxm.KeepAccount.ui.acutility;

import android.os.Bundle;

import wxm.KeepAccount.Base.define.BaseAppCompatActivity;
import wxm.KeepAccount.ui.fragment.utility.FrgUsrAdd;

/**
 * 添加用户
 */
public class ACAddUsr extends BaseAppCompatActivity {
    @Override
    protected void initUi(Bundle savedInstanceState) {
        LOG_TAG = "ACAddUsr";
        mFGHolder = new FrgUsrAdd();
        super.initUi(savedInstanceState);
    }

    @Override
    protected void leaveActivity() {
        finish();
    }
}
