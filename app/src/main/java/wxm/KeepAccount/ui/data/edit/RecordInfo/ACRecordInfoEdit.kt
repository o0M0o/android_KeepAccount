package wxm.KeepAccount.ui.data.edit.RecordInfo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.Switcher.ACSwitcherActivity

/**
 * for record info
 */
class ACRecordInfoEdit : ACSwitcherActivity<FrgRecordInfoEdit>() {

    override fun leaveActivity() {
        giveUpEdit()
    }

    override fun setupFragment(bundle: Bundle?) {
        val it = intent
        val tf = FrgRecordInfoEdit()
        tf.mEditType = it.getStringExtra(IT_PARA_RECORD_TYPE)

        addFragment(tf)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mu_preview_edit, menu)

        menu.findItem(R.id.mi_switch).isVisible = false
        menu.findItem(R.id.mi_giveup).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                acceptEdit()
            }

            R.id.mi_giveup -> {
                giveUpEdit()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun giveUpEdit()    {
        setResult(GlobalDef.INTRET_GIVEUP, Intent())
        finish()
    }

    private fun acceptEdit()    {
        setResult(GlobalDef.INTRET_SURE, Intent())
        finish()
    }

    companion object {
        const val IT_PARA_RECORD_TYPE = "record_type"
    }
}
