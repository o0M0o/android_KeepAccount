package wxm.KeepAccount.ui.acinterface;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowDaily;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowYearly;
import wxm.KeepAccount.ui.fragment.ShowData.TFShowMonthly;

public class ACNoteShowNew extends AppCompatActivity {
    private final static String TAG = "ACNoteShowNew";

    protected final static String TAB_DAILY     = "日统计";
    protected final static String TAB_MONTHLY   = "月统计";
    protected final static String TAB_YEARLY    = "年统计";

    private TabLayout   mTLTabs;
    private ViewPager   mVPTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_note_show_new);

        init_tabs();
    }

    private void init_tabs() {
        mTLTabs = (TabLayout) findViewById(R.id.tl_tabs);
        assert null != mTLTabs;
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_DAILY));
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_MONTHLY));
        mTLTabs.addTab(mTLTabs.newTab().setText(TAB_YEARLY));
        mTLTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        mVPTabs = (ViewPager) findViewById(R.id.tab_pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), mTLTabs.getTabCount());
        mVPTabs.setAdapter(adapter);
        mVPTabs.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTLTabs));
        mTLTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVPTabs.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fr;
            TabLayout.Tab t = mTLTabs.getTabAt(position);
            assert null != t;
            CharSequence t_cs = t.getText();
            assert null != t_cs;
            switch (t_cs.toString()) {
                case TAB_DAILY:
                    fr = new TFShowDaily();
                    break;

                case TAB_MONTHLY:
                    fr = new TFShowMonthly();
                    break;

                case TAB_YEARLY:
                    fr = new TFShowYearly();
                    break;

                default:
                    fr = null;
                    break;
            }

            return fr;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
