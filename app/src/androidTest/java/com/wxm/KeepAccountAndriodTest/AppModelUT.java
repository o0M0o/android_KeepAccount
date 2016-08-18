package com.wxm.KeepAccountAndriodTest;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.db.RecordItem;
import com.wxm.KeepAccount.Base.db.UsrItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;

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


    public void testRecordItem()    {
        UsrItem ui = AppModel.getUsrUtility().addUsr("wxm", "123456");
        assertNotNull(ui);

        LinkedList<RecordItem> lsit = new LinkedList<>();
        Date de = new Date();
        RecordItem ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("pay");
        ri.setInfo("tax");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("pay");
        ri.setInfo("water cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("pay");
        ri.setInfo("electrcity cost");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        ri = new RecordItem();
        ri.setUsr(ui);
        ri.setType("income");
        ri.setInfo("工资");
        ri.setVal(new BigDecimal(12.34));
        ri.getTs().setTime(de.getTime());
        lsit.add(ri);

        assertEquals(0, AppModel.getRecordUtility().AddRecords(lsit));
        assertNull(AppModel.getRecordUtility().GetAllRecords());

        AppModel.getInstance().setCurUsr(ui);
        assertEquals(4, AppModel.getRecordUtility().AddRecords(lsit));
        List<RecordItem> rets = AppModel.getRecordUtility().GetAllRecords();
        assertEquals(rets.size(), 4);

        String dtstr = UtilFun.TimestampToString(ri.getTs())
                            .substring(0, "yyyy-MM-dd".length());
        List<RecordItem> rets1 = AppModel.getRecordUtility().GetRecordsByDay(dtstr);
        assertEquals(rets1.size(), 4);

        String ni = "eat some thing";
        RecordItem mri = rets1.get(0);
        mri.setInfo(ni);
        LinkedList<RecordItem> lsmri = new LinkedList<>();
        lsmri.add(mri);
        AppModel.getRecordUtility().ModifyRecords(lsmri);

        RecordItem nmri = AppModel.getRecordUtility().GetRecordById(mri.getId());
        assertEquals(mri.getInfo(), nmri.getInfo());
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
