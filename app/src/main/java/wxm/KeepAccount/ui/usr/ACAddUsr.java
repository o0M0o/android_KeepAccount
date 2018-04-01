package wxm.KeepAccount.ui.usr;

import android.content.res.Configuration;
import android.os.Bundle;

import wxm.KeepAccount.ui.base.SwitcherActivity.ACSwitcherActivity;

/**
 * add usr
 */
public class ACAddUsr extends ACSwitcherActivity<FrgUsrAdd> {

    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        addFragment(new FrgUsrAdd());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getHotFragment().refreshUI();
    }
}
