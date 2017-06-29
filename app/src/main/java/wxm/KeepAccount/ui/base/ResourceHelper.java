package wxm.KeepAccount.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * Resource helper
 * Created by ookoo on 2017/3/3.
 */
public class ResourceHelper {
    public static int mCRLVLineOne;
    public static int mCRLVLineTwo;
    public static int mCRLVItemNoSel;
    public static int mCRLVItemSel;

    public static int mCRTextWhite;
    public static int mCRTextFit;

    static {
        Context ct = ContextUtil.getInstance();
        Resources res = ct.getResources();
        Resources.Theme te = ct.getTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCRLVLineOne = res.getColor(R.color.color_1, te);
            mCRLVLineTwo = res.getColor(R.color.color_2, te);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half, te);
            mCRLVItemSel = res.getColor(R.color.red_ff725f, te);

            mCRTextWhite = res.getColor(R.color.white, te);
            mCRTextFit = res.getColor(R.color.text_fit, te);
        } else {
            mCRLVLineOne = res.getColor(R.color.color_1);
            mCRLVLineTwo = res.getColor(R.color.color_2);

            mCRLVItemNoSel = res.getColor(R.color.red_ff725f_half);
            mCRLVItemSel = res.getColor(R.color.red_ff725f);

            mCRTextWhite = res.getColor(R.color.white);
            mCRTextFit = res.getColor(R.color.text_fit);
        }
    }
}
