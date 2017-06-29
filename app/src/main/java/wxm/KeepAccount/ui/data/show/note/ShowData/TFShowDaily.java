package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.ui.data.show.note.HelloChart.DailyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVDaily;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;


/**
 * fragment for daily data show
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends TFShowBase {

    public TFShowDaily() {
        super();
        LOG_TAG = "TFShowDaily";

        mViewHelper = new ShowViewBase[2];
        mViewHelper[0] = new LVDaily();
        mViewHelper[1] = new DailyChart();
    }
}
