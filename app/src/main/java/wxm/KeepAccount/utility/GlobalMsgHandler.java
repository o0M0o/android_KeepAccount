package wxm.KeepAccount.utility;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.UsrItem;


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
            case GlobalDef.MSG_USR_ADDUSR : {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                if(ContextUtil.getUsrUtility().hasUsr(usr))  {
                    ReplyMsg(h, GlobalDef.MSG_USR_ADDUSR,
                            new Object[]{false, data, "用户已经存在！"});
                } else {
                    boolean ret = (null != ContextUtil.getUsrUtility().addUsr(usr, pwd));
                    ReplyMsg(h, GlobalDef.MSG_USR_ADDUSR,
                            new Object[]{ret, data});
                }
            }
            break;

            case GlobalDef.MSG_USR_LOGIN: {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);

                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                ContextUtil.setCurUsr(ContextUtil.getUsrUtility().CheckAndGetUsr(usr, pwd));
                boolean ret = (null != ContextUtil.getCurUsr());

                ReplyMsg(h, GlobalDef.MSG_USR_LOGIN, ret);
            }
            break;

            case GlobalDef.MSG_USR_LOGOUT : {
                ContextUtil.setCurUsr(null);
            }
            break;
        }
    }


    /**
     * 回复消息
     * @param mh            接收回复消息的句柄
     * @param msg_type      原消息类型（{@code arg1}）
     * @param msg_obj       回复消息的参数{@code obj}
     */
    private static void ReplyMsg(Handler mh, int msg_type, Object msg_obj) {
        Message m = Message.obtain(mh, GlobalDef.MSG_REPLY);
        m.arg1 = msg_type;
        m.obj = msg_obj;
        m.sendToTarget();
    }
}

