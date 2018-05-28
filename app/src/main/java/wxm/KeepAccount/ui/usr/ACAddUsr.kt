package wxm.KeepAccount.ui.usr

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import wxm.KeepAccount.define.GlobalDef

import wxm.androidutil.ui.activity.ACSwitcherActivity

/**
 * add usr
 */
class ACAddUsr : ACSwitcherActivity<FrgUsrAdd>() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgUsrAdd())
    }

    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_GIVEUP, Intent())
        finish()
    }
}
