package wxm.KeepAccount.ui.usr

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * add usr
 */
class ACAddUsr : ACBase<FrgUsrAdd>() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
        finish()
    }
}
