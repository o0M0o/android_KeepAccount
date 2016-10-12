package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 工具类
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {
    private static final String TAG = "ToolUtil";
    private static final String SELF_PACKAGE_NAME = "com.wxm.keepaccount";

    /**
     * 获取包版本号
     * @param context  包上下文
     * @return   包版本号
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(SELF_PACKAGE_NAME, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return verCode;
    }


    /**
     * 获取包版本名
     * @param context  包上下文
     * @return   包版本名
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(SELF_PACKAGE_NAME, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return verName;
    }



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
     * 待转换字符串必须是"2016-08-06", "2016-08-06 12:00:00",
     *      "2016年08月06日"或者"2016年08月06日 12:00:00"格式
     *
     * @param str   待转换时间字符串
     * @return  转换的结果
     * @throws ParseException
     */
    public static Timestamp StringToTimestamp(String str) throws ParseException   {
        String valstr = str.replace("年", "-").replace("月", "-").replace("日", "");
        if(valstr.length() == "yyyy-MM-dd".length())
            valstr += " 00:00:00";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        java.util.Date date = format.parse(valstr);
        return  new Timestamp(date.getTime());
    }


    /**
     * 转换Date到字符串
     * @param dt  待转换日期
     * @return 日期字符串,格式"****年**月**日"
     */
    public static String DateToDateStr(Date dt) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(dt.getTime());
        return String.format(Locale.CHINA  ,"%d年%02d月%02d日"
                    ,cd.get(Calendar.YEAR) ,cd.get(Calendar.MONTH) ,cd.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * 转换Date到序列化字符串
     * @param dt 待转换日期
     * @return 序列化字符串
     */
    public static String DateToSerializetr(Date dt) {
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(dt.getTime());

        return String.format(Locale.CHINA  ,"%d-%02d-%02d %02d:%02d:%02d.%03d"
                ,cd.get(Calendar.YEAR) ,cd.get(Calendar.MONTH) ,cd.get(Calendar.DAY_OF_MONTH)
                ,cd.get(Calendar.HOUR_OF_DAY) ,cd.get(Calendar.MINUTE) ,cd.get(Calendar.SECOND)
                ,cd.get(Calendar.MILLISECOND));
    }

    /**
     * 序列化日期字符串转换到日期数据
     * @param str_dt 序列化日期字符串
     * @return 转换的日期数据
     */
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

    /**
     * 检查链表是否为NULL或者空
     * @param lst 待检查链表
     * @return 若链表为NULL或者空则返回true,否则返回false
     */
    public static boolean ListIsNullOrEmpty(List lst)    {
        return (null == lst) || lst.isEmpty();

    }


    /**
     * 次级列表视图计算列表高度有错误，使用此函数校正
     * @param listView      列表
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        Adapter sap = listView.getAdapter();
        for (int i = 0, len = sap.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = sap.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (sap.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
