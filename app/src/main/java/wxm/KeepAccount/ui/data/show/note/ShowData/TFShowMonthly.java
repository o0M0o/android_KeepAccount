package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.MonthlyChartHelper;
import wxm.KeepAccount.ui.data.show.note.ListView.MonthlyLVHelper;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase {

    public TFShowMonthly()   {
        super();
        LOG_TAG = "TFShowMonthly";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new MonthlyLVHelper();
        mViewHelper[1] = new MonthlyChartHelper();
    }
}
