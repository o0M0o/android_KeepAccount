package wxm.KeepAccount.ui.welcome.page.stat

import android.os.Bundle

import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.KeepAccount.utility.let1
import java.util.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class MonthStat : StatBase() {
    override fun loadUI(savedInstanceState: Bundle?) {
        ToolUtil.runInBackground(this.activity!!,
                {
                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    NoteDataHelper.notesMonths
                            .forEach {tag ->
                                NoteDataHelper.getInfoByMonth(tag).let1 {
                                    columns.add(Column(
                                            listOf(SubcolumnValue(it.payAmount.toFloat(), mPayColor),
                                                    SubcolumnValue(it.incomeAmount.toFloat(), mIncomeColor)))
                                            .setHasLabels(true))
                                    axisValues.add(AxisValue(idCol.toFloat()).setLabel(tag))
                                    idCol++
                                }
                            }

                    mChartData.columns = columns
                    mChartData.axisXBottom = Axis(axisValues)
                    mChartData.axisYLeft = Axis().setHasLines(true)

                    // prepare preview data, is better to use separate deep copy for preview chart.
                    // set color to grey to make preview area more visible.
                    mPreviewData = ColumnChartData(mChartData)
                    mPreviewData.columns.forEach {
                        it.values.forEach { it.color = ChartUtils.DEFAULT_DARKEN_COLOR }
                        it.setHasLabels(false)
                    }

                    mPreviewData.axisXBottom.values.forEach {
                        it.setLabel(String(it.labelAsChars).substring(0, 4))
                    }
                },
                {
                    loadUIUtility() })
    }
}
