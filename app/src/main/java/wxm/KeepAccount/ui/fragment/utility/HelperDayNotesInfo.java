package wxm.KeepAccount.ui.fragment.utility;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;

/**
 * 日数据头辅助类
 * Created by ookoo on 2017/1/22.
 */
public class HelperDayNotesInfo {
    private final static String LOG_TAG = "HelperDayNotesInfo";

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
        Resources res = ContextUtil.getInstance().getResources();
        int mCRForPay = res.getColor(R.color.darkred);
        int mCRForIncome = res.getColor(R.color.darkslategrey);

        boolean b_pay = !"0".equals(pay_count);
        boolean b_income = !"0".equals(income_count);

        RelativeLayout rl_p = UtilFun.cast_t(rl.findViewById(R.id.rl_pay));
        RelativeLayout rl_i = UtilFun.cast_t(rl.findViewById(R.id.rl_income));
        int full_width = (int)res.getDimension(R.dimen.rl_amount_info_width);
        //RelativeLayout rl_father = UtilFun.cast_t(rl.findViewById(R.id.rl_pay_income));
        //int full_width = rl_father.getWidth();
        //Log.e(LOG_TAG, "full_width : " + full_width);

        rl_i.setVisibility(View.VISIBLE);
        rl_p.setVisibility(View.VISIBLE);

        ImageView i_iv = UtilFun.cast_t(rl.findViewById(R.id.iv_income_line));
        ImageView p_iv = UtilFun.cast_t(rl.findViewById(R.id.iv_pay_line));
        if (b_pay) {
            ViewGroup.LayoutParams i_para = i_iv.getLayoutParams();
            i_para.width = full_width;
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
            p_para.width = full_width;
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

            para.width = (int) (full_width * ratio);
            iv.setLayoutParams(para);
        }

        TextView tv = UtilFun.cast_t(rl.findViewById(R.id.tv_amount));
        tv.setText(amount);
        tv.setTextColor(amount.startsWith("+") ? mCRForIncome : mCRForPay);
    }
}
