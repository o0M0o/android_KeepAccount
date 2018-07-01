package wxm.KeepAccount.ui.data.edit.RecordInfo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase

/**
 * for record info
 */
class ACRecordInfoEdit : ACBase<FrgRecordInfoEdit>() {

    override fun leaveActivity() {
        cancelEdit()
    }

    override fun setupFragment(): MutableList<FrgRecordInfoEdit> {
        return FrgRecordInfoEdit().apply {
            mEditType = intent.getStringExtra(IT_PARA_RECORD_TYPE)
        }.let {
            arrayListOf(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mu_preview_edit, menu)

        menu.findItem(R.id.mi_switch).isVisible = false
        menu.findItem(R.id.mi_cancel).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                acceptEdit()
            }

            R.id.mi_cancel -> {
                cancelEdit()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun cancelEdit()    {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
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
