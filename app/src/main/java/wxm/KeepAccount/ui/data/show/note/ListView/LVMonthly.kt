package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.ui.base.Adapter.LVAdapter
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.EventHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.viewUtil.ViewDataHolder
import wxm.androidutil.viewUtil.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * ListView for monthly data
 * Created by WangXM on 2016/9/10.
 */
class LVMonthly : LVBase() {
    // 若为true则数据以时间降序排列
    private var mBTimeDownOrder = true
    private val mLSMonthPara: LinkedList<MonthItemHolder> = LinkedList()
    private val mHMDayPara: HashMap<String, LinkedList<DayItemHolder>> = HashMap()

    internal data class MonthDetailItem(val month: String) {
        var show: String = EShowFold.FOLD.showStatus

        var monthDetail: RecordDetail? = null
        var amount: String? = null
    }

    internal inner class MonthItemHolder(tag: String)
        : ViewDataHolder<String, MonthDetailItem>(tag) {
        override fun getDataByTag(tag: String): MonthDetailItem {
            val map = MonthDetailItem(tag)
            NoteDataHelper.getInfoByMonth(tag)?.let {
                map.monthDetail = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                map.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)

                map.show = EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
            }

            return map
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
            NoteDataHelper.getInfoByDay(tag)?.let {
                map.dayNumber = tag.substring(8, 10).removePrefix("0")
                map.dayInWeek = ToolUtil.getDayInWeek(tag)
                map.dayDetail = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                map.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)
            }

