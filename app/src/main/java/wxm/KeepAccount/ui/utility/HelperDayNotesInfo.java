package wxm.KeepAccount.ui.utility;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;
import wxm.androidutil.ViewHolder.ViewHolder;

/**
 * day data helper
 * Created by WangXM on 2017/1/22.
 */
public class HelperDayNotesInfo {
    private static int CR_PAY;
    private static int CR_INCOME;
    private static int DIM_FULL_WIDTH;

    static {
        Context ct = Objects.requireNonNull(ContextUtil.Companion.getInstance());
        Resources res = ct.getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Resources.Theme te = ct.getTheme();
            CR_PAY = res.getColor(R.color.darkred, te);
            CR_INCOME = res.getColor(R.color.darkslategrey, te);
        } else {
            CR_PAY = res.getColor(R.color.darkred);
            CR_INCOME = res.getColor(R.color.darkslategrey);
        }

        DIM_FULL_WIDTH = (int) res.getDimension(R.dimen.rl_amount_info_width);
    }

    /**
     * fill note UI
     *
     * @param vh            holder for view
     * @param pay_count     pay count in one day
     * @param pay_amount    pay amount in one day
     * @param income_count  income count in one day
     * @param income_amount income amount in one day
     * @param amount        balance amount in one day
     */
    public static void fillNoteInfo(ViewHolder vh, String pay_count, String pay_amount,
                                    String income_count, String income_amount, String amount) {
        boolean b_pay = !"0".equals(pay_count);
        boolean b_income = !"0".equals(income_count);

        RelativeLayout rl_p = vh.getView(R.id.rl_pay);
        RelativeLayout rl_i = vh.getView(R.id.rl_income);

        rl_i.setVisibility(View.VISIBLE);
        rl_p.setVisibility(View.VISIBLE);

        ImageView i_iv = vh.getView(R.id.iv_income_line);
        ImageView p_iv = vh.getView(R.id.iv_pay_line);
        if (b_pay) {
            ViewGroup.LayoutParams i_para = i_iv.getLayoutParams();
            i_para.width = DIM_FULL_WIDTH;
            i_iv.setLayoutParams(i_para);

            TextView tv = vh.getView(R.id.tv_pay_count);
            tv.setText(pay_count);

            tv = vh.getView(R.id.tv_pay_amount);
            tv.setText(pay_amount);
        } else {
            rl_p.setVisibility(View.GONE);
        }

        if (b_income) {
            ViewGroup.LayoutParams p_para = p_iv.getLayoutParams();
            p_para.width = DIM_FULL_WIDTH;
            p_iv.setLayoutParams(p_para);

            TextView tv = vh.getView(R.id.tv_income_count);
            tv.setText(income_count);

            tv = vh.getView(R.id.tv_income_amount);
            tv.setText(income_amount);
        } else {
            rl_i.setVisibility(View.GONE);
        }

        if (b_income && b_pay) {
            float pay = Float.valueOf(pay_amount);
            float income = Float.valueOf(income_amount);
            ImageView iv = vh.getView(pay < income ? R.id.iv_pay_line : R.id.iv_income_line);
            ViewGroup.LayoutParams para = iv.getLayoutParams();
            float ratio = (pay > income ? income : pay)
                    / (pay < income ? income : pay);
            //Log.v(LOG_TAG, "ratio : " + ratio + ", width : " + org_para.width);

            para.width = (int) (DIM_FULL_WIDTH * ratio);
            iv.setLayoutParams(para);
        }

        TextView tv = vh.getView(R.id.tv_amount);
        tv.setText(amount);
        tv.setTextColor(amount.startsWith("+") ? CR_INCOME : CR_PAY);
    }
}
