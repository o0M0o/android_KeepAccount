package wxm.KeepAccount.ui.data.show.calendar;

import android.os.Bundle;

import wxm.androidutil.Switcher.ACSwitcherActivity;


/**
 * for calendar
 * Created by WangXM on 2016/12/4.
 */
public class ACCalendarShow extends ACSwitcherActivity<FrgCalendarShow> {
    @Override
    protected void setupFragment(Bundle bundle) {
        addFragment(new FrgCalendarShow());
    }
}
