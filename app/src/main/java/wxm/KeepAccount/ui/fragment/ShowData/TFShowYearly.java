package wxm.KeepAccount.ui.fragment.ShowData;

import wxm.KeepAccount.ui.fragment.HelloChart.YearlyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.YearlyLVHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * show fragment for yearly
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends TFShowBase {

    public TFShowYearly()   {
        super();
        LOG_TAG = "TFShowYearly";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new YearlyLVHelper();
        mViewHelper[1] = new YearlyChartHelper();
    }
}
