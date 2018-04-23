package wxm.KeepAccount.ui.utility

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import java.util.Objects

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.ViewHolder.ViewHolder

/**
 * day data helper
 * Created by WangXM on 2017/1/22.
 */
object HelperDayNotesInfo {
    private var CR_PAY: Int = 0
    private var CR_INCOME: Int = 0
    private var DIM_FULL_WIDTH: Int = 0

    init {
        val ct = Objects.requireNonNull<ContextUtil>(ContextUtil.instance)
        val res = ct.resources
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val te = ct.theme
            CR_PAY = res.getColor(R.color.darkred, te)
            CR_INCOME = res.getColor(R.color.darkslategrey, te)
        } else {
            CR_PAY = res.getColor(R.color.darkred)
            CR_INCOME = res.getColor(R.color.darkslategrey)
        }

        DIM_FULL_WIDTH = res.getDimension(R.dimen.rl_amount_info_width).toInt()
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
    fun fillNoteInfo(vh: ViewHolder, pay_count: String, pay_amount: String,
                     income_count: String, income_amount: String, amount: String) {
        val b_pay = "0" != pay_count
        val b_income = "0" != income_count

        val rl_p = vh.getView<RelativeLayout>(R.id.rl_pay)
        val rl_i = vh.getView<RelativeLayout>(R.id.rl_income)

        rl_i.visibility = View.VISIBLE
        rl_p.visibility = View.VISIBLE

        val i_iv = vh.getView<ImageView>(R.id.iv_income_line)
        val p_iv = vh.getView<ImageView>(R.id.iv_pay_line)
        if (b_pay) {
            val i_para = i_iv.layoutParams
            i_para.width = DIM_FULL_WIDTH
            i_iv.layoutParams = i_para

            var tv = vh.getView<TextView>(R.id.tv_pay_count)
            tv.text = pay_count

            tv = vh.getView(R.id.tv_pay_amount)
            tv.text = pay_amount
        } else {
            rl_p.visibility = View.GONE
        }

        if (b_income) {
            val p_para = p_iv.layoutParams
            p_para.width = DIM_FULL_WIDTH
            p_iv.layoutParams = p_para

            var tv = vh.getView<TextView>(R.id.tv_income_count)
            tv.text = income_count

            tv = vh.getView(R.id.tv_income_amount)
            tv.text = income_amount
        } else {
            rl_i.visibility = View.GONE
        }

        if (b_income && b_pay) {
            val pay = java.lang.Float.valueOf(pay_amount)!!
            val income = java.lang.Float.valueOf(income_amount)!!
            val iv = vh.getView<ImageView>(if (pay < income) R.id.iv_pay_line else R.id.iv_income_line)
            val para = iv.layoutParams
            val ratio = (if (pay > income) income else pay) / if (pay < income) income else pay
            //Log.v(LOG_TAG, "ratio : " + ratio + ", width : " + org_para.width);

            para.width = (DIM_FULL_WIDTH * ratio).toInt()
            iv.layoutParams = para
        }

        val tv = vh.getView<TextView>(R.id.tv_amount)
        tv.text = amount
        tv.setTextColor(if (amount.startsWith("+")) CR_INCOME else CR_PAY)
    }
}
