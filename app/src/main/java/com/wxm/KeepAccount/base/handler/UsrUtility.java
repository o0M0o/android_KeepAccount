package com.wxm.KeepAccount.Base.handler;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.data.AppMsgDef;
import com.wxm.KeepAccount.Base.db.UsrItem;
import com.wxm.KeepAccount.Base.utility.ContextUtil;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * 处理用户登录的辅助类
 * Created by 123 on 2016/5/15.
 */
public class UsrUtility {
    private static final String TAG = "UsrUtility";

    public static void doMsg(Message msg)   {
        Resources res = ContextUtil.getInstance().getResources();
        switch (msg.what) {
            case AppMsgDef.MSG_USR_ADDUSR : {
                Object[] arr = UtilFun.cast(msg.obj);

                Intent data = UtilFun.cast(arr[0]);
                Handler h = UtilFun.cast(arr[1]);
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                String pwd = data.getStringExtra(UsrItem.FIELD_PWD);

                Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
                if(AppModel.getUsrUtility().hasUsr(usr))  {
                    m.obj = new Object[]{false, data, "用户已经存在！"};
                    m.arg1 = AppMsgDef.MSG_USR_ADDUSR;
                    m.sendToTarget();
                } else {
                    boolean ret = (null != AppModel.getUsrUtility().addUsr(usr, pwd));

                    m.obj = new Object[]{ret, data};
                    m.arg1 = AppMsgDef.MSG_USR_ADDUSR;
                    m.sendToTarget();
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
                Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
                m.obj = ret;
                m.arg1 = AppMsgDef.MSG_USR_LOGIN;
                m.sendToTarget();
            }
            break;

            case AppMsgDef.MSG_USR_LOGOUT : {
                AppModel.getInstance().setCurUsr(null);
            }
            break;

            case AppMsgDef.MSG_USR_HASUSR : {
                Intent data = (Intent) msg.obj;
                String usr = data.getStringExtra(UsrItem.FIELD_NAME);
                AppModel.getUsrUtility().hasUsr(usr);
            }
            break;
        }
    }
}
