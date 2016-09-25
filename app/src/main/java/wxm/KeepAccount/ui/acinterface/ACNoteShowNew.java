package wxm.KeepAccount.ui.acinterface;

import android.app.ActivityGroup;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TabHost;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acutility.ACDailyShow;
import wxm.KeepAccount.ui.fragment.ShowData.TabFragment1;
import wxm.KeepAccount.ui.fragment.ShowData.TabFragment2;
import wxm.KeepAccount.ui.fragment.ShowData.TabFragment3;

public class ACNoteShowNew extends AppCompatActivity {
    private final static String TAG = "ACNoteShowNew";

    protected final static String TAB_DAILY     = "日统计";
    protected final static String TAB_MONTHLY   = "月统计";
    protected final static String TAB_YEARLY    = "年统计";

    private float       mLastX;
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

    /*
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                mLastX = touchevent.getX();
            }
            break;

            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();
                if(currentX != mLastX)
                    switchTabs(mLastX > currentX);
            }
            break;
        }
        return false;
    }

    /**
     * 切换tabhost当前标签页
     * @param direction     如向右移动则为false, 否则为true
    public void switchTabs(boolean direction) {
        int cur_pos = mTLTabs.getSelectedTabPosition();
        int tab_sz = mTLTabs.getTabCount();
        int n_pos = direction ?
                        cur_pos == 0 ? tab_sz - 1 : cur_pos - 1
                        : cur_pos != (tab_sz - 1) ? cur_pos + 1 : 0;

        mTLTabs.setScrollPosition(n_pos, 0, true);
    }
    */


    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fr;
            switch (position) {
                case 0:
                    fr = new TabFragment1();
                    break;

                case 1:
                    fr = new TabFragment2();
                    break;

                case 2:
                    fr = new TabFragment3();
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
