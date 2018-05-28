package wxm.KeepAccount.ui.setting


/**
 * remind setting page
 * Created by WangXM on 2016/10/10.
 */
class TFSettingRemind : TFSettingBase() {
    override fun updateSetting() {
        if (isSettingDirty) {
            isSettingDirty = false
        }
    }

    override fun getLayoutID(): Int {
        return 0
    }

    override fun isUseEventBus(): Boolean {
        return false
    }
}
