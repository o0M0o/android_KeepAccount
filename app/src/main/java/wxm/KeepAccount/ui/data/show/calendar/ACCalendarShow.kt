package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle
import wxm.KeepAccount.ui.base.ACBase.ACBase

import wxm.androidutil.ui.activity.ACSwitcherActivity


/**
 * for calendar
 * Created by WangXM on 2016/12/4.
 */
class ACCalendarShow : ACBase<FrgCalendarHolder>() {
    override fun setupFragment(savedInstanceState: Bundle?) {
        addFragment(FrgCalendarHolder())
    }
}
