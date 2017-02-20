package wxm.KeepAccount.db;


import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.DBHelper.DBUtilityBase;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.KeepAccount.utility.ToolUtil;

/**
 * 备忘本数据库工具类
 * Created by ookoo on 2016/11/16.
 */
public class PayIncomeDBUtility  {
    private PayDBUtility        mDUPay;
    private IncomeDBUtility     mDUIncome;

    public PayIncomeDBUtility() {
        super();
        mDUPay      = new PayDBUtility();
        mDUIncome   = new IncomeDBUtility();
    }

    /**
     * 返回pay数据辅助类
     * @return  辅助类
     */
    public PayDBUtility getPayDBUtility()   {
        return mDUPay;
    }

    /**
     * 返回income数据辅助类
     * @return  辅助类
     */
    public IncomeDBUtility getIncomeDBUtility()   {
        return mDUIncome;
    }


    /**
     * 得到数据最后被更改时间
     * @return  数据最后更新时间
     */
    public Timestamp getDataLastChangeTime()    {
        Timestamp ts_p = mDUPay.getDataLastChangeTime();
        Timestamp ts_i = mDUIncome.getDataLastChangeTime();

        return ts_p.after(ts_i) ? ts_p : ts_i;
    }

    /**
     * 根据预算查找支出数据
     * 并根据支出数据更新预算
     * @param bi  待匹配预算
     * @return 查找到的数据
     */
    public List<PayNoteItem> getPayNoteByBudget(BudgetItem bi)  {
        List<PayNoteItem> ls_pay =  ContextUtil.getDBHelper().getPayDataREDao()
                .queryForEq(PayNoteItem.FIELD_BUDGET, bi.get_id());

        bi.useBudget(BigDecimal.ZERO);
        if(!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
            BigDecimal all_pay = BigDecimal.ZERO;
            for(PayNoteItem i : ls_pay) {
                all_pay = all_pay.add(i.getVal());
            }

            bi.useBudget(all_pay);
        }

        ContextUtil.getDBHelper().getBudgetDataREDao().update(bi);
        return ls_pay;
    }

    /**
     * 获得当前用户所有支出/收入数据
     * @return 当前用户的所有记录
     */
    private List<INote> getAllNotes()   {
        List<PayNoteItem> payls = getPayDBUtility().getAllData();
        List<IncomeNoteItem> incomels = getIncomeDBUtility().getAllData();
        List<INote> objls = new LinkedList<>();
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
    public HashMap<String, ArrayList<INote>> getAllNotesToDay()    {
        List<INote> ret = getAllNotes();
        //Collections.sort(ret, (t1, t2) -> t1.getTs().compareTo(t2.getTs()));

        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTs().toString().substring(0, 10);
            ArrayList<INote> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<INote> v = new ArrayList<>();
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
    public HashMap<String, ArrayList<INote>> getAllNotesToMonth()    {
        List<INote> ret = getAllNotes();
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTs().toString().substring(0, 7);
            ArrayList<INote> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<INote> v = new ArrayList<>();
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
    public HashMap<String, ArrayList<INote>> getAllNotesToYear()    {
        List<INote> ret = getAllNotes();
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTs().toString().substring(0, 4);
            ArrayList<INote> h_v = hm_data.get(h_k);
            if (null == h_v) {
                ArrayList<INote> v = new ArrayList<>();
                v.add(i);
                hm_data.put(h_k, v);
            } else {
                h_v.add(i);
            }
        }

        return hm_data;
    }


    /**
     * 添加支出记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int addPayNotes(List<PayNoteItem> lsi)    {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        int ret = 0;
        if(null != cur_usr) {
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            ret = mDUPay.createDatas(lsi);
        }
        else    {
            boolean nousr = false;
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr) {
                ret = mDUPay.createDatas(lsi);
            }
        }

        return ret;
    }


    /**
     * 添加收入记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int addIncomeNotes(List<IncomeNoteItem> lsi)    {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        int ret = 0;
        if(null != cur_usr) {
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            ret = mDUIncome.createDatas(lsi);
        }
        else    {
            boolean nousr = false;
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr) {
                ret = mDUIncome.createDatas(lsi);
            }
        }

        return ret;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  删除的记录数
     */
    public int deletePayNotes(List<Integer> lsi)  {
        DBOrmLiteHelper mDBHelper = ContextUtil.getDBHelper();
        int ret = mDBHelper.getPayDataREDao().deleteIds(lsi);
        return ret;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  删除的记录数
     */
    public int deleteIncomeNotes(List<Integer> lsi)  {
        DBOrmLiteHelper mDBHelper = ContextUtil.getDBHelper();
        int ret = mDBHelper.getIncomeDataREDao().deleteIds(lsi);
        return ret;
    }


    public class PayDBUtility extends DBUtilityBase<PayNoteItem, Integer>   {
        public PayDBUtility() {
            super();
        }

        @Override
        protected RuntimeExceptionDao<PayNoteItem, Integer> getDBHelper() {
            return ContextUtil.getDBHelper().getPayDataREDao();
        }


        @Override
        public List<PayNoteItem> getAllData() {
            UsrItem ui = ContextUtil.getCurUsr();
            if(null == ui)
                return new ArrayList<>();

            return getDBHelper().queryForEq(PayNoteItem.FIELD_USR, ui.getId());
        }
    }


    public class IncomeDBUtility extends DBUtilityBase<IncomeNoteItem, Integer>   {
        public IncomeDBUtility() {
            super();
        }

        @Override
        protected RuntimeExceptionDao<IncomeNoteItem, Integer> getDBHelper() {
            return ContextUtil.getDBHelper().getIncomeDataREDao();
        }

        @Override
        public List<IncomeNoteItem> getAllData() {
            UsrItem ui = ContextUtil.getCurUsr();
            if(null == ui)
                return new ArrayList<>();

            return getDBHelper().queryForEq(PayNoteItem.FIELD_USR, ui.getId());
        }
    }
}


