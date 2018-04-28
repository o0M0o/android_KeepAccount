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
 * daily chart
 * Created by WangXM on2016/9/29.
 */
class DailyChart : ChartBase() {
    init {
        mPrvWidth = 4.5f
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter UI
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
        val eP = event.filterTag
        if (NoteDataHelper.TAB_TITLE_MONTHLY == event.sender) {
            mBFilter = true
            mFilterPara.clear()
            mFilterPara.addAll(eP)

            refreshData()
        }
    }

    override fun refreshData() {
        ToolUtil.runInBackground(this.activity,
                {
                    val ret = NoteDataHelper.instance.notesForDay ?: return@runInBackground

                    var idCol = 0
                    val axisValues = ArrayList<AxisValue>()
                    val columns = ArrayList<Column>()
                    ret.toSortedMap().entries.forEach {
                        if (!mBFilter || mFilterPara.contains(it.key)) {
                            var pay = BigDecimal.ZERO
                            var income = BigDecimal.ZERO
                            it.value.forEach {
                                when (it) {
                                    is PayNoteItem -> pay = pay.add(it.amount)
                                    is IncomeNoteItem -> income = income.add(it.amount)
                                }
                            }

                            val values = ArrayList<SubcolumnValue>()
                            values.add(SubcolumnValue(pay.toFloat(), mPayColor))
                            values.add(SubcolumnValue(income.toFloat(), mIncomeColor))

                            columns.add(Column(values).setHasLabels(true))

                            axisValues.add(AxisValue(idCol.toFloat()).setLabel(it.key))
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

                    var cc = 0
                    mPreviewData!!.axisXBottom.values.forEach {
                        it.setLabel(if (0 == cc % 5) String(it.labelAsChars).substring(0, 7) else "")
                        cc += 1
                    }
                },
                { loadUIUtility(true) })
    }
}
