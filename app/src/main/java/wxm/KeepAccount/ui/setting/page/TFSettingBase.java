package wxm.KeepAccount.ui.setting.page;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for base setting
 * Created by WangXM on 2016/10/10.
 */
public abstract class TFSettingBase extends FrgUtilitySupportBase {
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
