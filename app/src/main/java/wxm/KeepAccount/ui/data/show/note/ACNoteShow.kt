package wxm.KeepAccount.ui.data.show.note

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.androidutil.ui.activity.ACSwitcherActivity

/**
 * for Note show
 * Created by WangXM on2016/12/1.
 */
class ACNoteShow : ACBase<FrgNoteShow>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgNoteShow())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    /**
     * jump to page with name
     * @param tn   tab name
     */
    fun jumpByTabName(tn: String) {
        hotFragment.jumpByTabName(tn)
    }
}
