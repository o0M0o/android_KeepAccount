package wxm.KeepAccount.db

import android.util.Log

import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus

import java.sql.SQLException
import java.util.HashMap

import wxm.androidutil.DBHelper.DBUtilityBase
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil

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

        val cur_usr = ContextUtil.curUsr ?: return null

        var ret: List<BudgetItem>?
        try {
            ret = dbHelper.queryBuilder()
                    .where().eq(BudgetItem.FIELD_USR, cur_usr.id)
                    .and().eq(BudgetItem.FIELD_NAME, bn).query()
        } catch (e: SQLException) {
            Log.e(LOG_TAG, UtilFun.ExceptionToString(e))
            ret = null
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
            val cur_usr = ContextUtil.curUsr ?: return false

            bi.usr = cur_usr
        }

        return super.createData(bi)
    }

    /**
     * delete budget & relation data
     * @param lsBiId    ids for budget
     * @return          removed budget data count
     */
    override fun removeDatas(lsBiId: List<Int>): Int {
        if (!UtilFun.ListIsNullOrEmpty(lsBiId)) {
            for (id in lsBiId) {
                val ls_pay = ContextUtil.dbHelper.payDataREDao
                        .queryForEq(PayNoteItem.FIELD_BUDGET, id)
                if (!UtilFun.ListIsNullOrEmpty(ls_pay)) {
                    for (i in ls_pay) {
                        i.budget = null
                        ContextUtil.dbHelper.payDataREDao.update(i)
                    }
                }
            }

            return super.removeDatas(lsBiId)
        }

        return 0
    }

    override fun onDataModify(md: List<Int>) {
        //NoteDataHelper.reloadData()
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataCreate(cd: List<Int>) {
        //NoteDataHelper.reloadData()
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    override fun onDataRemove(dd: List<Int>) {
        //NoteDataHelper.reloadData()
        EventBus.getDefault().post(DBDataChangeEvent())
    }

    companion object {
        private val LOG_TAG = ::BudgetDBUtility.javaClass.simpleName
    }
}
