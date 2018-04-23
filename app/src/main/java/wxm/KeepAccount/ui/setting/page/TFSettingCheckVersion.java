package wxm.KeepAccount.ui.setting.page;


import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import wxm.androidutil.util.PackageUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.GlobalDef;

/**
 * check version
 * Created by WangXM on 2016/10/10.
 */
public class TFSettingCheckVersion extends TFSettingBase {
    @BindView(R.id.tv_ver_number)
    TextView mTVVerNumber;

    @BindView(R.id.tv_ver_name)
    TextView mTVVerName;

    @Override
    protected int getLayoutID() {
        return R.layout.page_setting_version;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle)    {
        String s = String.format(Locale.CHINA, "当前版本号 : %d",
                PackageUtil.getVerCode(getContext(), GlobalDef.PACKAGE_NAME));
        mTVVerNumber.setText(s);

        s = String.format(Locale.CHINA, "当前版本名 : %s",
                PackageUtil.getVerName(getContext(), GlobalDef.PACKAGE_NAME));
        mTVVerName.setText(s);
    }

    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            mBSettingDirty = false;
        }
    }
}
