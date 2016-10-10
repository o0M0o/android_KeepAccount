package wxm.KeepAccount.ui.fragment.Setting;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.R;

/**
 * 图表颜色设置页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingChartColor extends TFSettingBase {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vw_setting_chart_color, container, false);
        return v;
    }

}
