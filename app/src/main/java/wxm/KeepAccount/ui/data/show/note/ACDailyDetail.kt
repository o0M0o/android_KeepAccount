package wxm.KeepAccount.ui.data.show.note

import android.os.Bundle
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.show.note.page.FrgDailyDetail

import wxm.androidutil.log.TagLog

/**
 * day detail UI
 */
class ACDailyDetail : ACBase<FrgDailyDetail>() {
    override fun setupFragment(): MutableList<FrgDailyDetail> {
        val hotDay = intent!!.getStringExtra(KEY_HOT_DAY)
        if (hotDay.isNullOrEmpty()) {
            TagLog.e("调用intent缺少'KEY_HOT_DAY'参数")
            return arrayListOf()
        }

        // for holder
        return arrayListOf(FrgDailyDetail().apply {
            arguments = Bundle().apply { putString(KEY_HOT_DAY, hotDay) }
        })
    }

    companion object {
        const val KEY_HOT_DAY = "hot_day"
    }
}
