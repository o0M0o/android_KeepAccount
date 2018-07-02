package wxm.KeepAccount.db


import com.j256.ormlite.dao.RuntimeExceptionDao
import org.greenrobot.eventbus.EventBus
import wxm.KeepAccount.base.IImageDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.item.*
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.AppUtil
import wxm.androidutil.improve.forObj
import wxm.androidutil.improve.let1
import wxm.androidutil.util.UtilFun
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

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
        val lsPay = AppUtil.dbHelper.payDataREDao
                .queryForEq(PayNoteItem.FIELD_BUDGET, bi._id)
        bi.useBudget(BigDecimal.ZERO)
        if (!UtilFun.ListIsNullOrEmpty(lsPay)) {
            var allPay = BigDecimal.ZERO
            lsPay.forEach {
                allPay = allPay.add(it.amount)
            }

            bi.useBudget(allPay)
        }

        AppUtil.dbHelper.budgetDataREDao.update(bi)
        return lsPay
    }

    /**
     * add 'pay' record
     * @param lsi   need add record
     * @return      added record count
     */
    fun addPayNotes(lsi: List<PayNoteItem>): Int {
        return AppUtil.curUsr.forObj({ t ->
            lsi.filter { null == it.usr }.forEach {
                it.usr = t
            }
            payDBUtility.createDatas(lsi)
        }, {
            if (null == lsi.find { null == it.usr }) {
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
        return AppUtil.curUsr.forObj(
                { t ->
                    lsi.filter { null == it.usr }
                            .forEach { it.usr = t }

                    incomeDBUtility.createDatas(lsi)
                },
                {
                    if (null == lsi.find { null == it.usr }) {
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
     * delete notes in [lsn]
     */
    fun deleteNotes(lsn: List<INote>)   {
        lsn.filter { it is PayNoteItem }.map { it.id }.toList().let1 {
            if(it.isNotEmpty()) {
                payDBUtility.removeDatas(it)
            }
        }

        lsn.filter { it is IncomeNoteItem }.map { it.id }.toList().let1 {
            if(it.isNotEmpty()) {
                incomeDBUtility.removeDatas(it)
            }
        }
    }


    /**
     * pay data helper
     */
    inner class PayDBUtility internal constructor() : IImageDBUtility<PayNoteItem, Int>() {
        override fun getDBHelper(): RuntimeExceptionDao<PayNoteItem, Int> {
            return AppUtil.dbHelper.payDataREDao
        }

        override fun getAllData(): List<PayNoteItem> {
            return AppUtil.curUsr.forObj(
                    { t -> dbHelper.queryForEq(INote.FIELD_USR, t.id) },
                    { ArrayList() }
            ).apply {
                filterNotNull().forEach{
                    getItemChore(it)
                }
            }
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
    inner class IncomeDBUtility internal constructor() : IImageDBUtility<IncomeNoteItem, Int>() {
        override fun getDBHelper(): RuntimeExceptionDao<IncomeNoteItem, Int> {
            return AppUtil.dbHelper.incomeDataREDao
        }

        override fun getAllData(): List<IncomeNoteItem> {
            return AppUtil.curUsr.forObj(
                    { t -> dbHelper.queryForEq(INote.FIELD_USR, t.id) },
                    { ArrayList() }
            ).apply {
                filterNotNull().forEach{
                    getItemChore(it)
                }
            }
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

    companion object {
        val instance = PayIncomeDBUtility()

        /**
         * use [word] as key-word, [filter] as columns
         * search INote for current user
         */
        fun doSearch(word:String, type:Array<String>, filter:Array<String>): List<INote>    {
            if(filter.isEmpty())
                return ArrayList()

            return ArrayList<INote>().apply {
                if(type.contains(GlobalDef.STR_RECORD_PAY)) {
                    addAll(doSearchChore(AppUtil.dbHelper.payDataREDao, word, filter))
                }

                if(type.contains(GlobalDef.STR_RECORD_INCOME)) {
                    addAll(doSearchChore(AppUtil.dbHelper.incomeDataREDao, word, filter))
                }

                sortBy { it.ts }
            }
        }

        /**
         * do search chore
         * [helper] is db
         * [word] is search key-word
         * [filter] is search columns
         */
        private fun<T, ID> doSearchChore(helper:RuntimeExceptionDao<T, ID>, word:String, filter:Array<String>)
                : List<T>     {
            val stmt = helper.queryBuilder()
            val stmtWhere= stmt.where().eq(INote.FIELD_USR, AppUtil.curUsr!!.id)

            var orCount = 0
            filter.forEach {
                if(it == INote.FIELD_AMOUNT)    {
                    try {
                        val v = BigDecimal(it)
                        stmtWhere.eq(it, v)
                        orCount += 1
                    } catch (e:NumberFormatException)   {
                        // do nothing
                    }
                } else {
                    stmtWhere.like(it, "%$word%")
                    orCount += 1
                }
            }

            if(orCount > 1) {
                stmtWhere.or(orCount)
                stmtWhere.and(2)
            } else if(orCount == 1) {
                stmtWhere.and(2)
            }

            return helper.query(stmt.prepare())
        }
    }
}


