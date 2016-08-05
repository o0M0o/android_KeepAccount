package com.wxm.KeepAccount.Base.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxm.KeepAccount.Base.data.RecordItem;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DB功能类
 * Created by 123 on 2016/5/3.
 */
public class DBManager {
    private SQLiteDatabase db;

    public DBManager(Context context) {
        DBHelper helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 添加帐目记录
     * @param records  待添加数据
     */
    public void add(List<RecordItem> records)
    {
        db.beginTransaction();  //开始事务
        try {
            for (RecordItem record : records) {
                db.execSQL("INSERT INTO tb_KeepAccount VALUES(null, ?, ?, ?, ?, ?)",
                        new Object[]{record.getRecord_type(), record.getRecord_info(),
                                record.getRecord_note(), record.getRecord_val(), record.getRecord_ts()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * 修改记录
     * @param records 待修改数据
     */
    public void modify(List<RecordItem> records)    {
        db.beginTransaction();
        try {
            for (RecordItem record : records) {
                db.execSQL("UPDATE tb_KeepAccount"
                                + " SET"
                                + " record_type = ?"
                                + " ,record_info = ?"
                                + " ,record_note = ?"
                                + " ,record_val = ?"
                                + " ,record_ts = ?"
                                + " WHERE _id = ?",
                        new Object[]{
                                record.getRecord_type(), record.getRecord_info(),
                                record.getRecord_note(), record.getRecord_val(),
                                record.getRecord_ts(), record.get_id()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<RecordItem> query() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        ArrayList<RecordItem> persons = new ArrayList<>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            RecordItem ri = new RecordItem();
            ri.set_id(c.getInt(c.getColumnIndex("_id")));
            ri.setRecord_type(c.getString(c.getColumnIndex("record_type")));
            ri.setRecord_info(c.getString(c.getColumnIndex("record_info")));
            ri.setRecord_val(new BigDecimal(c.getDouble(c.getColumnIndex("record_val"))));
            ri.setRecord_note(c.getString(c.getColumnIndex("record_note")));

            try {
                Date date = format.parse(c.getString(c.getColumnIndex("record_ts")));
                ri.getRecord_ts().setTime(date.getTime());
            }
            catch (ParseException ex)
            {

                ri.setRecord_ts(new Timestamp(0));
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
            db.execSQL("DELETE FROM tb_KeepAccount");
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
                String sql = String.format("DELETE FROM tb_KeepAccount WHERE _id = '%s'", i);
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
        return db.rawQuery("SELECT * FROM tb_KeepAccount", null);
    }


    public Cursor queryUsrCursor()  {
        return db.rawQuery("SELECT * FROM tb_Usr", null);
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
