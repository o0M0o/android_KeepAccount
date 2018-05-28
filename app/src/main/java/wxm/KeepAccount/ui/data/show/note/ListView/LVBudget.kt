package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.data.show.note.base.EOperation
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.utility.*
import wxm.androidutil.time.*
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.ui.view.ViewHolder
import wxm.uilib.IconButton.IconButton
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * budget listview
 * Created by WangXM on 2016/9/15.
 */
class LVBudget : LVBase() {
    private var mActionType: EOperation = EOperation.EDIT

    private var mBODownOrder = true
    private val mMainPara: LinkedList<HashMap<String, MainAdapterItem>> = LinkedList()
    private val mHMSubPara: HashMap<String, LinkedList<SubAdapterItem>> = HashMap()

    companion object {
        private const val KEY_DATA = "data"
    }


    internal data class MainAdapterItem(val tag: String, val title: String) {
        var show: String = EShowFold.FOLD.showStatus

        var note: String? = null
        var amount: String? = null
    }

    internal data class SubAdapterItem(val tag: String, val subTag: String) {
        var id: Int = GlobalDef.INVALID_ID

        var month: String? = null
        var time: String? = null
        var dayNumber: String? = null
        var dayInWeek: String? = null
        var note: String? = null
        var title: String? = null
        var amount: String? = null
    }

    internal inner class BudgetActionHelper : ActionHelper() {
        private lateinit var mIBSort: IconButton
        private lateinit var mIBReport: IconButton

        override fun initActs(parentView: View) {
            mIBSort = parentView.findViewById(R.id.ib_sort)
            mIBReport = parentView.findViewById(R.id.ib_report)

            mIBReport.visibility = View.GONE

            mIBSort.setActIcon(if (mBODownOrder) R.drawable.ic_sort_up_1
            else R.drawable.ic_sort_down_1)
            mIBSort.setActName(if (mBODownOrder) R.string.cn_sort_up_by_name
            else R.string.cn_sort_down_by_name)

            EventHelper.setOnClickOperator(parentView,
                    intArrayOf(R.id.ib_sort, R.id.ib_delete, R.id.ib_add, R.id.ib_refresh),
                    this::onActionClick)
        }

        private fun onActionClick(v: View) {
            when (v.id) {
                R.id.ib_sort -> {
                    mBODownOrder = !mBODownOrder

                    mIBSort.setActIcon(if (mBODownOrder) R.drawable.ic_sort_up_1 else R.drawable.ic_sort_down_1)
                    mIBSort.setActName(if (mBODownOrder) R.string.cn_sort_up_by_name else R.string.cn_sort_down_by_name)

                    reorderData()
                    loadUI(null)
                }

                R.id.ib_refresh -> {
                    mActionType = EOperation.EDIT
                    reloadView(false)
                }

                R.id.ib_delete -> {
                    if (EOperation.DELETE != mActionType) {
                        mActionType = EOperation.DELETE
                        reloadView(false)
                    }
                }

                R.id.ib_add -> {
                    Intent(activity, ACNoteEdit::class.java).apply {
                        putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    }.let1 {
                        activity.startActivityForResult(it, 1)
                    }
                }
            }
        }
    }

    init {
        mAHActs = BudgetActionHelper()
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter event
     * @param event 事件
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
    }

