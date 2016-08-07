package com.wxm.KeepAccount.Base.handler;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.data.AppMsgDef;
import com.wxm.KeepAccount.Base.utility.ToolUtil;
import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.Base.utility.ContextUtil;

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
                Object[] arr = ToolUtil.cast(msg.obj);

                Intent data = ToolUtil.cast(arr[0]);
                Handler h = ToolUtil.cast(arr[1]);
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));

                Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
                if(AppModel.getInstance().hasUsr(usr))  {
                    m.obj = new Object[]{false, data, "用户已经存在！"};
                    m.arg1 = AppMsgDef.MSG_USR_ADDUSR;
                    m.sendToTarget();
                } else {
                    boolean ret = AppModel.getInstance().addUsr(usr, pwd);

                    m.obj = new Object[]{ret, data};
                    m.arg1 = AppMsgDef.MSG_USR_ADDUSR;
                    m.sendToTarget();
                }
            }
            break;

            case AppMsgDef.MSG_USR_LOGIN: {
                Object[] arr = ToolUtil.cast(msg.obj);

                Intent data = ToolUtil.cast(arr[0]);
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));

                boolean ret = AppModel.getInstance().checkUsr(usr, pwd);
                if(ret)   {
                    AppModel.getInstance().cur_usr = usr;
                }

                Handler h = ToolUtil.cast(arr[1]);
                Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
                m.obj = ret;
                m.arg1 = AppMsgDef.MSG_USR_LOGIN;
                m.sendToTarget();
            }
            break;

            case AppMsgDef.MSG_USR_LOGOUT : {
                AppModel.getInstance().cur_usr = "";
            }
            break;

            case AppMsgDef.MSG_USR_HASUSR : {
                Intent data = (Intent) msg.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                AppModel.getInstance().hasUsr(usr);
            }
            break;
        }
    }
}
