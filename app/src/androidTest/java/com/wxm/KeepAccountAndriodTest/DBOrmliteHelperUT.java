package com.wxm.KeepAccountAndriodTest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.Suppress;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wxm.KeepAccount.Base.data.RecordItem;
import com.wxm.KeepAccount.Base.data.UsrItem;
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

        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getRecordItemREDao();
        assertNotNull(sdao);
        sdao.executeRawNoArgs(
                String.format(Locale.CHINA
                        , "DELETE FROM %s"
                        , sdao.getTableName()));


        RuntimeExceptionDao<UsrItem, String> sdao1 = mHelper.getUsrItemREDao();
        assertNotNull(sdao1);
        sdao1.executeRawNoArgs(
                String.format(Locale.CHINA
                        , "DELETE FROM %s"
                        , sdao1.getTableName()));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        mHelper.close();
    }

    public void testRecordItemAdd()  {
        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getRecordItemREDao();
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

        testRecordItemGet();
    }

    @Suppress
    public void testRecordItemGet()  {
        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getRecordItemREDao();
        assertNotNull(sdao);

        List<RecordItem> rets = sdao.queryForAll();
        assertEquals(rets.size(), 4);

        List<RecordItem> rets1 = sdao.queryForEq("record_type", "pay");
        assertEquals(rets1.size(), 3);

        List<RecordItem> rets2 = sdao.queryForEq("record_type", "income");
        assertEquals(rets2.size(), 1);
    }


    public void testUsrItemAdd()    {
        RuntimeExceptionDao<UsrItem, String> sdao = mHelper.getUsrItemREDao();
        assertNotNull(sdao);

        UsrItem ui = new UsrItem();
        ui.setUsr_name("hugo");
        ui.setUsr_pwd("pwd");
        assertEquals(sdao.create(ui), 1);

        ui.setUsr_name("ookoo");
        ui.setUsr_pwd("pwd");
        assertEquals(sdao.create(ui), 1);

        ui.setUsr_name("ookoo");
        ui.setUsr_pwd("pwd");

        try {
            assertEquals(sdao.create(ui), 0);
            fail("期望有异常发生，但未发生");
        } catch (RuntimeException e)    {
            assertTrue(e.getMessage(), true);
        }

        testUsrItemGet();
    }

    @Suppress
    public void testUsrItemGet() {
        RuntimeExceptionDao<UsrItem, String> sdao = mHelper.getUsrItemREDao();
        assertNotNull(sdao);

        List<UsrItem> rets = sdao.queryForAll();
        assertEquals(rets.size(), 2);

        List<UsrItem> rets1 = sdao.queryForEq("usr_name", "hugo");
        assertEquals(rets1.size(), 1);

        List<UsrItem> rets2 = sdao.queryForEq("usr_name", "ookoo");
        assertEquals(rets2.size(), 1);

        List<UsrItem> rets3 = sdao.queryForEq("usr_name", "flyer");
        assertEquals(rets3.size(), 0);
    }
}
