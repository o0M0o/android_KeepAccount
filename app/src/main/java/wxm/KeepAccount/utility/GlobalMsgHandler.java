package wxm.KeepAccount.utility;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import wxm.KeepAccount.define.EMsgType;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.UsrItem;


/**
 * global msg handler
 * Created by 123 on 2016/6/30.
 */
public class GlobalMsgHandler extends Handler {
    private static final String TAG = "GlobalMsgHandler";

    /**
     * reply msg
     * @param mh        receive handler
     * @param msg_type  origin msg type{@code arg1}）
     * @param msg_obj   object for reply{@code obj}
     */
    private static void ReplyMsg(Handler mh, int msg_type, Object msg_obj) {
        Message m = Message.obtain(mh, EMsgType.REPLAY.getId());
        m.arg1 = msg_type;
        m.obj = msg_obj;
        m.sendToTarget();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.i(TAG, "receive msg : " + msg.toString());
        EMsgType et = EMsgType.getEMsgType(msg.what);
        if(null == et)
            return;

        switch (et) {
            case USR_ADD: {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                if (ContextUtil.getUsrUtility().hasUsr(usr)) {
                    ReplyMsg(h, EMsgType.USR_ADD.getId(),
                            new Object[]{false, data, "用户已经存在！"});
                } else {
                    boolean ret = (null != ContextUtil.getUsrUtility().addUsr(usr, pwd));
                    ReplyMsg(h, EMsgType.USR_ADD.getId(),
                            new Object[]{ret, data});
                }
            }
            break;

            case USR_LOGIN: {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);

                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                ContextUtil.setCurUsr(ContextUtil.getUsrUtility().CheckAndGetUsr(usr, pwd));
                boolean ret = (null != ContextUtil.getCurUsr());

                ReplyMsg(h, EMsgType.USR_LOGIN.getId(), ret);
            }
            break;

            case USR_LOGOUT: {
                ContextUtil.setCurUsr(null);
            }
            break;
        }
    }
}

