package wxm.KeepAccount.ui.data.report.base;

/**
 * event when select start & end day
 * Created by WangXM on 2017/3/7.
 */
public class EventSelectDays {
    public String mSZStartDay;
    public String mSZEndDay;

    public EventSelectDays(String s_d, String e_d) {
        mSZStartDay = s_d;
        mSZEndDay = e_d;
    }
}
