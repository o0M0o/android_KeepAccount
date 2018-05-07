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
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.EventHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ViewHolder.ViewDataHolder
import wxm.androidutil.ViewHolder.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * 年数据视图辅助类
 * Created by WangXM on 2016/9/10.
 */
class LVYearly : LVBase() {
    // 若为true则数据以时间降序排列
    private var mBTimeDownOrder = true

    private val mYearPara: LinkedList<YearItemHolder> = LinkedList()
    private val mHMMonthPara: HashMap<String, LinkedList<MonthItemHolder>> = HashMap()

    // for main listview
    internal data class YearData(val year: String) {
        var show: EShowFold = EShowFold.FOLD

        var yearDetail: RecordDetail? = null
        var amount: String? = null
    }

    internal inner class YearItemHolder(tag: String)
        : ViewDataHolder<String, YearData>(tag) {
        override fun getDataByTag(tag: String): YearData {
            val map = YearData(tag)
            NoteDataHelper.getInfoByYear(tag)?.let {
                map.yearDetail = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                map.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)
            }

            return map
        }
    }

    // for sub listview
    internal data class MonthData(val tag: String, val subTag: String) {
        var month: String? = null
        var monthDetail: RecordDetail? = null
        var amount: String? = null
    }

    internal inner class MonthItemHolder(tag: String)
        : ViewDataHolder<String, MonthData>(tag) {
        override fun getDataByTag(tag: String): MonthData {
            val map = MonthData(tag.substring(0, 4), tag)
            map.month = tag.substring(5, 7).removePrefix("0")

            NoteDataHelper.getInfoByMonth(tag)?.let {
                map.monthDetail = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                map.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)
            }

            return map
        }
    }

    internal inner class YearlyActionHelper : ActionHelper() {
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
        mAHActs = YearlyActionHelper()
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
    }


    /**
     * 'accept' or 'cancel' when [v] click
     */
    private fun onAcceptOrCancelClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.bt_accpet -> if (mBSelectSubFilter) {
                if (!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                    rootActivity!!.jumpByTabName(NoteDataHelper.TAB_TITLE_MONTHLY)
                    EventBus.getDefault().post(
                            FilterShowEvent(NoteDataHelper.TAB_TITLE_YEARLY,
                                    ArrayList<String>(mLLSubFilter)))

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
        }
    }

    /**
     * 重新加载数据
     */
    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_giveup),
                this::onAcceptOrCancelClick)

        ToolUtil.runInBackground(activity,
                {
                    mYearPara.clear()
                    mHMMonthPara.clear()

                    // for year
                    NoteDataHelper.notesYears.forEach {
                        mYearPara.add(YearItemHolder(it))
                    }

                    // for month
                    NoteDataHelper.notesMonths.forEach {
                        val ky = it.substring(0, 4)
                        var lsDay: LinkedList<MonthItemHolder>? = mHMMonthPara[ky]
                        if (null == lsDay) {
                            lsDay = LinkedList()
                            mHMMonthPara[ky] = lsDay
                        }
                        lsDay.add(MonthItemHolder(it))
                    }
                },
                { loadUI(bundle) })
    }

    override fun loadUI(bundle: Bundle?) {
        refreshAttachLayout()

        // set listview adapter
        mLVShow.adapter = YearAdapter(context,
                mYearPara.filter { !mBFilter || mFilterPara.contains(it.tag) }
                        .sortedWith(Comparator { o1, o2 ->
                            if (!mBTimeDownOrder) o1.tag.compareTo(o2.tag)
                            else o2.tag.compareTo(o1.tag)
                        }))
    }

    /// BEGIN PRIVATE

    /**
     * 更新附加layout
     */
    private fun refreshAttachLayout() {
        setAttachLayoutVisible(if (mBFilter || mBSelectSubFilter) View.VISIBLE else View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(if (mBSelectSubFilter) View.VISIBLE else View.GONE)
    }

    /**
     * load month view for [lv] with [tag]
     */
    private fun loadMonthDetailView(lv: ListView, tag: String) {
        mHMMonthPara[tag]?.let {
            lv.adapter = MonthAdapter(context, it.sortedWith(Comparator { o1, o2 ->
                if (!mBTimeDownOrder) o1.tag.compareTo(o2.tag)
                else o2.tag.compareTo(o1.tag)
            }))
            ListViewHelper.setListViewHeightBasedOnChildren(lv)
        }
    }
    /// END PRIVATE

    /**
     * year item view adapter
     */
    private inner class YearAdapter
    internal constructor(context: Context, mdata: List<YearItemHolder>)
        : LVAdapter(context, mdata, R.layout.li_yearly_show) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(context, convertView, R.layout.li_yearly_show)
            if (null == viewHolder.getSelfTag(SELF_TAG_ID)) {
                viewHolder.setSelfTag(SELF_TAG_ID, Any())

                val hm = (getItem(position) as YearItemHolder).data
                val lv = viewHolder.getView<ListView>(R.id.lv_show_detail)
                val doFold = { lvMonth: ListView, tagData: YearData ->
                    lvMonth.visibility = if (tagData.show.isFold()) {
                        View.GONE
                    } else {
                        if (0 == lv.count)
                            loadMonthDetailView(lvMonth, tagData.year)

                        View.VISIBLE
                    }
                }
                doFold(lv, hm)

                // adjust row color
                viewHolder.getView<ConstraintLayout>(R.id.cl_header).let {
                    it.setBackgroundColor(if (0 == position % 2) ResourceHelper.mCRLVLineOne
                    else ResourceHelper.mCRLVLineTwo)

                    it.setOnClickListener({ _ ->
                        hm.show = if (hm.show.isFold()) EShowFold.UNFOLD else EShowFold.FOLD
                        doFold(lv, hm)
                    })
                }

                // for year
                viewHolder.setText(R.id.tv_year, "${hm.year}年")

                // for graph value
                hm.yearDetail?.let {
                    viewHolder.getView<ValueShow>(R.id.vs_yearly_info).adjustAttribute(
                            HashMap<String, Any>().apply {
                                put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                                put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                                put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                                put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount) }) }
            }
            return viewHolder.convertView
        }
    }


    /**
     * month item view adapter
     */
    private inner class MonthAdapter
    internal constructor(context: Context, sdata: List<*>)
        : LVAdapter(context, sdata, R.layout.li_yearly_show_detail) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(context, convertView, R.layout.li_yearly_show_detail)
            if (null == viewHolder.getSelfTag(SELF_TAG_ID)) {
                viewHolder.setSelfTag(SELF_TAG_ID, Any())

                val hm = (getItem(position) as MonthItemHolder).data
                viewHolder.getView<ImageView>(R.id.iv_action).let {
                    it.setBackgroundColor(if (mLLSubFilter.contains(hm.subTag)) ResourceHelper.mCRLVItemSel
                    else ResourceHelper.mCRLVItemTransFull)
                    it.setOnClickListener { v ->
                        val subTag = hm.subTag
                        if (!mLLSubFilter.contains(subTag)) {
                            v.setBackgroundColor(ResourceHelper.mCRLVItemSel)

                            mLLSubFilter.add(subTag)
                            mLLSubFilterVW.add(v)

                            if (!mBSelectSubFilter) {
                                mBSelectSubFilter = true
                                refreshAttachLayout()
                            }
                        } else {
                            v.setBackgroundColor(ResourceHelper.mCRLVItemTransFull)

                            mLLSubFilter.remove(subTag)
                            mLLSubFilterVW.remove(v)

                            if (mLLSubFilter.isEmpty()) {
                                mLLSubFilterVW.clear()
                                mBSelectSubFilter = false
                                refreshAttachLayout()
                            }
                        }
                    }
                }

                // for show
                viewHolder.setText(R.id.tv_month, hm.month)

                hm.monthDetail?.let {
                    HelperDayNotesInfo.fillNoteInfo(viewHolder,
                        it.mPayCount, it.mPayAmount, it.mIncomeCount, it.mIncomeAmount,
                        hm.amount!!)
                }
            }
            return viewHolder.convertView
        }
    }

    companion object {
        private const val SELF_TAG_ID = 0
    }
}
