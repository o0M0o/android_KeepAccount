package wxm.KeepAccount.ui.usr

import android.content.res.Configuration
import android.os.Bundle

import wxm.androidutil.switcher.ACSwitcherActivity

/**
 * add usr
 */
class ACAddUsr : ACSwitcherActivity<FrgUsrAdd>() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgUsrAdd())
    }
}
