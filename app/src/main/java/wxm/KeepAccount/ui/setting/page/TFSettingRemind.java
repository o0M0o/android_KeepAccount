package wxm.KeepAccount.ui.setting.page;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * remind setting page
 * Created by WangXM on 2016/10/10.
 */
public class TFSettingRemind extends TFSettingBase {
    @Override
    public void updateSetting() {
        if (mBSettingDirty) {
            mBSettingDirty = false;
        }
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }
}
