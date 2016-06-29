package com.wxm.KeepAccount.BaseLib;

/**
 * 消息定义
 * Created by 123 on 2016/5/6.
 */
public class AppMsgDef {
    static final public int ACTIVITY_MAIN = 1;
    static final public int ACTIVITY_RECORD_PAY = 2;
    static final public int ACTIVITY_RECORD_INCOME = 3;
    static final public int APP_MANAGER = 10;

    static final public int MSG_USR_HASUSR  = 1000;
    static final public int MSG_USR_ADDUSR  = 1001;
    static final public int MSG_USR_LOGIN   = 1002;
    static final public int MSG_USR_LOGOUT  = 1003;

    static final public int MSG_CHANGE_ACTIVITY             = 2000;
    static final public int MSG_LOAD_ALL_RECORDS            = 2001;
    static final public int MSG_RECORD_ADD                  = 2002;
    static final public int MSG_RECORD_MODIFY               = 2003;
    static final public int MSG_RECORD_GET                  = 2004;
    static final public int MSG_DELETE_RECORDS              = 2005;

    static final public int MSG_TO_DAYREPORT                = 3000;
    static final public int MSG_TO_MONTHREPORT              = 3001;
    static final public int MSG_TO_YEARREPORT               = 3002;
    static final public int MSG_TO_DAILY_DETAILREPORT       = 3003;
}
