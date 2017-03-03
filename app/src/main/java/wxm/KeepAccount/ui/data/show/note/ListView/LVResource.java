package wxm.KeepAccount.ui.data.show.note.ListView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * ListView会用到的资源
 * Created by ookoo on 2017/3/3.
 */
class LVResource {
    static int mCRLVLineOne;
    static int mCRLVLineTwo;
    static int mCRLVItemNoSel;
    static int mCRLVItemSel;

    static Drawable     mDASortUp;
    static Drawable     mDASortDown;

    static {
        Context ct = ContextUtil.getInstance();
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCRLVLineOne = res.getColor(R.color.color_1, te);
            mCRLVLineTwo = res.getColor(R.color.color_2, te);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half, te);
            mCRLVItemSel = res.getColor(R.color.red_ff725f, te);

            mDASortUp = res.getDrawable(R.drawable.ic_sort_up_1, te);
            mDASortDown = res.getDrawable(R.drawable.ic_sort_down_1, te);
        } else  {
            mCRLVLineOne = res.getColor(R.color.color_1);
            mCRLVLineTwo = res.getColor(R.color.color_2);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half);
            mCRLVItemSel = res.getColor(R.color.red_ff725f);

            mDASortUp = res.getDrawable(R.drawable.ic_sort_up_1);
            mDASortDown = res.getDrawable(R.drawable.ic_sort_down_1);
        }
    }
}
