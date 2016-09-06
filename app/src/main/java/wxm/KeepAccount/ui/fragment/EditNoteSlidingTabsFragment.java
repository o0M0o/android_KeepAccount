package wxm.KeepAccount.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.base.fragment.NoteContentFragment;
import wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;

/**
 * 编辑收支数据的视图
 * Created by 123 on 2016/9/6.
 */
public class EditNoteSlidingTabsFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "EditNoteSlidingTabsFragment ";

    protected static class ListViewPagerItem extends SamplePagerItem {
        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            return NoteContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        if(mTabs.isEmpty()) {
            mTabs.add(new ListViewPagerItem(
                    AppGobalDef.CNSTR_RECORD_PAY, // Title
                    Color.GREEN, // Indicator color
                    Color.GRAY// Divider color
            ));

            mTabs.add(new ListViewPagerItem(
                    AppGobalDef.CNSTR_RECORD_INCOME, // Title
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
