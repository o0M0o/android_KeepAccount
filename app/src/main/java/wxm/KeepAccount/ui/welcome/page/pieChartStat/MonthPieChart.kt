package wxm.KeepAccount.ui.welcome.page.pieChartStat

import android.os.Bundle
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.improve.toSignalMoneyStr
import wxm.KeepAccount.ui.data.show.note.ACNoteDetail
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1
import wxm.androidutil.time.getDayInWeekStr
import wxm.androidutil.time.toCalendar
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/16
 */
class MonthPieChart : PieChartBase() {
    private lateinit var mLLMonths: List<String>
    private var mSZHotMonth = ""

    override fun loadUI(savedInstanceState: Bundle?) {
        super.loadUI(savedInstanceState)

        val l = { v:View ->
            val hotIdx = mLLMonths.indexOf(mSZHotMonth)
            if(0 <= hotIdx) {
                val dif = (v.id == R.id.iv_left).doJudge(-1, 1)
                val maxIdx = mLLMonths.size - 1
                val newIdx = hotIdx + dif
                when {
                    0 > newIdx -> mIVLeft.visibility = View.INVISIBLE
                    newIdx > maxIdx -> mIVRight.visibility = View.INVISIBLE
                    else -> {
                        mIVLeft.visibility = View.VISIBLE
                        mIVRight.visibility = View.VISIBLE

                        mSZHotMonth = mLLMonths[newIdx]
                        doLoadDay()
                    }
                }
            }
        }

        mIVLeft.setOnClickListener(l)
        mIVRight.setOnClickListener(l)

        mTVDayInWeek.visibility = View.GONE

        mLLMonths = NoteDataHelper.notesMonths
        mLLMonths.isEmpty().doJudge(View.INVISIBLE, View.VISIBLE).let1 {
            mIVLeft.visibility = it
            mIVRight.visibility = it
        }

        if (mLLMonths.isNotEmpty()) {
            mSZHotMonth = mLLMonths.last()
            doLoadDay()
        }
    }

    override fun lookDetail() {
        val fDay = ToolUtil.stringToTimestamp("$mSZHotMonth-01")
        val lDay = String.format(Locale.CHINA, "%s-%02d",
                mSZHotMonth, fDay.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)).let {
            ToolUtil.stringToTimestamp(it)
        }

        val para = ArrayList<String>().apply {
            if(mTBIncome.isChecked) {
                add(GlobalDef.STR_RECORD_INCOME)
            }

            if(mTBPay.isChecked) {
                add(GlobalDef.STR_RECORD_PAY)
            }
        }

        ACNoteDetail.start(context!!, this, fDay, lDay, para)
    }

    private fun doLoadDay() {
        mTVDateRange.text = mSZHotMonth

        NoteDataHelper.getInfoByMonth(mSZHotMonth).let1 {
            mTVPay.text = it.payAmount.toMoneyStr()
            mTVIncome.text = it.incomeAmount.toMoneyStr()
            mTVTotal.text = it.balance.toSignalMoneyStr()
        }

        loadData("$mSZHotMonth-01", "$mSZHotMonth-31")
    }
}