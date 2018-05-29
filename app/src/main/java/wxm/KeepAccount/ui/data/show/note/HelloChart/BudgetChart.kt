package wxm.KeepAccount.ui.data.show.note.HelloChart

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.math.BigDecimal
import java.util.ArrayList
import java.util.HashMap

import kotterknife.bindView
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.util.UtilFun
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import wxm.KeepAccount.R
import wxm.KeepAccount.item.BudgetItem
import wxm.KeepAccount.item.PayNoteItem
import wxm.KeepAccount.ui.base.TouchUI.TouchColumnChartView
import wxm.KeepAccount.ui.base.TouchUI.TouchPreviewColumnChartView
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase
import wxm.KeepAccount.utility.AppUtil
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.KeepAccount.utility.let1
import wxm.androidutil.ui.view.EventHelper

/**
 * 预算chart辅助类
 * Created by WangXM on 2016/10/7.
 */
class BudgetChart : ShowViewBase() {
    private val mChart: TouchColumnChartView by bindView(R.id.chart)
    private val mPreviewChart: TouchPreviewColumnChartView  by bindView(R.id.chart_preview)
    private val mSPBudget: Spinner by bindView(R.id.sp_budget)
    private val mIVRemainder: ImageView by bindView(R.id.iv_remainder)
    private val mIVUsed: ImageView by bindView(R.id.iv_used)

    private var mChartData: ColumnChartData? = null
    private var mPreviewData: ColumnChartData? = null
    private var mPrvWidth = 12f

    private var mSPBudgetData: List<BudgetItem>? = null
    private var mSPBudgetHot = Spinner.INVALID_POSITION

