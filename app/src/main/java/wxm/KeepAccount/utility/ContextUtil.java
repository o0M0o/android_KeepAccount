package wxm.KeepAccount.utility;

import android.app.Application;
import android.util.Log;

import com.j256.ormlite.stmt.DeleteBuilder;

import wxm.KeepAccount.db.BudgetDBUtility;
import wxm.KeepAccount.db.DBOrmLiteHelper;
import wxm.KeepAccount.db.PayIncomeDBUtility;
import wxm.KeepAccount.db.RecordTypeDBUtility;
import wxm.KeepAccount.db.RemindDBUtility;
import wxm.KeepAccount.db.UsrDBUtility;
import wxm.KeepAccount.define.BudgetItem;
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.define.RemindItem;
import wxm.KeepAccount.define.UsrItem;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

/**
 * get global context & helper
 * Created by 123 on 2016/5/7.
 */
public class ContextUtil extends Application {
    private static final String TAG = "ContextUtil";
    private static ContextUtil instance;

    // data for global use
    private UsrItem mUICurUsr;
    private GlobalMsgHandler mMHHandler;

    // mainly for sqlite
    private DBOrmLiteHelper mDBHelper;
    private UsrDBUtility mUsru;
    private RecordTypeDBUtility mRecordTypeu;
    private BudgetDBUtility mBudgetu;
    private PayIncomeDBUtility mPayIncomeu;
    private RemindDBUtility mRemindu;

    /**
     * get global context
     * @return      application context
     */
    public static ContextUtil getInstance() {
        return instance;
    }

    /**
     * get global msg handler
     * @return      msg handler
     */
    public static GlobalMsgHandler getMsgHandler() {
        return getInstance().mMHHandler;
    }

    /**
     * get DB helper
     * @return      helper
     */
    public static DBOrmLiteHelper getDBHelper() {
        return getInstance().mDBHelper;
    }

    /**
     * get current usr
     * @return      current usr
     */
    public static UsrItem getCurUsr() {
        return getInstance().mUICurUsr;
    }

    /**
     * set current usr
     * @param cur_usr   current usr
     */
    public static void setCurUsr(UsrItem cur_usr) {
        getInstance().mUICurUsr = cur_usr;
    }

    /**
     * get usr db helper
     * @return      usr db helper
     */
    public static UsrDBUtility getUsrUtility() {
        return getInstance().mUsru;
    }

    /**
     * get record type db helper
     * @return      helper
     */
    public static RecordTypeDBUtility getRecordTypeUtility() {
        return getInstance().mRecordTypeu;
    }

    /**
     * get budget helper
     * @return      helper
     */
    public static BudgetDBUtility getBudgetUtility() {
        return getInstance().mBudgetu;
    }

    /**
     * get pay & income data helper
     * @return      helper
     */
    public static PayIncomeDBUtility getPayIncomeUtility() {
        return getInstance().mPayIncomeu;
    }

    /**
     * get remind data helper
     * @return      helper
     */
    public static RemindDBUtility getRemindUtility() {
        return getInstance().mRemindu;
    }

    /**
     * clean db
     */
    public static void ClearDB() {
        try {
            UsrItem ui = getCurUsr();
            if (null != ui) {
                DBOrmLiteHelper dh = getInstance().mDBHelper;
                Integer uid = ui.getId();

                DeleteBuilder<PayNoteItem, Integer> db_pay = dh.getPayDataREDao().deleteBuilder();
                db_pay.where().eq(PayNoteItem.FIELD_USR, uid);
                db_pay.delete();

                DeleteBuilder<IncomeNoteItem, Integer> db_income = dh.getIncomeDataREDao().deleteBuilder();
                db_income.where().eq(IncomeNoteItem.FIELD_USR, uid);
                db_income.delete();

                DeleteBuilder<BudgetItem, Integer> db_budget = dh.getBudgetDataREDao().deleteBuilder();
                db_budget.where().eq(BudgetItem.FIELD_USR, uid);
                db_budget.delete();

                DeleteBuilder<RemindItem, Integer> db_remind = dh.getRemindREDao().deleteBuilder();
                db_remind.where().eq(RemindItem.FIELD_USR, uid);
                db_remind.delete();

                NoteDataHelper.getInstance().refreshData();
            }
        } catch (java.sql.SQLException e) {
            Log.e(TAG, "ClearDB catch an exception", e);
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        mMHHandler = new GlobalMsgHandler();

        // for db
        mDBHelper = new DBOrmLiteHelper(getInstance());

        mUsru = new UsrDBUtility();
        mRecordTypeu = new RecordTypeDBUtility();
        mBudgetu = new BudgetDBUtility();
        mPayIncomeu = new PayIncomeDBUtility();
        mRemindu = new RemindDBUtility();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // for db
        if (null != mDBHelper) {
            mDBHelper.close();
            mDBHelper = null;
        }

        mUsru = null;
        mRecordTypeu = null;
        mBudgetu = null;
        mPayIncomeu = null;
        mRemindu = null;
    }
}
