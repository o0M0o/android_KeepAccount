package com.wxm.BaseLib;

import android.content.Intent;
import android.content.res.Resources;

import com.wxm.KeepAccount.R;

/**
 * 处理用户登录的辅助类
 * Created by 123 on 2016/5/15.
 */
public class UsrUtility {

    public static Object processMsg(AppMsg am)  {
        switch (am.msg) {
            case AppMsgDef.MSG_USR_ADDUSR : {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) am.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));
                return AppModel.getInstance().addUsr(usr, pwd);
            }

            case AppMsgDef.MSG_USR_LOGIN: {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) am.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                String pwd = data.getStringExtra(res.getString(R.string.usr_pwd));
                if(AppModel.getInstance().checkUsr(usr, pwd))   {
                    AppModel.getInstance().cur_usr = usr;
                    return true;
                }

                return false;
            }

            case AppMsgDef.MSG_USR_LOGOUT : {
                AppModel.getInstance().cur_usr = "";
                return true;
            }

            case AppMsgDef.MSG_USR_HASUSR : {
                Resources res = ContextUtil.getInstance().getResources();
                Intent data = (Intent) am.obj;
                String usr = data.getStringExtra(res.getString(R.string.usr_name));
                return AppModel.getInstance().hasUsr(usr);
            }
        }

        return null;
    }
}
