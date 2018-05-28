package wxm.KeepAccount.ui.welcome.page

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RelativeLayout
import com.allure.lbanners.LMBanners
import com.allure.lbanners.transformer.TransitionEffect
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.dialog.DlgSelectChannel
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.ui.setting.page.TFSettingBase
import wxm.KeepAccount.ui.setting.page.TFSettingChartColor
import wxm.KeepAccount.ui.setting.page.TFSettingCheckVersion
import wxm.KeepAccount.ui.setting.page.TFSettingMain
import wxm.KeepAccount.ui.welcome.banner.FrgAdapter
import wxm.KeepAccount.ui.welcome.banner.FrgPara
import wxm.KeepAccount.utility.*
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.dragGrid.DragGridView
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.util.UtilFun
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class PageSetting : FrgSupportSwitcher<TFSettingBase>() {
    private val mTFMain = TFSettingMain()
    private val mTFChartColor = TFSettingChartColor()
    private val mTFCheckVer = TFSettingCheckVersion()

    private val mRLRemind: RelativeLayout by bindView(R.id.rl_remind)
    private val mRLShareApp: RelativeLayout by bindView(R.id.rl_share_app)

    init {
        setupFrgID(R.layout.vw_empty_page, R.id.fl_page)
    }

    override fun setupFragment(savedInstanceState: Bundle?) {
        addChildFrg(mTFMain)
        addChildFrg(mTFChartColor)
        addChildFrg(mTFCheckVer)
    }

    override fun getLayoutID(): Int = R.layout.page_setting_main

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mRLRemind.visibility = View.GONE
            mRLShareApp.visibility = View.GONE

            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.rl_check_version, R.id.rl_chart_color, R.id.rl_reformat_data),
                    View.OnClickListener { v ->
                        when (v.id) {
                            R.id.rl_check_version -> {
                                switchToPage(mTFCheckVer)
                            }

                            R.id.rl_chart_color -> {
                                switchToPage(mTFChartColor)
                            }

                            R.id.rl_reformat_data -> {
                                DlgAlert.showAlert(context, R.string.dlg_prompt, R.string.dlg_clear_all_data,
                                        { b ->
                                            b.setPositiveButton("是") { _, _ ->
                                                AlertDialog.Builder(this.activity).setTitle("提示")
                                                        .setMessage("请等待数据清理完毕...").create().let {
                                                            it.show()
                                                            ToolUtil.runInBackground(this.activity,
                                                                    { ContextUtil.clearDB() },
                                                                    { it.dismiss() })
                                                        }
                                            }
                                            b.setNegativeButton("否") { _, _ -> }
                                        })
                            }
                        }
                    })
        }
    }
}
