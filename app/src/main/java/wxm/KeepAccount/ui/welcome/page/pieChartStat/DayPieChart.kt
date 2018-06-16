package wxm.KeepAccount.ui.welcome.page.pieChartStat

import android.os.Bundle
import android.view.View
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.let1

/**
 * @author      WangXM
 * @version     create：2018/6/16
 */
class DayPieChart : PieChartBase() {
    private lateinit var mLLDays: List<String>
    private var mSZHotDay = ""

    override fun loadUI(savedInstanceState: Bundle?) {
        super.loadUI(savedInstanceState)

        val l = { v:View ->
            val hotIdx = mLLDays.indexOf(mSZHotDay)
            if(0 <= hotIdx) {
                val dif = (v.id == R.id.iv_left).doJudge(-1, 1)
                val maxIdx = mLLDays.size - 1
                val newIdx = hotIdx + dif
                when {
                    0 > newIdx -> mIVLeft.visibility = View.INVISIBLE
                    newIdx > maxIdx -> mIVRight.visibility = View.INVISIBLE
                    else -> {
                        mIVLeft.visibility = View.VISIBLE
                        mIVRight.visibility = View.VISIBLE

                        mSZHotDay = mLLDays[newIdx]
                        doLoadDay()
                    }
                }
            }
        }

        mIVLeft.setOnClickListener(l)
        mIVRight.setOnClickListener(l)

        mLLDays = NoteDataHelper.notesDays
        mLLDays.isEmpty().doJudge(View.INVISIBLE, View.VISIBLE).let1 {
            mIVLeft.visibility = it
            mIVRight.visibility = it
        }

        if (mLLDays.isNotEmpty()) {
            mSZHotDay = mLLDays.last()
            doLoadDay()
        }
    }

    private fun doLoadDay() {
        mTVDateRange.text = mSZHotDay
        loadData(mSZHotDay, mSZHotDay)
    }
}