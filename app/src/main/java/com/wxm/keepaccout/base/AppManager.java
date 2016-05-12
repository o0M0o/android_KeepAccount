package com.wxm.keepaccout.base;


import android.app.Activity;
import android.app.backup.RestoreObserver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.wxm.keepaccount.DBManager;
import com.wxm.keepaccount.R;
import com.wxm.keepaccount.RecordItem;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


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

            case AppMsgDef.MSG_DAILY_RECORDS_TO_DETAILREPORT: {
                ret = DailyRecordsToDetailReport(am);
            }
            break;

            case AppMsgDef.MSG_DELETE_RECORDS: {
                ret = DeleteRecords(am);
            }
            break;

            case AppMsgDef.MSG_ADD_RECORD: {
                ret = AddRecord(am);
            }
            break;
        }

        return ret;
    }

    private Object DeleteRecords(AppMsg am)    {
        List<String> ls_del = (List<String>)am.obj;
        return AppModel.getInstance().DeleteRecords(ls_del);
    }


    private Object DailyRecordsToDetailReport(AppMsg am)    {
        String date_str = (String)am.obj;

        // load record
        List<RecordItem> lr = AppModel.getInstance().GetRecordsByDay(date_str);

        // format output
        int pay_cout = 0;
        int income_cout = 0;
        BigDecimal pay_amount = BigDecimal.ZERO;
        BigDecimal income_amount = BigDecimal.ZERO;
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        for (RecordItem r : lr) {
            String tit = "";
            String show_str = "";
            if ((r.record_type.equals("pay")) || (r.record_type.equals("支出"))) {
                pay_cout += 1;
                pay_amount = pay_amount.add(r.record_val);

                tit = "支出";
                show_str = String.format("原因 : %s\n金额 : %.02f",
                                r.record_info, r.record_val);
            } else {
                income_cout += 1;
                income_amount = income_amount.add(r.record_val);

                tit = "收入";
                show_str = String.format("原因 : %s\n金额 : %.02f",
                        r.record_info, r.record_val);
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, tit);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            map.put(AppGobalDef.TEXT_ITEMID, String.format("%d", r._id));
            mylist.add(map);
        }

        Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });

        return mylist;
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
        AppModel.getInstance().AddRecords(items);

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
        AppModel.getInstance().AddRecords(items);

        return new Object();
    }

    private Object LoadAllRecords(AppMsg am)
    {
        return AppModel.getInstance().GetAllRecords();
    }

    private Object AllRecordsToDayReport(AppMsg am)
    {
        // load record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();

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
                    String.format("支出笔数 ： %d\t总金额 ：%.02f\n收入笔数 ： %d\t总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }

    private Object AddRecord(AppMsg am) {
        Resources res = ((Activity)am.sender).getResources();
        ArrayList<RecordItem> items = new ArrayList<>();
        Intent data = (Intent)am.obj;
        RecordItem ri = new RecordItem();
        ri.record_type = data.getStringExtra(res.getString(R.string.record_type));
        ri.record_info = data.getStringExtra(res.getString(R.string.record_info));
        ri.record_val = new BigDecimal(
                data.getStringExtra(
                        res.getString(R.string.record_amount)));

        String str_dt = data.getStringExtra(
                res.getString(R.string.record_date));
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
        AppModel.getInstance().AddRecords(items);

        return new Object();
    }

}
