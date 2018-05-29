package wxm.KeepAccount.ui.data.report.page

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ToggleButton
import kotterknife.bindView
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.data.report.ACReport
import wxm.KeepAccount.ui.data.report.base.EventSelectDays
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.view.EventHelper
import java.math.BigDecimal
import java.util.*

/**
 * daily report(webview)
 * Created by WangXM on 2017/3/4.
 */
class DayReportChart : FrgSupportBaseAdv() {
    private val mCVChart: PieChartView by bindView(R.id.chart)
    private val mPBLoadData: ProgressBar by bindView(R.id.pb_load_data)
    private val mTBIncome: ToggleButton by bindView(R.id.tb_income)
    private val mTBPay: ToggleButton by bindView(R.id.tb_pay)

    private var mASParaLoad: ArrayList<String>? = null
    private var mLLOrgData: LinkedList<INote> = LinkedList()

    /**
     * update day range
     * @param event for day range
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectDaysEvent(event: EventSelectDays) {
        mASParaLoad!![0] = event.mSZStartDay
        mASParaLoad!![1] = event.mSZEndDay
        loadData()
    }

    override fun getLayoutID(): Int {
        return R.layout.page_report_chart
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun initUI(bundle: Bundle?) {
        mASParaLoad = arguments!!.getStringArrayList(ACReport.PARA_LOAD)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.tb_income, R.id.tb_pay),
                { v ->
                    val vid = v.id
                    when (vid) {
                        R.id.tb_income -> {
                            mTBPay.isClickable = mTBIncome.isChecked
                        }

                        R.id.tb_pay -> {
                            mTBIncome.isClickable = mTBPay.isChecked
                        }
                    }

                    // update show
                    val cvData = PieChartData()
                    showProgress(true)
                    ToolUtil.runInBackground(this.activity!!,
                            { generateData(cvData) },
                            {
                                showProgress(false)

                                mCVChart.circleFillRatio = 0.6f
                                mCVChart.pieChartData = cvData
                            })
                })

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        mCVChart.onValueTouchListener = object : PieChartOnValueSelectListener {
            override fun onValueSelected(i: Int, sliceValue: SliceValue) {
                val sz = String.format(Locale.CHINA,
                        "%s : %.02f",
                        String(sliceValue.labelAsChars), sliceValue.value)

                Toast.makeText(activity, sz, Toast.LENGTH_SHORT).show()
            }

            override fun onValueDeselected() {}
        }

        loadData()
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources
                .getInteger(android.R.integer.config_shortAnimTime)

        mPBLoadData.visibility = if (show) View.VISIBLE else View.GONE
        mPBLoadData.animate().setDuration(shortAnimTime.toLong())
                .alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mPBLoadData.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    private fun loadData() {
        mLLOrgData.clear()
        mASParaLoad?.let {
            val cvData = PieChartData()
            showProgress(true)
            ToolUtil.runInBackground(this.activity!!,
                    {
                        val hmNote = NoteDataHelper.getNotesBetweenDays(it[0], it[1])
                        hmNote.values.iterator().forEach { mLLOrgData.addAll(it!!) }
                        generateData(cvData)
                    },
                    {
                        showProgress(false)

                        mCVChart.circleFillRatio = 0.6f
                        mCVChart.pieChartData = cvData
                    })
        }
    }

    private fun generateData(pd: PieChartData) {
        class ChartItem {
            var mType: Int = 0
            var mSZName: String? = null
            var mBDVal: BigDecimal? = null

            /**
             * update list item
             * @param lsData    list item
             */
            fun updateList(lsData: MutableList<ChartItem>) {
                var bf = false
                for (ci in lsData) {
                    if (ci.mSZName == mSZName && ci.mType == mType) {
                        bf = true
                        ci.mBDVal = ci.mBDVal!!.add(mBDVal)
                        break
                    }
                }

                if (!bf) {
                    lsData.add(this)
                }
            }
        }

        // create chart item list
        val bPay = mTBPay.isChecked
        val bIncome = mTBIncome.isChecked
        val lsCI = LinkedList<ChartItem>()
        for (data in mLLOrgData) {
            if (data.isPayNote && !bPay)
                continue

            if (data.isIncomeNote && !bIncome)
                continue

            val ci = ChartItem()
            ci.mBDVal = data.amount
            ci.mType = if (data.isPayNote) PAY_ITEM else INCOME_ITEM
            ci.mSZName = data.info

            ci.updateList(lsCI)
        }

        // create values
        val values = ArrayList<SliceValue>()
        for (ci in lsCI) {

            val sliceValue = SliceValue(ci.mBDVal!!.toFloat(), ChartUtils.pickColor())
            //sliceValue.setLabel((ci.mType == ChartItem.PAY_ITEM ? "p " : "i ")
            //                        + ci.mSZName);
            sliceValue.setLabel(ci.mSZName!!)
            values.add(sliceValue)
        }

        pd.values = values
        pd.setHasLabels(true)
        pd.setHasLabelsOutside(true)
        pd.setHasCenterCircle(true)
        pd.slicesSpacing = 12

        // hasCenterText1
        pd.centerText1 = if (bPay && bIncome) "收支" else if (bPay) "支出" else "收入"

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        pd.centerText1FontSize = ChartUtils.px2sp(
                resources.displayMetrics.scaledDensity, resources.getDimension(R.dimen.pie_chart_text_size).toInt())
    }

    companion object {
        const val PAY_ITEM: Int = 1
        const val INCOME_ITEM: Int = 2
    }
}
