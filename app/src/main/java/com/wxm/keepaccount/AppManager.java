package com.wxm.keepaccount;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.content.res.Resources;
import android.util.Log;

import com.wxm.keepaccout.base.AppGobalDef;
import com.wxm.keepaccout.base.AppMsg;
import com.wxm.keepaccout.base.AppMsgDef;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * Created by 123 on 2016/5/6.
 */
public class AppManager {
    private static final String TAG = "AppManager";

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

            case AppMsgDef.MSG_ADD_PAY_RECORD: {
                ret = AddPayRecord(am);
            }
            break;

            case AppMsgDef.MSG_ADD_INCOME_RECORD: {
                ret = AddIncomeRecord(am);
            }
            break;
        }

        return ret;
    }

    private Object AddPayRecord(AppMsg am)
    {
        Resources res = ((Activity)am.sender).getResources();
        ArrayList<RecordItem> items = new ArrayList<>();
        Intent data = (Intent)am.obj;
        RecordItem ri = new RecordItem();
        ri.record_type = "支出";
        ri.record_info = data.getStringExtra(res.getString(R.string.pay_type));
        ri.record_val = new BigDecimal(
                data.getStringExtra(
                        res.getString(R.string.pay_val)));

        String str_dt = data.getStringExtra(
                res.getString(R.string.pay_date));
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            ri.record_ts.setTime(df.parse(str_dt).getTime());
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format("解析'%s'到日期失败", str_dt));

            Date dt = new Date();
            ri.record_ts.setTime(dt.getTime());
        }

        items.add(ri);
        DBManager dbm = new DBManager((Context)am.sender);
        dbm.add(items);

        return new Object();
    }

    private Object AddIncomeRecord(AppMsg am)
    {
        Resources res = ((Activity)am.sender).getResources();
        ArrayList<RecordItem> items = new ArrayList<>();
        Intent data = (Intent)am.obj;
        RecordItem ri = new RecordItem();
        ri.record_type = "收入";
        ri.record_info = data.getStringExtra(res.getString(R.string.income_type));
        ri.record_val = new BigDecimal(
                data.getStringExtra(
                        res.getString(R.string.income_val)));

        String str_dt = data.getStringExtra(
                res.getString(R.string.income_date));
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            ri.record_ts.setTime(df.parse(str_dt).getTime());
        }
        catch(Exception ex)
        {
            Log.e(TAG, String.format("解析'%s'到日期失败", str_dt));

            Date dt = new Date();
            ri.record_ts.setTime(dt.getTime());
        }

        items.add(ri);
        DBManager dbm = new DBManager((Context)am.sender);
        dbm.add(items);

        return new Object();
    }

    private Object LoadAllRecords(AppMsg am)
    {
        DBManager dbm = new DBManager((Context)am.sender);
        List<RecordItem> lr = dbm.query();
        return lr;
    }

    private Object AllRecordsToDayReport(AppMsg am)
    {
        // load record
        DBManager dbm = new DBManager((Context)am.sender);
        List<RecordItem> lr = dbm.query();

        // get days info from record
        HashMap<String, ArrayList<RecordItem>> hm_data =
                new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.record_ts.toString().substring(0, 10);
            ArrayList<RecordItem> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<RecordItem> v = new ArrayList<>();
                v.add(record);
                hm_data.put(h_k, v);
            } else {
                h_v.add(record);
            }
        }

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
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

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }

}