    private val mColorRemainder = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_BUDGET_BALANCE_COLOR]!!
    private val mColorUsed  = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_BUDGET_UESED_COLOR]!!

    override fun getLayoutID(): Int {
        return R.layout.chart_budget_pager
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
    }

    override fun initUI(bundle: Bundle?) {
        if(null == bundle) {
            mBFilter = false

            // 填充预算数据
            mSPBudgetData = AppUtil.budgetUtility.budgetForCurUsr
            if (!UtilFun.ListIsNullOrEmpty(mSPBudgetData)) {
                val lsData = ArrayList<String>().apply {
                    addAll(mSPBudgetData!!.map { it.name })
                }

                ArrayAdapter(activity, android.R.layout.simple_spinner_item,
                        lsData).let1 {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    mSPBudget.adapter = it
                }

                mSPBudget.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        mSPBudgetHot = position
                        refreshData()
                        loadUI(null)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        mSPBudgetHot = Spinner.INVALID_POSITION
                        refreshData()
                        loadUI(null)
                    }
                }
            }

            // 主chart需要响应触摸滚动事件
            mChart.setOnTouchListener(object : View.OnTouchListener {
                private var mDownX = -1f

                private lateinit var mVPMax: Viewport
                private lateinit var mVPOrg: Viewport

                private var mChartWidth: Int = 0

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val act = event.action
                    when (act) {
                        MotionEvent.ACTION_DOWN -> {
                            mDownX = event.x
                            mVPOrg = Viewport(mPreviewChart.currentViewport)
                            mVPMax = mPreviewChart.maximumViewport

                            mChartWidth = mChart.width
                        }

                        MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                            if (act == MotionEvent.ACTION_UP) {
                                v.performClick()
                            }

                            val dif = (event.x - mDownX) / mChartWidth
                            if (0.05 < Math.abs(dif) || act == MotionEvent.ACTION_UP) {
                                Viewport(mVPOrg).let1 { cp ->
                                    if (cp.left > mVPMax.left || cp.right < mVPMax.right) {
                                        val w = cp.width()
                                        val m = -(dif * w)
                                        cp.offset(m, 0f)
                                        if (cp.left < mVPMax.left) {
                                            cp.left = mVPMax.left
                                            cp.right = mVPMax.left + w
                                        }

                                        if (cp.right > mVPMax.right) {
                                            cp.right = mVPMax.right
                                            cp.left = cp.right - w
                                        }

                                        mPreviewChart.currentViewport = cp
                                    }
                                }
                            }
                        }
                    }

                    return false
                }
            })

            // 预览chart需要锁定触摸滚屏
            mPreviewChart.setOnTouchListener { v, event ->
                rootActivity?.let {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> it.disableViewPageTouch(true)

                        MotionEvent.ACTION_UP -> {
                            v.performClick()
                            it.disableViewPageTouch(false)
                        }

                        else -> {
                            if (MotionEvent.ACTION_MOVE != event.action) {
                                it.disableViewPageTouch(false)
                            }
                        }
                    }
                }

                false
            }

            EventHelper.setOnClickOperator(view!!,
                    intArrayOf(R.id.bt_less_viewport, R.id.bt_more_viewport),
                    this::onLessOrMoreView)
        }
    }


    override fun loadUI(bundle: Bundle?) {
        loadUIUtility(false)
    }

    private fun loadUIUtility(b_full: Boolean) {
        refreshAttachLayout()

        if (b_full) {
            // 展示条
            mIVRemainder.setBackgroundColor(mColorRemainder)
            mIVUsed.setBackgroundColor(mColorUsed)

            /* for chart */
            mChart.columnChartData = mChartData
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            mChart.isZoomEnabled = false
            mChart.isScrollEnabled = false

            mPreviewChart.columnChartData = mPreviewData
            mPreviewChart.setViewportChangeListener(ViewportListener())
            mPreviewChart.zoomType = ZoomType.HORIZONTAL
            refreshViewPort()
        }
    }


    /**
     * 设置扩大/缩小viewport
     *
     * @param v 激活view
     */
    fun onLessOrMoreView(v: View) {
        val btLess = view!!.findViewById(R.id.bt_less_viewport) as Button
        when (v.id) {
            R.id.bt_less_viewport -> {
                mPrvWidth += 0.2f
                refreshViewPort()

                if (!btLess.isClickable && 1 < mPrvWidth)
                    btLess.isClickable = true
            }

            R.id.bt_more_viewport -> {
                if (1 < mPrvWidth) {
                    mPrvWidth -= 0.2f
                    refreshViewPort()
                } else {
                    btLess.isClickable = false
                }
            }
        }
    }

    private fun refreshData() {
        if (Spinner.INVALID_POSITION == mSPBudgetHot)
            return

        val bi = mSPBudgetData!![mSPBudgetHot]
        ToolUtil.runInBackground(this.activity!!,
                {
                    val hmRet = HashMap<String, ArrayList<PayNoteItem>>()
                    AppUtil.payIncomeUtility.getPayNoteByBudget(bi).forEach    {
                        val k = it.ts.toString().substring(0, 10)
                        val lsp = hmRet[k]
                        if (UtilFun.ListIsNullOrEmpty(lsp)) {
                            hmRet[k] = arrayListOf(it)
                        } else {
                            lsp!!.add(it)
                        }
                    }

                    if (0 == hmRet.size) {
                        hmRet[bi.ts.toString().substring(0, 10)] = ArrayList()
                    }

                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    var allPay = BigDecimal.ZERO
                    hmRet.toSortedMap().entries.forEach  {
                        var pay = BigDecimal.ZERO
                        it.value.forEach { pay = pay.add(it.amount) }

                        allPay = allPay.add(pay)
                        val leftBudget = bi.amount.subtract(allPay)

                        ArrayList<SubcolumnValue>().apply {
                            add(SubcolumnValue(leftBudget.toFloat(), mColorRemainder))
                            add(SubcolumnValue(allPay.toFloat(), mColorUsed))
                        }.let1 {
                            Column(it).apply { setHasLabels(true) }.let1 {
                                columns.add(it)
                            }
                        }

                        axisValues.add(AxisValue(idCol.toFloat()).setLabel(it.key))
                        idCol++
                    }

                    mChartData = ColumnChartData(columns)
                    mChartData!!.axisXBottom = Axis(axisValues)
                    mChartData!!.axisYLeft = Axis().setHasLines(true)

                    // prepare preview data, is better to use separate deep copy for preview chart.
                    // set color to grey to make preview area more visible.
                    mPreviewData = ColumnChartData(mChartData)
                    mPreviewData!!.columns.forEach    {
                        it.setHasLabels(false)
                        it.values.forEach { it.color = ChartUtils.DEFAULT_DARKEN_COLOR }
                    }

                    var cc = 0
                    mPreviewData!!.axisXBottom.values.forEach {
                        it.setLabel(if (0 == cc % 5) String(it.labelAsChars).substring(0, 7) else "")
                        cc += 1
                    }
                },
                { loadUIUtility(true) })
    }

    private fun refreshAttachLayout() {
        setAttachLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(View.GONE)
    }

    private fun refreshViewPort() {
        Viewport(mChart.maximumViewport).let1 {
            it.right = it.left + mPrvWidth
            mPreviewChart.setCurrentViewportWithAnimation(it)
        }
    }

    /**
     * Viewport listener for preview chart(lower one). in [.onViewportChanged] method change
     * viewport of upper chart.
     */
    private inner class ViewportListener : ViewportChangeListener {

        override fun onViewportChanged(newViewport: Viewport) {
            // don't use animation, it is unnecessary when using preview chart because usually viewport changes
            // happens to often.
            mChart.currentViewport = newViewport
        }
    }

}
