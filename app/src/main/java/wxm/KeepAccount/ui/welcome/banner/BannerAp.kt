package wxm.KeepAccount.ui.welcome.banner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.ColumnChartView
import lecho.lib.hellocharts.view.PieChartView
import wxm.KeepAccount.R
import wxm.KeepAccount.improve.toMoneyStr
import wxm.KeepAccount.preference.PreferencesUtil
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.utility.NoteShowInfo
import wxm.androidutil.improve.let1
import wxm.androidutil.time.getMonth
import wxm.androidutil.time.getYear
import wxm.androidutil.ui.view.ViewHelper
import wxm.uilib.lbanners.LMBanners
import wxm.uilib.lbanners.adapter.LBaseAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * for frg
 * Created by WangXM on 16/12/15.
 */
class BannerAp(private val mContext: Context, private val mVGGroup: ViewGroup?) : LBaseAdapter<BannerPara> {
    override fun getView(lBanners: LMBanners<*>, context: Context, position: Int, data: BannerPara): View? {
        return when (data.mLayoutId) {
            R.layout.banner_month -> {
                LayoutInflater.from(mContext).inflate(R.layout.banner_month, mVGGroup).apply {
                    fillMonth(this!!)
                }
            }

            R.layout.banner_year -> {
                LayoutInflater.from(mContext).inflate(R.layout.banner_year, mVGGroup).apply {
                    fillYear(this!!)
                }
            }

            else -> null
        }
    }

    /**
     * fill month data
     * @param v     UI
     */
    private fun fillMonth(v: View) {
        val vh = ViewHelper(v)
        val ci = Calendar.getInstance()

        vh.setText(R.id.tv_month_number, String.format(Locale.CHINA, "%02d", ci.getMonth()))
        vh.setText(R.id.tv_year_number, String.format(Locale.CHINA, "%04d", ci.getYear()))

        NoteDataHelper.getInfoByMonth(SimpleDateFormat("yyyy-MM", Locale.CHINA).format(ci.time))
                .let1 {
                    vh.setText(R.id.tv_pay_amount, it.payAmount.toMoneyStr())
                    vh.setText(R.id.tv_income_amount, it.incomeAmount.toMoneyStr())
                    fillPieChart(vh, it)
                }
    }

    /**
     * fill year data
     * @param v     UI
     */
    private fun fillYear(v: View) {
        val vh = ViewHelper(v)
        val ci = Calendar.getInstance()
        vh.setText(R.id.tv_year_number, String.format(Locale.CHINA, "%04d", ci.getYear()))
        NoteDataHelper.getInfoByYear(SimpleDateFormat("yyyy", Locale.CHINA).format(ci.time))
                .let1 {
                    vh.setText(R.id.tv_pay_amount, it.payAmount.toMoneyStr())
                    vh.setText(R.id.tv_income_amount, it.incomeAmount.toMoneyStr())
                    fillPieChart(vh, it)
                }
    }

    private fun fillPieChart(vh: ViewHelper, ni: NoteShowInfo) {
        val colorPay = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_PAY_COLOR]!!
        val colorIncome = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_INCOME_COLOR]!!

        vh.getChildView<PieChartView>(R.id.pc_chart)!!.let1 {
            it.circleFillRatio = 0.8f
            it.pieChartData = PieChartData().apply {
                setHasLabels(false)
                setHasLabelsOutside(false)
                setHasCenterCircle(true)
                slicesSpacing = 4

                values = ArrayList<SliceValue>().apply {
                    add(SliceValue(ni.payAmount.toFloat(), colorPay))
                    add(SliceValue(ni.incomeAmount.toFloat(), colorIncome))
                }
            }
        }
    }
}
