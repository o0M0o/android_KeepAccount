package wxm.KeepAccount.ui.data.show.note

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.frgUtil.FrgSupportSwitcher
import wxm.androidutil.switcher.ACSwitcherActivity

/**
 * for Note show
 * Created by WangXM on2016/12/1.
 */
class ACNoteShow : ACSwitcherActivity<FrgNoteShow>() {
    override fun leaveActivity() {
        val ret_data = GlobalDef.INTRET_USR_LOGOUT

        val data = Intent()
        setResult(ret_data, data)
        finish()
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgNoteShow())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_note_show, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_switch -> {
                val hot = hotFragment.hotTabItem
                hot?.switchPage()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
        hotFragment.reInitUI()
    }

    /**
     * open/close page touch
     * @param bflag     true open page touch
     */
    fun disableViewPageTouch(bflag: Boolean) {
        hotFragment.disableViewPageTouch(bflag)
    }

    /**
     * jump to page with name
     * @param tabname   tab name
     */
    fun jumpByTabName(tabname: String) {
        hotFragment.jumpByTabName(tabname)
    }
}
