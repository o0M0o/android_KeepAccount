package wxm.KeepAccount.ui.data.show.note

import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Process
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.show.note.page.FrgNoteShow
import wxm.KeepAccount.ui.help.ACHelp
import wxm.KeepAccount.ui.search.ACNoteSearch
import wxm.androidutil.improve.let1

/**
 * for Note show
 * Created by WangXM on2016/12/1.
 */
class ACNoteShow : ACBase<FrgNoteShow>() {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mu_show, menu)

        menu.findItem(R.id.mi_search).let1 {
            it.icon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_search -> {
                ACNoteSearch.start(this)
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }
}
