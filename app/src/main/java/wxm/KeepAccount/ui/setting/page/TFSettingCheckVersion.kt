package wxm.KeepAccount.ui.setting.page

import android.os.Bundle
import android.widget.TextView

import java.util.Locale

import kotterknife.bindView
import wxm.androidutil.util.PackageUtil
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef

/**
 * check version
 * Created by WangXM on 2016/10/10.
 */
class TFSettingCheckVersion : TFSettingBase() {
    private val mTVVerNumber: TextView by bindView(R.id.tv_ver_number)
    private val mTVVerName: TextView by bindView(R.id.tv_ver_name)

    override fun getLayoutID(): Int {
        return R.layout.page_setting_version
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        var s = String.format(Locale.CHINA, "当前版本号 : %d",
                PackageUtil.getVerCode(context, GlobalDef.PACKAGE_NAME))
        mTVVerNumber.text = s

        s = String.format(Locale.CHINA, "当前版本名 : %s",
                PackageUtil.getVerName(context, GlobalDef.PACKAGE_NAME))
        mTVVerName.text = s
    }

    override fun updateSetting() {
        if (isSettingDirty) {
            isSettingDirty = false
        }
    }
}