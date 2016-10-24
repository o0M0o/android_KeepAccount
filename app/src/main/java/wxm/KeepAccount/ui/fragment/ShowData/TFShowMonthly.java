package wxm.KeepAccount.ui.fragment.ShowData;

import wxm.KeepAccount.ui.fragment.HelloChart.MonthlyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.MonthlyLVHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase {
    private final static String TAG = "TFShowMonthly";

    public TFShowMonthly()   {
        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new MonthlyLVHelper();
        mViewHelper[1] = new MonthlyChartHelper();
    }
}
