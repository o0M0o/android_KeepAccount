package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao

import org.greenrobot.eventbus.EventBus

import wxm.androidutil.dbUtil.DBUtilityBase
import wxm.androidutil.util.MD5Util
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil

/**
 * usr数据处理类
 * Created by WangXM on 2016/8/9.
 */
class UsrDBUtility : DBUtilityBase<UsrItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<UsrItem, Int> {
        return ContextUtil.dbHelper.usrItemREDao
    }

    /**
     * 检查是否用户'usr'已经存在
     * @param usr 待检查用户
     * @return 如果存在返回true, 否则返回false
     */
    fun hasUsr(usr: String): Boolean {
        if (UtilFun.StringIsNullOrEmpty(usr))
            return false

        val ret = dbHelper.queryForEq(UsrItem.FIELD_NAME, usr)
        return !(null == ret || ret.size < 1)
    }


    /**
     * 添加用户
     * @param usr 待添加用户名
     * @param pwd 待添加用户密码
     * @return 添加成功返回添加后的数据, 否则返回null
     */
    fun addUsr(usr: String, pwd: String): UsrItem? {
        if (UtilFun.StringIsNullOrEmpty(usr) || UtilFun.StringIsNullOrEmpty(pwd))
            return null

        var pwdpad = pwd
        if (pwdpad.length < GlobalDef.STR_PWD_PAD.length) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length)
        }
        pwdpad = MD5Util.string2MD5(pwdpad)

        var uiret: UsrItem? = null
        val ret = dbHelper.queryForEq(UsrItem.FIELD_NAME, usr)
        if (null == ret || ret.size < 1) {
            val ui = UsrItem()
            ui.name = usr
            ui.pwd = pwdpad

            if (createData(ui))
                uiret = ui
        } else {
            val uiold = ret[0]
            if (pwdpad != uiold.pwd) {
                uiold.pwd = pwdpad
                if (modifyData(uiold))
                    uiret = uiold
            } else {
                uiret = uiold
            }
        }

        return uiret
    }


    /**
     * 检查用户
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回true, 否则返回false
     */
    fun checkUsr(usr: String, pwd: String): Boolean {
        var pwdpad = pwd
        if (pwdpad.length < GlobalDef.STR_PWD_PAD.length) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length)
        }

        val lsui = dbHelper.queryForEq(UsrItem.FIELD_NAME, usr)
        if (null == lsui || lsui.size < 1)
            return false

        val checkPwd = MD5Util.string2MD5(pwdpad)
        return checkPwd == lsui[0].pwd

    }


    /**
     * 检查登录信息，如果有符合的记录就返回对应用户信息
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回注册用户数据, 否则返回null
     */
    fun CheckAndGetUsr(usr: String, pwd: String): UsrItem? {
        var pwdpad = pwd
        if (pwdpad.length < GlobalDef.STR_PWD_PAD.length) {
            pwdpad += GlobalDef.STR_PWD_PAD.substring(pwd.length)
        }

        val lsui = dbHelper.queryForEq(UsrItem.FIELD_NAME, usr)
        if (null == lsui || lsui.size < 1)
            return null

        val checkPwd = MD5Util.string2MD5(pwdpad)
        return if (checkPwd == lsui[0].pwd) lsui[0] else null

    }

    /**
     * 使用用户信息登录APP
     * （并刷新数据)
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果登录成功返回true, 否则返回false
     */
    fun loginByUsr(usr: String, pwd: String): Boolean {
        val ui = CheckAndGetUsr(usr, pwd) ?: return false

        ContextUtil.curUsr = ui
        NoteDataHelper.reloadData()
        return true
    }

    override fun onDataModify(md: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataCreate(cd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }
}
