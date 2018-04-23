package wxm.KeepAccount.ui.setting.page;


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
    protected int getLayoutID() {
        return 0;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }
}
