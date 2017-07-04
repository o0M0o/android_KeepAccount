package wxm.KeepAccount.db;


import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import wxm.androidutil.DBHelper.DBUtilityBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.ui.utility.NoteDataHelper;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * 备忘本数据库工具类
 * Created by ookoo on 2016/11/16.
 */
public class PayIncomeDBUtility {
    private PayDBUtility mDUPay;
    private IncomeDBUtility mDUIncome;

    public PayIncomeDBUtility() {
        super();
        mDUPay = new PayDBUtility();
        mDUIncome = new IncomeDBUtility();
    }

    /**
     * 返回pay数据辅助类
     *
     * @return 辅助类
     */
    public PayDBUtility getPayDBUtility() {
        return mDUPay;
    }

    /**
     * 返回income数据辅助类
     *
     * @return 辅助类
     */
    public IncomeDBUtility getIncomeDBUtility() {
        return mDUIncome;
    }


    /**
     * 得到数据最后被更改时间
     *
     * @return 数据最后更新时间
     */
    public Timestamp getDataLastChangeTime() {
        Timestamp ts_p = mDUPay.getDataLastChangeTime();
        Timestamp ts_i = mDUIncome.getDataLastChangeTime();

        return ts_p.after(ts_i) ? ts_p : ts_i;
    }

    /**
     * 根据预算查找支出数据
     * 并根据支出数据更新预算
     *
     * @param bi 待匹配预算
     * @return 查找到的数据
     */
    public List<PayNoteItem> getPayNoteByBudget(BudgetItem bi) {
        List<PayNoteItem> ls_pay = ContextUtil.getDBHelper().getPayDataREDao()
                .queryForEq(PayNoteItem.FIELD_BUDGET, bi.get_id());

        bi.useBudget(BigDecimal.ZERO);
        if (!UtilFun.ListIsNullOrEmpty(ls_pay)) {
            BigDecimal all_pay = BigDecimal.ZERO;
            for (PayNoteItem i : ls_pay) {
                all_pay = all_pay.add(i.getVal());
            }

            bi.useBudget(all_pay);
        }

        ContextUtil.getDBHelper().getBudgetDataREDao().update(bi);
        return ls_pay;
    }

    /**
     * 获得当前用户所有支出/收入数据
     *
     * @return 当前用户的所有记录
     */
    private List<INote> getAllNotes() {
        List<PayNoteItem> payls = getPayDBUtility().getAllData();
        List<IncomeNoteItem> incomels = getIncomeDBUtility().getAllData();
        List<INote> objls = new LinkedList<>();
        if (null != payls)
            objls.addAll(payls);

        if (null != incomels)
            objls.addAll(incomels);

        return objls;
    }

    /**
     * 获得当前用户所有日度支出/收入数据
     * 并以 日期 --->  数据链表 方式返回数据
     *
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<INote>> getAllNotesToDay() {
        List<INote> ret = getAllNotes();

        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTsToStr().substring(0, 10);
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
     *
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<INote>> getAllNotesToMonth() {
        List<INote> ret = getAllNotes();
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTsToStr().substring(0, 7);
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
     *
     * @return 日期 --->  数据链表
     */
    public HashMap<String, ArrayList<INote>> getAllNotesToYear() {
        List<INote> ret = getAllNotes();
        HashMap<String, ArrayList<INote>> hm_data = new HashMap<>();
        for (INote i : ret) {
            String h_k = i.getTsToStr().substring(0, 4);
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
     *
     * @param lsi 待添加的记录集合
     * @return 返回添加成功的数据量
     */
    public int addPayNotes(List<PayNoteItem> lsi) {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        int ret = 0;
        if (null != cur_usr) {
            for (PayNoteItem i : lsi) {
                if (null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            ret = mDUPay.createDatas(lsi);
        } else {
            boolean nousr = false;
            for (PayNoteItem i : lsi) {
                if (null == i.getUsr()) {
                    nousr = true;
                    break;
                }
            }

            if (!nousr) {
                ret = mDUPay.createDatas(lsi);
            }
        }

        return ret;
    }


    /**
     * 添加收入记录
     *
     * @param lsi 待添加的记录集合
     * @return 返回添加成功的数据量
     */
    public int addIncomeNotes(List<IncomeNoteItem> lsi) {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        int ret = 0;
        if (null != cur_usr) {
            for (IncomeNoteItem i : lsi) {
                if (null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            ret = mDUIncome.createDatas(lsi);
        } else {
            boolean nousr = false;
            for (IncomeNoteItem i : lsi) {
                if (null == i.getUsr()) {
                    nousr = true;
                    break;
                }
            }

            if (!nousr) {
                ret = mDUIncome.createDatas(lsi);
            }
        }

        return ret;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     */
    public void deletePayNotes(List<Integer> lsi) {
        mDUPay.removeDatas(lsi);
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     */
    public void deleteIncomeNotes(List<Integer> lsi) {
        mDUIncome.removeDatas(lsi);
    }


    /**
     * 支出数据辅助类
     */
    public class PayDBUtility extends DBUtilityBase<PayNoteItem, Integer> {
        PayDBUtility() {
            super();
        }

        @Override
        protected RuntimeExceptionDao<PayNoteItem, Integer> getDBHelper() {
            return ContextUtil.getDBHelper().getPayDataREDao();
        }

        @Override
        public PayNoteItem getData(Integer id) {
            PayNoteItem pi = super.getData(id);
            if(null != pi)  {
                pi.setVal(pi.getVal());
                pi.setTs(pi.getTs());
            }

            return pi;
        }

        @Override
        public List<PayNoteItem> getAllData() {
            UsrItem ui = ContextUtil.getCurUsr();
            if (null == ui)
                return new ArrayList<>();

            List<PayNoteItem> rets = getDBHelper().queryForEq(PayNoteItem.FIELD_USR, ui.getId());
            for(PayNoteItem it : rets)  {
                it.setVal(it.getVal());
                it.setTs(it.getTs());
            }
            return rets;
        }

        @Override
        protected void onDataModify(List<Integer> md) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }

        @Override
        protected void onDataCreate(List<Integer> cd) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }

        @Override
        protected void onDataRemove(List<Integer> dd) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }
    }


    /**
     * 收入数据辅助类
     */
    public class IncomeDBUtility extends DBUtilityBase<IncomeNoteItem, Integer> {
        IncomeDBUtility() {
            super();
        }

        @Override
        protected RuntimeExceptionDao<IncomeNoteItem, Integer> getDBHelper() {
            return ContextUtil.getDBHelper().getIncomeDataREDao();
        }

        @Override
        public IncomeNoteItem getData(Integer id) {
            IncomeNoteItem pi = super.getData(id);
            if(null != pi)  {
                pi.setVal(pi.getVal());
                pi.setTs(pi.getTs());
            }

            return pi;
        }

        @Override
        public List<IncomeNoteItem> getAllData() {
            UsrItem ui = ContextUtil.getCurUsr();
            if (null == ui)
                return new ArrayList<>();

            List<IncomeNoteItem> rets = getDBHelper().queryForEq(PayNoteItem.FIELD_USR, ui.getId());
            for(IncomeNoteItem it : rets)   {
                it.setVal(it.getVal());
                it.setTs(it.getTs());
            }
            return rets;
        }

        @Override
        protected void onDataModify(List<Integer> md) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }

        @Override
        protected void onDataCreate(List<Integer> cd) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }

        @Override
        protected void onDataRemove(List<Integer> dd) {
            NoteDataHelper.getInstance().refreshData();
            EventBus.getDefault().post(new DBDataChangeEvent());
        }
    }
}


