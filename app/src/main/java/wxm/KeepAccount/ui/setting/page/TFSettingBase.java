package wxm.KeepAccount.ui.setting.page;

import android.support.v4.app.Fragment;

/**
 * for base setting
 * Created by 123 on 2016/10/10.
 */
public abstract class TFSettingBase extends Fragment {
    protected boolean mBSettingDirty = false;

    /**
     * check whether setting is changed
     * @return      if setting changed return true else false
     */
    public boolean isSettingDirty() {
        return mBSettingDirty;
    }

    /**
     * save setting
     */
    public abstract void updateSetting();
}
