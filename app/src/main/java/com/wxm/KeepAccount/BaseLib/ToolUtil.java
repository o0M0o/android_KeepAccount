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
}
