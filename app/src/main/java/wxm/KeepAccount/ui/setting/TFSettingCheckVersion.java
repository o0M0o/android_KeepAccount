package wxm.KeepAccount.ui.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.util.PackageUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;

/**
 * check version
 * Created by 123 on 2016/10/10.
 */
public class TFSettingCheckVersion extends TFSettingBase {
    @BindView(R.id.tv_ver_number)
    TextView mTVVerNumber;

    @BindView(R.id.tv_ver_name)
    TextView mTVVerName;

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
                    PackageUtil.getVerCode(getContext(), GlobalDef.PACKAGE_NAME));
            mTVVerNumber.setText(s);

            s = String.format(Locale.CHINA, "当前版本名 : %s",
                    PackageUtil.getVerName(getContext(), GlobalDef.PACKAGE_NAME));
            mTVVerName.setText(s);
        }
    }

    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            mBSettingDirty = false;
        }
    }
}
