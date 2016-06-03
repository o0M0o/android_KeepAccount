package com.wxm.KeepAccount.BaseLib;

/**
 * 工具类
 * Created by 123 on 2016/6/2.
 */
public class ToolUtil {

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
