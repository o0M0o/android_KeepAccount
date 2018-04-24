package wxm.KeepAccount.ui.utility


import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList

import wxm.KeepAccount.define.INote
import wxm.KeepAccount.utility.ContextUtil

/**
 * Data helper for NoteShow
 * Created by WangXM on2016/5/30.
 */
class NoteDataHelper private constructor() {
    /**
     * example :
     * '2016-10-24' ---- data   (日数据）
     * '2016-10'    ---- data   (月数据)
     * '2016'       ---- data   (年数据)
     */
    private val mHMDayInfo: HashMap<String, NoteShowInfo> = HashMap()
    private val mHMMonthInfo: HashMap<String, NoteShowInfo> = HashMap()
    private val mHMYearInfo: HashMap<String, NoteShowInfo> = HashMap()
    /**
     * get data group by day
     * @return  data
     */
    var notesForDay: HashMap<String, ArrayList<INote>>? = null
        private set
    /**
     * get data group by month
     * @return  data
     */
    var notesForMonth: HashMap<String, ArrayList<INote>>? = null
        private set
    /**
     * get data group by year
     * @return  data
     */
    var notesForYear: HashMap<String, ArrayList<INote>>? = null
    private var mALOrderedDays: ArrayList<String>? = null

    /**
     * update data for day/month/year
     */
    fun refreshData() {
        mHMDayInfo.clear()
        mHMMonthInfo.clear()
        mHMYearInfo.clear()

        val lsData = ContextUtil.payIncomeUtility.allNotes
        notesForYear = ContextUtil.payIncomeUtility.getNotesToYear(lsData)
        notesForMonth = ContextUtil.payIncomeUtility.getNotesToMonth(lsData)
        notesForDay = ContextUtil.payIncomeUtility.getNotesToDay(lsData)

        mALOrderedDays = ArrayList(notesForDay!!.keys)
        mALOrderedDays!!.sort()

        refreshDay()
        refreshMonth()
        refreshYear()
    }

    /**
     * get data between start to end
     * @param start     start day
     * @param end       end day
     * @return          data
     */
    fun getNotesBetweenDays(start: String, end: String): HashMap<String, ArrayList<INote>?> {
        val hmNote = HashMap<String, ArrayList<INote>?>()
        for (day in mALOrderedDays!!) {
            if (day >= start) {
                if (day > end)
                    break

                if(null != notesForDay) {
                    hmNote[day] = notesForDay!![day]
                }
            }
        }

        return hmNote
    }

    /**
     * get records for day
     * @param day   day(example : '2017-07-06')
     * @return      records
     */
    fun getNotesByDay(day: String): List<INote>? {
        return notesForDay!![day]
    }


    /**
     * get next day have record
     * @param org_day   origin day(example : "2017-02-24")
     * @return          next day or ""
     */
    fun getNextDay(org_day: String): String {
        if (mALOrderedDays!!.isEmpty())
            return ""

        val max_id = mALOrderedDays!!.size - 1
        var id = mALOrderedDays!!.indexOf(org_day)
        if (-1 == id) {
            for (idx in max_id downTo 0) {
                val day = mALOrderedDays!![idx]
                if (day < org_day) {
                    id = if (idx < max_id) idx + 1 else idx
                    break
                }
            }
        } else {
            id = id + 1
        }

        return if (id <= max_id && id != -1)
            mALOrderedDays!![id]
        else
            ""
    }


    /**
     * get prior day have record
     * @param org_day   origin day(example : "2017-02-24")
     * @return          prior day or ""
     */
    fun getPrvDay(org_day: String): String {
        if (mALOrderedDays!!.isEmpty())
            return ""

        var id = mALOrderedDays!!.indexOf(org_day)
        if (-1 == id) {
            val len = mALOrderedDays!!.size
            for (idx in 0 until len) {
                val day = mALOrderedDays!![idx]
                if (0 < day.compareTo(org_day)) {
                    id = if (idx > 0) idx - 1 else 0
                    break
                }
            }
        } else {
            id -= 1
        }

        return if (id >= 0) mALOrderedDays!![id] else ""
    }

