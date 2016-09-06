package com.wxm.KeepAccountAndriodTest;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.UsrItem;

/**
 * unittest for AppModel
 * Created by 123 on 2016/8/6.
 */
public class AppModelUT extends AndroidTestCase {
    public AppModelUT() {
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

        Context mMockContext = new RenamingDelegatingContext(getContext(), "test_");
        assertNotNull(mMockContext);

        AppModel.SetContext(mMockContext);
        AppModel.getInstance().SetUp();
        AppModel.getInstance().ClearDB();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        AppModel.getInstance().Release();
    }


    public void testNote()  {
        UsrItem ui = AppModel.getUsrUtility().addUsr("wxm", "123456");
        assertNotNull(ui);

        LinkedList<PayNoteItem> ls_pay = new LinkedList<>();
        Date de = new Date();
        PayNoteItem pay_it = new PayNoteItem();
        pay_it.setUsr(ui);
        pay_it.setInfo("tax");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);

        pay_it = new PayNoteItem();
        //pay_it.setUsr(ui);
        pay_it.setInfo("water cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);

        pay_it = new PayNoteItem();
        pay_it.setUsr(ui);
        pay_it.setInfo("electrcity  cost");
        pay_it.setVal(new BigDecimal(12.34));
        pay_it.getTs().setTime(de.getTime());
        ls_pay.add(pay_it);

        LinkedList<IncomeNoteItem> ls_income = new LinkedList<>();
        IncomeNoteItem income_it = new IncomeNoteItem();
        income_it.setUsr(ui);
        income_it.setInfo("工资");
        income_it.setVal(new BigDecimal(12.34));
        income_it.getTs().setTime(de.getTime());
        ls_income.add(income_it);

        AppModel.getInstance().setCurUsr(ui);

        // test query
        assertEquals(3, AppModel.getPayIncomeUtility().AddPayNotes(ls_pay));
        List<PayNoteItem> pay_ret = AppModel.getPayIncomeUtility().GetAllPayNotes();
        assertEquals(3, pay_ret.size());

        String dtstr = UtilFun.TimestampToString(pay_it.getTs())
                            .substring(0, "yyyy-MM-dd".length());
        pay_ret = AppModel.getPayIncomeUtility().GetPayNotesByDay(dtstr);
        assertEquals(3, pay_ret.size());


        assertEquals(1, AppModel.getPayIncomeUtility().AddIncomeNotes(ls_income));
        List<IncomeNoteItem> income_ret = AppModel.getPayIncomeUtility().GetAllIncomeNotes();
        assertEquals(1, income_ret.size());

        dtstr = UtilFun.TimestampToString(income_it.getTs())
                .substring(0, "yyyy-MM-dd".length());
        income_ret = AppModel.getPayIncomeUtility().GetIncomeNotesByDay(dtstr);
        assertEquals(1, income_ret.size());

        // test modify
        String ni = "eat some thing";
        PayNoteItem mri = pay_ret.get(0);
        mri.setInfo(ni);
        LinkedList<PayNoteItem> lsmri = new LinkedList<>();
        lsmri.add(mri);
        AppModel.getPayIncomeUtility().ModifyPayNotes(lsmri);

        PayNoteItem nmri = AppModel.getPayIncomeUtility().GetPayNoteById(mri.getId());
        assertNotNull(nmri);
        assertEquals(ni, nmri.getInfo());
    }


    public void testUsrItem()    {
        assertFalse(AppModel.getUsrUtility().hasUsr("hugo"));
        assertNotNull(AppModel.getUsrUtility().addUsr("hugo", "123456"));
        assertNotNull(AppModel.getUsrUtility().addUsr("hugo", "654321"));
        assertTrue(AppModel.getUsrUtility().hasUsr("hugo"));

        assertFalse(AppModel.getUsrUtility().checkUsr("hugo", "123456"));
        assertTrue(AppModel.getUsrUtility().checkUsr("hugo", "654321"));
    }
}
