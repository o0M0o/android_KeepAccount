package com.wxm.KeepAccount.Base.handler;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.wxm.KeepAccount.Base.data.AppGobalDef;
import com.wxm.KeepAccount.Base.data.AppModel;
import com.wxm.KeepAccount.Base.data.AppMsgDef;
import com.wxm.KeepAccount.Base.utility.ToolUtil;
import com.wxm.KeepAccount.Base.data.RecordItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 处理数据辅助类
 * Created by 123 on 2016/5/15.
 */
public class RecordUtility {
    private static final String TAG = "RecordUtility";

    public static void doMsg(Message msg)   {
        switch (msg.what) {
            case AppMsgDef.MSG_LOAD_ALL_RECORDS:
                LoadAllRecords(msg);
                break;

            case AppMsgDef.MSG_TO_DAYREPORT:
                AllRecordsToDayReport(msg);
                break;

            case AppMsgDef.MSG_TO_MONTHREPORT:
                AllRecordsToMonthReport(msg);
                break;

            case AppMsgDef.MSG_TO_YEARREPORT:
                AllRecordsToYearReport(msg);
                break;

            case AppMsgDef.MSG_TO_DAILY_DETAILREPORT:
                DailyRecordsToDetailReport(msg);
                break;

            case AppMsgDef.MSG_DELETE_RECORDS:
                DeleteRecords(msg);
                break;

            case AppMsgDef.MSG_RECORD_ADD:
                AddRecord(msg);
                break;

            case AppMsgDef.MSG_RECORD_MODIFY:
                ModifyRecord(msg);
                break;

            case AppMsgDef.MSG_RECORD_GET:
                GetRecord(msg);
                break;
        }
    }

    private static void DeleteRecords(Message am)    {
        Object[] arr = ToolUtil.cast(am.obj);
        List<String> ls_del = ToolUtil.cast(arr[0]);
        assert null != ls_del;
        boolean ret = AppModel.getInstance().DeleteRecords(ls_del);

        // reply message
        Handler h = ToolUtil.cast(arr[1]);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_DELETE_RECORDS;
        m.obj = ret;
        m.sendToTarget();
    }

    private static void DailyRecordsToDetailReport(Message am)    {
        Object[] arr = ToolUtil.cast(am.obj);

        String date_str = ToolUtil.cast(arr[0]);
        // load record & format output
        List<RecordItem> lr = AppModel.getInstance().GetRecordsByDay(date_str);
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        for (RecordItem r : lr) {
            String tit = r.getRecord_type();
            String show_str;
            if(ToolUtil.StringIsNullOrEmpty(r.getRecord_note())) {
                show_str = String.format(Locale.CHINA,
                        "原因 : %s\n金额 : %.02f",
                        r.getRecord_info(), r.getRecord_val());
            }
            else    {
                show_str = String.format(Locale.CHINA,
                        "原因 : %s\n金额 : %.02f\n备注 : %s",
                        r.getRecord_info(), r.getRecord_val(), r.getRecord_note());
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, tit);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            map.put(AppGobalDef.ITEM_ID, String.format(Locale.CHINA, "%d", r.get_id()));
            mylist.add(map);
        }

        Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });

        // reply message
        Handler h = ToolUtil.cast(arr[1]);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_TO_DAILY_DETAILREPORT;
        m.obj = mylist;
        m.sendToTarget();
    }

    private static void LoadAllRecords(Message am)
    {
        List<RecordItem> ret = AppModel.getInstance().GetAllRecords();
    }

    private static void AllRecordsToYearReport(Message am)
    {
        // get year info from record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();
        HashMap<String, ArrayList<RecordItem>> hm_data = new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.getRecord_ts().toString().substring(0, 4);
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
                if(AppGobalDef.CNSTR_RECORD_PAY.equals(r.getRecord_type()))  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.getRecord_val());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(r.getRecord_val());
                }
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, ToolUtil.FormatDateString(k));
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });

        Handler h = ToolUtil.cast(am.obj);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_TO_YEARREPORT;
        m.obj = mylist;
        m.sendToTarget();
    }

    private static void AllRecordsToMonthReport(Message am)
    {
        // get months info from record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();
        HashMap<String, ArrayList<RecordItem>> hm_data = new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.getRecord_ts().toString().substring(0, 7);
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
                if(AppGobalDef.CNSTR_RECORD_PAY.equals(r.getRecord_type()))  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.getRecord_val());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(r.getRecord_val());
                }
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, ToolUtil.FormatDateString(k));
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });

        Handler h = ToolUtil.cast(am.obj);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_TO_MONTHREPORT;
        m.obj = mylist;
        m.sendToTarget();
    }

    private static void AllRecordsToDayReport(Message am)
    {
        // get days info from record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();
        HashMap<String, ArrayList<RecordItem>> hm_data = new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.getRecord_ts().toString().substring(0, 10);
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
                if(AppGobalDef.CNSTR_RECORD_PAY.equals(r.getRecord_type()))  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(r.getRecord_val());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(r.getRecord_val());
                }
            }

            String show_str =
                    String.format(Locale.CHINA,
                            "支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, ToolUtil.FormatDateString(k));
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });

        Handler h = ToolUtil.cast(am.obj);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_TO_DAYREPORT;
        m.obj = mylist;
        m.sendToTarget();
    }

    private static void AddRecord(Message am) {
        Object[] arr = ToolUtil.cast(am.obj);

        Intent data = ToolUtil.cast(arr[0]);
        RecordItem ri = data.getParcelableExtra(AppGobalDef.STR_RECORD);
        ArrayList<RecordItem> items = new ArrayList<>();
        items.add(ri);
        AppModel.getInstance().AddRecords(items);

        Handler h = ToolUtil.cast(arr[1]);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_RECORD_ADD;
        m.obj = true;
        m.sendToTarget();
    }

    private static void ModifyRecord(Message am)   {
        Object[] arr = ToolUtil.cast(am.obj);

        Intent data = ToolUtil.cast(arr[0]);
        RecordItem ri = data.getParcelableExtra(AppGobalDef.STR_RECORD);
        ArrayList<RecordItem> items = new ArrayList<>();
        items.add(ri);
        AppModel.getInstance().ModifyRecords(items);

        Handler h = ToolUtil.cast(arr[1]);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.arg1 = AppMsgDef.MSG_RECORD_MODIFY;
        m.obj = true;
        m.sendToTarget();
    }

    private static void GetRecord(Message am)  {
        Object[] arr = ToolUtil.cast(am.obj);

        String tag = ToolUtil.cast(arr[0]);
        RecordItem ri = AppModel.getInstance().GetRecord(tag);

        Handler h = ToolUtil.cast(arr[1]);
        Message m = Message.obtain(h, AppMsgDef.MSG_REPLY);
        m.obj = ri;
        m.arg1 = AppMsgDef.MSG_RECORD_GET;
        m.sendToTarget();
    }
}
