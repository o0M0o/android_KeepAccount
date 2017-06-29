package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.MonthlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVMonthly;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase {

    public TFShowMonthly() {
        super();
        LOG_TAG = "TFShowMonthly";

        mViewHelper = new ShowViewBase[2];
        mViewHelper[0] = new LVMonthly();
        mViewHelper[1] = new MonthlyChart();
    }
}
