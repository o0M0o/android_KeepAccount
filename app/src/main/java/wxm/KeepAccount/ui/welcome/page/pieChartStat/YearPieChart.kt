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
class YearPieChart : PieChartBase() {
    private lateinit var mLLYears: List<String>
    private var mSZHotYear = ""

    override fun loadUI(savedInstanceState: Bundle?) {
        super.loadUI(savedInstanceState)

        val l = { v:View ->
            val hotIdx = mLLYears.indexOf(mSZHotYear)
            if(0 <= hotIdx) {
                val dif = (v.id == R.id.iv_left).doJudge(-1, 1)
                val maxIdx = mLLYears.size - 1
                val newIdx = hotIdx + dif
                when {
                    0 > newIdx -> mIVLeft.visibility = View.INVISIBLE
                    newIdx > maxIdx -> mIVRight.visibility = View.INVISIBLE
                    else -> {
                        mIVLeft.visibility = View.VISIBLE
                        mIVRight.visibility = View.VISIBLE

                        mSZHotYear = mLLYears[newIdx]
                        doLoadDay()
                    }
                }
            }
        }

        mIVLeft.setOnClickListener(l)
        mIVRight.setOnClickListener(l)

        mTVDayInWeek.visibility = View.GONE

        mLLYears = NoteDataHelper.notesYears
        mLLYears.isEmpty().doJudge(View.INVISIBLE, View.VISIBLE).let1 {
            mIVLeft.visibility = it
            mIVRight.visibility = it
        }

        if (mLLYears.isNotEmpty()) {
            mSZHotYear = mLLYears.last()
            doLoadDay()
        }
    }

    override fun lookDetail() {
        val fDay = ToolUtil.stringToTimestamp("$mSZHotYear-01-01")

        val lMonth = ToolUtil.stringToTimestamp("$mSZHotYear-12-01")
        val lDay = String.format(Locale.CHINA, "$mSZHotYear-12-%02d",
                lMonth.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)).let {
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
        mTVDateRange.text = mSZHotYear

        NoteDataHelper.getInfoByYear(mSZHotYear).let1 {
            mTVPay.text = it.payAmount.toMoneyStr()
            mTVIncome.text = it.incomeAmount.toMoneyStr()
            mTVTotal.text = it.balance.toSignalMoneyStr()
        }

        loadData("$mSZHotYear-01-01", "$mSZHotYear-12-31")
    }
}