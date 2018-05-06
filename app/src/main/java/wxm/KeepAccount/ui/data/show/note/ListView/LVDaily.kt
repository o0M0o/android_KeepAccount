package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.ui.base.Adapter.LVAdapter
import wxm.KeepAccount.ui.base.Helper.ResourceHelper
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.report.ACReport
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail
import wxm.KeepAccount.ui.data.show.note.ShowData.FilterShowEvent
import wxm.KeepAccount.ui.data.show.note.base.EOperation
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.KeepAccount.utility.EventHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.Dialog.DlgOKOrNOBase
import wxm.androidutil.ViewHolder.ViewDataHolder
import wxm.androidutil.ViewHolder.ViewHolder
import wxm.uilib.IconButton.IconButton
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale
import kotlin.Comparator

/**
 * listview for daily data
 * Created by WangXM on 2016/9/10.
 */
class LVDaily : LVBase() {
    private var mAction = EOperation.EDIT

    // 若为true则数据以时间降序排列
    private var mBTimeDownOrder = true
    private val mMainPara: LinkedList<ItemHolder> = LinkedList()

    internal data class MainAdapterItem(val year: String, val month: String, val day: String) {
        var show: String = EShowFold.FOLD.showStatus

        var dayInWeek: String? = null
        var dayInfo: RecordDetail? = null
        var amount: String? = null
    }

