package com.wxm.KeepAccount.Base.data;

import android.content.Context;
import android.util.Log;

import com.wxm.KeepAccount.Base.db.DBOrmliteHelper;
import com.wxm.KeepAccount.Base.utility.ContextUtil;
import com.wxm.KeepAccount.Base.utility.MD5Util;
import com.wxm.KeepAccount.Base.utility.ToolUtil;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 123 on 2016/5/7.
 * 本类为app的数据类
 */
public class AppModel {
    private static final String TAG = "AppModel";

    private static Context      mMockContext = null;
    private static AppModel ourInstance;

    private DBOrmliteHelper mDBHelper;

    // 当前登录用户
    public String cur_usr;

    public static AppModel getInstance() {
        if(null == ourInstance)
            ourInstance = new AppModel();

        return ourInstance;
    }


    public static void SetContext(Context ct)   {
        mMockContext = ct;
    }

    public static void Release()    {
        if(null != ourInstance) {
            ourInstance.mDBHelper.close();
            ourInstance.mDBHelper = null;
            ourInstance = null;
        }
    }

    private AppModel() {
        if(null == mMockContext)
            mDBHelper = new DBOrmliteHelper(ContextUtil.getInstance());
        else
            mDBHelper = new DBOrmliteHelper(mMockContext);
    }

    /**
     * 清理数据库
     */
    public void ClearDB()   {
        try {
            mDBHelper.getRecordItemREDao().deleteBuilder().delete();
            mDBHelper.getUsrItemREDao().deleteBuilder().delete();
        }catch (SQLException e) {
            Log.e(TAG, ToolUtil.ExceptionToString(e));
        }
    }


    /**
     * 根据ID查找RecordItem数据
     * @param id 待查找id
     * @return 查找到的数据
     */
    public RecordItem GetRecordById(int id)  {
        return mDBHelper.getRecordItemREDao().queryForId(id);
    }

    /**
     * 获得所有的记录
     * @return 所有记录
     */
    public List<RecordItem> GetAllRecords()     {
        return mDBHelper.getRecordItemREDao().queryForAll();
    }

    /**
     * 根据日期条件(例如 : '2016-05-07')获得相关记录
     * @param day_str   日期条件
     * @return  满足日期条件的记录
     */
    public List<RecordItem> GetRecordsByDay(String day_str)   {
        Timestamp tsb = ToolUtil.StringToTimestamp(day_str);
        Timestamp tse = ToolUtil.StringToTimestamp(day_str + " 23:59:59");

        List<RecordItem> ret;
        try {
            ret = mDBHelper.getRecordItemREDao().queryBuilder()
                    .where().between(RecordItem.FIELD_RECORD_TS, tsb, tse).query();
        }catch (SQLException e) {
            Log.e(TAG, ToolUtil.ExceptionToString(e));
            ret = new LinkedList<>();
        }

        return ret;
    }

    /**
     * 添加记录到文件
     * @param lsi 待添加的记录集合
     * @return 添加成功返回true
     */
    public void AddRecords(List<RecordItem> lsi)    {
        mDBHelper.getRecordItemREDao().create(lsi);
    }

    /**
     * 修改记录到文件
     * @param lsi 待修改的记录集合
     * @return  修改成功返回true
     */
    public boolean ModifyRecords(List<RecordItem> lsi)  {
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
        mDBHelper.getRecordItemREDao().deleteIds(lsi);
        return true;
    }

    /**
     * 检查是否用户'usr'已经存在
     * @param usr   待检查用户
     * @return  如果存在返回true,否则返回false
     */
    public boolean hasUsr(String usr) {
        if(ToolUtil.StringIsNullOrEmpty(usr))
            return false;

        List<UsrItem> ret = mDBHelper.getUsrItemREDao()
                                .queryForEq(UsrItem.FIELD_USR_NAME, usr);
        if(null == ret)
            return false;

        return ret.size() >= 1;

    }

    /**
     * 添加用户
     * @param usr   待添加用户名
     * @param pwd   待添加用户密码
     * @return  添加成功返回true, 否则返回false
     */
    public boolean addUsr(String usr, String pwd)   {
        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        UsrItem ui = new UsrItem();
        ui.setUsr_name(usr);
        ui.setUsr_pwd(MD5Util.string2MD5(pwdpad));

        return mDBHelper.getUsrItemREDao().createOrUpdate(ui).getNumLinesChanged() == 1;
    }

    /**
     * 检查用户
     * @param usr   待检查用户名
     * @param pwd   待检查用户密码
     * @return  如果符合返回true, 否则返回false
     */
    public boolean checkUsr(String usr, String pwd) {
        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = mDBHelper.getUsrItemREDao()
                                .queryForEq(UsrItem.FIELD_USR_NAME, usr);
        if((null == lsui) || (lsui.size() < 1))
            return false;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        if(checkPwd.equals(lsui.get(0).getUsr_pwd()))
            return true;

        return false;
    }
}
