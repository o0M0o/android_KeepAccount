package wxm.KeepAccount.ui.setting


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.ImageView

import java.util.HashMap

import kotterknife.bindView
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.dialog.DlgSelectColor
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.androidutil.ui.view.EventHelper

/**
 * setting for chart color
 * Created by WangXM on 2016/10/10.
 */
class TFSettingChartColor : TFSettingBase() {
    private val mIVPay: ImageView by bindView(R.id.iv_pay)
    private val mIVIncome: ImageView by bindView(R.id.iv_income)
    private val mIVBudgetBalance: ImageView by bindView(R.id.iv_budget_balance)
    private val mIVBudgetUsed: ImageView by bindView(R.id.iv_budget_used)

    private var mHMColors: HashMap<String, Int> = PreferencesUtil.loadChartColor()

    override fun getLayoutID(): Int {
        return R.layout.page_setting_chart_color
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mHMColors[PreferencesUtil.SET_PAY_COLOR]?.let { mIVPay.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_INCOME_COLOR]?.let { mIVIncome.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_BUDGET_BALANCE_COLOR]?.let { mIVBudgetBalance.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_BUDGET_UESED_COLOR]?.let { mIVBudgetUsed.setBackgroundColor(it) }

            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.iv_pay, R.id.iv_income, R.id.iv_budget_balance, R.id.iv_budget_used),
                    View.OnClickListener { v ->
                        run {
                            val vid = v.id
                            val dsc = DlgSelectColor()
                            dsc.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                                override fun onDialogPositiveResult(dialog: DialogFragment) {
                                    val ds = UtilFun.cast<DlgSelectColor>(dialog)
                                    val selCol = ds.selectedColor

                                    isSettingDirty = true
                                    when (vid) {
                                        R.id.iv_pay -> {
                                            mIVPay.setBackgroundColor(selCol)
                                            mHMColors[PreferencesUtil.SET_PAY_COLOR] = selCol
                                        }

                                        R.id.iv_income -> {
                                            mIVIncome.setBackgroundColor(selCol)
                                            mHMColors[PreferencesUtil.SET_INCOME_COLOR] = selCol
                                        }

                                        R.id.iv_budget_balance -> {
                                            mIVBudgetBalance.setBackgroundColor(selCol)
                                            mHMColors[PreferencesUtil.SET_BUDGET_BALANCE_COLOR] = selCol
                                        }

                                        R.id.iv_budget_used -> {
                                            mIVBudgetUsed.setBackgroundColor(selCol)
                                            mHMColors[PreferencesUtil.SET_BUDGET_UESED_COLOR] = selCol
                                        }
                                    }
                                }

                                override fun onDialogNegativeResult(dialog: DialogFragment) {}
                            })
                            dsc.show(fragmentManager, "选择颜色")
                        }
                    })
        }
    }

    override fun updateSetting() {
        if (isSettingDirty) {
            PreferencesUtil.saveChartColor(mHMColors)
            isSettingDirty = false
        }
    }
}
