package wxm.KeepAccount.Base.utility;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.RecordTypeItem;
import wxm.KeepAccount.Base.db.RemindItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.BuildConfig;
import wxm.KeepAccount.R;

/**
 * db ormlite helper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmliteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBOrmliteHelper";


    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "AppLocal.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 7;

    // the DAO object we use to access the SimpleData table
    private RuntimeExceptionDao<UsrItem, Integer>           mUsrNoteRDao = null;
    private RuntimeExceptionDao<RecordTypeItem, Integer>    mRTNoteRDao = null;
    private RuntimeExceptionDao<BudgetItem, Integer>        mBudgetNoteRDao = null;
    private RuntimeExceptionDao<PayNoteItem, Integer>       mPayNoteRDao = null;
    private RuntimeExceptionDao<IncomeNoteItem, Integer>    mIncomeNoteRDao = null;
    private RuntimeExceptionDao<RemindItem, Integer>        mRemindRDao = null;

    public DBOrmliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        CreateAndInitTable();
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if(7 == newVersion)     {
                TableUtils.dropTable(connectionSource, UsrItem.class, true);
                TableUtils.dropTable(connectionSource, RecordTypeItem.class, true);
                TableUtils.dropTable(connectionSource, PayNoteItem.class, true);
                TableUtils.dropTable(connectionSource, IncomeNoteItem.class, true);
                TableUtils.dropTable(connectionSource, BudgetItem.class, true);

                onCreate(db, connectionSource);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't upgrade databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<UsrItem, Integer> getUsrItemREDao() {
        if (mUsrNoteRDao == null) {
            mUsrNoteRDao = getRuntimeExceptionDao(UsrItem.class);
        }
        return mUsrNoteRDao;
    }

    public RuntimeExceptionDao<RecordTypeItem, Integer> getRTItemREDao() {
        if (mRTNoteRDao == null) {
            mRTNoteRDao = getRuntimeExceptionDao(RecordTypeItem.class);
        }
        return mRTNoteRDao;
    }

    public RuntimeExceptionDao<BudgetItem, Integer> getBudgetDataREDao() {
        if (mBudgetNoteRDao == null) {
            mBudgetNoteRDao = getRuntimeExceptionDao(BudgetItem.class);
        }
        return mBudgetNoteRDao;
    }

    public RuntimeExceptionDao<PayNoteItem, Integer> getPayDataREDao() {
        if (mPayNoteRDao == null) {
            mPayNoteRDao = getRuntimeExceptionDao(PayNoteItem.class);
        }
        return mPayNoteRDao;
    }

    public RuntimeExceptionDao<IncomeNoteItem, Integer> getIncomeDataREDao() {
        if (mIncomeNoteRDao == null) {
            mIncomeNoteRDao = getRuntimeExceptionDao(IncomeNoteItem.class);
        }
        return mIncomeNoteRDao;
    }

    public RuntimeExceptionDao<RemindItem, Integer> getRemindREDao() {
        if (mRemindRDao == null) {
            mRemindRDao = getRuntimeExceptionDao(RemindItem.class);
        }
        return mRemindRDao;
    }


    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        mUsrNoteRDao = null;
        mRTNoteRDao = null;
        mBudgetNoteRDao = null;
        mPayNoteRDao = null;
        mIncomeNoteRDao = null;
        mRemindRDao = null;
    }


    private void CreateAndInitTable()   {
        try {
            TableUtils.createTable(connectionSource, UsrItem.class);
            TableUtils.createTable(connectionSource, RecordTypeItem.class);
            TableUtils.createTable(connectionSource, BudgetItem.class);
            TableUtils.createTable(connectionSource, PayNoteItem.class);
            TableUtils.createTable(connectionSource, IncomeNoteItem.class);
            TableUtils.createTable(connectionSource, RemindItem.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }

        // 添加recordtype
        RuntimeExceptionDao<RecordTypeItem, Integer> redao = getRTItemREDao();
        Resources res = ContextUtil.getInstance().getResources();
        String[] type = res.getStringArray(R.array.payinfo);
        for(String ln : type)   {
            RecordTypeItem ri = new RecordTypeItem();
            ri.setItemType(RecordTypeItem.DEF_PAY);
            ri.setType(ln);

            redao.create(ri);
        }

        type = res.getStringArray(R.array.incomeinfo);
        for(String ln : type)   {
            RecordTypeItem ri = new RecordTypeItem();
            ri.setItemType(RecordTypeItem.DEF_INCOME);
            ri.setType(ln);

            redao.create(ri);
        }

        // 添加默认用户
        AppModel.getUsrUtility().addUsr(AppGobalDef.DEF_USR_NAME, AppGobalDef.DEF_USR_PWD);

        if(BuildConfig.FILL_TESTDATA)
            AddTestData();
    }


    /**
     * 填充测试数据
     */
    private void AddTestData()  {
        UsrItem ui_wxm  = AppModel.getUsrUtility().addUsr("wxm", "123456");
        UsrItem ui_hugo = AppModel.getUsrUtility().addUsr("hugo", "123456");

        // for wxm
        LinkedList<PayNoteItem> ls_pay = new LinkedList<>();
        Date de = new Date();
        PayNoteItem pay_it = new PayNoteItem();
        pay_it.setUsr(ui_wxm);
        pay_it.setInfo("tax");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);

        pay_it = new PayNoteItem();
        pay_it.setUsr(ui_wxm);
        pay_it.setInfo("water cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);

        pay_it = new PayNoteItem();
        pay_it.setUsr(ui_wxm);
        pay_it.setInfo("electrcity  cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);
        AppModel.getPayIncomeUtility().AddPayNotes(ls_pay);

        LinkedList<IncomeNoteItem> ls_income = new LinkedList<>();
        IncomeNoteItem income_it = new IncomeNoteItem();
        income_it.setUsr(ui_wxm);
        income_it.setInfo("工资");
        income_it.setVal(new BigDecimal(12.34));
        income_it.getTs().setTime(de.getTime());
        ls_income.add(income_it);
        AppModel.getPayIncomeUtility().AddIncomeNotes(ls_income);

        // for hugo
        for(PayNoteItem i : ls_pay)     {
            i.setId(AppGobalDef.INVALID_ID);
            i.setUsr(ui_hugo);
        }
        AppModel.getPayIncomeUtility().AddPayNotes(ls_pay);

        for(IncomeNoteItem i : ls_income)     {
            i.setId(AppGobalDef.INVALID_ID);
            i.setUsr(ui_hugo);
        }
        AppModel.getPayIncomeUtility().AddIncomeNotes(ls_income);

        AddTestDataForDefualtUsr();
    }


    private void AddTestDataForDefualtUsr() {
        UsrItem def_ui = AppModel.getUsrUtility()
                            .CheckAndGetUsr(AppGobalDef.DEF_USR_NAME, AppGobalDef.DEF_USR_PWD);
        if(null != def_ui)  {
            long one_day_msecs = 1000 * 3600 * 24;
            long before_msecs = one_day_msecs * 500;

            Date cur_dt = new Date();
            Date start_dt = new Date(cur_dt.getTime() - before_msecs);
            Date end_dt = new Date();

            for(; start_dt.before(end_dt);
                    start_dt.setTime(start_dt.getTime() + one_day_msecs))  {
                Random rand = new Random();
                int r = rand.nextInt(99);
                if(0 == r % 3)     {
                    Random rand1 = new Random();
                    int pay = rand1.nextInt(3);
                    if(0 < pay) {
                        int pay_max = 500;
                        LinkedList<PayNoteItem> ls_pay = new LinkedList<>();
                        for (int i = 0; i < pay; ++i) {
                            Random randp = new Random();

                            PayNoteItem pay_it = new PayNoteItem();
                            pay_it.setUsr(def_ui);
                            pay_it.setInfo("tax");
                            pay_it.setVal(new BigDecimal(randp.nextFloat() * pay_max + 0.1));
                            pay_it.getTs().setTime(start_dt.getTime());
                            ls_pay.add(pay_it);
                        }

                        AppModel.getPayIncomeUtility().AddPayNotes(ls_pay);
                    }


                    Random rand2 = new Random();
                    int income = rand2.nextInt(3);
                    if(0 < income) {
                        int income_max = 500;
                        LinkedList<IncomeNoteItem> ls_pay = new LinkedList<>();
                        for (int i = 0; i < income; ++i) {
                            Random randp = new Random();

                            IncomeNoteItem pay_it = new IncomeNoteItem();
                            pay_it.setUsr(def_ui);
                            pay_it.setInfo("tip");
                            pay_it.setVal(new BigDecimal(randp.nextFloat() * income_max + 0.1));
                            pay_it.getTs().setTime(start_dt.getTime());
                            ls_pay.add(pay_it);
                        }

                        AppModel.getPayIncomeUtility().AddIncomeNotes(ls_pay);
                    }
                }
            }
        }
    }
}
