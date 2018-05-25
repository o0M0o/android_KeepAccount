package wxm.KeepAccount.ui.setting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.setting.page.TFSettingBase
import wxm.KeepAccount.ui.setting.page.TFSettingChartColor
import wxm.KeepAccount.ui.setting.page.TFSettingCheckVersion
import wxm.KeepAccount.ui.setting.page.TFSettingMain
import wxm.androidutil.ui.activity.ACSwitcherActivity
import wxm.androidutil.ui.dialog.DlgAlert

/**
 * for app setting
 */
class ACSetting : ACSwitcherActivity<TFSettingBase>() {
    private val mTFMain = TFSettingMain()
    private val mTFChartColor = TFSettingChartColor()
    private val mTFCheckVer = TFSettingCheckVersion()
    //private val mTFRemind = TFSettingRemind()

    override fun leaveActivity() {
        if (mTFMain !== hotFragment) {
            switchToFragment(mTFMain)
        } else {
            setResult(GlobalDef.INTRET_GIVEUP, Intent())
            finish()
        }
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(mTFMain)
        addFragment(mTFChartColor)
        addFragment(mTFCheckVer)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.mu_save_giveup, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_save -> {
                if (mTFMain !== hotFragment) {
                    val tb = hotFragment
                    val h = this
                    if (tb.isSettingDirty) {
                        DlgAlert.showAlert(this, "配置已经更改", "是否保存更改的配置?",
                                { b ->
                                    b.setPositiveButton("是") { _ , _->
                                        tb.updateSetting()
                                        h.switchToFragment(mTFMain)
                                    }
                                    b.setNegativeButton("否") { _, _ -> h.switchToFragment(mTFMain) }
                                })
                    } else {
                        switchToFragment(mTFMain)
                    }
                } else {
                    setResult(GlobalDef.INTRET_SURE, Intent())
                    finish()
                }
            }
            R.id.mi_giveup -> { leaveActivity() }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    fun switchToPageByType(pageType: String) {
        if (pageType == TFSettingChartColor::class.java.name) {
            switchToFragment(mTFChartColor)
        } else if (pageType == TFSettingCheckVersion::class.java.name) {
            switchToFragment(mTFCheckVer)
        }
    }
}
