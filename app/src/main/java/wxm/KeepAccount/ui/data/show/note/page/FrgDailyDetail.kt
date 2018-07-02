package wxm.KeepAccount.ui.data.show.note.page

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import kotterknife.bindView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wxm.KeepAccount.R
import wxm.KeepAccount.db.DBDataChangeEvent
import wxm.KeepAccount.define.GlobalDef
import wxm.KeepAccount.improve.toMoneyStr
import wxm.androidutil.improve.let1
import wxm.KeepAccount.item.INote
import wxm.KeepAccount.ui.data.edit.NoteCreate.ACNoteCreate
import wxm.KeepAccount.ui.data.show.note.ACDailyDetail
import wxm.KeepAccount.ui.data.show.note.base.ValueShow
import wxm.KeepAccount.ui.utility.AdapterNoteDailyDetail
import wxm.KeepAccount.ui.utility.NoteDataHelper
import wxm.KeepAccount.utility.ToolUtil
import wxm.androidutil.ui.frg.FrgSupportBaseAdv
import wxm.androidutil.ui.view.EventHelper
import wxm.androidutil.util.UtilFun
import wxm.androidutil.time.getDayInWeekStr
import java.text.ParseException
import java.util.*


/**
 * for daily detail info
 * Created by WangXM on 2017/01/20.
 */
class FrgDailyDetail : FrgSupportBaseAdv() {
    // 展示时间信息的UI
    private val mTVMonthDay: TextView by bindView(R.id.tv_day)
    private val mTVYearMonth: TextView by bindView(R.id.tv_year_month)
    private val mTVDayInWeek: TextView by bindView(R.id.tv_day_in_week)

    // 展示数据的UI
    private val mLVBody: ListView by bindView(R.id.lv_note)

    // 跳转日期的UI
    private val mRLPrv: RelativeLayout by bindView(R.id.rl_prv)
    private val mRLNext: RelativeLayout by bindView(R.id.rl_next)

    // 展示日统计数据的UI
    private val mVSDataUI: ValueShow by bindView(R.id.vs_daily_info)
    private val mPBLoginProgress: ProgressBar by bindView(R.id.login_progress)

    // for data
    private var mSZHotDay: String? = null
    private var mLSDayContents: List<INote>? = null

    override fun initUI(bundle: Bundle?) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            mSZHotDay = arguments!!.getString(ACDailyDetail.KEY_HOT_DAY)
        }

        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            mLSDayContents = NoteDataHelper.getNotesByDay(mSZHotDay!!)
        }

        EventHelper.setOnClickOperator(view!!,
                intArrayOf(R.id.rl_prv, R.id.rl_next, R.id.ib_add),
                ::onClick)

        loadUI(bundle)
    }

    override fun getLayoutID(): Int {
        return R.layout.frg_daily_detail
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    override fun loadUI(bundle: Bundle?) {
        if (UtilFun.StringIsNullOrEmpty(mSZHotDay)) {
            setVisibility(View.INVISIBLE)
            return
        }

        setVisibility(View.VISIBLE)
        loadDayHeader()
        loadDayInfo()
        loadDayNotes()
    }

    /**
     * handler for DB data change
     * @param event     for event
     */
    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDBDataChangeEvent(event: DBDataChangeEvent) {
        initUI(null)
    }

    fun onClick(view: View) {
        val orgDay = mSZHotDay
        when (view.id) {
            R.id.rl_prv -> {
                NoteDataHelper.getPrvDay(mSZHotDay!!).let {
                    if (it.isNullOrEmpty()) {
                        mRLPrv.visibility = View.GONE
                    } else {
                        mSZHotDay = it

                        if (View.VISIBLE != mRLNext.visibility)
                            mRLNext.visibility = View.VISIBLE
                    }
                }
            }

            R.id.rl_next -> {
                NoteDataHelper.getNextDay(mSZHotDay!!).let {
                    if (it.isNullOrEmpty()) {
                        mRLNext.visibility = View.GONE
                    } else {
                        mSZHotDay = it

                        if (View.VISIBLE != mRLPrv.visibility)
                            mRLPrv.visibility = View.VISIBLE
                    }
                }
            }

            R.id.ib_add -> {
                val intent = Intent(activity, ACNoteCreate::class.java)
                val cal = Calendar.getInstance()
                intent.putExtra(GlobalDef.STR_RECORD_DATE,
                        String.format(Locale.CHINA, "%s %02d:%02d", mSZHotDay, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))

                startActivityForResult(intent, 1)
            }
        }

        if (!UtilFun.StringIsNullOrEmpty(mSZHotDay) && orgDay != mSZHotDay) {
            mLSDayContents = NoteDataHelper.getNotesByDay(mSZHotDay!!)
            loadUI(null)
        }
    }

    /// PRIVATE BEGIN
    /**
     * Shows the progress UI and hides the login form.
     */
    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources
                .getInteger(android.R.integer.config_shortAnimTime)

        mPBLoginProgress.visibility = if (show) View.VISIBLE else View.GONE
        mPBLoginProgress.animate().setDuration(shortAnimTime.toLong())
                .alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mPBLoginProgress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    /**
     * load day info header
     */
    private fun loadDayHeader() {
        mSZHotDay!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray().let1 {
                    mTVMonthDay.text = it[2]
                    mTVYearMonth.text = String.format(Locale.CHINA, "%s年%s月", it[0], it[1])
                }

        try {
            ToolUtil.stringToCalendar(mSZHotDay!!).let1 {
                mTVDayInWeek.text = it.getDayInWeekStr()
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    /**
     * load day info
     */
    private fun loadDayInfo() {
        HashMap<String, Any>().let {
            NoteDataHelper.getInfoByDay(mSZHotDay!!).let1 { t ->
                        it[ValueShow.ATTR_PAY_COUNT] = t.payCount.toString()
                        it[ValueShow.ATTR_PAY_AMOUNT] = t.payAmount.toMoneyStr()
                        it[ValueShow.ATTR_INCOME_COUNT] = t.incomeCount.toString()
                        it[ValueShow.ATTR_INCOME_AMOUNT] = t.incomeAmount.toMoneyStr()
                    }

            mVSDataUI.adjustAttribute(it)
        }
    }

    /**
     * load day data
     */
    private fun loadDayNotes() {
        val para = LinkedList<HashMap<String, INote>>()
        mLSDayContents!!.let {
            it.sortedBy { it.ts }.forEach {
                para.add(HashMap<String, INote>().apply { put(AdapterNoteDailyDetail.K_NODE, it) })
            }
        }

        mLVBody.adapter = AdapterNoteDailyDetail(activity!!, para)
    }

    /**
     * set UI visibility
     * @param vis       visibility param
     */
    private fun setVisibility(vis: Int) {
        mTVMonthDay.visibility = vis
        mTVYearMonth.visibility = vis
        mLVBody.visibility = vis
    }
    /// PRIVATE END
}
