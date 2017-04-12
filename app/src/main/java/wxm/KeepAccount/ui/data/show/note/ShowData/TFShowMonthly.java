package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.MonthlyChartHelper;
import wxm.KeepAccount.ui.data.show.note.ListView.LVHelperMonthly;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase {

    public TFShowMonthly()   {
        super();
        LOG_TAG = "TFShowMonthly";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new LVHelperMonthly();
        mViewHelper[1] = new MonthlyChartHelper();
    }
}
