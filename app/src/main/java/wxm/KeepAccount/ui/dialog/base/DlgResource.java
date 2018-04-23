package wxm.KeepAccount.ui.dialog.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.Objects;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * dialog会用到的资源
 * Created by WangXM on 2017/3/8.
 */
public class DlgResource {
    // for channel item shape
    public static Drawable mDAChannelSel;
    public static Drawable mDAChannelNoSel;

    // for sort icon
    public static Drawable mDASortDown;
    public static Drawable mDASortUp;

    // for show string
    public static String mSZSortByNameDown;
    public static String mSZSortByNameUp;

    // for color
    public static int mCLSelected;
    public static int mCLNotSelected;

    static {
        Context ct = Objects.requireNonNull(ContextUtil.Companion.getInstance());
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        mSZSortByNameUp = res.getString(R.string.cn_sort_up_by_name);
        mSZSortByNameDown = res.getString(R.string.cn_sort_down_by_name);

        mDAChannelNoSel = res.getDrawable(R.drawable.gi_channel_no_sel_shape, te);
        mDAChannelSel = res.getDrawable(R.drawable.gi_channel_sel_shape, te);

        mDASortDown = res.getDrawable(R.drawable.ic_sort_down_1, te);
        mDASortUp = res.getDrawable(R.drawable.ic_sort_up_1, te);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCLSelected = res.getColor(R.color.peachpuff, te);
            mCLNotSelected = res.getColor(R.color.white, te);
        } else {
            mCLSelected = res.getColor(R.color.peachpuff);
            mCLNotSelected = res.getColor(R.color.white);
        }
    }
}
