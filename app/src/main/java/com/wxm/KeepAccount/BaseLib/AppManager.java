package com.wxm.KeepAccount.BaseLib;

/**
 * 处理app内部消息接口类
 * Created by 123 on 2016/5/6.
 */
public class AppManager {
    private static final String TAG = "AppManager";

    private static AppManager ourInstance = new AppManager();
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
            case AppMsgDef.MSG_ADD_RECORD:
            case AppMsgDef.MSG_DAILY_RECORDS_TO_DETAILREPORT:
            case AppMsgDef.MSG_DELETE_RECORDS:
            case AppMsgDef.MSG_ALL_RECORDS_TO_DAYREPORT :   {
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