    /// PRIVATE BEGIN
    /**
     * update day stats
     * data from sqlite
     */
    private fun refreshDay() {
        for (k in mALOrderedDays!!) {
            val v = notesForDay!![k]
            val ni = NoteShowInfo()
            if (v != null) {
                for (r in v) {
                    if (r.isPayNote) {
                        ni.payCount = ni.payCount + 1
                        ni.payAmount = ni.payAmount.add(r.amount)
                    } else {
                        ni.incomeCount = ni.incomeCount + 1
                        ni.incomeAmount = ni.incomeAmount.add(r.amount)
                    }
                }
            }

            mHMDayInfo[k] = ni
        }
    }

    /**
     * update month stats
     * data from day stats
     */
    private fun refreshMonth() {
        val setkey = mHMDayInfo.keys
        for (k in setkey) {
            val curMonth = k.substring(0, 7)
            var vData: NoteShowInfo? = mHMMonthInfo[curMonth]
            if (null == vData) {
                vData = NoteShowInfo()
                mHMMonthInfo[curMonth] = vData
            }

            val curDay = mHMDayInfo[k]
            if(null != curDay) {
                vData.payCount = curDay.payCount + vData.payCount
                vData.incomeCount = curDay.incomeCount + vData.incomeCount
                vData.payAmount = curDay.payAmount.add(vData.payAmount)
                vData.incomeAmount = curDay.incomeAmount.add(vData.incomeAmount)
            }
        }
    }

    /**
     * update year stats
     * data from month stats
     */
    private fun refreshYear() {
        val setKey = mHMMonthInfo.keys
        for (k in setKey) {
            val curYear = k.substring(0, 4)
            var vData: NoteShowInfo? = mHMYearInfo[curYear]
            if (null == vData) {
                vData = NoteShowInfo()
                mHMYearInfo[curYear] = vData
            }

            val curMonth = mHMMonthInfo[k]
            if (curMonth != null) {
                vData.payCount = curMonth.payCount + vData.payCount
                vData.incomeCount = curMonth.incomeCount + vData.incomeCount
                vData.payAmount = curMonth.payAmount.add(vData.payAmount)
                vData.incomeAmount = curMonth.incomeAmount.add(vData.incomeAmount)
            }
        }
    }

    companion object {
        // 定义调用参数
        const val INTENT_PARA_FIRST_TAB = "first_tab"
        // 定义tab页标签内容
        const val TAB_TITLE_DAILY = "日流水"
        const val TAB_TITLE_MONTHLY = "月流水"
        const val TAB_TITLE_YEARLY = "年流水"
        const val TAB_TITLE_BUDGET = "预算"

        // use singleton
        val instance = NoteDataHelper()

        /**
         * get data for month
         * @param mt    month（example : '2017-01')
         * @return      data
         */
        fun getInfoByMonth(mt: String): NoteShowInfo? {
            return instance.mHMMonthInfo[mt]
        }

        /**
         * get month that have records
         * @return  month
         */
        val notesMonths: List<String>
            get() {
                val ls_sz = LinkedList<String>()
                ls_sz.addAll(instance.mHMMonthInfo.keys)
                return ls_sz
            }

        /**
         * get data for year
         * @param yr    year(example : '2017')
         * @return      data
         */
        fun getInfoByYear(yr: String): NoteShowInfo? {
            return instance.mHMYearInfo[yr]
        }

        /**
         * get year that have records
         * @return      year
         */
        val notesYears: List<String>
            get() {
                val lsSz = LinkedList<String>()
                lsSz.addAll(instance.mHMYearInfo.keys)
                return lsSz
            }

        /**
         * get data for year
         * @param day   day（example : '2017-01-12')
         * @return      data
         */
        fun getInfoByDay(day: String): NoteShowInfo? {
            return instance.mHMDayInfo[day]
        }

        /**
         * get day that have records
         * @return      days
         */
        val notesDays: List<String>
            get() {
                val lsSz = LinkedList<String>()
                lsSz.addAll(instance.mHMDayInfo.keys)
                return lsSz
            }
    }
    /// PRIVATE END
}
