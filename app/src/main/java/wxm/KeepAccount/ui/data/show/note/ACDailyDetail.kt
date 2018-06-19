package wxm.KeepAccount.ui.data.show.note

import android.os.Bundle
import wxm.KeepAccount.ui.base.ACBase.ACBase

import wxm.androidutil.log.TagLog

/**
 * day detail UI
 */
class ACDailyDetail : ACBase<FrgDailyDetail>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        val hotDay = intent!!.getStringExtra(KEY_HOT_DAY)
        if (hotDay.isNullOrEmpty()) {
            TagLog.e("调用intent缺少'KEY_HOT_DAY'参数")
            return
        }

        // for holder
        FrgDailyDetail().apply {
            arguments = Bundle().apply { putString(KEY_HOT_DAY, hotDay) }
            addFragment(this)
        }
    }

    companion object {
        const val KEY_HOT_DAY = "hotday"
    }
}
