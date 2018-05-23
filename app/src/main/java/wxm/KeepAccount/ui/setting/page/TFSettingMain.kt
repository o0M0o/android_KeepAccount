package wxm.KeepAccount.ui.setting.page

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RelativeLayout
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.setting.ACSetting
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.dialog.DlgAlert
import wxm.androidutil.ui.view.EventHelper

/**
 * main page for setting
 * Created by WangXM on 2016/10/10.
 */
class TFSettingMain : TFSettingBase() {
    private val mRLRemind: RelativeLayout by bindView(R.id.rl_remind)
    private val mRLShareApp: RelativeLayout by bindView(R.id.rl_share_app)

    override fun getLayoutID(): Int {
        return R.layout.page_setting_main
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mRLRemind.visibility = View.GONE
            mRLShareApp.visibility = View.GONE

            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.rl_check_version, R.id.rl_chart_color, R.id.rl_reformat_data),
                    View.OnClickListener { v ->
                        when (v.id) {
                            R.id.rl_check_version -> {
                                (activity as ACSetting)
                                        .switchToPageByType(TFSettingCheckVersion::class.java.name)
                            }

                            R.id.rl_chart_color -> {
                                (activity as ACSetting)
                                        .switchToPageByType(TFSettingChartColor::class.java.name)
                            }

                            R.id.rl_reformat_data -> {
                                DlgAlert.showAlert(context, "清除所有数据!", "此操作不能恢复，是否继续操作!",
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

    override fun updateSetting() {
        if (isSettingDirty) {
            isSettingDirty = false
        }
    }
}
