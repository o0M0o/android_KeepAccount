package wxm.KeepAccount.ui.welcome.page.pieChartStat

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import kotterknife.bindView
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.forObj
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.view.EventHelper
import java.math.BigDecimal
import java.util.*

/**
 * @author      WangXM
 * @version     create：2018/6/15
 */
abstract class PieChartBase : FrgSupportBaseAdv() {
    data class ChartItem(val mType: Int, val mSZName: String, var mBDVal: BigDecimal) {
        fun isPay() = mType == PAY_ITEM
        fun isIncome() = mType == INCOME_ITEM
    }

    private val mCVChart: PieChartView by bindView(R.id.chart)
    private val mTBIncome: ToggleButton by bindView(R.id.tb_income)
    private val mTBPay: ToggleButton by bindView(R.id.tb_pay)
    protected val mTVDateRange: TextView by bindView(R.id.tv_date_range)
    protected val mIVLeft: ImageView by bindView(R.id.iv_left)
    protected val mIVRight: ImageView by bindView(R.id.iv_right)
    protected val mTVDayInWeek: TextView by bindView(R.id.tv_date_in_week)

    protected val mTVPay: TextView by bindView(R.id.tv_pay)
    protected val mTVIncome: TextView by bindView(R.id.tv_income)
    protected val mTVTotal: TextView by bindView(R.id.tv_total)

    private val mCIItem = LinkedList<ChartItem>()

    override fun getLayoutID(): Int = R.layout.pg_stat_pie_chart

    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)

        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.tb_income, R.id.tb_pay),
                { v ->
                    when (v.id) {
                        R.id.tb_income -> {
                            mTBPay.isClickable = mTBIncome.isChecked
                        }

                        R.id.tb_pay -> {
                            mTBIncome.isClickable = mTBPay.isChecked
                        }
                    }

                    // update show
                    mCVChart.circleFillRatio = 0.6f
                    mCVChart.pieChartData = generateData()
                })

        loadUI(savedInstanceState)
    }


    @Suppress("RedundantOverride")
    override fun loadUI(savedInstanceState: Bundle?) {
        super.loadUI(savedInstanceState)

        /*
        mCVChart.onValueTouchListener = object : PieChartOnValueSelectListener {
            override fun onValueSelected(i: Int, sliceValue: SliceValue) {
                val sz = String.format(Locale.CHINA, "%s : %.02f",
                        String(sliceValue.labelAsChars), sliceValue.value)
                Toast.makeText(activity, sz, Toast.LENGTH_SHORT).show()
            }

            override fun onValueDeselected() {}
        }
        */
    }


    private fun generateData(): PieChartData {
        // create chart item list
        val bPay = mTBPay.isChecked
        val bIncome = mTBIncome.isChecked
        val lsCI = mCIItem.filter { (bPay && it.isPay()) || (bIncome && it.isIncome()) }

        // create values
        val pd = PieChartData()
        pd.values = lsCI.map {
            SliceValue(it.mBDVal.toFloat(), ChartUtils.pickColor()).apply {
                setLabel(String.format(Locale.CHINA, "%S %s", it.mSZName, it.mBDVal.toMoneyStr()))
            }
        }

        pd.setHasLabels(true)
        pd.setHasLabelsOutside(true)
        pd.setHasCenterCircle(true)
        pd.slicesSpacing = 12

        // hasCenterText1
        pd.centerText1 = getString(if (bPay && bIncome) R.string.cn_income_pay
        else if (bPay) R.string.cn_pay else R.string.cn_income)

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        pd.centerText1FontSize = ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity,
                resources.getDimensionPixelSize(R.dimen.pie_chart_text_size))
        return pd
    }


    protected fun loadData(startDay: String, endDay: String) {
        val vLeft = mIVLeft.visibility
        val vRight = mIVRight.visibility

        mIVLeft.visibility = View.INVISIBLE
        mIVRight.visibility = View.INVISIBLE
        ToolUtil.runInBackground(this.activity!!,
                {
                    val llData = LinkedList<INote>()
                    NoteDataHelper.getNotesBetweenDays(startDay, endDay).values
                            .forEach { llData.addAll(it!!) }

                    mCIItem.clear()
                    llData.forEach {
                        ChartItem(it.isPayNote.doJudge(PAY_ITEM, INCOME_ITEM), it.info, it.amount).apply {
                            mCIItem.find { it.mSZName == mSZName && it.mType == mType }.forObj(
                                    { it.mBDVal = it.mBDVal.add(mBDVal) },
                                    { mCIItem.add(this) }
                            )
                        }
                    }
                },
                {
                    mCVChart.circleFillRatio = 0.6f
                    mCVChart.pieChartData = generateData()

                    Handler().postDelayed({
                        mIVLeft.visibility = vLeft
                        mIVRight.visibility = vRight
                    }, 300)
                })
    }

    companion object {
        const val PAY_ITEM: Int = 1
        const val INCOME_ITEM: Int = 2
    }
}