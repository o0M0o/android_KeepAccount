package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.BudgetChartHelper;
import wxm.KeepAccount.ui.data.show.note.ListView.LVHelperBudget;

/**
 * 显示预算
 * Created by 123 on 2016/10/5.
 */

public class TFShowBudget extends TFShowBase {

    public TFShowBudget()   {
        super();
        LOG_TAG = "TFShowBudget";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new LVHelperBudget();
        mViewHelper[1] = new BudgetChartHelper();
    }
}
