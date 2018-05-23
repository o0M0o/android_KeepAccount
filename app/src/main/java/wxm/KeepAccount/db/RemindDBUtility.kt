package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao

import org.greenrobot.eventbus.EventBus

import java.sql.SQLException

import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.RemindItem
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.db.DBUtilityBase

/**
 * 提醒数据辅助类
 * Created by WangXM on 2016/10/9.
 */
class RemindDBUtility : DBUtilityBase<RemindItem, Int>() {

    /**
     * 返回当前用户查找RemindItem数据
     *
     * @return 返回数据, 无数据返回`NULL`
     */
    val allRemind: List<RemindItem>?
        get() {
            val cur_usr = ContextUtil.curUsr ?: return null

            return dbHelper.queryForEq(RemindItem.FIELD_USR, cur_usr.id)
        }

    override fun getDBHelper(): RuntimeExceptionDao<RemindItem, Int> {
        return ContextUtil.dbHelper.remindREDao
    }

    /**
     * 检查新提醒名字是否重名
     *
     * @param name 待检查新名字
     * @return 如果重名返回true, 否则返回false
     */
    fun CheckRemindName(name: String): Boolean {
        val cur_usr = ContextUtil.curUsr ?: return true

        var r: Boolean
        try {
            val ret = dbHelper.queryBuilder()
                    .where().eq(RemindItem.FIELD_USR, cur_usr.id)
                    .and().eq(RemindItem.FIELD_NAME, name).query()
            r = 0 < ret.size
        } catch (e: SQLException) {
            e.printStackTrace()
            r = true
        }

        return r
    }

    /**
     * 添加/更新提醒数据
     *
     * @param ri 提醒数据（可以是新数据也可以是更新后的现有数据）
     * @return 操作成功返回true, 否则返回false
     */
    fun AddOrUpdateRemind(ri: RemindItem): Boolean {
        if (null == ri.usr) {
            val cur_usr = ContextUtil.curUsr ?: return false

            ri.usr = cur_usr
        }

        val r: Boolean
        if (GlobalDef.INVALID_ID == ri.id)
            r = 1 == dbHelper.create(ri)
        else
            r = 1 == dbHelper.update(ri)

        return r
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
