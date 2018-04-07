package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.FrgUitlity.FrgAsyncLoad;
import wxm.KeepAccount.ui.data.show.note.HelloChart.BudgetChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVBudget;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for budget
 * Created by WangXM on 2016/10/5.
 */

public class TFShowBudget extends FrgSupportSwitcher<FrgAsyncLoad> {
    public TFShowBudget() {
        super();
        setupFrgID(R.layout.tf_show_base, R.id.fl_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addChildFrg(new LVBudget());
        addChildFrg(new BudgetChart());
    }
}
