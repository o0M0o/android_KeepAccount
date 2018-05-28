package wxm.KeepAccount.db

import android.content.Context
import com.j256.ormlite.stmt.PreparedQuery
import com.j256.ormlite.stmt.QueryBuilder
import wxm.KeepAccount.define.LoginHistoryItem
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil
import java.sql.Timestamp

/**
 * @author      WangXM
 * @version     create：2018/5/28
 */
object LoginHistoryUtility {
    private fun getREDao() = ContextUtil.dbHelper.loginHistoryREDao
    private fun getUsrREDao() = ContextUtil.dbHelper.usrItemREDao

    fun addHistory(usr:UsrItem) {
        getREDao().create(LoginHistoryItem().apply {
            usrId = usr.id
            loginTime = Timestamp(System.currentTimeMillis())
        })
    }

    fun getLastLoginAfter(ts:Timestamp): UsrItem?  {
        val ls = getREDao().queryBuilder().where().ge(LoginHistoryItem.FIELD_LOGIN_TIME, ts).let {
            getREDao().query(it.prepare())
        } ?: return null

        if(ls.isEmpty())
            return null

        ls.sortBy { it.loginTime }
        return getUsrREDao().queryForEq(UsrItem.FIELD_ID, ls[0].usrId).let {
            if(null == it || it.isEmpty())
                null
            else
                it[0]
        }
    }

    fun cleanHistory()  {
        getREDao().queryForAll().forEach {
            getREDao().deleteById(it.id)
        }
    }
}