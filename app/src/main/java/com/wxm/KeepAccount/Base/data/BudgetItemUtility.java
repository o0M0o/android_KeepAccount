package com.wxm.KeepAccount.Base.data;

import com.wxm.KeepAccount.Base.db.BudgetItem;
import com.wxm.KeepAccount.Base.db.UsrItem;

import java.util.List;

/**
 * 预算数据工具类
 * Created by 123 on 2016/9/1.
 */
public class BudgetItemUtility {
    private final String    TAG = "RecordItemUtility";

    public BudgetItemUtility()  {
    }

    /**
     * 根据ID查找RecordItem数据
     * @return 查找到的数据,没有数据时返回{@code NULL}
     */
    public BudgetItem GetBudget()  {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        List<BudgetItem> lsret = AppModel.getDBHelper().getBudgetItemREDao()
                                    .queryForEq(BudgetItem.FIELD_USR, cur_usr);
        if((null == lsret) || (0 == lsret.size()))
            return null;

        return lsret.get(0);
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


        return 1 == AppModel.getDBHelper().getBudgetItemREDao()
                        .create(bi);
    }

    /**
     * 修改预算数据
     * @param bi 待修改数据
     * @return 成功返回{@code true}
     */
    public boolean ModifyBudget(BudgetItem bi)  {
        return 1 == AppModel.getDBHelper().getBudgetItemREDao().update(bi);
    }

    /**
     * 删除预算数据
     * @param bi 待修改数据
     * @return 成功返回{@code true}
     */
    public boolean DeleteBudget(BudgetItem bi)  {
        return 1 == AppModel.getDBHelper().getBudgetItemREDao().delete(bi);
    }
}
