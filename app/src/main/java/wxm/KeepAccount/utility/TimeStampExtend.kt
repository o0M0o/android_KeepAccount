package wxm.KeepAccount.utility

import wxm.androidutil.time.toCalendar
import java.sql.Timestamp
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/6
 */

/**
 * get hour&minute string for timestamp
 * example : 2018-06-06 12:30:45 -> 12:30
 */
fun Timestamp.toHourMinuteStr():String  {
    return this.toCalendar().let {
        String.format(Locale.CHINA, "%02d:%02d",
                it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
    }
}

/**
 * get day string for timestamp
 * example : 2018-06-06 12:30:45 -> 2018年06月06日
 */
fun Timestamp.toDayStr():String {
    return this.toCalendar().let {
        String.format(Locale.CHINA, "%04d年%02d月%02d日",
                it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
    }
}