package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACNoteShowNew;
import wxm.KeepAccount.ui.fragment.GraphView.MonthlyCharts;

/**
 * fragment for monthly show
 * Created by wxm on 2016/9/25.
 */
public class TFShowMonthly extends TFShowBase  {
    private final static String TAG = "TFShowMonthly";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mListViewHelper = new MonthlyViewHelper();
        mChartViewHelper = new MonthlyCharts(getContext());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
