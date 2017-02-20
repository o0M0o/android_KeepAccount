package wxm.KeepAccount.ui.data.show.calendar;

import cn.wxm.andriodutillib.ExActivity.BaseRxAppCompatActivity;

/**
 * for calendar
 * Created by ookoo on 2016/12/4.
 */
public class ACCalendarShow extends BaseRxAppCompatActivity {
    private FrgCalendarShow mFGTCalendar = new FrgCalendarShow();

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
