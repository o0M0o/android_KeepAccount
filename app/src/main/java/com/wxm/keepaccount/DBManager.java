package com.wxm.keepaccount;

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


    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}