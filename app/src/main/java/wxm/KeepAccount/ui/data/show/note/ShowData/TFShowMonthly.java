package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.HelloChart.MonthlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVMonthly;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;

/**
 * for monthly data
 * Created by WangXM on2016/9/25.
 */
public class TFShowMonthly extends FrgSupportSwitcher<FrgSupportBaseAdv> {

    public TFShowMonthly() {
        super();
        setupFrgID(R.layout.tf_show_base, R.id.fl_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addChildFrg(new LVMonthly());
        addChildFrg(new MonthlyChart());
    }
}
