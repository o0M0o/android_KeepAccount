package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;
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

    private final static String[] DAY_IN_WEEK = {
            "星期日", "星期一", "星期二","星期三",
            "星期四","星期五","星期六"};

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
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }

        return verName;
    }


    /**
     * 从asset中读取文件并转化为字符串
     * @param fileName  asset中文件名
     * @param encode    asset中文件编码
     * @return          文件内容字符串
     */
    public static String getFromAssets(String fileName, String encode){
        String result = "";
        try {
            InputStream in = ContextUtil.getInstance().getResources().getAssets().open(fileName);
            int lenght = in.available();
            byte[]  buffer = new byte[lenght];

            in.read(buffer);
            result = EncodingUtils.getString(buffer, encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在测试版本满足条件后抛出异常
     * @param bThrow    若true则抛出异常
     */
    public static void throwExIf(boolean bThrow) throws AssertionError {
        //if(BuildConfig.DEBUG && bThrow)     {
        if(bThrow)     {
            throw new AssertionError("测试版本出现异常");
        }
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
     * 待转换字符串必须是如下格式 ：
     *      "2016-08-06",
     *      "2016年08月06日"
     *      "2016-08-06 12:00:00",
     *      "2016年08月06日 12:00:00"
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
     * 设置layout可见性
     * 仅调整可见性，其它设置保持不变
     * @param visible  若为 :
     *                  1. {@code View.INVISIBLE}, 不可见
     *                  2. {@code View.VISIBLE}, 可见
     */
    public static void setViewGroupVisible(ViewGroup rl, int visible)    {
        ViewGroup.LayoutParams param = rl.getLayoutParams();
        param.width = rl.getWidth();
        param.height = View.INVISIBLE != visible ? rl.getHeight() : 0;
        rl.setLayoutParams(param);
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

    /**
     * 获取时间戳所在的星期信息
     * @param ts    时间戳
     * @return  返回"星期*"
     */
    public static String getDayInWeek(Timestamp ts) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(ts.getTime());
        int dw = day.get(Calendar.DAY_OF_WEEK) - 1;
        return (0 <= dw && dw < DAY_IN_WEEK.length) ? DAY_IN_WEEK[dw] : "";
    }

    /**
     * 调整numberpicker大小
     */
    public static void resizeNumberPicker(ViewGroup np) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(80, ViewGroup.LayoutParams.WRAP_CONTENT);
        np.setLayoutParams(params);
    }
}
