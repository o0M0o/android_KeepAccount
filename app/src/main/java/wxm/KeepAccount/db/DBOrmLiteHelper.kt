package wxm.KeepAccount.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

import java.math.BigDecimal
import java.sql.SQLException
import java.util.Date
import java.util.LinkedList
import java.util.Random

import wxm.KeepAccount.BuildConfig
import wxm.KeepAccount.R
import wxm.KeepAccount.define.BudgetItem
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.define.RecordTypeItem
import wxm.KeepAccount.define.RemindItem
import wxm.KeepAccount.define.UsrItem
import wxm.KeepAccount.utility.ContextUtil

/**
 * db ormlite helper
 * Created by WangXM on 2016/8/5.
 */
class DBOrmLiteHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // the DAO object we use to access the SimpleData table
    private var mUsrNoteRDao: RuntimeExceptionDao<UsrItem, Int>? = null
    private var mRTNoteRDao: RuntimeExceptionDao<RecordTypeItem, Int>? = null
    private var mBudgetNoteRDao: RuntimeExceptionDao<BudgetItem, Int>? = null
    private var mPayNoteRDao: RuntimeExceptionDao<PayNoteItem, Int>? = null
    private var mIncomeNoteRDao: RuntimeExceptionDao<IncomeNoteItem, Int>? = null
    private var mRemindRDao: RuntimeExceptionDao<RemindItem, Int>? = null

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    val usrItemREDao: RuntimeExceptionDao<UsrItem, Int>
        get() {
            if (mUsrNoteRDao == null) {
                mUsrNoteRDao = getRuntimeExceptionDao(UsrItem::class.java)
            }
            return mUsrNoteRDao!!
        }

    val rtItemREDao: RuntimeExceptionDao<RecordTypeItem, Int>
        get() {
            if (mRTNoteRDao == null) {
                mRTNoteRDao = getRuntimeExceptionDao(RecordTypeItem::class.java)
            }
            return mRTNoteRDao!!
        }

    val budgetDataREDao: RuntimeExceptionDao<BudgetItem, Int>
        get() {
            if (mBudgetNoteRDao == null) {
                mBudgetNoteRDao = getRuntimeExceptionDao(BudgetItem::class.java)
            }
            return mBudgetNoteRDao!!
        }

    val payDataREDao: RuntimeExceptionDao<PayNoteItem, Int>
        get() {
            if (mPayNoteRDao == null) {
                mPayNoteRDao = getRuntimeExceptionDao(PayNoteItem::class.java)
            }
            return mPayNoteRDao!!
        }

    val incomeDataREDao: RuntimeExceptionDao<IncomeNoteItem, Int>
        get() {
            if (mIncomeNoteRDao == null) {
                mIncomeNoteRDao = getRuntimeExceptionDao(IncomeNoteItem::class.java)
            }
            return mIncomeNoteRDao!!
        }

    val remindREDao: RuntimeExceptionDao<RemindItem, Int>
        get() {
            if (mRemindRDao == null) {
                mRemindRDao = getRuntimeExceptionDao(RemindItem::class.java)
            }
            return mRemindRDao!!
        }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    override fun onCreate(db: SQLiteDatabase, connectionSource: ConnectionSource) {
        createAndInitTable()
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    override fun onUpgrade(db: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        try {
            // 版本9中budgetitem表结构有调整
            // 删除旧表，创建新表，然后把旧数据导入新表
            if (9 == newVersion && (8 == oldVersion || 7 == oldVersion)) {
                val lsBi = budgetDataREDao.queryForAll()
                TableUtils.dropTable<BudgetItem, Any>(connectionSource, BudgetItem::class.java, true)
                TableUtils.createTable(connectionSource, BudgetItem::class.java)
                budgetDataREDao.create(lsBi)
            }

            if (8 == newVersion || 7 == newVersion) {
                TableUtils.dropTable<UsrItem, Any>(connectionSource, UsrItem::class.java, true)
                TableUtils.dropTable<RecordTypeItem, Any>(connectionSource, RecordTypeItem::class.java, true)
                TableUtils.dropTable<PayNoteItem, Any>(connectionSource, PayNoteItem::class.java, true)
                TableUtils.dropTable<IncomeNoteItem, Any>(connectionSource, IncomeNoteItem::class.java, true)
                TableUtils.dropTable<BudgetItem, Any>(connectionSource, BudgetItem::class.java, true)
                TableUtils.dropTable<RemindItem, Any>(connectionSource, RemindItem::class.java, true)

                onCreate(db, connectionSource)
            }
        } catch (e: SQLException) {
            Log.e(LOG_TAG, "Can't upgrade databases", e)
            throw RuntimeException(e)
        }

    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    override fun close() {
        super.close()
        mUsrNoteRDao = null
        mRTNoteRDao = null
        mBudgetNoteRDao = null
        mPayNoteRDao = null
        mIncomeNoteRDao = null
        mRemindRDao = null
    }

    private fun createAndInitTable() {
        try {
            TableUtils.createTable(connectionSource, UsrItem::class.java)
            TableUtils.createTable(connectionSource, RecordTypeItem::class.java)
            TableUtils.createTable(connectionSource, BudgetItem::class.java)
            TableUtils.createTable(connectionSource, PayNoteItem::class.java)
            TableUtils.createTable(connectionSource, IncomeNoteItem::class.java)
            TableUtils.createTable(connectionSource, RemindItem::class.java)
        } catch (e: SQLException) {
            Log.e(LOG_TAG, "Can't create database", e)
            throw RuntimeException(e)
        }

        // 添加recordtype
        val reDao = rtItemREDao
        val res = ContextUtil.instance!!.resources
        var type = res.getStringArray(R.array.payinfo)
        for (ln in type) {
            val ri = line2item(RecordTypeItem.DEF_PAY, ln)
            reDao.create(ri)
        }

        type = res.getStringArray(R.array.incomeinfo)
        for (ln in type) {
            val ri = line2item(RecordTypeItem.DEF_INCOME, ln)
            reDao.create(ri)
        }

        // 添加默认用户
        ContextUtil.usrUtility.addUsr(GlobalDef.DEF_USR_NAME, GlobalDef.DEF_USR_PWD)

        if (BuildConfig.FILL_TESTDATA)
            AddTestData()
    }


    /**
     * 把字符串(生活费-生活开销)转换为item
     *
     * @param type 记录类型的类型
     * @param ln   待转换字符串
     * @return 记录类型数据
     */
    private fun line2item(type: String, ln: String): RecordTypeItem {
        val sln = ln.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (BuildConfig.DEBUG && 2 != sln.size) {
            throw AssertionError()
        }

        val ri = RecordTypeItem()
        ri.itemType = type
        ri.type = sln[0]
        ri.note = sln[1]
        return ri
    }


    /**
     * 填充测试数据
     */
    private fun AddTestData() {
        val ui_wxm = ContextUtil.usrUtility.addUsr("wxm", "123456")
        val ui_hugo = ContextUtil.usrUtility.addUsr("hugo", "123456")

        // for wxm
        val lsPay = LinkedList<PayNoteItem>()
        val de = Date()
        var payIt = PayNoteItem()
        payIt.usr = ui_wxm
        payIt.info = "tax"
        payIt.amount = BigDecimal(12.34)
        payIt.ts.time = de.time
        lsPay.add(payIt)

        payIt = PayNoteItem()
        payIt.usr = ui_wxm
        payIt.info = "water cost"
        payIt.amount = BigDecimal(12.34)
        payIt.ts.time = de.time
        lsPay.add(payIt)

        payIt = PayNoteItem()
        payIt.usr = ui_wxm
        payIt.info = "electrcity  cost"
        payIt.amount = BigDecimal(12.34)
        payIt.ts.time = de.time
        lsPay.add(payIt)
        ContextUtil.payIncomeUtility.addPayNotes(lsPay)

        val lsIncome = LinkedList<IncomeNoteItem>()
        val incomeIt = IncomeNoteItem()
        incomeIt.usr = ui_wxm
        incomeIt.info = "工资"
        incomeIt.amount = BigDecimal(12.34)
        incomeIt.ts.time = de.time
        lsIncome.add(incomeIt)
        ContextUtil.payIncomeUtility.addIncomeNotes(lsIncome)

        // for hugo
        for (i in lsPay) {
            i.id = GlobalDef.INVALID_ID
            i.usr = ui_hugo
        }
        ContextUtil.payIncomeUtility.addPayNotes(lsPay)

        for (i in lsIncome) {
            i.id = GlobalDef.INVALID_ID
            i.usr = ui_hugo
        }
        ContextUtil.payIncomeUtility.addIncomeNotes(lsIncome)

        addTestDataForDefualtUsr()
    }

    /**
     * 产生测试数据
     */
    private fun addTestDataForDefualtUsr() {
        class CreateUtility {
            val mSZPayInfo = arrayOf("租金", "生活费", "交通费", "社交开支", "娱乐开支", "其它")
            val mSZIncomeInfo = arrayOf("工资", "投资收入", "营业收入", "奖金", "其它")

            val payInfo: String
                get() = mSZPayInfo[Random().nextInt(mSZPayInfo.size)]

            val incomeInfo: String
                get() = mSZIncomeInfo[Random().nextInt(mSZIncomeInfo.size)]

            fun getVal(max_val: Double, min_val: Double): BigDecimal {
                return BigDecimal(Math.max(Random().nextFloat() * max_val, min_val))
            }
        }

        val ci = CreateUtility()
        val defUi = ContextUtil.usrUtility
                .CheckAndGetUsr(GlobalDef.DEF_USR_NAME, GlobalDef.DEF_USR_PWD)
        if (null != defUi) {
            val oneDayMSec = (1000 * 3600 * 24).toLong()
            val beforeMSec = oneDayMSec * 600

            val curDt = Date()
            val startDt = Date(curDt.time - beforeMSec)
            val endDt = Date()

            while (startDt.before(endDt)) {
                val r = Random().nextInt(99)
                if (0 == r % 3) {
                    val rand1 = Random()
                    val pay = rand1.nextInt(5)
                    if (0 < pay) {
                        val payMax = 500.0
                        val payMin = 0.1
                        val lsPay = LinkedList<PayNoteItem>()
                        for (i in 0 until pay) {
                            val payIt = PayNoteItem()
                            payIt.usr = defUi
                            payIt.info = ci.payInfo
                            payIt.amount = ci.getVal(payMax, payMin)
                            payIt.ts.time = startDt.time
                            lsPay.add(payIt)
                        }

                        ContextUtil.payIncomeUtility.addPayNotes(lsPay)
                    }

                    val rand2 = Random()
                    val income = rand2.nextInt(5)
                    if (0 < income) {
                        val incomeMax = 500.0
                        val incomeMin = 0.1
                        val lsIncome = LinkedList<IncomeNoteItem>()
                        for (i in 0 until income) {
                            val pay_it = IncomeNoteItem()
                            pay_it.usr = defUi
                            pay_it.info = ci.incomeInfo
                            pay_it.amount = ci.getVal(incomeMax, incomeMin)
                            pay_it.ts.time = startDt.time
                            lsIncome.add(pay_it)
                        }

                        ContextUtil.payIncomeUtility.addIncomeNotes(lsIncome)
                    }
                }
                startDt.time = startDt.time + oneDayMSec
            }
        }
    }

    companion object {
        private var LOG_TAG = ::DBOrmLiteHelper.javaClass.simpleName

        // dataBase file name
        private const val DATABASE_NAME = "AppLocal.db"
        // dataBase version
        private const val DATABASE_VERSION = 9
    }
}
