package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.db.RemindItem;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.RemindEdit.TFEditRemindBase;
import wxm.KeepAccount.ui.fragment.RemindEdit.TFEditRemindBudget;
import wxm.KeepAccount.ui.fragment.RemindEdit.TFEditRemindIncome;
import wxm.KeepAccount.ui.fragment.RemindEdit.TFEditRemindPay;

public class ACRemindEdit extends AppCompatActivity {
    private final static String TAG = "ACRemindEdit";


    private final static String[] REMIND_TYPE =  {
        RemindItem.REMIND_BUDGET, RemindItem.REMIND_PAY, RemindItem.REMIND_INCOME
    };

    private TabLayout   mTLTabs;
    private ViewPager   mVPPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_remind_edit);
        init_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_save_giveup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                int hot = mTLTabs.getSelectedTabPosition();
                if(hot >=0 && hot < REMIND_TYPE.length)     {
                    TFEditRemindBase tb = UtilFun.cast(mVPPages.getChildAt(hot));
                    if(tb.onAccept())   {
                        int ret_data = AppGobalDef.INTRET_SURE;

                        Intent data = new Intent();
                        setResult(ret_data, data);
                        finish();
                    }
                }
            }
            break;

            case R.id.mi_giveup:    {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

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


    /// BEGIN PRIVATE
    /**
     * 初始化view
     */
    private void init_view() {
        mTLTabs = (TabLayout) findViewById(R.id.tl_tabs);
        mVPPages = UtilFun.cast(findViewById(R.id.vp_remind_pages));
        assert null != mTLTabs && null != mVPPages;

        // for tablayout
        for(String i : REMIND_TYPE) {
            mTLTabs.addTab(mTLTabs.newTab().setText(i));
        }
        mTLTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        // for pages
        // 添加view的顺序必须和 REMIND_TYPE 保持一致
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), REMIND_TYPE.length);
        mVPPages.setAdapter(adapter);

        // for listen
        mVPPages.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVPPages.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    /// END PRIVATE

    /**
     * fragment adapter
     */
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment ret = null;
            switch (position)   {
                case 0 :
                    ret = new TFEditRemindBudget();
                    break;

                case 1 :
                    ret = new TFEditRemindPay();
                    break;

                case 2 :
                    ret = new TFEditRemindIncome();
                    break;
            }

            return ret;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

}
