package wxm.KeepAccount.ui.fragment.ShowData;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.fragment.base.SlidingTabsColorsFragment;

/**
 * 列表视图方式显示数据
 * Created by wxm on 2016/5/30.
 */
public class STListViewFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "STListViewFragment ";

    public final static String MPARA_TITLE      = "MPARA_TITLE";
    public final static String MPARA_ABSTRACT   = "MPARA_ABSTRACT";
    public final static String MPARA_TAG        = "MPARA_TAG";

    public final static String SPARA_TITLE  = "SPARA_TITLE";
    public final static String SPARA_DETAIL = "SPARA_DETAIL";
    public final static String SPARA_TAG    = "SPARA_TAG";
    public final static String SPARA_ID     = "SPARA_ID";

    public final static String MPARA_SHOW           = "MPARA_SHOW";
    public final static String MPARA_SHOW_UNFOLD    = "SHOW_UNFOLD";
    public final static String MPARA_SHOW_FOLD      = "SHOW_FOLD";

    public final static String SPARA_TAG_PAY    = "TAG_PAY";
    public final static String SPARA_TAG_INCOME = "TAG_INCOME";

    public static final String TAB_TITLE_DAILY      = "日统计";
    public static final String TAB_TITLE_MONTHLY    = "月统计";
    public static final String TAB_TITLE_YEARLY     = "年统计";
    public static final String TAB_TITLE_BUDGET     = "预算";


    protected static class ListViewPagerItem extends SamplePagerItem {
        protected LVFRGContent  mContent;

        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            mContent = LVFRGContent.newInstance(mTitle, mIndicatorColor, mDividerColor);
            return mContent;
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        if(mTabs.isEmpty()) {
            mTabs.add(new ListViewPagerItem(
                    TAB_TITLE_BUDGET, // Title
                    Color.GREEN, // Indicator color
                    Color.GRAY// Divider color
            ));

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

    /**
     * 过滤视图
     * @param ls_tag     待过滤的tag
     */
    public void filterView(List<String> ls_tag)     {
        int cur_pos = getCurViewPostion();
        if (AppGobalDef.INVALID_ID != cur_pos) {
            ListViewPagerItem hot_it = UtilFun.cast(mTabs.get(cur_pos));
            hot_it.mContent.doFilter(ls_tag);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int cur_pos = getCurViewPostion();
        if(AppGobalDef.INVALID_ID != cur_pos) {
            ListViewPagerItem hot_it = UtilFun.cast(mTabs.get(cur_pos));
            hot_it.mContent.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * listview fragment的内容类
     */
    public static class LVFRGContent  extends Fragment {
        private static final String TAG = "LVFRGContent";

        //private String      mCurTitle;
        private LVViewHelperBase    mViewHelper;

        /**
         * @return a new instance of {@link LVFRGContent}, adding the parameters into a bundle and
         * setting them as arguments.
         */
        public static LVFRGContent newInstance(CharSequence title,
                                               int indicatorColor, int dividerColor) {
            Bundle bundle = new Bundle();
            bundle.putCharSequence(KEY_TITLE, title);
            bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
            bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

            LVFRGContent fragment = new LVFRGContent();
            fragment.setArguments(bundle);
            return fragment;
        }

        public LVFRGContent()  {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            assert null != args;

            CharSequence cs = args.getCharSequence(KEY_TITLE);
            assert null != cs;

            String mCurTitle = cs.toString();
            switch(mCurTitle)   {
                case STListViewFragment.TAB_TITLE_DAILY :
                    mViewHelper = new DailyViewHelper();
                    break;

                case STListViewFragment.TAB_TITLE_MONTHLY :
                    mViewHelper = new MonthlyViewHelper();
                    break;

                case STListViewFragment.TAB_TITLE_YEARLY :
                    mViewHelper = new YearlyViewHelper();
                    break;

                case STListViewFragment.TAB_TITLE_BUDGET :
                    mViewHelper = new BudgetViewHelper();
                    break;
            }

            return mViewHelper.createView(inflater, container);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            /* Bundle args = getArguments();
            if (args != null) {
            }*/
            mViewHelper.loadView();
        }

        public void doFilter(List<String> ls_tag)   {
            mViewHelper.filterView(ls_tag);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mViewHelper.onActivityResult(requestCode, resultCode, data);
        }
    }
}
