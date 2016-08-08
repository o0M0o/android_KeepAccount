package com.wxm.KeepAccountAndriodTest;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.data.RecordItem;
import com.wxm.KeepAccount.Base.data.UsrItem;
import com.wxm.KeepAccount.Base.utility.ToolUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        AppModel.getInstance().ClearDB();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        AppModel.Release();
    }


    public void testRecordItem()    {
        UsrItem ui = AppModel.getInstance().addUsr("wxm", "123456");
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

        AppModel.getInstance().AddRecords(lsit);
        assertNull(AppModel.getInstance().GetAllRecords());

        AppModel.getInstance().setCurUsr(ui);
        List<RecordItem> rets = AppModel.getInstance().GetAllRecords();
        assertEquals(rets.size(), 4);

        String dtstr = ToolUtil.TimestampToString(ri.getTs())
                            .substring(0, "yyyy-MM-dd".length());
        List<RecordItem> rets1 = AppModel.getInstance().GetRecordsByDay(dtstr);
        assertEquals(rets1.size(), 4);

        String ni = "eat some thing";
        RecordItem mri = rets1.get(0);
        mri.setInfo(ni);
        LinkedList<RecordItem> lsmri = new LinkedList<>();
        lsmri.add(mri);
        AppModel.getInstance().ModifyRecords(lsmri);

        RecordItem nmri = AppModel.getInstance().GetRecordById(mri.getId());
        assertEquals(mri.getInfo(), nmri.getInfo());
    }


    public void testUsrItem()    {
        assertFalse(AppModel.getInstance().hasUsr("hugo"));
        assertNotNull(AppModel.getInstance().addUsr("hugo", "123456"));
        assertNotNull(AppModel.getInstance().addUsr("hugo", "654321"));
        assertTrue(AppModel.getInstance().hasUsr("hugo"));

        assertFalse(AppModel.getInstance().checkUsr("hugo", "123456"));
        assertTrue(AppModel.getInstance().checkUsr("hugo", "654321"));
    }
}
