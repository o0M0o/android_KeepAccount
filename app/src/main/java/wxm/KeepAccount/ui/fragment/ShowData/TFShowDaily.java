package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.GraphView.DailyCharts;
import wxm.KeepAccount.ui.fragment.HelloChart.DailyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.DailyViewHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;


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
        mViewHelper = new ShowViewHelperBase[2];
        mViewHelper[CHILD_LISTVIWE] = new DailyViewHelper();
        mViewHelper[CHILD_GRAPHVIWE] = new DailyChartHelper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
