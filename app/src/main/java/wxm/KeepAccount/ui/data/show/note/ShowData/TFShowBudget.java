package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.BudgetChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVBudget;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * 显示预算
 * Created by 123 on 2016/10/5.
 */

public class TFShowBudget extends TFShowBase {

    public TFShowBudget() {
        super();
        LOG_TAG = "TFShowBudget";

        mViewHelper = new ShowViewBase[2];
        mViewHelper[0] = new LVBudget();
        mViewHelper[1] = new BudgetChart();
    }
}
