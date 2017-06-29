package wxm.KeepAccount.ui.data.show.note.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * Resource for listview
 * Created by ookoo on 2017/3/3.
 */
public class LVResource {
    public static int mCRLVLineOne;
    public static int mCRLVLineTwo;
    public static int mCRLVItemNoSel;
    public static int mCRLVItemSel;

    static {
        Context ct = ContextUtil.getInstance();
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCRLVLineOne = res.getColor(R.color.color_1, te);
            mCRLVLineTwo = res.getColor(R.color.color_2, te);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half, te);
            mCRLVItemSel = res.getColor(R.color.red_ff725f, te);
        } else {
            mCRLVLineOne = res.getColor(R.color.color_1);
            mCRLVLineTwo = res.getColor(R.color.color_2);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half);
            mCRLVItemSel = res.getColor(R.color.red_ff725f);
        }
    }
}
