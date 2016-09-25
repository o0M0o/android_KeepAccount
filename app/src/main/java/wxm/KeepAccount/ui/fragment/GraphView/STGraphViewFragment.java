package wxm.KeepAccount.ui.fragment.GraphView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.base.SlidingTabsColorsFragment;

/**
 * 图表视图表现数据
 * Created by wxm on 2016/5/30.
 */
public class STGraphViewFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "GVSlidingTabsFragment ";

    public static final String TAB_TITLE_DAILY      = "日统计";
    public static final String TAB_TITLE_MONTHLY    = "月统计";
    public static final String TAB_TITLE_YEARLY     = "年统计";

    protected static class ListViewPagerItem extends SamplePagerItem {
        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            return GVContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_main, container, false);
    }


    @Override
    public void notifyDataChange()  {
        FragmentManager fragmentManager = getChildFragmentManager();
        //FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment f : fragments) {
            GVContentFragment gvf = (GVContentFragment)f;
            gvf.updateView();
        }
        //transaction.commit();
    }
}

