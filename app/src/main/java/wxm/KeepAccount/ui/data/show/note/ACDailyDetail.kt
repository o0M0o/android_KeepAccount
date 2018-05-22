package wxm.KeepAccount.ui.data.show.note

import android.content.Intent
import android.os.Bundle

import wxm.KeepAccount.define.GlobalDef
import wxm.androidutil.log.TagLog
import wxm.androidutil.switcher.ACSwitcherActivity
import wxm.androidutil.util.UtilFun

/**
 * day detail UI
 */
class ACDailyDetail : ACSwitcherActivity<FrgDailyDetail>() {

    override fun leaveActivity() {
        val ret_data = GlobalDef.INTRET_GIVEUP

        val data = Intent()
        setResult(ret_data, data)
        finish()
    }

    override fun setupFragment(bundle: Bundle?) {
        val it = intent!!
        val hot_day = it.getStringExtra(K_HOTDAY)
        if (UtilFun.StringIsNullOrEmpty(hot_day)) {
            TagLog.e("调用intent缺少'K_HOTDAY'参数", null)
            return
        }

        // for holder
        val fg = FrgDailyDetail()
        val bd = Bundle()
        bd.putString(K_HOTDAY, hot_day)
        fg.arguments = bd

        addFragment(fg)
    }

    companion object {
        val K_HOTDAY = "hotday"
    }
}
