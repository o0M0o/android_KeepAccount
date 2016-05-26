package com.wxm.KeepAccount.BaseLib;

import android.content.Intent;
import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 处理数据辅助类
 * Created by 123 on 2016/5/15.
 */
public class RecordUtility {
    private static final String TAG = "RecordUtility";

    public static Object processMsg(AppMsg am)  {
        Object ret = null;
        switch (am.msg) {
            case AppMsgDef.MSG_LOAD_ALL_RECORDS: {
                ret = LoadAllRecords(am);
            }
            break;

            case AppMsgDef.MSG_TO_DAYREPORT: {
                ret = AllRecordsToDayReport(am);
            }
            break;

            case AppMsgDef.MSG_TO_MONTHREPORT: {
                ret = AllRecordsToMonthReport(am);
            }
            break;

            case AppMsgDef.MSG_TO_YEARREPORT: {
                ret = AllRecordsToYearReport(am);
            }
            break;

            case AppMsgDef.MSG_TO_DAILY_DETAILREPORT: {
                ret = DailyRecordsToDetailReport(am);
            }
            break;

            case AppMsgDef.MSG_DELETE_RECORDS: {
                ret = DeleteRecords(am);
            }
            break;

            case AppMsgDef.MSG_RECORD_ADD: {
                ret = AddRecord(am);
            }
            break;

            case AppMsgDef.MSG_RECORD_MODIFY: {
                ret = ModifyRecord(am);
            }
            break;

            case AppMsgDef.MSG_RECORD_GET: {
                ret = GetRecord(am);
            }
            break;
        }

        return ret;
    }

    private static Object DeleteRecords(AppMsg am)    {
        List<String> ls_del = (List<String>)am.obj;
        return AppModel.getInstance().DeleteRecords(ls_del);
    }


    private static Object DailyRecordsToDetailReport(AppMsg am)    {
        String date_str = (String)am.obj;

        // load record
        List<RecordItem> lr = AppModel.getInstance().GetRecordsByDay(date_str);

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        for (RecordItem r : lr) {
            String tit = "";
            String show_str = "";

            if ((r.record_type.equals("pay")) || (r.record_type.equals("支出"))) {
                tit = "支出";
            } else {
                tit = "收入";
            }

            if(r.record_note.isEmpty()) {
                show_str = String.format("原因 : %s\n金额 : %.02f",
                        r.record_info, r.record_val);
            }
            else    {
                show_str = String.format("原因 : %s\n金额 : %.02f\n备注 : %s",
                        r.record_info, r.record_val, r.record_note);
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, tit);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            map.put(AppGobalDef.ITEM_ID, String.format("%d", r._id));
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

    private static Object LoadAllRecords(AppMsg am)
    {
        return AppModel.getInstance().GetAllRecords();
    }

    private static Object AllRecordsToYearReport(AppMsg am)
    {
        // load record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();

        // get year info from record
        HashMap<String, ArrayList<RecordItem>> hm_data =
                new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.record_ts.toString().substring(0, 4);
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
                    String.format("支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }

    private static Object AllRecordsToMonthReport(AppMsg am)
    {
        // load record
        List<RecordItem> lr = AppModel.getInstance().GetAllRecords();

        // get months info from record
        HashMap<String, ArrayList<RecordItem>> hm_data =
                new HashMap<>();
        for (RecordItem record : lr) {
            String h_k = record.record_ts.toString().substring(0, 7);
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
                    String.format("支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }


    private static Object AllRecordsToDayReport(AppMsg am)
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
                    String.format("支出项 ： %d    总金额 ：%.02f\n收入项 ： %d    总金额 ：%.02f",
                            pay_cout, pay_amount, income_cout, income_amount);

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, k);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            mylist.add(map);
        }

        return mylist;
    }

    private static Object AddRecord(AppMsg am) {
        Intent data = (Intent)am.obj;
        RecordItem ri = data.getParcelableExtra(AppGobalDef.STR_RECORD);

        ArrayList<RecordItem> items = new ArrayList<>();
        items.add(ri);
        AppModel.getInstance().AddRecords(items);

        return new Object();
    }

    private static Object ModifyRecord(AppMsg am)   {
        Intent data = (Intent)am.obj;
        RecordItem ri = data.getParcelableExtra(AppGobalDef.STR_RECORD);

        ArrayList<RecordItem> items = new ArrayList<>();
        items.add(ri);
        AppModel.getInstance().ModifyRecords(items);

        return new Object();
    }

    private static Object GetRecord(AppMsg am)  {
        String tag = (String)am.obj;
        return AppModel.getInstance().GetRecord(tag);
    }
}
