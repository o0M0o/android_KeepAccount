package wxm.KeepAccount.ui.setting

import android.os.Bundle
import android.widget.TextView

import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.androidutil.app.AppBase

/**
 * check version
 * Created by WangXM on 2016/10/10.
 */
class TFSettingCheckVersion : TFSettingBase() {
    private val mTVVerNumber: TextView by bindView(R.id.tv_ver_number)
    private val mTVVerName: TextView by bindView(R.id.tv_ver_name)

    override fun getLayoutID(): Int {
        return R.layout.pg_setting_version
    }

    override fun isUseEventBus(): Boolean {
        return false
    }

    override fun initUI(bundle: Bundle?) {
        mTVVerNumber.text = context!!.getString(R.string.version_code, AppBase.getVerCode())
        mTVVerName.text = context!!.getString(R.string.version_name, AppBase.getVerName())
    }

    override fun updateSetting() {
        isSettingDirty = false
    }
}
