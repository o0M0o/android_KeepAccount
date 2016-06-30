package com.wxm.KeepAccount.base.handler;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Message;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.base.data.AppModel;
import com.wxm.KeepAccount.base.data.AppMsgDef;
import com.wxm.KeepAccount.base.utility.ContextUtil;

/**
 * 处理用户登录的辅助类
 * Created by 123 on 2016/5/15.
 */
public class UsrUtility {
    private static final String TAG = "UsrUtility";

    public static void doMsg(Message msg)   {
        switch (msg.what) {
            case AppMsgDef.MSG_USR_ADDUSR : {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) msg.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));
                AppModel.getInstance().addUsr(usr, pwd);
            }

            case AppMsgDef.MSG_USR_LOGIN: {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) msg.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));
                if(AppModel.getInstance().checkUsr(usr, pwd))   {
                    AppModel.getInstance().cur_usr = usr;
                }
            }

            case AppMsgDef.MSG_USR_LOGOUT : {
                AppModel.getInstance().cur_usr = "";
            }

            case AppMsgDef.MSG_USR_HASUSR : {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) msg.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                AppModel.getInstance().hasUsr(usr);
            }
        }
    }
}
