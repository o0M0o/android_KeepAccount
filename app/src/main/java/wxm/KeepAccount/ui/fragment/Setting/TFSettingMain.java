package wxm.KeepAccount.ui.fragment.Setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACSetting;

/**
 * 设置主页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingMain extends TFSettingBase {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_setting_main, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            RelativeLayout rl = UtilFun.cast(view.findViewById(R.id.rl_check_version));
            assert null != rl;
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "check version", Toast.LENGTH_SHORT).show();
                    toPageByIdx(ACSetting.PAGE_IDX_CHECK_VERSION);
                }
            });

            rl = UtilFun.cast(view.findViewById(R.id.rl_chart_color));
            assert null != rl;
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(), "chart color", Toast.LENGTH_SHORT).show();
                    toPageByIdx(ACSetting.PAGE_IDX_CHART_COLOR);
                }
            });
        }
    }

    @Override
    public void updateSetting() {
        if(mBSettingDirty)  {
            mBSettingDirty = false;
        }
    }
}
