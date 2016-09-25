package wxm.KeepAccount.Base.data;

import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.BudgetItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * 预算数据工具类
 * Created by 123 on 2016/9/1.
 */
public class BudgetDataUtility {
    private final static String  TAG = "BudgetDataUtility";

    public BudgetDataUtility()  {
    }

    /**
     * 根据当前用户查找BudgetItem数据,且附带支出数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public HashMap<BudgetItem, List<PayNoteItem>> GetBudgetWithPayNote()  {
        HashMap<BudgetItem, List<PayNoteItem>>  bret = new HashMap<>();
        List<BudgetItem> ls_bi = GetBudget();
        if(null != ls_bi)   {
            for(BudgetItem bi : ls_bi)  {
                List<PayNoteItem> pi = AppModel.getPayIncomeUtility().GetPayNoteByBudget(bi);
                
                BudgetItem nbi = GetBudgetById(bi.get_id());
                bret.put(nbi, pi);
            }
        }

        return bret;
    }

    /**
     * 根据当前用户查找BudgetItem数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public List<BudgetItem> GetBudget()  {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        List<BudgetItem> lsret = AppModel.getDBHelper().getBudgetDataREDao()
                                    .queryForEq(BudgetItem.FIELD_USR, cur_usr);
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
//        return AppModel.getBudgetUtility().ModifyBudget(bi);
//    }
// --Commented out by Inspection STOP (2016/9/18 12:55)

    /**
     * 根据ID查找BudgetItem数据
     * @param biid 预算ID
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public BudgetItem GetBudgetById(int biid)  {
        return AppModel.getDBHelper().getBudgetDataREDao()
                .queryForId(biid);
    }

    /**
     * 根据预算名查找当前用户下的BudgetItem数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public BudgetItem GetBudgetByName(String bn)  {
        if(UtilFun.StringIsNullOrEmpty(bn))
            return null;

        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        List<BudgetItem> ret;
        try {
            ret = mDBHelper.getBudgetDataREDao().queryBuilder()
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
    public boolean AddBudget(BudgetItem bi)   {
        if((null == bi.getUsr()) || (-1 == bi.getUsr().getId())) {
            UsrItem cur_usr = AppModel.getInstance().getCurUsr();
            if(null == cur_usr)
                return false;

            bi.setUsr(cur_usr);
        }


        return 1 == AppModel.getDBHelper().getBudgetDataREDao()
                        .create(bi);
    }

    /**
     * 修改预算数据
     * @param bi 待修改数据
     * @return 成功返回{@code true}
     */
    public boolean ModifyBudget(BudgetItem bi)  {
        return 1 == AppModel.getDBHelper().getBudgetDataREDao().update(bi);
    }


    /**
     * 删除预算数据
     * 删除预算关联的支出数据
     * @param ls_biid 待删除数据id
     * @return 成功返回{@code true}
     */
    public boolean DeleteBudgetById(List<Integer> ls_biid)  {
        if(!ToolUtil.ListIsNullOrEmpty(ls_biid)) {
            int w_s = ls_biid.size();
            int r_s = 0;
            for(Integer id : ls_biid) {
                List<PayNoteItem> ls_pay = AppModel.getDBHelper().getPayDataREDao()
                                            .queryForEq(PayNoteItem.FIELD_BUDGET, id);
                if (!ToolUtil.ListIsNullOrEmpty(ls_pay)) {
                    for (PayNoteItem i : ls_pay) {
                        i.setBudget(null);
                        AppModel.getDBHelper().getPayDataREDao().update(i);
                    }
                }

                if(1 == AppModel.getDBHelper().getBudgetDataREDao().deleteById(id)) {
                    r_s += 1;
                }
            }

            return  w_s == r_s;
        }

        return true;
    }
}