package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Switcher.FrgSwitcher;
import wxm.KeepAccount.ui.data.show.note.HelloChart.MonthlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVMonthly;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for monthly data
 * Created by WangXM on2016/9/25.
 */
public class TFShowMonthly extends FrgSwitcher<FrgUtilitySupportBase> {

    public TFShowMonthly() {
        super();
        LOG_TAG = "TFShowMonthly";

        setFrgID(R.layout.tf_show_base, R.id.fl_holder);
        addChildFrg(new LVMonthly());
        addChildFrg(new MonthlyChart());
    }
}
