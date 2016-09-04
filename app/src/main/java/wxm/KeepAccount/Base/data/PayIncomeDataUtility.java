package wxm.KeepAccount.Base.data;

import android.util.Log;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.db.RecordItem;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.DBOrmliteHelper;
import wxm.KeepAccount.Base.utility.ToolUtil;

/**
 * 收支数据辅助类
 * Created by 123 on 2016/9/4.
 */
public class PayIncomeDataUtility {
    private final String    TAG = "PayIncomeDataUtility";

    public PayIncomeDataUtility()  {
    }

    /**
     * 根据ID查找支出数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public PayNoteItem GetPayNoteById(int id)  {
        return AppModel.getDBHelper().getPayDataREDao().queryForId(id);
    }

    /**
     * 根据ID查找收入数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public IncomeNoteItem GetIncomeNoteById(int id)  {
        return AppModel.getDBHelper().getIncomeDataREDao().queryForId(id);
    }

    /**
     * 获得当前用户所有支出数据
     * @return 当前用户的所有记录
     */
    public List<PayNoteItem> GetAllPayNotes()     {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getPayDataREDao()
                    .queryForEq(PayNoteItem.FIELD_USR, cur_usr.getId());
    }


    /**
     * 获得当前用户所有收入数据
     * @return 当前用户的所有记录
     */
    public List<IncomeNoteItem> GetAllIncomeNotes()     {
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null == cur_usr)
            return null;

        return AppModel.getDBHelper().getIncomeDataREDao()
                .queryForEq(IncomeNoteItem.FIELD_USR, cur_usr.getId());
    }



    /**
     * 根据日期条件(例如 : '2016-05-07')获得支出数据
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<PayNoteItem> GetPayNotesByDay(String day_str)   {
        Timestamp tsb = ToolUtil.StringToTimestamp(day_str);
        Timestamp tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();

        List<PayNoteItem> ret;
        try {
            ret = mDBHelper.getPayDataREDao().queryBuilder()
                    .where().between(RecordItem.FIELD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = null;
        }

        return ret;
    }


    /**
     * 根据日期条件(例如 : '2016-05-07')获得收入数据
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<IncomeNoteItem> GetIcomeNotesByDay(String day_str)   {
        Timestamp tsb = ToolUtil.StringToTimestamp(day_str);
        Timestamp tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();

        List<IncomeNoteItem> ret;
        try {
            ret = mDBHelper.getIncomeDataREDao().queryBuilder()
                    .where().between(RecordItem.FIELD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
            ret = null;
        }

        return ret;
    }


    /**
     * 添加支出记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int AddPayNotes(List<PayNoteItem> lsi)    {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null != cur_usr) {
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            return mDBHelper.getPayDataREDao().create(lsi);
        }
        else    {
            boolean nousr = false;
            for(PayNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr)
                return mDBHelper.getPayDataREDao().create(lsi);
        }

        return 0;
    }


    /**
     * 添加收入记录
     * @param lsi 待添加的记录集合
     * @return  返回添加成功的数据量
     */
    public int AddIncomeNotes(List<IncomeNoteItem> lsi)    {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        UsrItem cur_usr = AppModel.getInstance().getCurUsr();
        if(null != cur_usr) {
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())
                    i.setUsr(cur_usr);
            }

            return mDBHelper.getIncomeDataREDao().create(lsi);
        }
        else    {
            boolean nousr = false;
            for(IncomeNoteItem i : lsi) {
                if(null == i.getUsr())  {
                    nousr = true;
                    break;
                }
            }

            if(!nousr)
                return mDBHelper.getIncomeDataREDao().create(lsi);
        }

        return 0;
    }


    /**
     * 修改支出记录
     * @param lsi 待修改数据
     * @return  修改成功返回true
     */
    public boolean ModifyPayNotes(List<PayNoteItem> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        for(PayNoteItem i : lsi) {
            mDBHelper.getPayDataREDao().update(i);
        }
        return true;
    }


    /**
     * 修改收入记录
     * @param lsi 待修改数据
     * @return  修改成功返回true
     */
    public boolean ModifyIncomeNotes(List<IncomeNoteItem> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        for(IncomeNoteItem i : lsi) {
            mDBHelper.getIncomeDataREDao().update(i);
        }
        return true;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  操作成功返回true
     */
    public boolean DeletePayNotes(List<Integer> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        mDBHelper.getPayDataREDao().deleteIds(lsi);
        return true;
    }


    /**
     * 删除支出记录
     * @param lsi 待删除的记录集合的id值
     * @return  操作成功返回true
     */
    public boolean DeleteIncomeNotes(List<Integer> lsi)  {
        DBOrmliteHelper mDBHelper = AppModel.getDBHelper();
        mDBHelper.getIncomeDataREDao().deleteIds(lsi);
        return true;
    }
}
