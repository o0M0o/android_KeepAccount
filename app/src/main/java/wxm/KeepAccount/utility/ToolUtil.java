package wxm.KeepAccount.utility;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * tool helper
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {
    private final static String[] DAY_IN_WEEK = {
            "星期日", "星期一", "星期二", "星期三",
            "星期四", "星期五", "星期六"};

    /**
     * format date string '2016-01-14' to '2016年01月14日'
     * @param org       origin date string
     * @return          format string
     */
    public static String FormatDateString(String org) {
        int orglen = org.length();
        String ret = org.replaceFirst("-", "年")
                .replaceFirst("-", "月")
                .replaceFirst("-", "日");

        if (4 == orglen)
            ret += "年";

        if (7 == orglen)
            ret += "月";

        if (10 == orglen)
            ret += "日";

        return ret;
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
    public static Timestamp StringToTimestamp(String str) throws ParseException {
        String valstr = str.replace("年", "-").replace("月", "-").replace("日", "");
        if (valstr.length() == "yyyy-MM-dd".length())
            valstr += " 00:00:00";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        java.util.Date date = format.parse(valstr);
        return new Timestamp(date.getTime());
    }

    /**
     * get day in week for timestamp
     * @param ts    time
     * @return      "星期*"
     */
    public static String getDayInWeek(Timestamp ts) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(ts.getTime());
        int dw = day.get(Calendar.DAY_OF_WEEK) - 1;
        return (0 <= dw && dw < DAY_IN_WEEK.length) ? DAY_IN_WEEK[dw] : "";
    }

    /**
     * get "星期*"
     * @param dw    day order in week(0-6)
     * @return      "星期*"
     */
    public static String getDayInWeek(int dw) {
        dw--;
        return DAY_IN_WEEK[dw];
    }

    /**
     * do in background then show in UI
     * @param h         current activity
     * @param back      run in background
     * @param ui        run in UI
     */
    public static void runInBackground(Activity h, Runnable back, Runnable ui)    {
        final WeakReference<Activity> weakActivity = new WeakReference<>(h);
        Executors.newCachedThreadPool().submit(() -> {
           back.run();

           Activity cur = weakActivity.get();
           if(!(null == cur || cur.isDestroyed() || cur.isFinishing())) {
               cur.runOnUiThread(ui);
           }
        });
    }
}
