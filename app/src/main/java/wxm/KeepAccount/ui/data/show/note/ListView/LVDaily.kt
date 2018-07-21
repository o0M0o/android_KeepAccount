package wxm.KeepAccount.ui.data.show.note.ListView

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.CheckBox
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.PayIncomeDBUtility
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.event.FilterShow
import wxm.KeepAccount.improve.toMoneyStr
import wxm.androidutil.improve.let1
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.report.ACReport
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail
import wxm.KeepAccount.ui.data.show.note.base.EOperation
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.time.CalendarUtility
import wxm.androidutil.time.getDayInWeekStr
import wxm.androidutil.ui.dialog.DlgOKOrNOBase
import wxm.androidutil.ui.moreAdapter.MoreAdapter
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.ui.view.ViewDataHolder
import wxm.androidutil.ui.view.ViewHolder
import wxm.androidutil.improve.doJudge
import wxm.uilib.IconButton.IconButton
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
    private val mMainPara: LinkedList<HashMap<String, ItemHolder>> = LinkedList()

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

            NoteDataHelper.getInfoByDay(tag).let {
                item.dayInWeek = ToolUtil.stringToCalendar(tag).getDayInWeekStr()
                item.dayInfo = RecordDetail(it.payCount.toString(), it.payAmount.toMoneyStr(),
                        it.incomeCount.toString(), it.incomeAmount.toMoneyStr())

                item.amount = String.format(Locale.CHINA,
                        if (0 < it.balance.toFloat()) "+ %.02f" else "%.02f", it.balance)

                item.show = EShowFold.getByFold(!checkUnfoldItem(tag)).showStatus
            }

            return item
        }
    }

    init {
        mBActionExpand = false
        mAHActs = object : ActionHelper() {
            private lateinit var mIBSort: IconButton

            override fun initActs(parentView: View) {
                mIBSort = parentView.findViewById(R.id.ib_sort)

                mIBSort.setActIcon(mBTimeDownOrder.doJudge(R.drawable.ic_sort_up_1, R.drawable.ic_sort_down_1))
                mIBSort.setActName(mBTimeDownOrder.doJudge(R.string.cn_sort_up_by_name, R.string.cn_sort_down_by_name))

                EventHelper.setOnClickOperator(parentView,
                        intArrayOf(R.id.ib_sort, R.id.ib_delete, R.id.ib_add, R.id.ib_refresh, R.id.ib_report),
                        this::onActionClick)
            }

            private fun onActionClick(v: View) {
                when (v.id) {
                    R.id.ib_sort -> {
                        mBTimeDownOrder = !mBTimeDownOrder

                        mIBSort.setActIcon(mBTimeDownOrder.doJudge(R.drawable.ic_sort_up_1, R.drawable.ic_sort_down_1))
                        mIBSort.setActName(mBTimeDownOrder.doJudge(R.string.cn_sort_up_by_time, R.string.cn_sort_down_by_time))

                        reloadUI()
                    }

                    R.id.ib_refresh -> {
                        mAction = EOperation.EDIT
                        reInitUI()
                    }

                    R.id.ib_delete -> {
                        mAction = if (mAction.isEdit) {
                            EOperation.DELETE
                        } else {
                            doDelete()
                            EOperation.EDIT
                        }

                        reloadUI()
                    }

                    R.id.ib_add -> {
                        Intent(activity, ACNoteCreate::class.java).let1 {
                            it.putExtra(GlobalDef.STR_RECORD_DATE,
                                    CalendarUtility.SDF_YEAR_MONTH_DAY_HOUR_MINUTE.format(System.currentTimeMillis()))

                            activity!!.startActivityForResult(it, 1)
                        }
                    }

                    R.id.ib_report -> {
                        DlgSelectReportDays().let1 { dlg ->
                            dlg.addDialogListener(object : DlgOKOrNOBase.DialogResultListener {
                                override fun onDialogPositiveResult(dialogFragment: DialogFragment) {
                                    Intent(activity, ACReport::class.java).let1 {
                                        it.putExtra(ACReport.PARA_TYPE, ACReport.PT_DAY)
                                        it.putStringArrayListExtra(ACReport.PARA_LOAD,
                                                arrayListOf(dlg.startDay!!, dlg.endDay!!))
                                        activity!!.startActivity(it)
                                    }
                                }

                                override fun onDialogNegativeResult(dialogFragment: DialogFragment) {
                                }
                            })

                            dlg.show(activity!!.supportFragmentManager, "select days")
                        }
                    }
                }
            }
        }
    }

    override fun isUseEventBus(): Boolean = true

    /**
     * filter event
     * @param event     param
     */
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFilterShowEvent(event: FilterShow) {
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
                reloadUI()
            }

            R.id.bt_cancel -> {
                mAction = EOperation.EDIT
                reloadUI()
            }

            R.id.bt_cancel_filter -> {
                mBFilter = false
                reloadUI()
            }
        }
    }

    override fun initUI(bundle: Bundle?) {
        super.initUI(bundle)
        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.bt_accpet, R.id.bt_cancel, R.id.bt_cancel_filter),
                this::onAcceptOrCancelClick)

        showLoadingProgress(true)
        ToolUtil.runInBackground(activity!!,
                {
                    mMainPara.clear()
                    NoteDataHelper.notesDays.forEach {
                        mMainPara.add(HashMap<String, ItemHolder>().apply { put(KEY_DATA, ItemHolder(it)) })
                    }
                },
                {
                    showLoadingProgress(false)
                    loadUI(bundle)
                })
    }


    override fun loadUI(bundle: Bundle?) {
        // adjust attach layout
        setAttachLayoutVisible((mAction.isDelete || mBFilter).doJudge(View.VISIBLE, View.GONE))
        setFilterLayoutVisible(mBFilter.doJudge(View.VISIBLE, View.GONE))
        setAcceptGiveUpLayoutVisible((mAction.isDelete && !mBFilter).doJudge(View.VISIBLE, View.GONE))

        // load show data
        mMainPara.filter { !mBFilter || mFilterPara.contains(it[KEY_DATA]!!.tag) }
                .sortedWith(Comparator { o1, o2 ->
                    mBTimeDownOrder.doJudge(
                            o2[KEY_DATA]!!.tag.compareTo(o1[KEY_DATA]!!.tag),
                            o1[KEY_DATA]!!.tag.compareTo(o2[KEY_DATA]!!.tag))
                }).let1 {
                    mLVShow.adapter = MainAdapter(context!!, it)
                }
    }


    /// BEGIN PRIVATE
    private fun doDelete() {
        LinkedList<INote>().let1 { ls ->
            (mLVShow.adapter as MainAdapter).waitDeleteDays.forEach {
                NoteDataHelper.getNotesByDay(it)?.let1 {
                    ls.addAll(it)
                }
            }

            PayIncomeDBUtility.instance.deleteNotes(ls)
        }
    }
    /// END PRIVATE

    /**
     * main adapter
     */
    private inner class MainAdapter internal constructor(context: Context, data: List<Map<String, ItemHolder>>)
        : MoreAdapter(context, data, R.layout.li_daily_show) {

        private val mCLAdapter = View.OnClickListener { v ->
            getTypedItem(mLVShow.getPositionForView(v)).let1 { hm ->
                activity!!.let1 {
                    it.startActivity(Intent(it, ACDailyDetail::class.java)
                            .putExtra(ACDailyDetail.KEY_HOT_DAY, hm.tag))
                }
            }
        }

        private fun getTypedItem(pos: Int): ItemHolder {
            @Suppress("UNCHECKED_CAST")
            return (getItem(pos) as Map<String, ItemHolder>)[KEY_DATA]!!
        }

        /**
         * get daily data need delete
         * @return      daily data
         */
        internal val waitDeleteDays: List<String>
            get() {
                return mAction.isDelete.doJudge(
                        LinkedList<String>().let { ret ->
                            forEachChildView({ vw, _ ->
                                vw.findViewById<CheckBox>(R.id.cb_del)!!.let1 {
                                    if (it.isChecked) {
                                        ret.add(it.tag as kotlin.String)
                                    }
                                }

                                true
                            })

                            ret
                        },
                        emptyList())
            }

        override fun loadView(pos: Int, vhHolder: ViewHolder) {
            val it = getTypedItem(pos)
            val cb = vhHolder.getView<CheckBox>(R.id.cb_del)
            cb.tag = it.tag
            cb.visibility = if (mAction.isEdit) {
                cb.isChecked = false
                View.GONE
            } else {
                View.VISIBLE
            }

            vhHolder.convertView.let1 {
                it.setBackgroundColor((0 == pos % 2).doJudge(mCRLVLineOne, mCRLVLineTwo))
                it.setOnClickListener(mCLAdapter)
            }

            initItemShow(vhHolder, it.data,
                    if (pos > 0) getTypedItem(pos - 1).data else null)
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
        private const val KEY_DATA = "data"
    }
}


