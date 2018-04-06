package wxm.KeepAccount.ui.data.report;

import android.os.Bundle;
import wxm.KeepAccount.R;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * data month report
 * Created by WangXM on 2017/2/15.
 */
public class FrgReportMonth extends FrgSupportSwitcher<FrgUtilitySupportBase> {
    public FrgReportMonth()   {
        super();
        setupFrgID(R.layout.vw_report, R.id.fl_page_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
    }
}
