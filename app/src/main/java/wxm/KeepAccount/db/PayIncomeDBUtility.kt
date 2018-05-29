package wxm.KeepAccount.db


import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
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
                    lsi.filter { null == it.usr }
                            .forEach { it.usr = t }

                    incomeDBUtility.createDatas(lsi)
                },
                {
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

        override fun getAllData(): List<PayNoteItem> {
            return ContextUtil.curUsr.forObj(
                    { t -> dbHelper.queryForEq(PayNoteItem.FIELD_USR, t.id) },
                    { ArrayList() }
            )
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
    }

    /**
     * income data helper
     */
    inner class IncomeDBUtility internal constructor() : DBUtilityBase<IncomeNoteItem, Int>() {
        override fun getDBHelper(): RuntimeExceptionDao<IncomeNoteItem, Int> {
            return ContextUtil.dbHelper.incomeDataREDao
        }

        override fun getAllData(): List<IncomeNoteItem> {
            return ContextUtil.curUsr.forObj(
                    { t -> dbHelper.queryForEq(IncomeNoteItem.FIELD_USR, t.id) },
                    { ArrayList() }
            )
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
    }
}


