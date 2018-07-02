package wxm.KeepAccount.ui.welcome.page.pieChartStat

import android.os.Bundle
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.improve.toSignalMoneyStr
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1
import wxm.androidutil.time.getDayInWeekStr

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