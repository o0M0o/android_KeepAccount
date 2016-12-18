package wxm.KeepAccount.ui.acutility;

import cn.wxm.andriodutillib.ExActivity.BaseRxAppCompatActivity;
import wxm.KeepAccount.ui.fragment.utility.FrgCalendarShow;

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
