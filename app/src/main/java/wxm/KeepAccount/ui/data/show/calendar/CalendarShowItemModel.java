package wxm.KeepAccount.ui.data.show.calendar;


import wxm.uilib.SimpleCalendar.BaseCalendarItemModel;

/**
 * for extended
 * Created by ookoo on 2017/07/06
 */
class CalendarShowItemModel extends BaseCalendarItemModel {
    private int mRecordCount;

    int getRecordCount() {
        return mRecordCount;
    }

    void setRecordCount(int mRecordCount) {
        this.mRecordCount = mRecordCount;
    }
}
