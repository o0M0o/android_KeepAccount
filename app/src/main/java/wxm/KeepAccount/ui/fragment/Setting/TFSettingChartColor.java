package wxm.KeepAccount.ui.fragment.Setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.BuildConfig;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;

import cn.wxm.andriodutillib.Dialog.DlgOKOrNOBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.PreferencesUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.dialog.DlgSelectColor;

/**
 * 图表颜色设置页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingChartColor extends TFSettingBase
            implements View.OnClickListener {
    private HashMap<String, Integer>    mHMColors;

    private ImageView   mIVPay;
    private ImageView   mIVIncome;

    private ImageView   mIVBudgetBalance;
    private ImageView   mIVBudgetUsed;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_setting_chart_color, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mHMColors = PreferencesUtil.loadChartColor();

            mIVPay = UtilFun.cast(view.findViewById(R.id.iv_pay));
            mIVIncome = UtilFun.cast(view.findViewById(R.id.iv_income));
            mIVBudgetBalance = UtilFun.cast(view.findViewById(R.id.iv_budget_balance));
            mIVBudgetUsed = UtilFun.cast(view.findViewById(R.id.iv_budget_used));
            if(BuildConfig.DEBUG &&
                    (null == mIVPay || null == mIVIncome || null == mIVBudgetUsed
                            || null == mIVBudgetBalance))  {
                throw new AssertionError("获取控件失败");
            }

            mIVPay.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_PAY_COLOR));
            mIVIncome.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_INCOME_COLOR));
            mIVBudgetBalance.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_BUDGET_BALANCE_COLOR));
            mIVBudgetUsed.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_BUDGET_UESED_COLOR));

            mIVPay.setOnClickListener(this);
            mIVIncome.setOnClickListener(this);
            mIVBudgetBalance.setOnClickListener(this);
            mIVBudgetUsed.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        final int vid = v.getId();
        switch (vid)    {
            case R.id.iv_budget_balance :
            case R.id.iv_budget_used :
            case R.id.iv_pay :
            case R.id.iv_income :  {
                DlgSelectColor dsc = new DlgSelectColor();
                dsc.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
                    @Override
                    public void onDialogPositiveResult(DialogFragment dialog) {
                        DlgSelectColor ds = UtilFun.cast(dialog);
                        int sel_col = ds.getSelectedColor();

                        mBSettingDirty = true;
                        switch (vid)    {
                            case R.id.iv_pay :
                                mIVPay.setBackgroundColor(sel_col);
                                mHMColors.put(PreferencesUtil.SET_PAY_COLOR, sel_col);
                                break;

                            case R.id.iv_income :
                                mIVIncome.setBackgroundColor(sel_col);
                                mHMColors.put(PreferencesUtil.SET_INCOME_COLOR, sel_col);
                                break;

                            case R.id.iv_budget_balance :
                                mIVBudgetBalance.setBackgroundColor(sel_col);
                                mHMColors.put(PreferencesUtil.SET_BUDGET_BALANCE_COLOR, sel_col);
                                break;

                            case R.id.iv_budget_used :
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
            break;
        }
    }

    @Override
    public void updateSetting() {
        if(mBSettingDirty)  {
            PreferencesUtil.saveChartColor(mHMColors);
            mBSettingDirty = false;
        }
    }
}
