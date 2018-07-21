package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.event.FilterShow
import wxm.KeepAccount.improve.toMoneyStr
import wxm.androidutil.improve.let1
import wxm.KeepAccount.improve.toSignalMoneyStr
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.time.getDayInWeekStr
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.ui.view.ViewDataHolder
import wxm.androidutil.ui.view.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.androidutil.improve.doJudge
import wxm.androidutil.improve.forObj
import wxm.uilib.IconButton.IconButton
import java.util.*
import kotlin.collections.HashMap

/**
 * ListView for monthly data
 * Created by WangXM on 2016/9/10.
 */
class LVMonthly : LVBase() {
    // 若为true则数据以时间降序排列
    private var mBTimeDownOrder = true
    private val mLSMonthPara: LinkedList<HashMap<String, MonthItemHolder>> = LinkedList()
    private val mHMDayPara: HashMap<String, LinkedList<DayItemHolder>> = HashMap()

    internal data class MonthDetailItem(val month: String) {
        var show: String = EShowFold.FOLD.showStatus

        var monthDetail: RecordDetail? = null
        var amount: String? = null
    }

    internal inner class MonthItemHolder(tag: String)
        : ViewDataHolder<String, MonthDetailItem>(tag) {
        override fun getDataByTag(tag: String): MonthDetailItem {
            return MonthDetailItem(tag).let { map ->
                NoteDataHelper.getInfoByMonth(tag).let1 {
                    map.monthDetail = RecordDetail(it.payCount.toString(), it.payAmount.toMoneyStr(),
                            it.incomeCount.toString(), it.incomeAmount.toMoneyStr())

                    map.amount = it.balance.toSignalMoneyStr()

                    map.show = EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
                }

                map
            }
        }
    }

    internal data class DayDetailItem(val tag: String, val subTag: String) {
        var dayNumber: String? = null
        var dayInWeek: String? = null
        var dayDetail: LVBase.RecordDetail? = null
        var amount: String? = null
    }

    internal inner class DayItemHolder(tag: String)
        : ViewDataHolder<String, DayDetailItem>(tag) {
        override fun getDataByTag(tag: String): DayDetailItem {
            val mk = tag.substring(0, 7)
            val map = DayDetailItem(mk, tag)
            NoteDataHelper.getInfoByDay(tag).let {
                map.dayNumber = tag.substring(8, 10).removePrefix("0")
                map.dayInWeek = ToolUtil.stringToCalendar(tag).getDayInWeekStr()
                map.dayDetail = RecordDetail(it.payCount.toString(), it.payAmount.toMoneyStr(),
                        it.incomeCount.toString(), it.incomeAmount.toMoneyStr())

                map.amount = it.balance.toSignalMoneyStr()
            }

            return map
        }
    }

    init {
        mBActionExpand = false
        mAHActs = object : ActionHelper() {
            private lateinit var mIBSort: IconButton
            private lateinit var mIBReport: IconButton
            private lateinit var mIBAdd: IconButton
            private lateinit var mIBDelete: IconButton

            override fun initActs(parentView: View) {
                mIBSort = parentView.findViewById(R.id.ib_sort)
                mIBReport = parentView.findViewById(R.id.ib_report)
                mIBAdd = parentView.findViewById(R.id.ib_add)
                mIBDelete = parentView.findViewById(R.id.ib_delete)

                mIBReport.visibility = View.GONE
                mIBAdd.visibility = View.GONE
                mIBDelete.visibility = View.GONE

                mIBSort.setActIcon(if (mBTimeDownOrder) R.drawable.ic_sort_up_1
                else R.drawable.ic_sort_down_1)
                mIBSort.setActName(if (mBTimeDownOrder) R.string.cn_sort_up_by_time
                else R.string.cn_sort_down_by_time)

                EventHelper.setOnClickOperator(parentView,
                        intArrayOf(R.id.ib_sort, R.id.ib_refresh),
                        this::onActClick)
            }

            private fun onActClick(v: View) {
                when (v.id) {
                    R.id.ib_sort -> {
                        mBTimeDownOrder = !mBTimeDownOrder

                        mIBSort.setActIcon(mBTimeDownOrder.doJudge(R.drawable.ic_sort_up_1, R.drawable.ic_sort_down_1))
                        mIBSort.setActName(mBTimeDownOrder.doJudge(R.string.cn_sort_up_by_time,
                                R.string.cn_sort_down_by_time))

                        reloadUI()
                    }

                    R.id.ib_refresh -> {
                        reInitUI()
                    }
                }
            }
        }
    }

