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
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.time.getDayInWeekStr
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/16
 */
class WeekPieChart : PieChartBase() {
    private val mLLWeeks = LinkedList<String>()
    private var mSZHotWeek = ""

    override fun loadUI(savedInstanceState: Bundle?) {
        super.loadUI(savedInstanceState)

        val l = { v:View ->
            val hotIdx = mLLWeeks.indexOf(mSZHotWeek)
            if(0 <= hotIdx) {
                val dif = (v.id == R.id.iv_left).doJudge(-1, 1)
                val maxIdx = mLLWeeks.size - 1
                val newIdx = hotIdx + dif
                when {
                    0 > newIdx -> mIVLeft.visibility = View.INVISIBLE
                    newIdx > maxIdx -> mIVRight.visibility = View.INVISIBLE
                    else -> {
                        mIVLeft.visibility = View.VISIBLE
                        mIVRight.visibility = View.VISIBLE

                        mSZHotWeek = mLLWeeks[newIdx]
                        doLoadWeek()
                    }
                }
            }
        }

        mIVLeft.setOnClickListener(l)
        mIVRight.setOnClickListener(l)
        mTVDayInWeek.visibility = View.GONE

        parseWeek()
        mLLWeeks.isEmpty().doJudge(View.INVISIBLE, View.VISIBLE).let1 {
            mIVLeft.visibility = it
            mIVRight.visibility = it
        }

        if (mLLWeeks.isNotEmpty()) {
            mSZHotWeek = mLLWeeks.last()
            doLoadWeek()
        }
    }

    override fun lookDetail() {
        val fDay = mSZHotWeek.substring(0, mSZHotWeek.indexOf(SPLITER)).let {
            ToolUtil.stringToTimestamp(it)
        }
        val lDay = mSZHotWeek.substring(mSZHotWeek.indexOf(SPLITER) + SPLITER.length).let {
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

    private fun doLoadWeek() {
        mTVDateRange.text = mSZHotWeek
        val fDay = mSZHotWeek.substring(0, mSZHotWeek.indexOf(SPLITER))
        val lDay = mSZHotWeek.substring(mSZHotWeek.indexOf(SPLITER) + SPLITER.length)

        NoteDataHelper.getInfoByDay(fDay, lDay).let1 {
            mTVPay.text = it.payAmount.toMoneyStr()
            mTVIncome.text = it.incomeAmount.toMoneyStr()
            mTVTotal.text = it.balance.toSignalMoneyStr()
        }

        loadData(fDay, lDay)
    }


    private fun parseWeek() {
        val cu = CalendarUtility(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))
        mLLWeeks.clear()

        val nDays = NoteDataHelper.notesDays
        val len = nDays.size
        var i = 0
        while (i < len) {
            val cd = cu.parse(nDays[i])
            val fDay = getWeekFirstDay(cd)
            val lDay = getWeekLastDay(cd)
            mLLWeeks.add("${cu.format(fDay)}$SPLITER${cu.format(lDay)}")

            var j = i + 1
            while (j < len) {
                if(lDay < cu.parse(nDays[j]))    {
                    break
                }

                j += 1
            }

            i = j
        }
    }

    private fun getWeekFirstDay(day:Calendar): Calendar {
        val wd = day.get(Calendar.DAY_OF_WEEK)
        val dif = if(wd == Calendar.SUNDAY)   {
            -6
        } else  {
            -(wd - Calendar.MONDAY)
        }

        return Calendar.getInstance().apply {
            time = day.time
            add(Calendar.DAY_OF_WEEK, dif)
        }!!
    }

    private fun getWeekLastDay(day:Calendar): Calendar {
        val wd = day.get(Calendar.DAY_OF_WEEK)
        val dif = if(wd == Calendar.SUNDAY)   {
            0
        } else  {
            6 - (wd - Calendar.MONDAY)
        }

        return Calendar.getInstance().apply {
            time = day.time
            add(Calendar.DAY_OF_WEEK, dif)
        }!!
    }

    companion object {
        private const val SPLITER = " - "
    }
}