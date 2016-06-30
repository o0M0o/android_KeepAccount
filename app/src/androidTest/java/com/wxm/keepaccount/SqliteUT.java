package com.wxm.KeepAccount;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.wxm.KeepAccount.base.db.DBManager;
import com.wxm.KeepAccount.base.data.RecordItem;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sqlite辅助类
 * Created by 123 on 2016/5/2.
 */
//@RunWith(AndroidJUnit4.class)
//@LargeTest
public class SqliteUT extends ApplicationTestCase<Application> {

    public SqliteUT() {
        super(Application.class);
    }

    @Test
    public void invokeUT()
    {
        testAdd();
    }

    public void testAdd()
    {
        DBManager dbm = new DBManager(this.getContext());
        assertNotNull(dbm);

        dbm.deleteAll();

        ArrayList<RecordItem> items = new ArrayList<RecordItem>();
        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.record_type = "pay";
        ri.record_info = "tax";
        ri.record_val = new BigDecimal(12.34);
        ri.record_ts.setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.record_type = "pay";
        ri.record_info = "water cost";
        ri.record_val = new BigDecimal(12.34);
        ri.record_ts.setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.record_type = "pay";
        ri.record_info = "electrcity cost";
        ri.record_val = new BigDecimal(12.34);
        ri.record_ts.setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.record_type = "income";
        ri.record_info = "工资";
        ri.record_val = new BigDecimal(12.34);
        ri.record_ts.setTime(de.getTime());
        items.add(ri);


        de = new Date();
        ri = new RecordItem();
        ri.record_type = "income";
        ri.record_info = "稿酬";
        ri.record_val = new BigDecimal(12.34);
        ri.record_ts.setTime(de.getTime());
        items.add(ri);

        dbm.add(items);

        List<RecordItem> lr = dbm.query();
        assertEquals(lr.size(), 5);
    }
}
