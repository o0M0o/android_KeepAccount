package wxm.KeepAccount.ui.data.report

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.event.SelectDays
import wxm.KeepAccount.item.IncomeNoteItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.data.report.page.DayReportChart
import wxm.KeepAccount.ui.data.report.page.DayReportWebView
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.improve.let1
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.frg.FrgSupportSwitcher
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.util.UtilFun
import java.math.BigDecimal
import java.util.*

/**
 * day data report
 * Created by WangXM on 2017/2/15.
 */
class FrgReportDay : FrgSupportSwitcher<FrgSupportBaseAdv>() {
    private val mTVDay: TextView by bindView(R.id.tv_day)
    private val mTVPay: TextView by bindView(R.id.tv_pay)
    private val mTVIncome: TextView by bindView(R.id.tv_income)

    private var mASParaLoad: ArrayList<String>? = null

    private val mPGWebView = DayReportWebView()
    private val mPGChart = DayReportChart()

    init {
        setupFrgID(R.layout.frg_report, R.id.fl_page_holder)
    }

    /**
     * update date range
     * @param event     event with start & end day
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectDaysEvent(event: SelectDays) {
        mASParaLoad!![0] = event.mSZStartDay
        mASParaLoad!![1] = event.mSZEndDay

        loadUI(null)
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun setupFragment(bundle: Bundle?) {
        arguments!!.let1 {
            mPGWebView.arguments = it
            mPGChart.arguments = it

            mASParaLoad = it.getStringArrayList(ACReport.PARA_LOAD)
            addChildFrg(mPGWebView)
            addChildFrg(mPGChart)
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        if (null == savedInstanceState) {
            EventHelper.setOnClickListener(view!!,
                    intArrayOf(R.id.iv_switch, R.id.tv_select_days),
                    View.OnClickListener { v ->
                        when (v.id) {
                        // switch report type
                            R.id.iv_switch -> {
                                switchPage()
                            }

                        // reset start-end day
                            R.id.tv_select_days -> {
                                val dlgDay = DlgSelectReportDays()
                                dlgDay.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                                    override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                                        EventBus.getDefault().post(
                                                SelectDays(dlgDay.startDay!!, dlgDay.endDay!!))
                                    }

                                    override fun onDialogNegativeResult(dialogFragment: DialogFragment) {}
                                })

                                dlgDay.show(activity!!.supportFragmentManager, "select days")
                            }
                        }
                    })
        }
    }

    override fun loadUI(savedInstanceState: Bundle?) {
        val frg = this
        if (!UtilFun.ListIsNullOrEmpty(frg.mASParaLoad)) {
            if (2 != frg.mASParaLoad!!.size)
                return

            val param = arrayOfNulls<Any>(3)
            ToolUtil.runInBackground(this.activity!!,
                    {
                        val dStart = frg.mASParaLoad!![0]
                        val dEnd = frg.mASParaLoad!![1]
                        val lsNote = NoteDataHelper.getNotesBetweenDays(dStart, dEnd)

                        var mBDTotalPay = BigDecimal.ZERO
                        var mBDTotalIncome = BigDecimal.ZERO
                        lsNote.values.forEach {
                            it.forEach {
                                when (it) {
                                    is PayNoteItem -> mBDTotalPay = mBDTotalPay.add(it.amount)
                                    is IncomeNoteItem -> mBDTotalIncome = mBDTotalIncome.add(it.amount)
                                }
                            }
                        }


                        param[0] = String.format(Locale.CHINA, "%s - %s", dStart, dEnd)
                        param[1] = mBDTotalPay
                        param[2] = mBDTotalIncome
                    },
                    {
                        frg.mTVDay.text = param[0] as String
                        frg.mTVPay.text = String.format(Locale.CHINA,
                                "%.02f", (param[1] as BigDecimal).toFloat())
                        frg.mTVIncome.text = String.format(Locale.CHINA,
                                "%.02f", (param[2] as BigDecimal).toFloat())

                        super.loadUI(savedInstanceState)
                    })
        }
    }

    /// PRIVATE BEGIN
    /// PRIVATE END
}
