package wxm.KeepAccount.ui.data.show.note.HelloChart

import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.define.IncomeNoteItem
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import java.math.BigDecimal
import java.util.*

/**
 * 加载年度chart视图
 * Created by WangXM on2016/9/29.
 */
class YearlyChart : ChartBase() {
    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter UI
     * @param event     for filter
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
    }

    override fun refreshData() {
        ToolUtil.runInBackground(this.activity,
                {
                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    NoteDataHelper.instance.notesForYear.toSortedMap().entries.forEach {
                        var pay = BigDecimal.ZERO
                        var income = BigDecimal.ZERO
                        it.value.forEach {
                            when (it) {
                                is PayNoteItem -> pay = pay.add(it.amount)
                                is IncomeNoteItem -> income = income.add(it.amount)
                            }
                        }

                        columns.add(Column(
                                listOf(SubcolumnValue(pay.toFloat(), mPayColor),
                                        SubcolumnValue(income.toFloat(), mIncomeColor)))
                                .setHasLabels(true))
                        axisValues.add(AxisValue(idCol.toFloat()).setLabel(it.key))
                        idCol++
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
