package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.DailyChartHelper;
import wxm.KeepAccount.ui.data.show.note.ListView.LVHelperDaily;


/**
 * fragment for daily data show
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends TFShowBase {

    public TFShowDaily() {
        super();
        LOG_TAG = "TFShowDaily";

        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[0] = new LVHelperDaily();
        mViewHelper[1] = new DailyChartHelper();
    }
}
