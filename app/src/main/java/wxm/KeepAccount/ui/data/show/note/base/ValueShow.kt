package wxm.KeepAccount.ui.data.show.note.base


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import java.math.BigDecimal
import java.util.Locale

import kotterknife.bindView
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R


/**
 * use this help show data
 * @author      wxm
 * @version     create：2017/03/28
 */
class ValueShow(context: Context, attrs: AttributeSet)
        : ConstraintLayout(context, attrs) {
    private val mTVPayTag: TextView by bindView(R.id.tv_pay_tag)
    private val mTVPayCount: TextView by bindView(R.id.tv_pay_count)
    private val mTVPayAmount: TextView by bindView(R.id.tv_pay_amount)
    private val mTVIncomeTag: TextView by bindView(R.id.tv_income_tag)
    private val mTVIncomeCount: TextView by bindView(R.id.tv_income_count)
    private val mTVIncomeAmount: TextView by bindView(R.id.tv_income_amount)
    private val mTVBalanceAmount: TextView by bindView(R.id.tv_balance_amount)
    private val mIVPayLine: ImageView by bindView(R.id.iv_pay_line)
    private val mIVIncomeLine: ImageView by bindView(R.id.iv_income_line)

    /**
     * can set attributes
     */
    private var mAttrPayCount: String? = null
    private var mAttrPayAmount: String? = null

    private var mAttrIncomeCount: String? = null
    private var mAttrIncomeAmount: String? = null

    private var mAttrDMLineLen: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.vw_value_show, this)
        initComponent(context, attrs)
    }

    /**
     * adjust attributes
     * @param attrs     new attributes
     */
    fun adjustAttribute(attrs: Map<String, Any>) {
        for (k in attrs.keys) {
            when (k) {
                ATTR_PAY_COUNT -> mAttrPayCount = attrs[k] as String
                ATTR_PAY_AMOUNT -> mAttrPayAmount = attrs[k] as String
                ATTR_INCOME_COUNT -> mAttrIncomeCount = attrs[k] as String
                ATTR_INCOME_AMOUNT -> mAttrIncomeAmount = attrs[k] as String
            }
        }

        updateShow()
    }

    /**
     * use date init UI
     * @param context   context for UI
     * @param attrs     data for UI
     */
    private fun initComponent(context: Context, attrs: AttributeSet) {
        // for parameter
        val array = context.obtainStyledAttributes(attrs, R.styleable.ValueShow)
        try {
            mAttrPayCount = array.getString(R.styleable.ValueShow_szPayCount)
            mAttrPayCount = if (UtilFun.StringIsNullOrEmpty(mAttrPayCount)) "0" else mAttrPayCount

            mAttrPayAmount = array.getString(R.styleable.ValueShow_szPayAmount)
            mAttrPayAmount = if (UtilFun.StringIsNullOrEmpty(mAttrPayAmount)) "0" else mAttrPayAmount

            mAttrIncomeCount = array.getString(R.styleable.ValueShow_szIncomeCount)
            mAttrIncomeCount = if (UtilFun.StringIsNullOrEmpty(mAttrIncomeCount)) "0" else mAttrIncomeCount

            mAttrIncomeAmount = array.getString(R.styleable.ValueShow_szIncomeAmount)
            mAttrIncomeAmount = if (UtilFun.StringIsNullOrEmpty(mAttrIncomeAmount)) "0" else mAttrIncomeAmount

            val dst = getContext().resources.displayMetrics.density
            mAttrDMLineLen = array.getLayoutDimension(R.styleable.ValueShow_pxLineLen,
                    (200 * dst).toInt())

            updateShow()
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "catch ex : " + ex.toString())
        } finally {
            array.recycle()
        }
    }

    /**
     * update UI
     */
    private fun updateShow() {
        mTVPayCount.text = mAttrPayCount
        mTVPayAmount.text = mAttrPayAmount

        mTVIncomeCount.text = mAttrIncomeCount
        mTVIncomeAmount.text = mAttrIncomeAmount

        val bdPay = BigDecimal(mAttrPayAmount)
        val bdIncome = BigDecimal(mAttrIncomeAmount)
        val bdBig = BigDecimal(Math.max(bdPay.toFloat(), bdIncome.toFloat()).toDouble())

        val bdBalance = bdIncome.subtract(bdPay)
        val szBalance = String.format(Locale.CHINA, "%.02f", bdBalance.toFloat())
        mTVBalanceAmount.text = szBalance

        val bHavePay = mAttrPayCount == "0"
        mIVPayLine.visibility = if (bHavePay) View.GONE else View.VISIBLE
        mTVPayTag.visibility = if (bHavePay) View.GONE else View.VISIBLE
        mTVPayCount.visibility = if (bHavePay) View.GONE else View.VISIBLE
        mTVPayAmount.visibility = if (bHavePay) View.GONE else View.VISIBLE
        val mPayLinePercent = if (bHavePay) 0.toFloat() else bdPay.toFloat() / bdBig.toFloat()

        if (0.01 < mPayLinePercent)
            adjustLineLen(mIVPayLine, mPayLinePercent)

        val bHaveIncome = mAttrIncomeCount == "0"
        mIVIncomeLine.visibility = if (bHaveIncome) View.GONE else View.VISIBLE
        mTVIncomeTag.visibility = if (bHaveIncome) View.GONE else View.VISIBLE
        mTVIncomeCount.visibility = if (bHaveIncome) View.GONE else View.VISIBLE
        mTVIncomeAmount.visibility = if (bHaveIncome) View.GONE else View.VISIBLE
        val mIncomeLinePercent = if (bHaveIncome) 0.toFloat() else bdIncome.toFloat() / bdBig.toFloat()

        if (0.01 < mIncomeLinePercent)
            adjustLineLen(mIVIncomeLine, mIncomeLinePercent)

        invalidate()
        requestLayout()
    }

    /**
     * adjust line length
     * @param iv        line need adjust
     * @param percent   new length(percentage)
     */
    private fun adjustLineLen(iv: ImageView, percent: Float) {
        val lp = iv.layoutParams
        lp.width = (mAttrDMLineLen * percent).toInt()
        iv.layoutParams = lp
    }

    companion object {
        const val ATTR_PAY_COUNT = "pay_count"
        const val ATTR_PAY_AMOUNT = "pay_amount"
        const val ATTR_INCOME_COUNT = "income_count"
        const val ATTR_INCOME_AMOUNT = "income_amount"
        private val LOG_TAG = ::ValueShow.javaClass.simpleName
    }
}
