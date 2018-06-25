package wxm.KeepAccount.ui.sync

import android.content.Context
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.toDayHourMinuteStr
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
class SmsAdapter
    constructor(context: Context, sData: List<Map<String, SmsItem>>)
    : MoreAdapter(context, sData, R.layout.li_sms)  {
    override fun loadView(pos: Int, vhHolder: ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val si = (getItem(pos) as Map<String, SmsItem>)[KEY_DATA]!!

        vhHolder.setText(R.id.tv_sender, si.address)
        vhHolder.setText(R.id.tv_date, si.date.toDayHourMinuteStr())
        vhHolder.setText(R.id.tv_content, si.body)
    }

    companion object {
        const val KEY_DATA = "data"
    }
}

