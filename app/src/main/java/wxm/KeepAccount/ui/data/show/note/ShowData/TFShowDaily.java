package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Switcher.FrgSwitcher;
import wxm.KeepAccount.ui.data.show.note.HelloChart.DailyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVDaily;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;


/**
 * for daily data
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends FrgSwitcher {

    public TFShowDaily() {
        super();
        LOG_TAG = "TFShowDaily";

        setFrgID(R.layout.tf_show_base, R.id.fl_holder);
        setChildFrg(new LVDaily(), new DailyChart());
    }
}
