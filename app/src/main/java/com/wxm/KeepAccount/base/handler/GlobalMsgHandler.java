package com.wxm.KeepAccount.Base.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wxm.KeepAccount.Base.data.AppMsgDef;

/**
 * APP全局消息处理器
 * Created by 123 on 2016/6/30.
 */
public class GlobalMsgHandler extends Handler {
    private static final String TAG = "GlobalMsgHandler";

    @Override
    public void handleMessage(Message msg) {
        Log.i(TAG, "receive msg : " + msg.toString());
        switch (msg.what)   {
            case AppMsgDef.MSG_LOAD_ALL_RECORDS:
            case AppMsgDef.MSG_RECORD_ADD:
            case AppMsgDef.MSG_RECORD_GET:
            case AppMsgDef.MSG_RECORD_MODIFY:
            case AppMsgDef.MSG_TO_DAILY_DETAILREPORT:
            case AppMsgDef.MSG_DELETE_RECORDS:
            case AppMsgDef.MSG_TO_DAYREPORT:
            case AppMsgDef.MSG_TO_MONTHREPORT:
            case AppMsgDef.MSG_TO_YEARREPORT:   {
                RecordUtility.doMsg(msg);
            }
            break;

            case AppMsgDef.MSG_USR_HASUSR :
            case AppMsgDef.MSG_USR_ADDUSR :
            case AppMsgDef.MSG_USR_LOGOUT :
            case AppMsgDef.MSG_USR_LOGIN: {
                UsrUtility.doMsg(msg);
            }
            break;
        }
    }
}
