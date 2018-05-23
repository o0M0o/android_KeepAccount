package wxm.KeepAccount.ui.data.show.calendar

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.math.BigDecimal
import java.util.HashMap
import java.util.LinkedList
import java.util.Locale

import kotterknife.bindView
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.ui.data.show.calendar.base.SelectedDayEvent
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.util.UtilFun
import wxm.KeepAccount.R
import wxm.KeepAccount.define.INote
import wxm.KeepAccount.ui.utility.AdapterNoteDetail
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ContextUtil
import wxm.androidutil.app.AppBase

/**
 * for note pad content detail
 * Created by WangXM on 2016/12/12.
 */
class FrgCalendarContent : FrgSupportBaseAdv() {
    // for ui
    private val mTVMonthDay: TextView by bindView(R.id.tv_month_day)
    private val mTVYearMonth: TextView by bindView(R.id.tv_year_month)
    private val mTVBalance: TextView by bindView(R.id.header_day_balance)
    private val mLVBody: ListView by bindView(R.id.lv_body)

    private val mCLPay: Int = AppBase.getColor(R.color.darkred)
    private val mCLIncome: Int = AppBase.getColor(R.color.darkslategrey)

    // for data
    private var mSZHotDay: String? = null
    private var mLSDayContents: List<INote>? = null

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Suppress("UNUSED_PARAMETER", "unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBDataChange(event: DBDataChangeEvent) {
        mSZHotDay?.let {
            mLSDayContents = NoteDataHelper.getNotesByDay(it)
            loadUI(null)
        }
    }

    /**
     * handler for selected day change
     * @param event     for event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSelectedDayChange(event: SelectedDayEvent) {
        updateContent(event.day)
    }

    override fun getLayoutID(): Int {
        return R.layout.frg_calendar_content
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun initUI(bundle: Bundle?) {
        mSZHotDay = null
        mLSDayContents = null

        loadUI(bundle)
    }

    override fun loadUI(bundle: Bundle?) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            setVisibility(View.INVISIBLE)
            return
        }

        val arr = mSZHotDay!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (3 != arr.size) {
            setVisibility(View.INVISIBLE)
            return
        }

        // for header;
        setVisibility(View.VISIBLE)
        mTVMonthDay.text = arr[2]
        mTVYearMonth.text = String.format(Locale.CHINA, "%s年%s月", arr[0], arr[1])

        val ni = NoteDataHelper.getInfoByDay(mSZHotDay!!)
        val bb = ni?.balance ?: BigDecimal.ZERO
        mTVBalance.text = String.format(Locale.CHINA, "%s %.02f",
                if (bb < BigDecimal.ZERO) "-" else "+",
                Math.abs(bb.toFloat()))
        mTVBalance.setTextColor(if (bb < BigDecimal.ZERO) mCLPay else mCLIncome)

        // for list body
        val cPara = LinkedList<HashMap<String, INote>>()
        if (!UtilFun.ListIsNullOrEmpty(mLSDayContents)) {
            for (ci in mLSDayContents!!) {
                val hm = HashMap<String, INote>()
                hm[AdapterNoteDetail.K_NODE] = ci

                cPara.add(hm)
            }
        }

        val ap = AdapterNoteDetail(activity, cPara)
        mLVBody.adapter = ap
        ap.notifyDataSetChanged()
    }

    /// PRIVATE BEGIN
    /**
     * update hot day
     * @param day   show day(example : 2017-07-06)
     */
    private fun updateContent(day: String) {
        mSZHotDay = day
        mLSDayContents = NoteDataHelper.getNotesByDay(day)

        loadUI(null)
    }

    private fun setVisibility(vis: Int) {
        mTVMonthDay.visibility = vis
        mTVYearMonth.visibility = vis
        mLVBody.visibility = vis
    }
    /// PRIVATE END
}
