package wxm.KeepAccount.ui.fragment.Setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.R;

/**
 * 检查版本设置页面
 * Created by 123 on 2016/10/10.
 */
public class TFSettingCheckVersion extends TFSettingBase {
    @BindView(R.id.tv_ver_number)
    TextView    mTVVerNumber;

    @BindView(R.id.tv_ver_name)
    TextView    mTVVerName;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.page_setting_version, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != view) {
            String s = String.format(Locale.CHINA, "当前版本号 : %d",
                            ToolUtil.getVerCode(getContext()));
            mTVVerNumber.setText(s);

            s = String.format(Locale.CHINA, "当前版本名 : %s",
                            ToolUtil.getVerName(getContext()));
            mTVVerName.setText(s);
        }
    }

    @Override
    public void updateSetting() {
        if(mBSettingDirty)  {
            mBSettingDirty = false;
        }
    }
}
