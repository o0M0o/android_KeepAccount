package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.GraphView.YeaylyCharts;
import wxm.KeepAccount.ui.fragment.HelloChart.MonthlyChartHelper;
import wxm.KeepAccount.ui.fragment.HelloChart.YearlyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.MonthlyViewHelper;
import wxm.KeepAccount.ui.fragment.ListView.YearlyViewHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * show fragment for yearly
 * Created by wxm on 2016/9/25.
 */
public class TFShowYearly extends TFShowBase {
    private final static String TAG = "TFShowYearly";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[CHILD_LISTVIWE] = new YearlyViewHelper();
        mViewHelper[CHILD_GRAPHVIWE] = new YearlyChartHelper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
