package wxm.KeepAccount.Base.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import wxm.KeepAccount.Base.data.AppMsgDef;

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
            case AppMsgDef.MSG_RECORD_GET:
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


    /**
     * 回复消息
     * @param mh            接收回复消息的句柄
     * @param rmsgtype      原消息类型（{@code arg1}）
     * @param msgobj        回复消息的参数{@code obj}
     */
    public static void ReplyMsg(Handler mh, int rmsgtype, Object msgobj) {
        Message m = Message.obtain(mh, AppMsgDef.MSG_REPLY);
        m.arg1 = rmsgtype;
        m.obj = msgobj;
        m.sendToTarget();
    }
}
