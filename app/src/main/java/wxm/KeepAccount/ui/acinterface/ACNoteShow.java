package wxm.KeepAccount.ui.acinterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import wxm.KeepAccount.ui.fragment.STGraphViewFragment;
import wxm.KeepAccount.ui.fragment.STListViewFragment;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ACNoteShow
        extends AppCompatActivity   {
    private static final String TAG = "ACNoteShow";

    private SlidingTabsColorsFragment mTabFragment;
    private STGraphViewFragment gvTabFragment = new STGraphViewFragment();
    private STListViewFragment lvTabFragment = new STListViewFragment();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Boolean bModify = false;
        if (AppGobalDef.INTRET_RECORD_ADD == resultCode) {
            Log.i(TAG, "从'添加记录'页面返回");
            bModify = true;
        } else if (AppGobalDef.INTRET_DAILY_DETAIL == resultCode) {
            Log.i(TAG, "从详情页面返回");
            bModify = true;
        } else {
            Log.d(TAG, String.format("不处理的resultCode(%d)!", resultCode));
        }

        if (bModify) {
            updateView();
        }
    }

    private void updateView() {
        mTabFragment.notifyDataChange();
    }


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
        }
    }
}
