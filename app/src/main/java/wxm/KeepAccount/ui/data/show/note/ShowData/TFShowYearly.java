package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.YearlyChartHelper;
import wxm.KeepAccount.ui.data.show.note.ListView.LVHelperYearly;

/**
 * show fragment for yearly
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends TFShowBase {

    public TFShowYearly() {
        super();
        LOG_TAG = "TFShowYearly";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new LVHelperYearly();
        mViewHelper[1] = new YearlyChartHelper();
    }
}
