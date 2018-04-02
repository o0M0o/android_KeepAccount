package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Switcher.FrgSwitcher;
import wxm.KeepAccount.ui.data.show.note.HelloChart.BudgetChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVBudget;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for budget
 * Created by WangXM on 2016/10/5.
 */

public class TFShowBudget extends FrgSwitcher<FrgUtilitySupportBase> {

    public TFShowBudget() {
        super();
        LOG_TAG = "TFShowBudget";

        setFrgID(R.layout.tf_show_base, R.id.fl_holder);
        addChildFrg(new LVBudget());
        addChildFrg(new BudgetChart());
    }
}
