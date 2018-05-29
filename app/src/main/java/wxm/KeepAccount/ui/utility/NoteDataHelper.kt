package wxm.KeepAccount.ui.utility


import wxm.KeepAccount.item.INote
import wxm.KeepAccount.utility.AppUtil
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Data helper for NoteShow
 * Created by WangXM on2016/5/30.
 */
class NoteDataHelper private constructor() {
    data class NoteKey(val szDay: String) {
        val szYear: String = szDay.substring(0, 4)
        val szMonth: String = szDay.substring(0, 7)

        override fun equals(other: Any?): Boolean {
            return when (other) {
                !is NoteKey -> false
                else -> this === other || szDay == other.szDay
            }
        }

        override fun hashCode(): Int {
            return szDay.hashCode()
        }
    }

    /**
     * example :
     * '2016-10-24' ---- NoteInfo   (day)
     * '2016-10'    ---- NoteInfo   (month)
     * '2016'       ---- NoteInfo   (year)
     */
    private val mHMDayInfo: HashMap<String, NoteShowInfo> = HashMap()
    private val mHMMonthInfo: HashMap<String, NoteShowInfo> = HashMap()
    private val mHMYearInfo: HashMap<String, NoteShowInfo> = HashMap()

    /**
     * example :
     * '2016-10-24 12:00:00' ---- pay/income note
     */
    private val allNotes: HashMap<NoteKey, ArrayList<INote>> = HashMap()

    /**
     * day/month/year that have pay/income note
     */
    private val dayHaveNote: ArrayList<String> = ArrayList()
    private val monthHaveNote: ArrayList<String> = ArrayList()
    private val yearHaveNote: ArrayList<String> = ArrayList()

    /**
     * update data for day/month/year
     */
    private fun refreshData() {
        allNotes.clear()

        mHMDayInfo.clear()
        mHMMonthInfo.clear()
        mHMYearInfo.clear()

        dayHaveNote.clear()
        monthHaveNote.clear()
        yearHaveNote.clear()
        AppUtil.payIncomeUtility.allNotes.forEach {
            val nk = NoteKey(szDay = it.tsYearMonthDayTag)
            var lsNote = allNotes[nk]
            if(null == lsNote)    {
                lsNote = ArrayList()
                allNotes[nk] = lsNote
            }
            lsNote.add(it)

            dayHaveNote.apply {
                if(!contains(nk.szDay)) {
                    add(nk.szDay)
                } }

            monthHaveNote.apply {
                if(!contains(nk.szMonth)) {
                    add(nk.szMonth)
                } }

            yearHaveNote.apply {
                if(!contains(nk.szYear)) {
                    add(nk.szYear)
                } }
        }

        dayHaveNote.sort()
        monthHaveNote.sort()
        yearHaveNote.sort()

        refreshDay()
        refreshMonth()
        refreshYear()
    }

    /// PRIVATE BEGIN
    /**
     * update day stats
     * data from sqlite
     */
    private fun refreshDay() {
        allNotes.forEach{
            mHMDayInfo[it.key.szDay] = NoteShowInfo().apply {
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
            if (null == mHMMonthInfo[curMonth]) {
                mHMMonthInfo[curMonth] = NoteShowInfo()
            }

            mHMMonthInfo[curMonth]!!.apply {
                payCount += it.value.payCount
                incomeCount += it.value.incomeCount
                payAmount = it.value.payAmount.add(payAmount)
                incomeAmount = it.value.incomeAmount.add(incomeAmount)
            }
        }
    }

    /**
     * update year stats
     * data from month stats
     */
    private fun refreshYear() {
        mHMMonthInfo.forEach {
            val curYear = it.key.substring(0, 4)
            if (null == mHMYearInfo[curYear]) {
                mHMYearInfo[curYear] = NoteShowInfo()
            }

            mHMYearInfo[curYear]!!.apply {
                payCount += it.value.payCount
                incomeCount += it.value.incomeCount
                payAmount = it.value.payAmount.add(payAmount)
                incomeAmount = it.value.incomeAmount.add(incomeAmount)
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
         * reload data from DB
         * invoke it when DB changed
         */
        fun reloadData()    {
            instance.refreshData()
        }

        /**
         * get note between [[start] - [end]]
         */
        fun getNotesBetweenDays(start: String, end: String): HashMap<String, ArrayList<INote>?> {
            return HashMap<String, ArrayList<INote>?>().apply {
                instance.allNotes.filter { it.key.szDay in start..end }.forEach {
                    put(it.key.szDay, it.value) } }
        }

        /**
         * get note for [day]
         */
        fun getNotesByDay(day: String): List<INote>? {
            val nk = NoteKey(day)
            return if(instance.allNotes.containsKey(nk))    ArrayList(instance.allNotes[nk])
            else null
        }

        /**
         * get note for [month]
         */
        fun getNotesByMonth(month: String): List<INote> {
            return ArrayList<INote>().apply {
                instance.allNotes.filter { it.key.szMonth == month }.values.forEach {
                    addAll(it)
                }
            }
        }


        /**
         * get next day for [orgDay] have record or ""
         */
        fun getNextDay(orgDay: String): String? {
            instance.dayHaveNote.let {
                for((pos, day) in it.withIndex())   {
                    if(day == orgDay)  {
                        return if(pos == it.size - 1)   null   else it.elementAt(pos + 1)
                    }

                    if(day > orgDay)
                        return day
                }
            }

            return null
        }


        /**
         * get prior day have record
         * @param orgDay   origin day(example : "2017-02-24")
         * @return          prior day or ""
         */
        fun getPrvDay(orgDay: String): String? {
            instance.dayHaveNote.let {
                for(pos in it.size - 1 downTo 0)    {
                    val day = it.elementAt(pos)
                    if(day == orgDay)  {
                        return if(pos == 0)   null   else it.elementAt(pos - 1)
                    }

                    if(day < orgDay)
                        return day
                }
            }

            return null
        }


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
                return ArrayList<String>().apply {  addAll(instance.monthHaveNote) }
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
                return ArrayList<String>().apply { addAll(instance.yearHaveNote) }
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
                return ArrayList<String>().apply { addAll(instance.dayHaveNote) }
            }
    }
    /// PRIVATE END
}
