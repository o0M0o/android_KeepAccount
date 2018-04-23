package wxm.KeepAccount.ui.welcome.banner

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.allure.lbanners.LMBanners
import com.allure.lbanners.adapter.LBaseAdapter

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

import wxm.androidutil.util.UtilFun
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.view.ColumnChartView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.utility.NoteShowInfo
import wxm.KeepAccount.utility.PreferencesUtil

/**
 * for frg
 * Created by wangxm on 16/12/15.
 */
class FrgAdapter(private val mContext: Context, private val mVGGroup: ViewGroup?) : LBaseAdapter<FrgPara> {

    init {
        initView()
    }

    override fun getView(lBanners: LMBanners<*>, context: Context, position: Int, data: FrgPara): View? {
        var v: View? = null
        when (data.mFPViewId) {
            R.layout.banner_month -> {
                v = LayoutInflater.from(mContext).inflate(R.layout.banner_month, mVGGroup)
                fillMonth(v!!)
            }

            R.layout.banner_year -> {
                v = LayoutInflater.from(mContext).inflate(R.layout.banner_year, mVGGroup)
                fillYear(v!!)
            }
        }

        return v
    }

    private fun initView() {
        Log.d(LOG_TAG, "initView")

        var v = LayoutInflater.from(mContext).inflate(R.layout.banner_month, mVGGroup)
        fillMonth(v)

        v = LayoutInflater.from(mContext).inflate(R.layout.banner_year, mVGGroup)
        fillYear(v)
    }

    /**
     * fill month data
     * @param v     UI
     */
    private fun fillMonth(v: View) {
        val tv_pay = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_pay_amount))
        val tv_income = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_income_amount))

        val tv_month = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_month_number))
        val tv_year = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_year_number))

        val ci = Calendar.getInstance()
        tv_year.text = String.format(Locale.CHINA, "%04d", ci.get(Calendar.YEAR))
        tv_month.text = String.format(Locale.CHINA, "%02d", ci.get(Calendar.MONTH) + 1)

        val sd = SimpleDateFormat("yyyy-MM", Locale.CHINA)
        val cur_month = sd.format(ci.time)
        var ni: NoteShowInfo? = NoteDataHelper.getInfoByMonth(cur_month)
        if (null == ni) {
            ni = NoteShowInfo()
            ni.payAmount = BigDecimal.ZERO
            ni.incomeAmount = BigDecimal.ZERO
        }

        tv_pay.text = String.format(Locale.CHINA, "%.02f", ni.payAmount.toFloat())
        tv_income.text = String.format(Locale.CHINA, "%.02f", ni.incomeAmount.toFloat())
        fillChart(v, ni)
    }

    /**
     * fill year data
     * @param v     UI
     */
    private fun fillYear(v: View) {
        val tv_pay = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_pay_amount))
        val tv_income = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_income_amount))

        val tv_year = UtilFun.cast_t<TextView>(v.findViewById(R.id.tv_year_number))

        val ci = Calendar.getInstance()
        tv_year.text = String.format(Locale.CHINA, "%04d", ci.get(Calendar.YEAR))

        val sd = SimpleDateFormat("yyyy", Locale.CHINA)
        val cur_year = sd.format(ci.time)
        var ni: NoteShowInfo? = NoteDataHelper.getInfoByYear(cur_year)
        if (null == ni) {
            ni = NoteShowInfo()
            ni.payAmount = BigDecimal.ZERO
            ni.incomeAmount = BigDecimal.ZERO
        }

        tv_pay.text = String.format(Locale.CHINA, "%.02f", ni.payAmount.toFloat())
        tv_income.text = String.format(Locale.CHINA, "%.02f", ni.incomeAmount.toFloat())
        fillChart(v, ni)
    }

    /**
     * draw chart in banner
     * @param v         chart holder
     * @param ni        info to draw
     */
    private fun fillChart(v: View, ni: NoteShowInfo) {
        // draw bar
        val mHMColor = PreferencesUtil.loadChartColor()
        val cl_pay = mHMColor[PreferencesUtil.SET_PAY_COLOR]
        val cl_income = mHMColor[PreferencesUtil.SET_INCOME_COLOR]

        var iv = UtilFun.cast_t<ImageView>(v.findViewById(R.id.iv_income))
        iv.setBackgroundColor(cl_income!!)

        iv = UtilFun.cast_t(v.findViewById(R.id.iv_pay))
        iv.setBackgroundColor(cl_pay!!)

        // draw chart
        val axisValues = ArrayList<AxisValue>()
        val columns = ArrayList<Column>()
        val values = ArrayList<SubcolumnValue>()
        values.add(SubcolumnValue(ni.payAmount.toFloat(), cl_pay))
        values.add(SubcolumnValue(ni.incomeAmount.toFloat(), cl_income))

        val cd = Column(values)
        cd.setHasLabels(false)
        columns.add(cd)

        axisValues.add(AxisValue(0f).setLabel(""))

        val mChartData = ColumnChartData(columns)
        mChartData.axisXBottom = Axis(axisValues)
        mChartData.axisYLeft = Axis().setHasLines(true)

        val mChart = UtilFun.cast_t<ColumnChartView>(v.findViewById(R.id.chart))
        mChart.columnChartData = mChartData
    }

    companion object {
        private val LOG_TAG = ::FrgAdapter.javaClass.simpleName
    }
}
