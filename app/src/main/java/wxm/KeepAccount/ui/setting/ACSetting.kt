package wxm.KeepAccount.ui.setting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem

import wxm.KeepAccount.ui.setting.page.TFSettingBase
import wxm.KeepAccount.ui.setting.page.TFSettingChartColor
import wxm.KeepAccount.ui.setting.page.TFSettingCheckVersion
import wxm.KeepAccount.ui.setting.page.TFSettingMain
import wxm.KeepAccount.ui.setting.page.TFSettingRemind
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.Switcher.ACSwitcherActivity

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
            val ret_data = GlobalDef.INTRET_GIVEUP
            val data = Intent()
            setResult(ret_data, data)
            finish()
        }
    }

    override fun setupFragment(bundle: Bundle?) {
        addFragment(mTFMain)
        addFragment(mTFChartColor)
        addFragment(mTFCheckVer)
        //addFragment(mTFRemind);
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
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("配置已经更改")
                        builder.setMessage("是否保存更改的配置?")
                        builder.setPositiveButton("是") { _: DialogInterface, _: Int ->
                            tb.updateSetting()
                            h.switchToFragment(mTFMain)
                        }
                        builder.setNegativeButton("否") { _, _ -> h.switchToFragment(mTFMain) }
                        val alertDialog = builder.create()
                        alertDialog.show()
                    } else {
                        switchToFragment(mTFMain)
                    }
                } else {
                    val ret_data = GlobalDef.INTRET_SURE
                    val data = Intent()
                    setResult(ret_data, data)
                    finish()
                }
            }

            R.id.mi_giveup -> {
                if (mTFMain !== hotFragment) {
                    switchToFragment(mTFMain)
                } else {
                    val data = Intent()
                    setResult(GlobalDef.INTRET_GIVEUP, data)
                    finish()
                }
            }

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
