package wxm.KeepAccount.ui.setting.page;


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
 * Created by WangXM on 2016/10/10.
 */
public class TFSettingCheckVersion extends TFSettingBase {
    @BindView(R.id.tv_ver_number)
    TextView mTVVerNumber;

    @BindView(R.id.tv_ver_name)
    TextView mTVVerName;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.page_setting_version, viewGroup, false);
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
