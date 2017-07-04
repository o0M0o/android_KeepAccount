package wxm.KeepAccount.ui.data.show.calendar;

import wxm.androidutil.ExActivity.BaseRxAppCompatActivity;

/**
 * for calendar
 * Created by ookoo on 2016/12/4.
 */
public class ACCalendarShow extends BaseRxAppCompatActivity {
    private final FrgCalendarShow mFGTCalendar = new FrgCalendarShow();

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
