package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.UsrItem
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.util.MD5Util
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.forObj

/**
 * usr数据处理类
 * Created by WangXM on 2016/8/9.
 */
class UsrDBUtility : DBUtilityBase<UsrItem, Int>() {
    override fun onDataModify(md: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataCreate(cd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: List<Int>) {
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun getDBHelper(): RuntimeExceptionDao<UsrItem, Int> {
        return AppUtil.dbHelper.usrItemREDao
    }

    /**
     * 检查是否用户'usr'已经存在
     * @param usr 待检查用户
     * @return 如果存在返回true, 否则返回false
     */
    fun hasUsr(usr: String): Boolean {
        return dbHelper.queryBuilder().setCountOf(true)
                .where().eq(UsrItem.FIELD_NAME, usr).prepare().let {
                    dbHelper.countOf(it)
                } >= 1
    }


    /**
     * 添加用户
     * @param usr 待添加用户名
     * @param pwd 待添加用户密码
     * @return 添加成功返回添加后的数据, 否则返回null
     */
    fun addUsr(usr: String, pwd: String): UsrItem? {
        if (usr.isEmpty() || pwd.isEmpty())
            return null

        if (hasUsr(usr))
            return null

        return UsrItem().apply {
            this.name = usr
            this.pwd = getMd5Pwd(pwd)
        }.let {
            createData(it).doJudge(it, null)
        }
    }


    /**
     * 检查用户
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回true, 否则返回false
     */
    fun checkUsr(usr: String, pwd: String): Boolean {
        val query = dbHelper.queryBuilder().where()
                .eq(UsrItem.FIELD_NAME, usr)
                .and()
                .eq(UsrItem.FIELD_PWD, getMd5Pwd(pwd))
                .prepare()

        return dbHelper.query(query).let {
            null != it && it.isNotEmpty()
        }
    }


    /**
     * 检查登录信息，如果有符合的记录就返回对应用户信息
     * @param usr 待检查用户名
     * @param pwd 待检查用户密码
     * @return 如果符合返回注册用户数据, 否则返回null
     */
    fun checkGetUsr(usr: String, pwd: String): UsrItem? {
        val query = dbHelper.queryBuilder().where()
                .eq(UsrItem.FIELD_NAME, usr)
                .and()
                .eq(UsrItem.FIELD_PWD, getMd5Pwd(pwd))
                .prepare()

        return dbHelper.query(query).let {
            if (null == it || it.isEmpty()) null
            else it[0]
        }
    }

    /**
     * use usr name [usr] and password [pwd] login app
     * and record in history
     *
     * return true if everything ok
     */
    fun loginByUsr(usr: String, pwd: String): Boolean {
        return checkGetUsr(usr, pwd).forObj(
                { loginByUsr(it, true) }, { false })
    }

    /**
     * use usr [usr] login app
     * if [recordHistory] is true, record login history
     *
     * return true if everything ok
     */
    fun loginByUsr(usr: UsrItem, recordHistory: Boolean): Boolean {
        AppUtil.curUsr = usr
        if (recordHistory) LoginHistoryUtility.addHistory(usr)
        return true
    }

    /**
     * change [usr] icon to [fn]
     *
     * return true if everything ok
     */
    fun changeIcon(usr: UsrItem, fn: String): Boolean {
        return dbHelper.updateBuilder().let {
            it.updateColumnValue(UsrItem.FIELD_ICON_PATH, fn)
            it.where().eq(UsrItem.FIELD_ID, usr.id)
            it.update() == 1
        }.doJudge(
                {
                    usr.iconPath = AppUtil.usrUtility.getData(usr.id)!!.iconPath
                    true
                },
                { false }
        )
    }

    /**
     * return true if [pwd] can use as pwd
     */
    fun pwdValidity(pwd: String): Int {
        return (pwd.length > 4).doJudge(RET_OK, RET_PWD_TO_SHORT)
    }

    fun changePwd(usr: UsrItem, pwd: String): Boolean {
        if (RET_OK != pwdValidity(pwd)) {
            return false
        }

        return dbHelper.updateBuilder().let {
            it.updateColumnValue(UsrItem.FIELD_PWD, getMd5Pwd(pwd))
                    .where().eq(UsrItem.FIELD_ID, usr.id)

            it.update() == 1
        }.doJudge(
                {
                    usr.pwd = AppUtil.usrUtility.getData(usr.id)!!.pwd
                    true
                },
                { false }
        )
    }

    /**
     * return [orgPwd] as MD5 string
     */
    private fun getMd5Pwd(orgPwd: String): String {
        return (orgPwd.length < GlobalDef.STR_PWD_PAD.length).doJudge(
                { orgPwd + GlobalDef.STR_PWD_PAD.substring(orgPwd.length) },
                { orgPwd }
        ).let {
            MD5Util.string2MD5(it)
        }
    }

    companion object {
        const val RET_OK = 0
        const val RET_PWD_TO_SHORT = 1
    }
}
