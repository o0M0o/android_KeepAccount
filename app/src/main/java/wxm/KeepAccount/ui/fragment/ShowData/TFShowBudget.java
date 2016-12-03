package wxm.KeepAccount.ui.fragment.ShowData;

import wxm.KeepAccount.ui.fragment.HelloChart.BudgetChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.BudgetViewHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * 显示预算
 * Created by 123 on 2016/10/5.
 */

public class TFShowBudget extends TFShowBase {

    public TFShowBudget()   {
        super();
        LOG_TAG = "TFShowBudget";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new BudgetViewHelper();
        mViewHelper[1] = new BudgetChartHelper();
    }
}
