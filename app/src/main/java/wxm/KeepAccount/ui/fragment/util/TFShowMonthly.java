package wxm.KeepAccount.ui.fragment.util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.GraphView.MonthlyCharts;
import wxm.KeepAccount.ui.fragment.ListView.MonthlyViewHelper;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase {
    private final static String TAG = "TFShowMonthly";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mListViewHelper = new MonthlyViewHelper();
        mChartViewHelper = new MonthlyCharts(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
