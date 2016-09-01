package com.wxm.KeepAccount.Base.data;

import android.content.Context;
import android.util.Log;

import com.wxm.KeepAccount.Base.db.DBOrmliteHelper;
import com.wxm.KeepAccount.Base.db.UsrItem;
import com.wxm.KeepAccount.Base.utility.ContextUtil;

import java.sql.SQLException;

import cn.wxm.andriodutillib.util.UtilFun;

/**
 * Created by 123 on 2016/5/7.
 * 本类为app的数据类
 */
public class AppModel {
    private static final String TAG = "AppModel";
    private static AppModel ourInstance;

    private UsrItem         cur_usr;

    private static Context          mMockContext = null;
    private DBOrmliteHelper         mDBHelper;
    private UsrItemUtility          mUsru;
    private RecordItemUtility       mRecordu;
    private RecordTypeItemUtility   mRecordTypeu;
    private BudgetItemUtility       mBudgetu;

    public static void SetContext(Context ct)   {
        mMockContext = ct;
    }

    private AppModel() {
        SetUp();
    }

    public static AppModel getInstance() {
        if(null == ourInstance)
            ourInstance = new AppModel();

        return ourInstance;
    }


    public void SetUp()     {
        if(null == mDBHelper)   {
            if(null == mMockContext)
                mDBHelper = new DBOrmliteHelper(ContextUtil.getInstance());
            else
                mDBHelper = new DBOrmliteHelper(mMockContext);
        }

        if(null == mUsru)
            mUsru = new UsrItemUtility();

        if(null == mRecordu)
            mRecordu = new RecordItemUtility();

        if(null == mRecordTypeu)
            mRecordTypeu = new RecordTypeItemUtility();

        if(null == mBudgetu)
            mBudgetu = new BudgetItemUtility();
    }


    /**
     * 释放资源
     */
    public void Release()    {
        if(null != mDBHelper) {
            mDBHelper.close();
            mDBHelper = null;
        }

        mUsru           = null;
        mRecordu        = null;
        mRecordTypeu    = null;
        mBudgetu        = null;
    }

    /**
     * 清理数据库
     * 删除数据库中的所有数据
     */
    public void ClearDB()   {
        try {
            mDBHelper.getRecordItemREDao().deleteBuilder().delete();
            mDBHelper.getUsrItemREDao().deleteBuilder().delete();
        }catch (SQLException e) {
            Log.e(TAG, UtilFun.ExceptionToString(e));
        }
    }


    /**
     * 获取当前登录用户
     * @return 当前登录用户
     */
    public UsrItem getCurUsr() {
        return cur_usr;
    }

    /**
     * 设置当前登录用户
     * @param cur_usr 当前登录用户
     */
    public void setCurUsr(UsrItem cur_usr) {
        this.cur_usr = cur_usr;
    }

    /**
     * 获得数据库辅助类
     * @return 数据库辅助类
     */
    public static DBOrmliteHelper getDBHelper()    {
        return getInstance().mDBHelper;
    }

    /**
     * 获得用户数据辅助类
     * @return 用户数据辅助类
     */
    public static UsrItemUtility getUsrUtility()   {
        return getInstance().mUsru;
    }

    /**
     * 获得record数据辅助类
     * @return record数据辅助类
     */
    public static RecordItemUtility getRecordUtility()  {
        return getInstance().mRecordu;
    }

    /**
     * 获得recordtype数据辅助类
     * @return 辅助类
     */
    public static RecordTypeItemUtility getRecordTypeUtility()  {
        return getInstance().mRecordTypeu;
    }

    /**
     * 获得预算辅助类
     * @return  辅助类
     */
    public static BudgetItemUtility getBudgetUtility()  {
        return  getInstance().mBudgetu;
    }
}
