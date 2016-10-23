package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.HelloChart.YearlyChartHelper;
import wxm.KeepAccount.ui.fragment.ListView.YearlyLVHelper;
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
        //mViewHelper[0] = new YearlyLVHelper();
        mViewHelper[0] = new YearlyLVHelper();
        mViewHelper[1] = new YearlyChartHelper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
