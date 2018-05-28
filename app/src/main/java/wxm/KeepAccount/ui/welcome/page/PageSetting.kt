package wxm.KeepAccount.ui.welcome.page

import android.os.Bundle
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.setting.*
import wxm.androidutil.ui.frg.FrgSupportSwitcher

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageSetting : FrgSupportSwitcher<TFSettingBase>() {
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

    fun leaveFrg(): Boolean {
        if(hotPage !== mTFMain)    {
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
    fun onChangePageEvent(event: ChangePageEvent) {
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
