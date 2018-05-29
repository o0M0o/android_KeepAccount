package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao

import org.greenrobot.eventbus.EventBus

import wxm.KeepAccount.item.RecordTypeItem
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.db.DBUtilityBase

/**
 * for record type data
 * Created by WangXM on 2016/12/2.
 */
class RecordTypeDBUtility : DBUtilityBase<RecordTypeItem, Int>() {

    /**
     * 得到所有支付类类型数据
     *
     * @return 结果数据
     */
    val allPayItem: List<RecordTypeItem>
        get() = dbHelper
                .queryForEq(RecordTypeItem.FIELD_ITEM_TYPE, RecordTypeItem.DEF_PAY)


    /**
     * 得到所有收入类类型数据
     *
     * @return 结果数据
     */
    val allIncomeItem: List<RecordTypeItem>
        get() = dbHelper
                .queryForEq(RecordTypeItem.FIELD_ITEM_TYPE, RecordTypeItem.DEF_INCOME)


    override fun getDBHelper(): RuntimeExceptionDao<RecordTypeItem, Int> {
        return ContextUtil.dbHelper.rtItemREDao
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
