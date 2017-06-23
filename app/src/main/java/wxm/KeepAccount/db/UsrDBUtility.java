package wxm.KeepAccount.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.wxm.andriodutillib.DBHelper.DBUtilityBase;
import cn.wxm.andriodutillib.util.MD5Util;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * usr数据处理类
 * Created by 123 on 2016/8/9.
 */
public class UsrDBUtility extends DBUtilityBase<UsrItem, Integer> {
    private final String TAG = "UsrDBUtility";

    public UsrDBUtility() {
        super();
    }

    @Override
    protected RuntimeExceptionDao<UsrItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getUsrItemREDao();
    }

    /**
     * 检查是否用户'usr'已经存在
     *
     * @param usr 待检查用户
     * @return 如果存在返回true, 否则返回false
     */
    public boolean hasUsr(String usr) {
        if (UtilFun.StringIsNullOrEmpty(usr))
            return false;

        List<UsrItem> ret = getDBHelper().queryForEq(UsrItem.FIELD_NAME, usr);
        return !((null == ret) || (ret.size() < 1));
    }


    /**
     * 添加用户
     *
     * @param usr 待添加用户名
     * @param pwd 待添加用户密码
     * @return 添加成功返回添加后的数据, 否则返回null
     */
    public UsrItem addUsr(String usr, String pwd) {
        if (UtilFun.StringIsNullOrEmpty(usr) || UtilFun.StringIsNullOrEmpty(pwd))
            return null;

        String pwdpad = pwd;
        if (pwdpad.length() < GlobalDef.STR_PWD_PAD.length()) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length());
        }
        pwdpad = MD5Util.string2MD5(pwdpad);

        UsrItem uiret = null;
        List<UsrItem> ret = getDBHelper().queryForEq(UsrItem.FIELD_NAME, usr);
        if ((null == ret) || (ret.size() < 1)) {
            UsrItem ui = new UsrItem();
            ui.setName(usr);
            ui.setPwd(pwdpad);

            if (createData(ui))
                uiret = ui;
        } else {
            UsrItem uiold = ret.get(0);
            if (!pwdpad.equals(uiold.getPwd())) {
                uiold.setPwd(pwdpad);
                if (modifyData(uiold))
                    uiret = uiold;
            } else {
                uiret = uiold;
            }
        }

        return uiret;
    }


    /**
     * 检查用户
     *
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回true, 否则返回false
     */
    public boolean checkUsr(String usr, String pwd) {
        String pwdpad = pwd;
        if (pwdpad.length() < GlobalDef.STR_PWD_PAD.length()) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = getDBHelper().queryForEq(UsrItem.FIELD_NAME, usr);
        if ((null == lsui) || (lsui.size() < 1))
            return false;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        return checkPwd.equals(lsui.get(0).getPwd());

    }


    /**
     * 检查登录信息，如果有符合的记录就返回对应用户信息
     *
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回注册用户数据, 否则返回null
     */
    public UsrItem CheckAndGetUsr(String usr, String pwd) {
        String pwdpad = pwd;
        if (pwdpad.length() < GlobalDef.STR_PWD_PAD.length()) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = getDBHelper().queryForEq(UsrItem.FIELD_NAME, usr);
        if ((null == lsui) || (lsui.size() < 1))
            return null;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        if (checkPwd.equals(lsui.get(0).getPwd()))
            return lsui.get(0);

        return null;
    }

    /**
     * 使用用户信息登录APP
     * （并刷新数据)
     *
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果登录成功返回true, 否则返回false
     */
    public boolean loginByUsr(String usr, String pwd) {
        UsrItem ui = CheckAndGetUsr(usr, pwd);
        if (null == ui)
            return false;

        ContextUtil.setCurUsr(ui);
        NoteDataHelper.getInstance().refreshData();
        return true;
    }

    @Override
    protected void onDataModify(List<Integer> md) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataCreate(List<Integer> cd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }

    @Override
    protected void onDataRemove(List<Integer> dd) {
        EventBus.getDefault().post(new DBDataChangeEvent());
    }
}
