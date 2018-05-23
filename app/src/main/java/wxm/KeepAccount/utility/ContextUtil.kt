package wxm.KeepAccount.utility

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.*
import wxm.KeepAccount.db.*
import wxm.KeepAccount.define.*
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.androidutil.app.AppBase
import wxm.androidutil.log.TagLog

/**
 * get global context & helper
 * Created by WangXM on 2016/5/7.
 */
class ContextUtil : AppBase() {
    // data for global use
    private var mUICurUsr: UsrItem? = null
    private lateinit var mMHHandler: GlobalMsgHandler

    // mainly for sqlite
    private lateinit var mDBHelper: DBOrmLiteHelper
    private lateinit var mUsrUtility: UsrDBUtility
    private lateinit var mRecordTypeUtility: RecordTypeDBUtility
    private lateinit var mBudgetUtility: BudgetDBUtility
    private lateinit var mPayIncomeUtility: PayIncomeDBUtility
    private lateinit var mRemindUtility: RemindDBUtility

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        mMHHandler = GlobalMsgHandler()

        // for db
        mDBHelper = DBOrmLiteHelper(appContext())

        mUsrUtility = UsrDBUtility()
        mRecordTypeUtility = RecordTypeDBUtility()
        mBudgetUtility = BudgetDBUtility()
        mPayIncomeUtility = PayIncomeDBUtility()
        mRemindUtility = RemindDBUtility()
    }

    override fun onTerminate() {
        super.onTerminate()

        // for db
        mDBHelper.close()
    }

    companion object {
        val self: ContextUtil
            get() = (appContext() as ContextUtil)

        /**
         * get global msg handler
         * @return      msg handler
         */
        val msgHandler: GlobalMsgHandler
            get() = self.mMHHandler

        /**
         * get DB helper
         * @return      helper
         */
        val dbHelper: DBOrmLiteHelper
            get() = self.mDBHelper

        /**
         * get current usr
         * @return      current usr
         */
        /**
         * set current usr
         * @param cur_usr   current usr
         */
        var curUsr: UsrItem?
            get() = self.mUICurUsr
            set(cur_usr) {
                self.mUICurUsr = cur_usr
            }

        /**
         * get usr db helper
         * @return      usr db helper
         */
        val usrUtility: UsrDBUtility
            get() = self.mUsrUtility

        /**
         * get record type db helper
         * @return      helper
         */
        val recordTypeUtility: RecordTypeDBUtility
            get() = self.mRecordTypeUtility

        /**
         * get budget helper
         * @return      helper
         */
        val budgetUtility: BudgetDBUtility
            get() = self.mBudgetUtility

        /**
         * get pay & income data helper
         * @return      helper
         */
        val payIncomeUtility: PayIncomeDBUtility
            get() = self.mPayIncomeUtility

        /**
         * get remind data helper
         * @return      helper
         */
        val remindUtility: RemindDBUtility
            get() = self.mRemindUtility

        /**
         * clean db
         */
        fun clearDB() {
            try {
                curUsr?.let {
                    val uid = it.id
                    self.mDBHelper.let {
                        it.payDataREDao.deleteBuilder().apply {
                            where().eq(PayNoteItem.FIELD_USR, uid)
                            delete()
                        }

                        it.incomeDataREDao.deleteBuilder().apply {
                            where().eq(IncomeNoteItem.FIELD_USR, uid)
                            delete()
                        }

                        it.budgetDataREDao.deleteBuilder().apply {
                            where().eq(BudgetItem.FIELD_USR, uid)
                            delete()
                        }

                        it.remindREDao.deleteBuilder().apply {
                            where().eq(RemindItem.FIELD_USR, uid)
                            delete()
                        }

                        Unit
                    }

                    NoteDataHelper.reloadData()
                }
            } catch (e: java.sql.SQLException) {
                TagLog.e("clearDB catch an exception", e)
            }
        }
    }
}
