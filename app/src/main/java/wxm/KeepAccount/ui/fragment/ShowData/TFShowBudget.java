package wxm.KeepAccount.ui.fragment.ShowData;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.ListView.BudgetViewHelper;
import wxm.KeepAccount.ui.fragment.base.ShowViewHelperBase;

/**
 * 显示预算
 * Created by 123 on 2016/10/5.
 */

public class TFShowBudget extends TFShowBase {
    private final static String TAG = "TFShowBudget";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView, hot = " + mHotChild);
        mViewHelper = new ShowViewHelperBase[1];
        mViewHelper[0] = new BudgetViewHelper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
