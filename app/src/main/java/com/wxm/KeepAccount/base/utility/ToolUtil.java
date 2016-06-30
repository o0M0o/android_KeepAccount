package com.wxm.KeepAccount.base.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 工具类
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {
    private static final String TAG = "ToolUtil";

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        /*
        try {
            return (T) obj;
        }catch (ClassCastException | NullPointerException e)   {
            Log.e(TAG, ToolUtil.ExceptionToString(e));
        }

        return null;
        */
        return (T) obj;
    }

    /**
     * 可抛出类打印字符串
     * @param e 可抛出类
     * @return 字符串
     */
    public static String ThrowableToString(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw =  new PrintWriter(sw);
            //pw.append(e.getMessage());
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

        return sw.toString();
    }

    /**
     * 异常 --> 字符串
     * @param e 异常
     * @return 字符串
     */
    public static String ExceptionToString(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw =  new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

        return sw.toString();
    }

    /**
     * 检查字符串是否空或者null
     * @param cstr  待检查字符串
     * @return   检查结果
     */
    public static boolean StringIsNullOrEmpty(String cstr)      {
        return null == cstr || cstr.isEmpty();
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
}