    override fun isUseEventBus(): Boolean = true

    /**
     * filter view
     * @param event     param
     */
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShow) {
        if (NoteDataHelper.TAB_TITLE_YEARLY == event.sender) {
            mBFilter = true
            mBSelectSubFilter = false

            mFilterPara.clear()
            mFilterPara.addAll(event.filterTag)
            loadUI(null)
        }
    }

    /**
     * click [v] for 'accept' or 'cancel'
     */
    private fun onAcceptOrCancelClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.bt_accpet -> {
                if (mBSelectSubFilter) {
                    if (!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                        rootActivity!!.jumpByTabName(NoteDataHelper.TAB_TITLE_DAILY)
                        FilterShow(NoteDataHelper.TAB_TITLE_MONTHLY, ArrayList(mLLSubFilter))
                                .let1 { EventBus.getDefault().post(it) }

                        mLLSubFilter.clear()
                    }

                    mLLSubFilterVW.forEach {
                        it.isSelected = false
                        it.background.alpha = 0
                    }
                    mLLSubFilterVW.clear()

                    mBSelectSubFilter = false
                    refreshAttachLayout()
                }
            }

            R.id.bt_cancel -> {
                mBSelectSubFilter = false
                mLLSubFilter.clear()

                mLLSubFilterVW.forEach {
                    it.isSelected = false
                    it.background.alpha = 0
                }
                mLLSubFilterVW.clear()

                refreshAttachLayout()
            }

            R.id.bt_cancel_filter -> {
                mBFilter = false
                loadUI(null)
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_cancel, R.id.bt_cancel_filter),
                this::onAcceptOrCancelClick)

        ToolUtil.runInBackground(activity!!,
                {
                    mLSMonthPara.clear()
                    mHMDayPara.clear()

                    // for month
                    NoteDataHelper.notesMonths.forEach {
                        mLSMonthPara.add(HashMap<String, MonthItemHolder>().apply {
                            put(KEY_DATA, MonthItemHolder(it))
                        })
                    }

                    // for day
                    NoteDataHelper.notesDays.forEach {
                        val km = it.substring(0, 7)
                        mHMDayPara[km].forObj(
                                { ls ->
                                    ls.add(DayItemHolder(it))
                                    Unit
                                },
                                {
                                    mHMDayPara[km] = LinkedList<DayItemHolder>().apply {
                                        add(DayItemHolder(it))
                                    }
                                }
                        )
                    }
                },
                { loadUI(bundle) })
    }

    override fun loadUI(bundle: Bundle?) {
        refreshAttachLayout()

        mLVShow.adapter = MonthAdapter(context!!,
                mLSMonthPara.filter { !mBFilter || mFilterPara.contains(it[KEY_DATA]!!.tag) }
                        .sortedWith(Comparator { o1, o2 ->
                            mBTimeDownOrder.doJudge(
                                    o2[KEY_DATA]!!.tag.compareTo(o1[KEY_DATA]!!.tag),
                                    o1[KEY_DATA]!!.tag.compareTo(o2[KEY_DATA]!!.tag)
                            )
                        }))
    }

    /// BEGIN PRIVATE

    /**
     * update attach layout
     */
    private fun refreshAttachLayout() {
        setAttachLayoutVisible((mBFilter || mBSelectSubFilter).doJudge(View.VISIBLE, View.GONE))
        setFilterLayoutVisible((mBFilter).doJudge(View.VISIBLE, View.GONE))
        setAcceptGiveUpLayoutVisible(if (mBSelectSubFilter) View.VISIBLE else View.GONE)
    }

    /**
     * load detail view
     * @param lv        view need show detail view
     * @param tag       tag for data
     */
    private fun loadDayDetailView(lv: ListView, tag: String?) {
        mHMDayPara[tag]?.let {
            LinkedList<HashMap<String, DayItemHolder>>().apply {
                addAll(it.sortedWith(Comparator { o1, o2 ->
                    if (!mBTimeDownOrder) o1.tag.compareTo(o2.tag)
                    else o2.tag.compareTo(o1.tag)
                }).map { HashMap<String, DayItemHolder>().apply { put(KEY_DATA, it) } })
            }.let {
                lv.adapter = DayAdapter(context!!, it)
            }

            ListViewHelper.setListViewHeightBasedOnChildren(lv)
        }
    }
    /// END PRIVATE

    /**
     * month data adapter
     */
    private inner class MonthAdapter internal constructor(context: Context, data: List<Map<String, MonthItemHolder>>)
        : MoreAdapter(context, data, R.layout.li_monthly_show) {

        private fun getTypedItem(pos: Int): MonthItemHolder {
            @Suppress("UNCHECKED_CAST")
            return (getItem(pos) as Map<String, MonthItemHolder>)[KEY_DATA]!!
        }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            val item = getTypedItem(pos).data
            val lv = vhHolder.getView<ListView>(R.id.lv_show_detail)
            lv.visibility = EShowFold.getByShowStatus(item.show).isFold().doJudge(
                    { View.GONE },
                    {
                        if (0 == lv.count)
                            loadDayDetailView(lv, item.month)

                        View.VISIBLE
                    })

            View.OnClickListener { _ ->
                lv.visibility = EShowFold.getByShowStatus(item.show).isFold().doJudge(
                        {
                            if (0 == lv.count)
                                loadDayDetailView(lv, item.month)

                            addUnfoldItem(item.month)
                            item.show = EShowFold.getByFold(false).showStatus

                            View.VISIBLE
                        },
                        {
                            removeUnfoldItem(item.month)
                            item.show = EShowFold.getByFold(true).showStatus

                            View.GONE
                        }
                )
            }.let1 {
                vhHolder.getView<ConstraintLayout>(R.id.cl_header).apply {
                    setBackgroundColor((0 == pos % 2).doJudge(mCRLVLineOne, mCRLVLineTwo))
                    setOnClickListener(it)
                }
            }

            // for month
            vhHolder.setText(R.id.tv_month,
                    "${item.month.subSequence(0, 4)}年" +
                            "${item.month.subSequence(5, 7).removePrefix("0")}月")

            // for graph value
            item.monthDetail!!.let {
                vhHolder.getView<ValueShow>(R.id.vs_monthly_info).adjustAttribute(
                        HashMap<String, Any>().apply {
                            put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                            put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                            put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                            put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount)
                        })
            }
        }
    }


    /**
     * day data adapter
     */
    private inner class DayAdapter internal constructor(context: Context, data: List<Map<String, DayItemHolder>>)
        : MoreAdapter(context, data, R.layout.li_monthly_show_detail) {

        private fun getTypedItem(pos: Int): DayItemHolder {
            @Suppress("UNCHECKED_CAST")
            return (getItem(pos) as Map<String, DayItemHolder>)[KEY_DATA]!!
        }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            val item = getTypedItem(pos).data
            vhHolder.getView<ImageView>(R.id.iv_action).apply {
                setBackgroundColor(mLLSubFilter.contains(item.subTag)
                        .doJudge(mCRLVItemSel, mCRLVItemTransFull))
                setOnClickListener { v ->
                    val subTag = item.subTag
                    mLLSubFilter.contains(subTag).doJudge(
                            {
                                mLLSubFilter.remove(subTag)
                                mLLSubFilterVW.remove(v)
                                if (mLLSubFilter.isEmpty()) {
                                    mLLSubFilterVW.clear()
                                    mBSelectSubFilter = false
                                    refreshAttachLayout()
                                }

                                v.setBackgroundColor(mCRLVItemTransFull)
                            },
                            {
                                mLLSubFilter.add(subTag)
                                mLLSubFilterVW.add(v)
                                if (!mBSelectSubFilter) {
                                    mBSelectSubFilter = true
                                    refreshAttachLayout()
                                }

                                v.setBackgroundColor(mCRLVItemSel)
                            }
                    )
                }
            }

            // for show
            vhHolder.setText(R.id.tv_day_number, item.dayNumber)
            vhHolder.setText(R.id.tv_day_in_week, item.dayInWeek)

            // for graph value
            item.dayDetail?.let {
                vhHolder.getView<ValueShow>(R.id.vs_daily_info).adjustAttribute(
                        HashMap<String, Any>().apply {
                            put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                            put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                            put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                            put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount)
                        })
            }
        }

    }

    companion object {
        private const val KEY_DATA = "data"
    }
}
