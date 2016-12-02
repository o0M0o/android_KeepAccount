package wxm.KeepAccount.Base.db;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * 数据工具基类
 * Created by 123 on 2016/10/31.
 */
public abstract class DataUtilityBase {
    private LinkedList<IDBChangeNotice> mLLNotices;
    Timestamp mTSLastModifyData;

    DataUtilityBase()    {
        mLLNotices = new LinkedList<>();
        mTSLastModifyData = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * 添加数据变化监听
     * @param inc  新监听器
     */
    public void addDataChangeNotice(IDBChangeNotice inc)  {
        if(!mLLNotices.contains(inc))
            mLLNotices.add(inc);
    }

    /**
     * 移除数据变化监听
     * @param inc  待移除监听器
     */
    public void removeDataChangeNotice(IDBChangeNotice inc)   {
        mLLNotices.remove(inc);
    }


    /**
     * 返回数据最后更新时间
     * @return  数据最后更新时间
     */
    public Timestamp getDataLastChangeTime()    {
        return mTSLastModifyData;
    }

    /**
     * 数据有更新时调用
     */
    void onDataModify()   {
        mTSLastModifyData.setTime(Calendar.getInstance().getTimeInMillis());
        for (IDBChangeNotice inc : mLLNotices)    {
            inc.DataModifyNotice();
        }
    }

    /**
     * 新建数据后调用
     */
    void onDataCreate()   {
        mTSLastModifyData.setTime(Calendar.getInstance().getTimeInMillis());
        for (IDBChangeNotice inc : mLLNotices)    {
            inc.DataCreateNotice();
        }
    }

    /**
     * 删除数据后调用
     */
    void onDataDelete()   {
        mTSLastModifyData.setTime(Calendar.getInstance().getTimeInMillis());
        for (IDBChangeNotice inc : mLLNotices)    {
            inc.DataDeleteNotice();
        }
    }
}