    override fun loadUI(bundle: Bundle?) {
        refreshAttachLayout()
        mLVShow.adapter = MainAdapter(ContextUtil.self, mMainPara)
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_giveup),
                {
                    when (it.id) {
                        R.id.bt_accpet -> {
                            if (EOperation.DELETE == mActionType) {
                                mActionType = EOperation.DELETE

                                (mLVShow.adapter as MainAdapter).waitDeleteItems.let {
                                    ContextUtil.budgetUtility.removeDatas(it)
                                }
                            }

                            loadUI(null)
                        }

                        R.id.bt_giveup -> {
                            mActionType = EOperation.EDIT
                            loadUI(null)
                        }
                    }
                })

        ToolUtil.runInBackground(activity,
                { this.parseNotes() },
                { loadUI(bundle) })
    }

    /// BEGIN PRIVATE
    private fun refreshAttachLayout() {
        setAttachLayoutVisible(if (EOperation.EDIT != mActionType) View.VISIBLE else View.GONE)
        setFilterLayoutVisible(View.GONE)
        setAcceptGiveUpLayoutVisible(if (EOperation.EDIT != mActionType) View.VISIBLE else View.GONE)
    }


    /**
     * reorder data
     */
    private fun reorderData() {
        mMainPara.reverse()
    }

    /**
     * parse data
     */
    private fun parseNotes() {
        mMainPara.clear()
        mHMSubPara.clear()

        ContextUtil.budgetUtility.budgetWithPayNote.toSortedMap(
                Comparator { a, b ->
                    if (mBODownOrder)
                        a.name.compareTo(b.name)
                    else
                        b.name.compareTo(a.name)
                })
                .forEach {
                    val tag = it.key._id.toString()
                    val map = MainAdapterItem(tag, it.key.name)
                    it.key.note?.let {
                        map.note = it
                    }

                    map.amount = String.format(Locale.CHINA, "(总额)%.02f/(剩余)%.02f",
                            it.key.amount, it.key.remainderAmount)
                    map.show = LVBase.EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
                    mMainPara.add(HashMap<String, MainAdapterItem>()
                            .apply { put(KEY_DATA, map) })

                    parseSub(tag, it.value)
                }
    }


    private fun parseSub(parentTag: String, lsPay: List<PayNoteItem>) {
        val curLs = LinkedList<SubAdapterItem>()
        lsPay.sortedBy { it.ts }.forEach {
            val cl = it.ts.toCalendar()

            val map = SubAdapterItem(parentTag, it.tsToStr.substring(0, 10))
            map.month = "${cl.getYear()}年${cl.getMonth()}月"

            map.dayNumber = cl.getDayInMonth().toString()
            map.dayInWeek = cl.getDayInWeekString()

            it.note?.let {
                map.note = if (it.length > 10) it.substring(0, 10) + "..." else it
            }

            map.time = cl.getHourMinuteString()
            map.title = it.info
            map.amount = it.valToStr
            map.id = it.id
            curLs.add(map)
        }

        mHMSubPara[parentTag] = curLs
    }

    /**
     * load detail view
     * @param lv        UI
     * @param tag       data tag
     */
    private fun loadDayPayView(lv: ListView, tag: String?) {
        ArrayList<Map<String, SubAdapterItem>>().apply {
            mHMSubPara[tag]!!.forEach {
                add(HashMap<String, SubAdapterItem>().apply { put(KEY_DATA, it) })
            }
        }.let {
            lv.adapter = SubAdapter(context, it)
        }

        ListViewHelper.setListViewHeightBasedOnChildren(lv)
    }
    /// END PRIVATE

    /**
     * adapter for budget
     */
    private inner class MainAdapter internal constructor(context: Context, data: List<Map<String, MainAdapterItem>>)
        : MoreAdapter(context, data, R.layout.li_budget_show) {
        private val mALWaitDeleteItems = ArrayList<Int>()

        private val mCLAdapter = View.OnClickListener { v ->
            val pos = mLVShow.getPositionForView(v)
            val hm = getItem(pos) as MainAdapterItem
            val tagId = Integer.parseInt(hm.tag)
            when (v.id) {
                R.id.rl_delete -> {
                    if (mALWaitDeleteItems.contains(tagId)) {
                        mALWaitDeleteItems.remove(tagId as Any)
                        v.setBackgroundColor(ResourceHelper.mCRLVItemNoSel)
                    } else {
                        mALWaitDeleteItems.add(tagId)
                        v.setBackgroundColor(ResourceHelper.mCRLVItemSel)
                    }
                }

                R.id.iv_edit -> {
                    Intent(activity, ACNoteEdit::class.java).apply {
                        putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, tagId)
                        putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    }.let1 {
                        activity.startActivityForResult(it, 1)
                    }
                }
            }
        }

        val waitDeleteItems: List<Int>
            get() = mALWaitDeleteItems

        private fun getTypedItem(pos: Int): MainAdapterItem {
            @Suppress("UNCHECKED_CAST")
            return (getItem(pos) as Map<String, MainAdapterItem>)[KEY_DATA]!!
        }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            val hm = getTypedItem(pos)
            val lv = vhHolder.getView<ListView>(R.id.lv_show_detail)
            val tag = hm.tag
            EShowFold.getByShowStatus(hm.show).isFold().doJudge(
                    { lv.visibility = View.GONE },
                    {
                        loadDayPayView(lv, tag)
                        lv.visibility = View.VISIBLE
                    })

            // for background color
            vhHolder.getView<ConstraintLayout>(R.id.cl_header).let {
                it.setOnClickListener {
                    val bf = EShowFold.getByShowStatus(hm.show).isFold()
                    hm.show = EShowFold.getByFold(!bf).showStatus
                    bf.doJudge(
                            {
                                loadDayPayView(lv, tag)
                                addUnfoldItem(tag)
                                lv.visibility = View.VISIBLE
                            },
                            {
                                removeUnfoldItem(tag)
                                lv.visibility = View.GONE
                            })
                }

                it.setBackgroundColor((0 == pos % 2).doJudge(ResourceHelper.mCRLVLineOne, ResourceHelper.mCRLVLineTwo))
            }

            // for delete
            vhHolder.getView<View>(R.id.rl_delete).let {
                it.visibility = (mActionType == EOperation.EDIT).doJudge(View.GONE, View.VISIBLE)
                it.setOnClickListener(mCLAdapter)
            }

            // for note
            hm.note.let {
                it.isNullOrEmpty().doJudge(
                        {
                            vhHolder.getView<View>(R.id.iv_note).visibility = View.GONE
                            vhHolder.getView<View>(R.id.tv_budget_note).visibility = View.GONE
                        },
                        {
                            vhHolder.setText(R.id.tv_budget_note, it)
                            vhHolder.getView<View>(R.id.iv_note).visibility = View.VISIBLE
                            vhHolder.getView<View>(R.id.tv_budget_note).visibility = View.VISIBLE
                        }
                )
            }

            vhHolder.setText(R.id.tv_budget_name, hm.title)
            vhHolder.setText(R.id.tv_budget_amount, hm.amount)
            vhHolder.getView<ImageView>(R.id.iv_edit).setOnClickListener(mCLAdapter)
        }
    }

    /**
     * sub adapter
     */
    private inner class SubAdapter
    internal constructor(context: Context, sData: List<Map<String, SubAdapterItem>>)
        : MoreAdapter(context, sData, R.layout.li_budget_show_detail) {
        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            @Suppress("UNCHECKED_CAST")
            val hm = (getItem(pos) as Map<String, SubAdapterItem>)[KEY_DATA]!!
            val nt = hm.note
            if (nt.isNullOrEmpty()) {
                vhHolder.getView<View>(R.id.rl_pay_note).visibility = View.GONE
            }

            // for show
            vhHolder.setText(R.id.tv_month, hm.month)
            vhHolder.setText(R.id.tv_day_number, hm.dayNumber)
            vhHolder.setText(R.id.tv_day_in_week, hm.dayInWeek)
            vhHolder.setText(R.id.tv_pay_title, hm.title)
            vhHolder.setText(R.id.tv_pay_amount, hm.amount)
            vhHolder.setText(R.id.tv_pay_note, hm.note)
            vhHolder.setText(R.id.tv_pay_time, hm.time)

            // for look action
            vhHolder.getView<View>(R.id.iv_look).setOnClickListener { _ ->
                Intent(activity, ACNoteEdit::class.java).let {
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, Integer.valueOf(hm.id))
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_PAY)

                    activity.startActivityForResult(it, 1)
                }
            }
        }
    }
}
