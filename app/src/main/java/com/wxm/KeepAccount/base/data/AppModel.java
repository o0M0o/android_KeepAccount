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
    private UsrItem cur_usr;

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
     * 获得当前用户所有的记录
     * @return 当前用户的所有记录
     */
    public List<RecordItem> GetAllRecords()     {
        if(null == cur_usr)
            return null;

        return mDBHelper.getRecordItemREDao()
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

        List<RecordItem> ret;
        try {
            ret = mDBHelper.getRecordItemREDao().queryBuilder()
                    .where().between(RecordItem.FIELD_TS, tsb, tse).query();
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
        if(null != cur_usr) {
            for(RecordItem i : lsi) {
                i.setUsr(cur_usr);
            }

            mDBHelper.getRecordItemREDao().create(lsi);
        }
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
                                .queryForEq(UsrItem.FIELD_NAME, usr);
        return !((null == ret) || (ret.size() < 1));
    }

    /**
     * 添加用户
     * @param usr   待添加用户名
     * @param pwd   待添加用户密码
     * @return  添加成功返回添加后的数据, 否则返回null
     */
    public UsrItem addUsr(String usr, String pwd)   {
        if(ToolUtil.StringIsNullOrEmpty(usr) || ToolUtil.StringIsNullOrEmpty(pwd))
            return null;

        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }
        pwdpad = MD5Util.string2MD5(pwdpad);

        UsrItem uiret = null;
        List<UsrItem> ret = mDBHelper.getUsrItemREDao()
                    .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == ret) || (ret.size() < 1))   {
            UsrItem ui = new UsrItem();
            ui.setName(usr);
            ui.setPwd(pwdpad);

            if(1 == mDBHelper.getUsrItemREDao().create(ui))
                uiret = ui;
        } else  {
            UsrItem uiold = ret.get(0);
            if(!pwdpad.equals(uiold.getPwd()))  {
                uiold.setPwd(pwdpad);
                if(1 == mDBHelper.getUsrItemREDao().update(uiold))
                    uiret = uiold;
            } else {
                uiret = uiold;
            }
        }

        return uiret;
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
                                .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == lsui) || (lsui.size() < 1))
            return false;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        if(checkPwd.equals(lsui.get(0).getPwd()))
            return true;

        return false;
    }


    /**
     * 检查登录信息，如果有符合的记录就返回对应用户信息
     * @param usr   待检查用户名
     * @param pwd   待检查用户密码
     * @return  如果符合返回注册用户数据, 否则返回null
     */
    public UsrItem CheckAndGetUsr(String usr, String pwd)   {
        String pwdpad = pwd;
        if(pwdpad.length() < AppGobalDef.STR_PWD_PAD.length()) {
            pwdpad += AppGobalDef.STR_PWD_PAD.substring(pwd.length());
        }

        List<UsrItem> lsui = mDBHelper.getUsrItemREDao()
                                .queryForEq(UsrItem.FIELD_NAME, usr);
        if((null == lsui) || (lsui.size() < 1))
            return null;

        String checkPwd = MD5Util.string2MD5(pwdpad);
        if(checkPwd.equals(lsui.get(0).getPwd()))
            return lsui.get(0);

        return null;
    }

    public UsrItem getCurUsr() {
        return cur_usr;
    }

    public void setCurUsr(UsrItem cur_usr) {
        this.cur_usr = cur_usr;
    }
}
