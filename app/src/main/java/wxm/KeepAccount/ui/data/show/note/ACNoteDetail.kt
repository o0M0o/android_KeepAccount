package wxm.KeepAccount.ui.data.show.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.ACBase.ACBase
import wxm.KeepAccount.ui.data.show.note.page.FrgNoteDetail
import wxm.androidutil.log.TagLog
import java.sql.Timestamp

/**
 * day detail UI
 */
class ACNoteDetail : ACBase<FrgNoteDetail>() {
    override fun setupFragment(): MutableList<FrgNoteDetail> {
        val startDay = intent!!.getSerializableExtra(KEY_START_DAY)
        val endDay = intent!!.getSerializableExtra(KEY_END_DAY)
        if (null == startDay || null == endDay)  {
            TagLog.e("调用intent缺少'KEY_START_DAY'或者'KEY_END_DAY'参数")
            return arrayListOf()
        }

        val filterPay = intent!!.getBooleanExtra(GlobalDef.STR_RECORD_PAY, false)
        val filterIncome = intent!!.getBooleanExtra(GlobalDef.STR_RECORD_INCOME, false)

        // for holder
        return arrayListOf(FrgNoteDetail().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_START_DAY, startDay)
                putSerializable(KEY_END_DAY, endDay)

                putBoolean(GlobalDef.STR_RECORD_PAY, filterPay)
                putBoolean(GlobalDef.STR_RECORD_INCOME, filterIncome)
            }
        })
    }

    companion object {
        const val KEY_START_DAY = "start_day"
        const val KEY_END_DAY = "end_day"

        fun start(ct: Context, frg: Fragment, startDay: Timestamp, endDay: Timestamp,
                  filter: Iterable<String> = listOf(GlobalDef.STR_RECORD_PAY, GlobalDef.STR_RECORD_INCOME)) {
            frg.startActivityForResult(
                    Intent(ct, ACNoteDetail::class.java).apply {
                        putExtra(KEY_START_DAY, startDay)
                        putExtra(KEY_END_DAY, endDay)

                        filter.forEach {
                            putExtra(it, true)
                        }
                    },
                    1)
        }
    }
}
