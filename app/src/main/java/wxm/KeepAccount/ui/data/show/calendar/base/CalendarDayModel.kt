package wxm.KeepAccount.ui.data.show.calendar.base


import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.uilib.FrgCalendar.CalendarItem.BaseItemModel

/**
 * for extended
 * Created by WangXM on 2017/07/06
 */
class CalendarDayModel : BaseItemModel() {
    var recordCount: Int = -1
        private set
        get() {
            if(-1 == field) {
                field = 0
                NoteDataHelper.getInfoByDay(szDate).let {
                    field = it.incomeCount + it.payCount
                }
            }

            return field
        }
}
