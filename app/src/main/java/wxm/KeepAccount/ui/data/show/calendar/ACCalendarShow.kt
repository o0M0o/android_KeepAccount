package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle

import wxm.androidutil.Switcher.ACSwitcherActivity


/**
 * for calendar
 * Created by WangXM on 2016/12/4.
 */
class ACCalendarShow : ACSwitcherActivity<FrgCalendarHolder>() {
    override fun setupFragment(bundle: Bundle?) {
        addFragment(FrgCalendarHolder())
    }
}
