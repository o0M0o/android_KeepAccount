package wxm.KeepAccount.ui.welcome

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * first page after login
 */
class ACWelcome : ACBase<FrgWelcome>()  {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgWelcome())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun leaveActivity() {
        if ((hotFragment as FrgWelcome).leaveFrg()) {
            setResult(GlobalDef.INTRET_USR_LOGOUT, Intent())
            finish()
        }
    }
}
