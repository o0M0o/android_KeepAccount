package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import wxm.KeepAccount.item.SmsParseItem
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.db.DBUtilityBase

/**
 * @author      WangXM
 * @version     create：2018/5/28
 */
class SmsParseDBUtility : DBUtilityBase<SmsParseItem, Int>() {
    override fun getDBHelper(): RuntimeExceptionDao<SmsParseItem, Int> {
        return AppUtil.dbHelper.smsParseREDao
    }

    override fun onDataCreate(cd: MutableList<Int>?) {
    }

    override fun onDataRemove(dd: MutableList<Int>?) {
    }

    override fun onDataModify(md: MutableList<Int>?) {
    }

    fun clean() {
        removeDatas(allData.map { it.id }.toList())
    }

    fun addParseResult(sId: Long, parseResult: String) {
        createData(SmsParseItem().apply {
            smsId = sId
            parseStatus = parseResult
        })
    }

    fun getParseResult(sId: Long): String {
        val ret = dbHelper.queryForEq(SmsParseItem.FIELD_SMS_ID, sId)
        if(null == ret || ret.isEmpty())
            return SmsParseItem.FIELD_VAL_NONE

        return ret[0].parseStatus
    }
}