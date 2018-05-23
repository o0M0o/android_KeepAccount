package wxm.KeepAccount.db

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.log.TagLog
import wxm.androidutil.util.UtilFun
import java.sql.SQLException
import java.util.*

/**
 * Budget DB utility
 * Created by WangXM on 2016/9/1.
 */
class BudgetDBUtility : DBUtilityBase<BudgetItem, Int>() {
    /**
     * get current usr budget & it's pay data
     * @return      budget with pay data or null
     */
    val budgetWithPayNote: HashMap<BudgetItem, List<PayNoteItem>>
        get() {
            val bret = HashMap<BudgetItem, List<PayNoteItem>>()
            budgetForCurUsr?.forEach { bi ->
                val pi = ContextUtil.payIncomeUtility.getPayNoteByBudget(bi)
                val nbi = getData(bi._id)
                bret[nbi] = pi
            }

            return bret
        }

    /**
     * get current usr budget
     * @return      budget data or null
     */
    val budgetForCurUsr: List<BudgetItem>?
        get() {
            val curUsr = ContextUtil.curUsr ?: return null
            val lsRet = dbHelper.queryForEq(BudgetItem.FIELD_USR, curUsr)
            return if (null == lsRet || 0 == lsRet.size) null else lsRet
        }

    override fun getDBHelper(): RuntimeExceptionDao<BudgetItem, Int> {
        return ContextUtil.dbHelper.budgetDataREDao
    }

    /**
     * use name get budget data
     * @return      budget data or null
     */
    fun getBudgetByName(bn: String): BudgetItem? {
        if (UtilFun.StringIsNullOrEmpty(bn))
            return null

        val curUsr = ContextUtil.curUsr ?: return null

        val ret = try {
            dbHelper.queryBuilder()
                    .where().eq(BudgetItem.FIELD_USR, curUsr.id)
                    .and().eq(BudgetItem.FIELD_NAME, bn).query()
        } catch (e: SQLException) {
            TagLog.e("", e)
            null
        }

        return if (UtilFun.ListIsNullOrEmpty(ret)) null else ret!![0]
    }


    /**
     * create & add budget data
     * @param bi    new budget data
     * @return      true if success
     */
    override fun createData(bi: BudgetItem): Boolean {
        if (null == bi.usr || -1 == bi.usr!!.id) {
            val curUsr = ContextUtil.curUsr ?: return false

            bi.usr = curUsr
        }

        return super.createData(bi)
    }

    /**
     * delete budget & relation data
     * @param lsBiId    ids for budget
     * @return          removed budget data count
     */
    override fun removeDatas(lsBiId: List<Int>): Int {
        lsBiId.forEach {
            ContextUtil.dbHelper.payDataREDao.queryForEq(PayNoteItem.FIELD_BUDGET, it)
                .apply {
                    forEach {
                        it.budget = null
                        ContextUtil.dbHelper.payDataREDao.update(it)
                    }
                }
        }

        return super.removeDatas(lsBiId)
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
