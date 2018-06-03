package wxm.KeepAccount.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import wxm.KeepAccount.R
import wxm.KeepAccount.db.*
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.item.RemindItem
import wxm.KeepAccount.item.UsrItem
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.androidutil.app.AppBase
import wxm.androidutil.log.TagLog
import java.io.File

/**
 * get global context & helper
 * Created by WangXM on 2016/5/7.
 */
class AppUtil : AppBase() {
    // data for global use
    private var mUICurUsr: UsrItem? = null
    private lateinit var mMHHandler: GlobalMsgHandler

    // for sqlite
    private lateinit var mDBHelper: DBOrmLiteHelper
    private lateinit var mUsrUtility: UsrDBUtility
    private lateinit var mRecordTypeUtility: RecordTypeDBUtility
    private lateinit var mBudgetUtility: BudgetDBUtility
    private lateinit var mPayIncomeUtility: PayIncomeDBUtility
    private lateinit var mRemindUtility: RemindDBUtility

    // for dir
    private lateinit var mImageDir: String

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()

        initMsgHandler()
        initDB()
        initDir()
    }

    override fun onTerminate() {
        super.onTerminate()

        closeDB()
    }

    private fun initMsgHandler()    {
        mMHHandler = GlobalMsgHandler()
    }

    private fun initDB()    {
        mDBHelper = DBOrmLiteHelper(appContext())

        mUsrUtility = UsrDBUtility()
        mRecordTypeUtility = RecordTypeDBUtility()
        mBudgetUtility = BudgetDBUtility()
        mPayIncomeUtility = PayIncomeDBUtility()
        mRemindUtility = RemindDBUtility()
    }

    private fun initDir()   {
        val rootDir = filesDir
        val imagePath = "$rootDir/image"
        createDirIfNotExist(imagePath).let1 {
            mImageDir = if (it) imagePath else rootDir.path
        }

        File(defaultUsrIcon()).let1 {
            if (!it.exists()) {
                BitmapFactory.decodeResource(resources, R.drawable.image_default_usr).let1 {
                    saveBitmapToJPGFile(it, defaultUsrIcon(), Bitmap.CompressFormat.PNG)
                }
            }
        }
    }

    private fun closeDB()   {
        mDBHelper.close()
    }

    private fun createDirIfNotExist(pn:String): Boolean   {
        return File(pn).let {
            if (!it.exists()) {
                it.mkdirs()
                it.exists()
            } else true
        }
    }


    companion object {
        val self: AppUtil
            get() = (appContext() as AppUtil)

        val imagePath: String
            get() = self.mImageDir

        /**
         * get global msg handler
         *
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
