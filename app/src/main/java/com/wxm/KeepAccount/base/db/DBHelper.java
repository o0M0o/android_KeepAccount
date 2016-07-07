package com.wxm.KeepAccount.Base.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB辅助类
 * Created by 123 on 2016/5/2.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppLocal.db";
    private static final int DATABASE_VERSION = 2;

    public final static String COLNAME_USER_NAME = "usr_name";
    public final static String COLNAME_USER_PWD = "usr_pwd";

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS tb_KeepAccount" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   record_type NVARCHAR, " +
                "   record_info NVARCHAR, " +
                "   record_note NVARCHAR, " +
                "   record_val DECIMAL(10,2), " +
                "   record_ts TIMESTAMP)");

        db.execSQL("CREATE TABLE IF NOT EXISTS tb_Usr" +
                    "(" + COLNAME_USER_NAME + " NVARCHAR PRIMARY KEY, " +
                    "   " + COLNAME_USER_PWD + " NVARCHAR)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("ALTER TABLE person ADD COLUMN other STRING");

        if((1 == oldVersion) && (2 == newVersion))  {
            db.execSQL("CREATE TABLE IF NOT EXISTS tb_KeepAccount" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "   record_type NVARCHAR, " +
                    "   record_info NVARCHAR, " +
                    "   record_note NVARCHAR, " +
                    "   record_val DECIMAL(10,2), " +
                    "   record_ts TIMESTAMP)");
        }
    }
}