package wxm.KeepAccount.utility

import android.app.Application
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.util.Log

import wxm.KeepAccount.db.BudgetDBUtility
import wxm.KeepAccount.db.DBOrmLiteHelper
import wxm.KeepAccount.db.PayIncomeDBUtility
import wxm.KeepAccount.db.RecordTypeDBUtility
import wxm.KeepAccount.db.RemindDBUtility
import wxm.KeepAccount.db.UsrDBUtility
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.define.RemindItem
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.ui.utility.NoteDataHelper

/**
 * get global context & helper
 * Created by WangXM on 2016/5/7.
 */
class ContextUtil : Application() {
    // data for global use
    private var mUICurUsr: UsrItem? = null
    private var mMHHandler: GlobalMsgHandler? = null

    // mainly for sqlite
    private var mDBHelper: DBOrmLiteHelper? = null
    private var mUsrUtility: UsrDBUtility? = null
    private var mRecordTypeUtility: RecordTypeDBUtility? = null
    private var mBudgetUtility: BudgetDBUtility? = null
    private var mPayIncomeUtility: PayIncomeDBUtility? = null
    private var mRemindUtility: RemindDBUtility? = null

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        instance = this
        mMHHandler = GlobalMsgHandler()

        // for db
        mDBHelper = DBOrmLiteHelper(instance!!)

        mUsrUtility = UsrDBUtility()
        mRecordTypeUtility = RecordTypeDBUtility()
        mBudgetUtility = BudgetDBUtility()
        mPayIncomeUtility = PayIncomeDBUtility()
        mRemindUtility = RemindDBUtility()
    }

    override fun onTerminate() {
        super.onTerminate()

        // for db
        if (null != mDBHelper) {
            mDBHelper!!.close()
            mDBHelper = null
        }

        mUsrUtility = null
        mRecordTypeUtility = null
        mBudgetUtility = null
        mPayIncomeUtility = null
        mRemindUtility = null
    }

    companion object {
        private val LOG_TAG = ::ContextUtil.javaClass.simpleName

        /**
         * get global context
         * @return      application context
         */
        var instance: ContextUtil? = null
            private set

        /**
         * get global msg handler
         * @return      msg handler
         */
        val msgHandler: GlobalMsgHandler
            get() = instance!!.mMHHandler!!

        /**
         * get DB helper
         * @return      helper
         */
        val dbHelper: DBOrmLiteHelper
            get() = instance!!.mDBHelper!!

        /**
         * get current usr
         * @return      current usr
         */
        /**
         * set current usr
         * @param cur_usr   current usr
         */
        var curUsr: UsrItem?
            get() = instance!!.mUICurUsr
            set(cur_usr) {
                instance!!.mUICurUsr = cur_usr
            }

        /**
         * get usr db helper
         * @return      usr db helper
         */
        val usrUtility: UsrDBUtility
            get() = instance!!.mUsrUtility!!

        /**
         * get record type db helper
         * @return      helper
         */
        val recordTypeUtility: RecordTypeDBUtility
            get() = instance!!.mRecordTypeUtility!!

        /**
         * get budget helper
         * @return      helper
         */
        val budgetUtility: BudgetDBUtility
            get() = instance!!.mBudgetUtility!!

        /**
         * get pay & income data helper
         * @return      helper
         */
        val payIncomeUtility: PayIncomeDBUtility
            get() = instance!!.mPayIncomeUtility!!

        /**
         * get remind data helper
         * @return      helper
         */
        val remindUtility: RemindDBUtility?
            get() = instance!!.mRemindUtility

        /**
         * clean db
         */
        fun clearDB() {
            try {
                val ui = curUsr
                if (null != ui) {
                    val dh = instance!!.mDBHelper
                    val uid = ui.id

                    val dbPay = dh!!.payDataREDao.deleteBuilder()
                    dbPay.where().eq(PayNoteItem.FIELD_USR, uid)
                    dbPay.delete()

                    val dbIncome = dh.incomeDataREDao.deleteBuilder()
                    dbIncome.where().eq(IncomeNoteItem.FIELD_USR, uid)
                    dbIncome.delete()

                    val dbBudget = dh.budgetDataREDao.deleteBuilder()
                    dbBudget.where().eq(BudgetItem.FIELD_USR, uid)
                    dbBudget.delete()

                    val dbRemind = dh.remindREDao.deleteBuilder()
                    dbRemind.where().eq(RemindItem.FIELD_USR, uid)
                    dbRemind.delete()

                    NoteDataHelper.instance.refreshData()
                }
            } catch (e: java.sql.SQLException) {
                Log.e(LOG_TAG, "clearDB catch an exception", e)
            }
        }

        /**
         * get res string
         */
        fun getString(@StringRes resId : Int): String   {
            return instance!!.getString(resId)
        }

        @ColorInt
        fun getColor(@ColorRes resId : Int): Int  {
            return instance!!.getColor(resId)
        }
    }
}
