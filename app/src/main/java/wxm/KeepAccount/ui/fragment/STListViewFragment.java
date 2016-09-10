package wxm.KeepAccount.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.fragment.LVFRGContent;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;

/**
 * 列表视图方式显示数据
 * Created by wxm on 2016/5/30.
 */
public class STListViewFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "STListViewFragment ";

    public static final String TAB_TITLE_DAILY      = "日统计";
    public static final String TAB_TITLE_MONTHLY    = "月统计";
    public static final String TAB_TITLE_YEARLY     = "年统计";


    protected static class ListViewPagerItem extends SamplePagerItem {
        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            return LVFRGContent.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        if(mTabs.isEmpty()) {
            mTabs.add(new ListViewPagerItem(
                    TAB_TITLE_DAILY, // Title
                    Color.GREEN, // Indicator color
                    Color.GRAY// Divider color
            ));

            mTabs.add(new ListViewPagerItem(
                    TAB_TITLE_MONTHLY, // Title
                    Color.GREEN, // Indicator color
                    Color.GRAY// Divider color
            ));

            mTabs.add(new ListViewPagerItem(
                    TAB_TITLE_YEARLY, // Title
                    Color.GREEN, // Indicator color
                    Color.GRAY// Divider color
            ));
        }

        // END_INCLUDE (populate_tabs)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_main, container, false);
    }
}
