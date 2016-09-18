package wxm.KeepAccount.ui.fragment.GraphView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.utility.ToolUtil;
import wxm.KeepAccount.ui.fragment.base.SlidingTabsColorsFragment;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class GVContentFragment extends Fragment {
    private ChartsBase      cur_view;

    /**
     * @return a new instance of {@link GVContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static GVContentFragment newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(SlidingTabsColorsFragment.KEY_TITLE, title);
        bundle.putInt(SlidingTabsColorsFragment.KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(SlidingTabsColorsFragment.KEY_DIVIDER_COLOR, dividerColor);

        GVContentFragment fragment = new GVContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*        FrameLayout content = new FrameLayout(this.getContext());
        //缩放控件放置在FrameLayout的上层，用于放大缩小图表
        FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;

        //图表显示范围在占屏幕大小的90%的区域内
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scrWidth = (int) (dm.widthPixels * 0.9);
        int scrHeight = (int) (dm.heightPixels * 0.95);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                scrWidth, scrHeight);

        //居中显示
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
        //final RelativeLayout chartLayout = new RelativeLayout(this.getContext());*/

        // 根据页签选择视图
        Bundle args = getArguments();
        CharSequence cs = args.getCharSequence(SlidingTabsColorsFragment.KEY_TITLE);
        assert null != cs;

        String title = cs.toString();
        switch (title) {
            case STGraphViewFragment.TAB_TITLE_DAILY:
                cur_view = new DailyCharts(this.getContext());
                break;
            case STGraphViewFragment.TAB_TITLE_MONTHLY:
                cur_view = new MonthlyCharts(this.getContext());
                break;
            case STGraphViewFragment.TAB_TITLE_YEARLY:
                cur_view = new YeaylyCharts(this.getContext());
                break;
        }

        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*Bundle args = getArguments();
        if (args != null) {
        }*/

        updateView();
    }

    /**
     * 加载并显示数据
     */
    public void updateView() {
        if(null != cur_view) {
            List<Object> ls_ret = AppModel.getPayIncomeUtility().GetAllNotes();
            if(!ToolUtil.ListIsNullOrEmpty(ls_ret))     {
                cur_view.RenderChart(ls_ret);
            }
        }
    }
}


