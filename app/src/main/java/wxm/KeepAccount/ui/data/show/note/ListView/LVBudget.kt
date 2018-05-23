package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.define.PayNoteItem
import wxm.KeepAccount.ui.base.Adapter.LVAdapter
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.edit.NoteEdit.ACNoteEdit
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.data.show.note.base.EOperation
import wxm.KeepAccount.ui.utility.ListViewHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.view.ViewHolder
import wxm.androidutil.util.UtilFun
import wxm.androidutil.ui.view.EventHelper
import wxm.uilib.IconButton.IconButton
import java.util.*

/**
 * budget listview
 * Created by WangXM on 2016/9/15.
 */
class LVBudget : LVBase() {
    private var mActionType: EOperation = EOperation.EDIT

    private var mBODownOrder = true
    private val mMainPara: LinkedList<MainAdapterItem> = LinkedList()
    private val mHMSubPara: HashMap<String, LinkedList<SubAdapterItem>> = HashMap()

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
                    reloadView(context, false)
                }

                R.id.ib_delete -> {
                    if (EOperation.DELETE != mActionType) {
                        mActionType = EOperation.DELETE
                        reloadView(v.context, false)
                    }
                }

                R.id.ib_add -> {
                    val ac = rootActivity
                    val intent = Intent(ac, ACNoteEdit::class.java)
                    intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    ac!!.startActivityForResult(intent, 1)
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
        mLVShow.adapter = MainAdapter(ContextUtil.self, LinkedList<MainAdapterItem>(mMainPara))
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_giveup),
                {
                    when (it.id) {
                        R.id.bt_accpet -> {
                            if (EOperation.DELETE  == mActionType) {
                                mActionType = EOperation.DELETE

                                val sad = UtilFun.cast_t<MainAdapter>(mLVShow.adapter)
                                val lsDel = sad.waitDeleteItems
                                if (!UtilFun.ListIsNullOrEmpty(lsDel)) {
                                    ContextUtil.budgetUtility.removeDatas(lsDel)
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
                    val szShow = String.format(Locale.CHINA, "(总额)%.02f/(剩余)%.02f",
                            it.key.amount, it.key.remainderAmount)

                    val map = MainAdapterItem(tag, it.key.name)
                    it.key.note?.let {
                        map.note = it
                    }

                    map.amount = szShow
                    map.show = LVBase.EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
                    mMainPara.add(map)

                    parseSub(tag, it.value)
                }
    }


    private fun parseSub(parentTag: String, lsPay: List<PayNoteItem>) {
        val curLs = LinkedList<SubAdapterItem>()
        lsPay.sortedBy { it.ts }.forEach {
            val allDate = it.tsToStr!!
            val map = SubAdapterItem(parentTag, allDate.substring(0, 10))
            allDate.substring(0, 7).let {
                map.month = "${it.substring(0, 4)}年${it.substring(5, 7).removePrefix("0")}月"
            }

            map.dayNumber = allDate.substring(8, 10).removePrefix("0")
            map.dayInWeek = ToolUtil.getDayInWeek(it.ts)

            it.note?.let {
                map.note = if (it.length > 10) it.substring(0, 10) + "..." else it
            }

            map.time = allDate.substring(11, 16)
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
        val mAdapter = SubAdapter(context, mHMSubPara[tag]!!)
        lv.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        ListViewHelper.setListViewHeightBasedOnChildren(lv)
    }
    /// END PRIVATE

    /**
     * adapter for budget
     */
    private inner class MainAdapter internal constructor(context: Context, mdata: List<*>) : LVAdapter(context, mdata, R.layout.li_budget_show) {
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
                    val ac = rootActivity!!
                    val it = Intent(ac, ACNoteEdit::class.java)
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, tagId)
                    it.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE, GlobalDef.STR_RECORD_BUDGET)
                    ac.startActivityForResult(it, 1)
                }
            }
        }

        val waitDeleteItems: List<Int>
            get() = mALWaitDeleteItems

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(rootActivity, convertView, R.layout.li_budget_show)

            val hm = getItem(position) as MainAdapterItem
            val lv = viewHolder.getView<ListView>(R.id.lv_show_detail)
            val tag = hm.tag
            lv.visibility = if (EShowFold.getByShowStatus(hm.show).isFold()) {
                View.GONE
            } else {
                loadDayPayView(lv, tag)
                View.VISIBLE
            }

            val selfListener = View.OnClickListener { _ ->
                val bf = EShowFold.getByShowStatus(hm.show).isFold()
                hm.show = EShowFold.getByFold(!bf).showStatus
                lv.visibility = if (bf) {
                    loadDayPayView(lv, tag)
                    addUnfoldItem(tag)
                    View.VISIBLE
                } else {
                    removeUnfoldItem(tag)
                    View.GONE
                }
            }

            // for background color
            val rl = viewHolder.getView<ConstraintLayout>(R.id.cl_header)
            rl.setOnClickListener(selfListener)
            rl.setBackgroundColor(if (0 == position % 2)
                ResourceHelper.mCRLVLineOne
            else
                ResourceHelper.mCRLVLineTwo)

            // for delete
            val rlDel = viewHolder.getView<View>(R.id.rl_delete)
            rlDel.visibility = if (mActionType == EOperation.EDIT) View.GONE else View.VISIBLE
            rlDel.setOnClickListener(mCLAdapter)

            // for note
            val nt = hm.note
            val bNote = UtilFun.StringIsNullOrEmpty(nt)
            viewHolder.getView<View>(R.id.iv_note).visibility = if (bNote) View.GONE else View.VISIBLE
            viewHolder.getView<View>(R.id.tv_budget_note).visibility = if (bNote) View.GONE else View.VISIBLE
            if (!bNote)
                viewHolder.setText(R.id.tv_budget_note, nt)

            viewHolder.setText(R.id.tv_budget_name, hm.title)
            viewHolder.setText(R.id.tv_budget_amount, hm.amount)

            viewHolder.getView<ImageView>(R.id.iv_edit).setOnClickListener(mCLAdapter)
            return viewHolder.convertView
        }
    }


    /**
     * sub adapter
     */
    private inner class SubAdapter
    internal constructor(context: Context, sData: List<SubAdapterItem>)
        : LVAdapter(context, sData, R.layout.li_budget_show_detail) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val vh = ViewHolder.get(rootActivity, convertView, R.layout.li_budget_show_detail)

            val hm = getItem(position) as SubAdapterItem
            val nt = hm.note
            if (UtilFun.StringIsNullOrEmpty(nt)) {
                vh.getView<View>(R.id.rl_pay_note).visibility = View.GONE
            }

            // for show
            vh.setText(R.id.tv_month, hm.month)
            vh.setText(R.id.tv_day_number, hm.dayNumber)
            vh.setText(R.id.tv_day_in_week, hm.dayInWeek)
            vh.setText(R.id.tv_pay_title, hm.title)
            vh.setText(R.id.tv_pay_amount, hm.amount)
            vh.setText(R.id.tv_pay_note, hm.note)
            vh.setText(R.id.tv_pay_time, hm.time)

            // for look action
            vh.getView<View>(R.id.iv_look).setOnClickListener { _ ->
                val ac = rootActivity!!
                val intent: Intent
                intent = Intent(ac, ACNoteEdit::class.java)
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_ID, Integer.valueOf(hm.id))
                intent.putExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE,
                        GlobalDef.STR_RECORD_PAY)

                ac.startActivityForResult(intent, 1)
            }
            return vh.convertView
        }
    }
}
