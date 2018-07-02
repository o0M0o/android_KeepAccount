package wxm.KeepAccount.improve

import wxm.KeepAccount.item.INote
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.time.getDayInWeekStr
import wxm.androidutil.time.toCalendar
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * helper for timestamp
 * @author      WangXM
 * @version     create：2018/6/6
 */

private val FORMATTER_YEAR = CalendarUtility(SimpleDateFormat("yyyy", Locale.CHINA))
private val FORMATTER_YEAR_MONTH = CalendarUtility(SimpleDateFormat("yyyy-MM", Locale.CHINA))
private val FORMATTER_YEAR_MONTH_DAY = CalendarUtility(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))

/**
 * get hour&minute string for timestamp
 * example : 2018-06-06 12:30:45 -> 12:30
 */
fun Timestamp.toHourMinuteStr(): String {
    return this.toCalendar().let {
        String.format(Locale.CHINA, "%02d:%02d",
                it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
    }
}

/**
 * get day string for timestamp
 * example : 2018-06-06 12:30:45 -> 2018年06月06日
 */
fun Timestamp.toDayStr(): String {
    return this.toCalendar().let {
        String.format(Locale.CHINA, "%04d年%02d月%02d日",
                it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
    }
}

/**
 * get day-hour-minute for timestamp
 * example : 2018-06-06 12:30:45 -> 2018年06月06日 12:30
 */
fun Timestamp.toDayHourMinuteStr(): String {
    return this.toCalendar().let {
        String.format(Locale.CHINA, "%04d年%02d月%02d日 %02d:%02d",
                it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH),
                it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
    }
}

/**
 * get day in week string[星期一， 星期日]
 */
fun Timestamp.toDayInWeekStr(): String = this.toCalendar().getDayInWeekStr()


/**
 * for tag use
 */
fun Timestamp.toYearTag(): String = FORMATTER_YEAR.format(this)
fun Timestamp.toYearMonthTag(): String = FORMATTER_YEAR_MONTH.format(this)
fun Timestamp.toYearMonthDayTag(): String = FORMATTER_YEAR_MONTH_DAY.format(this)
fun Timestamp.toYearMonthDayHourMinuteTag(): String
        = CalendarUtility.SDF_YEAR_MONTH_DAY_HOUR_MINUTE.format(this)
fun Timestamp.toFullTag(): String
        = CalendarUtility.SDF_FULL.format(this)
