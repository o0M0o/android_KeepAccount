package wxm.KeepAccount.ui.data.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.KeepAccount.R;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;

/**
 * date year report
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportYear extends FrgUtilitySupportBase {
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgReportYear";
        View rootView = layoutInflater.inflate(R.layout.vw_report, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
    }
}
