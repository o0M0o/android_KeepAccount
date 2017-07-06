package wxm.KeepAccount.ui.data.show.calendar;


import wxm.uilib.SimpleCalendar.BaseCalendarItemModel;

/**
 * for extended
 * Created by kelin on 16-7-20.
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
