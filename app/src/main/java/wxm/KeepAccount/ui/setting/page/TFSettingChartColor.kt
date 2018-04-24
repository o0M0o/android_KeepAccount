package wxm.KeepAccount.ui.setting.page


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.ImageView

import java.util.HashMap

import butterknife.BindView
import butterknife.OnClick
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.dialog.DlgSelectColor
import wxm.KeepAccount.utility.PreferencesUtil

/**
 * setting for chart color
 * Created by WangXM on 2016/10/10.
 */
class TFSettingChartColor : TFSettingBase() {
    @BindView(R.id.iv_pay)
    internal var mIVPay: ImageView? = null
    @BindView(R.id.iv_income)
    internal var mIVIncome: ImageView? = null
    @BindView(R.id.iv_budget_balance)
    internal var mIVBudgetBalance: ImageView? = null
    @BindView(R.id.iv_budget_used)
    internal var mIVBudgetUsed: ImageView? = null
    private var mHMColors: HashMap<String, Int> = PreferencesUtil.loadChartColor()

    override fun getLayoutID(): Int {
        return R.layout.page_setting_chart_color
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        if (null == bundle) {
            mHMColors[PreferencesUtil.SET_PAY_COLOR]?.let { mIVPay!!.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_INCOME_COLOR]?.let { mIVIncome!!.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_BUDGET_BALANCE_COLOR]?.let { mIVBudgetBalance!!.setBackgroundColor(it) }
            mHMColors[PreferencesUtil.SET_BUDGET_UESED_COLOR]?.let { mIVBudgetUsed!!.setBackgroundColor(it) }
        }
    }

    @OnClick(R.id.iv_pay, R.id.iv_income, R.id.iv_budget_balance, R.id.iv_budget_used)
    fun onIVClick(v: View) {
        val vid = v.id
        val dsc = DlgSelectColor()
        dsc.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
            override fun onDialogPositiveResult(dialog: DialogFragment) {
                val ds = UtilFun.cast<DlgSelectColor>(dialog)
                val selCol = ds.selectedColor

                isSettingDirty = true
                when (vid) {
                    R.id.iv_pay -> {
                        mIVPay!!.setBackgroundColor(selCol)
                        mHMColors[PreferencesUtil.SET_PAY_COLOR] = selCol
                    }

                    R.id.iv_income -> {
                        mIVIncome!!.setBackgroundColor(selCol)
                        mHMColors[PreferencesUtil.SET_INCOME_COLOR] = selCol
                    }

                    R.id.iv_budget_balance -> {
                        mIVBudgetBalance!!.setBackgroundColor(selCol)
                        mHMColors[PreferencesUtil.SET_BUDGET_BALANCE_COLOR] = selCol
                    }

                    R.id.iv_budget_used -> {
                        mIVBudgetUsed!!.setBackgroundColor(selCol)
                        mHMColors[PreferencesUtil.SET_BUDGET_UESED_COLOR] = selCol
                    }
                }
            }

            override fun onDialogNegativeResult(dialog: DialogFragment) {}
        })
        dsc.show(fragmentManager, "选择颜色")
    }

    override fun updateSetting() {
        if (isSettingDirty) {
            PreferencesUtil.saveChartColor(mHMColors)
            isSettingDirty = false
        }
    }
}
