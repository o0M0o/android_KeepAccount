package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.GraphView.STGraphViewFragment;
import wxm.KeepAccount.ui.fragment.ShowData.STListViewFragment;
import wxm.KeepAccount.ui.fragment.base.SlidingTabsColorsFragment;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ACNoteShow
        extends AppCompatActivity   {
    private static final String TAG = "ACNoteShow";

    // for notes data
    private boolean     mBDayNoteModify = true;
    private boolean     mBMonthNoteModify = true;
    private boolean     mBYearNoteModify = true;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByDay;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByMonth;
    private HashMap<String, ArrayList<Object>> mHMNoteDataByYear;
    /*
    private LinkedList<HashMap<String, Object>>     mHMNoteDataTags;

    public static final String NDTAG_YEAR   = "year";
    public static final String NDTAG_MONTH  = "month";
    public static final String NDTAG_DAY    = "day";
    public static final String NDTAG_LOAD   = "load";
    */

    // for tab colors fragment
    private SlidingTabsColorsFragment mTabFragment;
    private STGraphViewFragment gvTabFragment = new STGraphViewFragment();
    private STListViewFragment  lvTabFragment = new STListViewFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show);

        initView(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acbar_back_help, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acb_mi_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            case R.id.acb_mi_leave :    {
                int ret_data = AppGobalDef.INTRET_USR_LOGOUT;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    public void switchShow()    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mTabFragment instanceof STListViewFragment) {
            mTabFragment = gvTabFragment;
        } else {
            mTabFragment = lvTabFragment;
        }

        transaction.replace(R.id.tabfl_content, mTabFragment);
        transaction.commit();
    }

    public void jumpByTabName(String tabname)  {
        mTabFragment.jumpToTabName(tabname);
    }

    public void filterView(List<String> ls_tag) {
        if(mTabFragment instanceof STListViewFragment)  {
            STListViewFragment sf = UtilFun.cast(mTabFragment);
            sf.filterView(ls_tag);
        }
    }

    public HashMap<String, ArrayList<Object>> getNotesByDay()   {
        if(mBDayNoteModify) {
            mHMNoteDataByDay = AppModel.getPayIncomeUtility().GetAllNotesToDay();
            mBDayNoteModify = false;
        }

        return mHMNoteDataByDay;
    }

    public HashMap<String, ArrayList<Object>> getNotesByMonth()   {
        if(mBMonthNoteModify) {
            mHMNoteDataByMonth = AppModel.getPayIncomeUtility().GetAllNotesToMonth();
            mBMonthNoteModify = false;
        }

        return mHMNoteDataByMonth;
    }

    public HashMap<String, ArrayList<Object>> getNotesByYear()   {
        if(mBYearNoteModify) {
            mHMNoteDataByYear = AppModel.getPayIncomeUtility().GetAllNotesToYear();
            mBYearNoteModify = false;
        }

        return mHMNoteDataByYear;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(AppGobalDef.INTRET_RECORD_ADD == resultCode
                || AppGobalDef.INTRET_RECORD_MODIFY == resultCode)  {
            mBDayNoteModify = true;
            mBMonthNoteModify = true;
            mBYearNoteModify = true;
        }

        if(mTabFragment instanceof STListViewFragment)  {
            STListViewFragment sf = UtilFun.cast(mTabFragment);
            sf.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
    private void updateView() {
        mTabFragment.notifyDataChange();
    }
    */


    /**
     * 初始化
     *
     * @param savedInstanceState activity创建参数
     */
    private void initView(Bundle savedInstanceState) {
        // set fragment for tab
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //mTabFragment = new STListViewFragment();
            mTabFragment = lvTabFragment;
            transaction.replace(R.id.tabfl_content, mTabFragment);
            transaction.commit();
        } else  {
            if(null == mTabFragment)
                mTabFragment = lvTabFragment;
        }
    }
}
