package wxm.KeepAccount.ui.data.show.note.HelloChart

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import kotterknife.bindView
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.Viewport
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.base.TouchUI.TouchColumnChartView
import wxm.KeepAccount.ui.base.TouchUI.TouchPreviewColumnChartView
import wxm.KeepAccount.ui.data.show.note.base.ShowViewBase
import wxm.KeepAccount.utility.PreferencesUtil
import wxm.androidutil.ui.view.EventHelper

/**
 * chart view base helper
 * Created by WangXM on2016/9/29.
 */
abstract class ChartBase : ShowViewBase() {
    val mChart: TouchColumnChartView by bindView(R.id.chart)
    private val mPreviewChart: TouchPreviewColumnChartView by bindView(R.id.chart_preview)
    private val mIVIncome: ImageView by bindView(R.id.iv_income)
    private val mIVPay: ImageView by bindView(R.id.iv_pay)
    private val mBTLessViewPort: Button by bindView(R.id.bt_less_viewport)
    private val mBTMoreViewPort: Button by bindView(R.id.bt_more_viewport)

    var mChartData: ColumnChartData? = null
    var mPreviewData: ColumnChartData? = null

    private var mPrvVPWidth = 5f
    private var mPrvVPOneDataWidth = 1f
    private var mPrvVPMaxWidth = 1f

    companion object {
        val mPayColor: Int = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_PAY_COLOR]!!
        val mIncomeColor: Int = PreferencesUtil.loadChartColor()[PreferencesUtil.SET_INCOME_COLOR]!!
    }

    override fun getLayoutID(): Int {
        return R.layout.pg_note_chart
    }

    override fun initUI(bundle: Bundle?) {
        mBFilter = false

        if (null == bundle) {
            mIVIncome.setBackgroundColor(mIncomeColor)
            mIVPay.setBackgroundColor(mPayColor)

            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            mChart.isZoomEnabled = false
            mChart.isScrollEnabled = false

            mPreviewChart.setViewportChangeListener(ViewportListener())
            mPreviewChart.zoomType = ZoomType.HORIZONTAL

            // main chart need respond touch event
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
                                val cp = Viewport(mVPOrg)
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

                    return false
                }
            })

            // 预览chart需要锁定触摸滚屏
            mPreviewChart.setOnTouchListener { _, event ->
                rootActivity?.let {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> it.disableViewPageTouch(true)
                        MotionEvent.ACTION_UP -> it.disableViewPageTouch(false)
                        else -> if (MotionEvent.ACTION_MOVE != event.action)
                            it.disableViewPageTouch(false)
                    }
                }

                false
            }

            EventHelper.setOnClickOperator(view!!,
                    intArrayOf(R.id.bt_less_viewport, R.id.bt_more_viewport, R.id.bt_cancel_filter),
                    this::onLessOrMoreView)
        }

        refreshData()
    }

    override fun loadUI(bundle: Bundle?) {
        loadUIUtility(false)
    }


    protected fun loadUIUtility(b_full: Boolean) {
        refreshAttachLayout()

        if (b_full) {
            /* for chart */
            mChartData?.let {
                mChart.columnChartData = it
                mPreviewChart.columnChartData = mPreviewData

                val colCount = it.columns.count()
                if(0 < colCount) {
                    mPrvVPMaxWidth= mChart.maximumViewport.width()
                    mPrvVPOneDataWidth = mPrvVPMaxWidth / colCount
                    mPrvVPWidth = if (colCount >= 5) {
                        5 * mPrvVPOneDataWidth
                    } else {
                        mPrvVPMaxWidth
                    }
                }
            }

            refreshViewPort()
        }
    }


    /**
     * adjust viewport
     *
     * @param v clicked view
     */
    fun onLessOrMoreView(v: View) {
        when (v.id) {
            R.id.bt_more_viewport -> {
                mPrvVPWidth = mPreviewChart.currentViewport.width()
                mPrvVPWidth = Math.min(mPrvVPWidth + mPrvVPOneDataWidth / 2, mPrvVPMaxWidth)
            }

            R.id.bt_less_viewport -> {
                mPrvVPWidth = mPreviewChart.currentViewport.width()
                mPrvVPWidth = Math.max(mPrvVPWidth - mPrvVPOneDataWidth / 2, mPrvVPOneDataWidth)
            }

            R.id.bt_cancel_filter -> {
                mBFilter = false
                refreshData()
            }
        }

        if(v.id in setOf(R.id.bt_less_viewport, R.id.bt_more_viewport))  {
            refreshViewPort()
            mBTMoreViewPort.isClickable = mPrvVPMaxWidth > mPrvVPWidth
            mBTLessViewPort.isClickable = mPrvVPOneDataWidth < mPrvVPWidth
        }
    }

    protected abstract fun refreshData()


    private fun refreshAttachLayout() {
        setAttachLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(View.GONE)
    }


    private fun refreshViewPort() {
        val vpTmp = Viewport(mChart.currentViewport)
        val vpMax = Viewport(mChart.maximumViewport)
        val newRight = vpTmp.left + mPrvVPWidth
        if (newRight <= vpMax.right) {
            vpTmp.right = newRight
        } else {
            if(vpMax.width() > mPrvVPWidth) {
                vpTmp.right = vpMax.right
                vpTmp.left = vpMax.right - mPrvVPWidth
            } else  {
                vpTmp.right = vpMax.right
                vpTmp.left = vpMax.left
            }
        }
        mPreviewChart.setCurrentViewportWithAnimation(vpTmp)
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
