package wxm.KeepAccount.ui.utility;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.wxm.andriodutillib.util.FastViewHolder;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * 日数据头辅助类
 * Created by ookoo on 2017/1/22.
 */
public class HelperDayNotesInfo {
    private final static String LOG_TAG = "HelperDayNotesInfo";

    private static int CR_PAY;
    private static int CR_INCOME;
    private static int DIM_FULL_WIDTH;

    static {
        Resources res = ContextUtil.getInstance().getResources();
        CR_PAY = res.getColor(R.color.darkred);
        CR_INCOME = res.getColor(R.color.darkslategrey);

        DIM_FULL_WIDTH = (int) res.getDimension(R.dimen.rl_amount_info_width);
    }

    /**
     * 填充note信息区
     *
     * @param rl            信息区句柄
     * @param pay_count     支出次数
     * @param pay_amount    支出金额
     * @param income_count  收入次数
     * @param income_amount 收入金额
     * @param amount        结余金额
     */
    public static void fillNoteInfo(RelativeLayout rl, String pay_count, String pay_amount,
                                    String income_count, String income_amount, String amount) {
        boolean b_pay = !"0".equals(pay_count);
        boolean b_income = !"0".equals(income_count);

        RelativeLayout rl_p = UtilFun.cast_t(rl.findViewById(R.id.rl_pay));
        RelativeLayout rl_i = UtilFun.cast_t(rl.findViewById(R.id.rl_income));

        rl_i.setVisibility(View.VISIBLE);
        rl_p.setVisibility(View.VISIBLE);

        ImageView i_iv = UtilFun.cast_t(rl.findViewById(R.id.iv_income_line));
        ImageView p_iv = UtilFun.cast_t(rl.findViewById(R.id.iv_pay_line));
        if (b_pay) {
            ViewGroup.LayoutParams i_para = i_iv.getLayoutParams();
            i_para.width = DIM_FULL_WIDTH;
            i_iv.setLayoutParams(i_para);

            TextView tv = UtilFun.cast_t(rl.findViewById(R.id.tv_pay_count));
            tv.setText(pay_count);

            tv = UtilFun.cast_t(rl.findViewById(R.id.tv_pay_amount));
            tv.setText(pay_amount);
        } else {
            rl_p.setVisibility(View.GONE);
        }

        if (b_income) {
            ViewGroup.LayoutParams p_para = p_iv.getLayoutParams();
            p_para.width = DIM_FULL_WIDTH;
            p_iv.setLayoutParams(p_para);

            TextView tv = UtilFun.cast_t(rl.findViewById(R.id.tv_income_count));
            tv.setText(income_count);

            tv = UtilFun.cast_t(rl.findViewById(R.id.tv_income_amount));
            tv.setText(income_amount);
        } else {
            rl_i.setVisibility(View.GONE);
        }

        if (b_income && b_pay) {
            float pay = Float.valueOf(pay_amount);
            float income = Float.valueOf(income_amount);
            ImageView iv = UtilFun.cast_t(rl.findViewById(pay < income ?
                    R.id.iv_pay_line : R.id.iv_income_line));
            ViewGroup.LayoutParams para = iv.getLayoutParams();
            float ratio = (pay > income ? income : pay)
                    / (pay < income ? income : pay);
            //Log.v(LOG_TAG, "ratio : " + ratio + ", width : " + org_para.width);

            para.width = (int) (DIM_FULL_WIDTH * ratio);
            iv.setLayoutParams(para);
        }

        TextView tv = UtilFun.cast_t(rl.findViewById(R.id.tv_amount));
        tv.setText(amount);
        tv.setTextColor(amount.startsWith("+") ? CR_INCOME : CR_PAY);
    }


    /**
     * 填充note信息区
     *
     * @param vh            视图holder
     * @param pay_count     支出次数
     * @param pay_amount    支出金额
     * @param income_count  收入次数
     * @param income_amount 收入金额
     * @param amount        结余金额
     */
    public static void fillNoteInfo(FastViewHolder vh, String pay_count, String pay_amount,
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
