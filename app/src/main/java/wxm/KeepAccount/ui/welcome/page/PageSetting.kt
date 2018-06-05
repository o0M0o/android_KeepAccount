package wxm.KeepAccount.ui.welcome.page

import android.os.Bundle
import android.view.View
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.event.ChangePage
import wxm.KeepAccount.ui.setting.*
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import java.util.*

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageSetting : FrgSupportSwitcher<TFSettingBase>(), PageBase {
    private val mTFChartColor = TFSettingChartColor()
    private val mTFCheckVer = TFSettingCheckVersion()
    private val mTFMain = TFSettingMain()

    init {
        setupFrgID(R.layout.vw_empty_page, R.id.fl_page)
    }

    override fun isUseEventBus(): Boolean = true

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mTFMain)
        addChildFrg(mTFChartColor)
        addChildFrg(mTFCheckVer)
    }

    override fun leavePage(): Boolean {
        val hp = hotPage
        if(hp !== mTFMain)    {
            if(hp.isSettingDirty) {
                DlgAlert.showAlert(context!!, R.string.dlg_info, R.string.setting_changed,
                        {db ->
                            db.setPositiveButton(R.string.cn_sure, {_, _ ->
                                hp.updateSetting()
                            })
                        })
            }

            switchToPage(mTFMain)
            return false
        }

        return true
    }

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangePageEvent(event: ChangePage) {
        when(event.JavaClassName)   {
            TFSettingCheckVersion::class.java.name ->    {
                switchToPage(mTFCheckVer)
            }

            TFSettingChartColor::class.java.name ->    {
                switchToPage(mTFChartColor)
            }
        }
    }
}
