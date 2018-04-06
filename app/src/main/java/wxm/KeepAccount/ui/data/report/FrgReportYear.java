package wxm.KeepAccount.ui.data.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import wxm.KeepAccount.R;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * date year report
 * Created by WangXM on 2017/2/15.
 */
public class FrgReportYear extends FrgSupportSwitcher<FrgUtilitySupportBase> {
    public FrgReportYear()   {
        super();
        setupFrgID(R.layout.vw_report, R.id.fl_page_holder);
    }

    @Override
    protected void setupFragment(Bundle bundle) {
    }
}
