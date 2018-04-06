package wxm.KeepAccount.ui.data.show.note.ShowData;

import android.os.Bundle;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.show.note.HelloChart.YearlyChart;
import wxm.KeepAccount.ui.data.show.note.ListView.LVYearly;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * for yearly data
 * Created by WangXM on2016/9/25.
 */
public class TFShowYearly extends FrgSupportSwitcher<FrgUtilitySupportBase> {
    public TFShowYearly() {
        super();
        setupFrgID(R.layout.tf_show_base, R.id.fl_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        addChildFrg(new LVYearly());
        addChildFrg(new YearlyChart());
    }
}
