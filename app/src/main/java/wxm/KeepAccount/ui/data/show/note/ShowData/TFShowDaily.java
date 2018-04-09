package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.FrgUitlity.FrgAdvBase;
import wxm.KeepAccount.ui.data.show.note.HelloChart.DailyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVDaily;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;


/**
 * for daily data
 * Created by WangXM on2016/9/25.
 */
public class TFShowDaily extends FrgSupportSwitcher<FrgAdvBase> {
    public TFShowDaily() {
        super();
        setupFrgID(R.layout.tf_show_base, R.id.fl_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addChildFrg(new LVDaily());
        addChildFrg(new DailyChart());
    }
}
