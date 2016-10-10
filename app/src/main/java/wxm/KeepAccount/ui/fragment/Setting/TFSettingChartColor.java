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
public class TFSettingChartColor extends TFSettingBase {
    private boolean                     mColorsDirty = false;
    private HashMap<String, Integer>    mHMColors;

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

            final ImageView iv_pay = UtilFun.cast(view.findViewById(R.id.iv_pay));
            final ImageView iv_income = UtilFun.cast(view.findViewById(R.id.iv_income));
            assert null != iv_pay && null != iv_income;

            iv_pay.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_PAY_COLOR));
            iv_income.setBackgroundColor(mHMColors.get(PreferencesUtil.SET_INCOME_COLOR));

            iv_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DlgSelectColor dsc = new DlgSelectColor();
                    dsc.setDialogListener(new DlgSelectColor.NoticeDialogListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            DlgSelectColor ds = UtilFun.cast(dialog);
                            iv_pay.setBackgroundColor(ds.getSelectedColor());
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                        }
                    });
                    dsc.show(getFragmentManager(), "选择颜色");
                }
            });


            iv_income.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DlgSelectColor dsc = new DlgSelectColor();
                    dsc.setDialogListener(new DlgSelectColor.NoticeDialogListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            DlgSelectColor ds = UtilFun.cast(dialog);
                            iv_income.setBackgroundColor(ds.getSelectedColor());
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                        }
                    });
                    dsc.show(getFragmentManager(), "选择颜色");
                }
            });
        }
    }
}
