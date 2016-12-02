package wxm.KeepAccount.Base.utility;

import android.app.Application;
import android.util.Log;

import wxm.KeepAccount.Base.data.DBOrmLiteHelper;
import wxm.KeepAccount.Base.data.UsrItem;
import wxm.KeepAccount.Base.db.BudgetDataUtility;
import wxm.KeepAccount.Base.db.PayIncomeDBUtility;
import wxm.KeepAccount.Base.db.RecordTypeDataUtility;
import wxm.KeepAccount.Base.db.RemindDataUtility;
import wxm.KeepAccount.Base.db.UsrDataUtility;
import wxm.KeepAccount.Base.handler.GlobalMsgHandler;

/**
 * 获取全局context
 * 获取全局辅助类
 * Created by 123 on 2016/5/7.
 */
public class ContextUtil extends Application {
    private static final String TAG = "ContextUtil";
    private static ContextUtil instance;

    // data for global use
    private UsrItem             mUICurUsr;
    private GlobalMsgHandler    mMHHandler;

    // mainly for sqlite
    private DBOrmLiteHelper mDBHelper;
    private UsrDataUtility          mUsru;
    private RecordTypeDataUtility   mRecordTypeu;
    private BudgetDataUtility       mBudgetu;
    private PayIncomeDBUtility      mPayIncomeu;
    private RemindDataUtility       mRemindu;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance    = this;
        mMHHandler  = new GlobalMsgHandler();

        // for db
        mDBHelper       = new DBOrmLiteHelper(getInstance());

        mUsru           = new UsrDataUtility();
        mRecordTypeu    = new RecordTypeDataUtility();
        mBudgetu        = new BudgetDataUtility();
        mPayIncomeu     = new PayIncomeDBUtility();
        mRemindu        = new RemindDataUtility();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // for db
        if(null != mDBHelper) {
            mDBHelper.close();
            mDBHelper = null;
        }

        mUsru           = null;
        mRecordTypeu    = null;
        mBudgetu        = null;
        mPayIncomeu     = null;
        mRemindu        = null;
    }

    /**
     * 获取全局context
     * @return  全局context
     */
    public static ContextUtil getInstance() {
        return instance;
    }

    /**
     * 获取全局消息处理器
     * @return  全局消息处理器
     */
    public static GlobalMsgHandler getMsgHandler()  {
        return getInstance().mMHHandler;
    }

    /**
     * 获得数据库辅助类
     * @return 数据库辅助类
     */
    public static DBOrmLiteHelper getDBHelper()    {
        return getInstance().mDBHelper;
    }


    /**
     * 获取当前登录用户
     * @return 当前登录用户
     */
    public static UsrItem getCurUsr() {
        return getInstance().mUICurUsr;
    }

    /**
     * 设置当前登录用户
     * @param cur_usr 当前登录用户
     */
    public static void setCurUsr(UsrItem cur_usr) {
        getInstance().mUICurUsr = cur_usr;
    }


    /**
     * 获得用户数据辅助类
     * @return 用户数据辅助类
     */
    public static UsrDataUtility getUsrUtility()   {
        return getInstance().mUsru;
    }

    /**
     * 获得recordtype数据辅助类
     * @return 辅助类
     */
    public static RecordTypeDataUtility getRecordTypeUtility()  {
        return getInstance().mRecordTypeu;
    }

    /**
     * 获得预算辅助类
     * @return  辅助类
     */
    public static BudgetDataUtility getBudgetUtility()  {
        return  getInstance().mBudgetu;
    }

    /**
     * 获得收支数据辅助类
     * @return  辅助类
     */
    public static PayIncomeDBUtility getPayIncomeUtility()  {
        return  getInstance().mPayIncomeu;
    }


    /**
     * 获得提醒数据辅助类
     * @return  辅助类
     */
    public static RemindDataUtility getRemindUtility()  {
        return  getInstance().mRemindu;
    }


    /**
     * 清理数据库
     * 删除数据库中的所有数据
     */
    public static void ClearDB()   {
        try {
            DBOrmLiteHelper dh = getInstance().mDBHelper;

            //mDBHelper.getUsrItemREDao().deleteBuilder().delete();
            dh.getPayDataREDao().deleteBuilder().delete();
            dh.getIncomeDataREDao().deleteBuilder().delete();
            //mDBHelper.getRTItemREDao().deleteBuilder().delete();
            dh.getRemindREDao().deleteBuilder().delete();
        } catch (java.sql.SQLException e) {
            Log.e(TAG, "ClearDB catch an exception", e);
        }
    }
}
