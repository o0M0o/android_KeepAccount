package wxm.KeepAccount.utility;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 工具类
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {
    private static final String LOG_TAG = "ToolUtil";

    private final static String[] DAY_IN_WEEK = {
            "星期日", "星期一", "星期二", "星期三",
            "星期四", "星期五", "星期六"};


    /**
     * 把"2016-01-14"转换为"2016年01月14日"
     *
     * @param org 待转换日期字符串
     * @return 转换后的字符串
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
     * 时间字符串转换到时间戳
     * 待转换字符串必须是如下格式 ：
     * "2016-08-06",
     * "2016年08月06日"
     * "2016-08-06 12:00:00",
     * "2016年08月06日 12:00:00"
     *
     * @param str 待转换时间字符串
     * @return 转换的结果
     * @throws ParseException
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
     * 获取时间戳所在的星期信息
     *
     * @param ts 时间戳
     * @return 返回"星期*"
     */
    public static String getDayInWeek(Timestamp ts) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(ts.getTime());
        int dw = day.get(Calendar.DAY_OF_WEEK) - 1;
        return (0 <= dw && dw < DAY_IN_WEEK.length) ? DAY_IN_WEEK[dw] : "";
    }

    /**
     * 返回“星期*"
     *
     * @param dw 0-6格式的星期数
     * @return 星期*
     */
    public static String getDayInWeek(int dw) {
        dw--;
        return DAY_IN_WEEK[dw];
    }

    /**
     * 打印调用堆栈
     *
     * @param max_lv 打印最大调用层数
     * @return 调用堆栈
     */
    public static String[] getCallStack(int max_lv) {
        StackTraceElement st[] = Thread.currentThread().getStackTrace();
        int val_lv = st.length - 3;
        if (0 < val_lv) {
            int real_lv = max_lv > val_lv ? val_lv : max_lv;
            String[] ret_ar = new String[real_lv];

            int lv_end = real_lv + 3;
            for (int i = 3; i < lv_end; i++) {
                ret_ar[i - 3] = st[i].toString();
            }

            return ret_ar;
        }

        return new String[0];
    }

}
