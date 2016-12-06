package wxm.KeepAccount.ui.acutility;

import wxm.KeepAccount.Base.define.BaseRxAppCompatActivity;
import wxm.KeepAccount.ui.fragment.utility.FrgCalendarData;

/**
 * for calendar
 * Created by ookoo on 2016/12/4.
 */
public class ACCalendarShow extends BaseRxAppCompatActivity     {
    private FrgCalendarData mFGTCalendar = new FrgCalendarData();

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACCalendarShow";
        mFGHolder = mFGTCalendar;
    }
}
