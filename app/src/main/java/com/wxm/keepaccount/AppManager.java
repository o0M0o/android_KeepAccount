package com.wxm.keepaccount;


import android.content.Context;

import com.wxm.keepaccout.base.AppGobalDef;
import com.wxm.keepaccout.base.AppMsg;
import com.wxm.keepaccout.base.AppMsgDef;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 123 on 2016/5/6.
 */
public class AppManager {
    private static AppManager ourInstance = new AppManager();
    public static AppManager getInstance() {
        return ourInstance;
    }

    private AppManager() {
    }

    /**
     * 处理app中消息
     * @param am 待处理消息
     * @return  消息处理结果
     */
    public Object ProcessAppMsg(AppMsg am)
    {
        Object ret = null;
        switch (am.msg)
        {
            case AppMsgDef.MSG_LOAD_ALL_RECORDS: {
                ret = LoadAllRecords(am);
            }
            break;

            case AppMsgDef.MSG_ALL_RECORDS_TO_DAYREPORT : {
                ret = AllRecordsToDayReport(am);
            }
            break;

        }

        return ret;
    }


    private Object LoadAllRecords(AppMsg am)
    {
        DBManager dbm = new DBManager((Context)am.obj);
        List<RecordItem> lr = dbm.query();
        return lr;
    }

    private Object AllRecordsToDayReport(AppMsg am)
    {
        // load record
        DBManager dbm = new DBManager((Context)am.obj);
        List<RecordItem> lr = dbm.query();

        // get days info from record
        HashMap<String, ArrayList<RecordItem>> hm_data =
                new HashMap<String, ArrayList<RecordItem>>();
        for (RecordItem record : lr) {
            String h_k = record.record_ts.toString().substring(0, 10);
            ArrayList<RecordItem> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<RecordItem> v = new ArrayList<RecordItem>();
                v.add(record);
                hm_data.put(h_k, v);
            } else {
                h_v.add(record);
            }
        }

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        ArrayList<String> set_k = new ArrayList<String>(hm_data.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            ArrayList<RecordItem> v = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;

            for (RecordItem r : v) {
                if ((r.record_type.equals("pay")) || (r.record_type.equals("支出"))) {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.record_val);
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(r.record_val);
                }
            }

            String show_str =
                    String.format("支出笔数 ： %d, 支出金额 ：%.02f\n收入笔数 ： %d, 收入金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }

}
