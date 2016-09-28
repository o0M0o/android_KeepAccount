package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.GraphView.DailyCharts;
import wxm.KeepAccount.ui.fragment.ListView.DailyViewHelper;


/**
 * fragment for daily data show
 * Created by wxm on 2016/9/25.
 */

public class TFShowDaily extends TFShowBase {
    private final static String TAG = "TFShowDaily";

    public TFShowDaily()    {
        super();
        Log.i(TAG, "new show daily");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView, hot = " + mHotChild);
        mListViewHelper = new DailyViewHelper();
        mChartViewHelper = new DailyCharts(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated, hot = " + mHotChild);
        super.onViewCreated(view, savedInstanceState);
    }

}
