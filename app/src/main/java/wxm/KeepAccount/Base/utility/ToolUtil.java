package wxm.KeepAccount.Base.utility;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 工具类
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {
    private static final String TAG = "ToolUtil";


    /**
     * 把"2016-01-14"转换为"2016年01月14日"
     * @param org 待转换日期字符串
     * @return 转换后的字符串
     */
    public static String FormatDateString(String org)   {
        int orglen = org.length();
        String ret = org.replaceFirst("-", "年")
                            .replaceFirst("-", "月")
                            .replaceFirst("-", "日");

        if(4 == orglen)
            ret += "年";

        if(7 == orglen)
            ret += "月";

        if(10 == orglen)
            ret += "日";

        return ret;
    }

    /**
     *
     * 把"2016年01月14日"转换为"2016-01-14"
     * @param org 待转换日期字符串
     * @return 转换后的字符串
     */
    public static String ReFormatDateString(String org) {
        String ret = org;
        int max_pos = org.length() - 1;

        int day_pos = org.indexOf("日");
        if(0 < day_pos)     {
            ret = ret.replace("日", "");
        }

        int moth_pos = org.indexOf("月");
        if(0 < moth_pos) {
            if(moth_pos == max_pos)
                ret = ret.replace("月", "");
            else
                ret = ret.replace("月", "-");
        }

        int year_pos = org.indexOf("年");
        if(0 < year_pos) {
            if(year_pos == max_pos)
                ret = ret.replace("年", "");
            else
                ret = ret.replace("年", "-");
        }

        return ret;
    }



    /**
     * 时间字符串转换到时间戳
     * 待转换字符串必须是"2016-08-06"或者"2016-08-06 12:00:00"格式
     * @param str   待转换时间字符串
     * @return  如果字符串不符合规范或者遭遇异常则返回 Timestamp(0)
     */
    public static Timestamp StringToTimestamp(String str)   {
        Timestamp ts = new Timestamp(0);
        int slen = str.length();
        if((slen != "yyyy-MM-dd HH:mm:ss".length())
                && (slen != "yyyy-MM-dd".length()))
            return ts;

        String valstr = str.replace("年", "-").replace("月", "-").replace("日", "");
        if(slen == "yyyy-MM-dd".length())
            valstr += " 00:00:00";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            java.util.Date date = format.parse(valstr);
            ts.setTime(date.getTime());
        } catch (ParseException ex)     {
            ts = new Timestamp(0);
        }

        return  ts;
    }


    public static String DateToDateStr(Date dt) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(dt.getTime());
        return String.format(Locale.CHINA  ,"%d年%02d月%02d日"
                    ,cd.get(Calendar.YEAR) ,cd.get(Calendar.MONTH) ,cd.get(Calendar.DAY_OF_MONTH));
    }


    public static String DateToSerializetr(Date dt) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(dt.getTime());

        return String.format(Locale.CHINA  ,"%d-%02d-%02d %02d:%02d:%02d.%03d"
                ,cd.get(Calendar.YEAR) ,cd.get(Calendar.MONTH) ,cd.get(Calendar.DAY_OF_MONTH)
                ,cd.get(Calendar.HOUR_OF_DAY) ,cd.get(Calendar.MINUTE) ,cd.get(Calendar.SECOND)
                ,cd.get(Calendar.MILLISECOND));
    }

    public static Date SerializeStrToDate(String str_dt)    {
        Timestamp ts = new Timestamp(0);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
            java.util.Date date = format.parse(str_dt);
            ts.setTime(date.getTime());
        } catch (ParseException ex)     {
            Log.e(TAG, "转换'" + str_dt + "'失败，ex : " + UtilFun.ExceptionToString(ex));
        }

        return new Date(ts.getTime());
    }
}
