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
import wxm.KeepAccount.ui.fragment.GraphView.YeaylyCharts;

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
        mChartViewHelper = new YeaylyCharts(getContext());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
