package wxm.KeepAccount.ui.data.show.note.ShowData;

import java.util.List;

/**
 * 过滤视图事件类
 * Created by User on 2017/2/14.
 */
public class FilterShowEvent {
    /**
     * 事件发送者
     */
    private String mSZSender;

    /**
     * 过滤视图参数
     */
    private List<String> mLSFilterTag;

    public FilterShowEvent(String sender, List<String> ft) {
        mSZSender = sender;
        mLSFilterTag = ft;
    }

    /**
     * 得到过滤参数
     *
     * @return 过滤参数
     */
    public List<String> getFilterTag() {
        return mLSFilterTag;
    }

    /**
     * 获得事件发送者
     *
     * @return 事件发送者
     */
    public String getSender() {
        return mSZSender;
    }
}
