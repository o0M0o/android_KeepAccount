package wxm.KeepAccount.ui.data.edit.debt

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.edit.debt.page.PgDebtEdit

/**
 * income/pay record UI
 */
class ACDebtCreate : ACBase<PgDebtEdit>() {
    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mu_note_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                val hf = hotFragment
                if (hf.onAccept()) {
                    setResult(GlobalDef.INTRET_SURE, Intent())
                    finish()
                }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }
}
