package com.wxm.KeepAccountAndriodTest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.wxm.KeepAccount.Base.data.RecordItem;
import com.wxm.KeepAccount.Base.db.DBManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sqlite辅助类
 * Created by 123 on 2016/5/2.
 */
public class SqliteUT extends AndroidTestCase {
    private RenamingDelegatingContext mMockContext;
    private DBManager                 mDbManger;

    public SqliteUT() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mMockContext = new RenamingDelegatingContext(getContext(), "test_");

        mDbManger = new DBManager(mMockContext);
        assertNotNull(mDbManger);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testAdd()
    {
        mDbManger.deleteAll();

        ArrayList<RecordItem> items = new ArrayList<RecordItem>();
        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.setType("pay");
        ri.setInfo("tax");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.setType("pay");
        ri.setInfo("water cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.setType("pay");
        ri.setInfo("electrcity cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        items.add(ri);

        de = new Date();
        ri = new RecordItem();
        ri.setType("income");
        ri.setInfo("工资");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        items.add(ri);


        de = new Date();
        ri = new RecordItem();
        ri.setType("income");
        ri.setInfo("稿酬");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        items.add(ri);

        mDbManger.add(items);

        List<RecordItem> lr = mDbManger.query();
        assertEquals(lr.size(), 5);
    }
}
