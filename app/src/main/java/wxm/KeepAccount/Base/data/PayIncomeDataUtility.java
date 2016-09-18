package wxm.KeepAccount.Base.data;

import android.util.Log;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * 收支数据辅助类
 * Created by 123 on 2016/9/4.
 */
public class PayIncomeDataUtility {
    private final String    TAG = "PayIncomeDataUtility";

    public PayIncomeDataUtility()  {
    }

    /**
     * 根据预算查找支出数据
     * 并根据支出数据更新预算
     * @param bi  待匹配预算
     * @return 查找到的数据
     */
    public List<PayNoteItem> GetPayNoteByBudget(BudgetItem bi)  {
        List<PayNoteItem> ls_pay =  AppModel.getDBHelper().getPayDataREDao()
                                .queryForEq(PayNoteItem.FIELD_BUDGET, bi.get_id());

        bi.useBudget(BigDecimal.ZERO);
        if(!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
            BigDecimal all_pay = BigDecimal.ZERO;
            for(PayNoteItem i : ls_pay) {
                all_pay = all_pay.add(i.getVal());
            }

            bi.useBudget(all_pay);
        }

        AppModel.getDBHelper().getBudgetDataREDao().update(bi);
        return ls_pay;
    }


    /**
     * 根据ID查找支出数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public PayNoteItem GetPayNoteById(int id)  {
        return AppModel.getDBHelper().getPayDataREDao().queryForId(id);
    }

    /**
     * 根据ID查找收入数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public IncomeNoteItem GetIncomeNoteById(int id)  {
        return AppModel.getDBHelper().getIncomeDataREDao().queryForId(id);
    }


    /**
     * 获得当前用户所有支出/收入数据
     * @return 当前用户的所有记录
     */
    public List<Object> GetAllNotes()   {
        List<PayNoteItem> payls = GetAllPayNotes();
        List<IncomeNoteItem> incomels = GetAllIncomeNotes();
        List<Object> objls = new LinkedList<>();
        if(null != payls)
            objls.addAll(payls);

        if(null != incomels)
            objls.addAll(incomels);

        return objls;
    }

    /**
     * 获得当前用户所有日度支出/收入数据
     * 并以 日期 --->  数据链表 方式返回数据
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<Object>> GetAllNotesToDay()    {
        List<Object> ret = GetAllNotes();
        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for (Object i : ret) {
            String h_k = i instanceof PayNoteItem ?
                    ((PayNoteItem) i).getTs().toString().substring(0, 10)
                    : ((IncomeNoteItem) i).getTs().toString().substring(0, 10);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        return hm_data;
    }

    /**
     * 获得当前用户所有月度支出/收入数据
     * 并以 日期 --->  数据链表 方式返回数据
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<Object>> GetAllNotesToMonth()    {
        List<Object> ret = GetAllNotes();
        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for (Object i : ret) {
            String h_k = i instanceof PayNoteItem ?
                    ((PayNoteItem) i).getTs().toString().substring(0, 7)
                    : ((IncomeNoteItem) i).getTs().toString().substring(0, 7);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        return hm_data;
    }

    /**
     * 获得当前用户所有年度支出/收入数据
     * 并以 日期 --->  数据链表 方式返回数据
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<Object>> GetAllNotesToYear()    {
        List<Object> ret = GetAllNotes();
        HashMap<String, ArrayList<Object>> hm_data = new HashMap<>();
        for (Object i : ret) {
            String h_k = i instanceof PayNoteItem ?
                    ((PayNoteItem) i).getTs().toString().substring(0, 4)
                    : ((IncomeNoteItem) i).getTs().toString().substring(0, 4);

            ArrayList<Object> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<Object> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        return hm_data;
    }

    /**
     * 获得当前用户所有支出数据
     * @return 当前用户的所有记录
     */
    public List<PayNoteItem> GetAllPayNotes()     {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getPayDataREDao()
                    .queryForEq(PayNoteItem.FIELD_USR, cur_usr.getId());
    }


    /**
     * 获得当前用户所有收入数据
     * @return 当前用户的所有记录
     */
    public List<IncomeNoteItem> GetAllIncomeNotes()     {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getIncomeDataREDao()
                .queryForEq(IncomeNoteItem.FIELD_USR, cur_usr.getId());
    }



    /**
     * 根据日期条件(例如 : '2016-05-07')获得支出数据
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<PayNoteItem> GetPayNotesByDay(String day_str)   {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        Timestamp tsb ;
        Timestamp tse;
        try {
            tsb = ToolUtil.StringToTimestamp(day_str);
            tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, UtilFun.ExceptionToString(e));
            return null;
        }

        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        List<PayNoteItem> ret;
        try {
            ret = mDBHelper.getPayDataREDao().queryBuilder()
                    .where().eq(PayNoteItem.FIELD_USR, cur_usr.getId())
                            .and().between(PayNoteItem.FIELD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = null;
        }

        return ret;
    }


    /**
     * 根据日期条件(例如 : '2016-05-07')获得收入数据
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<IncomeNoteItem> GetIncomeNotesByDay(String day_str)   {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        Timestamp tsb;
        Timestamp tse;
        try {
            tsb = ToolUtil.StringToTimestamp(day_str);
            tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, UtilFun.ExceptionToString(e));
            return null;
        }

        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        List<IncomeNoteItem> ret;
        try {
            ret = mDBHelper.getIncomeDataREDao().queryBuilder()
                    .where().eq(IncomeNoteItem.FIELD_USR, cur_usr.getId())
                            .and().between(IncomeNoteItem.FIELD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = null;
        }

        return ret;
    }


    /**
     * 添加支出记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int AddPayNotes(List<PayNoteItem> lsi)    {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null != cur_usr) {
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            return mDBHelper.getPayDataREDao().create(lsi);
        }
        else    {
            boolean nousr = false;
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr)
                return mDBHelper.getPayDataREDao().create(lsi);
        }

        return 0;
    }


    /**
     * 添加收入记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int AddIncomeNotes(List<IncomeNoteItem> lsi)    {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null != cur_usr) {
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            return mDBHelper.getIncomeDataREDao().create(lsi);
        }
        else    {
            boolean nousr = false;
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr)
                return mDBHelper.getIncomeDataREDao().create(lsi);
        }

        return 0;
    }


    /**
     * 修改支出记录
     * @param lsi 待修改数据
     * @return  修改成功的记录数
     */
    public int ModifyPayNotes(List<PayNoteItem> lsi)  {
        int ret = 0;
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        for(PayNoteItem i : lsi) {
            ret += mDBHelper.getPayDataREDao().update(i);
        }

        return ret;
    }


    /**
     * 修改收入记录
     * @param lsi 待修改数据
     * @return  修改成功的记录数
     */
    public int ModifyIncomeNotes(List<IncomeNoteItem> lsi)  {
        int ret = 0;
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        for(IncomeNoteItem i : lsi) {
            ret += mDBHelper.getIncomeDataREDao().update(i);
        }

        return ret;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  删除的记录数
     */
    public int DeletePayNotes(List<Integer> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        return mDBHelper.getPayDataREDao().deleteIds(lsi);
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  删除的记录数
     */
    public int DeleteIncomeNotes(List<Integer> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        return mDBHelper.getIncomeDataREDao().deleteIds(lsi);
    }
}
