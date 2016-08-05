package com.wxm.KeepAccount.Base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wxm.KeepAccount.Base.data.RecordItem;
import com.wxm.KeepAccount.Base.data.UsrItem;

import java.sql.SQLException;

/**
 * db ormlite helper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmliteHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DBOrmliteHelper";


    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "AppLocal.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 3;

    // the DAO object we use to access the SimpleData table
    private Dao<RecordItem, Integer> simpleRecordItemDao = null;
    private RuntimeExceptionDao<RecordItem, Integer> simpleRecordItemRuntimeDao = null;

    private Dao<UsrItem, String> simpleUsrItemDao = null;
    private RuntimeExceptionDao<UsrItem, String> simpleUsrItemRuntimeDao = null;

    public DBOrmliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");
            TableUtils.createTable(connectionSource, RecordItem.class);
            TableUtils.createTable(connectionSource, UsrItem.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "onUpgrade");

            TableUtils.dropTable(connectionSource, RecordItem.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<RecordItem, Integer> getRecordItemDao() throws SQLException {
        if (simpleRecordItemDao == null) {
            simpleRecordItemDao = getDao(RecordItem.class);
        }
        return simpleRecordItemDao;
    }

    public Dao<UsrItem, String> getUsrItemDao() throws SQLException {
        if (simpleUsrItemDao == null) {
            simpleUsrItemDao = getDao(UsrItem.class);
        }
        return simpleUsrItemDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<RecordItem, Integer> getRecordItemREDao() {
        if (simpleRecordItemRuntimeDao == null) {
            simpleRecordItemRuntimeDao = getRuntimeExceptionDao(RecordItem.class);
        }
        return simpleRecordItemRuntimeDao;
    }

    public RuntimeExceptionDao<UsrItem, String> getUsrItemREDao() {
        if (simpleUsrItemRuntimeDao == null) {
            simpleUsrItemRuntimeDao = getRuntimeExceptionDao(UsrItem.class);
        }
        return simpleUsrItemRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        simpleRecordItemDao = null;
        simpleRecordItemRuntimeDao = null;

        simpleUsrItemDao = null;
        simpleUsrItemRuntimeDao = null;
    }
}
