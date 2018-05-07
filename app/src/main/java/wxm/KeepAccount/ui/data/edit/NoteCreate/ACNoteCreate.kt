package wxm.KeepAccount.ui.data.edit.NoteCreate

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.Switcher.ACSwitcherActivity
import wxm.androidutil.util.UtilFun

/**
 * income/pay record UI
 */
class ACNoteCreate : ACSwitcherActivity<FrgNoteCreate>() {
    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_GIVEUP, Intent())
        finish()
    }

    override fun setupFragment(bundle: Bundle?) {
        val date = intent.getStringExtra(GlobalDef.STR_RECORD_DATE)!!
        FrgNoteCreate().let {
            it.arguments = Bundle().apply {
                putString(GlobalDef.STR_RECORD_DATE, date)
            }
            addFragment(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_note_add, menu)
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
