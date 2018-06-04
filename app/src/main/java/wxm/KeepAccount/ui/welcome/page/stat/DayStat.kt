package wxm.KeepAccount.ui.welcome.page.stat


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotterknife.bindView
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.welcome.base.PageBase
import wxm.androidutil.ui.frg.FrgSupportBaseAdv

import com.flyco.tablayout.SegmentTabLayout
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.util.ChartUtils
import wxm.KeepAccount.ui.data.show.note.HelloChart.ChartBase
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.log.TagLog
import java.util.ArrayList

/**
 * for welcome
 * Created by WangXM on 2016/12/7.
 */
class DayStat : StatBase() {
    override fun loadUI(savedInstanceState: Bundle?) {
        TagLog.i("here")
        ToolUtil.runInBackground(this.activity!!,
                {
                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    NoteDataHelper.notesDays
                            .forEach {
                                val tag = it
                                NoteDataHelper.getInfoByDay(it)?.let {
                                    columns.add(Column(
                                            listOf(SubcolumnValue(it.payAmount.toFloat(), ChartBase.mPayColor),
                                                    SubcolumnValue(it.incomeAmount.toFloat(), ChartBase.mIncomeColor)))
                                            .setHasLabels(true))
                                    axisValues.add(AxisValue(idCol.toFloat()).apply {
                                        setLabel(if (0 == idCol % 3) tag  else   "")
                                    })

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
                        it.setLabel(if (null != it.labelAsChars && 4 < it.labelAsChars.size)
                            String(it.labelAsChars).substring(0, 4)
                        else "")
                    }
                },
                {
                    TagLog.i("finish")
                    loadUIUtility() })
    }
}
