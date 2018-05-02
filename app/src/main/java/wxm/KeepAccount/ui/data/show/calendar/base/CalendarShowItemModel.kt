package wxm.KeepAccount.ui.data.show.calendar.base


import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * for extended
 * Created by WangXM on 2017/07/06
 */
class CalendarShowItemModel : BaseItemModel() {
    var recordCount: Int = 0
        private set
        get() {
            if (!isCurrentMonth)
                return 0

            NoteDataHelper.getInfoByDay(
                    YEAR_MONTH_DAY_FORMAT.format(getTimeMill()))?.let {
                        return it.incomeCount + it.payCount
                    }
            return 0
        }

    companion object {
        private val YEAR_MONTH_DAY_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    }
}