            return map
        }
    }

    internal inner class MonthlyActionHelper : ActionHelper() {
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

                    mIBSort.setActIcon(if (mBTimeDownOrder) R.drawable.ic_sort_up_1
                    else R.drawable.ic_sort_down_1)
                    mIBSort.setActName(if (mBTimeDownOrder) R.string.cn_sort_up_by_time
                    else R.string.cn_sort_down_by_time)

                    loadUI(null)
                }

                R.id.ib_refresh -> {
                    reloadView(v.context, false)
                }
            }
        }
    }

    init {
        mBActionExpand = false
        mAHActs = MonthlyActionHelper()
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter view
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
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
                        val ac = rootActivity
                        ac!!.jumpByTabName(NoteDataHelper.TAB_TITLE_DAILY)

                        val lsSub = ArrayList(mLLSubFilter)
                        EventBus.getDefault().post(FilterShowEvent(NoteDataHelper.TAB_TITLE_MONTHLY, lsSub))

                        mLLSubFilter.clear()
                    }

                    for (i in mLLSubFilterVW) {
                        i.isSelected = false
                        i.background.alpha = 0
                    }
                    mLLSubFilterVW.clear()

                    mBSelectSubFilter = false
                    refreshAttachLayout()
                }
            }

            R.id.bt_giveup -> {
                mBSelectSubFilter = false
                mLLSubFilter.clear()

                for (i in mLLSubFilterVW) {
                    i.isSelected = false
                    i.background.alpha = 0
                }
                mLLSubFilterVW.clear()

                refreshAttachLayout()
            }

            R.id.bt_giveup_filter -> {
                mBFilter = false
                loadUI(null)
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_giveup, R.id.bt_giveup_filter),
                this::onAcceptOrCancelClick)

        ToolUtil.runInBackground(activity,
                {
                    mLSMonthPara.clear()
                    mHMDayPara.clear()

                    // for month
                    NoteDataHelper.notesMonths.forEach {
                        mLSMonthPara.add(MonthItemHolder(it))
                    }

                    // for day
                    NoteDataHelper.notesDays.forEach {
                        val km = it.substring(0, 7)
                        var lsDay: LinkedList<DayItemHolder>? = mHMDayPara[km]
                        if (null == lsDay) {
                            lsDay = LinkedList()
                            mHMDayPara[km] = lsDay
                        }
                        lsDay.add(DayItemHolder(it))
                    }
                },
                { loadUI(bundle) })
    }

    override fun loadUI(bundle: Bundle?) {
        // adjust attach layout
        refreshAttachLayout()

        // set listview adapter
        mLVShow.adapter = MonthAdapter(context,
                mLSMonthPara.filter { !mBFilter || mFilterPara.contains(it.tag) }
                        .sortedWith(Comparator { o1, o2 ->
                            if (!mBTimeDownOrder) o1.tag.compareTo(o2.tag)
                            else o2.tag.compareTo(o1.tag)
                        }))
    }

    /// BEGIN PRIVATE

    /**
     * update attach layout
     */
    private fun refreshAttachLayout() {
        setAttachLayoutVisible(if (mBFilter || mBSelectSubFilter) View.VISIBLE else View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(if (mBSelectSubFilter) View.VISIBLE else View.GONE)
    }

    /**
     * load detail view
     * @param lv        view need show detail view
     * @param tag       tag for data
     */
    private fun loadDayDetailView(lv: ListView, tag: String?) {
        mHMDayPara[tag]?.let {
            lv.adapter = DayAdapter(context,
                    it.sortedWith(Comparator { o1, o2 ->
                        if (!mBTimeDownOrder) o1.tag.compareTo(o2.tag)
                        else o2.tag.compareTo(o1.tag)
                    }))
            ListViewHelper.setListViewHeightBasedOnChildren(lv)
        }
    }
    /// END PRIVATE

    /**
     * month data adapter
     */
    private inner class MonthAdapter internal constructor(context: Context, data: List<MonthItemHolder>)
        : LVAdapter(context, data, R.layout.li_monthly_show) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(context, convertView, R.layout.li_monthly_show)
            if (null == viewHolder.getSelfTag(SELF_TAG_ID)) {
                viewHolder.setSelfTag(SELF_TAG_ID, Any())

                val item = (getItem(position) as MonthItemHolder).data
                val lv = viewHolder.getView<ListView>(R.id.lv_show_detail)
                lv.visibility = if (EShowFold.getByShowStatus(item.show).isFold()) {
                    View.GONE
                } else {
                    if (0 == lv.count)
                        loadDayDetailView(lv, item.month)

                    View.VISIBLE
                }

                val localListener = View.OnClickListener { _ ->
                    val bf = EShowFold.getByShowStatus(item.show).isFold()
                    item.show = EShowFold.getByFold(!bf).showStatus
                    lv.visibility = if (bf) {
                        if (0 == lv.count)
                            loadDayDetailView(lv, item.month)

                        addUnfoldItem(item.month)
                        View.VISIBLE
                    } else {
                        removeUnfoldItem(item.month)
                        View.GONE
                    }
                }

                viewHolder.getView<ConstraintLayout>(R.id.cl_header).apply {
                    setBackgroundColor(if (0 == position % 2) ResourceHelper.mCRLVLineOne
                    else ResourceHelper.mCRLVLineTwo)
                    setOnClickListener(localListener)
                }

                // for month
                viewHolder.setText(R.id.tv_month,
                        "${item.month.subSequence(0, 4)}年" +
                                "${item.month.subSequence(5, 7).removePrefix("0")}月")

                // for graph value
                item.monthDetail?.let {
                    viewHolder.getView<ValueShow>(R.id.vs_monthly_info).adjustAttribute(
                            HashMap<String, Any>().apply {
                                put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                                put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                                put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                                put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount)
                            })
                }
            }

            return viewHolder.convertView
        }
    }


    /**
     * day data adapter
     */
    private inner class DayAdapter internal constructor(context: Context, data: List<DayItemHolder>)
        : LVAdapter(context, data, R.layout.li_monthly_show_detail) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(rootActivity,
                    convertView, R.layout.li_monthly_show_detail)
            if (null == viewHolder.getSelfTag(SELF_TAG_ID)) {
                viewHolder.setSelfTag(SELF_TAG_ID, Any())

                val item = (getItem(position) as DayItemHolder).data
                viewHolder.getView<ImageView>(R.id.iv_action).apply {
                    setBackgroundColor(if (mLLSubFilter.contains(item.subTag)) ResourceHelper.mCRLVItemSel
                    else ResourceHelper.mCRLVItemTransFull)
                    setOnClickListener { v ->
                        val subTag = item.subTag
                        v.setBackgroundColor(if (!mLLSubFilter.contains(subTag)) {
                            mLLSubFilter.add(subTag)
                            mLLSubFilterVW.add(v)
                            if (!mBSelectSubFilter) {
                                mBSelectSubFilter = true
                                refreshAttachLayout()
                            }

                            ResourceHelper.mCRLVItemSel
                        } else {
                            mLLSubFilter.remove(subTag)
                            mLLSubFilterVW.remove(v)
                            if (mLLSubFilter.isEmpty()) {
                                mLLSubFilterVW.clear()
                                mBSelectSubFilter = false
                                refreshAttachLayout()
                            }

                            ResourceHelper.mCRLVItemTransFull
                        })
                    }
                }

                // for show
                viewHolder.setText(R.id.tv_day_number, item.dayNumber)
                viewHolder.setText(R.id.tv_day_in_week, item.dayInWeek)

                // for graph value
                item.dayDetail?.let {
                    viewHolder.getView<ValueShow>(R.id.vs_daily_info).adjustAttribute(
                            HashMap<String, Any>().apply {
                                put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                                put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                                put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                                put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount)
                            })
                }
            }

            return viewHolder.convertView
        }
    }

    companion object {
        private const val SELF_TAG_ID = 0
    }
}
