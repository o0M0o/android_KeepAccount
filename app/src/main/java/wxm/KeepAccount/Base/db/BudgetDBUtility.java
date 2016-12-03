package wxm.KeepAccount.Base.db;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.DBHelper.DBUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.Base.data.UsrItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * 预算数据工具类
 * Created by 123 on 2016/9/1.
 */
public class BudgetDBUtility extends DBUtilityBase<BudgetItem, Integer> {
    private final static String  TAG = "BudgetDBUtility";

    public BudgetDBUtility()  {
        super();
    }

    @Override
    protected RuntimeExceptionDao<BudgetItem, Integer> getDBHelper() {
        return ContextUtil.getDBHelper().getBudgetDataREDao();
    }

    /**
     * 根据当前用户查找BudgetItem数据,且附带支出数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public HashMap<BudgetItem, List<PayNoteItem>> getBudgetWithPayNote()  {
        HashMap<BudgetItem, List<PayNoteItem>>  bret = new HashMap<>();
        List<BudgetItem> ls_bi = getBudgetForCurUsr();
        if(null != ls_bi)   {
            for(BudgetItem bi : ls_bi)  {
                List<PayNoteItem> pi = ContextUtil.getPayIncomeUtility().getPayNoteByBudget(bi);
                
                BudgetItem nbi = getData(bi.get_id());
                bret.put(nbi, pi);
            }
        }

        return bret;
    }

    /**
     * 根据当前用户查找BudgetItem数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public List<BudgetItem> getBudgetForCurUsr()  {
        UsrItem cur_usr = ContextUtil.getCurUsr();
        if(null == cur_usr)
            return null;

        List<BudgetItem> lsret = getDBHelper().queryForEq(BudgetItem.FIELD_USR, cur_usr);
        if((null == lsret) || (0 == lsret.size()))
            return null;

        return lsret;
    }


// --Commented out by Inspection START (2016/9/18 12:55):
//    /**
//     * 根据ID更新并获取预算数据
//     * @param biid 预算ID
//     * @return 查找到的数据,没有数据时返回{@code NULL}
//     */
//    public boolean RefrashBudgetById(int biid) {
//        BudgetItem bi = AppModel.getBudgetUtility().GetBudgetById(biid);
//        if(null == bi)
//            return false;
//
//        RuntimeExceptionDao<PayNoteItem, Integer> pired = AppModel.getDBHelper().getPayDataREDao();
//        List<PayNoteItem> pis = pired.queryForEq(PayNoteItem.FIELD_BUDGET, bi.get_id());
//
//        BigDecimal all_use = BigDecimal.ZERO;
//        if(!ToolUtil.ListIsNullOrEmpty(pis)) {
//            for(PayNoteItem i : pis)    {
//                all_use = all_use.add(i.getVal());
//            }
//        }
//
//        bi.useBudget(all_use);
//        return AppModel.getBudgetUtility().modifyData(bi);
//    }
// --Commented out by Inspection STOP (2016/9/18 12:55)

    /**
     * 根据预算名查找当前用户下的BudgetItem数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public BudgetItem getBudgetByName(String bn)  {
        if(UtilFun.StringIsNullOrEmpty(bn))
            return null;

        UsrItem cur_usr = ContextUtil.getCurUsr();
        if(null == cur_usr)
            return null;

        List<BudgetItem> ret;
        try {
            ret = getDBHelper().queryBuilder()
                    .where().eq(BudgetItem.FIELD_USR, cur_usr.getId())
                    .and().eq(BudgetItem.FIELD_NAME, bn).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = null;
        }

        if(ToolUtil.ListIsNullOrEmpty(ret))
            return null;

        return ret.get(0);
    }


    /**
     * 添加预算数据
     * @param bi  待添加数据
     * @return 添加成功返回{@code true}
     */
    @Override
    public boolean createData(BudgetItem bi)   {
        if((null == bi.getUsr()) || (-1 == bi.getUsr().getId())) {
            UsrItem cur_usr = ContextUtil.getCurUsr();
            if(null == cur_usr)
                return false;

            bi.setUsr(cur_usr);
        }

        return super.createData(bi);
    }

    /**
     * 删除预算数据
     * 删除预算关联的支出数据
     * @param ls_biid       待删除数据id
     * @return  删除数据
     */
    @Override
    public int removeDatas(List<Integer> ls_biid)  {
        if(!ToolUtil.ListIsNullOrEmpty(ls_biid)) {
            for(Integer id : ls_biid) {
                List<PayNoteItem> ls_pay = ContextUtil.getDBHelper().getPayDataREDao()
                                            .queryForEq(PayNoteItem.FIELD_BUDGET, id);
                if (!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
                    for (PayNoteItem i : ls_pay) {
                        i.setBudget(null);
                        ContextUtil.getDBHelper().getPayDataREDao().update(i);
                    }
                }
            }

            return  super.removeDatas(ls_biid);
        }

        return 0;
    }
}
