package wxm.KeepAccount.ui.utility


import wxm.KeepAccount.define.INote
import wxm.KeepAccount.utility.ContextUtil
import java.util.*
import kotlin.collections.ArrayList

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
     * '2016-10-24' ---- pay/income record
     */
    var notesForDay: HashMap<String, ArrayList<INote>> = HashMap()
        private set

    /**
     * get data group by month
     * '2016-10' ---- pay/income record
     */
    var notesForMonth: HashMap<String, ArrayList<INote>> = HashMap()
        private set

    /**
     * get data group by year
     * '2016' ---- pay/income record
     */
    var notesForYear: HashMap<String, ArrayList<INote>> = HashMap()
        private set

    /**
     * update data for day/month/year
     */
    fun refreshData() {
        mHMDayInfo.clear()
        mHMMonthInfo.clear()
        mHMYearInfo.clear()

        notesForDay.clear()
        notesForMonth.clear()
        notesForYear.clear()
        ContextUtil.payIncomeUtility.allNotes.forEach {
            // day
            val dTag = it.tsToStr!!.substring(0, 10)
            var lsDay = notesForDay[dTag]
            if (null == lsDay) {
                lsDay = ArrayList()
                notesForDay[dTag] = lsDay
            }
            lsDay.add(it)

            // month
            val mTag = dTag.substring(0, 7)
            var lsMonth = notesForMonth[mTag]
            if (null == lsMonth) {
                lsMonth = ArrayList()
                notesForMonth[mTag] = lsMonth
            }
            lsMonth.add(it)

            // day
            val yTag = mTag.substring(0, 4)
            var lsYear = notesForYear[yTag]
            if (null == lsYear) {
                lsYear = ArrayList()
                notesForYear[yTag] = lsYear
            }
            lsYear.add(it)
        }

        notesForDay.toSortedMap(kotlin.Comparator { o1, o2 ->  o1.compareTo(o2)})

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
        return HashMap<String, ArrayList<INote>?>().apply {
            notesForDay.filter { it.key in start..end }.forEach {
                put(it.key, it.value) } }
    }

    /**
     * get records for day
     * @param day   day(example : '2017-07-06')
     * @return      records
     */
    fun getNotesByDay(day: String): List<INote>? {
        return notesForDay[day]
    }


    /**
     * get next day have record
     * @param orgDay   origin day(example : "2017-02-24")
     * @return          next day or ""
     */
    fun getNextDay(orgDay: String): String {
        notesForDay.keys.let {
            for((pos, day) in it.withIndex())   {
                if(day == orgDay)  {
                    return if(pos == it.size - 1)   ""   else it.elementAt(pos + 1)
                }

                if(day > orgDay)
                    return day
            }
        }

        return ""
    }


    /**
     * get prior day have record
     * @param orgDay   origin day(example : "2017-02-24")
     * @return          prior day or ""
     */
    fun getPrvDay(orgDay: String): String {
        notesForDay.keys.let {
            if(it.isEmpty())
                return ""

            for(pos in it.size - 1 downTo 0)    {
                val day = it.elementAt(pos)
                if(day == orgDay)  {
                    return if(pos == 0)   ""   else it.elementAt(pos - 1)
                }

                if(day < orgDay)
                    return day
            }
        }

        return ""
    }

    /// PRIVATE BEGIN
    /**
     * update day stats
     * data from sqlite
     */
    private fun refreshDay() {
        notesForDay.forEach{
            mHMDayInfo[it.key] = NoteShowInfo().apply {
                it.value.forEach{
                    if (it.isPayNote) {
                        payCount += 1
                        payAmount = payAmount.add(it.amount)
                    } else {
                        incomeCount += 1
                        incomeAmount = incomeAmount.add(it.amount)
                    }
                }
            }
        }
    }

    /**
     * update month stats
     * data from day stats
     */
    private fun refreshMonth() {
        mHMDayInfo.forEach {
            val curMonth = it.key.substring(0, 7)
            var vData: NoteShowInfo? = mHMMonthInfo[curMonth]
            if (null == vData) {
                vData = NoteShowInfo()
                mHMMonthInfo[curMonth] = vData
            }

            vData.payCount += it.value.payCount
            vData.incomeCount += it.value.incomeCount
            vData.payAmount = it.value.payAmount.add(vData.payAmount)
            vData.incomeAmount = it.value.incomeAmount.add(vData.incomeAmount)
        }
    }

    /**
     * update year stats
     * data from month stats
     */
    private fun refreshYear() {
        mHMMonthInfo.forEach {
            val curYear = it.key.substring(0, 4)
            var vData: NoteShowInfo? = mHMYearInfo[curYear]
            if (null == vData) {
                vData = NoteShowInfo()
                mHMYearInfo[curYear] = vData
            }

            vData.payCount += it.value.payCount
            vData.incomeCount += it.value.incomeCount
            vData.payAmount = it.value.payAmount.add(vData.payAmount)
            vData.incomeAmount = it.value.incomeAmount.add(vData.incomeAmount)
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
                return LinkedList<String>().apply {  addAll(instance.mHMMonthInfo.keys) }
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
                return LinkedList<String>().apply { addAll(instance.mHMYearInfo.keys) }
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
                return LinkedList<String>().apply { addAll(instance.mHMDayInfo.keys) }
            }
    }
    /// PRIVATE END
}
