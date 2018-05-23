package wxm.KeepAccount.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import wxm.KeepAccount.BuildConfig
import wxm.KeepAccount.R
import wxm.KeepAccount.define.*
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.log.TagLog
import java.math.BigDecimal
import java.sql.SQLException
import java.util.*

/**
 * db ormlite helper
 * Created by WangXM on 2016/8/5.
 */
class DBOrmLiteHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    val usrItemREDao: RuntimeExceptionDao<UsrItem, Int> = getRuntimeExceptionDao(UsrItem::class.java)
    val rtItemREDao: RuntimeExceptionDao<RecordTypeItem, Int> = getRuntimeExceptionDao(RecordTypeItem::class.java)
    val budgetDataREDao: RuntimeExceptionDao<BudgetItem, Int> = getRuntimeExceptionDao(BudgetItem::class.java)
    val payDataREDao: RuntimeExceptionDao<PayNoteItem, Int> = getRuntimeExceptionDao(PayNoteItem::class.java)
    val incomeDataREDao: RuntimeExceptionDao<IncomeNoteItem, Int> = getRuntimeExceptionDao(IncomeNoteItem::class.java)
    val remindREDao: RuntimeExceptionDao<RemindItem, Int> = getRuntimeExceptionDao(RemindItem::class.java)

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
                TableUtils.dropTable<BudgetItem, Any>(connectionSource, BudgetItem::class.java, true)
                TableUtils.createTable(connectionSource, BudgetItem::class.java)

                budgetDataREDao.queryForAll().let {
                    budgetDataREDao.create(it)
                }
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
            TagLog.e("Can't upgrade databases", e)
            throw RuntimeException(e)
        }

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
            TagLog.e("Can't create database", e)
            throw RuntimeException(e)
        }

        // 添加recordtype
        AppBase.getResources().let {
            it.getStringArray(R.array.pay_info).filterNotNull().forEach {
                line2item(RecordTypeItem.DEF_PAY, it).let {
                    rtItemREDao.create(it)
                }
            }

            it.getStringArray(R.array.income_info).filterNotNull().forEach {
                line2item(RecordTypeItem.DEF_INCOME, it).let {
                    rtItemREDao.create(it)
                }
            }

            Unit
        }


        // 添加默认用户
        ContextUtil.usrUtility.addUsr(GlobalDef.DEF_USR_NAME, GlobalDef.DEF_USR_PWD)

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FILL_TESTDATA)
            addTestData()
    }


    /**
     * 把字符串(生活费-生活开销)转换为item
     *
     * @param szType 记录类型的类型
     * @param ln   待转换字符串
     * @return 记录类型数据
     */
    private fun line2item(szType: String, ln: String): RecordTypeItem {
        val sln = ln.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (BuildConfig.DEBUG && 2 != sln.size) {
            throw AssertionError()
        }

        return RecordTypeItem().apply {
            itemType = szType
            type = sln[0]
            note = sln[1]
        }
    }


    /**
     * 填充测试数据
     */
    private fun addTestData() {
        val uiWxm = ContextUtil.usrUtility.addUsr("wxm", "123456")
        val uiHugo = ContextUtil.usrUtility.addUsr("hugo", "123456")

        // for wxm
        val lsPay = LinkedList<PayNoteItem>()
        val de = Date()
        lsPay.add(PayNoteItem().apply {
            usr = uiWxm
            info = "tax"
            amount = BigDecimal(12.34)
            ts.time = de.time
        })

        lsPay.add(PayNoteItem().apply {
            usr = uiWxm
            info = "water cost"
            amount = BigDecimal(12.34)
            ts.time = de.time
        })

        lsPay.add(PayNoteItem().apply {
            usr = uiWxm
            info = "electricity  cost"
            amount = BigDecimal(12.34)
            ts.time = de.time
        })
        ContextUtil.payIncomeUtility.addPayNotes(lsPay)

        val lsIncome = LinkedList<IncomeNoteItem>().apply {
            add(IncomeNoteItem().apply {
                usr = uiWxm
                info = "工资"
                amount = BigDecimal(12.34)
                ts.time = de.time
            })
        }.let {
            ContextUtil.payIncomeUtility.addIncomeNotes(it)
            it
        }

        // for hugo
        lsPay.forEach {
            it.id = GlobalDef.INVALID_ID
            it.usr = uiHugo
        }
        ContextUtil.payIncomeUtility.addPayNotes(lsPay)

        lsIncome.forEach {
            it.id = GlobalDef.INVALID_ID
            it.usr = uiHugo
        }
        ContextUtil.payIncomeUtility.addIncomeNotes(lsIncome)

        createTestDataForDefaultUsr()
    }

    /**
     * create test data
     */
    private fun createTestDataForDefaultUsr() {
        class CreateUtility {
            val mSZPayInfo = arrayOf("租金", "生活费", "交通费", "社交开支", "娱乐开支", "其它")
            val mSZIncomeInfo = arrayOf("工资", "投资收入", "营业收入", "奖金", "其它")

            val payInfo: String
                get() = randomItem(mSZPayInfo)

            val incomeInfo: String
                get() = randomItem(mSZIncomeInfo)

            fun getVal(max_val: Double, min_val: Double): BigDecimal {
                return BigDecimal(Math.max(Random().nextFloat() * max_val, min_val))
            }

            private fun <T> randomItem(arr: Array<T>): T = arr[Random().nextInt(arr.size)]
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
                        LinkedList<PayNoteItem>().apply {
                            for (i in 0 until pay) {
                                add(PayNoteItem().apply {
                                    usr = defUi
                                    info = ci.payInfo
                                    amount = ci.getVal(payMax, payMin)
                                    ts.time = startDt.time
                                })
                            }
                        }.let {
                            ContextUtil.payIncomeUtility.addPayNotes(it)
                        }
                    }

                    val rand2 = Random()
                    val income = rand2.nextInt(5)
                    if (0 < income) {
                        val incomeMax = 500.0
                        val incomeMin = 0.1
                        LinkedList<IncomeNoteItem>().apply {
                            for (i in 0 until income) {
                                add(IncomeNoteItem().apply {
                                    usr = defUi
                                    info = ci.incomeInfo
                                    amount = ci.getVal(incomeMax, incomeMin)
                                    ts.time = startDt.time
                                })
                            }
                        }.let {
                            ContextUtil.payIncomeUtility.addIncomeNotes(it)
                        }
                    }
                }

                startDt.time = startDt.time + oneDayMSec
            }
        }
    }

    companion object {
        // dataBase file name
        private const val DATABASE_NAME = "AppLocal.db"
        // dataBase version
        private const val DATABASE_VERSION = 9
    }
}
