package wxm.KeepAccount.ui.utility

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import wxm.KeepAccount.R
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase
import wxm.androidutil.ui.view.ViewHolder

/**
 * day data helper
 * Created by WangXM on 2017/1/22.
 */
object HelperDayNotesInfo {
    private val CR_PAY: Int = AppBase.getColor(R.color.darkred)
    private val CR_INCOME: Int = AppBase.getColor(R.color.darkslategrey)
    private val DIM_FULL_WIDTH: Int = AppBase.getDimension(R.dimen.rl_amount_info_width).toInt()

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
        val valPay = "0" != pay_count
        val valIncome = "0" != income_count

        val rlPay = vh.getView<RelativeLayout>(R.id.rl_pay)
        val rlIncome = vh.getView<RelativeLayout>(R.id.rl_income)

        rlIncome.visibility = View.VISIBLE
        rlPay.visibility = View.VISIBLE

        val lineIncome = vh.getView<ImageView>(R.id.iv_income_line)
        val linePay = vh.getView<ImageView>(R.id.iv_pay_line)
        if (valPay) {
            val paramIncome = lineIncome.layoutParams
            paramIncome.width = DIM_FULL_WIDTH
            lineIncome.layoutParams = paramIncome

            var tv = vh.getView<TextView>(R.id.tv_pay_count)
            tv.text = pay_count

            tv = vh.getView(R.id.tv_pay_amount)
            tv.text = pay_amount
        } else {
            rlPay.visibility = View.GONE
        }

        if (valIncome) {
            val paramPay = linePay.layoutParams
            paramPay.width = DIM_FULL_WIDTH
            linePay.layoutParams = paramPay

            var tv = vh.getView<TextView>(R.id.tv_income_count)
            tv.text = income_count

            tv = vh.getView(R.id.tv_income_amount)
            tv.text = income_amount
        } else {
            rlIncome.visibility = View.GONE
        }

        if (valIncome && valPay) {
            val pay = java.lang.Float.valueOf(pay_amount)!!
            val income = java.lang.Float.valueOf(income_amount)!!
            val iv = vh.getView<ImageView>(if (pay < income) R.id.iv_pay_line else R.id.iv_income_line)
            val para = iv.layoutParams
            val ratio = (if (pay > income) income else pay) / if (pay < income) income else pay
            //TagLog.v(LOG_TAG, "ratio : " + ratio + ", width : " + org_para.width);

            para.width = (DIM_FULL_WIDTH * ratio).toInt()
            iv.layoutParams = para
        }

        val tv = vh.getView<TextView>(R.id.tv_amount)
        tv.text = amount
        tv.setTextColor(if (amount.startsWith("+")) CR_INCOME else CR_PAY)
    }
}
