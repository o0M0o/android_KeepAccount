package wxm.KeepAccount.ui.setting.page;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.DlgSelectColor;
import wxm.KeepAccount.utility.PreferencesUtil;

/**
 * setting for chart color
 * Created by WangXM on 2016/10/10.
 */
public class TFSettingChartColor extends TFSettingBase {
    @BindView(R.id.iv_pay)
    ImageView mIVPay;
    @BindView(R.id.iv_income)
    ImageView mIVIncome;
    @BindView(R.id.iv_budget_balance)
    ImageView mIVBudgetBalance;
    @BindView(R.id.iv_budget_used)
    ImageView mIVBudgetUsed;
    private HashMap<String, Integer> mHMColors;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.page_setting_chart_color, viewGroup, false);
    }

    @Override
    protected void initUI(Bundle bundle)    {
        if(null == bundle) {
            mHMColors = PreferencesUtil.loadChartColor();

            mIVPay.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_PAY_COLOR));
            mIVIncome.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_INCOME_COLOR));
            mIVBudgetBalance.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_BUDGET_BALANCE_COLOR));
            mIVBudgetUsed.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_BUDGET_UESED_COLOR));
        }
    }

    @OnClick({R.id.iv_pay, R.id.iv_income, R.id.iv_budget_balance, R.id.iv_budget_used})
    public void onIVClick(View v) {
        final int vid = v.getId();
        DlgSelectColor dsc = new DlgSelectColor();
        dsc.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
            @Override
            public void onDialogPositiveResult(DialogFragment dialog) {
                DlgSelectColor ds = UtilFun.cast(dialog);
                int sel_col = ds.getSelectedColor();

                mBSettingDirty = true;
                switch (vid) {
                    case R.id.iv_pay:
                        mIVPay.setBackgroundColor(sel_col);
                        mHMColors.put(PreferencesUtil.SET_PAY_COLOR, sel_col);
                        break;

                    case R.id.iv_income:
                        mIVIncome.setBackgroundColor(sel_col);
                        mHMColors.put(PreferencesUtil.SET_INCOME_COLOR, sel_col);
                        break;

                    case R.id.iv_budget_balance:
                        mIVBudgetBalance.setBackgroundColor(sel_col);
                        mHMColors.put(PreferencesUtil.SET_BUDGET_BALANCE_COLOR, sel_col);
                        break;

                    case R.id.iv_budget_used:
                        mIVBudgetUsed.setBackgroundColor(sel_col);
                        mHMColors.put(PreferencesUtil.SET_BUDGET_UESED_COLOR, sel_col);
                        break;
                }
            }

            @Override
            public void onDialogNegativeResult(DialogFragment dialog) {
            }
        });
        dsc.show(getFragmentManager(), "选择颜色");
    }

    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            PreferencesUtil.saveChartColor(mHMColors);
            mBSettingDirty = false;
        }
    }
}
