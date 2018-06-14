package wxm.KeepAccount.ui.welcome


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.activity.ACSwitcherActivity


/**
 * first page after login
 */
class ACWelcome : ACSwitcherActivity<FrgWelcome>()  {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgWelcome())
        findViewById<Toolbar>(R.id.toolbar).let1 {
            it.setTitleTextAppearance(this, R.style.AppTheme_ActionBar_Text)
        }
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
