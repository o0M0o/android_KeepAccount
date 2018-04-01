package wxm.KeepAccount.ui.data.show.calendar;

import android.os.Bundle;

import wxm.KeepAccount.ui.base.SwitcherActivity.ACSwitcherActivity;

/**
 * for calendar
 * Created by ookoo on 2016/12/4.
 */
public class ACCalendarShow extends ACSwitcherActivity<FrgCalendarShow> {
    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);
        LOG_TAG = "ACCalendarShow";
        addFragment(new FrgCalendarShow());
    }
}
