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
    override fun setupFragment(bundle: Bundle?) {
        val hotDay = intent!!.getStringExtra(K_HOTDAY)
        if (UtilFun.StringIsNullOrEmpty(hotDay)) {
            TagLog.e("调用intent缺少'K_HOTDAY'参数")
            return
        }

        // for holder
        FrgDailyDetail().apply {
            arguments = Bundle().apply { putString(K_HOTDAY, hotDay) }
            addFragment(this)
        }
    }

    companion object {
        const val K_HOTDAY = "hotday"
    }
}