    internal inner class ItemHolder(tag: String)
        : ViewDataHolder<String, MainAdapterItem>(tag) {
        override fun getDataByTag(tag: String): MainAdapterItem {
            val szMonth = tag.substring(5, 7).removePrefix("0")
            val szDay = tag.substring(8, 10).removePrefix("0")
            val item = MainAdapterItem(tag.substring(0, 4), szMonth, szDay)

            NoteDataHelper.getInfoByDay(tag)?.let {
                item.dayInWeek = ToolUtil.getDayInWeek(tag)
                item.dayInfo = RecordDetail(it.payCount.toString(), it.szPayAmount,
                        it.incomeCount.toString(), it.szIncomeAmount)

                item.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)

                item.show = EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
            }

            return item
        }
    }

    internal inner class DailyActionHelper : ActionHelper() {
        private lateinit var mIBSort: IconButton

        override fun initActs(parentView: View) {
            mIBSort = parentView.findViewById(R.id.ib_sort)

            mIBSort.setActIcon(if (mBTimeDownOrder) R.drawable.ic_sort_up_1
            else R.drawable.ic_sort_down_1)
            mIBSort.setActName(if (mBTimeDownOrder) R.string.cn_sort_up_by_name
            else R.string.cn_sort_down_by_name)

            EventHelper.setOnClickOperator(parentView,
                    intArrayOf(R.id.ib_sort, R.id.ib_delete, R.id.ib_add, R.id.ib_refresh, R.id.ib_report),
                    this::onActionClick)
        }

        private fun onActionClick(v: View) {
            when (v.id) {
                R.id.ib_sort -> {
                    mBTimeDownOrder = !mBTimeDownOrder

                    mIBSort.setActIcon(if (mBTimeDownOrder) R.drawable.ic_sort_up_1 else R.drawable.ic_sort_down_1)
                    mIBSort.setActName(if (mBTimeDownOrder) R.string.cn_sort_up_by_time else R.string.cn_sort_down_by_time)

                    reorderData()
                    reloadUI()
                }

                R.id.ib_refresh -> {
                    mAction = EOperation.EDIT
                    reloadView(context, false)
                }

                R.id.ib_delete -> {
                    mAction = if (mAction.isEdit) {
                        EOperation.DELETE
                    } else {
                        doDelete()
                        EOperation.EDIT
                    }

                    redrawUI()
                }

                R.id.ib_add -> {
                    val ac = rootActivity
                    val intent = Intent(ac, ACNoteCreate::class.java)
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = System.currentTimeMillis()
                    intent.putExtra(GlobalDef.STR_RECORD_DATE,
                            String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))

                    ac!!.startActivityForResult(intent, 1)
                }

                R.id.ib_report -> {
                    val dlgDay = DlgSelectReportDays()
                    dlgDay.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                        override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                            val it = Intent(rootActivity, ACReport::class.java)
                            it.putExtra(ACReport.PARA_TYPE, ACReport.PT_DAY)
                            it.putStringArrayListExtra(ACReport.PARA_LOAD,
                                    arrayListOf(dlgDay.startDay!!, dlgDay.endDay!!))
                            rootActivity!!.startActivity(it)
                        }

                        override fun onDialogNegativeResult(dialogFragment: DialogFragment) {
                        }
                    })

                    dlgDay.show(rootActivity!!.supportFragmentManager, "select days")
                }
            }
        }
    }

    init {
        mBActionExpand = false
        mAHActs = DailyActionHelper()


    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    /**
     * filter event
     * @param event     param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShowEvent) {
        if (NoteDataHelper.TAB_TITLE_MONTHLY == event.sender) {
            mBFilter = true
            mAction = EOperation.EDIT

            mFilterPara.clear()
            mFilterPara.addAll(event.filterTag)
            reloadUI()
        }
    }

    /**
     * handler for 'accept' or 'cancel'
     */
    private fun onAcceptOrCancelClick(v: View) {
        val vid = v.id
        when (vid) {
            R.id.bt_accpet -> if (mAction.isDelete) {
                doDelete()

                mAction = EOperation.EDIT
                redrawUI()
            }

            R.id.bt_giveup -> {
                mAction = EOperation.EDIT
                redrawUI()
            }

            R.id.bt_giveup_filter -> {
                mBFilter = false
                reloadUI()
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_giveup, R.id.bt_giveup_filter),
                this::onAcceptOrCancelClick)

        showLoadingProgress(true)
        ToolUtil.runInBackground(activity,
                {
                    mMainPara.clear()
                    NoteDataHelper.notesDays.toSortedSet(
                            Comparator { o1, o2 ->
                                if (!mBTimeDownOrder) o1.compareTo(o2)
                                else o2.compareTo(o1)
                            }).forEach {
                        mMainPara.add(ItemHolder(it))
                    }
                },
                {
                    showLoadingProgress(false)
                    loadUI(bundle)
                })
    }


    override fun loadUI(bundle: Bundle?) {
        // adjust attach layout
        setAttachLayoutVisible(if (mAction.isDelete || mBFilter) View.VISIBLE
        else View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(if (mAction.isDelete && !mBFilter) View.VISIBLE else View.GONE)

        // load show data
        MainAdapter(context,
                mMainPara.filter {
                    if (mBFilter) mFilterPara.contains(it.tag) else true
                }).let {
            mLVShow.adapter = it
            it.notifyDataSetChanged()
        }
    }


    /// BEGIN PRIVATE
    private fun doDelete() {
        val lsIncome = ArrayList<Int>()
        val lsPay = ArrayList<Int>()
        (mLVShow.adapter as MainAdapter).waitDeleteDays.forEach {
            NoteDataHelper.getNotesByDay(it)?.forEach {
                (if (it.isPayNote) lsPay else lsIncome).add(it.id)
            }
        }

        if (!lsIncome.isEmpty()) {
            ContextUtil.payIncomeUtility.deleteIncomeNotes(lsIncome)
        }

        if (!lsPay.isEmpty()) {
            ContextUtil.payIncomeUtility.deletePayNotes(lsPay)
        }
    }

    /**
     * redraw UI
     */
    private fun redrawUI() {
        // adjust attach layout
        setAttachLayoutVisible(if (mAction.isDelete || mBFilter)
            View.VISIBLE
        else
            View.GONE)
        setFilterLayoutVisible(if (mBFilter) View.VISIBLE else View.GONE)
        setAcceptGiveUpLayoutVisible(if (mAction.isDelete && !mBFilter) View.VISIBLE else View.GONE)

        MainAdapter(context,
                mMainPara.filter {
                    if (mBFilter) mFilterPara.contains(it.tag) else true
                }).let {
            mLVShow.adapter = it
            it.notifyDataSetChanged()
        }
    }

    /**
     * reorder data
     */
    private fun reorderData() {
        mMainPara.reverse()
    }
    /// END PRIVATE

    /**
     * main adapter
     */
    private inner class MainAdapter internal constructor(context: Context, data: List<ItemHolder>)
        : LVAdapter(context, data, R.layout.li_daily_show) {
        private val mCLAdapter = View.OnClickListener { v ->
            val pos = mLVShow.getPositionForView(v)
            val hm = getItem(pos) as ItemHolder

            rootActivity?.let {
                it.startActivity(Intent(it, ACDailyDetail::class.java)
                        .putExtra(ACDailyDetail.K_HOTDAY, hm.tag))
            }
        }

        /**
         * get daily data need delete
         * @return      daily data
         */
        internal val waitDeleteDays: List<String>
            get() {
                if (!mAction.isDelete) {
                    return emptyList()
                }

                val ret = LinkedList<String>()
                val count = mLVShow.childCount
                for (pos in 0 until count) {
                    mLVShow.getChildAt(pos).findViewById<CheckBox>(R.id.cb_del)
                            .apply {
                                if (isChecked) {
                                    ret.add(tag as String)
                                }
                            }
                }
                return ret
            }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder = ViewHolder.get(context, convertView, R.layout.li_daily_show)
            val cb = viewHolder.getView<CheckBox>(R.id.cb_del)
            cb.visibility = if (mAction.isEdit) {
                cb.isChecked = false
                View.GONE
            } else {
                View.VISIBLE
            }

            val vwRoot = viewHolder.convertView
            if (null == viewHolder.getSelfTag(SELF_TAG_ID)) {
                viewHolder.setSelfTag(SELF_TAG_ID, Any())

                val it = getItem(position) as ItemHolder
                cb.tag = it.tag

                vwRoot.setBackgroundColor(if (0 == position % 2) ResourceHelper.mCRLVLineOne
                else ResourceHelper.mCRLVLineTwo)
                vwRoot.setOnClickListener(mCLAdapter)

                initItemShow(viewHolder, it.data,
                        if (position > 0) (getItem(position - 1) as ItemHolder).data
                        else null)
            }

            return vwRoot
        }

        private fun initItemShow(vh: ViewHolder, item: MainAdapterItem, prvItem: MainAdapterItem?) {
            if (null == prvItem || prvItem.year != item.year) {
                vh.setText(R.id.tv_year_number, "${item.year}年")
            } else {
                vh.getView<View>(R.id.tv_year_number).visibility = View.INVISIBLE
            }

            vh.setText(R.id.tv_month_number, item.month)
            vh.setText(R.id.tv_day_number, item.day)
            vh.setText(R.id.tv_day_in_week, item.dayInWeek)

            item.dayInfo?.let {
                vh.getView<ValueShow>(R.id.vs_daily_info)
                        .adjustAttribute(HashMap<String, Any>().apply {
                            put(ValueShow.ATTR_PAY_COUNT, it.mPayCount)
                            put(ValueShow.ATTR_PAY_AMOUNT, it.mPayAmount)
                            put(ValueShow.ATTR_INCOME_COUNT, it.mIncomeCount)
                            put(ValueShow.ATTR_INCOME_AMOUNT, it.mIncomeAmount)
                        })
            }
        }
    }

    companion object {
        private const val SELF_TAG_ID = 0
    }
}


