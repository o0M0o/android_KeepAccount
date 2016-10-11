package wxm.KeepAccount.ui.fragment.Setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;

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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_setting_chart_color, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            mHMColors = PreferencesUtil.loadChartColor();

            mIVPay = UtilFun.cast(view.findViewById(R.id.iv_pay));
            mIVIncome = UtilFun.cast(view.findViewById(R.id.iv_income));
            assert null != mIVPay && null != mIVIncome;

            mIVPay.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_PAY_COLOR));
            mIVIncome.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_INCOME_COLOR));

            mIVPay.setOnClickListener(this);
            mIVIncome.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        final int vid = v.getId();
        switch (vid)    {
            case R.id.iv_pay :
            case R.id.iv_income :  {
                DlgSelectColor dsc = new DlgSelectColor();
                dsc.setDialogListener(new DlgSelectColor.NoticeDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        DlgSelectColor ds = UtilFun.cast(dialog);
                        int sel_col = ds.getSelectedColor();

                        mBSettingDirty = true;
                        if(R.id.iv_pay == vid)  {
                            mIVPay.setBackgroundColor(sel_col);
                            mHMColors.put(PreferencesUtil.SET_PAY_COLOR, sel_col);
                        } else {
                            mIVIncome.setBackgroundColor(sel_col);
                            mHMColors.put(PreferencesUtil.SET_INCOME_COLOR, sel_col);
                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
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
