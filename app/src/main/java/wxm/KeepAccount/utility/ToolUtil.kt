package wxm.KeepAccount.utility

import android.app.Activity

import java.lang.ref.WeakReference
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors

/**
 * tool helper
 * Created by WangXM on 2016/6/2.
 */
object ToolUtil {
    /**
     * format date string '2016-01-14' to '2016年01月14日'
     * @param org       origin date string
     * @return          format string
     */
    fun formatDateString(org: String): String {
        val orglen = org.length
        var ret = org.replaceFirst("-".toRegex(), "年")
                .replaceFirst("-".toRegex(), "月")
                .replaceFirst("-".toRegex(), "日")

        if (4 == orglen)
            ret += "年"

        if (7 == orglen)
            ret += "月"

        if (10 == orglen)
            ret += "日"

        return ret
    }


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
    fun getDayInWeek(dw: Int): String {
        val ret: String
        when (dw) {
            Calendar.SUNDAY -> ret = "星期日"

            Calendar.MONDAY -> ret = "星期一"

            Calendar.TUESDAY -> ret = "星期二"

            Calendar.WEDNESDAY -> ret = "星期三"

            Calendar.THURSDAY -> ret = "星期四"

            Calendar.FRIDAY -> ret = "星期五"

            Calendar.SATURDAY -> ret = "星期六"

            else -> ret = ""
        }

        return ret
    }

    /**
     * do in background then show in UI
     * @param h         current activity
     * @param back      run in background
     * @param ui        run in UI
     */
    fun runInBackground(h: Activity, back: Runnable, ui: Runnable) {
        val weakActivity = WeakReference(h)
        Executors.newCachedThreadPool().submit {
            back.run()

            val cur = weakActivity.get()
            if (!(null == cur || cur.isDestroyed || cur.isFinishing)) {
                cur.runOnUiThread(ui)
            }
        }
    }
}
