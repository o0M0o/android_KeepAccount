package wxm.KeepAccount.ui.fragment.ShowData;

import wxm.KeepAccount.ui.fragment.HelloChart.DailyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.DailyLVHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;


/**
 * fragment for daily data show
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends TFShowBase {
    private final static String TAG = "TFShowDaily";

    public TFShowDaily()   {
        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new DailyLVHelper();
        mViewHelper[1] = new DailyChartHelper();
    }
}
