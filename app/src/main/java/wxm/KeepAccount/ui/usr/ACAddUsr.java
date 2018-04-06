package wxm.KeepAccount.ui.usr;

import android.content.res.Configuration;
import android.os.Bundle;

import wxm.androidutil.Switcher.ACSwitcherActivity;

/**
 * add usr
 */
public class ACAddUsr extends ACSwitcherActivity<FrgUsrAdd> {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getHotFragment().refreshUI();
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(new FrgUsrAdd());
    }
}
