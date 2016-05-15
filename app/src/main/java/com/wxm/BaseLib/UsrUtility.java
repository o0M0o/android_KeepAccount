package com.wxm.BaseLib;

/**
 * 处理用户登录的辅助类
 * Created by 123 on 2016/5/15.
 */
public class UsrUtility {

    public static Object processMsg(AppMsg am)  {
        switch (am.msg) {
            case AppMsgDef.MSG_USR_ADDUSR :
                return AppModel.getInstance().addUsr((String)am.obj, (String)am.sub_obj);

            case AppMsgDef.MSG_USR_CHECKUSR :
                return AppModel.getInstance().checkUsr((String)am.obj, (String)am.sub_obj);

            case AppMsgDef.MSG_USR_HASUSR :
                return AppModel.getInstance().hasUsr((String)am.obj);
        }

        return null;
    }
}
