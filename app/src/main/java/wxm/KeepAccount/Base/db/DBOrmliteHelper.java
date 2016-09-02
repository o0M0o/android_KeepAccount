package wxm.KeepAccount.Base.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.BuildConfig;
import wxm.KeepAccount.R;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

/**
 * db ormlite helper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmliteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBOrmliteHelper";


    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "AppLocal.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 6;

    // the DAO object we use to access the SimpleData table
    //private Dao<RecordItem, Integer> simpleRecordItemDao = null;
    private RuntimeExceptionDao<RecordItem, Integer> simpleRecordItemRuntimeDao = null;

    //private Dao<UsrItem, Integer> simpleUsrItemDao = null;
    private RuntimeExceptionDao<UsrItem, Integer> simpleUsrItemRuntimeDao = null;

    //private Dao<RecordTypeItem, Integer> simpleRTItemDao = null;
    private RuntimeExceptionDao<RecordTypeItem, Integer> simpleRTItemRuntimeDao = null;

    private RuntimeExceptionDao<BudgetItem, Integer> simpleBudgetItemRuntimeDao = null;

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
        Log.i(TAG, "onUpgrade");
        try {
            if(5 == newVersion) {
                TableUtils.dropTable(connectionSource, RecordItem.class, true);
                TableUtils.dropTable(connectionSource, UsrItem.class, true);
            }

            if(6 == newVersion)     {
                TableUtils.dropTable(connectionSource, RecordItem.class, true);
                TableUtils.dropTable(connectionSource, UsrItem.class, true);
                TableUtils.dropTable(connectionSource, RecordTypeItem.class, true);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            //throw new RuntimeException(e);
        }

        // after we drop the old databases, we create the new ones
        onCreate(db, connectionSource);
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<RecordItem, Integer> getRecordItemREDao() {
        if (simpleRecordItemRuntimeDao == null) {
            simpleRecordItemRuntimeDao = getRuntimeExceptionDao(RecordItem.class);
        }

        simpleRecordItemRuntimeDao.setObjectCache(ReferenceObjectCache.makeSoftCache());
        return simpleRecordItemRuntimeDao;
    }

    public RuntimeExceptionDao<UsrItem, Integer> getUsrItemREDao() {
        if (simpleUsrItemRuntimeDao == null) {
            simpleUsrItemRuntimeDao = getRuntimeExceptionDao(UsrItem.class);
        }
        return simpleUsrItemRuntimeDao;
    }

    public RuntimeExceptionDao<RecordTypeItem, Integer> getRTItemREDao() {
        if (simpleRTItemRuntimeDao == null) {
            simpleRTItemRuntimeDao = getRuntimeExceptionDao(RecordTypeItem.class);
        }
        return simpleRTItemRuntimeDao;
    }

    public RuntimeExceptionDao<BudgetItem, Integer> getBudgetItemREDao() {
        if (simpleBudgetItemRuntimeDao == null) {
            simpleBudgetItemRuntimeDao = getRuntimeExceptionDao(BudgetItem.class);
        }
        return simpleBudgetItemRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        simpleRecordItemRuntimeDao = null;
        simpleUsrItemRuntimeDao = null;
        simpleRTItemRuntimeDao = null;
        simpleBudgetItemRuntimeDao = null;
    }


    private void CreateAndInitTable()   {
        try {
            TableUtils.createTable(connectionSource, RecordItem.class);
            TableUtils.createTable(connectionSource, UsrItem.class);
            TableUtils.createTable(connectionSource, RecordTypeItem.class);
            TableUtils.createTable(connectionSource, BudgetItem.class);
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
        LinkedList<RecordItem> lsit = new LinkedList<>();
        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.setUsr(ui_wxm);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("tax");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui_wxm);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("water cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui_wxm);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("electrcity cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui_wxm);
        ri.setType(AppGobalDef.CNSTR_RECORD_INCOME);
        ri.setInfo("工资");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        AppModel.getRecordUtility().AddRecords(lsit);

        // for hugo
        lsit.clear();
        ri = new RecordItem();
        ri.setUsr(ui_hugo);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("water cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);


        ri = new RecordItem();
        ri.setUsr(ui_hugo);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("tax");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui_hugo);
        ri.setType(AppGobalDef.CNSTR_RECORD_PAY);
        ri.setInfo("electrcity cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui_hugo);
        ri.setType(AppGobalDef.CNSTR_RECORD_INCOME);
        ri.setInfo("工资");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        AppModel.getRecordUtility().AddRecords(lsit);
    }
}
