package wxm.KeepAccount.ui.data.show.note.ShowData;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.Switcher.FrgSwitcher;
import wxm.KeepAccount.ui.data.show.note.HelloChart.YearlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVYearly;
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase;

/**
 * for yearly data
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends FrgSwitcher {

    public TFShowYearly() {
        super();
        LOG_TAG = "TFShowYearly";

        setFrgID(R.layout.tf_show_base, R.id.fl_holder);
        setChildFrg(new LVYearly(), new YearlyChart());
    }
}
