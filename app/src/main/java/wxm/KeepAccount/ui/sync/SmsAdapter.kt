package wxm.KeepAccount.ui.sync

import android.content.Context
import wxm.KeepAccount.R
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.ViewHolder

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
class SmsAdapter
    constructor(context: Context, sData: List<Map<String, String>>)
    : MoreAdapter(context, sData, R.layout.li_sms) {

    override fun loadView(pos: Int, vhHolder: ViewHolder) {
        @Suppress("UNCHECKED_CAST")
        val hm = (getItem(pos) as Map<String, String>)

        vhHolder.setText(R.id.tv_sender, hm[KEY_SENDER])
        vhHolder.setText(R.id.tv_date, hm[KEY_DATE])
        vhHolder.setText(R.id.tv_content, hm[KEY_CONTENT])
    }

    companion object {
        const val KEY_SENDER = "sender"
        const val KEY_CONTENT = "content"
        const val KEY_DATE = "date"
    }
}

