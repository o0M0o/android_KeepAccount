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
 * data month report
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportMonth extends FrgUtilitySupportBase {
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgReportMonth";
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
