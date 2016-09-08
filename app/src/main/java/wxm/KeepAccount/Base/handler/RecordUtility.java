package wxm.KeepAccount.Base.handler;

import android.os.Handler;
import android.os.Message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.utility.ToolUtil;

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

            case AppMsgDef.MSG_RECORD_GET:
                GetRecord(msg);
                break;
        }
    }


    private static void DeleteRecords(Message am)    {
        Object[] arr = UtilFun.cast(am.obj);
        List<Integer> pay_ls = UtilFun.cast(arr[0]);
        List<Integer> income_ls = UtilFun.cast(arr[1]);

        boolean ret = false;
        if(null != pay_ls && 0 < pay_ls.size())     {
            int lssz = pay_ls.size();
            ret = lssz == AppModel.getPayIncomeUtility().DeletePayNotes(pay_ls);
        }

        if(null != income_ls && 0 < income_ls.size())     {
            int lssz = income_ls.size();
            ret &= lssz == AppModel.getPayIncomeUtility().DeleteIncomeNotes(income_ls);
        }

        // reply message
        Handler h = UtilFun.cast(arr[2]);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_DELETE_RECORDS, ret);
    }

    private static void DailyRecordsToDetailReport(Message am)    {
        Object[] arr = UtilFun.cast(am.obj);

        String date_str = UtilFun.cast(arr[0]);
        // load record & format output
        List<PayNoteItem> payls = AppModel.getPayIncomeUtility().GetPayNotesByDay(date_str);
        List<IncomeNoteItem> incomels = AppModel.getPayIncomeUtility().GetIncomeNotesByDay(date_str);
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        for(Object i : objls)   {
            boolean ispay = i instanceof PayNoteItem;
            PayNoteItem pi = ispay ? (PayNoteItem)i : null;
            IncomeNoteItem ii = ispay ? null : (IncomeNoteItem)i;

            String show_str = String.format(Locale.CHINA,
                                    "原因 : %s\n金额 : %.02f",
                                    ispay ? pi.getInfo() : ii.getInfo()
                                    ,ispay ? pi.getVal() : ii.getVal());
            String note = ispay ? pi.getNote() : ii.getNote();
            String bn = ispay ?
                            (null == pi.getBudget() ? null : pi.getBudget().getName())
                            : null;

            if(!UtilFun.StringIsNullOrEmpty(bn)) {
                show_str = String.format(Locale.CHINA, "%s\n使用预算 : %s"
                        ,show_str ,bn);
            }

            if(!UtilFun.StringIsNullOrEmpty(note)) {
                show_str = String.format(Locale.CHINA, "%s\n备注 : %s"
                                ,show_str ,note);
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(AppGobalDef.ITEM_TITLE, ispay ? AppGobalDef.CNSTR_RECORD_PAY
                                                : AppGobalDef.CNSTR_RECORD_INCOME);
            map.put(AppGobalDef.ITEM_TYPE, ispay ? AppGobalDef.CNSTR_RECORD_PAY
                                                : AppGobalDef.CNSTR_RECORD_INCOME);
            map.put(AppGobalDef.ITEM_TEXT, show_str);
            map.put(AppGobalDef.ITEM_ID, String.valueOf(ispay ? pi.getId() : ii.getId()));
            mylist.add(map);
        }

        /* Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        }); */

        // reply message
        Handler h = UtilFun.cast(arr[1]);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_TO_DAILY_DETAILREPORT, mylist);
    }

    private static void LoadAllRecords(Message am)
    {
        List<PayNoteItem> payls = AppModel.getPayIncomeUtility().GetAllPayNotes();
        List<IncomeNoteItem> incomels = AppModel.getPayIncomeUtility().GetAllIncomeNotes();
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        // reply message
        Object[] arr = UtilFun.cast(am.obj);
        Handler h = UtilFun.cast(arr[0]);
        GlobalMsgHandler.ReplyMsg(h ,AppMsgDef.MSG_LOAD_ALL_RECORDS
                ,new Object[] { objls });
    }

    private static void AllRecordsToYearReport(Message am)
    {
        // get year info from record
        List<PayNoteItem> payls = AppModel.getPayIncomeUtility().GetAllPayNotes();
        List<IncomeNoteItem> incomels = AppModel.getPayIncomeUtility().GetAllIncomeNotes();
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for(Object i : objls)   {
            String h_k = i instanceof PayNoteItem ?
                            ((PayNoteItem)i).getTs().toString().substring(0, 4)
                            : ((IncomeNoteItem)i).getTs().toString().substring(0, 4);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        ArrayList<String> set_k = new ArrayList<String>(hm_data.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            ArrayList<Object> v = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;

            for (Object r : v) {
                if(r instanceof PayNoteItem)  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(((PayNoteItem)r).getVal());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(((IncomeNoteItem)r).getVal());
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

        /*  Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        });  */

        Handler h = UtilFun.cast(am.obj);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_TO_YEARREPORT, mylist);
    }

    private static void AllRecordsToMonthReport(Message am)
    {
        // get months info from record
        List<PayNoteItem> payls = AppModel.getPayIncomeUtility().GetAllPayNotes();
        List<IncomeNoteItem> incomels = AppModel.getPayIncomeUtility().GetAllIncomeNotes();
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for(Object i : objls)   {
            String h_k = i instanceof PayNoteItem ?
                    ((PayNoteItem)i).getTs().toString().substring(0, 7)
                    : ((IncomeNoteItem)i).getTs().toString().substring(0, 7);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        ArrayList<String> set_k = new ArrayList<String>(hm_data.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            ArrayList<Object> v = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;

            for (Object r : v) {
                if(r instanceof PayNoteItem)  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(((PayNoteItem)r).getVal());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(((IncomeNoteItem)r).getVal());
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

        /* Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        }); */

        Handler h = UtilFun.cast(am.obj);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_TO_MONTHREPORT, mylist);
    }

    private static void AllRecordsToDayReport(Message am)
    {
        // get days info from record
        List<PayNoteItem> payls = AppModel.getPayIncomeUtility().GetAllPayNotes();
        List<IncomeNoteItem> incomels = AppModel.getPayIncomeUtility().GetAllIncomeNotes();
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for(Object i : objls)   {
            String h_k = i instanceof PayNoteItem ?
                    ((PayNoteItem)i).getTs().toString().substring(0, 10)
                    : ((IncomeNoteItem)i).getTs().toString().substring(0, 10);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        // format output
        ArrayList<HashMap<String, String>> mylist = new ArrayList<>();
        ArrayList<String> set_k = new ArrayList<String>(hm_data.keySet());
        Collections.sort(set_k);
        for (String k : set_k) {
            ArrayList<Object> v = hm_data.get(k);

            int pay_cout = 0;
            int income_cout = 0;
            BigDecimal pay_amount = BigDecimal.ZERO;
            BigDecimal income_amount = BigDecimal.ZERO;

            for (Object r : v) {
                if(r instanceof PayNoteItem)  {
                    pay_cout += 1;
                    pay_amount = pay_amount.add(((PayNoteItem)r).getVal());
                } else {
                    income_cout += 1;
                    income_amount = income_amount.add(((IncomeNoteItem)r).getVal());
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

        /* Collections.sort(mylist, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                return lhs.get(AppGobalDef.ITEM_TITLE).compareTo(rhs.get(AppGobalDef.ITEM_TITLE));
            }
        }); */

        Handler h = UtilFun.cast(am.obj);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_TO_DAYREPORT, mylist);
    }

    private static void GetRecord(Message am)  {
        Object[] arr = UtilFun.cast(am.obj);
        String ty = UtilFun.cast(arr[0]);
        Integer tag = UtilFun.cast(arr[1]);

        Object objret;
        if(ty.equals(AppGobalDef.CNSTR_RECORD_PAY))     {
            objret = AppModel.getPayIncomeUtility().GetPayNoteById(tag);
        }   else    {
            objret = AppModel.getPayIncomeUtility().GetIncomeNoteById(tag);
        }

        Handler h = UtilFun.cast(arr[2]);
        GlobalMsgHandler.ReplyMsg(h, AppMsgDef.MSG_RECORD_GET, new Object[] { ty, objret });
    }
}
