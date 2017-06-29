package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.YearlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVYearly;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * show fragment for yearly
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends TFShowBase {

    public TFShowYearly() {
        super();
        LOG_TAG = "TFShowYearly";

        mViewHelper = new ShowViewBase[2];
        mViewHelper[0] = new LVYearly();
        mViewHelper[1] = new YearlyChart();
    }
}
