package wxm.KeepAccount.ui.data.show.note.HelloChart


import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import java.util.*

/**
 * month chart
 * Created by WangXM on2016/9/29.
 */
class MonthlyChart : ChartBase() {
    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter UI
     * @param event     for filter
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
        val e_p = event.filterTag
        if (NoteDataHelper.TAB_TITLE_YEARLY == event.sender) {
            mBFilter = true
            mFilterPara.clear()
            mFilterPara.addAll(e_p)

            refreshData()
        }
    }

    override fun refreshData() {
        ToolUtil.runInBackground(this.activity!!,
                {
                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    NoteDataHelper.notesMonths
                            .filter { !mBFilter || mFilterPara.contains(it) }
                            .forEach {
                                val tag = it
                                NoteDataHelper.getInfoByMonth(it)?.let {
                                    columns.add(Column(
                                            listOf(SubcolumnValue(it.payAmount.toFloat(), mPayColor),
                                                    SubcolumnValue(it.incomeAmount.toFloat(), mIncomeColor)))
                                            .setHasLabels(true))
                                    axisValues.add(AxisValue(idCol.toFloat()).setLabel(tag))
                                    idCol++
                                }
                            }

                    mChartData = ColumnChartData(columns)
                    mChartData!!.axisXBottom = Axis(axisValues)
                    mChartData!!.axisYLeft = Axis().setHasLines(true)

                    // prepare preview data, is better to use separate deep copy for preview chart.
                    // set color to grey to make preview area more visible.
                    mPreviewData = ColumnChartData(mChartData!!)
                    mPreviewData!!.columns.forEach {
                        it.values.forEach { it.color = ChartUtils.DEFAULT_DARKEN_COLOR }
                        it.setHasLabels(false)
                    }

                    mPreviewData!!.axisXBottom.values.forEach {
                        it.setLabel(String(it.labelAsChars).substring(0, 4))
                    }
                },
                { loadUIUtility(true) })
    }
}
