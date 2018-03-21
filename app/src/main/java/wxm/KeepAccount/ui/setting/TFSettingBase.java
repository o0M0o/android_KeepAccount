package wxm.KeepAccount.ui.setting;


import android.content.Context;
import android.support.v4.app.Fragment;

import wxm.androidutil.util.UtilFun;

/**
 * for base setting
 * Created by 123 on 2016/10/10.
 */
public abstract class TFSettingBase extends Fragment {
    protected boolean mBSettingDirty = false;

    /**
     *  get ACSetting
     * @return      ACSetting else null
     */
    public ACSetting getRootActivity() {
        Context ct = getContext();
        if (ct instanceof ACSetting) {
            return UtilFun.cast(ct);
        }

        return null;
    }

    /**
     *  switch to new page
     * @param idx   for new page
     */
    public void toPageByIdx(int idx) {
        ACSetting acs = getRootActivity();
        if (null != acs) {
            acs.change_page(idx);
        }
    }

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
