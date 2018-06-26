package wxm.KeepAccount.ui.data.edit.SmsToNote

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.sync.SmsItem

/**
 * preview/edit UI for note
 */
class ACSmsToNote : ACBase<FrgSmsToNote>() {
    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_preview_edit, menu)

        menu.findItem(R.id.mi_switch).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                if (hotFragment.onAccept()) {
                    val data = Intent().apply {
                        putExtra(FIELD_SMS, intent.getParcelableExtra<SmsItem>(FIELD_SMS)!!)
                        putExtra(FIELD_NOTE_TYPE, intent.getStringExtra(FIELD_NOTE_TYPE)!!)
                    }

                    setResult(GlobalDef.INTRET_SURE, data)
                    finish()
                }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    companion object {
        const val FIELD_SMS = "sms"
        const val FIELD_NOTE_TYPE = "note_type"

        const val FIELD_NT_PAY = "pay"
        const val FIELD_NT_INCOME = "income"
    }
}
