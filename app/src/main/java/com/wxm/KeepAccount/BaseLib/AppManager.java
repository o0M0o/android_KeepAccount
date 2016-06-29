package com.wxm.KeepAccount.BaseLib;

/**
 * 处理app内部消息接口类
 * Created by 123 on 2016/5/6.
 */
public class AppManager {
    private static final String TAG = "AppManager";

    private static final AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    private AppManager() {
    }

    /**
     * 处理app中消息
     * @param am 待处理消息
     * @return  消息处理结果
     */
    public Object ProcessAppMsg(AppMsg am)
    {
        Object ret = null;
        switch (am.msg)
        {
            case AppMsgDef.MSG_LOAD_ALL_RECORDS:
            case AppMsgDef.MSG_RECORD_ADD:
            case AppMsgDef.MSG_RECORD_GET:
            case AppMsgDef.MSG_RECORD_MODIFY:
            case AppMsgDef.MSG_TO_DAILY_DETAILREPORT:
            case AppMsgDef.MSG_DELETE_RECORDS:
            case AppMsgDef.MSG_TO_DAYREPORT:
            case AppMsgDef.MSG_TO_MONTHREPORT:
            case AppMsgDef.MSG_TO_YEARREPORT:   {
                ret = RecordUtility.processMsg(am);
            }
            break;

            case AppMsgDef.MSG_USR_HASUSR :
            case AppMsgDef.MSG_USR_ADDUSR :
            case AppMsgDef.MSG_USR_LOGOUT :
            case AppMsgDef.MSG_USR_LOGIN: {
                ret = UsrUtility.processMsg(am);
            }
            break;
        }

        return ret;
    }
}
