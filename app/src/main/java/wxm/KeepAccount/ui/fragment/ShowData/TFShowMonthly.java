package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.HelloChart.MonthlyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.MonthlyLVNewHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

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
        mViewHelper = new ShowViewHelperBase[2];
        //mViewHelper[0] = new MonthlyLVHelper();
        mViewHelper[0] = new MonthlyLVNewHelper();
        mViewHelper[1] = new MonthlyChartHelper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
