package wxm.KeepAccount.ui.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.KeepAccount.ui.fragment.STListViewFragment;
import wxm.KeepAccount.ui.viewhelper.DailyViewHelper;
import wxm.KeepAccount.ui.viewhelper.ILVViewHelper;
import wxm.KeepAccount.ui.viewhelper.MonthlyViewHelper;
import wxm.KeepAccount.ui.viewhelper.YearlyViewHelper;

/**
 * listview fragment的内容类
 * Created by 123 on 2016/9/10.
 */
public class LVFRGContent  extends Fragment {
    public final static String MPARA_TITLE      = "MPARA_TITLE";
    public final static String MPARA_ABSTRACT   = "MPARA_ABSTRACT";
    public final static String MPARA_TAG    = "MPARA_TAG";
    public final static String MPARA_STATUS = "MPARA_STATUS";

    public final static String SPARA_SHOW   = "SPARA_SHOW";
    public final static String SPARA_TAG    = "SPARA_TAG";

    public final static String MPARA_TAG_SHOW = "TAG_SHOW";
    public final static String MPARA_TAG_HIDE = "TAG_Hide";

    public final static String SPARA_TAG_PAY    = "TAG_PAY";
    public final static String SPARA_TAG_INCOME = "TAG_INCOME";


    private static final String TAG = "LVFRGContent";

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private String              mCurTitle;
    private ILVViewHelper mViewHelper;


    /**
     * @return a new instance of {@link LVContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static LVFRGContent newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor) {
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

        mCurTitle = cs.toString();
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
}
