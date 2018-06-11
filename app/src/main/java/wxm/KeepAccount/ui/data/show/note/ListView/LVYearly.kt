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
import wxm.androidutil.improve.let1
import wxm.KeepAccount.improve.toSignalMoneyString
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.HelperDayNotesInfo
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
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
 * 年数据视图辅助类
 * Created by WangXM on 2016/9/10.
 */
class LVYearly : LVBase() {
    // 若为true则数据以时间降序排列
    private var mBTimeDownOrder = true

    private val mYearPara: LinkedList<HashMap<String, YearItemHolder>> = LinkedList()
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
            NoteDataHelper.getInfoByYear(tag).let {
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

            NoteDataHelper.getInfoByMonth(tag).let {
                map.monthDetail = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                map.amount = it.balance.toSignalMoneyString()
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

                mIBSort.setActIcon(mBTimeDownOrder.doJudge(R.drawable.ic_sort_up_1, R.drawable.ic_sort_down_1))
                mIBSort.setActName(mBTimeDownOrder.doJudge(R.string.cn_sort_up_by_time, R.string.cn_sort_down_by_time))

                EventHelper.setOnClickOperator(parentView,
                        intArrayOf(R.id.ib_sort, R.id.ib_refresh),
                        this::onActClick)
            }

            private fun onActClick(v: View) {
                when (v.id) {
                    R.id.ib_sort -> {
                        mBTimeDownOrder = !mBTimeDownOrder

                        mIBSort.setActIcon(mBTimeDownOrder.doJudge(R.drawable.ic_sort_up_1, R.drawable.ic_sort_down_1))
                        mIBSort.setActName(mBTimeDownOrder.doJudge(R.string.cn_sort_up_by_time, R.string.cn_sort_down_by_time))

                        loadUI(null)
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
     * 过滤视图事件
     *
     * @param event 事件
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShow) {
    }

    /**
     * 'accept' or 'cancel' when [v] click
     */
    private fun onAcceptOrCancelClick(v: View) {
        when (v.id) {
            R.id.bt_accpet -> if (mBSelectSubFilter) {
                if (!UtilFun.ListIsNullOrEmpty(mLLSubFilter)) {
                    rootActivity!!.jumpByTabName(NoteDataHelper.TAB_TITLE_MONTHLY)
                    EventBus.getDefault().post(
                            FilterShow(NoteDataHelper.TAB_TITLE_YEARLY,
                                    ArrayList<String>(mLLSubFilter)))

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
        }
    }

    /**
     * 重新加载数据
     */
    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_cancel),
                this::onAcceptOrCancelClick)

        ToolUtil.runInBackground(activity!!,
                {
                    mYearPara.clear()
                    mHMMonthPara.clear()

                    // for year
                    NoteDataHelper.notesYears.map {
                        HashMap<String, YearItemHolder>()
                                .apply { put(KEY_DATA, YearItemHolder(it)) }
                    }.let1 {
                        mYearPara.addAll(it)
                    }

                    // for month
                    NoteDataHelper.notesMonths.forEach {
                        val ky = it.substring(0, 4)
                        mHMMonthPara[ky].forObj(
                                { ls ->
                                    ls.add(MonthItemHolder(it))
                                    Unit
                                },
                                {
                                    mHMMonthPara[ky] = LinkedList<MonthItemHolder>().apply {
                                        add(MonthItemHolder(it))
                                    }
                                    Unit
                                }
                        )
                    }
                },
                { loadUI(bundle) })
    }

    override fun loadUI(bundle: Bundle?) {
        refreshAttachLayout()

        mLVShow.adapter = YearAdapter(context!!,
                mYearPara.filter { !mBFilter || mFilterPara.contains(it[KEY_DATA]!!.tag) }
                        .sortedWith(Comparator { o1, o2 ->
                            mBTimeDownOrder.doJudge(
                                    o1[KEY_DATA]!!.tag.compareTo(o2[KEY_DATA]!!.tag),
                                    o2[KEY_DATA]!!.tag.compareTo(o1[KEY_DATA]!!.tag))
                        }))
    }

    /// BEGIN PRIVATE

    /**
     * 更新附加layout
     */
    private fun refreshAttachLayout() {
        setAttachLayoutVisible((mBFilter || mBSelectSubFilter).doJudge(View.VISIBLE, View.GONE))
        setFilterLayoutVisible(mBFilter.doJudge(View.VISIBLE, View.GONE))
        setAcceptGiveUpLayoutVisible(mBSelectSubFilter.doJudge(View.VISIBLE, View.GONE))
    }

    /**
     * load month view for [lv] with [tag]
     */
    private fun loadMonthDetailView(lv: ListView, tag: String) {
        mHMMonthPara[tag]?.let {
            LinkedList<HashMap<String, MonthItemHolder>>().apply {
                addAll(it.sortedWith(Comparator { o1, o2 ->
                    mBTimeDownOrder.doJudge(o1.tag.compareTo(o2.tag), o2.tag.compareTo(o1.tag))
                }).map { HashMap<String, MonthItemHolder>().apply { put(KEY_DATA, it) } })
            }.let {
                lv.adapter = MonthAdapter(context!!, it)
            }

            ListViewHelper.setListViewHeightBasedOnChildren(lv)
        }
    }
    /// END PRIVATE

    /**
     * year item view adapter
     */
    private inner class YearAdapter internal constructor(context: Context, mdata: List<Map<String, YearItemHolder>>)
        : MoreAdapter(context, mdata, R.layout.li_yearly_show) {
        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            @Suppress("UNCHECKED_CAST")
            val hm = (getItem(pos) as Map<String, YearItemHolder>)[KEY_DATA]!!.data
            val lv = vhHolder.getView<ListView>(R.id.lv_show_detail)
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
            vhHolder.getView<ConstraintLayout>(R.id.cl_header).let {
                it.setBackgroundColor((0 == pos % 2)
                        .doJudge(ResourceHelper.mCRLVLineOne, ResourceHelper.mCRLVLineTwo))

                it.setOnClickListener({ _ ->
                    hm.show = hm.show.isFold().doJudge(EShowFold.UNFOLD, EShowFold.FOLD)
                    doFold(lv, hm)
                })
            }

            // for year
            vhHolder.setText(R.id.tv_year, "${hm.year}年")

            // for graph value
            hm.yearDetail!!.let {
                vhHolder.getView<ValueShow>(R.id.vs_yearly_info).adjustAttribute(
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
     * month item view adapter
     */
    private inner class MonthAdapter
    internal constructor(context: Context, sdata: List<Map<String, MonthItemHolder>>)
        : MoreAdapter(context, sdata, R.layout.li_yearly_show_detail) {
        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            @Suppress("UNCHECKED_CAST")
            val hm = (getItem(pos) as Map<String, MonthItemHolder>)[KEY_DATA]!!.data
            vhHolder.getView<ImageView>(R.id.iv_action).let {
                it.setBackgroundColor(mLLSubFilter.contains(hm.subTag)
                        .doJudge(ResourceHelper.mCRLVItemSel, ResourceHelper.mCRLVItemTransFull))
                it.setOnClickListener { v ->
                    val subTag = hm.subTag
                    mLLSubFilter.contains(subTag).doJudge(
                            {
                                v.setBackgroundColor(ResourceHelper.mCRLVItemTransFull)

                                mLLSubFilter.remove(subTag)
                                mLLSubFilterVW.remove(v)

                                if (mLLSubFilter.isEmpty()) {
                                    mLLSubFilterVW.clear()
                                    mBSelectSubFilter = false
                                    refreshAttachLayout()
                                }
                            },
                            {
                                v.setBackgroundColor(ResourceHelper.mCRLVItemSel)

                                mLLSubFilter.add(subTag)
                                mLLSubFilterVW.add(v)

                                if (!mBSelectSubFilter) {
                                    mBSelectSubFilter = true
                                    refreshAttachLayout()
                                }
                            }
                    )
                }
            }

            // for show
            vhHolder.setText(R.id.tv_month, hm.month)

            hm.monthDetail?.let {
                HelperDayNotesInfo.fillNoteInfo(vhHolder,
                        it.mPayCount, it.mPayAmount, it.mIncomeCount, it.mIncomeAmount,
                        hm.amount!!)
            }
        }
    }

    companion object {
        private const val KEY_DATA = "data"
    }
}
