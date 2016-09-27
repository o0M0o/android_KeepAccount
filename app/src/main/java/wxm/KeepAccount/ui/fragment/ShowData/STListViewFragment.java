package wxm.KeepAccount.ui.fragment.ShowData;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
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
}
