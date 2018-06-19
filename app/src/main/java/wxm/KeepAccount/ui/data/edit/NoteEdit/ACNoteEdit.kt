package wxm.KeepAccount.ui.data.edit.NoteEdit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.edit.base.FrgEditBase
import wxm.KeepAccount.utility.AppUtil

/**
 * preview/edit UI for note
 */
class ACNoteEdit : ACBase<FrgEditBase>() {
    private lateinit var mMISwitch: MenuItem
    private lateinit var mMISave: MenuItem

    override fun leaveActivity() {
        setResult(GlobalDef.INTRET_CANCEL, Intent())
        finish()
    }

    override fun setupFragment(bundle: Bundle?) {
        val it = intent
        val type = it.getStringExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE)
        val id = it.getIntExtra(GlobalDef.INTENT_LOAD_RECORD_ID, -1)
        val frg = FrgNoteEdit()
        when (type) {
            GlobalDef.STR_RECORD_PAY -> {
                frg.setCurData(type, AppUtil.payIncomeUtility.payDBUtility.getData(id))
                addFragment(frg)
            }

            GlobalDef.STR_RECORD_INCOME -> {
                frg.setCurData(type, AppUtil.payIncomeUtility.incomeDBUtility.getData(id))
                addFragment(frg)
            }

            GlobalDef.STR_RECORD_BUDGET -> {
                frg.setCurData(type, AppUtil.budgetUtility.getData(id))
                addFragment(frg)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_preview_edit, menu)

        mMISwitch = menu.findItem(R.id.mi_switch)
        mMISave = menu.findItem(R.id.mi_save)
        mMISwitch.title = if (hotFragment.isEditStatus()) TITLE_EDIT else TITLE_PREVIEW
        mMISave.isVisible = hotFragment.isEditStatus()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_switch -> {
                mMISave.isVisible = hotFragment.isPreviewStatus()
                mMISwitch.title = if (hotFragment.isEditStatus()) TITLE_EDIT else TITLE_PREVIEW
                if (hotFragment.isPreviewStatus())
                    hotFragment.toEditStatus()
                else
                    hotFragment.toPreviewStatus()
            }

            R.id.mi_save -> {
                if (hotFragment.onAccept()) {
                    val data = Intent()
                    setResult(GlobalDef.INTRET_SURE, data)
                    finish()
                }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    companion object {
        private const val TITLE_PREVIEW = "预览"
        private const val TITLE_EDIT = "编辑"
    }
}
