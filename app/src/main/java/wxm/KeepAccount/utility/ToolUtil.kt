package wxm.KeepAccount.utility

import android.app.Activity

import java.lang.ref.WeakReference
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * tool helper
 * Created by WangXM on 2016/6/2.
 */
object ToolUtil {
    /**
     * date string to Timestamp
     * string must be :
     * "2016-08-06",
     * "2016年08月06日"
     * "2016-08-06 12:00:00",
     * "2016年08月06日 12:00:00"
     *
     * @param str   date string
     * @return      timestamp
     * @throws ParseException parse failure
     */
    @Throws(ParseException::class)
    fun stringToTimestamp(str: String): Timestamp {
        var valstr = str.replace("年", "-").replace("月", "-").replace("日", "")
        if (valstr.length == "yyyy-MM-dd".length)
            valstr += " 00:00:00"

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val date = format.parse(valstr)
        return Timestamp(date.time)
    }

    /**
     * get day-in-week for [szDate]
     * string can be :
     * "2016-08-06",
     * "2016年08月06日"
     * "2016-08-06 12:00:00",
     * "2016年08月06日 12:00:00"
     */
    fun getDayInWeek(szDate: String): String {
        return getDayInWeek(stringToTimestamp(szDate))
    }

    /**
     * get day in week for timestamp
     * @param ts    time
     * @return      "星期*"
     */
    fun getDayInWeek(ts: Timestamp): String {
        val day = Calendar.getInstance()
        day.timeInMillis = ts.time
        return getDayInWeek(day.get(Calendar.DAY_OF_WEEK))
    }

    /**
     * get "星期*"
     * @param dw    day order in week(1-7)
     * @return      "星期*"
     */
    private fun getDayInWeek(dw: Int): String {
        return when (dw) {
            Calendar.SUNDAY -> "星期日"
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            else -> ""
        }
    }

    /**
     * in background-thread run [back]
     * after [back] and if no exception happen then run [ui] in ui-thread
     */
    fun runInBackground(h: Activity, back: () -> Unit, ui:  () -> Unit) {
        val weakActivity = WeakReference(h)
        var runRet = false
        Executors.newCachedThreadPool().submit {
            try {
                back()
                runRet = true
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if(runRet) {
                weakActivity.get()?.let {
                    if (!(it.isDestroyed || it.isFinishing)) {
                        it.runOnUiThread(ui)
                    }
                }
            }
        }
    }

    /**
     * in background-thread run [back]
     * return callable result or [defRet] if exception happens
     * caller will wait result with timeout parameter [unit] and [timeout]
     */
    fun<T> callInBackground(back: ()->T, defRet: T, unit: TimeUnit, timeout: Long): T {
        val task = Executors.newCachedThreadPool().submit(back)
        try {
            return task.get(timeout, unit)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return defRet
    }
}
