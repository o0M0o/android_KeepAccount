package com.wxm.KeepAccount.BaseLib;

import java.lang.Object;

/**
 * 消息体定义
 * Created by 123 on 2016/5/6.
 */
public class AppMsg {
    public int src;
    public int dst;
    public int msg;

    public Object obj;
    public Object sub_obj;
    public Object sender;

    public AppMsg()
    {
    }
}
