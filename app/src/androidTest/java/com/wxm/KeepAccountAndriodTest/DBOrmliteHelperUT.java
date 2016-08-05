package com.wxm.KeepAccountAndriodTest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.Suppress;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wxm.KeepAccount.Base.data.RecordItem;
import com.wxm.KeepAccount.Base.db.DBOrmliteHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * UT for DBOrmliteHelper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmliteHelperUT extends AndroidTestCase {
    private RenamingDelegatingContext   mMockContext;
    private DBOrmliteHelper             mHelper;

    public DBOrmliteHelperUT()  {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mMockContext = new RenamingDelegatingContext(getContext(), "test1_");
        assertNotNull(mMockContext);
        mHelper = new DBOrmliteHelper(mMockContext);
        assertNotNull(mHelper);

        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getSimpleDataDao();
        assertNotNull(sdao);

        sdao.executeRawNoArgs(
                String.format(Locale.CHINA
                        , "DELETE FROM %s"
                        , sdao.getTableName()));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()  {
        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getSimpleDataDao();
        assertNotNull(sdao);

        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.setRecord_type("pay");
        ri.setRecord_info("tax");
        ri.setRecord_val(new BigDecimal(12.34));
        ri.getRecord_ts().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        de = new Date();
        ri = new RecordItem();
        ri.setRecord_type("pay");
        ri.setRecord_info("water cost");
        ri.setRecord_val(new BigDecimal(12.34));
        ri.getRecord_ts().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);


        de = new Date();
        ri = new RecordItem();
        ri.setRecord_type("pay");
        ri.setRecord_info("electrcity cost");
        ri.setRecord_val(new BigDecimal(12.34));
        ri.getRecord_ts().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        de = new Date();
        ri = new RecordItem();
        ri.setRecord_type("income");
        ri.setRecord_info("工资");
        ri.setRecord_val(new BigDecimal(12.34));
        ri.getRecord_ts().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        testGet();
    }

    @Suppress
    public void testGet()  {
        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getSimpleDataDao();
        assertNotNull(sdao);

        List<RecordItem> rets = sdao.queryForAll();
        assertEquals(rets.size(), 4);

        List<RecordItem> rets1 = sdao.queryForEq("record_type", "pay");
        assertEquals(rets1.size(), 3);

        List<RecordItem> rets2 = sdao.queryForEq("record_type", "income");
        assertEquals(rets2.size(), 1);
    }
}
