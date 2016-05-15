package com.wxm.BaseLib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 123 on 2016/5/3.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 添加帐目记录
     * @param records
     */
    public void add(List<RecordItem> records)
    {
        db.beginTransaction();  //开始事务
        try {
            for (RecordItem record : records) {
                db.execSQL("INSERT INTO tb_KeepAccout VALUES(null, ?, ?, ?, ?)",
                        new Object[]{record.record_type, record.record_info,
                                        record.record_val, record.record_ts});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<RecordItem> query() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<RecordItem> persons = new ArrayList<RecordItem>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            RecordItem ri = new RecordItem();
            ri._id = c.getInt(c.getColumnIndex("_id"));
            ri.record_type = c.getString(c.getColumnIndex("record_type"));
            ri.record_info = c.getString(c.getColumnIndex("record_info"));
            ri.record_val = new BigDecimal(c.getDouble(c.getColumnIndex("record_val")));

            try {
                Date date = format.parse(c.getString(c.getColumnIndex("record_ts")));
                ri.record_ts.setTime(date.getTime());
            }
            catch (ParseException ex)
            {

                ri.record_ts = new Timestamp(0);
            }

            persons.add(ri);
        }
        c.close();
        return persons;
    }


    /**
     * 删除表中所有数据
     */
    public void deleteAll()
    {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("DELETE FROM tb_KeepAccout");
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }


    /**
     * 删除指定的记录
     * @param lsi 需删除数据
     */
    public void deleteRecords(List<String> lsi) {
        db.beginTransaction();
        try {
            for(String i : lsi) {
                String sql = String.format("DELETE FROM tb_KeepAccout WHERE _id = '%s'", i);
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM tb_KeepAccout", null);
        return c;
    }


    public Cursor queryUsrCursor()  {
        Cursor c = db.rawQuery("SELECT * FROM tb_Usr", null);
        return c;
    }


    /**
     * 判断是否已经存在用户
     * @param usr 待检查用户名
     * @return 如果用户已经存在，返回true, 否则返回false
     */
    public boolean hasUsr(String usr)   {
        if(usr.isEmpty())   {
            return false;
        }

        String sql = String.format("SELECT %s FROM tb_Usr WHERE %s = '%s'",
                DBHelper.COLNAME_USER_NAME,
                DBHelper.COLNAME_USER_NAME,
                usr);
        Cursor c = db.rawQuery(sql, null);

        boolean ret = false;
        if(c.moveToNext())   {
            ret = true;
        }

        c.close();
        return ret;
    }


    /**
     * 添加用户
     * @param usr   用户名
     * @param pwd   用户密码
     * @return  成功返回true,否则返回false
     */
    public boolean addUsr(String usr, String pwd)   {
        if(hasUsr(usr) || pwd.isEmpty()) {
            return false;
        }

        boolean ret = false;
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO tb_Usr VALUES(?, ?)",
                    new Object[]{usr, pwd});

            ret = true;
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }

        return ret;
    }

    /**
     * 检查用户
     * @param usr   用户名
     * @param pwd   密码
     * @return  成功返回true,否则返回false
     */
    public boolean checkUsr(String usr, String pwd) {
        if(!hasUsr(usr) || pwd.isEmpty()) {
            return false;
        }

        String sql = String.format("SELECT %s FROM tb_Usr WHERE %s = '%s'",
                                        DBHelper.COLNAME_USER_PWD,
                                        DBHelper.COLNAME_USER_NAME,
                                        usr);
        Cursor c = db.rawQuery(sql, null);

        boolean ret = false;
        if(c.moveToNext())   {
            String db_pwd = c.getString(
                                c.getColumnIndex(DBHelper.COLNAME_USER_PWD));
            ret = pwd.equals(db_pwd);
        }

        c.close();
        return ret;
    }


    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
