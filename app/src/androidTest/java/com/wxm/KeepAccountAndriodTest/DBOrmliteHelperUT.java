package com.wxm.KeepAccountAndriodTest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.Suppress;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.RecordItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;

/**
 * UT for DBOrmliteHelper
 * Created by 123 on 2016/8/5.
 */
public class DBOrmliteHelperUT extends AndroidTestCase {
    private RenamingDelegatingContext   mMockContext;
    private DBOrmliteHelper             mHelper;

    public DBOrmliteHelperUT()     {
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
        sdao.deleteBuilder().delete();

        RuntimeExceptionDao<UsrItem, Integer> sdao1 = mHelper.getUsrItemREDao();
        assertNotNull(sdao1);
        sdao1.deleteBuilder().delete();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getRecordItemREDao();
        assertNotNull(sdao);
        sdao.deleteBuilder().delete();

        RuntimeExceptionDao<UsrItem, Integer> sdao1 = mHelper.getUsrItemREDao();
        assertNotNull(sdao1);
        sdao1.deleteBuilder().delete();

        mHelper.close();
    }

    public void testNoteAdd()   {
        RuntimeExceptionDao<PayNoteItem, Integer> pay_dao = mHelper.getPayDataREDao();
        RuntimeExceptionDao<IncomeNoteItem, Integer> income_dao = mHelper.getIncomeDataREDao();
        RuntimeExceptionDao<UsrItem, Integer> usr_dao = mHelper.getUsrItemREDao();
        assertNotNull(pay_dao);
        assertNotNull(income_dao);
        assertNotNull(usr_dao);

        UsrItem ui = new UsrItem();
        ui.setName("wxm");
        ui.setPwd("123456");
        assertEquals(1, usr_dao.create(ui));

        Date de = new Date();
        PayNoteItem pay_it = new PayNoteItem();
        pay_it.setInfo("tax");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        try {
            pay_dao.create(pay_it);
            fail("期望有异常发生，但未发生");
        } catch (RuntimeException e)    {
            assertTrue(e.getMessage(), true);
        }

        pay_it.setUsr(ui);
        assertEquals(1, pay_dao.create(pay_it));

        de = new Date();
        pay_it = new PayNoteItem();
        pay_it.setUsr(ui);
        pay_it.setInfo("water cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        assertEquals(1, pay_dao.create(pay_it));

        de = new Date();
        pay_it = new PayNoteItem();
        pay_it.setUsr(ui);
        pay_it.setInfo("electrcity cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        assertEquals(1, pay_dao.create(pay_it));

        de = new Date();
        IncomeNoteItem income_it = new IncomeNoteItem();
        income_it.setUsr(ui);
        income_it.setInfo("工资");
        income_it.setVal(new BigDecimal(12.34));
        income_it.getTs().setTime(de.getTime());
        assertEquals(1, income_dao.create(income_it));

        // test get
        List<PayNoteItem> pay_ret = pay_dao.queryForAll();
        assertEquals(3, pay_ret.size());

        List<IncomeNoteItem> income_ret = income_dao.queryForAll();
        assertEquals(1, income_ret.size());

        pay_ret = pay_dao.queryForEq(PayNoteItem.FIELD_USR, ui);
        assertEquals(3, pay_ret.size());

        income_ret = income_dao.queryForEq(PayNoteItem.FIELD_USR, ui);
        assertEquals(1, income_ret.size());


        UsrItem nui = new UsrItem();
        nui.setName("wxm");
        nui.setPwd("123456");
        pay_ret = pay_dao.queryForEq(PayNoteItem.FIELD_USR, nui);
        assertEquals(0, pay_ret.size());
    }


    public void testRecordItemAdd()  {
        RuntimeExceptionDao<RecordItem, Integer> sdao = mHelper.getRecordItemREDao();
        RuntimeExceptionDao<UsrItem, Integer> sdao1 = mHelper.getUsrItemREDao();
        assertNotNull(sdao);
        assertNotNull(sdao1);

        UsrItem ui = new UsrItem();
        ui.setName("wxm");
        ui.setPwd("123456");
        assertEquals(sdao1.create(ui), 1);

        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.setType("pay");
        ri.setInfo("tax");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        try {
            sdao.create(ri);
            fail("期望有异常发生，但未发生");
        } catch (RuntimeException e)    {
            assertTrue(e.getMessage(), true);
        }

        ri.setUsr(ui);
        assertEquals(sdao.create(ri), 1);

        de = new Date();
        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("pay");
        ri.setInfo("water cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        de = new Date();
        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("pay");
        ri.setInfo("electrcity cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        de = new Date();
        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("income");
        ri.setInfo("工资");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        assertEquals(sdao.create(ri), 1);

        // test get
        List<RecordItem> rets = sdao.queryForAll();
        assertEquals(rets.size(), 4);

        List<RecordItem> rets1 = sdao.queryForEq(RecordItem.FIELD_TYPE, "pay");
        assertEquals(rets1.size(), 3);

        List<RecordItem> rets2 = sdao.queryForEq(RecordItem.FIELD_TYPE, "income");
        assertEquals(rets2.size(), 1);

        List<RecordItem> rets3 = sdao.queryForEq(RecordItem.FIELD_USR, ui);
        assertEquals(rets3.size(), 4);

        UsrItem nui = new UsrItem();
        nui.setName("wxm");
        nui.setPwd("123456");
        rets3 = sdao.queryForEq(RecordItem.FIELD_USR, nui);
        assertEquals(rets3.size(), 0);
    }



    public void testUsrItemAdd()    {
        RuntimeExceptionDao<UsrItem, Integer> sdao = mHelper.getUsrItemREDao();
        assertNotNull(sdao);

        UsrItem ui = new UsrItem();
        ui.setName("hugo");
        ui.setPwd("pwd");
        assertEquals(sdao.create(ui), 1);

        ui.setName("ookoo");
        ui.setPwd("pwd");
        assertEquals(sdao.create(ui), 1);

        ui.setName("ookoo");
        ui.setPwd("pwd");

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
        RuntimeExceptionDao<UsrItem, Integer> sdao = mHelper.getUsrItemREDao();
        assertNotNull(sdao);

        List<UsrItem> rets = sdao.queryForAll();
        assertEquals(rets.size(), 2);

        List<UsrItem> rets1 = sdao.queryForEq(UsrItem.FIELD_NAME, "hugo");
        assertEquals(rets1.size(), 1);

        List<UsrItem> rets2 = sdao.queryForEq(UsrItem.FIELD_NAME, "ookoo");
        assertEquals(rets2.size(), 1);

        List<UsrItem> rets3 = sdao.queryForEq(UsrItem.FIELD_NAME, "flyer");
        assertEquals(rets3.size(), 0);
    }
}
