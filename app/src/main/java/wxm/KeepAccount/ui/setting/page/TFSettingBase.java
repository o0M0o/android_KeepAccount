package wxm.KeepAccount.ui.setting.page;


import android.content.Context;
import android.support.v4.app.Fragment;

import wxm.KeepAccount.ui.setting.ACSetting;
import wxm.KeepAccount.ui.setting.FrgSetting;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.util.UtilFun;

/**
 * for base setting
 * Created by 123 on 2016/10/10.
 */
public abstract class TFSettingBase extends Fragment {
    protected boolean mBSettingDirty = false;
    protected FrgSetting    mFrgHolder = null;

    public void setFrgHolder(FrgSetting frgHolder)  {
        mFrgHolder = frgHolder;
    }

    /**
     *  switch to new page
     * @param idx   for new page
     */
    public void toPageByIdx(int idx) {
        if(null != mFrgHolder)
            mFrgHolder.change_page(idx);
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
