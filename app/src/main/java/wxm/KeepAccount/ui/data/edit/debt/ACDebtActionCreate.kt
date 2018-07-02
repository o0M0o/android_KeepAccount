package wxm.KeepAccount.ui.data.edit.debt

import android.support.v4.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.DebtActionItem
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.base.ACBase.ACResultUtil
import wxm.KeepAccount.ui.data.edit.debt.page.PgDebtActionEdit
import wxm.KeepAccount.ui.data.edit.debt.page.PgDebtEdit

/**
 * income/pay record UI
 */
class ACDebtActionCreate : ACBase<PgDebtActionEdit>() {
    override fun setupFragment(): MutableList<PgDebtActionEdit> {
        return arrayListOf(PgDebtActionEdit().apply {
            setEditData(DebtActionItem())
        })
    }


    override fun leaveActivity() {
        ACResultUtil.doFinish(this, ACResultUtil.RET_CANCEL)
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
                    ACResultUtil.doFinish(this, ACResultUtil.RET_OK, hf.getEditData() as Parcelable)
                }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val DEBT_ACTION_CREATE_REQUEST = 1

        fun start(ct: Context, frg:Fragment) {
            frg.startActivityForResult(Intent(ct, ACDebtActionCreate::class.java),
                    DEBT_ACTION_CREATE_REQUEST)
        }
    }

}
