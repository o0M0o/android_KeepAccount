package wxm.KeepAccount.ui.test;

import wxm.KeepAccount.Base.define.BaseRxAppCompatActivity;

/**
 * for calendar
 * Created by ookoo on 2016/12/4.
 */
public class ACTestCalendar extends BaseRxAppCompatActivity     {
    private FrgTestCalendar mFGTCalendar = new FrgTestCalendar();

    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACTestCalendar";
        mFGHolder = mFGTCalendar;
    }
}
