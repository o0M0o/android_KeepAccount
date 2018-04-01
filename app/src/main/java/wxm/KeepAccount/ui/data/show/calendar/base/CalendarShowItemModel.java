package wxm.KeepAccount.ui.data.show.calendar.base;


import wxm.uilib.SimpleCalendar.BaseCalendarItemModel;

/**
 * for extended
 * Created by ookoo on 2017/07/06
 */
public class CalendarShowItemModel extends BaseCalendarItemModel {
    private int mRecordCount;

    public int getRecordCount() {
        return mRecordCount;
    }

    public void setRecordCount(int mRecordCount) {
        this.mRecordCount = mRecordCount;
    }
}
