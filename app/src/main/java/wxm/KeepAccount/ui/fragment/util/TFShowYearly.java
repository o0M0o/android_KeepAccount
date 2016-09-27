package wxm.KeepAccount.ui.fragment.util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.GraphView.YeaylyCharts;
import wxm.KeepAccount.ui.fragment.ListView.YearlyViewHelper;

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
        mListViewHelper = new YearlyViewHelper();
        mChartViewHelper = new YeaylyCharts(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
