package wxm.KeepAccount.db


import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.INote
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.db.DBUtilityBase
import wxm.androidutil.util.UtilFun
import wxm.androidutil.util.forObj
import java.math.BigDecimal
import java.util.*

/**
 * 备忘本数据库工具类
 * Created by WangXM on 2016/11/16.
 */
class PayIncomeDBUtility {
    /**
     * pay data helper
     */
    val payDBUtility = PayDBUtility()

    /**
     * income data helper
     */
    val incomeDBUtility = IncomeDBUtility()

    /**
     * current usr all pay/income data
     */
    val allNotes: List<INote>
        get() {
            return LinkedList<INote>().apply {
                addAll(payDBUtility.allData)
                addAll(incomeDBUtility.allData)
            }
        }

    /**
     * use budget get it's pay data
     * @param bi    budget
     * @return      pay data use bi
     */
    fun getPayNoteByBudget(bi: BudgetItem): List<PayNoteItem> {
        val lsPay = ContextUtil.dbHelper.payDataREDao
                .queryForEq(PayNoteItem.FIELD_BUDGET, bi._id)
        bi.useBudget(BigDecimal.ZERO)
        if (!UtilFun.ListIsNullOrEmpty(lsPay)) {
            var allPay = BigDecimal.ZERO
            lsPay.forEach {
                allPay = allPay.add(it.amount)

                // this for update string
                it.amount = it.amount
                it.ts = it.ts
            }

            bi.useBudget(allPay)
        }

        ContextUtil.dbHelper.budgetDataREDao.update(bi)
        return lsPay
    }

    /**
     * add 'pay' record
     * @param lsi   need add record
     * @return      added record count
     */
    fun addPayNotes(lsi: List<PayNoteItem>): Int {
        return ContextUtil.curUsr.forObj({ t ->
            lsi.filter { null == it.usr }.forEach {
                it.usr = t
            }
            payDBUtility.createDatas(lsi)
        }, {
            if (null != lsi.find { null == it.usr }) {
                payDBUtility.createDatas(lsi)
            } else 0
        })
    }

    /**
     * 添加收入记录
     *
     * @param lsi 待添加的记录集合
     * @return 返回添加成功的数据量
     */
    fun addIncomeNotes(lsi: List<IncomeNoteItem>): Int {
        return ContextUtil.curUsr.forObj(
                { t ->
                    lsi.filter { null == it.usr }.forEach {
                        it.usr = t
                    }
                    incomeDBUtility.createDatas(lsi)
                },
                {
                    var nousr = false
                    for (i in lsi) {
                        if (null == i.usr) {
                            nousr = true
                            break
                        }
                    }

                    if (null != lsi.find { null == it.usr }) {
                        incomeDBUtility.createDatas(lsi)
                    } else 0
                }
        )
    }

    /**
     * delete pay
     * @param lsi   id for pay
     */
    fun deletePayNotes(lsi: List<Int>) {
        payDBUtility.removeDatas(lsi)
    }

    /**
     * delete income
     * @param lsi   id for income
     */
    fun deleteIncomeNotes(lsi: List<Int>) {
        incomeDBUtility.removeDatas(lsi)
    }

    /**
     * pay data helper
     */
    inner class PayDBUtility internal constructor() : DBUtilityBase<PayNoteItem, Int>() {
        override fun getDBHelper(): RuntimeExceptionDao<PayNoteItem, Int> {
            return ContextUtil.dbHelper.payDataREDao
        }

        override fun getData(id: Int?): PayNoteItem? {
            return super.getData(id).let {
                if(null != it)  updateNote(it)
                it
            }
        }

        override fun getAllData(): List<PayNoteItem> {
            val ui = ContextUtil.curUsr ?: return ArrayList()
            val ret = dbHelper.queryForEq(PayNoteItem.FIELD_USR, ui.id)
            for (it in ret) {
                updateNote(it)
            }
            return ret
        }

        override fun onDataModify(md: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        override fun onDataCreate(cd: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        override fun onDataRemove(dd: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        /**
         * update pay data
         * @param pi   pay data
         */
        private fun updateNote(pi: PayNoteItem) {
            pi.amount = pi.amount
            pi.ts = pi.ts
        }
    }

    /**
     * income data helper
     */
    inner class IncomeDBUtility internal constructor() : DBUtilityBase<IncomeNoteItem, Int>() {
        override fun getDBHelper(): RuntimeExceptionDao<IncomeNoteItem, Int> {
            return ContextUtil.dbHelper.incomeDataREDao
        }

        override fun getData(id: Int?): IncomeNoteItem? {
            val pi = super.getData(id)
            if (null != pi) {
                updateNote(pi)
            }

            return pi
        }

        override fun getAllData(): List<IncomeNoteItem> {
            val ui = ContextUtil.curUsr ?: return ArrayList()
            val ret = dbHelper.queryForEq(PayNoteItem.FIELD_USR, ui.id)
            for (it in ret) {
                updateNote(it)
            }
            return ret
        }

        override fun onDataModify(md: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        override fun onDataCreate(cd: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        override fun onDataRemove(dd: List<Int>) {
            NoteDataHelper.reloadData()
            EventBus.getDefault().post(DBDataChangeEvent())
        }

        /**
         * update income data
         * @param ii   data
         */
        private fun updateNote(ii: IncomeNoteItem) {
            ii.amount = ii.amount
            ii.ts = ii.ts
        }
    }
}


