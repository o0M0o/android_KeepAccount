package wxm.KeepAccount.Base.data;

import android.util.Log;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.RecordItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * record数据辅助类
 * Created by 123 on 2016/8/9.
 */
public class RecordDataUtility {
    private final String    TAG = "RecordDataUtility";

    public RecordDataUtility()  {
    }

    /**
     * 根据ID查找RecordItem数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public RecordItem GetRecordById(int id)  {
        return AppModel.getDBHelper().getRecordItemREDao().queryForId(id);
    }

    /**
     * 获得当前用户所有的记录
     * @return 当前用户的所有记录
     */
    public List<RecordItem> GetAllRecords()     {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getRecordItemREDao()
               .queryForEq(RecordItem.FIELD_USR, cur_usr);
    }

    /**
     * 根据日期条件(例如 : '2016-05-07')获得相关记录
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<RecordItem> GetRecordsByDay(String day_str)   {
        Timestamp tsb = ToolUtil.StringToTimestamp(day_str);
        Timestamp tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();

        List<RecordItem> ret;
        try {
            ret = mDBHelper.getRecordItemREDao().queryBuilder()
                    .where().between(RecordItem.FIELD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = new LinkedList<>();
        }

        return ret;
    }

    /**
     * 添加记录到文件
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int AddRecords(List<RecordItem> lsi)    {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null != cur_usr) {
            for(RecordItem i : lsi) {
                i.setUsr(cur_usr);
            }

            return mDBHelper.getRecordItemREDao().create(lsi);
        }
        else    {
            boolean nousr = false;
            for(RecordItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr)
                return mDBHelper.getRecordItemREDao().create(lsi);
        }

        return 0;
    }

    /**
     * 修改记录到文件
     * @param lsi 待修改的记录集合
     * @return  修改成功返回true
     */
    public boolean ModifyRecords(List<RecordItem> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        for(RecordItem i : lsi) {
            mDBHelper.getRecordItemREDao().update(i);
        }
        return true;
    }

    /**
     * 删除文件中的记录
     * @param lsi 待删除的记录集合的id值
     * @return  操作成功返回true
     */
    public boolean DeleteRecords(List<Integer> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        mDBHelper.getRecordItemREDao().deleteIds(lsi);
        return true;
    }

}
