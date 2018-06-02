package wxm.KeepAccount.ui.welcome.banner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.allure.lbanners.LMBanners
import com.allure.lbanners.adapter.LBaseAdapter
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.ColumnChartView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.ui.utility.NoteShowInfo
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.KeepAccount.utility.let1
import wxm.KeepAccount.utility.toMoneyString
import wxm.androidutil.time.getMonth
import wxm.androidutil.time.getYear
import wxm.androidutil.ui.view.ViewHelper
import java.text.SimpleDateFormat
import java.util.*

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
                    vh.setText(R.id.tv_pay_amount, it.payAmount.toMoneyString())
                    vh.setText(R.id.tv_income_amount, it.incomeAmount.toMoneyString())
                    fillChart(v, it)
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
                    vh.setText(R.id.tv_pay_amount, it.payAmount.toMoneyString())
                    vh.setText(R.id.tv_income_amount, it.incomeAmount.toMoneyString())
                    fillChart(v, it)
                }
    }

    /**
     * draw chart in banner
     * @param v         chart holder
     * @param ni        info to draw
     */
    private fun fillChart(v: View, ni: NoteShowInfo) {
        val vh = ViewHelper(v)

        // draw bar
        vh.getChildView<ImageView>(R.id.iv_income)!!.setBackgroundColor(COLOR_INCOME)
        vh.getChildView<ImageView>(R.id.iv_pay)!!.setBackgroundColor(COLOR_PAY)

        // draw chart
        val columns = ArrayList<Column>()
        ArrayList<SubcolumnValue>().apply {
            add(SubcolumnValue(ni.payAmount.toFloat(), COLOR_PAY))
            add(SubcolumnValue(ni.incomeAmount.toFloat(), COLOR_INCOME))

            columns.add(Column(this).apply { setHasLabels(false) })
        }

        val mChartData = ColumnChartData(columns).apply {
            axisXBottom = Axis(ArrayList<AxisValue>().apply {
                add(AxisValue(0f).setLabel(""))
            })
            axisYLeft = Axis().setHasLines(true)
        }

        vh.getChildView<ColumnChartView>(R.id.chart)!!.apply {
            columnChartData = mChartData
        }
    }

    companion object {
        private val COLOR_PAY = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_PAY_COLOR]!!
        private val COLOR_INCOME = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_INCOME_COLOR]!!
    }
}
