package wxm.KeepAccount.Base.data;

import java.util.List;

import cn.wxm.andriodutillib.util.MD5Util;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;

/**
 * usr数据处理类
 * Created by 123 on 2016/8/9.
 */
public class UsrDataUtility {
    private final String    TAG = "UsrDataUtility";

    public UsrDataUtility()  {
    }


    /**
     * 检查是否用户'usr'已经存在
     * @param usr   待检查用户
     * @return  如果存在返回true,否则返回false
     */
    public boolean hasUsr(String usr) {
        if(UtilFun.StringIsNullOrEmpty(usr))
            return false;

        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        List<UsrItem> ret = mDBHelper.getUsrItemREDao()
                .queryForEq(UsrItem.FIELD_NAME, usr);
        return !((null == ret) || (ret.size() < 1));
    }


    /**
     * 添加用户
     * @param usr   待添加用户名
     * @param pwd   待添加用户密码
     * @return  添加成功返回添加后的数据, 否则返回null
     */
    public UsrItem addUsr(String usr, String pwd)   {
        if(UtilFun.StringIsNullOrEmpty(usr) || UtilFun.StringIsNullOrEmpty(pwd))
            return null;

        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }
        pwdpad = MD5Util.string2MD5(pwdpad);

        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem uiret = null;
        List<UsrItem> ret = mDBHelper.getUsrItemREDao()
                .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == ret) || (ret.size() < 1))   {
            UsrItem ui = new UsrItem();
            ui.setName(usr);
            ui.setPwd(pwdpad);

            if(1 == mDBHelper.getUsrItemREDao().create(ui))
                uiret = ui;
        } else  {
            UsrItem uiold = ret.get(0);
            if(!pwdpad.equals(uiold.getPwd()))  {
                uiold.setPwd(pwdpad);
                if(1 == mDBHelper.getUsrItemREDao().update(uiold))
                    uiret = uiold;
            } else {
                uiret = uiold;
            }
        }

        return uiret;
    }


    /**
     * 检查用户
     * @param usr   待检查用户名
     * @param pwd   待检查用户密码
     * @return  如果符合返回true, 否则返回false
     */
    public boolean checkUsr(String usr, String pwd) {
        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = AppModel.getDBHelper().getUsrItemREDao()
                            .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == lsui) || (lsui.size() < 1))
            return false;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        return checkPwd.equals(lsui.get(0).getPwd());

    }


    /**
     * 检查登录信息，如果有符合的记录就返回对应用户信息
     * @param usr   待检查用户名
     * @param pwd   待检查用户密码
     * @return  如果符合返回注册用户数据, 否则返回null
     */
    public UsrItem CheckAndGetUsr(String usr, String pwd)   {
        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = AppModel.getDBHelper().getUsrItemREDao()
                            .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == lsui) || (lsui.size() < 1))
            return null;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        if(checkPwd.equals(lsui.get(0).getPwd()))
            return lsui.get(0);

        return null;
    }

    /**
     * 根据用户ID获取用户数据
     * @param uid 用户ID
     * @return  用户数据，或者返回NULL
     */
    public UsrItem GetUsrById(int uid)  {
        return AppModel.getDBHelper().getUsrItemREDao().queryForId(uid);
    }
}
