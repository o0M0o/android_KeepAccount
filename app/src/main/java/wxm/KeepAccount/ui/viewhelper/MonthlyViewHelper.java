package wxm.KeepAccount.ui.viewhelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 月数据辅助类
 * Created by 123 on 2016/9/10.
 */
public class MonthlyViewHelper implements ILVViewHelper {
    private DailyViewHelper  mDailyVWHelper;

    public MonthlyViewHelper()    {
        mDailyVWHelper = new DailyViewHelper();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        return mDailyVWHelper.createView(inflater, container);
    }

    @Override
    public View getView() {
        return mDailyVWHelper.getView();
    }

    @Override
    public void loadView() {
        mDailyVWHelper.loadView();
    }
}
