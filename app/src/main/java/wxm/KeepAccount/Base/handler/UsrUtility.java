package wxm.KeepAccount.Base.handler;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.db.UsrItem;

/**
 * 处理用户登录的辅助类
 * Created by 123 on 2016/5/15.
 */
class UsrUtility {
    private static final String TAG = "UsrUtility";

    static void doMsg(Message msg)   {
        switch (msg.what) {
            case AppMsgDef.MSG_USR_ADDUSR : {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                if(AppModel.getUsrUtility().hasUsr(usr))  {
                    GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_USR_ADDUSR,
                                    new Object[]{false, data, "用户已经存在！"});
                } else {
                    boolean ret = (null != AppModel.getUsrUtility().addUsr(usr, pwd));
                    GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_USR_ADDUSR,
                                    new Object[]{ret, data});
                }
            }
            break;

            case AppMsgDef.MSG_USR_LOGIN: {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                AppModel.getInstance().setCurUsr(AppModel.getUsrUtility().CheckAndGetUsr(usr, pwd));
                boolean ret = (null != AppModel.getInstance().getCurUsr());

                Handler h = UtilFun.cast(arr[1]);
                GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_USR_LOGIN, ret);
            }
            break;

            case AppMsgDef.MSG_USR_LOGOUT : {
                AppModel.getInstance().setCurUsr(null);
            }
            break;
        }
    }
}
