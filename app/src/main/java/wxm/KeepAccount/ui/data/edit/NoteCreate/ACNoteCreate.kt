package wxm.KeepAccount.ui.data.edit.NoteCreate

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * income/pay record UI
 */
class ACNoteCreate : ACBase<FrgNoteCreate>() {
    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
        finish()
    }

    override fun setupFragment(): MutableList<FrgNoteCreate> {
        val arg = Bundle().apply {
            putString(GlobalDef.STR_RECORD_DATE, intent.getStringExtra(GlobalDef.STR_RECORD_DATE)!!)
        }

        return arrayListOf(FrgNoteCreate()).apply {
            forEach {
                it.arguments = arg
            }
        }
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
