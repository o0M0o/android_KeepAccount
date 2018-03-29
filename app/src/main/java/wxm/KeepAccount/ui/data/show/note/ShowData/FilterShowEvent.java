package wxm.KeepAccount.ui.data.show.note.ShowData;

import java.util.List;

/**
 * filter show data
 * Created by User on 2017/2/14.
 */
public class FilterShowEvent {
    /**
     * event sender
     */
    private String mSZSender;

    /**
     * event parameter
     */
    private List<String> mLSFilterTag;

    public FilterShowEvent(String sender, List<String> ft) {
        mSZSender = sender;
        mLSFilterTag = ft;
    }

    /**
     * get filter parameter
     * @return      parameter for filter
     */
    public List<String> getFilterTag() {
        return mLSFilterTag;
    }

    /**
     * get event sender
     * @return      sender
     */
    public String getSender() {
        return mSZSender;
    }
}
